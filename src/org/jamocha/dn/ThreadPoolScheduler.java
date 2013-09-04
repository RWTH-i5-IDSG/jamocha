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

/**
 * {@link Scheduler} to process {@link Runnable Runnables} using a thread pool of fixed size.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 * @see Executors#newFixedThreadPool(int)
 */
public class ThreadPoolScheduler implements Scheduler {

	final Executor executor;

	/**
	 * Creates a scheduler with a thread pool with the given size.
	 * 
	 * @param nThreads
	 *            the size of the thread pool
	 */
	public ThreadPoolScheduler(int nThreads) {
		executor = Executors.newFixedThreadPool(nThreads);
	}

	@Override
	public void enqueue(Runnable runnable) {
		executor.execute(runnable);
	}

}