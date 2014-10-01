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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.impls.DefaultFunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class UniformFunctionTranslator {
	public static PredicateWithArguments translate(
			final PredicateWithArguments predicateWithArguments) {
		final PredicateWithArguments copy =
				(PredicateWithArguments) predicateWithArguments.accept(new FWADeepCopy())
						.getResult();
		final PredicateWithArguments transformed =
				(PredicateWithArguments) copy.accept(new UpperLevelFWATranslator(copy)).result;
		return transformed;
	}

	static interface SelectiveFunctionWithArgumentsVisitor extends
			DefaultFunctionWithArgumentsVisitor {
		@Override
		default void defaultAction(FunctionWithArguments function) {
		}
	}

	static interface SelectiveFunctionVisitor extends DefaultFunctionVisitor {
		@Override
		default <R> void defaultAction(final Function<R> function) {
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
			this.<Object, Function<?>, FunctionWithArgumentsComposite> handle(
					functionWithArgumentsComposite, FunctionWithArgumentsComposite::new);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.<Boolean, Predicate, PredicateWithArgumentsComposite> handle(
					predicateWithArgumentsComposite, PredicateWithArgumentsComposite::new);
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
				this.result = translated.accept(new UpperLevelFWATranslator(translated)).result;
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
		public void visit(final org.jamocha.function.impls.functions.Minus<?> function) {
			this.result =
					new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
							org.jamocha.function.impls.functions.Plus.inClips,
							function.getParamTypes()), new FunctionWithArguments[] {
							this.upperGwac.getArgs()[0],
							new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
									org.jamocha.function.impls.functions.UnaryMinus.inClips,
									function.getParamTypes()[1]), this.upperGwac.getArgs()[1]) });
		}

		// -(-(a)) -> a
		// -(+(a,b,c,...)) -> +(-a,-b,-c,...)
		@Override
		public void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
			this.result =
					this.upperGwac.getArgs()[0].accept(new LowerLevelFWATranslator(
							UnaryMinusTranslator::new, this.upperGwac)).result;
		}

		// /(a,b) -> *(a,1/b)
		@Override
		public void visit(final org.jamocha.function.impls.functions.DividedBy<?> function) {
			if (function.getReturnType() == SlotType.LONG)
				return;
			this.result =
					new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
							org.jamocha.function.impls.functions.Times.inClips,
							function.getParamTypes()), new FunctionWithArguments[] {
							this.upperGwac.getArgs()[0],
							new FunctionWithArgumentsComposite(FunctionDictionary.lookup(
									org.jamocha.function.impls.functions.TimesInverse.inClips,
									function.getParamTypes()[1]), this.upperGwac.getArgs()[1]) });
		}

		// 1/(1/a) -> a
		@Override
		public void visit(final org.jamocha.function.impls.functions.TimesInverse<?> function) {
			if (function.getReturnType() == SlotType.LONG)
				return;
			this.result =
					this.upperGwac.getArgs()[0].accept(new LowerLevelFWATranslator(
							TimesInverseTranslator::new, this.upperGwac)).result;

		}

		private void argumentChanginLoopWithIndex(
				final LowerLevelFunctionWithPositionTranslatorCtor ctor) {
			for (int i = 0; i < this.upperGwac.getArgs().length; i++) {
				final FunctionWithArguments arg = this.upperGwac.getArgs()[i];
				final int j = i;
				this.result = arg.accept(new LowerLevelFWATranslator((u, l) -> {
					return ctor.create(u, l, j);
				}, this.upperGwac)).result;
				if (this.upperGwac != this.result)
					return;
			}
		}

		// +(+(a,b),c) -> +(a,b,c)
		// +(a,+(b,c)) -> +(a,b,c)
		@Override
		public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
			argumentChanginLoopWithIndex(PlusTranslator::new);
		}

		// *(*(a,b),c) -> *(a,b,c)
		// *(+(a,b),c) -> +(*(a,c),*(b,c))
		// *(-(a),b) -> -(*(a,b))
		@Override
		public void visit(final org.jamocha.function.impls.functions.Times<?> function) {
			argumentChanginLoopWithIndex(TimesTranslator::new);
		}

		private static FunctionWithArguments[] swapTwoArguments(final FunctionWithArguments[] args) {
			return new FunctionWithArguments[] { args[1], args[0] };
		}

		// >(a,b) -> <(b,a)
		@Override
		public void visit(final org.jamocha.function.impls.predicates.Greater predicate) {
			this.result =
					new PredicateWithArgumentsComposite(FunctionDictionary.lookupPredicate(
							org.jamocha.function.impls.predicates.Less.inClips,
							predicate.getParamTypes()), swapTwoArguments(this.upperGwac.getArgs()));
		}

		// <=(a,b) -> !(<(b,a))
		@Override
		public void visit(final org.jamocha.function.impls.predicates.LessOrEqual predicate) {
			this.result =
					new PredicateWithArgumentsComposite(FunctionDictionary.lookupPredicate(
							org.jamocha.function.impls.predicates.Not.inClips, SlotType.BOOLEAN),
							new PredicateWithArgumentsComposite(FunctionDictionary.lookupPredicate(
									org.jamocha.function.impls.predicates.Less.inClips,
									predicate.getParamTypes()), swapTwoArguments(this.upperGwac
									.getArgs())));
		}

		// >=(a,b) -> !(<(a,b))
		@Override
		public void visit(final org.jamocha.function.impls.predicates.GreaterOrEqual predicate) {
			this.result =
					new PredicateWithArgumentsComposite(FunctionDictionary.lookupPredicate(
							org.jamocha.function.impls.predicates.Not.inClips, SlotType.BOOLEAN),
							new PredicateWithArgumentsComposite(FunctionDictionary.lookupPredicate(
									org.jamocha.function.impls.predicates.Less.inClips,
									predicate.getParamTypes()), this.upperGwac.getArgs()));
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
			this.result = gwac.getFunction().accept(this.ctor.create(this.upperGwac, gwac)).result;
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
		public UnaryMinusTranslator(final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC) {
			super(upperGWAC, lowerGWAC);
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
			this.result = this.lowerGwac.getArgs()[0];
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
			result =
					new FunctionWithArgumentsComposite(function, toArray(
							Arrays.stream(lowerGwac.getArgs()).map(
									(final FunctionWithArguments fwa) -> {
										return new FunctionWithArgumentsComposite(upperGwac
												.getFunction(), fwa);
									}), FunctionWithArguments[]::new));
		}
	}

	private static class TimesInverseTranslator extends LowerLevelFunctionTranslator {
		public TimesInverseTranslator(final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC) {
			super(upperGWAC, lowerGWAC);
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.TimesInverse<?> function) {
			if (function.getReturnType() == SlotType.LONG) {
				return;
			}
			this.result = this.lowerGwac.getArgs()[0];
		}
	}

	@FunctionalInterface
	private static interface LowerLevelFunctionWithPositionTranslatorCtor {
		public LowerLevelFunctionTranslator create(
				final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC, final int position);
	}

	private static class LowerLevelFunctionWithPositionTranslator extends
			LowerLevelFunctionTranslator {
		final int position;

		public LowerLevelFunctionWithPositionTranslator(
				final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC, final int position) {
			super(upperGWAC, lowerGWAC);
			this.position = position;
		}

		protected FunctionWithArguments combineSameFunction(
				final org.jamocha.function.Function<?> function) {
			// `newArgs` are `upperArgs` with the `lowerArgs` embedded at `position` replacing one
			// arg with two or more
			final FunctionWithArguments[] upperArgs = this.upperGwac.getArgs();
			final FunctionWithArguments[] lowerArgs = this.lowerGwac.getArgs();
			final int length = upperArgs.length + lowerArgs.length - 1;
			final FunctionWithArguments[] newArgs = Arrays.copyOf(upperArgs, length);
			System.arraycopy(lowerArgs, 0, newArgs, position, lowerArgs.length);
			System.arraycopy(upperArgs, 0, newArgs, 0, position);
			System.arraycopy(upperArgs, position + 1, newArgs, position + lowerArgs.length,
					upperArgs.length - position - 1);
			final SlotType paramTypes[] = new SlotType[length];
			Arrays.fill(paramTypes, function.getParamTypes()[0]);
			return new FunctionWithArgumentsComposite(FunctionDictionary.lookup(function.inClips(),
					paramTypes), newArgs);
		}

	}

	private static class PlusTranslator extends LowerLevelFunctionWithPositionTranslator {
		public PlusTranslator(final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC, final int position) {
			super(upperGWAC, lowerGWAC, position);
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
			this.result = combineSameFunction(function);
		}
	}

	private static class TimesTranslator extends LowerLevelFunctionWithPositionTranslator {
		public TimesTranslator(final GenericWithArgumentsComposite<?, ?> upperGWAC,
				final GenericWithArgumentsComposite<?, ?> lowerGWAC, final int position) {
			super(upperGWAC, lowerGWAC, position);
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.Plus<?> function) {
			// *(+(a,b),c,d,...) -> +(*(a,c,d,...),*(b,c,d,...))
			final FunctionWithArguments[] oldTimesArgs = this.upperGwac.getArgs();
			final FunctionWithArguments[] oldPlusArgs = this.lowerGwac.getArgs();
			final FunctionWithArguments[] newPlusArgs =
					new FunctionWithArguments[oldPlusArgs.length];
			for (int i = 0; i < oldPlusArgs.length; i++) {
				// for each element in the plus, create a times with the element and cAndMore
				final FunctionWithArguments[] newTimesArgs =
						Arrays.copyOf(oldTimesArgs, oldTimesArgs.length);
				newTimesArgs[this.position] = oldPlusArgs[i];
				newPlusArgs[i] =
						new FunctionWithArgumentsComposite(this.upperGwac.getFunction(),
								newTimesArgs);
			}
			this.result = new FunctionWithArgumentsComposite(function, newPlusArgs);
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.Times<?> function) {
			// *(*(a,b),c) -> *(a,b,c)
			this.result = combineSameFunction(function);
		}

		@Override
		public void visit(final org.jamocha.function.impls.functions.UnaryMinus<?> function) {
			// *(-(a),b) -> -(*(a,b))
			// upperGwac - times
			// lowerGwac - unary minus
			// unwrap parameter i from its unary minus and replace it inplace
			final FunctionWithArguments[] newArgs = this.upperGwac.getArgs().clone();
			newArgs[this.position] = this.lowerGwac.getArgs()[0];
			this.result =
					new FunctionWithArgumentsComposite(function,
							new FunctionWithArgumentsComposite(this.upperGwac.getFunction(),
									newArgs));
		}
	}
}
