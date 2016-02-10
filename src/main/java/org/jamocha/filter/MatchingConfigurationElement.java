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

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

@Data
@AllArgsConstructor
public class MatchingConfigurationElement implements Comparable<MatchingConfigurationElement> {
    final SlotAddress address;
    final Optional<?> constant;
    final boolean single;

    public MatchingConfigurationElement(final SlotAddress address, final Optional<?> constant,
            final Template template) {
        this(address, constant, !address.getSlotType(template).isArrayType());
    }

    @Override
    public int compareTo(final MatchingConfigurationElement o) {
        return this.address.compareTo(o.address);
    }
}
