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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
class MatchingElementAddress extends SlotAddress {
    final SlotAddress origin;
    final int matchingIndex;
    final boolean single;

    MatchingElementAddress(final SlotAddress origin, final int matchingIndex, final boolean single) {
        super(origin.index);
        this.origin = origin;
        this.matchingIndex = matchingIndex;
        this.single = single;
    }

    @Override
    public SlotType getSlotType(final Template template) {
        final SlotType slotType = this.origin.getSlotType(template);
        return (this.single ? SlotType.arrayToSingle(slotType) : slotType);
    }

    @Override
    public int compareTo(final org.jamocha.dn.memory.SlotAddress o) {
        final int superCompare = super.compareTo(o);
        if (0 != superCompare) {
            return superCompare;
        }
        if (!(o instanceof MatchingElementAddress)) {
            return 1;
        }
        return Integer.compare(this.matchingIndex, ((MatchingElementAddress) o).matchingIndex);
    }
}
