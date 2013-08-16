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
import java.util.LinkedHashSet;
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

	static abstract class StackElement {
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
			return new StackElement(memStack) {
				@Override
				Object getValue(final NetworkFactAddress addr,
						final SlotAddress slot) {
					return this.getTable().get(this.rowIndex)[((org.jamocha.engine.memory.javaimpl.MemoryFactAddress) addr
							.getNodeInput().getTargetNode()
							.delocalizeAddress(addr)
							.getMemoryFactAddressInTarget()).index]
							.getValue(((org.jamocha.engine.memory.javaimpl.SlotAddress) slot));
				}
			};
		}

		public static StackElement originInput(final MemoryHandlerTemp token) {
			ArrayList<ArrayList<Fact[]>> list = new ArrayList<ArrayList<Fact[]>>(
					1);
			list.add(((org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp) token).facts);
			return new StackElement(list) {
				@Override
				Object getValue(final NetworkFactAddress addr,
						final SlotAddress slot) {
					return this.getTable().get(this.rowIndex)[((org.jamocha.engine.memory.javaimpl.MemoryFactAddress) addr
							.getMemoryFactAddressInTarget()).index]
							.getValue(((org.jamocha.engine.memory.javaimpl.SlotAddress) slot));
				}
			};
		}

		ArrayList<Fact[]> getTable() {
			return this.memStack.get(this.memIndex);
		}

		abstract Object getValue(final NetworkFactAddress addr,
				final SlotAddress slot);

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
		final int columns = originInput.getTargetNode().getMemory()
				.getTemplate().length;
		final ArrayList<Fact[]> TR = new ArrayList<Fact[]>();

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
						params[i] = se.getValue(fact, address.getSlotAddress());
					}
					if ((boolean) function.evaluate(params)) {
						// copy result to new TR
						final Fact[] row = new Fact[columns];
						// TODO Copy the result to the new temporary result
						TR.add(row);
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
				// point all inputs that were joint during this turn to the TR
				// StackElement
				final StackElement originElement = inputToStack
						.get(originInput);
				for (final NetworkAddress address : addresses) {
					final NodeInput input = address.getNetworkFactAddress()
							.getNodeInput();
					if (inputToStack.get(input) == originElement)
						continue;
					inputToStack.put(input, originElement);
				}
				// TODO replace TR in originElement with new temporary result
				// reset all indices in the StackElements
				for (final StackElement elem : stack) {
					elem.rowIndex = 0;
					elem.memIndex = 0;
				}
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
		return this.facts.get(row)[((org.jamocha.engine.memory.javaimpl.MemoryFactAddress) address)
				.getIndex()]
				.getValue((org.jamocha.engine.memory.javaimpl.SlotAddress) slot);
	}

	public static void main(String[] args) {
		final LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();
		map.put(Integer.valueOf(1), Integer.valueOf(10));
		map.put(Integer.valueOf(2), Integer.valueOf(20));
		map.put(Integer.valueOf(3), Integer.valueOf(30));
		map.put(Integer.valueOf(4), Integer.valueOf(40));
		map.put(Integer.valueOf(5), Integer.valueOf(50));
		System.out.println(map.values());
		map.put(Integer.valueOf(2), map.get(Integer.valueOf(1)));
		System.out.println(new LinkedHashSet<Integer>(map.values()));
	}

}
