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

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;

/**
 * This class stores the {@link SlotType} of the represented Slot only. All other relevant information are stored in the
 * containing {@link org.jamocha.filter.AddressNodeFilterSet.AddressFilter}. A {@link PathLeaf} is translated into a
 * {@link ParameterLeaf} as soon as the {@link org.jamocha.dn.nodes.Node} representing the surrounding {@link
 * org.jamocha.filter.NodeFilterSet} has been created. In doing so, the containing {@link
 * org.jamocha.filter.AddressNodeFilterSet.AddressFilter} stores the corresponding {@link
 * org.jamocha.dn.nodes.SlotInFactAddress} in
 * {@link org.jamocha.filter.AddressNodeFilterSet.AddressFilter#getAddressesInTarget()}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.filter.AddressNodeFilterSet.AddressFilter
 * @see org.jamocha.dn.nodes.SlotInFactAddress
 */
@EqualsAndHashCode
public class ParameterLeaf implements ExchangeableLeaf<ParameterLeaf> {
    private final SlotType slotType;
    private final SlotType[] slotTypes;
    private final int hashCode;

    public ParameterLeaf(final SlotType type, final int hashCode) {
        super();
        this.slotType = type;
        this.slotTypes = new SlotType[]{type};
        this.hashCode = hashCode;
    }

    @Override
    public String toString() {
        return "[" + this.slotType + "]";
    }

    @Override
    public SlotType[] getParamTypes() {
        return this.slotTypes;
    }

    @Override
    public SlotType getReturnType() {
        return this.slotType;
    }

    @Override
    public Function<?> lazyEvaluate(final Function<?>... params) {
        return params[0];
    }

    @Override
    public Object evaluate(final Object... params) {
        return params[0];
    }

    @Override
    public <T extends FunctionWithArgumentsVisitor<ParameterLeaf>> T accept(final T visitor) {
        visitor.visit(this);
        return visitor;
    }

    /**
     * @return the slot type
     */
    public SlotType getType() {
        return this.slotType;
    }

    @Override
    public int hashPositionIsIrrelevant() {
        return this.hashCode;
    }

    @Override
    public ExchangeableLeaf<ParameterLeaf> copy() {
        return new ParameterLeaf(this.slotType, this.hashCode);
    }
}
