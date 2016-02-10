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

import java.util.function.BiFunction;
import java.util.function.Consumer;

import lombok.ToString;

import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.LockWrapper.WriteLockWrapper;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerMain.SafeWriteQueue;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp.CounterUpdater;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.AddressNodeFilterSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@ToString(callSuper = true)
@SuppressWarnings("checkstyle:finalclass")
public class MemoryHandlerMinusTemp extends MemoryHandlerTemp implements org.jamocha.dn.memory.MemoryHandlerMinusTemp {

    private static MemoryHandlerMinusTempComplete empty =
            new MemoryHandlerMinusTempComplete(null, null, new JamochaArray<Row>(0), new JamochaArray<Row>(0),
                    new FactAddress[]{});

    private static Consumer<Row> nullConsumer = (final Row row) -> {
    };

    /**
     * Maps FactAddresses valid in the current scope of the token by position of the facts in the temp memory
     */
    final FactAddress[] factAddresses;

    static MemoryHandlerMinusTemp newRootTemp(final MemoryHandlerMain memoryHandlerMain, final MemoryFact[] facts) {
        final JamochaArray<Row> minusFacts = new JamochaArray<>(facts.length);
        for (final MemoryFact fact : facts) {
            minusFacts.add(memoryHandlerMain.newRow(new Fact[]{(Fact) fact}));
        }
        final FactAddress[] factAddresses = memoryHandlerMain.addresses;
        assert factAddresses.length == 1;
        final JamochaArray<Row> relevantFactTuples =
                getRelevantFactTuples(memoryHandlerMain, MemoryHandlerMinusTemp::filterTargetMain, minusFacts,
                        factAddresses, EqualityChecker.ALPHA, nullConsumer);
        if (0 == relevantFactTuples.size()) {
            return MemoryHandlerMinusTemp.empty;
        }
        return new MemoryHandlerMinusTempComplete(memoryHandlerMain.getTemplate(), memoryHandlerMain,
                relevantFactTuples, relevantFactTuples, factAddresses);
    }

    static JamochaArray<Row> getRemainingFactTuples(final JamochaArray<Row> originalFacts,
            final JamochaArray<Row> minusFacts, final FactAddress[] factAddresses, final boolean[] marked,
            final EqualityChecker equalityChecker) {
        return getRemainingFactTuples(originalFacts, nullConsumer, minusFacts, factAddresses, marked, equalityChecker);
    }

    private static JamochaArray<Row> getRemainingFactTuples(final JamochaArray<Row> originalFacts,
            final Consumer<Row> deletedRowConsumer, final JamochaArray<Row> minusFacts,
            final FactAddress[] factAddresses, final boolean[] marked, final EqualityChecker equalityChecker) {
        final int originalFactsSize = originalFacts.size();
        final int minusFactsSize = minusFacts.size();
        final LazyListCopy<Row> remainingFacts = new LazyListCopy<>(originalFacts);
        outerLoop:
        for (int originalFactsIndex = 0; originalFactsIndex < originalFactsSize; ++originalFactsIndex) {
            final Row originalRow = originalFacts.get(originalFactsIndex);
            for (int minusFactsIndex = 0; minusFactsIndex < minusFactsSize; ++minusFactsIndex) {
                final Row minusRow = minusFacts.get(minusFactsIndex);
                if (equalityChecker.equals(originalRow, minusRow, minusFacts, minusFactsIndex, factAddresses)) {
                    // we spotted a match for a complete row in the minus token
                    remainingFacts.drop(originalFactsIndex);
                    // consume
                    deletedRowConsumer.accept(originalRow);
                    // mark the responsible row in the minus token as relevant for the successor
                    // network
                    marked[minusFactsIndex] = true;
                    // don't reconsider the same line in the original facts again
                    continue outerLoop;
                }
            }
            // facts differ at some point, keep in remaining facts
        }
        return remainingFacts.getList();
    }

    static MemoryHandlerMinusTemp newExistentialBetaFromRowsToDelete(
            final MemoryHandlerMainWithExistentials originatingMainHandler, final JamochaArray<Row> rowsToDelete,
            final Edge originIncomingEdge) {
        return newRegularMinusTemp(originatingMainHandler, MemoryHandlerMinusTemp::filterTargetMain, originIncomingEdge,
                rowsToDelete, EqualityChecker.EQUAL_ROW, originatingMainHandler.addresses, (edge, address) -> address,
                originatingMainHandler.template);
    }

