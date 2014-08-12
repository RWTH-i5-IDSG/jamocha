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
import org.jamocha.languages.common.ConditionalElementsVisitor;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;

/**
 * @author "Christoph Terwelp <christoph.terwelp@rwth-aachen.de>"
 *
 */
public class ConditionalElementFormatter extends Formatter {

	private static final ConditionalElementFormatter singleton = new ConditionalElementFormatter();

	static public ConditionalElementFormatter getConditionalElementFormatter() {
		return singleton;
	}

	private ConditionalElementFormatter() {
	}

	public String format(ConditionalElement ce) {
		ConditionalElementFormatterVisitor cef = new ConditionalElementFormatterVisitor();
		ce.accept(cef);
		return cef.getString();
	}

	public String format(ConditionalElement ce, int level) {
		ConditionalElementFormatterVisitor cef = new ConditionalElementFormatterVisitor(level);
		ce.accept(cef);
		return cef.getString();
	}

	private class ConditionalElementFormatterVisitor implements ConditionalElementsVisitor {

		final private StringBuilder sb = new StringBuilder();
		int level = 0;

		public ConditionalElementFormatterVisitor() {
		}

		public ConditionalElementFormatterVisitor(int level) {
			this.level = level;
		}

		public String getString() {
			return sb.toString();
		}

		private void indent() {
			for (int i = 0; i < level; i++) {
				sb.append("   ");
			}
		}

		private void prettyPrint(String name, ConditionalElement ce) {
			indent();
			level++;
			sb.append("(" + name + "\n");
			ce.getChildren().forEach((x) -> {
				x.accept(this);
			});
			level--;
			indent();
			sb.append(")\n");
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
			indent();
			sb.append("(initialFact)\n");
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
			indent();
			sb.append("(test\n");
			level++;
			indent();
			sb.append(ce.getFwa().toString());
			level--;
			sb.append("\n");
			indent();
			sb.append(")\n");
		}

	}

}
