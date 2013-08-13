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

import org.jamocha.engine.memory.Template;
import org.jamocha.filter.Filter;
import org.jamocha.filter.FunctionWithArguments;

/**
 * @author Fabian Ohler
 * 
 */
public class MemoryHandlerTemp implements
		org.jamocha.engine.memory.MemoryHandlerTemp {

	static class Semaphore {
		int count;

		public Semaphore(int count) {
			super();
			this.count = count;
		}
		
		public synchronized boolean release() {
			return --count != 0;
		}
	}
	
	final MemoryHandlerMain originatingMainHandler;
	final Fact facts[][];
	final Semaphore lock;
	boolean valid;

	public MemoryHandlerTemp(final MemoryHandlerMain originatingMainHandler,
			final Filter filter) {
		super();
		this.originatingMainHandler = originatingMainHandler;
		this.lock = new Semaphore(numberOfChildren);
		this.facts = null;
		this.valid = true;
		// TODO visit the elements of the list in the filter (visitor-pattern)
		// as the number of inputs needed for a join may vary, we emulate
		// recursive joining:
		// get a fixed-size array of indices (size: #inputs of the node),
		// determine number of inputs for the current join as maxIndex
		// loop through the inputs line-wise using array[0] .. array[maxIndex]
		// as line indices, incrementing array[maxIndex] and propagating the
		// increment to lower indices when input-size is reached
		final FunctionWithArguments[] filterSteps = filter.getFilterSteps();
		for (final FunctionWithArguments fwa : filterSteps) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.engine.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.engine.memory.MemoryHandlerTemp#releaseLock()
	 */
	@Override
	public void releaseLock() {
		if (this.lock.release()) return;
		// all children have processed the temp memory, now we have to write its content to main memory
		originatingMainHandler.add(this);
		
	}

	@Override
	public Template[] getTemplate() {
		return this.originatingMainHandler.getTemplate();
	}

}
