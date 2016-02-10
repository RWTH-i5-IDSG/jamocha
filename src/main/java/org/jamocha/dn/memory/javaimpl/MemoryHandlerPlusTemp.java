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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.ToString;
import lombok.Value;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.LockWrapper.MultipleReadLockWrapper;
import org.jamocha.dn.memory.javaimpl.LockWrapper.ReadLockWrapper;
import org.jamocha.dn.memory.javaimpl.LockWrapper.WriteLockWrapper;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerMain.SafeReadQueue;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerMain.SafeWriteQueue;
import org.jamocha.dn.nodes.AddressPredecessor;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.filter.AddressNodeFilterSet.AddressMatchingConfiguration;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;

/**
 * Java-implementation of the {@link org.jamocha.dn.memory.MemoryHandlerPlusTemp} interface.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp
 */
@ToString(callSuper = true, of = "valid")
public class MemoryHandlerPlusTemp extends MemoryHandlerTemp implements org.jamocha.dn.memory.MemoryHandlerPlusTemp {

    private static MemoryFactory memoryFactory = (MemoryFactory) MemoryFactory.getMemoryFactory();

    private static MemoryHandlerPlusTemp empty =
            new MemoryHandlerPlusTemp(new Template[0], null, new JamochaArray<Row>(), 0, true);

    static class Semaphore {
        final AtomicInteger count;

        Semaphore(final int count) {
            super();
            this.count = new AtomicInteger(count);
        }

        public boolean release() {
            return this.count.decrementAndGet() != 0;
        }
    }

    final Semaphore lock;
    /**
     * set to false as soon as the memory has been committed to its main memory
     */
    boolean valid = true;
    Optional<JamochaArray<Row>> filtered = Optional.empty();

    JamochaArray<Row> getFiltered() {
        return this.filtered.orElse(this.validRows);
    }

    void setFiltered(final JamochaArray<Row> filteredRows) {
        this.filtered = Optional.of(filteredRows);
    }

    protected MemoryHandlerPlusTemp(final Template[] template, final MemoryHandlerMain originatingMainHandler,
            final JamochaArray<Row> rows, final int numChildren, final boolean omitSemaphore) {
        super(template, originatingMainHandler, rows);
        if (rows.isEmpty()) {
            this.lock = null;
            this.valid = false;
        } else if (omitSemaphore) {
            this.lock = null;
            if (null == this.originatingMainHandler) return;
            try (final SafeWriteQueue writeQueue = originatingMainHandler.getWriteableValidOutgoingPlusTokens()) {
                writeQueue.add(this);
                // commit and invalidate the token
                commitAndInvalidate();
            }
        } else {
            this.lock = new Semaphore(numChildren);
            try (final SafeWriteQueue writeQueue = originatingMainHandler.getWriteableValidOutgoingPlusTokens()) {
                writeQueue.add(this);
            }
        }
    }

    protected MemoryHandlerPlusTemp(final MemoryHandlerMain originatingMainHandler, final JamochaArray<Row> rows,
            final int numChildren, final boolean omitSemaphore) {
        this(originatingMainHandler.template, originatingMainHandler, rows, numChildren, omitSemaphore);
    }

    @Override
    public final void releaseLock() {
        if (null == this.lock || this.lock.release()) return;
        // all children have processed the temp memory, now we have to write its
        // content to main memory
        commitAndInvalidate();
    }

    protected final void commitAndInvalidate() {
        try (final WriteLockWrapper wlw = new WriteLockWrapper(this.originatingMainHandler.lock)) {
            try (final SafeWriteQueue writeQueue = this.originatingMainHandler.getWriteableValidOutgoingPlusTokens()) {
                assert this == writeQueue.peek();
                writeQueue.remove();

                final JamochaArray<Row> rows = this.getFiltered();
                this.valid = false;
                // skip further code if no rows to add
                if (rows.isEmpty()) {
                    return;
                }
                // add new filtered rows to main valid rows
                this.originatingMainHandler.validRows.addAll(rows);
            }
        }
    }

    @Override
    public void enqueueInEdge(final Edge edge) {
        edge.enqueueMemory(this);
    }

    /* * * * * * * * * STATIC FACTORY PART * * * * * * * * */

