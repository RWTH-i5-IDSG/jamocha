/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.filter.fwa;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Function;
import org.jamocha.languages.common.errors.NoSlotForThatNameError;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Modify implements FunctionWithArguments {
	@Value
	public static class SlotAndValue implements FunctionWithArguments {
		final String slotName;
		final FunctionWithArguments value;
		@Getter(lazy = true, value = AccessLevel.PRIVATE)
		private final int hashPIR = initHashPIR(), hashPII = initHashPII();

		private int initHashPII() {
			final int[] hashPII = new int[2];
			hashPII[0] = slotName.hashCode();
			hashPII[1] = value.hashPositionIsIrrelevant();
			return FunctionWithArguments.hash(hashPII, FunctionWithArguments.positionIsIrrelevant);
		}

		private int initHashPIR() {
			final int[] hashPIR = new int[2];
			hashPIR[0] = slotName.hashCode();
			hashPIR[1] = value.hashPositionIsRelevant();
			return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.positionIsRelevant);
		}

		@Override
		public int hashPositionIsIrrelevant() {
			return getHashPII();
		}

		@Override
		public int hashPositionIsRelevant() {
			return getHashPIR();
		}

		@Override
		public <V extends FunctionWithArgumentsVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public SlotType[] getParamTypes() {
			return this.value.getParamTypes();
		}

		@Override
		public SlotType getReturnType() {
			return this.value.getReturnType();
		}

		@Override
		public Function<?> lazyEvaluate(final Function<?>... params) {
			return this.value.lazyEvaluate(params);
		}

		@Override
		public Object evaluate(final Object... params) {
			return this.value.evaluate(params);
		}
	}

	@Getter
	@NonNull
	final Network network;
	@Getter
	@NonNull
	final FunctionWithArguments targetFact;
	@Getter
	final SlotAndValue[] args;
	@Getter(lazy = true, onMethod = @__(@Override))
	private final SlotType[] paramTypes = calculateParamTypes();
	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final int hashPIR = initHashPIR(), hashPII = initHashPII();

	private SlotType[] calculateParamTypes() {
		return calculateParamTypes(this.args);
	}

	static private SlotType[] calculateParamTypes(final SlotAndValue[] args) {
		final ArrayList<SlotType> types =
				Arrays.stream(args).map(FunctionWithArguments::getReturnType).map(Arrays::asList)
						.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
		return toArray(types, SlotType[]::new);
	}

	private int initHashPII() {
		final int[] hashPII = new int[args.length + 1];
		hashPII[0] = targetFact.hashPositionIsIrrelevant();
		for (int i = 0; i < args.length; i++) {
			final SlotAndValue arg = args[i];
			hashPII[i + 1] = arg.hashPositionIsIrrelevant();
		}
		return FunctionWithArguments.hash(hashPII, FunctionWithArguments.positionIsIrrelevant);
	}

	private int initHashPIR() {
		final int[] hashPIR = new int[args.length + 1];
		hashPIR[0] = targetFact.hashPositionIsRelevant();
		for (int i = 0; i < args.length; i++) {
			final SlotAndValue arg = args[i];
			hashPIR[i + 1] = arg.hashPositionIsRelevant();
		}
		return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.positionIsRelevant);
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType getReturnType() {
		return SlotType.FACTADDRESS;
	}

	@Override
	public Function<Object> lazyEvaluate(final Function<?>... params) {
		final FunctionWithArguments[] array = new FunctionWithArguments[args.length + 1];
		array[0] = this.targetFact;
		System.arraycopy(args, 0, array, 1, args.length);
		return new GenericWithArgumentsComposite.LazyObject(GenericWithArgumentsComposite
				.staticLazyEvaluate(
						fs -> {
							final FactIdentifier factIdentifier = Retract.toFactIdentifier(fs[0]);
							final Fact fact =
									network.getMemoryFact(factIdentifier.getId()).toMutableFact();
							network.retractFacts(factIdentifier);
							final Template template = fact.getTemplate();
							for (int i = 0; i < args.length; ++i) {
								final String slotName = args[i].getSlotName();
								final SlotAddress slotAddress = template.getSlotAddress(slotName);
								if (null == slotAddress) {
									throw new NoSlotForThatNameError(slotName);
								}
								template.setValue(fact, slotAddress, fs[i + 1].evaluate());
							}
							return network.assertFacts(fact)[0];
						}, "assert", array, params).evaluate());
	}

	@Override
	public Object evaluate(final Object... params) {
		return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return getHashPII();
	}

	@Override
	public int hashPositionIsRelevant() {
		return getHashPIR();
	}
}
