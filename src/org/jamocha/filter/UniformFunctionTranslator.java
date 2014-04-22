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

import org.jamocha.filter.fwa.ConstantLeaf;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.FunctionWithArgumentsComposite;
import org.jamocha.filter.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.filter.fwa.PathLeaf;
import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class UniformFunctionTranslator {
	public static PredicateWithArguments translate(
			final PredicateWithArguments predicateWithArguments) {
		final PredicateWithArguments transformed =
				predicateWithArguments.accept(new TopLevelTranslator()).functionWithArguments;
		return transformed;
	}

	static interface Visitor extends FunctionWithArgumentsVisitor {
		@Override
		public default void visit(
				final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void visit(
				final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void visit(final ConstantLeaf constantLeaf) {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void visit(final ParameterLeaf parameterLeaf) {
			throw new UnsupportedOperationException();
		}

		@Override
		public default void visit(final PathLeaf pathLeaf) {
			throw new UnsupportedOperationException();
		}
	}

	private static FunctionWithArguments[] translateArgs(
			final FunctionWithArguments[] originalArgs, final FunctionWithArguments parent) {
		final int numArgs = originalArgs.length;
		final FunctionWithArguments[] translatedArgs = new FunctionWithArguments[numArgs];
		for (int i = 0; i < numArgs; ++i) {
			final FunctionWithArguments originalArg = originalArgs[i];
			translatedArgs[i] =
					originalArg.accept(new FunctionWithArgumentsTranslator(parent)).functionWithArguments;
		}
		return translatedArgs;
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	private static class TopLevelTranslator implements Visitor {
		private PredicateWithArguments functionWithArguments;

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			assert null == functionWithArguments;
			final FunctionWithArguments[] args = predicateWithArgumentsComposite.getArgs();
			// first look at the arguments
			translateArgs(args);

			final Predicate function = predicateWithArgumentsComposite.getFunction();
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(function, translateArgs(args,
							predicateWithArgumentsComposite));

			translateArgs(args);
		}

		private static void translateArgs(final FunctionWithArguments[] args) {
			for (int i = 0; i < args.length; i++) {
				boolean changed = true;
				do {
					final FunctionWithArguments translated =
							args[i].accept(new FunctionWithArgumentsTranslator(null/* TODO */)).functionWithArguments;
					changed = args[i] != translated;
					args[i] = translated;
				} while (changed);
			}
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	private static class FunctionWithArgumentsTranslator implements Visitor {
		private final FunctionWithArguments parent;
		private FunctionWithArguments functionWithArguments;

		private FunctionWithArgumentsTranslator(final FunctionWithArguments parent) {
			this.parent = parent;
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			this.functionWithArguments =
					new FunctionWithArgumentsComposite(
							functionWithArgumentsComposite.getFunction(),
							translateArgs(functionWithArgumentsComposite.getArgs()));
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(
							predicateWithArgumentsComposite.getFunction(),
							translateArgs(predicateWithArgumentsComposite.getArgs()));

		}
	}
}
