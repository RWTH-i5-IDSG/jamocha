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
package org.jamocha.dn;

import lombok.Getter;

import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.nodes.Node;

/**
 * The Network class encapsulates the central objects for {@link MemoryFactory}
 * and {@link Scheduler} which are required all over the whole discrimination
 * network.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 * @see MemoryFactory
 * @see Scheduler
 * @see Node
 */

@Getter
public class Network {

	/**
	 * The memoryFactory to generate the nodes {@link MemoryHandlerMain} and
	 * {@link MemoryHandlerTemp}.
	 * 
	 * @return The networks memory Factory.
	 */
	private final MemoryFactory memoryFactory;

	/**
	 * The capacity of the token queues in all token processing {@link Node
	 * nodes}.
	 */
	private final int tokenQueueCapacity;

	/**
	 * The scheduler handling the dispatching of token processing to different
	 * threads.
	 */
	private final Scheduler scheduler;

	/**
	 * Creates an new network object.
	 * 
	 * @param memoryFactory
	 *            the {@link MemoryFactory} to use in the created network.
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing
	 *            {@link Node nodes}.
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token
	 *            processing.
	 */
	public Network(final MemoryFactory memoryFactory, int tokenQueueCapacity,
			final Scheduler scheduler) {
		this.memoryFactory = memoryFactory;
		this.tokenQueueCapacity = tokenQueueCapacity;
		this.scheduler = scheduler;
	}

	/**
	 * A default network object with a basic setup, used for testing and other
	 * quick and dirty networks.
	 */
	public final static Network DEFAULTNETWORK = new Network(
			org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
			Integer.MAX_VALUE, new SchedulerThreadPool(10));

}