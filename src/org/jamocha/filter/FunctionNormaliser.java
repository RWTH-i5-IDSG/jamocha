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

import org.jamocha.filter.fwa.Assert;
import org.jamocha.filter.fwa.Assert.TemplateContainer;
import org.jamocha.filter.fwa.ConstantLeaf;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.FunctionWithArgumentsComposite;
import org.jamocha.filter.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.filter.fwa.GenericWithArgumentsComposite;
import org.jamocha.filter.fwa.Modify;
import org.jamocha.filter.fwa.PathLeaf;
import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.filter.fwa.Retract;
import org.jamocha.filter.fwa.SymbolLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class FunctionNormaliser {

	public static PredicateWithArguments normalise(
			final PredicateWithArguments predicateWithArguments) {
		predicateWithArguments.accept(new FWANormaliser());
		return predicateWithArguments;
	}

	private static class FWANormaliser implements FunctionWithArgumentsVisitor {
		private static void handle(final GenericWithArgumentsComposite<?, ?> gwac) {
			final FunctionWithArguments[] args = gwac.getArgs();
			recurseOverArgs(args);
			if (!(gwac.getFunction() instanceof CommutativeFunction<?>)) {
				return;
			}
			sortArgs(args);
		}

		private static void recurseOverArgs(final FunctionWithArguments[] args) {
			Arrays.stream(args).forEach(fwa -> fwa.accept(new FWANormaliser()));
		}

		private static void sortArgs(final FunctionWithArguments[] args) {
			Arrays.<FunctionWithArguments> sort(args, (a, b) -> Integer.compare(a.hash(), b.hash()));
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			handle(functionWithArgumentsComposite);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			handle(predicateWithArgumentsComposite);
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
		}

		@Override
		public void visit(final Assert fwa) {
			final TemplateContainer[] args = fwa.getArgs();
			recurseOverArgs(args);
			sortArgs(args);
		}

		@Override
		public void visit(final Assert.TemplateContainer fwa) {
			// don't permute the order of the template container args, as they are the objects
			// passed to the ctor of the fact
			recurseOverArgs(fwa.getArgs());
		}

		@Override
		public void visit(final Modify fwa) {
			fwa.getTargetFact().accept(new FWANormaliser());
			final FunctionWithArguments[] args = fwa.getArgs();
			recurseOverArgs(args);
			sortArgs(args);
		}

		@Override
		public void visit(final Modify.SlotAndValue fwa) {
			fwa.getValue().accept(new FWANormaliser());
		}

		@Override
		public void visit(final Retract fwa) {
			final FunctionWithArguments[] args = fwa.getArgs();
			recurseOverArgs(args);
			sortArgs(args);
		}

		@Override
		public void visit(final SymbolLeaf fwa) {
		}
	}
}
