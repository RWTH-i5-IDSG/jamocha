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

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import lombok.Getter;

import org.jamocha.dn.Network;
import org.jamocha.dn.Scheduler;
import org.jamocha.dn.Token;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.filter.PathNodeFilterSetToAddressNodeFilterSetTranslator;
import org.jamocha.visitor.Visitable;

/**
 * Base class for all node types
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public abstract class Node implements Visitable<NodeVisitor> {

    /**
     * Base implementation of the {@link Edge} interface taking care of the intersection of the commonalities of all
     * edges: source node, target node, filter.
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     * @see Edge
     */
    protected abstract class EdgeImpl implements Edge {
        protected final Node sourceNode;
        protected final Node targetNode;
        protected AddressNodeFilterSet filter;
        protected AddressFilter[] filterParts;

        protected EdgeImpl(final Node sourceNode, final Node targetNode, final AddressNodeFilterSet filter) {
            this.sourceNode = sourceNode;
            this.targetNode = targetNode;
            setFilter(filter);
        }

        @Override
        public Node getSourceNode() {
            return this.sourceNode;
        }

        @Override
        public Node getTargetNode() {
            return this.targetNode;
        }

        @Override
        public void disconnect() {
            this.sourceNode.removeChild(this);
        }

        @Override
        public void setFilter(final AddressNodeFilterSet filter) {
            this.filter = filter;
            if (null == filter) {
                this.filterParts = null;
                return;
            }
            this.filterParts = this.sourceNode.memory.getRelevantExistentialFilterParts(filter, this);
        }

        @Override
        public AddressNodeFilterSet getFilter() {
            return this.filter;
        }

        @Override
        public AddressFilter[] getFilterPartsForCounterColumns() {
            return this.filterParts;
        }

        protected void newPlusToken(final MemoryHandlerTemp mem) {
            this.targetNode.enqueue(new Token.PlusToken(mem, this));
        }

        protected void newMinusToken(final MemoryHandlerTemp mem) {
            this.targetNode.enqueue(new Token.MinusToken(mem, this));
        }
    }

    /**
     * Returns a list of all incoming edges.
     *
     * @return a list of all incoming edges
     */
    @Getter
    protected final Edge[] incomingEdges;

    /**
     * Returns a collection of all outgoing edges.
     *
     * @return a collection of all outgoing edges
     */
    @Getter
    protected final Collection<Edge> outgoingEdges = new LinkedList<>();

    /**
     * Returns a collection of the outgoing existential edges.
     *
     * @return a collection of the outgoing existential edges
     */
    @Getter
    protected final Set<Edge> outgoingExistentialEdges = new HashSet<>();

    /**
     * Map used to map {@link FactAddress}es valid in this node to {@link AddressPredecessor}s, whose {@link
     * FactAddress} is valid in a parent node identified by the source node of the {@link Edge} in the {@link
     * AddressPredecessor}.
     */
    protected final Map<FactAddress, AddressPredecessor> delocalizeMap = new HashMap<>();

    /**
     * Returns the {@link MemoryHandlerMain main memory handler} of the node.
     *
     * @return the {@link MemoryHandlerMain main memory handler} of the node
     */
    @Getter
    protected final MemoryHandlerMain memory;
    /**
     * Network INSTANCE this node is used for.
     */
    protected final Network network;
    /**
     * {@link org.jamocha.dn.nodes.Node.TokenQueue} of the node, populated by incoming edges.
     */
    protected TokenQueue tokenQueue;

    /**
     * Returns the filter that has originally been set to all inputs.
     *
     * @return the filter that has originally been set to all inputs
     */
    @Getter
    protected final AddressNodeFilterSet filter;

    /**
     * Returns the Path Node Filter Set.
     *
     * @return the Path Node Filter Set
     */
    @Getter
    private final Set<PathNodeFilterSet> pathNodeFilterSets = new HashSet<>();

    public static class TokenQueue implements Runnable {
        /**
         * Queue of {@link Token}s belonging to this node.
         *
         * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
         */
        private enum TokenQueueState {
            INACTIVE {
                @Override
                public void activate(final TokenQueue context) {
                    if (context.tokenQueue.isEmpty()) {
                        context.state = WAITING_ACTIVE;
                    } else {
                        context.state = ENQUEUED_ACTIVE;
                        context.scheduler.enqueue(context);
                    }
                }

                @Override
                public void deactivate(final TokenQueue context) {
                }

                @Override
                public void enqueue(final TokenQueue context, final Token token) {
                    context.tokenQueue.add(token);
                }

                @Override
                public void run(final TokenQueue context) {
                    // queue was inactivated while waiting for a run-call
                }
            },

            WAITING_ACTIVE {
                @Override
                public void activate(final TokenQueue context) {
                }

                @Override
                public void deactivate(final TokenQueue context) {
                    context.state = INACTIVE;
                }

                /**
                 * Adds a token to the queue, enqueues the queue in the scheduler if the queue was
                 * empty.
                 *
                 * @param context
                 *            token queue context
                 * @param token
                 *            {@link Token} to enqueue
                 */
                @Override
                public void enqueue(final TokenQueue context, final Token token) {
                    assert context.tokenQueue.isEmpty();
                    context.tokenQueue.add(token);
                    context.state = ENQUEUED_ACTIVE;
                    context.scheduler.enqueue(context);
                }

                /**
                 * Calls {@link Token#run()} for the first {@link Token} in the queue and removes it
                 * from the queue if the call was successful. Re-queues itself if there are more
                 * tokens to be run.
                 *
                 * @param context
                 *            token queue context
                 */
                @Override
                public void run(final TokenQueue context) {
                    // legal, if queue was inactivated and re-activated before this call to run
                }
            },

            ENQUEUED_ACTIVE {
                @Override
                public void activate(final TokenQueue context) {
                }

                @Override
                public void deactivate(final TokenQueue context) {
                    context.state = INACTIVE;
                }

                @Override
                public void enqueue(final TokenQueue context, final Token token) {
                    assert !context.tokenQueue.isEmpty();
                    context.tokenQueue.add(token);
                }

                @Override
                public void run(final TokenQueue context) {
                    final Token token = context.tokenQueue.peek();
                    assert null != token; // queue shouldn't have been in the work queue
                    try {
                        token.run();
                    } catch (final CouldNotAcquireLockException ex) {
                        context.scheduler.enqueue(context);
                        return;
                    }
                    synchronized (context) {
                        context.tokenQueue.remove();
                        if (!context.tokenQueue.isEmpty()) {
                            context.scheduler.enqueue(context);
                        } else {
                            context.state = WAITING_ACTIVE;
                        }
                    }
                }
            };

            public abstract void activate(final TokenQueue context);

            public abstract void deactivate(final TokenQueue context);

            /**
             * Adds a token to the queue, enqueues the queue in the scheduler if the queue was empty.
             *
             * @param context
             *         token queue context
             * @param token
             *         {@link Token} to enqueue
             */
            public abstract void enqueue(final TokenQueue context, final Token token);

            /**
             * Calls {@link Token#run()} for the first {@link Token} in the queue and removes it from the queue if the
             * call was successful. Re-queues itself if there are more tokens to be run.
             *
             * @param context
             *         token queue context
             */
            public abstract void run(final TokenQueue context);
        }

        final Scheduler scheduler;
        final Queue<Token> tokenQueue;
        TokenQueueState state;

        public TokenQueue(final Scheduler scheduler) {
            this.scheduler = scheduler;
            this.tokenQueue = new LinkedList<>();
            state = TokenQueueState.INACTIVE;
        }

        public synchronized void activate() {
            this.state.activate(this);
        }

        public synchronized void deactivate() {
            this.state.deactivate(this);
        }

        public synchronized void enqueue(final Token token) {
            this.state.enqueue(this, token);
        }

        @Override
        public synchronized void run() {
            this.state.run(this);
        }
    }

    /**
     * Only for testing purposes.
     */
    @Deprecated
    protected Node(final Network network, final Node... parents) {
        this.network = network;
        this.tokenQueue = new TokenQueue(network.getScheduler());
        this.incomingEdges = new Edge[parents.length];
        final Map<Edge, Set<Path>> edgesAndPaths = new HashMap<>();
        for (int i = 0; i < parents.length; i++) {
            final Edge edge = this.connectRegularParent(parents[i]);
            this.incomingEdges[i] = edge;
            edgesAndPaths.put(edge, null);
        }
        this.filter = AddressNodeFilterSet.EMPTY;
        final MemoryHandlerMainAndCounterColumnMatcher memoryHandlerMainAndCounterColumnMatcher =
                network.getMemoryFactory().newMemoryHandlerMain(PathNodeFilterSet.EMPTY, edgesAndPaths);
        this.memory = memoryHandlerMainAndCounterColumnMatcher.getMemoryHandlerMain();
    }

    protected Node(final Network network, final Template template, final Path... paths) {
        this.network = network;
        this.tokenQueue = new TokenQueue(network.getScheduler());
        this.incomingEdges = new Edge[0];
        this.memory = network.getMemoryFactory().newMemoryHandlerMain(template, paths);
        this.filter = AddressNodeFilterSet.EMPTY;
    }

    public Node(final Network network, final PathNodeFilterSet filter) {
        this.network = network;
        this.tokenQueue = new TokenQueue(network.getScheduler());
        this.pathNodeFilterSets.add(filter);
        final HashSet<Path> paths = PathCollector.newHashSet().collectAllInLists(filter).getPaths();
        final Map<Edge, Set<Path>> edgesAndPaths = new HashMap<>();
        final ArrayList<Edge> edges = new ArrayList<>();
        final Set<Path> joinedPaths = new HashSet<>();
        while (!paths.isEmpty()) {
            // get next path
            final Path path = paths.iterator().next();
            final Set<Path> joinedWith = path.getJoinedWith();
            // create new edge from clNode to this
            final Edge edge;
            if (Collections.disjoint(joinedWith, filter.getNegativeExistentialPaths()) && Collections
                    .disjoint(joinedWith, filter.getPositiveExistentialPaths())) {
                edge = connectRegularParent(path.getCurrentlyLowestNode());
            } else {
                edge = connectExistentialParent(path.getCurrentlyLowestNode());
            }
            // mark all joined paths as done
            paths.removeAll(joinedWith);
            edgesAndPaths.put(edge, joinedWith);
            edges.add(edge);
            // add paths to joined paths
            joinedPaths.addAll(joinedWith);
        }

        this.incomingEdges = toArray(edges, Edge[]::new);
        // create new main memory
        // this also produces translation maps on all our edges
        final MemoryHandlerMainAndCounterColumnMatcher memoryHandlerMainAndCounterColumnMatcher =
                network.getMemoryFactory().newMemoryHandlerMain(filter, edgesAndPaths);
        this.memory = memoryHandlerMainAndCounterColumnMatcher.getMemoryHandlerMain();
        // update all Paths from joinedWith to new addresses
        for (final Entry<Edge, Set<Path>> entry : edgesAndPaths.entrySet()) {
            final Edge edge = entry.getKey();
            final Set<Path> joinedWith = entry.getValue();
            for (final Path path : joinedWith) {
                final FactAddress factAddressInCurrentlyLowestNode = path.getFactAddressInCurrentlyLowestNode();
                path.setCurrentlyLowestNode(this);
                path.setFactAddressInCurrentlyLowestNode(edge.localizeAddress(factAddressInCurrentlyLowestNode));
                path.setJoinedWith(joinedPaths);
            }
        }
        this.filter = PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(filter, memoryHandlerMainAndCounterColumnMatcher.getFilterElementToCounterColumn());
        for (final Edge edge : this.incomingEdges) {
            edge.setFilter(this.filter);
        }

        // Create new PlusToken if no preceding Node has not empty memory
        final Optional<Edge> optMinEdge =
                Arrays.stream(this.incomingEdges).filter(e -> !e.getSourceNode().outgoingExistentialEdges.contains(e))
                        .min((a, b) -> Integer
                                .compare(a.getSourceNode().getMemory().size(), b.getSourceNode().getMemory().size()));
        assert optMinEdge.isPresent();
        final Edge minEdge = optMinEdge.get();
        final Node sourceNode = minEdge.getSourceNode();
        final MemoryHandlerMain minEdgeMemory = sourceNode.getMemory();
        sourceNode.deactivateTokenQueue();
        final MemoryHandlerPlusTemp mem = minEdgeMemory.newNewNodeToken();
        if (mem.size() > 0) {
            minEdge.enqueueMemory(mem);
        }
        sourceNode.activateTokenQueue();
    }

    /**
     * Connects a parent via an edge (to be created by {@link #newEdge(Node)}), that does not contain existential facts,
     * by calling {@link #acceptRegularEdgeToChild(Edge)}.
     *
     * @param parent
     *         parent {@link Node}
     * @return edge created
     */
    protected Edge connectRegularParent(final Node parent) {
        final Edge edge = newEdge(parent);
        parent.acceptRegularEdgeToChild(edge);
        return edge;
    }

    /**
     * Connects a parent via an edge (to be created by {@link #newEdge(Node)}), that contains existential facts, by
     * calling {@link #acceptExistentialEdgeToChild(Edge)}.
     *
     * @param parent
     *         parent {@link Node}
     * @return edge created
     */
    protected Edge connectExistentialParent(final Node parent) {
        final Edge edge = newEdge(parent);
        parent.acceptExistentialEdgeToChild(edge);
        return edge;
    }

    /**
     * Called when a child is added via an edge not containing existential facts. Defaults to adding the edge to the
     * child to the list of outgoing edges.
     *
     * @param edgeToChild
     *         the edge to the child to be added
     */
    protected void acceptRegularEdgeToChild(final Edge edgeToChild) {
        this.outgoingEdges.add(edgeToChild);
    }

    /**
     * Called when a child is added via an edge containing existential facts. Defaults to adding the edge to the child
     * to the list of outgoing edges and to the list of outgoing existential edges.
     *
     * @param edgeToChild
     *         the edge to the child to be added
     */
    protected void acceptExistentialEdgeToChild(final Edge edgeToChild) {
        this.outgoingEdges.add(edgeToChild);
        this.outgoingExistentialEdges.add(edgeToChild);
    }

    /**
     * Called when a child is removed. Defaults to removing the edge to the child from the lists of outgoing edges.
     *
     * @param edgeToChild
     *         the edge to the child to be removed
     */
    protected void removeChild(final Edge edgeToChild) {
        this.outgoingEdges.remove(edgeToChild);
        this.outgoingExistentialEdges.remove(edgeToChild);
    }

    /**
     * Creates a new {@link Edge} which will connect this node (as the edge's target node) and the given source node (as
     * its parent).
     *
     * @param source
     *         source node to connect to this node via a {@link Edge} to be constructed
     * @return {@link Edge} connecting the given source node with this node
     */
    protected abstract Edge newEdge(final Node source);

    /**
     * Returns the number of outgoing edges.
     *
     * @return the number of outgoing edges
     */
    public int getNumberOfOutgoingEdges() {
        return this.outgoingEdges.size();
    }

    /**
     * Transforms an address valid for this node into the corresponding address valid for the source node of the edge
     * specified in {@link AddressPredecessor}.
     *
     * @param localFactAddress
     *         an address valid in the current node
     * @return an {@link AddressPredecessor} containing an address valid in the parent node
     */
    public AddressPredecessor delocalizeAddress(final FactAddress localFactAddress) {
        assert null != localFactAddress;
        assert null != this.delocalizeMap.get(localFactAddress);
        return this.delocalizeMap.get(localFactAddress);
    }

    /**
     * Enqueues the {@link Token} given into the {@link org.jamocha.dn.nodes.Node.TokenQueue} of the {@link Node}.
     *
     * @param token
     *         {@link Token} to enqueue
     */
    private void enqueue(final Token token) {
        this.tokenQueue.enqueue(token);
    }

    /**
     * Shares {@link Node} with the {@link Path}s given.
     *
     * @param paths
     *         {@link Path}s to share the node with
     */
    public abstract void shareNode(final PathNodeFilterSet filter, final Map<Path, FactAddress> map,
            final Path... paths);

    public void activateTokenQueue() {
        this.tokenQueue.activate();
    }

    public void deactivateTokenQueue() {
        this.tokenQueue.deactivate();
    }
}
