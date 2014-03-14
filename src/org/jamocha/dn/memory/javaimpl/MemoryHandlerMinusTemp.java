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
			new ArrayList<FactTuple>(0), new FactAddress[] {});

	/**
	 * Maps FactAddresses valid in the current scope of the token by position of the facts in the
	 * temp memory
	 */
	final FactAddress[] factAddresses;

	static MemoryHandlerMinusTemp newRootTemp(final MemoryHandlerMain memoryHandlerMain,
			final org.jamocha.dn.memory.Fact[] facts) {
		final ArrayList<FactTuple> minusFacts = new ArrayList<>(facts.length);
		for (final org.jamocha.dn.memory.Fact fact : facts) {
			minusFacts.add(new FactTuple(new Fact[] { new Fact(fact.getSlotValues()) }));
		}
		final FactAddress[] factAddresses = memoryHandlerMain.addresses;
		assert factAddresses.length == 1;
		final ArrayList<FactTuple> relevantFactTuples =
				getRelevantFactTuples(memoryHandlerMain, minusFacts, factAddresses,
						EqualityChecker.root);
		if (0 == relevantFactTuples.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(memoryHandlerMain.getTemplate(), memoryHandlerMain,
				relevantFactTuples, factAddresses);
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
		boolean equals(final FactTuple originalFactTuple, final FactTuple minusFactTuple,
				final ArrayList<FactTuple> minusFacts, final int minusFactsIndex,
				final FactAddress[] factAddresses);

		static EqualityChecker root = new EqualityChecker() {
			@Override
			public boolean equals(final FactTuple originalFactTuple,
					final FactTuple minusFactTuple, final ArrayList<FactTuple> minusFacts,
					final int minusFactsIndex, final FactAddress[] factAddresses) {
				final Fact originalFact = originalFactTuple.getFactTuple()[0];
				final Fact minusFact = minusFactTuple.getFactTuple()[0];
				if (Fact.equalContent(originalFact, minusFact)) {
					minusFacts.set(minusFactsIndex, originalFactTuple);
					return true;
				}
				return false;
			}
		};
		static EqualityChecker alpha = new EqualityChecker() {
			@Override
			public boolean equals(final FactTuple originalFactTuple,
					final FactTuple minusFactTuple, final ArrayList<FactTuple> minusFacts,
					final int minusFactsIndex, final FactAddress[] factAddresses) {
				final Fact originalFact = originalFactTuple.getFactTuple()[0];
				final Fact minusFact = minusFactTuple.getFactTuple()[0];
				return minusFact == originalFact;
			}
		};
		static EqualityChecker beta = new EqualityChecker() {
			@Override
			public boolean equals(final FactTuple originalFactTuple,
					final FactTuple minusFactTuple, final ArrayList<FactTuple> minusFacts,
					final int minusFactsIndex, final FactAddress[] factAddresses) {
				for (int i = 0; i < factAddresses.length; ++i) {
					final int originalAddress = factAddresses[i].index;
					final int minusAddress = i;
					final Fact originalFact = originalFactTuple.getFactTuple()[originalAddress];
					final Fact minusFact = minusFactTuple.getFactTuple()[minusAddress];
					if (minusFact != originalFact) {
						return false;
					}
				}
				return true;
			}
		};
	}

	private static ArrayList<FactTuple> getRemainingFactTuples(
			final ArrayList<FactTuple> originalFacts, final ArrayList<FactTuple> minusFacts,
			final FactAddress[] factAddresses, final boolean[] marked,
			final EqualityChecker equalityChecker) {
		final int originalFactsSize = originalFacts.size();
		final int minusFactsSize = minusFacts.size();
		final LazyListCopy remainingFacts = new LazyListCopy(originalFacts);
		outerLoop: for (int originalFactsIndex = 0; originalFactsIndex < originalFactsSize; ++originalFactsIndex) {
			final FactTuple originalFactTuple = originalFacts.get(originalFactsIndex);
			for (int minusFactsIndex = 0; minusFactsIndex < minusFactsSize; ++minusFactsIndex) {
				final FactTuple minusFactTuple = minusFacts.get(minusFactsIndex);
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
	public org.jamocha.dn.memory.MemoryHandlerTemp newBetaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		if (originIncomingEdge.getTargetNode().getOutgoingExistentialEdges().isEmpty()) {
			// some of the target node's paths are existential, we need to pass a complete token,
			// not only the partial version
		}

		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final ArrayList<FactTuple> minusFacts = this.rows;
		final FactAddress[] localizedAddressMap =
				localizeAddressMap(this.factAddresses, originIncomingEdge);
		final ArrayList<FactTuple> relevantMinusFacts =
				getRelevantFactTuples(targetMain, minusFacts, localizedAddressMap,
						EqualityChecker.beta);
		return new MemoryHandlerMinusTemp(getTemplate(),
				(MemoryHandlerMain) originatingMainHandler, relevantMinusFacts, localizedAddressMap);
	}

	private static void filterOutgoingTemps(
			final Queue<MemoryHandlerPlusTemp> validOutgoingPlusTokens,
			final ArrayList<FactTuple> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		for (final MemoryHandlerPlusTemp temp : validOutgoingPlusTokens) {
			final ArrayList<FactTuple> originalFacts =
					(null == temp.filtered ? temp.rows : temp.filtered);
			final ArrayList<FactTuple> remainingFacts =
					getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked,
							equalityChecker);
			temp.filtered = remainingFacts;
		}
	}

	private static void filterTargetMain(final MemoryHandlerMain targetMain,
			final ArrayList<FactTuple> minusFacts, final FactAddress[] factAddresses,
			final boolean[] marked, final EqualityChecker equalityChecker) {
		final ArrayList<FactTuple> originalFacts = targetMain.rows;
		final int originalFactsSize = originalFacts.size();
		final ArrayList<FactTuple> remainingFacts =
				getRemainingFactTuples(originalFacts, minusFacts, factAddresses, marked,
						equalityChecker);
		if (remainingFacts.size() != originalFactsSize) {
			targetMain.acquireWriteLock();
			targetMain.rows = remainingFacts;
			targetMain.releaseWriteLock();
		}
	}

	private static ArrayList<FactTuple> getMarkedFactTuples(final ArrayList<FactTuple> minusFacts,
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
		final ArrayList<FactTuple> relevantMinusFacts =
				new ArrayList<FactTuple>(relevantMinusFactsSize);
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
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final ArrayList<FactTuple> minusFacts = this.rows;
		final ArrayList<FactTuple> markedFactTuples =
				getRelevantFactTuples(targetMain, minusFacts, this.factAddresses,
						EqualityChecker.alpha);
		if (0 == markedFactTuples.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(getTemplate(),
				(MemoryHandlerMain) originatingMainHandler, markedFactTuples, localizeAddressMap(
						this.factAddresses, originIncomingEdge));
	}

	private static ArrayList<FactTuple> getRelevantFactTuples(final MemoryHandlerMain targetMain,
			final ArrayList<FactTuple> minusFacts, final FactAddress[] factAddresses,
			final EqualityChecker equalityChecker) {
		final boolean[] marked = new boolean[minusFacts.size()];
		filterTargetMain(targetMain, minusFacts, factAddresses, marked, equalityChecker);
		filterOutgoingTemps(targetMain.getValidOutgoingPlusTokens(), minusFacts, factAddresses,
				marked, equalityChecker);
		return getMarkedFactTuples(minusFacts, marked);
	}

	private MemoryHandlerMinusTemp(final Template[] template,
			final MemoryHandlerMain originatingMainHandler, final ArrayList<FactTuple> facts,
			final FactAddress[] factAddresses) {
		super(template, originatingMainHandler, facts);
		this.factAddresses = factAddresses;
	}

	@Override
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this);
	}

	@Override
	public MemoryHandlerTemp releaseLock() {
		// TODO does nothing, do we need it in minus temps?
		return null;
	}

}
