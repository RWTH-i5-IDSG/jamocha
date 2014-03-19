/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.memory.javaimpl;

import java.util.ArrayList;
import java.util.Queue;
import java.util.function.Function;

import lombok.ToString;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.AddressFilter;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
@ToString(callSuper = true)
public class MemoryHandlerMinusTemp extends MemoryHandlerTemp implements
		org.jamocha.dn.memory.MemoryHandlerMinusTemp {

	private static MemoryHandlerMinusTemp empty = new MemoryHandlerMinusTemp(null, null,
			new ArrayList<Row>(0), new FactAddress[] {});

	/**
	 * Maps FactAddresses valid in the current scope of the token by position of the facts in the
	 * temp memory
	 */
	final FactAddress[] factAddresses;

	static MemoryHandlerMinusTemp newRootTemp(final MemoryHandlerMain memoryHandlerMain,
			final org.jamocha.dn.memory.Fact[] facts) {
		final ArrayList<Row> minusFacts = new ArrayList<>(facts.length);
		for (final org.jamocha.dn.memory.Fact fact : facts) {
			minusFacts.add(new Row(new Fact[] { new Fact(fact.getSlotValues()) }));
		}
		final FactAddress[] factAddresses = memoryHandlerMain.addresses;
		assert factAddresses.length == 1;
		final ArrayList<Row> relevantFactTuples =
				getRelevantFactTuples(memoryHandlerMain, minusFacts, factAddresses,
						EqualityChecker.root);
		if (0 == relevantFactTuples.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(memoryHandlerMain.getTemplate(), memoryHandlerMain,
				relevantFactTuples, factAddresses);
	}

	private static ArrayList<Row> getRemainingFactTuples(final ArrayList<Row> originalFacts,
			final ArrayList<Row> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		return getRemainingTs(originalFacts, Function.identity(), minusFacts, factAddresses,
				marked, equalityChecker);
	}

	private static <T> ArrayList<T> getRemainingTs(final ArrayList<T> originalFacts,
			final Function<T, Row> converter, final ArrayList<Row> minusFacts,
			final FactAddress[] factAddresses, final boolean[] marked,
			final EqualityChecker equalityChecker) {
		final int originalFactsSize = originalFacts.size();
		final int minusFactsSize = minusFacts.size();
		final LazyListCopy<T> remainingFacts = new LazyListCopy<>(originalFacts);
		outerLoop: for (int originalFactsIndex = 0; originalFactsIndex < originalFactsSize; ++originalFactsIndex) {
			final Row originalFactTuple = converter.apply(originalFacts.get(originalFactsIndex));
			for (int minusFactsIndex = 0; minusFactsIndex < minusFactsSize; ++minusFactsIndex) {
				final Row minusFactTuple = minusFacts.get(minusFactsIndex);
				if (equalityChecker.equals(originalFactTuple, minusFactTuple, minusFacts,
						minusFactsIndex, factAddresses)) {
					// we spotted a match for a complete row in the minus token
					remainingFacts.drop(originalFactsIndex);
					// mark the responsible row in the minus token as relevant for the successor
					// network
					marked[minusFactsIndex] = true;
					// don't reconsider the same line in the original facts again
					continue outerLoop;
				}
			}
			// facts differ at some point, add to remaining facts
			remainingFacts.keep(originalFactsIndex);
		}
		return remainingFacts.getList();
	}

	@Override
	public MemoryHandlerTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		if (originIncomingEdge.getTargetNode().getOutgoingExistentialEdges().isEmpty()) {
			// some of the target node's paths are existential, we need to pass a complete token,
			// not only the partial version
		}

		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final ArrayList<Row> minusFacts = this.validRows;
		final FactAddress[] localizedAddressMap =
				localizeAddressMap(this.factAddresses, originIncomingEdge);
		final ArrayList<Row> relevantMinusFacts =
				getRelevantFactTuples(targetMain, minusFacts, localizedAddressMap,
						EqualityChecker.beta);
		return new MemoryHandlerMinusTemp(getTemplate(),
				(MemoryHandlerMain) originatingMainHandler, relevantMinusFacts, localizedAddressMap);
	}

	@Override
	public MemoryHandlerTemp newBetaTemp(
			final MemoryHandlerMainWithExistentials originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		// TODO implement the existential version
		return null;
	}

	private static void filterOutgoingTemps(
			final Queue<MemoryHandlerPlusTemp> validOutgoingPlusTokens,
			final ArrayList<Row> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		for (final MemoryHandlerPlusTemp temp : validOutgoingPlusTokens) {
			final ArrayList<Row> originalFacts = temp.getFiltered();
			final ArrayList<Row> remainingFacts =
					getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked,
							equalityChecker);
			temp.setFiltered(remainingFacts);
		}
	}

	private static void filterTargetMain(final MemoryHandlerMain targetMain,
			final ArrayList<Row> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		final ArrayList<Row> originalFacts = targetMain.getAllRows();
		final int originalFactsSize = originalFacts.size();
		final ArrayList<Row> remainingFacts =
				getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked,
						equalityChecker);
		if (remainingFacts.size() != originalFactsSize) {
			targetMain.acquireWriteLock();
			targetMain.allRows = remainingFacts;
			targetMain.releaseWriteLock();
		}
	}

	private static ArrayList<Row> getMarkedFactTuples(final ArrayList<Row> minusFacts,
			final boolean[] marked) {
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
		final ArrayList<Row> relevantMinusFacts = new ArrayList<Row>(relevantMinusFactsSize);
		for (int minusFactsIndex = 0; minusFactsIndex < minusFactsSize; ++minusFactsIndex) {
			if (marked[minusFactsIndex]) {
				relevantMinusFacts.add(minusFacts.get(minusFactsIndex));
			}
		}
		return relevantMinusFacts;

	}

	private static FactAddress[] localizeAddressMap(final FactAddress[] old,
			final Edge localizingEdge) {
		final int length = old.length;
		final FactAddress[] factAddresses = new FactAddress[length];
		final AddressFilter filter = localizingEdge.getFilter();
		for (int i = 0; i < length; ++i) {
			final FactAddress oldAddress = old[i];
			if (null == oldAddress)
				continue;
			final FactAddress newAddress = (FactAddress) localizingEdge.localizeAddress(oldAddress);
			factAddresses[i] = filter.isExistential(newAddress) ? null : newAddress;
		}
		return factAddresses;
	}

	@Override
	public MemoryHandlerTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final ArrayList<Row> minusFacts = this.validRows;
		final ArrayList<Row> markedFactTuples =
				getRelevantFactTuples(targetMain, minusFacts, this.factAddresses,
						EqualityChecker.alpha);
		if (0 == markedFactTuples.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(getTemplate(),
				(MemoryHandlerMain) originatingMainHandler, markedFactTuples, localizeAddressMap(
						this.factAddresses, originIncomingEdge));
	}

	private static ArrayList<Row> getRelevantFactTuples(final MemoryHandlerMain targetMain,
			final ArrayList<Row> minusFacts, final FactAddress[] factAddresses,
			final EqualityChecker equalityChecker) {
		final boolean[] marked = new boolean[minusFacts.size()];
		filterTargetMain(targetMain, minusFacts, factAddresses, marked, equalityChecker);
		filterOutgoingTemps(targetMain.getValidOutgoingPlusTokens(), minusFacts, factAddresses,
				marked, equalityChecker);
		return getMarkedFactTuples(minusFacts, marked);
	}

	private MemoryHandlerMinusTemp(final Template[] template,
			final MemoryHandlerMain originatingMainHandler, final ArrayList<Row> facts,
			final FactAddress[] factAddresses) {
		super(template, originatingMainHandler, facts);
		this.factAddresses = factAddresses;
	}

	@Override
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this);
	}

	@Override
	public void releaseLock() {
		// TODO does nothing, do we need it in minus temps?
	}

}
