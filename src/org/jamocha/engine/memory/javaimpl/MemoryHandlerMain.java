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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jamocha.engine.memory.MemoryFactAddress;
import org.jamocha.engine.memory.SlotAddress;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.nodes.Node.Edge;

/**
 * @author Fabian Ohler
 * 
 */
public class MemoryHandlerMain implements
		org.jamocha.engine.memory.MemoryHandlerMain {
	static final long tryLockTimeout = 1L;
	static final TimeUnit tu = TimeUnit.SECONDS;

	final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	final ArrayList<Fact[]> facts = new ArrayList<>();
	final Template[] template;

	public MemoryHandlerMain(final Edge... inputsToBeJoined) {
		this.template = inputsToTemplate(inputsToBeJoined);
	}

	private static Template[] inputsToTemplate(final Edge[] inputs) {
		final ArrayList<Template> templates = new ArrayList<>();
		for (final Edge input : inputs) {
			for (final Template t : input.getSourceNode().getMemory()
					.getTemplate()) {
				input.setMemoryFactAddress(new org.jamocha.engine.memory.javaimpl.MemoryFactAddress(
						templates.size()));
				templates.add(t);
			}
		}
		return templates.toArray(new Template[templates.size()]);
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
	 * @see org.jamocha.engine.memory.MemoryHandler#getValue(MemoryFactAddress,
	 *      SlotAddress, int)
	 */
	@Override
	public Object getValue(final MemoryFactAddress address,
			final SlotAddress slot, final int row) {
		return this.facts.get(row)[((org.jamocha.engine.memory.javaimpl.MemoryFactAddress) address)
				.getIndex()]
				.getValue((org.jamocha.engine.memory.javaimpl.SlotAddress) slot);
	}
}
