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

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;

@Value
@EqualsAndHashCode(of = {"template", "slot"})
public class TemplateSlotLeaf implements ExchangeableLeaf<TemplateSlotLeaf> {
    final Template template;
    final SlotAddress slot;

    final SlotType[] paramTypes;
    final SlotType returnType;

    public TemplateSlotLeaf(final Template template, final SlotAddress slot) {
        this.template = template;
        this.slot = slot;
        this.returnType = template.getSlotType(slot);
        this.paramTypes = new SlotType[]{this.returnType};
    }

    @Override
    public String toString() {
        if (null == slot) return template.getName();
        return template.getName() + "::" + template.getSlotName(slot);
    }

    @Override
    public SlotType[] getParamTypes() {
        return this.paramTypes;
    }

    @Override
    public SlotType getReturnType() {
        return this.returnType;
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
        return FunctionWithArguments
                .hash(Arrays.asList(this.template, this.slot).stream().mapToInt(java.util.Objects::hashCode).toArray(),
                        FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    @Override
    public <V extends FunctionWithArgumentsVisitor<TemplateSlotLeaf>> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    @Override
    public ExchangeableLeaf<TemplateSlotLeaf> copy() {
        throw new UnsupportedOperationException();
    }
}
