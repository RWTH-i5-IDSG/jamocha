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

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.LockWrapper.ReadLockWrapper;
import org.jamocha.dn.memory.javaimpl.LockWrapper.WriteLockWrapper;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * Java-implementation of the {@link org.jamocha.dn.memory.MemoryHandlerMain} interface.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryHandlerMain
 */
@ToString(callSuper = true)
public class MemoryHandlerMain extends MemoryHandlerBase implements org.jamocha.dn.memory.MemoryHandlerMain {
    static final long TRY_LOCK_TIMEOUT = 1L;
    static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    final FactAddress[] addresses;
    private final Queue<MemoryHandlerPlusTemp> validOutgoingPlusTokens = new LinkedList<>();
    private final ReadWriteLock validOutgoingPlusTokensQueueLock = new ReentrantReadWriteLock(true);
    final Counter counter;

    MemoryHandlerMain(final Template template, final Path... paths) {
        super(new Template[]{template}, new JamochaArray<Row>());
        final FactAddress address = new FactAddress(0);
        this.addresses = new FactAddress[]{address};
        for (final Path path : paths) {
            path.setFactAddressInCurrentlyLowestNode(address);
            Path.setJoinedWithForAll(path);
        }
        this.counter = Counter.newCounter();
    }

    MemoryHandlerMain(final Template[] template, final Counter counter, final FactAddress[] addresses) {
        super(template, new JamochaArray<>());
        this.addresses = addresses;
        this.counter = counter;
    }

    static class SafeWriteQueue extends WriteLockWrapper implements Iterable<MemoryHandlerPlusTemp> {
        @Getter
        final Queue<MemoryHandlerPlusTemp> queue;

        SafeWriteQueue(final Queue<MemoryHandlerPlusTemp> queue, final ReadWriteLock lock) {
            super(lock);
            this.queue = queue;
        }

        /**
         * @see java.util.Queue#add(java.lang.Object)
         */
        public boolean add(final MemoryHandlerPlusTemp e) {
            return this.queue.add(e);
        }

        /**
         * @see java.util.Queue#remove()
         */
        public MemoryHandlerPlusTemp remove() {
            return this.queue.remove();
        }

        /**
         * @see java.util.Collection#iterator()
         */
        @Override
        public Iterator<MemoryHandlerPlusTemp> iterator() {
            return this.queue.iterator();
        }

        /**
         * @see java.util.Queue#peek()
         */
        public MemoryHandlerPlusTemp peek() {
            return this.queue.peek();
        }

        /**
         * @see java.util.Collection#stream()
         */
        public Stream<MemoryHandlerPlusTemp> stream() {
            return this.queue.stream();
        }
    }

    @RequiredArgsConstructor
    static class SafeReadQueue implements Iterable<MemoryHandlerPlusTemp>, AutoCloseable {
        final Queue<MemoryHandlerPlusTemp> queue;
        final ReadLockWrapper rlw;

        /**
         * @see java.util.Collection#iterator()
         */
        @Override
        public Iterator<MemoryHandlerPlusTemp> iterator() {
            return this.queue.iterator();
        }

        /**
         * @see java.util.Collection#stream()
         */
        public Stream<MemoryHandlerPlusTemp> stream() {
            return this.queue.stream();
        }

        /**
         * @see org.jamocha.dn.memory.javaimpl.LockWrapper.ReadLockWrapper#close()
         */
        @Override
        public void close() {
            this.rlw.close();
        }
    }

    public SafeWriteQueue getWriteableValidOutgoingPlusTokens() {
        return new SafeWriteQueue(this.validOutgoingPlusTokens, this.validOutgoingPlusTokensQueueLock);
    }

    public SafeReadQueue getReadableValidOutgoingPlusTokens() throws CouldNotAcquireLockException {
        return new SafeReadQueue(this.validOutgoingPlusTokens,
                ReadLockWrapper.withTimeout(this.validOutgoingPlusTokensQueueLock));
    }

    public SafeReadQueue getWithoutTimeoutReadableValidOutgoingPlusTokens() {
        return new SafeReadQueue(this.validOutgoingPlusTokens,
                ReadLockWrapper.withoutTimeout(this.validOutgoingPlusTokensQueueLock));
    }

