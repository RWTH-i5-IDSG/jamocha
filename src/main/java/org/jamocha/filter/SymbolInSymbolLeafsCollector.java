/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
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
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * Collects all symbols used within the {@link FunctionWithArguments}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SymbolInSymbolLeafsCollector
        implements DefaultConditionalElementsVisitor<SymbolLeaf>, DefaultFunctionWithArgumentsLeafVisitor<SymbolLeaf> {
    @Getter
    private Set<VariableSymbol> symbols = new HashSet<>();

    public static Set<VariableSymbol> collect(final ConditionalElement<SymbolLeaf> ce) {
        return ce.accept(new SymbolInSymbolLeafsCollector()).symbols;
    }

    public static Set<VariableSymbol> collect(final FunctionWithArguments<SymbolLeaf> fwa) {
        return fwa.accept(new SymbolInSymbolLeafsCollector()).symbols;
    }

    @Override
    public void visit(final TestConditionalElement<SymbolLeaf> ce) {
        ce.getPredicateWithArguments().accept(this);
    }

    @Override
    public void defaultAction(final ConditionalElement<SymbolLeaf> ce) {
        for (final ConditionalElement<SymbolLeaf> child : ce.getChildren()) {
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
