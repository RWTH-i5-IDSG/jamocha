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
import lombok.Getter;
import lombok.NonNull;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Path;
import org.jamocha.function.Function;

/**
 * A parameter of a {@link Function} may be a slot of a {@link org.jamocha.dn.memory.Fact}. The corresponding {@link
 * SlotAddress} and {@link Path} are stored in this class. As soon as the {@link org.jamocha.dn.nodes.Node} representing
 * the surrounding {@link org.jamocha.filter.NodeFilterSet} has been created, the {@link
 * org.jamocha.filter.NodeFilterSet} is {@link org.jamocha.filter
 * .PathNodeFilterSetToAddressNodeFilterSetTranslator#translate(PathNodeFilterSet,
 * CounterColumnMatcher)} translated} and all {@link PathLeaf PathLeafs} are replaced with {@link ParameterLeaf
 * ParameterLeafs}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Path
 * @see SlotAddress
 * @see org.jamocha.dn.nodes.Node
 */
@EqualsAndHashCode
public class PathLeaf implements ExchangeableLeaf<PathLeaf> {
    @NonNull
    private final Path path;
    private final SlotAddress slot;
    @Getter(lazy = true)
    private final int hashCode = initHashCode();

    public PathLeaf(final Path path, final SlotAddress slot) {
        assert path != null;
        this.path = path;
        this.slot = slot;
    }

    private int initHashCode() {
        return FunctionWithArguments
                .hash(Arrays.asList(this.path.getTemplate(), this.slot).stream().mapToInt(java.util.Objects::hashCode)
                        .toArray(), FunctionWithArguments.POSITION_IS_IRRELEVANT);
    }

    @Override
    public String toString() {
        return getPath().toString(slot);
    }

    @Override
    public SlotType[] getParamTypes() {
        return new SlotType[]{getPath().getTemplate().getSlotType(getSlot())};
    }

    @Override
    public SlotType getReturnType() {
        if (null == slot) return SlotType.FACTADDRESS;
        return getSlot().getSlotType(getPath().getTemplate());
    }

    @Override
    public Function<?> lazyEvaluate(final Function<?>... params) {
        throw new UnsupportedOperationException("Evaluate not allowed for PathLeafs!");
    }

    @Override
    public Object evaluate(final Object... params) {
        throw new UnsupportedOperationException("Evaluate not allowed for PathLeafs!");
    }

    @Override
    public <T extends FunctionWithArgumentsVisitor<PathLeaf>> T accept(final T visitor) {
        visitor.visit(this);
        return visitor;
    }

    /**
     * @return the path
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * @return the slot
     */
    public SlotAddress getSlot() {
        return this.slot;
    }

    @Override
    public int hashPositionIsIrrelevant() {
        return getHashCode();
    }

    @Override
    public ExchangeableLeaf<PathLeaf> copy() {
        return new PathLeaf(path, slot);
    }
}