    protected static boolean canOmitSemaphore(final Edge originEdge) {
        return canOmitSemaphore(originEdge.getTargetNode());
    }

    protected static boolean canOmitSemaphore(final Node targetNode) {
        if (targetNode.getOutgoingEdges().isEmpty()) return true;
        for (final Edge edge : targetNode.getOutgoingEdges()) {
            if (edge.targetsBeta()) {
                return false;
            }
        }
        return true;
    }

    public static MemoryHandlerPlusTemp newNewNodeTemp(final Template[] template, final JamochaArray<Row> rows) {
        return new MemoryHandlerPlusTemp(template, null, rows, 0, true);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler,
            final Edge originIncomingEdge, final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        // create follow-up-temp
        final org.jamocha.dn.memory.MemoryHandlerTemp token =
                newRegularBetaTemp(originatingMainHandler, originIncomingEdge, filter, this);
        // push old temp into incoming edge to augment the memory seen by its target
        originIncomingEdge.getTempMemories().add(this);
        // return new temp
        return token;
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(
            final MemoryHandlerMainWithExistentials originatingMainHandler, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        // create follow-up-temp
        final org.jamocha.dn.memory.MemoryHandlerTemp token =
                newExistentialBetaTemp(originatingMainHandler, filter, this, originIncomingEdge);
        // push old temp into incoming edge to augment the memory seen by its target
        originIncomingEdge.getTempMemories().add(this);
        // return new temp
        return token;
    }

    static MemoryHandlerPlusTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
            final MemoryHandlerPlusTemp token, final Edge originIncomingEdge, final AddressNodeFilterSet filter)
            throws CouldNotAcquireLockException {
        final JamochaArray<Row> factList = new JamochaArray<>(1);
        factLoop:
        for (final Row row : token.validRows) {
            assert row.getFactTuple().length == 1;
            for (final AddressFilter filterElement : filter.getFilters()) {
                if (!applyFilterElement(row.getFactTuple()[0], filterElement)) {
                    continue factLoop;
                }
            }
            factList.add(row);
        }
        if (factList.isEmpty()) {
            return empty;
        }
        return new MemoryHandlerPlusTemp(originatingMainHandler, factList,
                originIncomingEdge.getTargetNode().getNumberOfOutgoingEdges(), canOmitSemaphore(originIncomingEdge));
    }

