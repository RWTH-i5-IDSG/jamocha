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
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Function;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Assert implements FunctionWithArguments {

	@Value
	public static class TemplateContainer implements FunctionWithArguments {
		final Template template;
		final FunctionWithArguments[] args;
		@Getter(lazy = true, onMethod = @__(@Override))
		private final SlotType[] paramTypes = calculateParamTypes();
		@Getter(lazy = true, value = AccessLevel.PRIVATE)
		private final int hashPIR = initHashPIR(), hashPII = initHashPII();

		private SlotType[] calculateParamTypes() {
			return Assert.calculateParamTypes(args);
		}

		private int initHashPII() {
			final int[] hashPII = new int[args.length + 1];
			hashPII[0] = template.hashCode();
			for (int i = 0; i < args.length; i++) {
				final FunctionWithArguments arg = args[i];
				hashPII[i + 1] = arg.hashPositionIsIrrelevant();
			}
			return FunctionWithArguments.hash(hashPII, FunctionWithArguments.positionIsIrrelevant);
		}

		private int initHashPIR() {
			final int[] hashPIR = new int[args.length + 1];
			hashPIR[0] = template.hashCode();
			for (int i = 0; i < args.length; i++) {
				final FunctionWithArguments arg = args[i];
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
			// not really true
			return SlotType.FACTADDRESS;
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
		public Function<?> lazyEvaluate(final Function<?>... params) {
			return new GenericWithArgumentsComposite.LazyObject(
					GenericWithArgumentsComposite.staticLazyEvaluate(
							fwas -> new GenericWithArgumentsComposite.LazyObject(template
									.newFact(Arrays.stream(fwas).map(f -> f.evaluate()).toArray())),
							"assert::templateContainer", args, params));
		}

		@Override
		public Object evaluate(final Object... params) {
			return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
		}
	}

	@Getter
	final Network network;
	@Getter
	final TemplateContainer[] args;
	@Getter(lazy = true, onMethod = @__(@Override))
	private final SlotType[] paramTypes = calculateParamTypes();
	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final int hashPIR = initHashPIR(), hashPII = initHashPII();

	private SlotType[] calculateParamTypes() {
		return calculateParamTypes(args);
	}

	static private SlotType[] calculateParamTypes(final FunctionWithArguments[] args) {
		final ArrayList<SlotType> types =
				Arrays.stream(args).map(FunctionWithArguments::getParamTypes).map(Arrays::asList)
						.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
		return toArray(types, SlotType[]::new);
	}

	private int initHashPII() {
		final int[] hashPII = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			final FunctionWithArguments arg = args[i];
			hashPII[i] = arg.hashPositionIsIrrelevant();
		}
		return FunctionWithArguments.hash(hashPII, FunctionWithArguments.positionIsIrrelevant);
	}

	private int initHashPIR() {
		final int[] hashPIR = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			final FunctionWithArguments arg = args[i];
			hashPIR[i] = arg.hashPositionIsRelevant();
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
		return SlotType.FACTADDRESSES;
	}

	@Override
	public Function<Object> lazyEvaluate(final Function<?>... params) {
		return new GenericWithArgumentsComposite.LazyObject(GenericWithArgumentsComposite
				.staticLazyEvaluate(
						fs -> network.assertFacts(Arrays.stream(fs).map(f -> f.evaluate())
								.toArray(Fact[]::new)), "assert", args, params).evaluate());
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
