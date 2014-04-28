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
package org.jamocha.filter;

import java.util.Arrays;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.FunctionWithArgumentsComposite;
import org.jamocha.filter.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.filter.fwa.GenericWithArgumentsComposite;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.filter.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class UniformFunctionTranslator {
	public static PredicateWithArguments translate(
			final PredicateWithArguments predicateWithArguments) {
		final PredicateWithArguments transformed =
				(PredicateWithArguments) predicateWithArguments.accept(new UpperLevelFWATranslator(
						predicateWithArguments)).result;
		return transformed;
	}

	static interface SelectiveFunctionWithArgumentsVisitor extends FunctionWithArgumentsVisitor {
		@Override
		public default void visit(
				final org.jamocha.filter.fwa.FunctionWithArgumentsComposite functionWithArgumentsComposite) {
		}

		@Override
		public default void visit(
				final org.jamocha.filter.fwa.PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
		}

		@Override
		public default void visit(final org.jamocha.filter.fwa.ConstantLeaf constantLeaf) {
		}

		@Override
		public default void visit(final org.jamocha.filter.fwa.PathLeaf.ParameterLeaf parameterLeaf) {
		}

		@Override
		public default void visit(final org.jamocha.filter.fwa.PathLeaf pathLeaf) {
		}
	}

	static interface SelectiveFunctionVisitor extends FunctionVisitor {
		// functions
		@Override
		default void visit(final org.jamocha.filter.impls.functions.DividedBy<?> function) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.functions.Minus<?> function) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.functions.Plus<?> function) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.functions.Times<?> function) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.functions.TimesInverse<?> function) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.functions.TypeConverter<?> function) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.functions.UnaryMinus<?> function) {
		}

		// predicates
		@Override
		default void visit(final org.jamocha.filter.impls.predicates.And predicate) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.predicates.Equals predicate) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.predicates.Greater predicate) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.predicates.GreaterOrEqual predicate) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.predicates.Less predicate) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.predicates.LessOrEqual predicate) {
		}

		@Override
		default void visit(final org.jamocha.filter.impls.predicates.Not predicate) {
		}
	}

	static boolean translateArgsClone(final FunctionWithArguments[] argsClone) {
		boolean changed = false;
		for (int i = 0; i < argsClone.length; i++) {
			final FunctionWithArguments input = argsClone[i];
			final FunctionWithArguments result =
					input.accept(new UpperLevelFWATranslator(input)).result;
			if (input != result)
				changed = true;
			argsClone[i] = result;
		}
		return changed;
	}

	@FunctionalInterface
	private static interface GwacCtor<R, F extends Function<? extends R>, G extends GenericWithArgumentsComposite<R, F>> {
		G create(final F f, final FunctionWithArguments[] args);
	}

	private static class UpperLevelFWATranslator implements SelectiveFunctionWithArgumentsVisitor {
		FunctionWithArguments result;

		public UpperLevelFWATranslator(final FunctionWithArguments defaultResult) {
			this.result = defaultResult;
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			handle(functionWithArgumentsComposite, FunctionWithArgumentsComposite::new);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			handle(predicateWithArgumentsComposite, PredicateWithArgumentsComposite::new);
		}

		public <R, F extends Function<? extends R>, G extends GenericWithArgumentsComposite<R, F>> void handle(
				final G gwac, final GwacCtor<R, F, G> ctor) {
			final FunctionWithArguments[] argsClone = gwac.getArgs().clone();
			final F f = gwac.getFunction();
			final G newGwac;
			if (translateArgsClone(argsClone)) {
				// some arg was replaced, generate new gwac to hold the new arg list
				newGwac = ctor.create(f, argsClone);
			} else {
				newGwac = gwac;
			}
			final FunctionWithArguments translated =
					f.accept(new UpperLevelFunctionTranslator(newGwac)).result;
			if (this.result != translated) {
				this.result = new UpperLevelFWATranslator(translated).result;
			} else {
				this.result = translated;
			}
		}
	}

	private static class UpperLevelFunctionTranslator implements SelectiveFunctionVisitor {
		FunctionWithArguments result;
		final GenericWithArgumentsComposite<?, ?> upperGwac;

		public UpperLevelFunctionTranslator(final GenericWithArgumentsComposite<?, ?> upperGwac) {
			this.upperGwac = upperGwac;
			this.result = upperGwac;
		}

		// -(a,b) -> +(a,(-b))
		@Override
		public void visit(final org.jamocha.filter.impls.functions.Minus<?> function) {
			result =
					new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
							org.jamocha.filter.impls.functions.Plus.inClips,
							function.getParamTypes()), new FunctionWithArguments[] {
							upperGwac.getArgs()[0],
							new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
									org.jamocha.filter.impls.functions.UnaryMinus.inClips,
									function.getParamTypes()[1]), upperGwac.getArgs()[1]) });
		}

		// -(-(a)) -> a
		@Override
		public void visit(final org.jamocha.filter.impls.functions.UnaryMinus<?> function) {
			result =
					upperGwac.getArgs()[0].accept(new LowerLevelFWATranslator(
							UnaryMinusTranslator::new, upperGwac)).result;
		}

		// /(a,b) -> *(a,1/b)
		// FIXME does not work for longs: 7 / 5 = 1 != 0 = 7 * (1/5)
		@Override
		public void visit(final org.jamocha.filter.impls.functions.DividedBy<?> function) {
			result =
					new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
							org.jamocha.filter.impls.functions.Times.inClips,
							function.getParamTypes()), new FunctionWithArguments[] {
							upperGwac.getArgs()[0],
							new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
									org.jamocha.filter.impls.functions.TimesInverse.inClips,
									function.getParamTypes()[1]), upperGwac.getArgs()[1]) });
		}

		// 1/(1/a) -> a
		// FIXME does not work for longs: 1 / (1 / 5) = DIV_BY_0_ERROR != 5
		@Override
		public void visit(final org.jamocha.filter.impls.functions.TimesInverse<?> function) {
			result =
					upperGwac.getArgs()[0].accept(new LowerLevelFWATranslator(
							TimesInverseTranslator::new, upperGwac)).result;

		}

		// +(+(a,b),c) -> +(a,b,c)
		// +(a,+(b,c)) -> +(a,b,c)
		@Override
		public void visit(final org.jamocha.filter.impls.functions.Plus<?> function) {
			for (int i = 0; i < upperGwac.getArgs().length; i++) {
				final FunctionWithArguments arg = upperGwac.getArgs()[i];
				final int j = i;
				result = arg.accept(new LowerLevelFWATranslator((u, l) -> {
					return new PlusTranslator(u, l, j);
				}, upperGwac)).result;
				if (upperGwac != result)
					return;
			}
		}

		// *(+(a,b),c) -> +(*(a,c),*(b,c))
		// *(-(a),b) -> -(*(a,b))
		@Override
		public void visit(final org.jamocha.filter.impls.functions.Times<?> function) {

		}
	}

	@FunctionalInterface
	private static interface LowerLevelFunctionTranslatorCtor {
		public LowerLevelFunctionTranslator create(
				final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC);
	}

	private static class LowerLevelFWATranslator implements SelectiveFunctionWithArgumentsVisitor {
		FunctionWithArguments result;
		final LowerLevelFunctionTranslatorCtor ctor;
		final GenericWithArgumentsComposite<?, ?> upperGwac;

		public LowerLevelFWATranslator(final LowerLevelFunctionTranslatorCtor ctor,
				final GenericWithArgumentsComposite<?, ?> upperGwac) {
			this.ctor = ctor;
			this.upperGwac = upperGwac;
			this.result = upperGwac;
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			handle(functionWithArgumentsComposite);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			handle(predicateWithArgumentsComposite);
		}

		public void handle(final GenericWithArgumentsComposite<?, ?> gwac) {
			this.result = gwac.getFunction().accept(ctor.create(upperGwac, gwac)).result;
		}
	}

	private abstract static class LowerLevelFunctionTranslator implements SelectiveFunctionVisitor {
		final GenericWithArgumentsComposite<?, ?> upperGwac;
		final GenericWithArgumentsComposite<?, ?> lowerGwac;
		FunctionWithArguments result;

		public LowerLevelFunctionTranslator(final GenericWithArgumentsComposite<?, ?> upperGwac,
				final GenericWithArgumentsComposite<?, ?> lowerGwac) {
			this.upperGwac = upperGwac;
			this.lowerGwac = lowerGwac;
			this.result = upperGwac;
		}

	}

	private static class UnaryMinusTranslator extends LowerLevelFunctionTranslator {
		public UnaryMinusTranslator(GenericWithArgumentsComposite<?, ?> upperGWAC,
				GenericWithArgumentsComposite<?, ?> lowerGWAC) {
			super(upperGWAC, lowerGWAC);
		}

		@Override
		public void visit(final org.jamocha.filter.impls.functions.UnaryMinus<?> function) {
			result = lowerGwac.getArgs()[0];
		}
	}

	private static class TimesInverseTranslator extends LowerLevelFunctionTranslator {
		public TimesInverseTranslator(GenericWithArgumentsComposite<?, ?> upperGWAC,
				GenericWithArgumentsComposite<?, ?> lowerGWAC) {
			super(upperGWAC, lowerGWAC);
		}

		@Override
		public void visit(final org.jamocha.filter.impls.functions.TimesInverse<?> function) {
			result = lowerGwac.getArgs()[0];
		}
	}

	private static class PlusTranslator extends LowerLevelFunctionTranslator {
		final int position;

		public PlusTranslator(GenericWithArgumentsComposite<?, ?> upperGWAC,
				GenericWithArgumentsComposite<?, ?> lowerGWAC, final int position) {
			super(upperGWAC, lowerGWAC);
			this.position = position;
		}

		@Override
		public void visit(final org.jamocha.filter.impls.functions.Plus<?> function) {
			// `newArgs` are `upperArgs` with the `lowerArgs` embedded at `position` replacing one
			// arg with two or more
			final FunctionWithArguments[] upperArgs = upperGwac.getArgs();
			final FunctionWithArguments[] lowerArgs = lowerGwac.getArgs();
			final int length = upperArgs.length + lowerArgs.length - 1;
			final FunctionWithArguments[] newArgs = Arrays.copyOf(upperArgs, length);
			System.arraycopy(lowerArgs, 0, newArgs, position, lowerArgs.length);
			if (position + lowerArgs.length < newArgs.length)
				System.arraycopy(upperArgs, upperArgs.length - (lowerArgs.length - 1), newArgs,
						position + lowerArgs.length, lowerArgs.length - 1);
			final SlotType paramTypes[] = new SlotType[length];
			Arrays.fill(paramTypes, function.getParamTypes()[0]);
			result =
					new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
							org.jamocha.filter.impls.functions.Plus.inClips, paramTypes), newArgs);
		}
	}
}
