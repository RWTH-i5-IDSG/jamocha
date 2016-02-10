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

import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.jamocha.dn.memory.SlotType;

@Value
@EqualsAndHashCode(of = {"type"})
public class TypeLeaf implements ExchangeableLeaf<TypeLeaf> {
    final SlotType type;

    final SlotType[] paramTypes;

    public TypeLeaf(final SlotType type) {
        this.type = type;
        this.paramTypes = new SlotType[]{this.type};
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public SlotType[] getParamTypes() {
        return this.paramTypes;
    }

    @Override
    public SlotType getReturnType() {
        return this.type;
    }

    @Override
    public org.jamocha.function.Function<?> lazyEvaluate(final org.jamocha.function.Function<?>... params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final Object... params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashPositionIsIrrelevant() {
        return FunctionWithArguments.hash(new int[]{Objects.hash(type)}, FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    @Override
    public <V extends FunctionWithArgumentsVisitor<TypeLeaf>> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    @Override
    public ExchangeableLeaf<TypeLeaf> copy() {
        throw new UnsupportedOperationException();
    }
}