    @SuppressWarnings("checkstyle:parameternumber")
    private static <T extends MemoryHandlerMain> MemoryHandlerMinusTemp newRegularMinusTemp(
            final T originatingMainHandler, final MainMemoryFilter<T> mainMemoryFilter, final Edge originIncomingEdge,
            final JamochaArray<Row> rowsToDelete, final EqualityChecker equalityChecker,
            final FactAddress[] factAddresses, final BiFunction<Edge, FactAddress, FactAddress> addressLocalizer,
            final Template[] template) {
        final boolean createComplete = !originIncomingEdge.getTargetNode().getOutgoingExistentialEdges().isEmpty();
        final JamochaArray<Row> completeDeletedRows;
        final Consumer<Row> completeDeletedRowsAdder;
        if (createComplete) {
            // some of the target node's paths are existential, we need to pass a complete
            // token, not only the partial version
            completeDeletedRows = new JamochaArray<>();
            completeDeletedRowsAdder = completeDeletedRows::add;
        } else {
            completeDeletedRows = null;
            completeDeletedRowsAdder = nullConsumer;
        }
        final FactAddress[] localizedAddressMap =
                localizeAddressMap(factAddresses, originIncomingEdge, addressLocalizer);
        final JamochaArray<Row> relevantMinusFacts =
                getRelevantFactTuples(originatingMainHandler, mainMemoryFilter, rowsToDelete, localizedAddressMap,
                        equalityChecker, completeDeletedRowsAdder);
        if (0 == relevantMinusFacts.size()) {
            return MemoryHandlerMinusTemp.empty;
        }
        if (createComplete) {
            return new MemoryHandlerMinusTempComplete(template, originatingMainHandler, relevantMinusFacts,
                    completeDeletedRows, localizedAddressMap);
        }
        return new MemoryHandlerMinusTemp(template, originatingMainHandler, relevantMinusFacts, localizedAddressMap);
    }

    private <T extends MemoryHandlerMain> MemoryHandlerMinusTemp newRegularBeta(final T originatingMainHandler,
            final MainMemoryFilter<T> mainMemoryFilter, final Edge originIncomingEdge) {
        return newRegularMinusTemp(originatingMainHandler, mainMemoryFilter, originIncomingEdge, this.validRows,
                EqualityChecker.BETA, this.factAddresses, translateDownwards, this.template);
    }

