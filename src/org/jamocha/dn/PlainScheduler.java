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

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@link Scheduler} to process {@linkplain Runnable runnables} in order of arrival in only one
 * {@link Thread thread}. The Scheduler has to be started by calling {@link #run()}.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class PlainScheduler implements Scheduler, Runnable {

	final Queue<Runnable> workQueue = new LinkedList<>();

	@Override
	public void enqueue(final Runnable runnable) {
		this.workQueue.add(runnable);
	}

	/**
	 * Processes all enqueued {@link Runnable Runnables} in order of arrival and return when queue
	 * is empty.
	 */
	@Override
	public void run() {
		while (!this.workQueue.isEmpty()) {
			this.workQueue.poll().run();
		}
	}

}
