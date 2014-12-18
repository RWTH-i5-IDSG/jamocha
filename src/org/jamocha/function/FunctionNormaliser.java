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
package org.jamocha.function;

import java.util.Arrays;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class FunctionNormaliser {

	public static <L extends ExchangeableLeaf<L>> PredicateWithArguments<L> normalise(
			final PredicateWithArguments<L> predicateWithArguments) {
		predicateWithArguments.accept(new FWANormaliser<>());
		return predicateWithArguments;
	}

	public static <L extends ExchangeableLeaf<L>> PredicateWithArgumentsComposite<L> normalise(
			final PredicateWithArgumentsComposite<L> predicateWithArguments) {
		predicateWithArguments.accept(new FWANormaliser<>());
		return predicateWithArguments;
	}

	private static class FWANormaliser<L extends ExchangeableLeaf<L>> implements FunctionWithArgumentsVisitor<L> {
		private static <L extends ExchangeableLeaf<L>> void handle(final GenericWithArgumentsComposite<?, ?, L> gwac) {
			final FunctionWithArguments<L>[] args = gwac.getArgs();
			recurseOverArgs(args);
			if (!(gwac.getFunction() instanceof CommutativeFunction<?>)) {
				return;
			}
			sortArgs(args);
		}

		private static <L extends ExchangeableLeaf<L>> void recurseOverArgs(final FunctionWithArguments<L>[] args) {
			Arrays.stream(args).forEach(fwa -> fwa.accept(new FWANormaliser<>()));
		}

		private static <L extends ExchangeableLeaf<L>> void sortArgs(final FunctionWithArguments<L>[] args) {
			Arrays.<FunctionWithArguments<L>> sort(args, (a, b) -> Integer.compare(a.hash(), b.hash()));
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite<L> functionWithArgumentsComposite) {
			handle(functionWithArgumentsComposite);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite) {
			handle(predicateWithArgumentsComposite);
		}

		@Override
		public void visit(final ConstantLeaf<L> constantLeaf) {
		}

		@Override
		public void visit(final GlobalVariableLeaf<L> globalVariableLeaf) {
		}

		@Override
		public void visit(final Assert<L> fwa) {
			final TemplateContainer<L>[] args = fwa.getArgs();
			recurseOverArgs(args);
			sortArgs(args);
		}

		@Override
		public void visit(final Assert.TemplateContainer<L> fwa) {
			// don't permute the order of the template container args, as they are the objects
			// passed to the ctor of the fact
			recurseOverArgs(fwa.getArgs());
		}

		@Override
		public void visit(final Modify<L> fwa) {
			fwa.getTargetFact().accept(new FWANormaliser<L>());
			final FunctionWithArguments<L>[] args = fwa.getArgs();
			recurseOverArgs(args);
			sortArgs(args);
		}

		@Override
		public void visit(final Modify.SlotAndValue<L> fwa) {
			fwa.getValue().accept(new FWANormaliser<>());
		}

		@Override
		public void visit(final Retract<L> fwa) {
			final FunctionWithArguments<L>[] args = fwa.getArgs();
			recurseOverArgs(args);
			sortArgs(args);
		}

		@Override
		public void visit(final L leaf) {
		}
	}
}
