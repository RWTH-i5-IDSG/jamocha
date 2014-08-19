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
package org.jamocha.languages.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;

import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RuleCondition {
	@Getter
	final List<SingleFactVariable> singleFactVariables = new ArrayList<>();
	@Getter
	final List<SingleSlotVariable> singleSlotVariables = new ArrayList<>();
	@Getter
	final List<ConditionalElement> conditionalElements = new ArrayList<>();

	public void addSingleVariable(final SingleFactVariable singleVariable) {
		this.singleFactVariables.add(singleVariable);
	}

	public void addSingleVariable(final SingleSlotVariable singleVariable) {
		this.singleSlotVariables.add(singleVariable);
	}

	public Stream<Symbol> getSymbols() {
		return Stream.concat(singleFactVariables.stream().map(SingleFactVariable::getSymbol),
				singleSlotVariables.stream().map(SingleSlotVariable::getSymbol));
	}

	public void addConditionalElement(final ConditionalElement conditionalElement) {
		this.conditionalElements.add(conditionalElement);
	}

	public void addConditionalElements(final Collection<ConditionalElement> conditionalElement) {
		this.conditionalElements.addAll(conditionalElement);
	}

	public void flatten() {
		// add surrounding (and ), if more than one CE
		if (this.conditionalElements.size() > 1) {
			final List<ConditionalElement> tmplist = new ArrayList<ConditionalElement>();
			tmplist.addAll(this.conditionalElements);
			ConditionalElement ce = new AndFunctionConditionalElement(tmplist);
			this.conditionalElements.clear();
			this.conditionalElements.add(ce);
		}

		// move (not )s down to the lowest possible nodes
		ConditionalElement ce = this.conditionalElements.get(0);
		this.conditionalElements.remove(0);
		this.conditionalElements.add(RuleConditionProcessor.moveNots(ce));

		// combine nested ands and ors
		RuleConditionProcessor.combineNested(this.conditionalElements.get(0));

		// expand ors
		ce = this.conditionalElements.get(0);
		this.conditionalElements.remove(0);
		this.conditionalElements.add(RuleConditionProcessor.expandOrs(ce));
	}
}
