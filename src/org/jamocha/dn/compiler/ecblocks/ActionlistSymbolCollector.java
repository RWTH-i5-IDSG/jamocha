/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.dn.compiler.ecblocks;

import java.util.Set;

import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ActionlistSymbolCollector implements DefaultFunctionWithArgumentsLeafVisitor<SymbolLeaf> {

	final Set<VariableSymbol> boundSymbols = Sets.newIdentityHashSet();
	final Set<VariableSymbol> symbols = Sets.newIdentityHashSet();

	public static Set<VariableSymbol> getUnboundSymbols(final FunctionWithArguments<SymbolLeaf>[] actionList) {
		final ActionlistSymbolCollector instance = new ActionlistSymbolCollector();
		for (final FunctionWithArguments<SymbolLeaf> fwa : actionList) {
			fwa.accept(instance);
		}
		return Sets.difference(instance.symbols, instance.boundSymbols);
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

	@Override
	public void visit(final Bind<SymbolLeaf> fwa) {
		final SymbolLeaf leaf = (SymbolLeaf) fwa.getArgs()[0];
		this.boundSymbols.add(leaf.getSymbol());
		for (int i = 1; i < fwa.getArgs().length; ++i) {
			fwa.getArgs()[i].accept(this);
		}
	}
}
