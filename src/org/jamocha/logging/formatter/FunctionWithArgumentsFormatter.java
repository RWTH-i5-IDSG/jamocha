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
package org.jamocha.logging.formatter;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class FunctionWithArgumentsFormatter implements Formatter<FunctionWithArguments> {

	private static final FunctionWithArgumentsFormatter singleton = new FunctionWithArgumentsFormatter();

	static public FunctionWithArgumentsFormatter getFunctionWithArgumentsFormatter() {
		return singleton;
	}

	static public String formatFwa(final FunctionWithArguments fwa) {
		return getFunctionWithArgumentsFormatter().format(fwa);
	}

	private FunctionWithArgumentsFormatter() {
	}

	@Override
	public String format(final FunctionWithArguments fwa) {
		final FunctionWithArgumentsFormatterVisitor fwaf = new FunctionWithArgumentsFormatterVisitor();
		fwa.accept(fwaf);
		return fwaf.getString();
	}

	private class FunctionWithArgumentsFormatterVisitor implements FunctionWithArgumentsVisitor {

		final private StringBuilder sb = new StringBuilder();

		public FunctionWithArgumentsFormatterVisitor() {
		}

		public String getString() {
			return sb.toString();
		}

		@SuppressWarnings("rawtypes")
		private void prettyPrint(final GenericWithArgumentsComposite fwa) {
			sb.append("(" + fwa.getFunction().inClips());
			for (final FunctionWithArguments functionWithArguments : fwa.getArgs()) {
				sb.append(" ");
				functionWithArguments.accept(this);
			}
			sb.append(")");
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			prettyPrint(functionWithArgumentsComposite);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			prettyPrint(predicateWithArgumentsComposite);
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			sb.append(constantLeaf.toString());
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			sb.append(parameterLeaf.toString());
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
			sb.append(pathLeaf.toString());
		}

		@Override
		public void visit(final Assert fwa) {
			sb.append(fwa.toString());
		}

		@Override
		public void visit(final TemplateContainer fwa) {
			sb.append(fwa.toString());
		}

		@Override
		public void visit(final Retract fwa) {
			sb.append(fwa.toString());
		}

		@Override
		public void visit(final Modify fwa) {
			sb.append(fwa.toString());
		}

		@Override
		public void visit(final SlotAndValue fwa) {
			sb.append(fwa.toString());
		}

		@Override
		public void visit(final SymbolLeaf fwa) {
			sb.append(fwa.getSymbol().toString());
		}

	}
}
