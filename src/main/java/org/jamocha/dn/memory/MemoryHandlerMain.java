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

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;

/**
 * Interface for main memory implementations. A main memory contains the facts for one {@link Node node}. It is
 * complemented by {@link MemoryHandlerPlusTemp}, which stores join results until they have been adopted by all
 * follow-up nodes and {@link MemoryHandlerMinusTemp}, which similarly stores facts to be deleted. <br /> To prevent
 * data inconsistencies on the one hand and deadlocks on the other, a fair read-write-lock is needed to handle read- and
 * write-operations on the main memory. We consider a read-write-lock as fair, if it stalls further readers as soon as a
 * writer tries to acquire the write-lock.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see MemoryHandlerPlusTemp
 * @see MemoryHandlerMinusTemp
 * @see Node
 */
public interface MemoryHandlerMain extends MemoryHandler {
    /**
     * Creates a new {@link MemoryHandlerPlusTemp} that joins the given {@code token} with all other incoming edges of
     * the target beta {@link Node node} applying the given {@link org.jamocha.filter.NodeFilterSet filter}.
     *
     * @param token
     *         {@link MemoryHandlerPlusTemp token} to join with all other inputs
     * @param originIncomingEdge
     *         {@link Edge edge} the token arrived on
     * @param filter
     *         {@link org.jamocha.filter.NodeFilterSet filter} to apply
     * @return {@link MemoryHandlerPlusTemp token} containing the result of the join
     * @throws CouldNotAcquireLockException
     *         iff one of the locks could not be acquired
     */
    MemoryHandlerTemp processTokenInBeta(final MemoryHandlerTemp token, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException;

    /**
     * Creates a new {@link MemoryHandlerPlusTemp} that contains the part of the facts in the given token that match the
     * given filter.
     *
     * @param token
     *         {@link MemoryHandlerPlusTemp token} to process
     * @param originIncomingEdge
     *         {@link Edge edge} the token arrived on
     * @param filter
     *         {@link org.jamocha.filter.NodeFilterSet filter} to apply
     * @return {@link MemoryHandlerPlusTemp token} containing the result of the filter operation
     * @throws CouldNotAcquireLockException
     *         iff one of the locks could not be acquired
     */
    MemoryHandlerTemp processTokenInAlpha(final MemoryHandlerTemp token, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException;

    /**
     * Creates a new token containing
     *
     * @return
     */
    MemoryHandlerPlusTemp newNewNodeToken();

    /**
     * Creates a new {@link MemoryHandlerPlusTemp} that contains the facts given.
     *
     * @param otn
     *         {@link Node node} the facts are for
     * @param facts
     *         {@link Fact facts} to store in the {@link MemoryHandlerPlusTemp token}
     * @return {@link MemoryHandlerPlusTemp token} containing the facts given
     */
    Pair<? extends MemoryHandlerPlusTemp, MemoryFact[]> newPlusToken(final Node otn, final Fact... facts);

    /**
     * Creates a new {@link MemoryHandlerMinusTemp} that contains the facts given.
     *
     * @param facts
     *         {@link Fact facts} to store in the {@link MemoryHandlerMinusTemp token}
     * @return {@link MemoryHandlerMinusTemp token} containing the facts given
     */
    MemoryHandlerMinusTemp newMinusToken(final MemoryFact... facts);

    /**
     * Creates a new
     * {@link MemoryHandlerTerminal} that caches {@link org.jamocha.dn.memory.MemoryHandlerTerminal.Assert}s
     * until the corresponding {@link org.jamocha.dn.memory.MemoryHandlerTerminal.Retract} arrives.
     *
     * @return the newly created {@link MemoryHandlerTerminal}
     */
    MemoryHandlerTerminal newMemoryHandlerTerminal();

    /**
     * Determines the filter elements that contain existential addresses passed over the given edge.
     *
     * @param filter
     *         filter containing the filter elements to be inspected
     * @param edge
     *         edge used to localize the fact addresses of the memory to match the filter addresses. The memory of the
     *         source node of the edge is the memory used to call this method.
     * @return filter elements containing existential addresses passed over the given edge
     */
    AddressFilter[] getRelevantExistentialFilterParts(final AddressNodeFilterSet filter, final Edge edge);

}
