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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.SlotAddress;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.nodes.AddressPredecessor;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.Node.Edge;
import org.jamocha.engine.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.FunctionWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
	boolean valid = true;

	static MemoryHandlerTemp newBetaTemp(
			final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerTemp token, final Edge originInput,
			final Filter filter) throws InterruptedException {
		return new MemoryHandlerTemp(originatingMainHandler, performJoin(
				originatingMainHandler, filter, token, originInput),
				new Semaphore(originInput.getTargetNode().numChildren()));
	}

	static MemoryHandlerTemp newAlphaTemp(
			final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerTemp token, final Node alphaNode,
			final Filter filter) throws InterruptedException {
		final ArrayList<Fact[]> factList = new ArrayList<>(1);
		factLoop: for (final Fact[] fact : token.facts) {
			assert fact.length == 1;
			for (final FilterElement filterElement : filter.getFilterElements()) {
				// determine parameters
				final SlotInFactAddress addresses[] = filterElement
						.getAddressesInTarget();
				final int paramLength = addresses.length;
				final Object params[] = new Object[paramLength];
				for (int i = 0; i < paramLength; ++i) {
					final SlotInFactAddress address = addresses[i];
					params[i] = fact[0].getValue(address.getSlotAddress());
				}
				// check filter
				if (false == (Boolean) filterElement.getFunction().evaluate(
						params)) {
					continue factLoop;
				}
			}
			factList.add(fact);
		}
		return new MemoryHandlerTemp(originatingMainHandler, factList,
				new Semaphore(alphaNode.numChildren()));
	}

	static MemoryHandlerTemp newRootTemp(
			final MemoryHandlerMain originatingMainHandler, final Node otn,
			final org.jamocha.engine.memory.Fact... facts)
			throws InterruptedException {
		final ArrayList<Fact[]> factList = new ArrayList<>();
		for (org.jamocha.engine.memory.Fact fact : facts) {
			factList.add(new Fact[] { new Fact(fact.getSlotValues()) });
		}
		return new MemoryHandlerTemp(originatingMainHandler, factList,
				new Semaphore(otn.numChildren()));
	}

	static abstract class StackElement {
		int rowIndex;
		int memIndex;
		final ArrayList<ArrayList<Fact[]>> memStack;
		final int offset;

		private StackElement(final ArrayList<ArrayList<Fact[]>> memStack,
				final int offset) {
			this.memStack = memStack;
			this.offset = offset;
		}

		public static StackElement ordinaryInput(final Edge edge,
				final int offset) {
			final LinkedList<? extends MemoryHandler> temps = edge
					.getTempMemories();
			final ArrayList<ArrayList<Fact[]>> memStack = new ArrayList<ArrayList<Fact[]>>(
					temps.size() + 1);
			memStack.add(((org.jamocha.engine.memory.javaimpl.MemoryHandlerMain) edge
					.getSourceNode().getMemory()).facts);
			for (final MemoryHandler temp : temps) {
				memStack.add(((org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp) temp).facts);
			}
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr,
						final SlotAddress slot) {
					return this.getRow()[((org.jamocha.engine.memory.javaimpl.FactAddress) addr
							.getAddress()).index]
							.getValue(((org.jamocha.engine.memory.javaimpl.SlotAddress) slot));
				}
			};
		}

		public static StackElement originInput(int columns,
				final Edge originEdge, final MemoryHandlerTemp token,
				final int offset) {
			final org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp temp = (org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp) token;
			final ArrayList<Fact[]> listWithHoles = new ArrayList<>(
					temp.facts.size());
			for (final Fact[] facts : temp.facts) {
				final Fact[] row = new Fact[columns];
				System.arraycopy(facts, 0, row, offset, facts.length);
				listWithHoles.add(row);
			}
			final ArrayList<ArrayList<Fact[]>> memStack = new ArrayList<ArrayList<Fact[]>>(
					1);
			memStack.add(listWithHoles);
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr,
						final SlotAddress slot) {
					return this.getRow()[((org.jamocha.engine.memory.javaimpl.FactAddress) addr
							.getEdge().localizeAddress(addr.getAddress())).index]
							.getValue(((org.jamocha.engine.memory.javaimpl.SlotAddress) slot));
				}
			};
		}

		ArrayList<Fact[]> getTable() {
			return this.memStack.get(this.memIndex);
		}

		Fact[] getRow() {
			return this.memStack.get(this.memIndex).get(this.rowIndex);
		}

		abstract Object getValue(final AddressPredecessor addr,
				final SlotAddress slot);

		boolean checkMemBounds() {
			return memStack.size() > memIndex && memIndex >= 0;
		}

		boolean checkRowBounds() {
			return getTable().size() > rowIndex && rowIndex >= 0;
		}

		void resetIndices() {
			this.rowIndex = 0;
			this.memIndex = 0;
		}

		int getOffset() {
			return this.offset;
		}

	}

	static interface FunctionPointer {
		public void apply(final ArrayList<Fact[]> TR,
				final Collection<StackElement> stack,
				final StackElement originElement);
	}

	private static void loop(final FunctionPointer functionPointer,
			final Collection<StackElement> stack,
			final StackElement originElement) {
		if (stack.isEmpty())
			return;
		final ArrayList<Fact[]> TR = new ArrayList<Fact[]>();
		outerloop: while (true) {
			innerloop: while (true) {
				functionPointer.apply(TR, stack, originElement);
				// increment row indices
				for (final Iterator<StackElement> iter = stack.iterator(); iter
						.hasNext();) {
					final StackElement element = iter.next();
					element.rowIndex++;
					if (element.checkRowBounds())
						break;
					element.rowIndex = 0;
					if (!iter.hasNext())
						break innerloop;
				}
			}
			// increment memory indices
			for (final Iterator<StackElement> iter = stack.iterator(); iter
					.hasNext();) {
				final StackElement element = iter.next();
				element.memIndex++;
				if (element.checkMemBounds())
					break;
				element.memIndex = 0;
				if (!iter.hasNext())
					break outerloop;
			}
		}
		// reset all indices in the StackElements
		for (final StackElement elem : stack) {
			elem.resetIndices();
		}
		// replace TR in originElement with new temporary result
		originElement.memStack.set(0, TR);
	}

	private static ArrayList<Fact[]> performJoin(
			final MemoryHandlerMain originatingMainHandler,
			final Filter filter, final MemoryHandlerTemp token,
			final Edge originInput) throws InterruptedException {
		// get a fixed-size array of indices (size: #inputs of the node),
		// determine number of inputs for the current join as maxIndex
		// loop through the inputs line-wise using array[0] .. array[maxIndex]
		// as line indices, incrementing array[maxIndex] and propagating the
		// increment to lower indices when input-size is reached

		// set locks and create stack
		final Edge[] nodeInputs = originInput.getTargetNode().getIncomingEdges();
		final LinkedHashMap<Edge, StackElement> inputToStack = new LinkedHashMap<>();
		final int columns = originInput.getTargetNode().getMemory()
				.getTemplate().length;
		StackElement tempOriginElement = null;
		final StackElement originElement;
		int offset = 0;
		for (final Edge input : nodeInputs) {
			if (input == originInput) {
				tempOriginElement = StackElement.originInput(columns,
						originInput, token, offset);
				offset += input.getSourceNode().getMemory().getTemplate().length;
				// don't lock the originInput
				continue;
			}
			if (!input.getSourceNode().getMemory().tryReadLock()) {
				throw new Error();
				// FIXME throw some exception hinting the user to return
				// the join job to the global queue
			}
			inputToStack.put(input, StackElement.ordinaryInput(input, offset));
			offset += input.getSourceNode().getMemory().getTemplate().length;
		}
		originElement = tempOriginElement;
		inputToStack.put(originInput, originElement);
		final Node targetNode = originInput.getTargetNode();

		// get filter steps
		final FilterElement filterSteps[] = filter.getFilterElements();

		for (final FilterElement filterElement : filterSteps) {
			final Collection<StackElement> stack = new ArrayList<>(
					filterSteps.length);
			final FunctionWithArguments function = filterElement.getFunction();
			final SlotInFactAddress addresses[] = filterElement
					.getAddressesInTarget();

			// determine new edges
			final Set<Edge> newEdges = new HashSet<>();
			for (final SlotInFactAddress address : addresses) {
				final Edge edge = targetNode.delocalizeAddress(
						address.getFactAddress()).getEdge();
				final StackElement element = inputToStack.get(edge);
				if (element != originElement) {
					if (newEdges.add(edge)) {
						stack.add(element);
					}
				}
			}

			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Fact[]> TR,
						final Collection<StackElement> stack,
						final StackElement originElement) {
					final int paramLength = addresses.length;
					final Object params[] = new Object[paramLength];
					// determine parameters
					for (int i = 0; i < paramLength; ++i) {
						final SlotInFactAddress address = addresses[i];
						final AddressPredecessor fact = targetNode.delocalizeAddress(address
								.getFactAddress());
						final StackElement se = inputToStack.get(fact.getEdge());
						params[i] = se.getValue(fact, address.getSlotAddress());
					}
					// copy result to new TR if facts match predicate
					if ((boolean) function.evaluate(params)) {
						// copy current row from old TR
						final Fact[] row = originElement.getRow();
						// insert information from new inputs
						for (final Edge edge : newEdges) {
							// source is some temp, destination new TR
							final StackElement se = inputToStack.get(edge);
							final Fact[] newRowPart = se.getRow();
							System.arraycopy(newRowPart, 0, row,
									se.getOffset(), newRowPart.length);
						}
						// copy the result to new TR
						TR.add(row);
					}
				}
			}, stack, originElement);
			// point all inputs that were joint during this turn to the TR
			// StackElement
			for (final Edge input : newEdges) {
				inputToStack.put(input, originElement);
			}
		}

		// full join with all inputs not pointing to TR now
		for (final Map.Entry<Edge, StackElement> entry : inputToStack
				.entrySet()) {
			if (entry.getValue() == originElement)
				continue;
			final Edge nodeInput = entry.getKey();
			final StackElement se = entry.getValue();
			final Collection<StackElement> stack = Arrays.asList(originElement,
					se);
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Fact[]> TR,
						final Collection<StackElement> stack,
						final StackElement originElement) {
					// copy result to new TR
					// copy current row from old TR
					final Fact[] row = originElement.getRow();
					// insert information from new input
					// source is some temp, destination new TR
					final Fact[] newRowPart = se.getRow();
					System.arraycopy(newRowPart, 0, row, se.getOffset(),
							newRowPart.length);
					// copy the result to new TR
					TR.add(row);
				}
			}, stack, originElement);
			// point all inputs that were joint during this turn to the TR
			// StackElement
			inputToStack.put(nodeInput, originElement);
		}
		// release lock
		for (final Edge input : nodeInputs) {
			if (input == originInput)
				continue;
			input.getSourceNode().getMemory().releaseReadLock();
		}
		return originElement.getTable();
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		return this.facts.size();
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
		// TODO invalidate and use the invalidation info at corresponding points
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandlerMain#getTemplate()
	 */
	@Override
	public Template[] getTemplate() {
		return this.originatingMainHandler.getTemplate();
	}

	/**
	 * @see org.jamocha.engine.memory.MemoryHandler#getValue(FactAddress,
	 *      SlotAddress, int)
	 */
	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot,
			final int row) {
		return this.facts.get(row)[((org.jamocha.engine.memory.javaimpl.FactAddress) address)
				.getIndex()]
				.getValue((org.jamocha.engine.memory.javaimpl.SlotAddress) slot);
	}
}
