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
package org.jamocha.dn.memory.javaimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.nodes.AddressPredecessor;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.FunctionWithArguments;

/**
 * Java-implementation of the {@link org.jamocha.dn.memory.MemoryHandlerPlusTemp} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp
 */
public class MemoryHandlerPlusTemp extends MemoryHandlerTemp implements
		org.jamocha.dn.memory.MemoryHandlerPlusTemp {

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

	final Semaphore lock;
	boolean valid = true;

	private MemoryHandlerPlusTemp(final MemoryHandlerMain originatingMainHandler,
			final List<Fact[]> facts, final Semaphore lock) {
		super(originatingMainHandler, facts);
		this.lock = lock;
		if (facts.size() == 0) {
			this.valid = false;
		} else if (lock.count == 0) {
			commitAndInvalidate();
		}
	}

	static MemoryHandlerPlusTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerPlusTemp token, final Edge originIncomingEdge, final Filter filter)
			throws CouldNotAcquireLockException {
		return new MemoryHandlerPlusTemp(originatingMainHandler, performJoin(
				originatingMainHandler, filter, token, originIncomingEdge), new Semaphore(
				originIncomingEdge.getTargetNode().getNumberOfOutgoingEdges()));
	}

	@Override
	public MemoryHandlerPlusTemp newBetaTemp(
			org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			Edge originIncomingEdge, Filter filter) throws CouldNotAcquireLockException {
		return newBetaTemp((MemoryHandlerMain) originatingMainHandler, this, originIncomingEdge,
				filter);
	}

	static MemoryHandlerPlusTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerPlusTemp token, final Edge originIncomingEdge, final Filter filter)
			throws CouldNotAcquireLockException {
		final ArrayList<Fact[]> factList = new ArrayList<>(1);
		factLoop: for (final Fact[] fact : token.facts) {
			assert fact.length == 1;
			for (final FilterElement filterElement : filter.getFilterElements()) {
				if (!applyFilterElement(fact[0], filterElement)) {
					continue factLoop;
				}
			}
			factList.add(fact);
		}
		// FIXME only use Semaphores if one of the outgoing edges is connected to the beta network
		return new MemoryHandlerPlusTemp(originatingMainHandler, factList, new Semaphore(
				originIncomingEdge.getTargetNode().getNumberOfOutgoingEdges()));
	}

	@Override
	public MemoryHandlerPlusTemp newAlphaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException {
		return newAlphaTemp((MemoryHandlerMain) originatingMainHandler, this, originIncomingEdge,
				filter);
	}

	static MemoryHandlerPlusTemp newRootTemp(final MemoryHandlerMain originatingMainHandler,
			final Node otn, final org.jamocha.dn.memory.Fact... facts) {
		final ArrayList<Fact[]> factList = new ArrayList<>();
		for (org.jamocha.dn.memory.Fact fact : facts) {
			factList.add(new Fact[] { new Fact(fact.getSlotValues()) });
		}
		return new MemoryHandlerPlusTemp(originatingMainHandler, factList, new Semaphore(
				otn.getNumberOfOutgoingEdges()));
	}

	static abstract class StackElement {
		int rowIndex;
		int memIndex;
		final List<List<Fact[]>> memStack;
		final int offset;

		private StackElement(final List<List<Fact[]>> memStack, final int offset) {
			this.memStack = memStack;
			this.offset = offset;
		}

		public static StackElement ordinaryInput(final Edge edge, final int offset) {
			final LinkedList<? extends MemoryHandler> temps = edge.getTempMemories();
			final List<List<Fact[]>> memStack = new ArrayList<List<Fact[]>>(temps.size() + 1);
			memStack.add(((org.jamocha.dn.memory.javaimpl.MemoryHandlerMain) edge.getSourceNode()
					.getMemory()).facts);
			for (Iterator<? extends MemoryHandler> iter = temps.iterator(); iter.hasNext();) {
				final MemoryHandlerPlusTemp temp = (MemoryHandlerPlusTemp) iter.next();
				if (!temp.valid) {
					iter.remove();
					continue;
				}
				memStack.add(((org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp) temp).facts);
			}
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
					return this.getRow()[((org.jamocha.dn.memory.javaimpl.FactAddress) addr
							.getAddress()).index]
							.getValue(((org.jamocha.dn.memory.javaimpl.SlotAddress) slot));
				}
			};
		}

		public static StackElement originInput(int columns, final Edge originEdge,
				final MemoryHandlerPlusTemp token, final int offset) {
			final org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp temp =
					(org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp) token;
			final ArrayList<Fact[]> listWithHoles = new ArrayList<>(temp.facts.size());
			for (final Fact[] facts : temp.facts) {
				final Fact[] row = new Fact[columns];
				assert columns >= offset + facts.length;
				System.arraycopy(facts, 0, row, offset, facts.length);
				listWithHoles.add(row);
			}
			final List<List<Fact[]>> memStack = new ArrayList<List<Fact[]>>(1);
			memStack.add(listWithHoles);
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
					return this.getRow()[((org.jamocha.dn.memory.javaimpl.FactAddress) addr
							.getEdge().localizeAddress(addr.getAddress())).index]
							.getValue(((org.jamocha.dn.memory.javaimpl.SlotAddress) slot));
				}
			};
		}

		List<Fact[]> getTable() {
			return this.memStack.get(this.memIndex);
		}

		Fact[] getRow() {
			return this.memStack.get(this.memIndex).get(this.rowIndex);
		}

		abstract Object getValue(final AddressPredecessor addr, final SlotAddress slot);

		boolean checkMemBounds() {
			return memStack.size() > memIndex && memIndex >= 0;
		}

		boolean checkRowBounds() {
			return checkMemBounds() && getTable().size() > rowIndex && rowIndex >= 0;
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
		public void apply(final ArrayList<Fact[]> TR, final Collection<StackElement> stack,
				final StackElement originElement);
	}

	private static void loop(final FunctionPointer functionPointer,
			final Collection<StackElement> stack, final StackElement originElement) {
		if (stack.isEmpty() || stack.size() == 1) {
			return;
		}
		{
			final Iterator<StackElement> iter = stack.iterator();
			// skip originElement
			iter.next();
			for (; iter.hasNext();) {
				final StackElement element = iter.next();
				while (!element.checkRowBounds()) {
					if (!element.checkMemBounds())
						return;
					element.memIndex++;
				}
			}
		}
		final ArrayList<Fact[]> TR = new ArrayList<Fact[]>();
		outerloop: while (true) {
			innerloop: while (true) {
				functionPointer.apply(TR, stack, originElement);
				// increment row indices
				for (final Iterator<StackElement> iter = stack.iterator(); iter.hasNext();) {
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
			for (final Iterator<StackElement> iter = stack.iterator(); iter.hasNext();) {
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

	private static List<Fact[]> performJoin(final MemoryHandlerMain originatingMainHandler,
			final Filter filter, final MemoryHandlerPlusTemp token, final Edge originIncomingEdge)
			throws CouldNotAcquireLockException {
		// get a fixed-size array of indices (size: #inputs of the node),
		// determine number of inputs for the current join as maxIndex
		// loop through the inputs line-wise using array[0] .. array[maxIndex]
		// as line indices, incrementing array[maxIndex] and propagating the
		// increment to lower indices when input-size is reached

		// set locks and create stack
		final Node targetNode = originIncomingEdge.getTargetNode();
		final Edge[] nodeIncomingEdges = targetNode.getIncomingEdges();
		final LinkedHashMap<Edge, StackElement> edgeToStack = new LinkedHashMap<>();
		final int columns = targetNode.getMemory().getTemplate().length;
		final StackElement originElement;
		{
			StackElement tempOriginElement = null;
			int offset = 0;
			for (final Edge incomingEdge : nodeIncomingEdges) {
				if (incomingEdge == originIncomingEdge) {
					tempOriginElement =
							StackElement.originInput(columns, originIncomingEdge, token, offset);
					offset += incomingEdge.getSourceNode().getMemory().getTemplate().length;
					// don't lock the originInput
					continue;
				}
				try {
					if (!incomingEdge.getSourceNode().getMemory().tryReadLock()) {
						throw new CouldNotAcquireLockException();
					}
				} catch (final InterruptedException ex) {
					throw new Error(
							"Should not happen, interruption of this method is not supported!", ex);
				}
				edgeToStack.put(incomingEdge, StackElement.ordinaryInput(incomingEdge, offset));
				offset += incomingEdge.getSourceNode().getMemory().getTemplate().length;
			}
			originElement = tempOriginElement;
		}
		edgeToStack.put(originIncomingEdge, originElement);

		// get filter steps
		final FilterElement filterSteps[] = filter.getFilterElements();

		for (final FilterElement filterElement : filterSteps) {
			final Collection<StackElement> stack = new ArrayList<>(filterSteps.length);
			final FunctionWithArguments function = filterElement.getFunction();
			final SlotInFactAddress addresses[] = filterElement.getAddressesInTarget();

			// determine new edges
			final Set<Edge> newEdges = new HashSet<>();
			stack.add(originElement);
			for (final SlotInFactAddress address : addresses) {
				final Edge edge = targetNode.delocalizeAddress(address.getFactAddress()).getEdge();
				final StackElement element = edgeToStack.get(edge);
				if (element != originElement) {
					if (newEdges.add(edge)) {
						stack.add(element);
					}
				}
			}

			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Fact[]> TR, final Collection<StackElement> stack,
						final StackElement originElement) {
					final int paramLength = addresses.length;
					final Object params[] = new Object[paramLength];
					// determine parameters
					for (int i = 0; i < paramLength; ++i) {
						final SlotInFactAddress address = addresses[i];
						final AddressPredecessor fact =
								targetNode.delocalizeAddress(address.getFactAddress());
						final StackElement se = edgeToStack.get(fact.getEdge());
						params[i] = se.getValue(fact, address.getSlotAddress());
					}
					// copy result to new TR if facts match predicate
					if ((boolean) function.evaluate(params)) {
						// copy current row from old TR
						final Fact[] row =
								Arrays.copyOf(originElement.getRow(), originElement.getRow().length);
						// insert information from new inputs
						for (final Edge edge : newEdges) {
							// source is some temp, destination new TR
							final StackElement se = edgeToStack.get(edge);
							final Fact[] newRowPart = se.getRow();
							System.arraycopy(newRowPart, 0, row, se.getOffset(), newRowPart.length);
						}
						// copy the result to new TR
						TR.add(row);
					}
				}
			}, stack, originElement);
			// point all inputs that were joint during this turn to the TR
			// StackElement
			for (final Edge incomingEdge : newEdges) {
				edgeToStack.put(incomingEdge, originElement);
			}
			if (!originElement.checkRowBounds()) {
				return originElement.getTable();
			}
		}

		// full join with all inputs not pointing to TR now
		for (final Map.Entry<Edge, StackElement> entry : edgeToStack.entrySet()) {
			if (entry.getValue() == originElement)
				continue;
			final Edge nodeInput = entry.getKey();
			final StackElement se = entry.getValue();
			final Collection<StackElement> stack = Arrays.asList(originElement, se);
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Fact[]> TR, final Collection<StackElement> stack,
						final StackElement originElement) {
					// copy result to new TR
					// copy current row from old TR
					final Fact[] row = originElement.getRow();
					// insert information from new input
					// source is some temp, destination new TR
					final Fact[] newRowPart = se.getRow();
					System.arraycopy(newRowPart, 0, row, se.getOffset(), newRowPart.length);
					// copy the result to new TR
					TR.add(row);
				}
			}, stack, originElement);
			// point all inputs that were joint during this turn to the TR
			// StackElement
			edgeToStack.put(nodeInput, originElement);
		}
		// release lock
		for (final Edge incomingEdge : nodeIncomingEdges) {
			if (incomingEdge == originIncomingEdge)
				continue;
			incomingEdge.getSourceNode().getMemory().releaseReadLock();
		}
		return originElement.getTable();
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp#releaseLock()
	 */
	@Override
	public boolean releaseLock() {
		if (this.lock.release())
			return false;
		// all children have processed the temp memory, now we have to write its
		// content to main memory
		commitAndInvalidate();
		return true;
	}

	private void commitAndInvalidate() {
		originatingMainHandler.acquireWriteLock();
		originatingMainHandler.add(this);
		originatingMainHandler.releaseWriteLock();
		this.valid = false;
	}

	@Override
	public void enqueueInEdges(final Collection<? extends Edge> edges) {
		for (final Edge edge : edges) {
			edge.enqueuePlusMemory(this);
		}
	}
}