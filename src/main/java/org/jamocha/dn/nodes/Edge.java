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

import java.util.LinkedList;
import java.util.Map;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;


/**
 * Interface for classes representing connections between {@link org.jamocha.dn.nodes.Node}s. Edges have a source and a
 * target node, where the source node is closer to the root node and the target node is closer to the terminal nodes.
 * Edges can translate FactAddresses valid in their source nodes to the corresponding addresses valid in their target
 * nodes. Tokens originating in the source node are passed to edges via
 * {@link #enqueueMemory(org.jamocha.dn.memory.MemoryHandlerPlusTemp)}
 * or {@link #enqueueMemory(org.jamocha.dn.memory.MemoryHandlerMinusTemp)} and thereby wrapped into tokens. When
 * processed, these tokens call the corresponding {@link #processPlusToken(MemoryHandlerTemp)} or {@link
 * #processMinusToken(MemoryHandlerTemp)}, respectively. Every edge holds a {@link org.jamocha.filter.NodeFilterSet}
 * equivalent to the filter in its target node, but the order of the {@link org.jamocha.filter.Filter}s may differ.
 * Additionally, edges may hold a subset of these filter elements relevant for existential facts to identify the counter
 * updates needed without having to perform the join. <br /> After a plus token has been processed in the target memory
 * of an edge, its memory handler is kept in the edge as long as its still valid, i.e. until it has been committed into
 * the main memory of the source node of the edge.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.dn.nodes.Node
 * @see org.jamocha.filter.NodeFilterSet
 * @see org.jamocha.filter.Filter
 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp
 * @see org.jamocha.dn.memory.MemoryHandlerMinusTemp
 */
public interface Edge {

    /**
     * Process the given memory with positive annotation in the main memory of the target node. Afterwards enqueue any
     * resulting tokens into the outgoing edges of the target node.
     *
     * @param memory
     *         temp handler to be processed into the main memory of the target node
     * @throws CouldNotAcquireLockException
     *         passed through if thrown by memory operations
     */
    void processPlusToken(final MemoryHandlerTemp memory) throws CouldNotAcquireLockException;

    /**
     * Process the given memory with negative annotation in the main memory of the target node. Afterwards enqueue any
     * resulting tokens into the outgoing edges of the target node.
     *
     * @param memory
     *         temp handler to be processed into the main memory of the target node
     * @throws CouldNotAcquireLockException
     *         passed through if thrown by memory operations
     */
    void processMinusToken(final MemoryHandlerTemp memory) throws CouldNotAcquireLockException;

    /**
     * Returns the source {@link Node} of the edge.
     *
     * @return the source {@link Node} of the edge
     */
    Node getSourceNode();

    /**
     * Returns the target {@link Node} of the edge.
     *
     * @return the target {@link Node} of the edge
     */
    Node getTargetNode();

    /**
     * Returns true iff the target {@link Node} of the edge is part of the beta network.
     *
     * @return true iff the target {@link Node} of the edge is part of the beta network
     */
    boolean targetsBeta();

    /**
     * Transforms an address valid for the source node of the edge into the corresponding address valid for the target
     * node of the edge.
     *
     * @param addressInSource
     *         an address valid in the source node of the input
     * @return an address valid in the target node of the input
     */
    FactAddress localizeAddress(final FactAddress addressInSource);

    /**
     * Sets the map used for {@link Edge#localizeAddress(org.jamocha.dn.memory.FactAddress)}
     *
     * @param map
     *         Map used for {@link Edge#localizeAddress(org.jamocha.dn.memory.FactAddress)}
     */
    void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map);

    /**
     * Disconnects the {@link Edge} from the formerly connected nodes. This will remove the edge from the target node
     * incoming edges as well as from the collection of children of the source node.
     */
    void disconnect();

    /**
     * Sets the filter of the edge to be the filter passed. Also sets the filter parts relevant for counter columns
     * retrievable by {@link #getFilterPartsForCounterColumns()}
     *
     * @param filter
     *         the filter to be set for the edge
     */
    void setFilter(final AddressNodeFilterSet filter);

    /**
     * Returns the filter previously set by {@link #setFilter(org.jamocha.filter.AddressNodeFilterSet)} or the
     * constructor.
     *
     * @return the filter previously set by {@link #setFilter(org.jamocha.filter.AddressNodeFilterSet)} or the
     * constructor
     */
    AddressNodeFilterSet getFilter();

    /**
     * Returns the {@link org.jamocha.filter.Filter}s of the {@link Edge}'s {@link org.jamocha.filter.NodeFilterSet}
     * relevant for {@link org.jamocha.dn.memory.javaimpl.CounterUpdate}s if existential {@link
     * org.jamocha.dn.memory.Fact}s are passed over this {@link Edge}.
     *
     * @return the aforementioned filter elements
     */
    AddressFilter[] getFilterPartsForCounterColumns();

    /**
     * Returns the {@link MemoryHandlerPlusTemp}s processed by the {@link org.jamocha.dn.memory.MemoryHandlerMain} of
     * the target {@link Node} but not yet committed to the {@link org.jamocha.dn.memory.MemoryHandlerMain} of the
     * source {@link Node} ({@link MemoryHandlerPlusTemp}s are invalidated after commit, but not removed from this list,
     * this is e.g. done when processing a token in a sibling edge).
     *
     * @return the aforementioned plus temps
     */
    LinkedList<MemoryHandlerPlusTemp> getTempMemories();

    /**
     * Enqueues the {@link MemoryHandlerPlusTemp} creating a token and adding it to the {@link Node.TokenQueue} of the
     * target node.
     *
     * @param mem
     *         plus temp to wrap and enqueue
     */
    void enqueueMemory(final MemoryHandlerPlusTemp mem);

    /**
     * Enqueues the {@link MemoryHandlerMinusTemp} creating a token and adding it to the {@link Node.TokenQueue} of the
     * target node.
     *
     * @param mem
     *         minus temp to wrap and enqueue
     */
    void enqueueMemory(final MemoryHandlerMinusTemp mem);
}
