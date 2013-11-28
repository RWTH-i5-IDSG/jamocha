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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import lombok.ToString;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;

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
	final protected Queue<MemoryHandlerPlusTemp> validOutgoingPlusTokens = new LinkedList<>();

	MemoryHandlerMain(final Template template, final Path... paths) {
		super(new Template[] { template }, new ArrayList<Fact[]>());
		final FactAddress address = new FactAddress(0);
		this.addresses = new FactAddress[] { address };
		for (final Path path : paths) {
			path.setFactAddressInCurrentlyLowestNode(address);
			Path.setJoinedWithForAll(path);
		}
	}

	MemoryHandlerMain(final Edge... edgesToBeJoined) {
		super(calculateAddressesAndTemplates(edgesToBeJoined), new ArrayList<Fact[]>());
		final ArrayList<FactAddress> addresses = new ArrayList<>();
		for (final Edge edge : edgesToBeJoined) {
			final HashMap<FactAddress, FactAddress> fMap = new HashMap<>();
			final MemoryHandlerMain memoryHandlerMain =
					(MemoryHandlerMain) edge.getSourceNode().getMemory();
			for (final FactAddress oldFactAddress : memoryHandlerMain.addresses) {
				final FactAddress newFactAddress = new FactAddress(addresses.size());
				fMap.put(oldFactAddress, newFactAddress);
				addresses.add(newFactAddress);
			}
			edge.setAddressMap(fMap);
		}
		this.addresses = addresses.toArray(new FactAddress[addresses.size()]);
	}

	private static Template[] calculateAddressesAndTemplates(final Edge[] edgesToBeJoined) {
		final ArrayList<Template> template = new ArrayList<>();
		for (final Edge edge : edgesToBeJoined) {
			final org.jamocha.dn.memory.MemoryHandlerMain memoryHandlerMain =
					edge.getSourceNode().getMemory();
			for (final Template t : memoryHandlerMain.getTemplate()) {
				template.add(t);
			}
		}
		return template.toArray(new Template[template.size()]);
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
			final Filter filter) throws CouldNotAcquireLockException {
		return token.newBetaTemp(this, originIncomingEdge, filter);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInAlpha(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final Filter filter) throws CouldNotAcquireLockException {
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
