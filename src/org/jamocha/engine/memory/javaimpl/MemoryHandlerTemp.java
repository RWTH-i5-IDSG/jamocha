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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.jamocha.engine.memory.MemoryFactAddress;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.SlotAddress;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.nodes.NetworkAddress;
import org.jamocha.engine.nodes.NetworkFactAddress;
import org.jamocha.engine.nodes.Node.NodeInput;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;
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
	final ArrayList<Fact[]> facts;
	final Semaphore lock;
	boolean valid;

	public MemoryHandlerTemp(final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerTemp token, final NodeInput originInput,
			final Filter filter) throws InterruptedException {
		super();
		this.originatingMainHandler = originatingMainHandler;
		this.lock = new Semaphore(originInput.getTargetNode().numChildren());
		this.facts = performJoin(filter, token, originInput);
		this.valid = true;
	}

	static class StackElement {
		int rowIndex;
		int memIndex;
		final ArrayList<ArrayList<Fact[]>> memStack;

		private StackElement(final ArrayList<ArrayList<Fact[]>> memStack) {
			this.memStack = memStack;
		}

		public static StackElement ordinaryInput(final NodeInput input) {
			final LinkedList<? extends MemoryHandler> temps = input
					.getTempMemories();

			final ArrayList<ArrayList<Fact[]>> memStack = new ArrayList<ArrayList<Fact[]>>(
					temps.size() + 1);
			memStack.add(((org.jamocha.engine.memory.javaimpl.MemoryHandlerMain) input
					.getSourceNode().getMemory()).facts);
			for (final MemoryHandler temp : temps) {
				memStack.add(((org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp) temp).facts);
			}
			return new StackElement(memStack);
		}

		public static StackElement originInput(final MemoryHandlerTemp token) {
			ArrayList<ArrayList<Fact[]>> list = new ArrayList<ArrayList<Fact[]>>(
					1);
			list.add(((org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp) token).facts);
			return new StackElement(list);
		}

		ArrayList<Fact[]> getTable() {
			return this.memStack.get(this.memIndex);
		}

		Object getValue(final MemoryFactAddress fact, final SlotAddress slot) {
			return this.getTable().get(this.rowIndex)[((org.jamocha.engine.memory.javaimpl.MemoryFactAddress) fact).index]
					.getValue(((org.jamocha.engine.memory.javaimpl.SlotAddress) slot));
		}

		boolean checkMemBounds() {
			return memStack.size() > memIndex && memIndex >= 0;
		}

		boolean checkRowBounds() {
			return getTable().size() > rowIndex && rowIndex >= 0;
		}

	}

	private static ArrayList<Fact[]> performJoin(final Filter filter,
			final MemoryHandlerTemp token, final NodeInput originInput)
			throws InterruptedException {
		// get a fixed-size array of indices (size: #inputs of the node),
		// determine number of inputs for the current join as maxIndex
		// loop through the inputs line-wise using array[0] .. array[maxIndex]
		// as line indices, incrementing array[maxIndex] and propagating the
		// increment to lower indices when input-size is reached

		// set locks and create stack
		final NodeInput[] nodeInputs = originInput.getTargetNode().getInputs();
		final LinkedHashMap<NodeInput, StackElement> inputToStack = new LinkedHashMap<>();
		for (final NodeInput input : nodeInputs) {
			if (input == originInput) {
				continue;
			}
			if (!input.getSourceNode().getMemory().tryReadLock()) {
				// FIXME throw some exception hinting the user to return
				// the join job to the global queue
			}
			inputToStack.put(input, StackElement.ordinaryInput(input));
		}
		inputToStack.put(originInput, StackElement.originInput(token));

		// get filter steps
		final FilterElement filterSteps[] = filter.getFilterSteps();

		final Collection<StackElement> stack = inputToStack.values();
		for (final FilterElement filterElement : filterSteps) {
			final FunctionWithArguments function = filterElement.getFunction();
			final NetworkAddress addresses[] = filterElement.getAddresses();

			final int paramLength = addresses.length;
			final Object params[] = new Object[paramLength];

			outerloop: while (true) {
				innerloop: while (true) {
					for (int i = 0; i < paramLength; ++i) {
						final NetworkAddress address = addresses[i];
						final NetworkFactAddress fact = address
								.getNetworkFactAddress();
						final StackElement se = inputToStack.get(fact
								.getNodeInput());
						params[i] = se.getValue(fact.getMemoryFactAddress(),
								address.getSlotAddress());
					}
					if ((boolean) function.evaluate(params)) {
						// TODO Copy the result to the new temporary result
					}

					for (final Iterator<StackElement> iter = stack.iterator(); iter
							.hasNext();) {
						final StackElement element = iter.next();
						element.rowIndex++;
						if (element.checkRowBounds())
							break;
						if (!iter.hasNext())
							break innerloop;
						element.rowIndex = 0;
					}
				}
				for (final Iterator<StackElement> iter = stack.iterator(); iter
						.hasNext();) {
					final StackElement element = iter.next();
					element.memIndex++;
					if (element.checkMemBounds())
						break;
					if (!iter.hasNext())
						break outerloop;
					element.memIndex = 0;
				}
				// TODO point all inputs that were joint during this turn to the TR StackElement
				// TODO overwrite TR with current temporary result and reset all row- and mem-indices
			}
			for (final NodeInput input : nodeInputs) {
				input.getSourceNode().getMemory().releaseReadLock();
			}
			// TODO full join with all inputs not pointing to TR now
		}
		return null;
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

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerTemp#releaseLock()
	 */
	@Override
	public void releaseLock() {
		if (this.lock.release())
			return;
		// all children have processed the temp memory, now we have to write its
		// content to main memory
		originatingMainHandler.add(this);

	}

	@Override
	public Template[] getTemplate() {
		return this.originatingMainHandler.getTemplate();
	}

	@Override
	public Object getValue(final MemoryFactAddress address,
			final SlotAddress slot, final int row) {
		return this.facts[row][((org.jamocha.engine.memory.javaimpl.MemoryFactAddress) address)
				.getIndex()]
				.getValue((org.jamocha.engine.memory.javaimpl.SlotAddress) slot);
	}

}
