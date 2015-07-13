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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jamocha.dn.nodes.Node.TokenQueue;

/**
 * {@link Scheduler} to process {@link Runnable Runnables} using a thread pool of fixed size.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see Executors#newFixedThreadPool(int)
 */
public class ThreadPoolScheduler implements Scheduler {

	ExecutorService executor;
	final int nThreads;
	final AtomicLong unfinishedJobs = new AtomicLong();

	final Lock lock = new ReentrantLock();
	final Condition empty = lock.newCondition();

	private interface State {
		void enqueue(final TokenQueue runnable);

		void pushJobs();
	}

	final private State ACTIVE_STATE = new State() {

		@Override
		public void enqueue(final TokenQueue runnable) {
			unfinishedJobs.incrementAndGet();
			executor.execute(() -> {
				runnable.run();
				final long newCounter = unfinishedJobs.decrementAndGet();
				if (!hasUnfinishedJobs(newCounter)) {
					lock.lock();
					try {
						empty.signal();
					} finally {
						lock.unlock();
					}
				}
			});
		}

		@Override
		public void pushJobs() {
		}

	};

	private State state = ACTIVE_STATE;

	/**
	 * Creates a scheduler with a thread pool with the given size.
	 *
	 * @param nThreads
	 *            the size of the thread pool
	 */
	public ThreadPoolScheduler(final int nThreads) {
		this.nThreads = nThreads;
		this.executor = createExecutor();
	}

	private ExecutorService createExecutor() {
		return Executors.newFixedThreadPool(nThreads);
	}

	@Override
	public void enqueue(final TokenQueue runnable) {
		state.enqueue(runnable);
	}

	@Override
	public void activate() {
		final State oldState = state;
		state = ACTIVE_STATE;
		if (null == executor)
			executor = createExecutor();
		// push jobs collected during inactivity
		oldState.pushJobs();
	}

	@Override
	public void deactivate() {
		// check if state is active
		if (ACTIVE_STATE != state)
			return;
		// shutdown executor service and save queue
		@SuppressWarnings("unchecked")
		final List<TokenQueue> oldQueue = (List<TokenQueue>) (List<? extends Runnable>) this.executor.shutdownNow();
		// create inactive state with new queue
		state = new State() {

			final Queue<TokenQueue> queue = new LinkedList<>();

			synchronized State enqueueAll(final Collection<TokenQueue> runnables) {
				queue.addAll(runnables);
				return this;
			}

			@Override
			public synchronized void enqueue(final TokenQueue runnable) {
				queue.add(runnable);
			}

			@Override
			public synchronized void pushJobs() {
				for (final TokenQueue tokenQueue : queue) {
					ThreadPoolScheduler.this.enqueue(tokenQueue);
				}
			}

		}
				// enqueue all jobs from old queue
				.enqueueAll(oldQueue);
		// lower unfinishedJobs counter by jobs from old queue
		unfinishedJobs.addAndGet(-1 * oldQueue.size());
		// only return if scheduler is idle
		waitForNoUnfinishedJobs();
	}

	private static boolean hasUnfinishedJobs(final long counter) {
		return 0L != counter;
	}

	@Override
	public void shutdown() {
		this.executor.shutdown();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TokenQueue> shutdownNow() {
		return (List<TokenQueue>) (List<? extends Runnable>) this.executor.shutdownNow();
	}

	@Override
	public void waitForNoUnfinishedJobs() {
		this.lock.lock();
		try {
			while (hasUnfinishedJobs(unfinishedJobs.longValue())) {
				try {
					this.empty.await();
				} catch (final InterruptedException e) {
					// ignore
				}
			}
		} finally {
			this.lock.unlock();
		}
	}
}