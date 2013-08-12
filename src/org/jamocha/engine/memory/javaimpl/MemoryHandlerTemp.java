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

import org.jamocha.filter.ConstantLeaf;
import org.jamocha.filter.Filter;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.FunctionWithArgumentsComposite;
import org.jamocha.filter.FunctionWithArgumentsVisitor;
import org.jamocha.filter.PathLeaf;
import org.jamocha.filter.PathLeaf.AddressLeaf;

/**
 * @author Fabian Ohler
 * 
 */
public class MemoryHandlerTemp implements
		org.jamocha.engine.memory.MemoryHandlerTemp {

	final Fact facts[][];

	public MemoryHandlerTemp(final Filter filter) {
		super();
		this.facts = null;
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

	static interface TR {

	}

	static class Joiner implements FunctionWithArgumentsVisitor<TR> {
		int depth = 0;

		@Override
		public TR visit(final FunctionWithArgumentsComposite function,
				final TR proxy) {
			// TODO join as described above
			++depth;
			if (1 == depth) {
				// predicate behaviour
			} else {
				// function behaviour
			}
			return null;
		}

		@Override
		public TR visit(final PathLeaf function, final TR proxy) {
			throw new UnsupportedOperationException(
					"Joins can not be performed on PathLeafs!");
		}

		@Override
		public TR visit(final AddressLeaf function, final TR proxy) {
			return null;
			// TODO Auto-generated method stub
		}

		@Override
		public TR visit(final ConstantLeaf function, final TR proxy) {
			return null;
			// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

}
