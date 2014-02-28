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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import lombok.ToString;

import org.jamocha.dn.memory.PathFilterElementToCounterColumn;
import org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;

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
	// each fact address corresponds to the template at the same index for 0<=i<template.length and
	// to a counter column for template.length<=i<addresses.length
	final FactAddress[] addresses;
	@Getter
	final protected Queue<MemoryHandlerPlusTemp> validOutgoingPlusTokens = new LinkedList<>();
	final Counter counter;

	MemoryHandlerMain(final Template template, final Path... paths) {
		super(new Template[] { template }, new ArrayList<Fact[]>());
		final FactAddress address = new FactAddress(0);
		this.addresses = new FactAddress[] { address };
		for (final Path path : paths) {
			path.setFactAddressInCurrentlyLowestNode(address);
			Path.setJoinedWithForAll(path);
		}
		this.counter = Counter.newEmptyCounter();
	}

	MemoryHandlerMain(final Template[] template, final List<Fact[]> facts, final Counter counter,
			final FactAddress[] addresses) {
		super(template, facts);
		this.addresses = addresses;
		this.counter = counter;
	}

	public static MemoryHandlerMainAndCounterColumnMatcher newMemoryHandlerMain(
			final PathFilter filter, final Map<Edge, Set<Path>> edgesAndPaths) {
		final HashSet<Path> existentialPaths = new HashSet<>();
		existentialPaths.addAll(filter.getPositiveExistentialPaths());
		existentialPaths.addAll(filter.getNegativeExistentialPaths());
		final ArrayList<Template> template = new ArrayList<>();
		final ArrayList<FactAddress> addresses = new ArrayList<>();
		final ArrayList<FactAddress> fakeAddresses = new ArrayList<>();

		final LinkedHashSet<Path> regularPaths =
				PathCollector.newLinkedHashSet().collect(filter).getPaths();
		regularPaths.removeAll(existentialPaths);
		for (final Path path : regularPaths) {
			final Template pathTemplate = path.getTemplate();
			final org.jamocha.dn.memory.FactAddress pathFactAddress =
					path.getFactAddressInCurrentlyLowestNode();

		}

		// Zuordnung template zu Path über template position and

		for (final Entry<Edge, Set<Path>> entry : edgesAndPaths.entrySet()) {
			final Edge edge = entry.getKey();
			final Set<Path> pathsInEdge = entry.getValue();
			// for each edge look at regular paths and translate their addresses
			final MemoryHandlerMain memoryHandlerMain =
					(MemoryHandlerMain) edge.getSourceNode().getMemory();
			final HashMap<FactAddress, FactAddress> addressMap = new HashMap<>();
			{
				final Template[] currentHandlerTemplate = memoryHandlerMain.getTemplate();
				outerloop: for (int i = 0; i < currentHandlerTemplate.length; i++) {
					final Template t = currentHandlerTemplate[i];
					// find a path that corresponds
					for (final Path path : pathsInEdge) {
						final Template pathTemplate = path.getTemplate();
						final FactAddress pathFactAddress =
								(FactAddress) path.getFactAddressInCurrentlyLowestNode();
						if (pathFactAddress.index == i && t == pathTemplate) {
							if (!existentialPaths.contains(path)) {
								template.add(t);
								final FactAddress oldFactAddress = memoryHandlerMain.addresses[i];
								final FactAddress newFactAddress =
										new FactAddress(addresses.size());
								addressMap.put(oldFactAddress, newFactAddress);
								addresses.add(newFactAddress);
							}
							continue outerloop;
						}
					}
				}
			}
			edge.setAddressMap(addressMap);
		}
		final Template[] templArray = template.toArray(new Template[template.size()]);
		final FactAddress[] addrArray = addresses.toArray(new FactAddress[addresses.size()]);
		// TODO make it non-null ;)
		final PathFilterElementToCounterColumn filterElementToCounterColumn = null;
		return new MemoryHandlerMainAndFilterElementToCounterColumn(new MemoryHandlerMain(
				templArray, new ArrayList<Fact[]>(), Counter.newCounter(filter,
						filterElementToCounterColumn), addrArray), filterElementToCounterColumn);
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
	public void add(final org.jamocha.dn.memory.MemoryHandlerPlusTemp toAdd) {
		final MemoryHandlerPlusTemp temp = (MemoryHandlerPlusTemp) toAdd;
		final List<Fact[]> facts = (null == temp.filtered ? temp.facts : temp.filtered);
		for (final Fact[] row : facts) {
			this.facts.add(row);
		}
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInBeta(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return token.newBetaTemp(this, originIncomingEdge, filter);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInAlpha(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return token.newAlphaTemp(this, originIncomingEdge, filter);
	}

	@Override
	public MemoryHandlerPlusTemp newPlusToken(final Node otn,
			final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerPlusTemp.newRootTemp(this, otn, facts);
	}

	@Override
	public MemoryHandlerMinusTemp newMinusToken(final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerMinusTemp.newRootTemp(this, facts);
	}

	@Override
	public MemoryHandlerTerminal newMemoryHandlerTerminal() {
		return new MemoryHandlerTerminal(this);
	}

}
