/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.languages.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class RuleCondition {
	final HashMap<Symbol, List<SingleVariable>> variables = new HashMap<>();
	final List<ConditionalElement> conditionalElements = new ArrayList<>();

	public void addSingleVariable(final SingleVariable singleVariable) {
		this.variables.computeIfAbsent(singleVariable.image, s -> new ArrayList<>()).add(
				singleVariable);
	}

	public List<SingleVariable> getVariablesForSymbol(final Symbol symbol) {
		return this.variables.get(symbol);
	}
	
	
}
