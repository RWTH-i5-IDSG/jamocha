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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import lombok.ToString;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;

/**
 * Java-implementation of the {@link org.jamocha.dn.memory.MemoryHandlerMain} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryHandlerMain
 */
@ToString(callSuper = true)
public class MemoryHandlerMain extends MemoryHandlerBase implements
		org.jamocha.dn.memory.MemoryHandlerMain {
	static final long tryLockTimeout = 1L;
	static final TimeUnit tu = TimeUnit.SECONDS;

	final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	final FactAddress[] addresses;
	@Getter
	final protected Queue<MemoryHandlerPlusTemp<? extends MemoryHandlerMain>> validOutgoingPlusTokens =
			new LinkedList<>();
	final Counter counter;
	private ArrayList<Row> validRows;

	MemoryHandlerMain(final Template template, final Path... paths) {
		super(new Template[] { template });
		this.validRows = new ArrayList<Row>();
		final FactAddress address = new FactAddress(0);
		this.addresses = new FactAddress[] { address };
		for (final Path path : paths) {
			path.setFactAddressInCurrentlyLowestNode(address);
			Path.setJoinedWithForAll(path);
		}
		this.counter = Counter.newCounter();
	}

	MemoryHandlerMain(final Template[] template, final Counter counter,
			final FactAddress[] addresses) {
		super(template);
		this.validRows = new ArrayList<>();
		this.addresses = addresses;
		this.counter = counter;
	}

	public static org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher newMemoryHandlerMain(
			final PathFilter filter, final Map<Edge, Set<Path>> edgesAndPaths) {
		final PathFilterElementToCounterColumn pathFilterElementToCounterColumn =
				new PathFilterElementToCounterColumn();
		final boolean containsExistentials =
				!filter.getPositiveExistentialPaths().isEmpty()
						|| !filter.getNegativeExistentialPaths().isEmpty();
		if (containsExistentials) {
			// gather existential paths
			final HashSet<Path> existentialPaths = new HashSet<>();
			existentialPaths.addAll(filter.getPositiveExistentialPaths());
			existentialPaths.addAll(filter.getNegativeExistentialPaths());

			int index = 0;
			for (final PathFilterElement pathFilterElement : filter.getFilterElements()) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(pathFilterElement).getPaths();
				paths.retainAll(existentialPaths);
				if (0 == paths.size())
					continue;
				pathFilterElementToCounterColumn.putFilterElementToCounterColumn(pathFilterElement,
						new CounterColumn(index++));
			}
		}

		final ArrayList<Template> template = new ArrayList<>();
		final ArrayList<FactAddress> addresses = new ArrayList<>();
		if (!edgesAndPaths.isEmpty()) {
			final Edge[] incomingEdges =
					edgesAndPaths.entrySet().iterator().next().getKey().getTargetNode()
							.getIncomingEdges();
			for (final Edge edge : incomingEdges) {
				final MemoryHandlerMain memoryHandlerMain =
						(MemoryHandlerMain) edge.getSourceNode().getMemory();
				for (final Template t : memoryHandlerMain.getTemplate()) {
					template.add(t);
				}
				final HashMap<FactAddress, FactAddress> addressMap = new HashMap<>();
				for (final FactAddress oldFactAddress : memoryHandlerMain.addresses) {
					final FactAddress newFactAddress = new FactAddress(addresses.size());
					addressMap.put(oldFactAddress, newFactAddress);
					addresses.add(newFactAddress);
				}
				edge.setAddressMap(addressMap);
			}
		}
		final Template[] templArray = template.toArray(new Template[template.size()]);
		final FactAddress[] addrArray = addresses.toArray(new FactAddress[addresses.size()]);
		if (containsExistentials) {
			return new MemoryHandlerMainAndFilterElementToCounterColumn(
					new MemoryHandlerMainWithExistentials(templArray, Counter.newCounter(filter,
							pathFilterElementToCounterColumn), addrArray),
					pathFilterElementToCounterColumn);
		}
		return new MemoryHandlerMainAndFilterElementToCounterColumn(
				new MemoryHandlerMain(templArray, Counter.newCounter(filter,
						pathFilterElementToCounterColumn), addrArray),
				pathFilterElementToCounterColumn);
	}

	@Override
	public boolean tryReadLock() throws InterruptedException {
		return this.lock.readLock().tryLock(tryLockTimeout, tu);
	}

	@Override
	public void releaseReadLock() {
		this.lock.readLock().unlock();
	}

	@Override
	public void acquireWriteLock() {
		this.lock.writeLock().lock();
	}

	@Override
	public void releaseWriteLock() {
		this.lock.writeLock().unlock();
	}

	@Override
	public ArrayList<Row> getRowsForSucessorNodes() {
		return this.validRows;
	}

	public ArrayList<Row> getAllRows() {
		return this.validRows;
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInBeta(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return ((org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp<?>) token).newBetaTemp(this,
				originIncomingEdge, filter);
	}

	@Override
	public MemoryHandlerTemp<?> processTokenInAlpha(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return ((org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp<?>) token).newAlphaTemp(this,
				originIncomingEdge, filter);
	}

	@Override
	public MemoryHandlerPlusTemp<?> newPlusToken(final Node otn,
			final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerPlusTemp.newRootTemp(this, otn, facts);
	}

	@Override
	public MemoryHandlerMinusTemp newMinusToken(
			final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerMinusTemp.newRootTemp(this, facts);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTerminal newMemoryHandlerTerminal() {
		return new MemoryHandlerTerminal(this);
	}

	private static AddressFilterElement[] emptyAddressFilterElementArray =
			new AddressFilterElement[0];

	@Override
	public AddressFilterElement[] getRelevantExistentialFilterParts(final AddressFilter filter,
			final Edge edge) {
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
			final org.jamocha.dn.memory.FactAddress localizedAddress =
					edge.localizeAddress(originAddress);
			if (positiveExistentialAddresses.contains(localizedAddress)
					|| negativeExistentialAddresses.contains(localizedAddress)) {
				existentialAddresses.add(localizedAddress);
			}
		}
		final AddressFilterElement[] filterElements = filter.getFilterElements();
		final ArrayList<AddressFilterElement> partList = new ArrayList<>(filterElements.length);
		filterElementLoop: for (final AddressFilterElement filterElement : filterElements) {
			final SlotInFactAddress[] addresses = filterElement.getAddressesInTarget();
			for (final SlotInFactAddress slotInFactAddress : addresses) {
				final org.jamocha.dn.memory.FactAddress factAddress =
						slotInFactAddress.getFactAddress();
				if (existentialAddresses.contains(factAddress)) {
					partList.add(filterElement);
					continue filterElementLoop;
				}
			}
		}
		return (AddressFilterElement[]) partList.toArray();
	}

	/**
	 * creates new row using the fact tuple given and default counter values.
	 * 
	 * @param factTuple
	 *            fact tuple to use
	 * @return row containing fact tuple and default counter values
	 */
	public Row newRow(final Fact... factTuple) {
		assert factTuple.length == this.template.length;
		return new Row(factTuple);
	}

	/**
	 * creates new row of given width and default counter values.
	 * 
	 * @param factTuple
	 *            fact tuple to use
	 * @return row containing fact tuple and default counter values
	 */
	public Row newRow(final int columns) {
		return newRow(new Fact[columns]);
	}

}
