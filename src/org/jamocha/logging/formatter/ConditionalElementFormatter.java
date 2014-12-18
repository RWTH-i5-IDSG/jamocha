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

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.tuple.Pair;
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
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author "Christoph Terwelp <christoph.terwelp@rwth-aachen.de>"
 */
@AllArgsConstructor
public class ConditionalElementFormatter implements Formatter<ConditionalElement> {

	final Map<SingleFactVariable, Pair<VariableSymbol, List<Pair<VariableSymbol, SingleSlotVariable>>>> slotVariablesByTemplate;

	@Override
	public String format(final ConditionalElement ce) {
		return ce.accept(new ConditionalElementFormatterVisitor()).getString();
	}

	private class ConditionalElementFormatterVisitor implements ConditionalElementsVisitor {

		final private StringBuilder sb = new StringBuilder();

		public String getString() {
			return sb.toString();
		}

		private void prettyPrint(final String name, final ConditionalElement ce) {
			sb.append("(" + name);
			ce.getChildren().forEach((x) -> {
				sb.append(" ");
				x.accept(this);
			});
			sb.append(")");
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			prettyPrint("and", ce);
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			prettyPrint("exists", ce);
		}

		@Override
		public void visit(final InitialFactConditionalElement ce) {
			sb.append("(initialFact)");
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			prettyPrint("not exists", ce);
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			prettyPrint("not", ce);
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			prettyPrint("or", ce);
		}

		@Override
		public void visit(final TestConditionalElement ce) {
			sb.append("(test ");
			sb.append(FunctionWithArgumentsFormatter.formatFwa(ce.getPredicateWithArguments()));
			sb.append(")");
		}

		@Override
		public void visit(final SharedConditionalElementWrapper ce) {
			sb.append("(shared ");
			ce.getCe().accept(this);
			sb.append(")");
		}

		@Override
		public void visit(final TemplatePatternConditionalElement ce) {
			sb.append("(template ");
			final SingleFactVariable factVariable = ce.getFactVariable();
			sb.append(factVariable.getTemplate().getName());
			formatSingleVariables(factVariable);
			sb.append(")");
		}

		private void formatSingleVariables(final SingleFactVariable factVariable) {
			final Pair<VariableSymbol, List<Pair<VariableSymbol, SingleSlotVariable>>> pair =
					slotVariablesByTemplate.get(factVariable);
			if (null == pair)
				return;
			final List<Pair<VariableSymbol, SingleSlotVariable>> singleSlotVariables = pair.getRight();
			if (null == singleSlotVariables || singleSlotVariables.isEmpty())
				return;
			sb.append(" ");
			final VariableSymbol factVariableSymbol = pair.getLeft();
			sb.append((null == factVariableSymbol) ? ScopeStack.dummySymbolImage : factVariableSymbol.toString());
			singleSlotVariables.forEach(slotVariable -> sb.append(" (")
					.append(slotVariable.getRight().getSlot().getSlotName(factVariable.getTemplate())).append(" ")
					.append(slotVariable.getLeft().toString()).append(")"));
		}
	}
}
