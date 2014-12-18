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

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * Collects all symbols used within the {@link FunctionWithArguments}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SymbolInSymbolLeafsCollector implements DefaultConditionalElementsVisitor,
		DefaultFunctionWithArgumentsLeafVisitor<SymbolLeaf> {
	@Getter
	private Set<VariableSymbol> symbols = new HashSet<>();

	public SymbolInSymbolLeafsCollector(final ConditionalElement ce) {
		ce.accept(this);
	}

	public static Set<VariableSymbol> collect(final ConditionalElement ce) {
		return new SymbolInSymbolLeafsCollector(ce).symbols;
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		for (ConditionalElement child : ce.getChildren()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
	}

	@Override
	public void visit(final GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
	}

	@Override
	public void visit(final SymbolLeaf leaf) {
		this.symbols.add(leaf.getSymbol());
	}
}
