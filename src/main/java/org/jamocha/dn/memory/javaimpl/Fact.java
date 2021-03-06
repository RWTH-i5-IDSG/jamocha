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
package org.jamocha.dn.memory.javaimpl;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;

/**
 * Implementation of a Fact. Stores values as Objects.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
@EqualsAndHashCode
class Fact implements MemoryFact {
    @Getter(onMethod = @__({@Override}))
    private final Template template;

    private final Object[] slotValues;

    private final FactIdentifier factIdentifier;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("f-").append(null == this.factIdentifier ? "?" : this.factIdentifier.getId()).append(": (")
                .append(this.template.getName());
        for (int i = 0; i < this.slotValues.length; ++i) {
            final Object value = this.slotValues[i];
            if (null == value) continue;
            sb.append(" (").append(this.template.getSlotName(i)).append(' ').append(value).append(')');
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public FactIdentifier getFactIdentifier() {
        return this.factIdentifier;
    }

    /**
     * Retrieves the value stored in the slot identified by {@link SlotAddress slot}.
     *
     * @param slot
     *         {@link SlotAddress slot address} identifying the slot the value is stored in
     * @return the value stored in the slot identified by {@link SlotAddress slot}
     */
    @Override
    public Object getValue(final org.jamocha.dn.memory.SlotAddress slot) {
        if (null == slot) return this.factIdentifier;
        return this.slotValues[((SlotAddress) slot).getIndex()];
    }

    public static boolean equalContent(final Fact a, final Fact b) {
        if (a == b) return true;
        return Arrays.deepEquals(a.slotValues, b.slotValues);
    }

    public static boolean equalReference(final Fact a, final Fact b) {
        return a == b;
    }

    @Override
    public org.jamocha.dn.memory.Fact toMutableFact() {
        return new org.jamocha.dn.memory.Fact(this.template, this.slotValues);
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    public static class MultislotPatternMatching extends Fact {
        final int[] separators;

        MultislotPatternMatching(final Template template, final Object[] slotValues,
                final FactIdentifier factIdentifier, final int[] separators) {
            super(template, slotValues, factIdentifier);
            this.separators = separators;
        }

        MultislotPatternMatching(final Fact origin, final int[] separators) {
            this(origin.template, origin.slotValues, origin.factIdentifier, separators);
        }

        @Override
        public Object getValue(final org.jamocha.dn.memory.SlotAddress slotAddress) {
            final MatchingElementAddress addr = (MatchingElementAddress) slotAddress;
            final Object[] values = (Object[]) super.getValue(addr.origin);
            final int index = addr.matchingIndex;
            final int from = 0 == index ? 0 : this.separators[index - 1];
            final int to = index < this.separators.length ? this.separators[index] : values.length;
            if (addr.single) {
                assert 1 == to - from;
                return values[from];
            }
            return Arrays.copyOfRange(values, from, to);
        }
    }
}