    public static org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher newMemoryHandlerMain(
            final PathNodeFilterSet filterSet, final Map<Edge, Set<Path>> edgesAndPaths) {
        final ArrayList<Template> template = new ArrayList<>();
        final ArrayList<FactAddress> addresses = new ArrayList<>();
        final HashMap<FactAddress, FactAddress> newAddressesCache = new HashMap<>();
        if (!edgesAndPaths.isEmpty()) {
            final Edge[] incomingEdges =
                    edgesAndPaths.entrySet().iterator().next().getKey().getTargetNode().getIncomingEdges();
            for (final Edge edge : incomingEdges) {
                final MemoryHandlerMain memoryHandlerMain = (MemoryHandlerMain) edge.getSourceNode().getMemory();
                for (final Template t : memoryHandlerMain.getTemplate()) {
                    template.add(t);
                }
                final HashMap<FactAddress, FactAddress> addressMap = new HashMap<>();
                for (final FactAddress oldFactAddress : memoryHandlerMain.addresses) {
                    final FactAddress newFactAddress = new FactAddress(addresses.size());
                    addressMap.put(oldFactAddress, newFactAddress);
                    newAddressesCache.put(oldFactAddress, newFactAddress);
                    addresses.add(newFactAddress);
                }
                edge.setAddressMap(addressMap);
            }
        }
        final Template[] templArray = toArray(template, Template[]::new);
        final FactAddress[] addrArray = toArray(addresses, FactAddress[]::new);

        final PathFilterToCounterColumn pathFilterToCounterColumn = new PathFilterToCounterColumn();

        if (filterSet.getPositiveExistentialPaths().isEmpty() && filterSet.getNegativeExistentialPaths().isEmpty()) {
            return new MemoryHandlerMainAndCounterColumnMatcher(
                    new MemoryHandlerMain(templArray, Counter.newCounter(filterSet, pathFilterToCounterColumn),
                            addrArray), pathFilterToCounterColumn);
        }
        final boolean[] existential = new boolean[templArray.length];
        // gather existential paths
        final HashSet<Path> existentialPaths = new HashSet<>();
        existentialPaths.addAll(filterSet.getPositiveExistentialPaths());
        existentialPaths.addAll(filterSet.getNegativeExistentialPaths());

        int index = 0;
        for (final PathFilter pathFilter : filterSet.getFilters()) {
            final HashSet<Path> paths = PathCollector.newHashSet().collect(pathFilter).getPaths();
            paths.retainAll(existentialPaths);
            if (0 == paths.size()) continue;
            for (final Path path : paths) {
                existential[newAddressesCache.get(path.getFactAddressInCurrentlyLowestNode()).index] = true;
            }
            pathFilterToCounterColumn.putFilterElementToCounterColumn(pathFilter, new CounterColumn(index++));
        }
        return new MemoryHandlerMainAndCounterColumnMatcher(new MemoryHandlerMainWithExistentials(templArray,
                Counter.newCounter(filterSet, pathFilterToCounterColumn), addrArray, existential),
                pathFilterToCounterColumn);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInBeta(
            final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        return ((org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp) token).newBetaTemp(this, originIncomingEdge, filter);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInAlpha(
            final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        return ((org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp) token)
                .newAlphaTemp(this, originIncomingEdge, filter);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerPlusTemp newNewNodeToken() {
        final JamochaArray<Row> rows;
        try (final SafeReadQueue readQueue = this.getWithoutTimeoutReadableValidOutgoingPlusTokens()) {
            final int outgoingPlusRows = readQueue.stream().mapToInt(token -> token.validRows.size()).sum();
            this.lock.readLock().lock();
            rows = new JamochaArray<>(this.validRows, this.validRows.size() + outgoingPlusRows);
            this.lock.readLock().unlock();
            for (final MemoryHandlerPlusTemp plus : readQueue) {
                rows.addAll(plus.validRows);
            }
        }
        return MemoryHandlerPlusTemp.newNewNodeTemp(this.template, rows);
    }

    @Override
    public Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> newPlusToken(final Node otn,
            final org.jamocha.dn.memory.Fact... facts) {
        return MemoryHandlerPlusTemp.newRootTemp(this, otn, facts);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerMinusTemp newMinusToken(final MemoryFact... facts) {
        return MemoryHandlerMinusTemp.newRootTemp(this, facts);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerTerminal newMemoryHandlerTerminal() {
        return new MemoryHandlerTerminal(this);
    }

    private static AddressFilter[] emptyAddressFilterElementArray = new AddressFilter[0];

    @Override
    public AddressFilter[] getRelevantExistentialFilterParts(final AddressNodeFilterSet filter, final Edge edge) {
        assert this == edge.getSourceNode().getMemory();
        final Set<org.jamocha.dn.memory.FactAddress> positiveExistentialAddresses =
                filter.getPositiveExistentialAddresses();
        final Set<org.jamocha.dn.memory.FactAddress> negativeExistentialAddresses =
                filter.getNegativeExistentialAddresses();
        if (positiveExistentialAddresses.isEmpty() && negativeExistentialAddresses.isEmpty()) {
            return emptyAddressFilterElementArray;
        }
        final Set<org.jamocha.dn.memory.FactAddress> existentialAddresses = new HashSet<>();
        for (final FactAddress originAddress : this.addresses) {
            final org.jamocha.dn.memory.FactAddress localizedAddress = edge.localizeAddress(originAddress);
            if (positiveExistentialAddresses.contains(localizedAddress) || negativeExistentialAddresses
                    .contains(localizedAddress)) {
                existentialAddresses.add(localizedAddress);
            }
        }
        final Set<AddressFilter> filterElements = filter.getFilters();
        final ArrayList<AddressFilter> partList = new ArrayList<>(filterElements.size());
        filterElementLoop:
        for (final AddressFilter filterElement : filterElements) {
            final SlotInFactAddress[] addresses = filterElement.getAddressesInTarget();
            for (final SlotInFactAddress slotInFactAddress : addresses) {
                final org.jamocha.dn.memory.FactAddress factAddress = slotInFactAddress.getFactAddress();
                if (existentialAddresses.contains(factAddress)) {
                    partList.add(filterElement);
                    continue filterElementLoop;
                }
            }
        }
        return toArray(partList, AddressFilter[]::new);
    }

    /**
     * creates new row using the fact tuple given and default counter values.
     *
     * @param factTuple
     *         fact tuple to use
     * @return row containing fact tuple and default counter values
     */
    public Row newRow(final Fact[] factTuple) {
        assert factTuple.length == this.template.length;
        return new RowWithoutCounters(factTuple);
    }

    /**
     * creates new row for current template and default counter values.
     *
     * @return row containing empty fact tuple of current template and default counter values
     */
    public Row newRow() {
        return newRow(new Fact[this.template.length]);
    }
}