    @Override
    public MemoryHandlerTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        return newRegularBeta(originatingMainHandler, MemoryHandlerMinusTemp::filterTargetMain, originIncomingEdge);
    }

    @Override
    public org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(
            final MemoryHandlerMainWithExistentials originatingMainHandler, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        if (!originIncomingEdge.getSourceNode().getOutgoingExistentialEdges().contains(originIncomingEdge)) {
            // if (originIncomingEdge.getFilterPartsForCounterColumns().length == 0) {
            // regular edge
            return newRegularBeta(originatingMainHandler, MemoryHandlerMinusTemp::filterTargetExistentialMain,
                    originIncomingEdge);
        }
        // existential edge
        final JamochaArray<Row> completeMinusRows = ((MemoryHandlerMinusTempComplete) this).completeRows;
        return MemoryHandlerPlusTemp
                .handleExistentialEdge(originatingMainHandler, filter, completeMinusRows, originIncomingEdge,
                        CounterUpdater.DECREMENTER);
    }

    private static void filterOutgoingTemps(final Iterable<MemoryHandlerPlusTemp> validOutgoingPlusTokens,
            final JamochaArray<Row> minusFacts, final FactAddress[] factAddresses, final boolean[] marked,
            final EqualityChecker equalityChecker) {
        for (final MemoryHandlerPlusTemp temp : validOutgoingPlusTokens) {
            final JamochaArray<Row> originalFacts = temp.getFiltered();
            final JamochaArray<Row> remainingFacts =
                    getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked, equalityChecker);
            temp.setFiltered(remainingFacts);
        }
    }

    private static void filterTargetMain(final MemoryHandlerMain targetMain, final JamochaArray<Row> minusFacts,
            final FactAddress[] factAddresses, final boolean[] marked, final EqualityChecker equalityChecker,
            final Consumer<Row> deletedRowConsumer) {
        final JamochaArray<Row> originalFacts = targetMain.validRows;
        final int originalFactsSize = originalFacts.size();
        final JamochaArray<Row> remainingFacts =
                getRemainingFactTuples(originalFacts, deletedRowConsumer, minusFacts, factAddresses, marked,
                        equalityChecker);
        if (remainingFacts.size() != originalFactsSize) {
            try (final WriteLockWrapper wlw = new WriteLockWrapper(targetMain.lock)) {
                targetMain.validRows = remainingFacts;
            }
        }
    }

    private static void filterTargetExistentialMain(final MemoryHandlerMainWithExistentials targetMain,
            final JamochaArray<Row> minusFacts, final FactAddress[] factAddresses, final boolean[] marked,
            final EqualityChecker equalityChecker, final Consumer<Row> deletedRowConsumer) {
        final JamochaArray<Row> validDeletedRows = new JamochaArray<>();
        targetMain.allRows = getRemainingFactTuples(targetMain.allRows, (final Row deletedRow) -> {
            if (targetMain.counter.isValid(deletedRow)) validDeletedRows.add(deletedRow);
        }, minusFacts, factAddresses, marked, equalityChecker);
        final JamochaArray<Row> originalFacts = targetMain.validRows;
        final int originalFactsSize = originalFacts.size();
        final JamochaArray<Row> remainingFacts =
                getRemainingFactTuples(originalFacts, deletedRowConsumer, validDeletedRows, factAddresses,
                        new boolean[validDeletedRows.size()], EqualityChecker.EQUAL_ROW);
        if (remainingFacts.size() != originalFactsSize) {
            try (final WriteLockWrapper wlw = new WriteLockWrapper(targetMain.lock)) {
                targetMain.validRows = remainingFacts;
            }
        }
    }

    private static JamochaArray<Row> getMarkedFactTuples(final JamochaArray<Row> minusFacts, final boolean[] marked) {
        final int minusFactsSize = minusFacts.size();
        int relevantMinusFactsSize = 0;
        for (final boolean mark : marked) {
            if (mark) {
                ++relevantMinusFactsSize;
            }
        }
        if (relevantMinusFactsSize == minusFactsSize) {
            return minusFacts;
        }
        final JamochaArray<Row> relevantMinusFacts = new JamochaArray<Row>(relevantMinusFactsSize);
        for (int minusFactsIndex = 0; minusFactsIndex < minusFactsSize; ++minusFactsIndex) {
            if (marked[minusFactsIndex]) {
                relevantMinusFacts.add(minusFacts.get(minusFactsIndex));
            }
        }
        return relevantMinusFacts;

    }

    static BiFunction<Edge, FactAddress, FactAddress> translateDownwards =
            (final Edge localizingEdge, final FactAddress factAddress) -> (FactAddress) localizingEdge
                    .localizeAddress(factAddress);

    private static FactAddress[] localizeAddressMap(final FactAddress[] old, final Edge localizingEdge,
            final BiFunction<Edge, FactAddress, FactAddress> addressLocalizer) {
        final int length = old.length;
        final FactAddress[] factAddresses = new FactAddress[length];
        final AddressNodeFilterSet filter = localizingEdge.getFilter();
        for (int i = 0; i < length; ++i) {
            final FactAddress oldAddress = old[i];
            if (null == oldAddress) continue;
            final FactAddress newAddress = addressLocalizer.apply(localizingEdge, oldAddress);
            factAddresses[i] = filter.isExistential(newAddress) ? null : newAddress;
        }
        return factAddresses;
    }

    @Override
    public MemoryHandlerTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler, final Edge originIncomingEdge,
            final AddressNodeFilterSet filter) throws CouldNotAcquireLockException {
        return newRegularMinusTemp(originatingMainHandler, MemoryHandlerMinusTemp::filterTargetMain, originIncomingEdge,
                validRows, EqualityChecker.ALPHA, factAddresses, translateDownwards, template);
    }

    @FunctionalInterface
    private interface MainMemoryFilter<T extends MemoryHandlerMain> {
        void apply(final T targetMain, final JamochaArray<Row> minusFacts, final FactAddress[] factAddresses,
                final boolean[] marked, final EqualityChecker equalityChecker, final Consumer<Row> deletedRowConsumer);
    }

    private static <T extends MemoryHandlerMain> JamochaArray<Row> getRelevantFactTuples(final T targetMain,
            final MainMemoryFilter<T> mainMemoryFilter, final JamochaArray<Row> minusFacts,
            final FactAddress[] factAddresses, final EqualityChecker equalityChecker,
            final Consumer<Row> deletedRowConsumer) {
        final boolean[] marked = new boolean[minusFacts.size()];
        mainMemoryFilter.apply(targetMain, minusFacts, factAddresses, marked, equalityChecker, deletedRowConsumer);
        try (final SafeWriteQueue writeQueue = targetMain.getWriteableValidOutgoingPlusTokens()) {
            filterOutgoingTemps(writeQueue, minusFacts, factAddresses, marked, equalityChecker);
        }
        return getMarkedFactTuples(minusFacts, marked);
    }

    private MemoryHandlerMinusTemp(final Template[] template, final MemoryHandlerMain originatingMainHandler,
            final JamochaArray<Row> rows, final FactAddress[] factAddresses) {
        super(template, originatingMainHandler, rows);
        this.factAddresses = factAddresses;
    }

    @Override
    public void enqueueInEdge(final Edge edge) {
        edge.enqueueMemory(this);
    }

    @Override
    public void releaseLock() {
        // not needed for minus temps
    }

    @Override
    protected JamochaArray<Row> getRowsToSplit() {
        throw new UnsupportedOperationException("Partial Minus Temps should not be split!");
    }

    static class MemoryHandlerMinusTempComplete extends MemoryHandlerMinusTemp {
        final JamochaArray<Row> completeRows;

        MemoryHandlerMinusTempComplete(final Template[] template, final MemoryHandlerMain originatingMainHandler,
                final JamochaArray<Row> partialRows, final JamochaArray<Row> completeRows,
                final FactAddress[] factAddresses) {
            super(template, originatingMainHandler, partialRows, factAddresses);
            this.completeRows = completeRows;
        }

        @Override
        protected JamochaArray<Row> getRowsToSplit() {
            return this.completeRows;
        }
    }

}
