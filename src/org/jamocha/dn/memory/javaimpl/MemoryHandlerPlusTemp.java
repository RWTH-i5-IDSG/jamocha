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
import java.util.Map;
import java.util.Set;

import lombok.ToString;
import lombok.Value;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.nodes.AddressPredecessor;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.fwa.PredicateWithArguments;

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
	ArrayList<FactTuple> filtered;
	final ArrayList<CounterUpdate> counterUpdates;

	private MemoryHandlerPlusTemp(final MemoryHandlerMain originatingMainHandler,
			final ArrayList<FactTuple> facts, final int numChildren,
			final ArrayList<CounterUpdate> counterUpdates) {
		super(originatingMainHandler, facts);
		this.counterUpdates = counterUpdates;
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
		// create follow-up-temp
		final MemoryHandlerPlusTemp mem =
				getLocksAndPerformJoin(originatingMainHandler, filter, token, originIncomingEdge);
		// push old temp into incoming edge to augment the memory seen by its target
		originIncomingEdge.getTempMemories().add(token);
		// return new temp
		return mem;
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
		final ArrayList<FactTuple> factList = new ArrayList<>(1);
		factLoop: for (final FactTuple row : token.rows) {
			assert row.getFactTuple().length == 1;
			for (final AddressFilterElement filterElement : filter.getFilterElements()) {
				if (!applyFilterElement(row.getFactTuple()[0], filterElement)) {
					continue factLoop;
				}
			}
			factList.add(row);
		}
		// FIXME only use Semaphores if one of the outgoing edges is connected to the beta network
		return new MemoryHandlerPlusTemp(originatingMainHandler, factList, originIncomingEdge
				.getTargetNode().getNumberOfOutgoingEdges(), new ArrayList<CounterUpdate>());
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
		final ArrayList<FactTuple> factList = new ArrayList<>(facts.length);
		for (final org.jamocha.dn.memory.Fact fact : facts) {
			factList.add(originatingMainHandler.newRow(new Fact(fact.getSlotValues())));
		}
		return new MemoryHandlerPlusTemp(originatingMainHandler, factList,
				otn.getNumberOfOutgoingEdges(), new ArrayList<CounterUpdate>());
	}

	static abstract class StackElement {
		int rowIndex, memIndex;
		ArrayList<ArrayList<FactTuple>> memStack;
		final int offset;

		private StackElement(final ArrayList<ArrayList<FactTuple>> memStack, final int offset) {
			this.memStack = memStack;
			this.offset = offset;
		}

		public static StackElement ordinaryInput(final Edge edge, final int offset) {
			final LinkedList<? extends MemoryHandler> temps = edge.getTempMemories();
			final ArrayList<ArrayList<FactTuple>> memStack =
					new ArrayList<ArrayList<FactTuple>>(temps.size() + 1);
			memStack.add(((org.jamocha.dn.memory.javaimpl.MemoryHandlerMain) edge.getSourceNode()
					.getMemory()).rows);
			for (final Iterator<? extends MemoryHandler> iter = temps.iterator(); iter.hasNext();) {
				final MemoryHandlerPlusTemp temp = (MemoryHandlerPlusTemp) iter.next();
				if (!temp.valid) {
					iter.remove();
					continue;
				}
				memStack.add(temp.rows);
			}
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
					return this.getRow().getFactTuple()[((org.jamocha.dn.memory.javaimpl.FactAddress) addr
							.getAddress()).index].getValue((slot));
				}
			};
		}

		public static StackElement originInput(final int columns, final Edge originEdge,
				final MemoryHandlerPlusTemp token, final int offset) {
			final org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp temp = token;
			final ArrayList<FactTuple> listWithHoles = new ArrayList<>(temp.rows.size());
			for (final FactTuple row : temp.rows) {
				final FactTuple wideRow =
						((MemoryHandlerMain) originEdge.getTargetNode().getMemory())
								.newRow(columns);
				assert columns >= offset + row.getFactTuple().length;
				wideRow.copy(offset, row);
				listWithHoles.add(wideRow);
			}
			final ArrayList<ArrayList<FactTuple>> memStack = new ArrayList<ArrayList<FactTuple>>(1);
			memStack.add(listWithHoles);
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
					return this.getRow().getFactTuple()[((org.jamocha.dn.memory.javaimpl.FactAddress) addr
							.getEdge().localizeAddress(addr.getAddress())).index].getValue((slot));
				}
			};
		}

		ArrayList<FactTuple> getTable() {
			return this.memStack.get(this.memIndex);
		}

		FactTuple getRow() {
			return this.getTable().get(this.rowIndex);
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
		public void apply(final ArrayList<FactTuple> TR, final StackElement originElement);
	}

	private static void loop(final FunctionPointer functionPointer, final ArrayList<FactTuple> TR,
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
						originElement.memStack.set(0, new ArrayList<FactTuple>(0));
						TR.clear();
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

	private static MemoryHandlerPlusTemp getLocksAndPerformJoin(
			final MemoryHandlerMain originatingMainHandler, final AddressFilter filter,
			final MemoryHandlerPlusTemp token, final Edge originIncomingEdge)
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

		// lock target main if there are existentials in the token
		final boolean containsExistentials =
				(originIncomingEdge.getFilterPartsForCounterColumns().length != 0);
		if (containsExistentials) {
			try {
				if (!originatingMainHandler.tryReadLock()) {
					for (final Edge edge : edgeToStack.keySet()) {
						edge.getSourceNode().getMemory().releaseReadLock();
					}
					throw new CouldNotAcquireLockException();
				}
			} catch (InterruptedException ex) {
				throw new Error("Should not happen, interruption of this method is not supported!",
						ex);
			}
		}

		final ArrayList<CounterUpdate> counterUpdates = new ArrayList<>();
		final ArrayList<FactTuple> rowsToAdd = new ArrayList<>();
		final ArrayList<FactTuple> rowsToDel = new ArrayList<>();
		performJoin(filter, targetNode, edgeToStack, originIncomingEdge, rowsToAdd, rowsToDel,
				counterUpdates);
		// release lock
		for (final Edge incomingEdge : nodeIncomingEdges) {
			if (incomingEdge == originIncomingEdge)
				continue;
			incomingEdge.getSourceNode().getMemory().releaseReadLock();
		}
		if (containsExistentials) {
			originatingMainHandler.releaseReadLock();
		}
		final ArrayList<FactTuple> facts = originElement.getTable();
		facts.addAll(rowsToAdd);
		return new MemoryHandlerPlusTemp(originatingMainHandler, facts, originIncomingEdge
				.getTargetNode().getNumberOfOutgoingEdges(), counterUpdates);
	}

	/*
	 * Assumption: every existentially quantified path/address is only used in a single filter
	 * element.
	 */
	private static void performJoin(final AddressFilter filter, final Node targetNode,
			final LinkedHashMap<Edge, StackElement> edgeToStack, final Edge originEdge,
			final ArrayList<FactTuple> rowsToAdd, final ArrayList<FactTuple> rowsToDel,
			final ArrayList<CounterUpdate> counterUpdates) {
		final StackElement originElement = edgeToStack.get(originEdge);

		// if there are existential facts in the token, perform a join with the main memory
		final AddressFilterElement[] filterPartsForCounterColumns =
				originEdge.getFilterPartsForCounterColumns();
		if (filterPartsForCounterColumns.length != 0) {
			final FactAddressPartition partition = partitionFactAddresses(filter, originEdge);
			final MemoryHandlerMain memoryHandlerMain =
					(MemoryHandlerMain) originEdge.getTargetNode().getMemory();
			final ArrayList<FactTuple> mainRows = memoryHandlerMain.rows;
			final ArrayList<FactTuple> tokenRows = originElement.getTable();
			final int mainSize = mainRows.size();
			final int tokenSize = tokenRows.size();
			final boolean[] tokenRowContainsOnlyOldFactsInRegularPart = new boolean[tokenSize];
			for (int mainIndex = 0; mainIndex < mainSize; ++mainIndex) {
				final FactTuple mainRow = mainRows.get(mainIndex);
				final Fact[] mainFactTuple = mainRow.getFactTuple();
				CounterUpdate currentCounterUpdate = null;
				tokenloop: for (int tokenIndex = 0; tokenIndex < tokenSize; ++tokenIndex) {
					final FactTuple tokenRow = tokenRows.get(tokenIndex);
					final Fact[] tokenFactTuple = tokenRow.getFactTuple();
					// check whether the rows are the same in the regular fact part
					for (final FactAddress factAddress : partition.regular) {
						if (tokenFactTuple[factAddress.index] != mainFactTuple[factAddress.index]) {
							continue tokenloop;
						}
					}
					// mark row: does not contain new facts in the regular part
					tokenRowContainsOnlyOldFactsInRegularPart[tokenIndex] = true;
					// check whether the existential part fulfill the filter conditions
					for (final AddressFilterElement filterElement : filterPartsForCounterColumns) {
						final PredicateWithArguments predicate = filterElement.getFunction();
						final SlotInFactAddress[] addresses = filterElement.getAddressesInTarget();
						final CounterColumn counterColumn =
								(CounterColumn) filterElement.getCounterColumn();
						final int paramLength = addresses.length;
						final Object params[] = new Object[paramLength];
						// determine parameters using facts in the token where possible
						for (int i = 0; i < paramLength; ++i) {
							final SlotInFactAddress address = addresses[i];
							final Fact[] factBase;
							if (partition.existential.contains(address.getFactAddress())) {
								factBase = tokenFactTuple;
							} else {
								factBase = mainFactTuple;
							}
							params[i] =
									factBase[((FactAddress) address.getFactAddress()).index]
											.getValue(address.getSlotAddress());
						}
						// if combined row doesn't match, try the next token row
						if (!predicate.evaluate(params)) {
							continue tokenloop;
						}
						// token row matches main row, we can increment the counter
						if (null == currentCounterUpdate) {
							currentCounterUpdate = new CounterUpdate(mainRow);
							counterUpdates.add(currentCounterUpdate);
						}
						currentCounterUpdate.increment(counterColumn, 1);
					}
				}
			}
			// for the join with the other inputs, delete the rows that did not contain new regular,
			// but only new existential facts
			final LazyListCopy copy = LazyListCopy.newLazyListCopy(tokenRows);
			for (int i = 0; i < originElement.getTable().size(); ++i) {
				if (tokenRowContainsOnlyOldFactsInRegularPart[i])
					copy.drop(i);
				else
					copy.keep(i);
			}
			originElement.memStack.set(0, copy.getList());
		}

		// get filter steps
		final AddressFilterElement filterSteps[] = filter.getFilterElements();
		for (final AddressFilterElement filterElement : filterSteps) {
			final Collection<StackElement> stack = new ArrayList<>(filterSteps.length);
			final PredicateWithArguments predicate = filterElement.getFunction();
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

			final ArrayList<FactTuple> TR = new ArrayList<>();
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<FactTuple> TR, final StackElement originElement) {
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
					if (predicate.evaluate(params)) {
						// copy current row from old TR
						final FactTuple row = originElement.getRow().copy();
						// insert information from new inputs
						for (final Edge edge : newEdges) {
							// source is some temp, destination new TR
							final StackElement se = edgeToStack.get(edge);
							row.copy(se.getOffset(), se.getRow());
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
			if (!originElement.checkRowBounds()) {
				return;
			}
		}

		// full join with all inputs not pointing to TR now
		for (final Map.Entry<Edge, StackElement> entry : edgeToStack.entrySet()) {
			if (entry.getValue() == originElement)
				continue;
			final Edge nodeInput = entry.getKey();
			final StackElement se = entry.getValue();
			final Collection<StackElement> stack = Arrays.asList(originElement, se);
			final ArrayList<FactTuple> TR = new ArrayList<>();
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<FactTuple> TR, final StackElement originElement) {
					// copy result to new TR
					// copy current row from old TR
					// insert information from new input
					// source is some temp, destination new TR
					// copy the result to new TR
					TR.add(originElement.getRow().copy().copy(se.getOffset(), se.getRow()));
				}
			}, TR, stack, originElement);
			// replace TR in originElement with new temporary result
			originElement.memStack.set(0, TR);
			// point all inputs that were joint during this turn to the TR
			// StackElement
			edgeToStack.put(nodeInput, originElement);
		}
	}

	@Value
	static class FactAddressPartition {
		FactAddress regular[];
		Set<FactAddress> existential;
	}

	private static FactAddressPartition partitionFactAddresses(final AddressFilter filter,
			final Edge originEdge) {
		final FactAddress[] originAddresses =
				((MemoryHandlerMain) originEdge.getSourceNode().getMemory()).addresses;
		final Set<org.jamocha.dn.memory.FactAddress> filterNegativeExistentialAddresses =
				filter.getNegativeExistentialAddresses();
		final Set<org.jamocha.dn.memory.FactAddress> filterPositiveExistentialAddresses =
				filter.getPositiveExistentialAddresses();
		final ArrayList<FactAddress> regular = new ArrayList<>(originAddresses.length);
		final Set<FactAddress> existential = new HashSet<>(originAddresses.length);
		for (final FactAddress sourceFactAddress : originAddresses) {
			final FactAddress targetFactAddress =
					(FactAddress) originEdge.localizeAddress(sourceFactAddress);
			if (!filterNegativeExistentialAddresses.contains(targetFactAddress)
					&& !filterPositiveExistentialAddresses.contains(targetFactAddress)) {
				regular.add(targetFactAddress);
			} else {
				existential.add(targetFactAddress);
			}
		}
		return new FactAddressPartition((FactAddress[]) regular.toArray(), existential);
	}

	@Override
	public MemoryHandlerTemp releaseLock() {
		internalReleaseLock();
		return null;
	}

	boolean internalReleaseLock() {
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
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this);
	}

}
