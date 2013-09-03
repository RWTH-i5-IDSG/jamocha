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

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
@Getter
public class Network {
	
	private final MemoryFactory memoryFactory;
	private final int tokenQueueCapacity;
	private final int schedulerThreads;
	private final Scheduler scheduler;
	
	public Network(final MemoryFactory memoryFactory, final int tokenQueueCapacity, final int schedulerThreads) {
		this.memoryFactory = memoryFactory;
		this.tokenQueueCapacity = tokenQueueCapacity;
		this.schedulerThreads = schedulerThreads;
		this.scheduler = new Scheduler(schedulerThreads);
	}
	
	public final static Network DEFAULTNETWORK = new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
			Integer.MAX_VALUE, 10);

}