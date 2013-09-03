/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.engine.memory.javaimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jamocha.engine.memory.Template;
import org.jamocha.engine.nodes.Node.Edge;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class MemoryHandlerMain implements
		org.jamocha.engine.memory.MemoryHandlerMain {
	static final long tryLockTimeout = 1L;
	static final TimeUnit tu = TimeUnit.SECONDS;

	final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	final ArrayList<Fact[]> facts = new ArrayList<>();
	final Template[] template;
	final FactAddress[] addresses;

	public MemoryHandlerMain(final Template template, final Path... paths) {
		final FactAddress address = new FactAddress(0);
		this.addresses = new FactAddress[] { address };
		this.template = new Template[] { template };
		for (final Path path : paths) {
			PathTransformation.addressMapping.get(path)
					.setFactAddressInCurrentlyLowestNode(address);
		}
	}

	public MemoryHandlerMain(final Edge... inputsToBeJoined) {
		final ArrayList<Template> template = new ArrayList<>();
		final ArrayList<FactAddress> addresses = new ArrayList<>();
		for (final Edge input : inputsToBeJoined) {
			final HashMap<FactAddress, FactAddress> fMap = new HashMap<>();
			final MemoryHandlerMain memoryHandlerMain = (MemoryHandlerMain) input
					.getSourceNode().getMemory();
			for (int i = 0; i < memoryHandlerMain.template.length; i++) {
				final Template t = memoryHandlerMain.template[i];
				final FactAddress oldFactAddress = memoryHandlerMain.addresses[i];
				final FactAddress newFactAddress = new FactAddress(
						addresses.size());
				fMap.put(oldFactAddress, newFactAddress);
				template.add(t);
				addresses.add(newFactAddress);
			}
			input.setAddressMap(fMap);
		}
		this.template = template.toArray(new Template[template.size()]);
		this.addresses = addresses.toArray(new FactAddress[addresses.size()]);
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		return this.facts.size();
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandler#getTemplate()
	 */
	@Override
	public Template[] getTemplate() {
		return template;
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerMain#tryReadLock()
	 */
	@Override
	public boolean tryReadLock() throws InterruptedException {
		return this.lock.readLock().tryLock(tryLockTimeout, tu);
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerMain#releaseReadLock()
	 */
	@Override
	public void releaseReadLock() {
		this.lock.readLock().unlock();
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerMain#acquireWriteLock()
	 */
	@Override
	public void acquireWriteLock() {
		this.lock.writeLock().lock();
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerMain#releaseWriteLock()
	 */
	@Override
	public void releaseWriteLock() {
		this.lock.writeLock().unlock();
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerMain#add(MemoryHandlerTemp)
	 */
	@Override
	public void add(final MemoryHandlerTemp toAdd) {
		for (final Fact[] row : toAdd.facts) {
			this.facts.add(row);
		}
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandler#getValue(FactAddress,
	 *      SlotAddress, int)
	 */
	@Override
	public Object getValue(final org.jamocha.engine.memory.FactAddress address,
			final org.jamocha.engine.memory.SlotAddress slot, final int row) {
		return this.facts.get(row)[((org.jamocha.engine.memory.javaimpl.FactAddress) address)
				.getIndex()]
				.getValue((org.jamocha.engine.memory.javaimpl.SlotAddress) slot);
	}
}