    @Override
    public MemoryHandlerPlusTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
            final Edge originIncomingEdge, final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        return newAlphaTemp(originatingMainHandler, this, originIncomingEdge, filter);
    }

    static Pair<MemoryHandlerPlusTemp, MemoryFact[]> newRootTemp(final MemoryHandlerMain originatingMainHandler,
            final Node otn, final org.jamocha.dn.memory.Fact... facts) {
        final JamochaArray<Row> factList = new JamochaArray<>(facts.length);
        final Fact[] memoryFacts = new Fact[facts.length];
        try (final SafeReadQueue readQueue = originatingMainHandler
                .getWithoutTimeoutReadableValidOutgoingPlusTokens()) {
            factLoop:
            for (int i = 0; i < facts.length; i++) {
                final org.jamocha.dn.memory.Fact fact = facts[i];
                assert fact.getTemplate() == otn.getMemory().getTemplate()[0];
                final Fact memoryFact =
                        new Fact((org.jamocha.dn.memory.javaimpl.Template) fact.getTemplate(), fact.getSlotValues(),
                                memoryFactory.getNextFactIdentifier());
                // spot internal duplicates
                for (int j = 0; j < i; ++j) {
                    if (null != memoryFacts[j] && Fact.equalContent(memoryFact, memoryFacts[j])) {
                        continue factLoop;
                    }
                }
                // spot duplicates wrt main memory
                for (final Row mainRow : originatingMainHandler.validRows) {
                    final Fact mainFact = mainRow.getFactTuple()[0];
                    if (Fact.equalContent(mainFact, memoryFact)) {
                        continue factLoop;
                    }
                }
                // spot duplicates wrt outgoing plus tokens
                for (final MemoryHandlerPlusTemp temp : readQueue) {
                    for (final Row mainRow : temp.getFiltered()) {
                        final Fact mainFact = mainRow.getFactTuple()[0];
                        if (Fact.equalContent(mainFact, memoryFact)) {
                            continue factLoop;
                        }
                    }
                }
                memoryFacts[i] = memoryFact;
                factList.add(originatingMainHandler.newRow(new Fact[]{memoryFact}));
            }
        }
        return Pair.of(new MemoryHandlerPlusTemp(originatingMainHandler, factList, otn.getNumberOfOutgoingEdges(),
                canOmitSemaphore(otn)), memoryFacts);
    }

    abstract static class StackElement {
        int rowIndex, memIndex;
        JamochaArray<JamochaArray<Row>> memStack;
        final int offset;

        private StackElement(final JamochaArray<JamochaArray<Row>> memStack, final int offset) {
            this.memStack = memStack;
            this.offset = offset;
        }

        public static StackElement ordinaryInput(final Edge edge, final int offset) {
            final LinkedList<? extends MemoryHandler> temps = edge.getTempMemories();
            final JamochaArray<JamochaArray<Row>> memStack = new JamochaArray<JamochaArray<Row>>(temps.size() + 1);
            memStack.add(
                    ((org.jamocha.dn.memory.javaimpl.MemoryHandlerMain) edge.getSourceNode().getMemory()).validRows);
            for (final Iterator<? extends MemoryHandler> iter = temps.iterator(); iter.hasNext(); ) {
                final MemoryHandlerPlusTemp temp = (MemoryHandlerPlusTemp) iter.next();
                if (!temp.valid) {
                    iter.remove();
                    continue;
                }
                memStack.add(temp.validRows);
            }
            return new StackElement(memStack, offset) {
                @Override
                Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
                    final FactAddress factAddress = (FactAddress) addr.getAddress();
                    final Fact[] factTuple = this.getRow().getFactTuple();
                    return factTuple[factAddress.index].getValue(slot);
                }
            };
        }

        public static StackElement originInput(final int columns, final Edge originEdge,
                final JamochaArray<Row> tokenRows, final int offset) {
            final JamochaArray<Row> listWithHoles = new JamochaArray<>(tokenRows.size());
            for (final Row row : tokenRows) {
                final Row wideRow = ((MemoryHandlerMain) originEdge.getTargetNode().getMemory()).newRow();
                assert columns >= offset + row.getFactTuple().length;
                wideRow.copy(offset, row);
                listWithHoles.add(wideRow);
            }
            final JamochaArray<JamochaArray<Row>> memStack = new JamochaArray<JamochaArray<Row>>(1);
            memStack.add(listWithHoles);
            return new StackElement(memStack, offset) {
                @Override
                Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
                    final FactAddress factAddress = (FactAddress) addr.getEdge().localizeAddress(addr.getAddress());
                    final Fact[] factTuple = this.getRow().getFactTuple();
                    return factTuple[factAddress.index].getValue(slot);
                }
            };
        }

        JamochaArray<Row> getTable() {
            return this.memStack.get(this.memIndex);
        }

        Row getRow() {
            return this.getTable().get(this.rowIndex);
        }

        abstract Object getValue(final AddressPredecessor addr, final SlotAddress slot);

        boolean checkMemBounds() {
            return this.memStack.size() > this.memIndex && this.memIndex >= 0;
        }

        boolean checkRowBounds() {
            return checkMemBounds() && getTable().size() > this.rowIndex && this.rowIndex >= 0;
        }

        void resetIndices() {
            this.rowIndex = 0;
            this.memIndex = 0;
        }

        int getOffset() {
            return this.offset;
        }

        public void prepareMatching(final AddressMatchingConfiguration matchingConfiguration) {
            assert 0 == this.rowIndex && 0 == this.memIndex;
            this.memStack = this.memStack.stream().map(memory -> memory.stream().flatMap(
                    row -> MatchingProcessor.processMatching(row, matchingConfiguration)
                            .stream()).<JamochaArray<Row>>collect(JamochaArray::new, JamochaArray::add,
                    JamochaArray::addAll)).collect(JamochaArray::new, JamochaArray::add, JamochaArray::addAll);
        }
    }

    @FunctionalInterface
    interface FunctionPointer {
        void apply(final JamochaArray<Row> tr, final StackElement originElement);
    }

    private static JamochaArray<Row> loop(final FunctionPointer functionPointer, final List<StackElement> stack) {
        if (stack.isEmpty()) {
            return new JamochaArray<>();
        }
        final StackElement originElement = stack.get(0);
        final JamochaArray<Row> tr = new JamochaArray<>();
        {
            final Iterator<StackElement> iter = stack.iterator();
            // initialize all memory indices to valid values
            while (iter.hasNext()) {
                final StackElement element = iter.next();
                while (!element.checkRowBounds()) {
                    if (!element.checkMemBounds()) {
                        // one of the elements doesn't hold any facts, the join will be empty
                        // reset all indices in the StackElements
                        for (final StackElement elem : stack) {
                            elem.resetIndices();
                        }
                        return tr;
                    }
                    element.memIndex++;
                    // we could break here, since there can be no empty temp memories
                }
            }
        }
        outerloop:
        while (true) {
            innerloop:
            while (true) {
                functionPointer.apply(tr, originElement);
                // increment row indices
                for (final Iterator<StackElement> iter = stack.iterator(); iter.hasNext(); ) {
                    final StackElement element = iter.next();
                    element.rowIndex++;
                    if (element.checkRowBounds()) break;
                    element.rowIndex = 0;
                    if (!iter.hasNext()) break innerloop;
                }
            }
            // increment memory indices
            for (final Iterator<StackElement> iter = stack.iterator(); iter.hasNext(); ) {
                final StackElement element = iter.next();
                element.memIndex++;
                if (element.checkMemBounds()) break;
                element.memIndex = 0;
                if (!iter.hasNext()) break outerloop;
            }
        }
        // reset all indices in the StackElements
        for (final StackElement elem : stack) {
            elem.resetIndices();
        }
        return tr;
    }

    private static LinkedHashMap<Edge, StackElement> getStack(final JamochaArray<Row> tokenRows,
            final Edge originIncomingEdge) {
        // get a fixed-size array of indices (size: #inputs of the node),
        // determine number of inputs for the current join as maxIndex
        // loop through the inputs line-wise using array[0] .. array[maxIndex]
        // as line indices, incrementing array[maxIndex] and propagating the
        // increment to lower indices when input-size is reached

        // set locks and create stack
        final Node targetNode = originIncomingEdge.getTargetNode();
        final Edge[] nodeIncomingEdges = targetNode.getIncomingEdges();
        final LinkedHashMap<Edge, StackElement> edgeToStack = new LinkedHashMap<>();
        final int columns = targetNode.getMemory().getTemplate().length;
        final StackElement originElement;
        {
            StackElement tempOriginElement = null;
            int offset = 0;
            for (final Edge incomingEdge : nodeIncomingEdges) {
                if (incomingEdge == originIncomingEdge) {
                    tempOriginElement = StackElement.originInput(columns, originIncomingEdge, tokenRows, offset);
                    offset += incomingEdge.getSourceNode().getMemory().getTemplate().length;
                    continue;
                }
                edgeToStack.put(incomingEdge, StackElement.ordinaryInput(incomingEdge, offset));
                offset += incomingEdge.getSourceNode().getMemory().getTemplate().length;
            }
            originElement = tempOriginElement;
        }
        edgeToStack.put(originIncomingEdge, originElement);
        return edgeToStack;
    }

    private static MemoryHandlerTemp newRegularBetaTemp(final MemoryHandlerMain originatingMainHandler,
            final Edge originEdge, final AddressNodeFilterSet filter, final MemoryHandlerPlusTemp token)
            throws CouldNotAcquireLockException {
        final JamochaArray<Row> facts = regularLockJoinAndUnlock(filter, token, originEdge);
        final int numChildren = originEdge.getTargetNode().getNumberOfOutgoingEdges();
        return new MemoryHandlerPlusTemp(originatingMainHandler, facts, numChildren, canOmitSemaphore(originEdge));
    }

    private static JamochaArray<Row> regularLockJoinAndUnlock(final AddressNodeFilterSet filter,
            final MemoryHandlerPlusTemp token, final Edge originEdge) throws CouldNotAcquireLockException {
        try (final MultipleReadLockWrapper mrlw = new MultipleReadLockWrapper(originEdge)) {
            final LinkedHashMap<Edge, StackElement> edgeToStack = getStack(token.validRows, originEdge);

            prepareMatchings(filter, edgeToStack, originEdge);
            performJoin(filter, edgeToStack, originEdge);

            final JamochaArray<Row> facts = edgeToStack.get(originEdge).getTable();
            return facts;
        }
    }

    private static void prepareMatchings(final AddressNodeFilterSet filter,
            final LinkedHashMap<Edge, StackElement> edgeToStack, final Edge originEdge) {
        final Node targetNode = originEdge.getTargetNode();
        for (final AddressMatchingConfiguration matchingConfiguration : filter.getMatchingConfigurations()) {
            final org.jamocha.dn.memory.FactAddress factAddress = matchingConfiguration.getFactAddress();
            final Edge edge = targetNode.delocalizeAddress(factAddress).getEdge();
            final StackElement stackElement = edgeToStack.get(edge);
            stackElement.prepareMatching(matchingConfiguration);
        }
    }

    static JamochaArray<Row> validPart(final Counter counter, final JamochaArray<Row> allRows) {
        final LazyListCopy<Row> copy = new LazyListCopy<>(allRows);
        for (int index = 0; index < allRows.size(); index++) {
            final Row row = allRows.get(index);
            if (!counter.isValid(row)) {
                copy.drop(index);
            }
        }
        return copy.getList();
    }

    private static org.jamocha.dn.memory.MemoryHandlerTemp newExistentialBetaTemp(
            final MemoryHandlerMainWithExistentials originatingMainHandler, final AddressNodeFilterSet filter,
            final MemoryHandlerPlusTemp token, final Edge originEdge) throws CouldNotAcquireLockException {
        // existential join (counter updates) or regular join (new rows)?
        final boolean existential = originEdge.getFilterPartsForCounterColumns().length != 0;
        if (!existential) {
            final JamochaArray<Row> newUnfilteredRows = regularLockJoinAndUnlock(filter, token, originEdge);
            // push new rows into main.allRows
            // no need for a lock as the current node is the only one reading/writing allRows
            // FIXME think about locking originatingMainHandler.allRows - really unnecessary ?
            for (final Row newUnfilteredRow : newUnfilteredRows) {
                originatingMainHandler.allRows.add(newUnfilteredRow);
            }
            // get valid part of the new rows
            final JamochaArray<Row> newValidRows = validPart(originatingMainHandler.counter, newUnfilteredRows);
            if (newValidRows.isEmpty()) {
                return empty;
            }
            final int numChildren = originEdge.getTargetNode().getNumberOfOutgoingEdges();
            return new MemoryHandlerPlusTemp(originatingMainHandler, newValidRows, numChildren,
                    canOmitSemaphore(originEdge));

        }
        return handleExistentialEdge(originatingMainHandler, filter, token.validRows, originEdge,
                CounterUpdater.INCREMENTER);
    }

    /*
     * Assumption: every existentially quantified path/address is only used in a single filter
     * element.
     */
    private static void performJoin(final AddressNodeFilterSet nodeFilterSet,
            final LinkedHashMap<Edge, StackElement> edgeToStack, final Edge originEdge) {
        final Node targetNode = originEdge.getTargetNode();
        final StackElement originElement = edgeToStack.get(originEdge);

        final Counter counter = ((MemoryHandlerMain) originEdge.getTargetNode().getMemory()).counter;

        for (final AddressFilter filter : nodeFilterSet.getFilters()) {
            final SlotInFactAddress[] addresses = filter.getAddressesInTarget();
            final List<StackElement> stack = new ArrayList<>(addresses.length);
            final PredicateWithArguments<ParameterLeaf> predicate = filter.getFunction();
            final CounterColumn counterColumn = (CounterColumn) filter.getCounterColumn();
            final boolean existential = (counterColumn != null);

            // determine new edges
            final Set<Edge> newEdges = new HashSet<>();
            stack.add(originElement);
            for (final SlotInFactAddress address : addresses) {
                final Edge edge = targetNode.delocalizeAddress(address.getFactAddress()).getEdge();
                final StackElement element = edgeToStack.get(edge);
                if (element != originElement) {
                    // (!a || b) <=> (a -> b)
                    if (!existential || edge.getSourceNode().getOutgoingExistentialEdges().contains(edge)) {
                        if (newEdges.add(edge)) {
                            stack.add(element);
                        }
                    } else {
                        // full join regular edges if FilterElement is existential
                        fullJoin(edgeToStack, originElement, edge, element);
                    }
                }
            }

            // if existential, perform slightly different join not copying but only changing
            // counters.

            if (existential) {
                loop(new FunctionPointer() {
                    @Override
                    public void apply(final JamochaArray<Row> tr, final StackElement originElement) {
                        final int paramLength = predicate.getParamTypes().length;
                        final Object[] params = new Object[paramLength];
                        // determine parameters
                        for (int i = 0; i < paramLength; ++i) {
                            final SlotInFactAddress slotInTargetAddress = addresses[i];
                            final org.jamocha.dn.memory.FactAddress targetAddress =
                                    slotInTargetAddress.getFactAddress();
                            final AddressPredecessor upwardsAddress = targetNode.delocalizeAddress(targetAddress);
                            final StackElement se = edgeToStack.get(upwardsAddress.getEdge());
                            params[i] = se.getValue(upwardsAddress, slotInTargetAddress.getSlotAddress());
                        }
                        // increment counter if facts match predicate
                        if (predicate.evaluate(params)) {
                            final Row row = originElement.getRow();
                            // use counter to set counterColumn to 1 if counterColumn is not null
                            counter.increment(row, counterColumn, 1);
                        }
                    }
                }, stack);
            } else {
                // replace TR in originElement with new temporary result
                originElement.memStack.set(0, loop(new FunctionPointer() {
                    @Override
                    public void apply(final JamochaArray<Row> tr, final StackElement originElement) {
                        final int paramLength = predicate.getParamTypes().length;
                        final Object[] params = new Object[paramLength];
                        // determine parameters
                        for (int i = 0; i < paramLength; ++i) {
                            final SlotInFactAddress slotInTargetAddress = addresses[i];
                            final org.jamocha.dn.memory.FactAddress targetAddress =
                                    slotInTargetAddress.getFactAddress();
                            final AddressPredecessor upwardsAddress = targetNode.delocalizeAddress(targetAddress);
                            final StackElement se = edgeToStack.get(upwardsAddress.getEdge());
                            params[i] = se.getValue(upwardsAddress, slotInTargetAddress.getSlotAddress());
                        }
                        // copy result to new TR if facts match predicate
                        if (predicate.evaluate(params)) {
                            // copy current row from old TR
                            final Row row = originElement.getRow().copy();
                            // insert information from new inputs
                            for (final Edge edge : newEdges) {
                                // source is some temp, destination new TR
                                final StackElement se = edgeToStack.get(edge);
                                row.copy(se.getOffset(), se.getRow());
                            }
                            // copy the result to new TR
                            tr.add(row);
                        }
                    }
                }, stack));
            }

            // point all inputs that were joint during this turn to the TR StackElement
            for (final Edge incomingEdge : newEdges) {
                edgeToStack.put(incomingEdge, originElement);
            }
            if (!originElement.checkRowBounds()) {
                return;
            }
        }

        // full join with all inputs not pointing to TR now
        for (final Map.Entry<Edge, StackElement> entry : edgeToStack.entrySet()) {
            final StackElement se = entry.getValue();
            if (se == originElement) continue;
            final Edge nodeInput = entry.getKey();
            fullJoin(edgeToStack, originElement, nodeInput, se);
        }
    }

    private static void fullJoin(final LinkedHashMap<Edge, StackElement> edgeToStack, final StackElement originElement,
            final Edge edge, final StackElement se) {
        // replace TR in originElement with new temporary result
        originElement.memStack.set(0, loop(new FunctionPointer() {
            @Override
            public void apply(final JamochaArray<Row> tr, final StackElement originElement) {
                // copy result to new TR
                // copy current row from old TR
                // insert information from new input
                // source is some temp, destination new TR
                // copy the result to new TR
                tr.add(originElement.getRow().copy().copy(se.getOffset(), se.getRow()));
            }
        }, Arrays.asList(originElement, se)));
        // point all inputs that were joint during this turn to the TR StackElement
        edgeToStack.put(edge, originElement);
    }

    @Value
    static class FactAddressPartition {
        FactAddress[] regular;
        Set<FactAddress> existential;
    }

    private static FactAddressPartition partitionFactAddresses(final AddressNodeFilterSet filter,
            final Edge originEdge) {
        final FactAddress[] originAddresses = ((MemoryHandlerMain) originEdge.getSourceNode().getMemory()).addresses;
        final Set<org.jamocha.dn.memory.FactAddress> filterNegativeExistentialAddresses =
                filter.getNegativeExistentialAddresses();
        final Set<org.jamocha.dn.memory.FactAddress> filterPositiveExistentialAddresses =
                filter.getPositiveExistentialAddresses();
        final JamochaArray<FactAddress> regular = new JamochaArray<>(originAddresses.length);
        final Set<FactAddress> existential = new HashSet<>(originAddresses.length);
        for (final FactAddress sourceFactAddress : originAddresses) {
            final FactAddress targetFactAddress = (FactAddress) originEdge.localizeAddress(sourceFactAddress);
            if (!filterNegativeExistentialAddresses.contains(targetFactAddress) && !filterPositiveExistentialAddresses
                    .contains(targetFactAddress)) {
                regular.add(targetFactAddress);
            } else {
                existential.add(targetFactAddress);
            }
        }
        return new FactAddressPartition(regular.toArray(FactAddress[]::new), existential);
    }

    static org.jamocha.dn.memory.MemoryHandlerTemp handleExistentialEdge(
            final MemoryHandlerMainWithExistentials originatingMainHandler, final AddressNodeFilterSet filter,
            final JamochaArray<Row> tokenRows, final Edge originEdge, final CounterUpdater counterUpdater)
            throws CouldNotAcquireLockException {

        final JamochaArray<CounterUpdate> counterUpdates;
        // read-lock target main for existential join
        try (final ReadLockWrapper rlw = ReadLockWrapper.withTimeout(originatingMainHandler.lock)) {
            // read-lock all other input-mains
            try (final MultipleReadLockWrapper mrlw = new MultipleReadLockWrapper(originEdge)) {
                // get stack
                final LinkedHashMap<Edge, StackElement> edgeToStack =
                        MemoryHandlerPlusTemp.getStack(tokenRows, originEdge);

                prepareMatchings(filter, edgeToStack, originEdge);

                // perform the actual join to get the counter updates
                counterUpdates =
                        performExistentialJoin(filter, edgeToStack.get(originEdge), originEdge, counterUpdater);
            }
        }

        if (counterUpdates.isEmpty()) return empty;

        // apply the counter updates and generate the actual temps
        try (final WriteLockWrapper wlw = new WriteLockWrapper(originatingMainHandler.lock)) {
            final JamochaArray<Row> rowsToAdd = new JamochaArray<>();
            final JamochaArray<Row> rowsToDel = new JamochaArray<>();
            final Counter counter = originatingMainHandler.counter;
            for (final CounterUpdate counterUpdate : counterUpdates) {
                final Row row = counterUpdate.row;
                final boolean wasValid = counter.isValid(row);
                counterUpdate.apply();
                final boolean isValid = counter.isValid(row);
                if (!wasValid && isValid) {
                    // changed to valid
                    rowsToAdd.add(counterUpdate.row);
                } else if (wasValid && !isValid) {
                    // changed to invalid
                    rowsToDel.add(counterUpdate.row);
                }
                // else: no change
            }
            final boolean noLinesToAdd = rowsToAdd.isEmpty();
            final boolean noLinesToDel = rowsToDel.isEmpty();
            if (noLinesToAdd && noLinesToDel) {
                // no change
                return empty;
            }
            if (noLinesToAdd) {
                // create -token for invalidated rows (deleting them while at it)
                return MemoryHandlerMinusTemp
                        .newExistentialBetaFromRowsToDelete(originatingMainHandler, rowsToDel, originEdge);
            }
            final int numChildren = originEdge.getTargetNode().getNumberOfOutgoingEdges();
            if (noLinesToDel) {
                // create + token for validated rows
                return new MemoryHandlerPlusTemp(originatingMainHandler, rowsToAdd, numChildren,
                        canOmitSemaphore(originEdge));
            }
            // create both tokens as above and wrap them
            return new MemoryHandlerTempPairDistributer(
                    new MemoryHandlerPlusTemp(originatingMainHandler, rowsToAdd, numChildren,
                            canOmitSemaphore(originEdge)), MemoryHandlerMinusTemp
                    .newExistentialBetaFromRowsToDelete(originatingMainHandler, rowsToDel, originEdge));
        }
    }

    @FunctionalInterface
    interface CounterUpdater {
        void apply(final CounterUpdate counterUpdate, final CounterColumn counterColumn);

        CounterUpdater INCREMENTER =
                (final CounterUpdate counterUpdate, final CounterColumn counterColumn) -> counterUpdate
                        .increment(counterColumn, 1);

        CounterUpdater DECREMENTER =
                (final CounterUpdate counterUpdate, final CounterColumn counterColumn) -> counterUpdate
                        .increment(counterColumn, -1);
    }

    static JamochaArray<CounterUpdate> performExistentialJoin(final AddressNodeFilterSet filterSet,
            final StackElement originElement, final Edge originEdge, final CounterUpdater counterUpdater) {
        final AddressFilter[] filterPartsForCounterColumns = originEdge.getFilterPartsForCounterColumns();
        // if there are existential facts in the token, perform a join with the main memory
        final JamochaArray<CounterUpdate> counterUpdates = new JamochaArray<>();
        final FactAddressPartition partition = partitionFactAddresses(filterSet, originEdge);
        final MemoryHandlerMainWithExistentials memoryHandlerMain =
                (MemoryHandlerMainWithExistentials) originEdge.getTargetNode().getMemory();
        final JamochaArray<Row> mainRows = memoryHandlerMain.allRows;
        final JamochaArray<Row> tokenRows = originElement.getTable();
        final int mainSize = mainRows.size();
        final int tokenSize = tokenRows.size();
        // TODO adjust to be able to handle multiple existential edges
        for (int mainIndex = 0; mainIndex < mainSize; ++mainIndex) {
            final Row mainRow = mainRows.get(mainIndex);
            final Fact[] mainFactTuple = mainRow.getFactTuple();
            CounterUpdate currentCounterUpdate = null;
            tokenloop:
            for (int tokenIndex = 0; tokenIndex < tokenSize; ++tokenIndex) {
                final Row tokenRow = tokenRows.get(tokenIndex);
                final Fact[] tokenFactTuple = tokenRow.getFactTuple();
                // check whether the existential part fulfill the filter conditions
                for (final AddressFilter filter : filterPartsForCounterColumns) {
                    final PredicateWithArguments<ParameterLeaf> predicate = filter.getFunction();
                    final SlotInFactAddress[] addresses = filter.getAddressesInTarget();
                    final CounterColumn counterColumn = (CounterColumn) filter.getCounterColumn();
                    final int paramLength = predicate.getParamTypes().length;
                    final Object[] params = new Object[paramLength];
                    // determine parameters using facts in the token where possible
                    for (int i = 0; i < paramLength; ++i) {
                        final SlotInFactAddress address = addresses[i];
                        final Fact[] factBase;
                        if (partition.existential.contains(address.getFactAddress())) {
                            factBase = tokenFactTuple;
                        } else {
                            factBase = mainFactTuple;
                        }
                        params[i] = factBase[((FactAddress) address.getFactAddress()).index]
                                .getValue(address.getSlotAddress());
                    }
                    // if combined row doesn't match, try the next token row
                    if (!predicate.evaluate(params)) {
                        continue tokenloop;
                    }
                    // token row matches main row, we can increment the counter
                    if (null == currentCounterUpdate) {
                        currentCounterUpdate = new CounterUpdate(mainRow);
                        counterUpdates.add(currentCounterUpdate);
                    }
                    counterUpdater.apply(currentCounterUpdate, counterColumn);
                }
            }
        }
        originElement.memStack.set(0, tokenRows);
        return counterUpdates;
    }
}
