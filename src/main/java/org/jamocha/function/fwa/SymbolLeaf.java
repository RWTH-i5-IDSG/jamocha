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
package org.jamocha.function.fwa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@ToString(of = {"symbol"})
public class SymbolLeaf implements ExchangeableLeaf<SymbolLeaf> {
    private final VariableSymbol symbol;

    @Getter(lazy = true)
    private final int hashCode = initHashCode();

    private int initHashCode() {
        return FunctionWithArguments
                .hash(new int[]{this.symbol.hashCode()}, FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    @Override
    public SlotType[] getParamTypes() {
        return new SlotType[]{symbol.getType()};
    }

    @Override
    public SlotType getReturnType() {
        return symbol.getType();
    }

    @Override
    public Function<?> lazyEvaluate(final Function<?>... params) {
        throw new UnsupportedOperationException("Evaluate not allowed for SymbolLeafs!");
    }

    @Override
    public Object evaluate(final Object... params) {
        throw new UnsupportedOperationException("Evaluate not allowed for SymbolLeafs!");
    }

    @Override
    public int hashPositionIsIrrelevant() {
        return getHashCode();
    }

    @Override
    public <V extends FunctionWithArgumentsVisitor<SymbolLeaf>> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    @Override
    public ExchangeableLeaf<SymbolLeaf> copy() {
        return new SymbolLeaf(symbol);
    }
}
