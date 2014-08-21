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

import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.ConditionalElementsVisitor;

/**
 * @author "Christoph Terwelp <christoph.terwelp@rwth-aachen.de>"
 *
 */
public class ConditionalElementFormatter implements Formatter<ConditionalElement> {

	private static final ConditionalElementFormatter singleton = new ConditionalElementFormatter();

	static public ConditionalElementFormatter getConditionalElementFormatter() {
		return singleton;
	}

	public static String formatCE(ConditionalElement ce) {
		return getConditionalElementFormatter().format(ce);
	}

	private ConditionalElementFormatter() {
	}

	@Override
	public String format(ConditionalElement ce) {
		ConditionalElementFormatterVisitor cef = new ConditionalElementFormatterVisitor();
		ce.accept(cef);
		return cef.getString();
	}

	private class ConditionalElementFormatterVisitor implements ConditionalElementsVisitor {

		final private StringBuilder sb = new StringBuilder();

		public String getString() {
			return sb.toString();
		}

		private void prettyPrint(String name, ConditionalElement ce) {
			sb.append("(" + name);
			ce.getChildren().forEach((x) -> {
				sb.append(" ");
				x.accept(this);
			});
			sb.append(")");
		}

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			prettyPrint("and", ce);
		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
			prettyPrint("exists", ce);
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
			sb.append("(initialFact)");
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			prettyPrint("not exists", ce);
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			prettyPrint("not", ce);
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			prettyPrint("or", ce);
		}

		@Override
		public void visit(TestConditionalElement ce) {
			sb.append("(test ");
			sb.append(FunctionWithArgumentsFormatter.formatFwa(ce.getPredicateWithArguments()));
			sb.append(")");
		}

		@Override
		public void visit(SharedConditionalElementWrapper ce) {
			sb.append("(shared ");
			ce.getCe().accept(this);
			sb.append(")");
		}

		@Override
		public void visit(TemplatePatternConditionalElement ce) {
			sb.append("(template ");
			sb.append(ce.getFactVariable().getTemplate().getName());
			sb.append(")");
		}
	}

}
