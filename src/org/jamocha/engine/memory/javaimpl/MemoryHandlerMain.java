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

import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.Template;

/**
 * @author Fabian Ohler
 * 
 */
public class MemoryHandlerMain implements
		org.jamocha.engine.memory.MemoryHandlerMain {
	static long tryLockTimeout = 1L;
	static TimeUnit tu = TimeUnit.SECONDS;

	final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	final ArrayList<Fact[]> facts = new ArrayList<>();
	final Template[] template;

	public MemoryHandlerMain(final MemoryHandler[] handlersToBeJoined) {
		this.template = handlersToTemplate(handlersToBeJoined);
	}

	private static Template[] handlersToTemplate(final MemoryHandler[] handlers) {
		final ArrayList<Template> templates = new ArrayList<>();
		for (final MemoryHandler mh : handlers) {
			for (final Template t : mh.getTemplate()) {
				templates.add(t);
			}
		}
		return templates.toArray(new Template[templates.size()]);
	}

	@Override
	public int size() {
		return this.facts.size();
	}

	@Override
	public Template[] getTemplate() {
		return template;
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
	public void add(final MemoryHandlerTemp toAdd){
		
	}

}
