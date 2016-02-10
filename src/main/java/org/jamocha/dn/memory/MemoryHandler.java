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
package org.jamocha.dn.memory;

/**
 * Super interface of the memory handlers {@link MemoryHandlerMain} and {@link MemoryHandlerTemp}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see MemoryHandlerMain
 * @see MemoryHandlerTemp
 */
public interface MemoryHandler {
    /**
     * Gets the size of the underlying memory.
     *
     * @return the size of the underlying memory
     */
    int size();

    /**
     * Gets the {@link Template} of the facts in the underlying memory.
     *
     * @return the {@link Template} of the facts in the underlying memory.
     * @see Template
     */
    Template[] getTemplate();

    /**
     * Fetches a value from the memory fully identified by a {@link FactAddress}, a {@link SlotAddress} and a row
     * number.
     *
     * @param address
     *         a {@link FactAddress} identifying the fact the wanted value is in
     * @param slot
     *         a {@link SlotAddress} identifying the slot the wanted value is in
     * @param row
     *         the row number in the underlying memory
     * @return a value from the memory identified by the given parameters
     * @see FactAddress
     * @see SlotAddress
     */
    Object getValue(final FactAddress address, final SlotAddress slot, final int row);

    /**
     * Returns an array of fact identifiers corresponding to the facts in the specified row. Null entries indicate
     * existential parts.
     *
     * @param row
     *         row to map
     * @return fact identifiers corresponding to the facts in the specified row
     */
    FactIdentifier[] getFactIdentifiers(final int row);
}
