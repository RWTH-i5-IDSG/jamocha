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
package org.jamocha.dn;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jamocha.dn.nodes.Node.TokenQueue;

/**
 * {@link Scheduler} to process {@link Runnable Runnables} using a thread pool of fixed size.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 * @see Executors#newFixedThreadPool(int)
 */
public class ThreadPoolScheduler implements Scheduler {

	final Executor executor;
	final AtomicLong unfinishedJobs = new AtomicLong();

	final Lock lock = new ReentrantLock();
	final Condition empty = lock.newCondition();

	/**
	 * Creates a scheduler with a thread pool with the given size.
	 * 
	 * @param nThreads
	 *            the size of the thread pool
	 */
	public ThreadPoolScheduler(final int nThreads) {
		this.executor = Executors.newFixedThreadPool(nThreads);
	}

	@Override
	public void enqueue(final TokenQueue runnable) {
		this.executor.execute(runnable);
	}

	@Override
	public void signalNewJob() {
		this.unfinishedJobs.incrementAndGet();
	}

	@Override
	public void signalFinishedJob() {
		this.lock.lock();
		try {
			this.unfinishedJobs.decrementAndGet();
			if (!hasUnfinishedJobs()) {
				this.empty.signal();
			}
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public boolean hasUnfinishedJobs() {
		return 0L != unfinishedJobs.longValue();
	}

	@Override
	public void waitForNoUnfinishedJobs() {
		this.lock.lock();
		while (hasUnfinishedJobs()) {
			try {
				this.empty.await();
				return;
			} catch (final InterruptedException e) {
				// ignore
			} finally {
				this.lock.unlock();
			}
		}
	}
}