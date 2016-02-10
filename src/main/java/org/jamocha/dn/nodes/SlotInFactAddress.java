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
package org.jamocha.dn.nodes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.SlotAddress;

/**
 * Combines a {@link FactAddress fact address} with a {@link SlotAddress slot address}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see FactAddress
 * @see SlotAddress
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class SlotInFactAddress {
    /**
     * -- GETTER --
     *
     * Getter for the {@link FactAddress} identifying a fact in a row in a MemoryHandler;
     */
    final FactAddress factAddress;
    /**
     * -- GETTER --
     *
     * Getter for the {@link SlotAddress} identifying a slot in a fact.
     */
    final SlotAddress slotAddress;
}
