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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class MemoryHandlerTemp extends MemoryHandlerBase implements org.jamocha.dn.memory.MemoryHandlerTemp {

    final MemoryHandlerMain originatingMainHandler;

    protected MemoryHandlerTemp(final MemoryHandlerMain originatingMainHandler, final JamochaArray<Row> validRows) {
        this(originatingMainHandler.template, originatingMainHandler, validRows);
    }

    protected MemoryHandlerTemp(final Template[] template, final MemoryHandlerMain originatingMainHandler,
            final JamochaArray<Row> validRows) {
        super(template, validRows);
        this.originatingMainHandler = originatingMainHandler;
    }

    @Override
    public List<MemoryHandler> splitIntoChunksOfSize(final int size) {
        final List<MemoryHandler> memoryHandlers = new ArrayList<>();
        final JamochaArray<Row> rowsToSplit = getRowsToSplit();
        final int max = rowsToSplit.size();
        if (size >= max) {
            memoryHandlers.add(this);
            return memoryHandlers;
        }
        int current = 0;
        while (current < max) {
            final JamochaArray<Row> facts = new JamochaArray<>();
            for (int i = 0; i < size && current + i < max; ++i) {
                facts.add(rowsToSplit.get(current + i));
            }
            memoryHandlers.add(new MemoryHandlerBase(getTemplate(), facts));
            current += size;
        }
        return memoryHandlers;
    }

    protected JamochaArray<Row> getRowsToSplit() {
        return this.validRows;
    }

    protected static boolean applyFilterElement(final Fact fact, final AddressFilter element) {
        // determine parameters
        final SlotInFactAddress[] addresses = element.getAddressesInTarget();
        final int paramLength = element.getFunction().getParamTypes().length;
        final Object[] params = new Object[paramLength];
        for (int i = 0; i < paramLength; ++i) {
            final SlotInFactAddress address = addresses[i];
            params[i] = fact.getValue(address.getSlotAddress());
        }
        // check filter
        return element.getFunction().evaluate(params);
    }

    public abstract org.jamocha.dn.memory.MemoryHandlerTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
            final Edge originIncomingEdge, final AddressNodeFilterSet filter) throws CouldNotAcquireLockException;

    public abstract org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler,
            final Edge originIncomingEdge, final AddressNodeFilterSet filter) throws CouldNotAcquireLockException;

    public abstract org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(
            final MemoryHandlerMainWithExistentials originatingMainHandler, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException;
}
