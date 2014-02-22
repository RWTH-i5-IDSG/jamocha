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
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.NegativeExistentialEdge;
import org.jamocha.dn.nodes.PositiveEdge;
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
			new ArrayList<Fact[]>(0), new FactAddress[] {});

	/**
	 * Maps FactAddresses valid in the current scope of the token by position of the facts in the
	 * temp memory
	 */
	final FactAddress[] factAddresses;

	static MemoryHandlerMinusTemp newRootTemp(final MemoryHandlerMain memoryHandlerMain,
			final org.jamocha.dn.memory.Fact[] facts) {
		final List<Fact[]> minusFacts = new ArrayList<>(facts.length);
		for (final org.jamocha.dn.memory.Fact fact : facts) {
			minusFacts.add(new Fact[] { new Fact(fact.getSlotValues()) });
		}
		final FactAddress[] factAddresses = memoryHandlerMain.addresses;
		assert factAddresses.length == 1;
		final List<Fact[]> relevantFactTuples =
				getRelevantFactTuples(memoryHandlerMain, minusFacts, factAddresses,
						EqualityChecker.root);
		if (0 == relevantFactTuples.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(memoryHandlerMain.getTemplate(), memoryHandlerMain,
				relevantFactTuples, factAddresses);
	}

	/**
	 * Tiny state-like interface for the following use case: <br />
	 * A filter is applied to elements in a list and we want to get a list of the elements that did
	 * not match the filter. Thus, if the filter matches no element, the original list may be
	 * returned. Otherwise we need to copy parts of it. <br />
	 * To achieve this, a reference to that list is stored in the start-state (SameList) and
	 * unmatched is called as long as no entry matches the filter. As soon as the first element
	 * matching the filter leads to a matched-call, the list is copied up to (but not including) the
	 * current position and the state is changed to CopiedList. Now, all further matched calls are
	 * ignored and all unmatched have the corresponding element copied into the new list. There is
	 * no further state change. <br />
	 * <b>Note:</b> The return value of matched and unmatched has to be stored in the instance used
	 * to accept the state change!
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static interface LazyListCopy {
		LazyListCopy unmatched(final int index);

		LazyListCopy matched(final int index);

		List<Fact[]> getList();
	}

	/**
	 * @see LazyListCopy
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@AllArgsConstructor
	static abstract class LLC implements LazyListCopy {
		@Getter
		final List<Fact[]> list;

		@Override
		public LazyListCopy unmatched(final int index) {
			return this;
		}

		@Override
		public LazyListCopy matched(final int index) {
			return this;
		}
	};

	/**
	 * @see LazyListCopy
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static class SameList extends LLC {
		public SameList(final List<Fact[]> list) {
			super(list);
		}

		@Override
		public LazyListCopy matched(final int index) {
			final List<Fact[]> copy = new ArrayList<>(this.list.size());
			for (int i = 0; i < index; i++) {
				copy.add(this.list.get(i));
			}
			return new CopiedList(this.list, copy);
		}
	}

	/**
	 * @see LazyListCopy
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static class CopiedList extends LLC {
		final List<Fact[]> copy;

		public CopiedList(final List<Fact[]> list, final List<Fact[]> copy) {
			super(list);
			this.copy = copy;
		}

		@Override
		public LazyListCopy unmatched(final int index) {
			this.copy.add(this.list.get(index));
			return this;
		}

		@Override
		public List<Fact[]> getList() {
			return this.copy;
		}
	}

	/**
	 * Interface to easy code re-usage where the only difference was the comparison of fact tuples.
	 * The parameters of equals can be seen as the union of the parameters needed for the three
	 * implementations, thus some are only needed in special cases. <br />
	 * Implementations
	 * <ul>
	 * <li><b>root:</b> checks for equal content in both facts (first element of fact tuple) and
	 * replaces the negative fact with its corresponding original if matched to allow for
	 * referential comparison in the rest of the network (for better performance)</li>
	 * <li><b>alpha:</b> checks for referential equality of the first elements of the fact tuples</li>
	 * <li><b>beta:</b> checks for referential equality of all elements of the fact tuples, uses
	 * fact address translation to do so</li>
	 * </ul>
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static interface EqualityChecker {
		boolean equals(final Fact[] originalFactTuple, final Fact[] minusFactTuple,
				final List<Fact[]> minusFacts, final int minusFactsIndex,
				final FactAddress[] factAddresses);

		static EqualityChecker root = new EqualityChecker() {
			@Override
			public boolean equals(final Fact[] originalFactTuple, final Fact[] minusFactTuple,
					final List<Fact[]> minusFacts, final int minusFactsIndex,
					final FactAddress[] factAddresses) {
				final Fact originalFact = originalFactTuple[0];
				final Fact minusFact = minusFactTuple[0];
				if (Fact.equalContent(originalFact, minusFact)) {
					minusFacts.set(minusFactsIndex, originalFactTuple);
					return true;
				}
				return false;
			}
		};
		static EqualityChecker alpha = new EqualityChecker() {
			@Override
			public boolean equals(final Fact[] originalFactTuple, final Fact[] minusFactTuple,
					final List<Fact[]> minusFacts, final int minusFactsIndex,
					final FactAddress[] factAddresses) {
				final Fact originalFact = originalFactTuple[0];
				final Fact minusFact = minusFactTuple[0];
				return minusFact == originalFact;
			}
		};
		static EqualityChecker beta = new EqualityChecker() {
			@Override
			public boolean equals(final Fact[] originalFactTuple, final Fact[] minusFactTuple,
					final List<Fact[]> minusFacts, final int minusFactsIndex,
					final FactAddress[] factAddresses) {
				for (int i = 0; i < factAddresses.length; ++i) {
					final int originalAddress = factAddresses[i].index;
					final int minusAddress = i;
					final Fact originalFact = originalFactTuple[originalAddress];
					final Fact minusFact = minusFactTuple[minusAddress];
					if (minusFact != originalFact) {
						return false;
					}
				}
				return true;
			}
		};
	}

	private static List<Fact[]> getRemainingFactTuples(final List<Fact[]> originalFacts,
			final List<Fact[]> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		final int originalFactsSize = originalFacts.size();
		final int minusFactsSize = minusFacts.size();
		LazyListCopy remainingFacts = new SameList(originalFacts);
		outerLoop: for (int originalFactsIndex = 0; originalFactsIndex < originalFactsSize; ++originalFactsIndex) {
			final Fact[] originalFactTuple = originalFacts.get(originalFactsIndex);
			for (int minusFactsIndex = 0; minusFactsIndex < minusFactsSize; ++minusFactsIndex) {
				final Fact[] minusFactTuple = minusFacts.get(minusFactsIndex);
				if (equalityChecker.equals(originalFactTuple, minusFactTuple, minusFacts,
						minusFactsIndex, factAddresses)) {
					// we spotted a match for a complete row in the minus token, mark the
					// responsible row in the minus token as relevant for the successor network
					remainingFacts = remainingFacts.matched(originalFactsIndex);
					marked[minusFactsIndex] = true;
					// don't reconsider the same line in the original facts again
					continue outerLoop;
				}
			}
			// facts differ at some point, add to remaining facts
			remainingFacts = remainingFacts.unmatched(originalFactsIndex);
		}
		return remainingFacts.getList();
	}

	@Override
	public MemoryHandlerTemp newBetaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final PositiveEdge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final List<Fact[]> minusFacts = this.facts;
		final FactAddress[] localizedAddressMap =
				localizeAddressMap(this.factAddresses, originIncomingEdge);
		final List<Fact[]> relevantMinusFacts =
				getRelevantFactTuples(targetMain, minusFacts, localizedAddressMap,
						EqualityChecker.beta);
		return new MemoryHandlerMinusTemp(getTemplate(),
				(MemoryHandlerMain) originatingMainHandler, relevantMinusFacts, localizedAddressMap);
	}

	@Override
	public MemoryHandlerTemp newBetaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final NegativeExistentialEdge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		// FIXME implement
		return null;
	}

	private static void filterOutgoingTemps(
			final Queue<MemoryHandlerPlusTemp> validOutgoingPlusTokens,
			final List<Fact[]> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		for (final MemoryHandlerPlusTemp temp : validOutgoingPlusTokens) {
			final List<Fact[]> originalFacts = (null == temp.filtered ? temp.facts : temp.filtered);
			final List<Fact[]> remainingFacts =
					getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked,
							equalityChecker);
			temp.filtered = remainingFacts;
		}
	}

	private static void filterTargetMain(final MemoryHandlerMain targetMain,
			final List<Fact[]> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		final List<Fact[]> originalFacts = targetMain.facts;
		final int originalFactsSize = originalFacts.size();
		final List<Fact[]> remainingFacts =
				getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked,
						equalityChecker);
		if (remainingFacts.size() != originalFactsSize) {
			targetMain.acquireWriteLock();
			targetMain.facts = remainingFacts;
			targetMain.releaseWriteLock();
		}
	}

	private static List<Fact[]> getMarkedFactTuples(final List<Fact[]> minusFacts,
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
		final List<Fact[]> relevantMinusFacts = new ArrayList<Fact[]>(relevantMinusFactsSize);
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
		for (int i = 0; i < length; ++i) {
			factAddresses[i] = (FactAddress) localizingEdge.localizeAddress(old[i]);
		}
		return factAddresses;
	}

	@Override
	public MemoryHandlerTemp newAlphaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final PositiveEdge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final List<Fact[]> minusFacts = this.facts;
		final List<Fact[]> markedFactTuples =
				getRelevantFactTuples(targetMain, minusFacts, this.factAddresses,
						EqualityChecker.alpha);
		if (0 == markedFactTuples.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(getTemplate(),
				(MemoryHandlerMain) originatingMainHandler, markedFactTuples, localizeAddressMap(
						this.factAddresses, originIncomingEdge));
	}

	@Override
	public MemoryHandlerTemp newAlphaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final NegativeExistentialEdge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		// FIXME implement
		return null;
	}

	private static List<Fact[]> getRelevantFactTuples(final MemoryHandlerMain targetMain,
			final List<Fact[]> minusFacts, final FactAddress[] factAddresses,
			final EqualityChecker equalityChecker) {
		final boolean[] marked = new boolean[minusFacts.size()];
		filterTargetMain(targetMain, minusFacts, factAddresses, marked, equalityChecker);
		filterOutgoingTemps(targetMain.getValidOutgoingPlusTokens(), minusFacts, factAddresses,
				marked, equalityChecker);
		return getMarkedFactTuples(minusFacts, marked);
	}

	private MemoryHandlerMinusTemp(final Template[] template,
			final MemoryHandlerMain originatingMainHandler, final List<Fact[]> facts,
			final FactAddress[] factAddresses) {
		super(template, originatingMainHandler, facts);
		this.factAddresses = factAddresses;
	}

	@Override
	public void enqueueInEdges(final Collection<? extends Edge> edges) {
		for (final Edge edge : edges) {
			edge.enqueueMinusMemory(this);
		}
	}

	@Override
	public Object getValue(final org.jamocha.dn.memory.FactAddress address, final SlotAddress slot,
			final int row) {
		return this.facts.get(row)[0].getValue(slot);
	}

	@Override
	public boolean releaseLock() {
		// TODO does nothing, do we need it in minus temps?
		return false;
	}

}
