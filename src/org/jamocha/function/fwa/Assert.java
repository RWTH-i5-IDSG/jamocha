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
package org.jamocha.function.fwa;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.Function;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Assert<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {

	@Value
	@ToString(exclude = { "hashPIR", "hashPII" })
	public static class TemplateContainer<L extends ExchangeableLeaf<L>> implements FunctionWithArguments<L> {
		final Template template;
		final FunctionWithArguments<L>[] args;
		@Getter(lazy = true, onMethod = @__(@Override))
		private final SlotType[] paramTypes = calculateParamTypes();
		@Getter(lazy = true, value = AccessLevel.PRIVATE)
		private final int hashPIR = initHashPIR(), hashPII = initHashPII();

		@SafeVarargs
		public TemplateContainer(final Template template, final FunctionWithArguments<L>... args) {
			this.template = template;
			this.args = args;
		}

		private SlotType[] calculateParamTypes() {
			return Assert.calculateParamTypes(args);
		}

		private int initHashPII() {
			final int[] hashPII = new int[args.length + 1];
			hashPII[0] = template.hashCode();
			for (int i = 0; i < args.length; i++) {
				final FunctionWithArguments<L> arg = args[i];
				hashPII[i + 1] = arg.hashPositionIsIrrelevant();
			}
			return FunctionWithArguments.hash(hashPII, FunctionWithArguments.positionIsIrrelevant);
		}

		private int initHashPIR() {
			final int[] hashPIR = new int[args.length + 1];
			hashPIR[0] = template.hashCode();
			for (int i = 0; i < args.length; i++) {
				final FunctionWithArguments<L> arg = args[i];
				hashPIR[i + 1] = arg.hashPositionIsRelevant();
			}
			return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.positionIsRelevant);
		}

		@Override
		public <T extends FunctionWithArgumentsVisitor<L>> T accept(final T visitor) {
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
			return GenericWithArgumentsComposite.staticLazyEvaluate(
					(final Function<?>[] functions) -> template.newFact(Arrays.stream(functions)
							.<Object> map(f -> f.evaluate()).toArray()), "assert::templateContainer", args, params);
		}

		@Override
		public Object evaluate(final Object... params) {
			return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
		}

		public Fact toFact() {
			return (Fact) evaluate();
		}
	}

	@Getter
	final SideEffectFunctionToNetwork network;
	@Getter
	final TemplateContainer<L>[] args;
	@Getter(lazy = true, onMethod = @__(@Override))
	private final SlotType[] paramTypes = calculateParamTypes();
	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final int hashPIR = initHashPIR(), hashPII = initHashPII();

	private SlotType[] calculateParamTypes() {
		return calculateParamTypes(args);
	}

	static private <L extends ExchangeableLeaf<L>> SlotType[] calculateParamTypes(final FunctionWithArguments<L>[] args) {
		final ArrayList<SlotType> types =
				Arrays.stream(args).map(FunctionWithArguments::getParamTypes).map(Arrays::asList)
						.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
		return toArray(types, SlotType[]::new);
	}

	private int initHashPII() {
		final int[] hashPII = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			final FunctionWithArguments<L> arg = args[i];
			hashPII[i] = arg.hashPositionIsIrrelevant();
		}
		return FunctionWithArguments.hash(hashPII, FunctionWithArguments.positionIsIrrelevant);
	}

	private int initHashPIR() {
		final int[] hashPIR = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			final FunctionWithArguments<L> arg = args[i];
			hashPIR[i] = arg.hashPositionIsRelevant();
		}
		return FunctionWithArguments.hash(hashPIR, FunctionWithArguments.positionIsRelevant);
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor<L>> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType getReturnType() {
		return SlotType.FACTADDRESS;
	}

	@Override
	public Function<FactIdentifier> lazyEvaluate(final Function<?>... params) {
		return GenericWithArgumentsComposite.staticLazyEvaluate(
				fs -> {
					final FactIdentifier[] assertFacts =
							network.assertFacts(toArray(Arrays.stream(fs).map(f -> (Fact) f.evaluate()), Fact[]::new));
					return assertFacts[assertFacts.length - 1];
				}, "assert", args, params);
	}

	@Override
	public FactIdentifier evaluate(final Object... params) {
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
