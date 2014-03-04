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

import lombok.ToString;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.nodes.AddressPredecessor;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.visitor.Visitor;

/**
 * Java-implementation of the {@link org.jamocha.dn.memory.MemoryHandlerPlusTemp} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp
 */
@ToString(callSuper = true, of = "valid")
public class MemoryHandlerPlusTemp extends MemoryHandlerTemp implements
		org.jamocha.dn.memory.MemoryHandlerPlusTemp {

	static class Semaphore {
		int count;

		public Semaphore(final int count) {
			super();
			this.count = count;
		}

		public synchronized boolean release() {
			return --this.count != 0;
		}
	}

	final Semaphore lock;
	boolean valid = true;
	List<Fact[]> filtered;

	private MemoryHandlerPlusTemp(final MemoryHandlerMain originatingMainHandler,
			final List<Fact[]> facts, final int numChildren) {
		super(originatingMainHandler, facts);
		if (facts.size() == 0) {
			this.lock = null;
			this.valid = false;
		} else if (numChildren == 0) {
			this.lock = null;
			originatingMainHandler.getValidOutgoingPlusTokens().add(this);
			commitAndInvalidate();
		} else {
			this.lock = new Semaphore(numChildren);
			originatingMainHandler.getValidOutgoingPlusTokens().add(this);
		}
	}

	static MemoryHandlerPlusTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerPlusTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return new MemoryHandlerPlusTemp(originatingMainHandler, getLocksAndPerformJoin(
				originatingMainHandler, filter, token, originIncomingEdge,
				Counter.newCounter(originatingMainHandler)), originIncomingEdge.getTargetNode()
				.getNumberOfOutgoingEdges());
	}

	@Override
	public MemoryHandlerPlusTemp newBetaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		return newBetaTemp((MemoryHandlerMain) originatingMainHandler, this, originIncomingEdge,
				filter);
	}

	static MemoryHandlerPlusTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerPlusTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		final ArrayList<Fact[]> factList = new ArrayList<>(1);
		factLoop: for (final Fact[] fact : token.facts) {
			assert fact.length == 1;
			for (final AddressFilterElement filterElement : filter.getFilterElements()) {
				if (!applyFilterElement(fact[0], filterElement)) {
					continue factLoop;
				}
			}
			factList.add(fact);
		}
		// FIXME only use Semaphores if one of the outgoing edges is connected to the beta network
		return new MemoryHandlerPlusTemp(originatingMainHandler, factList, originIncomingEdge
				.getTargetNode().getNumberOfOutgoingEdges());
	}

	@Override
	public MemoryHandlerPlusTemp newAlphaTemp(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		return newAlphaTemp((MemoryHandlerMain) originatingMainHandler, this, originIncomingEdge,
				filter);
	}

	static MemoryHandlerPlusTemp newRootTemp(final MemoryHandlerMain originatingMainHandler,
			final Node otn, final org.jamocha.dn.memory.Fact... facts) {
		final ArrayList<Fact[]> factList = new ArrayList<>(facts.length);
		for (final org.jamocha.dn.memory.Fact fact : facts) {
			factList.add(new Fact[] { new Fact(fact.getSlotValues()) });
		}
		return new MemoryHandlerPlusTemp(originatingMainHandler, factList,
				otn.getNumberOfOutgoingEdges());
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
			for (final Iterator<? extends MemoryHandler> iter = temps.iterator(); iter.hasNext();) {
				final MemoryHandlerPlusTemp temp = (MemoryHandlerPlusTemp) iter.next();
				if (!temp.valid) {
					iter.remove();
					continue;
				}
				memStack.add(temp.facts);
			}
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
					return this.getRow()[((org.jamocha.dn.memory.javaimpl.FactAddress) addr
							.getAddress()).index].getValue((slot));
				}
			};
		}

		public static StackElement originInput(final int columns, final Edge originEdge,
				final MemoryHandlerPlusTemp token, final int offset) {
			final org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp temp = token;
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
							.getEdge().localizeAddress(addr.getAddress())).index].getValue((slot));
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
			return this.memStack.size() > this.memIndex && this.memIndex >= 0;
		}

		boolean checkRowBounds() {
			return checkMemBounds() && getTable().size() > this.rowIndex && this.rowIndex >= 0;
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
		public void apply(final ArrayList<Fact[]> TR, final StackElement originElement);
	}

	private static void loop(final FunctionPointer functionPointer, final ArrayList<Fact[]> TR,
			final Collection<StackElement> stack, final StackElement originElement) {
		if (stack.isEmpty()) {
			return;
		}
		{
			final Iterator<StackElement> iter = stack.iterator();
			// skip originElement
			iter.next();
			// initialize all memory indices to valid values
			while (iter.hasNext()) {
				final StackElement element = iter.next();
				while (!element.checkRowBounds()) {
					if (!element.checkMemBounds()) {
						// one of the elements doesn't hold any facts, the join will be empty
						// delete all partial fact tuples in the TR
						originElement.memStack.set(0, new ArrayList<Fact[]>(0));
						return;
					}
					element.memIndex++;
				}
			}
		}
		outerloop: while (true) {
			innerloop: while (true) {
				functionPointer.apply(TR, originElement);
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
	}

	private static List<Fact[]> getLocksAndPerformJoin(
			final MemoryHandlerMain originatingMainHandler, final AddressFilter filter,
			final MemoryHandlerPlusTemp token, final Edge originIncomingEdge, final Counter counter)
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
						for (final Edge edge : edgeToStack.keySet()) {
							edge.getSourceNode().getMemory().releaseReadLock();
						}
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

		performJoin(filter, targetNode, edgeToStack, originIncomingEdge, counter);
		// release lock
		for (final Edge incomingEdge : nodeIncomingEdges) {
			if (incomingEdge == originIncomingEdge)
				continue;
			incomingEdge.getSourceNode().getMemory().releaseReadLock();
		}
		return originElement.getTable();
	}

	private static class IntegerHolder {
		int value;
	}

	/*
	 * Assumption: every existentially quantified path/address is only used in a single filter
	 * element.
	 */
	private static void performJoin(final AddressFilter filter, final Node targetNode,
			final LinkedHashMap<Edge, StackElement> edgeToStack, final Edge originEdge,
			final Counter counter) {
		final StackElement originElement = edgeToStack.get(originEdge);
		// get filter steps
		final AddressFilterElement filterSteps[] = filter.getFilterElements();

		// order of counter columns depends on order of them in the filter passed to node ctor
		final AddressFilter counterOrderFilter = originEdge.getTargetNode().getFilter();

		final IntegerHolder counterColumn = new IntegerHolder(), counterRow = new IntegerHolder();
		// FIXME assumption: there are no more regular filter elements after the first existential
		// or negated existential filter element in the filterSteps
		// otherwise: copy&paste this loop and first only look at regular filter elements, then look
		// at existential and negated existential filter elements
		for (final AddressFilterElement filterElement : filterSteps) {
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

			// filterElement.

			org.jamocha.visitor.Visitor visitor = new Visitor() {
				public void visit(final AddressFilterElement fe) {
					final ArrayList<Fact[]> TR = new ArrayList<>();
					loop(new FunctionPointer() {
						@Override
						public void apply(final ArrayList<Fact[]> TR,
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
										Arrays.copyOf(originElement.getRow(),
												originElement.getRow().length);
								// insert information from new inputs
								for (final Edge edge : newEdges) {
									// source is some temp, destination new TR
									final StackElement se = edgeToStack.get(edge);
									final Fact[] newRowPart = se.getRow();
									System.arraycopy(newRowPart, 0, row, se.getOffset(),
											newRowPart.length);
								}
								// copy the result to new TR
								TR.add(row);
							}
						}
					}, TR, stack, originElement);
					// replace TR in originElement with new temporary result
					originElement.memStack.set(0, TR);
					// point all inputs that were joint during this turn to the TR
					// StackElement
					for (final Edge incomingEdge : newEdges) {
						edgeToStack.put(incomingEdge, originElement);
					}
				}

				public void visitNegatedExistential(final AddressFilterElement fe) {
					if (counter.size() == 0) {
						counter.addEmptyRows(originElement.memStack.get(0).size());
					}
					final ArrayList<Fact[]> TR = new ArrayList<>();
					loop(new FunctionPointer() {
						@Override
						public void apply(final ArrayList<Fact[]> TR,
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
							// increment corresponding counter if facts match predicate
							if ((boolean) function.evaluate(params)) {
								counter.increment(counterRow.value, counterColumn.value);
							}
							++counterRow.value;
						}
					}, TR, stack, originElement);
					// point all inputs that were joint during this turn to the TR
					// StackElement
					for (final Edge incomingEdge : newEdges) {
						edgeToStack.put(incomingEdge, originElement);
					}
					++counterColumn.value;
				}

			};
		}
		if (!originElement.checkRowBounds()) {
			return;
		}

		// full join with all inputs not pointing to TR now
		for (final Map.Entry<Edge, StackElement> entry : edgeToStack.entrySet()) {
			if (entry.getValue() == originElement)
				continue;
			final Edge nodeInput = entry.getKey();
			final StackElement se = entry.getValue();
			final Collection<StackElement> stack = Arrays.asList(originElement, se);
			final ArrayList<Fact[]> TR = new ArrayList<>();
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Fact[]> TR, final StackElement originElement) {
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
			}, TR, stack, originElement);
			// replace TR in originElement with new temporary result
			originElement.memStack.set(0, TR);
			// point all inputs that were joint during this turn to the TR
			// StackElement
			edgeToStack.put(nodeInput, originElement);
		}
	}

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
		this.originatingMainHandler.acquireWriteLock();
		assert this == this.originatingMainHandler.getValidOutgoingPlusTokens().peek();
		this.originatingMainHandler.getValidOutgoingPlusTokens().remove();
		this.originatingMainHandler.add(this);
		this.originatingMainHandler.releaseWriteLock();
		this.valid = false;
	}

	@Override
	public void enqueueInEdges(final Collection<? extends Edge> edges) {
		for (final Edge edge : edges) {
			edge.enqueuePlusMemory(this);
		}
	}

}
