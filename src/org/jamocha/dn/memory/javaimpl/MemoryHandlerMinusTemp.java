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
import java.util.Iterator;
import java.util.List;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class MemoryHandlerMinusTemp extends MemoryHandlerTemp implements
		org.jamocha.dn.memory.MemoryHandlerMinusTemp {

	private static MemoryHandlerMinusTemp empty = new MemoryHandlerMinusTemp(null, null,
			new ArrayList<Fact[]>(0), new FactAddress[] {});

	/**
	 * Maps from FactAddress valid in the current scope of the token
	 */
	final FactAddress[] factAddresses;

	static MemoryHandlerMinusTemp newRootTemp(final MemoryHandlerMain memoryHandlerMain,
			final org.jamocha.dn.memory.Fact[] facts) {
		final List<Fact[]> factList = new ArrayList<>();
		for (org.jamocha.dn.memory.Fact fact : facts) {
			factList.add(new Fact[] { new Fact(fact.getSlotValues()) });
		}
		final List<Fact[]> filteredFactList =
				deleteAndFilterFacts(factList, memoryHandlerMain, FactCompare.equalContent);
		assert memoryHandlerMain.addresses.length == 1;
		if (0 == filteredFactList.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(memoryHandlerMain, filteredFactList,
				memoryHandlerMain.addresses);
	}

	/**
	 * State-pattern interface to provide easy locking/unlocking
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	private interface LazyLocker {
		LazyLocker getLock(final MemoryHandlerMain targetMain);

		LazyLocker releaseLock(final MemoryHandlerMain targetMain);

		static final LazyLocker locked = new LazyLocker() {
			@Override
			public LazyLocker getLock(final MemoryHandlerMain targetMain) {
				return this;
			}

			@Override
			public LazyLocker releaseLock(final MemoryHandlerMain targetMain) {
				targetMain.releaseWriteLock();
				return unlocked;
			}
		};

		static final LazyLocker unlocked = new LazyLocker() {
			@Override
			public LazyLocker getLock(final MemoryHandlerMain targetMain) {
				targetMain.acquireWriteLock();
				return locked;
			}

			@Override
			public LazyLocker releaseLock(final MemoryHandlerMain targetMain) {
				return this;
			}
		};
	}

	@Override
	public MemoryHandlerTemp newBetaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException {
		final MemoryHandlerMain targetMain =
				(MemoryHandlerMain) originIncomingEdge.getTargetNode().getMemory();
		final List<Fact[]> targetFacts = targetMain.facts;
		LazyLocker lazyLocker = LazyLocker.unlocked;

		try {
			for (final Iterator<Fact[]> targetFactsIterator = targetFacts.iterator(); targetFactsIterator
					.hasNext();) {
				final Fact[] targetFactTuple = targetFactsIterator.next();
				minusLoop: for (final Fact[] minusFactTuple : this.facts) {
					for (int i = 0; i < this.factAddresses.length; ++i) {
						final int targetAddress = this.factAddresses[i].index;
						final int minusAddress = i;
						final Fact targetFact = targetFactTuple[targetAddress];
						final Fact minusFact = minusFactTuple[minusAddress];
						if (minusFact != targetFact) {
							continue minusLoop;
						}
					}
					// we spotted a match for a complete row in the minus token, so we delete the
					// corresponding row in the main memory
					lazyLocker = lazyLocker.getLock(targetMain);
					targetFactsIterator.remove();
					// don't reconsider the same line again
					break;
				}
			}
		} finally {
			lazyLocker = lazyLocker.releaseLock(targetMain);
		}
		return new MemoryHandlerMinusTemp(originatingMainHandler, facts, localizeAddressMap(
				factAddresses, originIncomingEdge));
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
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException {
		final List<Fact[]> relevantFacts =
				deleteAndFilterFacts(this.facts, (MemoryHandlerMain) originIncomingEdge
						.getTargetNode().getMemory(), FactCompare.equalReference);
		if (0 == relevantFacts.size()) {
			return MemoryHandlerMinusTemp.empty;
		}
		return new MemoryHandlerMinusTemp(originatingMainHandler, relevantFacts,
				localizeAddressMap(factAddresses, originIncomingEdge));
	}

	private interface FactCompare {
		boolean equal(final Fact a, final Fact b);

		static FactCompare equalReference = new FactCompare() {
			@Override
			public boolean equal(final Fact a, final Fact b) {
				return Fact.equalReference(a, b);
			}
		};
		static FactCompare equalContent = new FactCompare() {
			@Override
			public boolean equal(final Fact a, final Fact b) {
				return Fact.equalContent(a, b);
			}
		};
	}

	/**
	 * Don't use for fact tuples as the full fact tuple will be copied
	 * 
	 * @param minusFacts
	 * @param targetMemory
	 * @param factCompare
	 * @return
	 */
	protected static List<Fact[]> deleteAndFilterFacts(final List<Fact[]> minusFacts,
			final MemoryHandlerMain targetMemory, final FactCompare factCompare) {
		LazyLocker lazyLocker = LazyLocker.unlocked;
		final List<Fact[]> targetFacts = targetMemory.facts;
		final List<Fact[]> minusFactsCopy = new ArrayList<>(minusFacts);
		final List<Fact[]> relevantFacts = new ArrayList<Fact[]>();
		factLoop: for (final Iterator<Fact[]> minusFactsIterator = minusFactsCopy.iterator(); minusFactsIterator
				.hasNext();) {
			final Fact[] minusFactTuple = minusFactsIterator.next();
			for (final Iterator<Fact[]> targetFactsIterator = targetFacts.iterator(); targetFactsIterator
					.hasNext();) {
				final Fact[] targetFactTuple = targetFactsIterator.next();
				assert 1 == targetFactTuple.length;
				if (factCompare.equal(targetFactTuple[0], minusFactTuple[0])) {
					lazyLocker = lazyLocker.getLock(targetMemory);
					relevantFacts.add(targetFactTuple);
					targetFactsIterator.remove();
					minusFactsIterator.remove();
					if (minusFacts.isEmpty()) {
						break factLoop;
					}
					continue factLoop;
				}
			}
		}
		lazyLocker = lazyLocker.releaseLock(targetMemory);
		return relevantFacts;
	}

	private MemoryHandlerMinusTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final List<Fact[]> facts, final FactAddress[] factAddresses) {
		super(originatingMainHandler, facts);
		this.factAddresses = factAddresses;
	}

	private MemoryHandlerMinusTemp(final Template[] template,
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final List<Fact[]> facts, final FactAddress[] factAddresses) {
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

}
