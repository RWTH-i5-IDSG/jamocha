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
import org.jamocha.visitor.Visitable;

/**
 * Java-implementation of the {@link org.jamocha.dn.memory.MemoryHandlerPlusTemp} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp
 */
@ToString(callSuper = true, of = "valid")
public abstract class MemoryHandlerPlusTemp<T extends MemoryHandlerMain> extends
		MemoryHandlerTemp<T> implements org.jamocha.dn.memory.MemoryHandlerPlusTemp,
		Visitable<MemoryHandlerPlusTempVisitor> {

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
	/**
	 * set to false as soon as the memory has been committed to its main memory
	 */
	boolean valid = true;

	protected MemoryHandlerPlusTemp(final T originatingMainHandler, final int numChildren,
			final boolean empty, final boolean omitSemaphore) {
		super(originatingMainHandler);
		if (empty) {
			this.lock = null;
			this.valid = false;
		} else if (omitSemaphore || numChildren == 0) {
			this.lock = null;
			originatingMainHandler.getValidOutgoingPlusTokens().add(this);
			// commit and invalidate the token
			final org.jamocha.dn.memory.MemoryHandlerTemp pendingTemp = commitAndInvalidate();
			/*
			 * if this token was a MemoryHandlerPlusTempNewRowsAndCounterUpdates we may have
			 * produced new temps by calling commitAndInvalidate, yet we cannot process them without
			 * lots of ugly code at the worst location to think of, so this is unsupported for now
			 * as it has no purpose in a production system where the only nodes without children are
			 * terminal nodes
			 */
			if (null != pendingTemp) {
				throw new IllegalArgumentException(
						"Nodes using existentials without children are unsupported!");
			}
		} else {
			this.lock = new Semaphore(numChildren);
			originatingMainHandler.getValidOutgoingPlusTokens().add(this);
		}
	}

	@Override
	final public org.jamocha.dn.memory.MemoryHandlerTemp releaseLock() {
		try {
			if (this.lock.release())
				return null;
		} catch (final NullPointerException e) {
			// lock was null
			return null;
		}
		// all children have processed the temp memory, now we have to write its
		// content to main memory
		return commitAndInvalidate();
	}

	final protected org.jamocha.dn.memory.MemoryHandlerTemp commitAndInvalidate() {
		this.originatingMainHandler.acquireWriteLock();
		assert this == this.originatingMainHandler.getValidOutgoingPlusTokens().peek();
		this.originatingMainHandler.getValidOutgoingPlusTokens().remove();
		final org.jamocha.dn.memory.MemoryHandlerTemp newTemp = commitToMain();
		this.originatingMainHandler.releaseWriteLock();
		this.valid = false;
		return newTemp;
	}

	abstract protected org.jamocha.dn.memory.MemoryHandlerTemp commitToMain();

	@Override
	public void enqueueInEdge(final Edge edge) {
		edge.enqueueMemory(this);
	}

	/* * * * * * * * * STATIC FACTORY PART * * * * * * * * */

	protected static boolean canOmitSemaphore(final Edge originEdge) {
		return originEdge.getTargetNode().getIncomingEdges().length <= 1;
	}

	protected static boolean canOmitSemaphore(final Node sourceNode) {
		for (final Edge edge : sourceNode.getOutgoingEdges()) {
			if (!canOmitSemaphore(edge))
				return false;
		}
		return true;
	}

	@Override
	public MemoryHandlerPlusTemp<?> newBetaTemp(final MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		// create follow-up-temp
		final MemoryHandlerPlusTemp<?> token =
				newRegularBetaTemp(originatingMainHandler, filter, this, originIncomingEdge);
		// push old temp into incoming edge to augment the memory seen by its target
		originIncomingEdge.getTempMemories().add(token);
		// return new temp
		return token;
	}

	@Override
	public MemoryHandlerPlusTemp<?> newBetaTemp(
			final MemoryHandlerMainWithExistentials originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		// create follow-up-temp
		final MemoryHandlerPlusTemp<?> token =
				newExistentialBetaTemp(originatingMainHandler, filter, this, originIncomingEdge);
		// push old temp into incoming edge to augment the memory seen by its target
		originIncomingEdge.getTempMemories().add(token);
		// return new temp
		return token;
	}

	static MemoryHandlerPlusTemp<MemoryHandlerMain> newAlphaTemp(
			final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerPlusTemp<? extends MemoryHandlerMain> token,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException {
		final ArrayList<Row> factList = new ArrayList<>(1);
		factLoop: for (final Row row : token.getRowsForSucessorNodes()) {
			assert row.getFactTuple().length == 1;
			for (final AddressFilterElement filterElement : filter.getFilterElements()) {
				if (!applyFilterElement(row.getFactTuple()[0], filterElement)) {
					continue factLoop;
				}
			}
			factList.add(row);
		}
		return new MemoryHandlerPlusTempValidRowsAdder(originatingMainHandler, factList,
				originIncomingEdge.getTargetNode().getNumberOfOutgoingEdges(),
				canOmitSemaphore(originIncomingEdge));
		// FIXME only use Semaphores if one of the outgoing edges is connected to the beta network
		// return new MemoryHandlerPlusTemp(originatingMainHandler,
		// originIncomingEdge.getTargetNode().getNumberOfOutgoingEdges(),
		// needsSemaphore(originIncomingEdge));
	}

	@Override
	public MemoryHandlerPlusTemp<MemoryHandlerMain> newAlphaTemp(
			final MemoryHandlerMain originatingMainHandler, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return newAlphaTemp((MemoryHandlerMain) originatingMainHandler, this, originIncomingEdge,
				filter);
	}

	static MemoryHandlerPlusTemp<MemoryHandlerMain> newRootTemp(
			final MemoryHandlerMain originatingMainHandler, final Node otn,
			final org.jamocha.dn.memory.Fact... facts) {
		final ArrayList<Row> factList = new ArrayList<>(facts.length);
		for (final org.jamocha.dn.memory.Fact fact : facts) {
			assert fact.getTemplate() == otn.getMemory().getTemplate()[0];
			factList.add(originatingMainHandler.newRow(new Fact(fact.getSlotValues())));
		}
		return new MemoryHandlerPlusTempValidRowsAdder(originatingMainHandler, factList,
				otn.getNumberOfOutgoingEdges(), canOmitSemaphore(otn));
		// new MemoryHandlerPlusTemp(originatingMainHandler, factList,
		// otn.getNumberOfOutgoingEdges(), needsSemaphore(otn));
	}

	static abstract class StackElement {
		int rowIndex, memIndex;
		ArrayList<ArrayList<Row>> memStack;
		final int offset;

		private StackElement(final ArrayList<ArrayList<Row>> memStack, final int offset) {
			this.memStack = memStack;
			this.offset = offset;
		}

		public static StackElement ordinaryInput(final Edge edge, final int offset) {
			final LinkedList<? extends MemoryHandler> temps = edge.getTempMemories();
			final ArrayList<ArrayList<Row>> memStack =
					new ArrayList<ArrayList<Row>>(temps.size() + 1);
			memStack.add(((org.jamocha.dn.memory.javaimpl.MemoryHandlerMain) edge.getSourceNode()
					.getMemory()).getRowsForSucessorNodes());
			for (final Iterator<? extends MemoryHandler> iter = temps.iterator(); iter.hasNext();) {
				final MemoryHandlerPlusTemp<?> temp = (MemoryHandlerPlusTemp<?>) iter.next();
				if (!temp.valid) {
					iter.remove();
					continue;
				}
				memStack.add(temp.getRowsForSucessorNodes());
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
				final MemoryHandlerPlusTemp<?> token, final int offset) {
			final ArrayList<Row> listWithHoles =
					new ArrayList<>(token.getRowsForSucessorNodes().size());
			for (final Row row : token.getRowsForSucessorNodes()) {
				final Row wideRow =
						((MemoryHandlerMain) originEdge.getTargetNode().getMemory())
								.newRow(columns);
				assert columns >= offset + row.getFactTuple().length;
				wideRow.copy(offset, row);
				listWithHoles.add(wideRow);
			}
			final ArrayList<ArrayList<Row>> memStack = new ArrayList<ArrayList<Row>>(1);
			memStack.add(listWithHoles);
			return new StackElement(memStack, offset) {
				@Override
				Object getValue(final AddressPredecessor addr, final SlotAddress slot) {
					return this.getRow().getFactTuple()[((org.jamocha.dn.memory.javaimpl.FactAddress) addr
							.getEdge().localizeAddress(addr.getAddress())).index].getValue((slot));
				}
			};
		}

		ArrayList<Row> getTable() {
			return this.memStack.get(this.memIndex);
		}

		Row getRow() {
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
		public void apply(final ArrayList<Row> TR, final StackElement originElement);
	}

	private static void loop(final FunctionPointer functionPointer, final ArrayList<Row> TR,
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
						originElement.memStack.set(0, new ArrayList<Row>(0));
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

	private static final LinkedHashMap<Edge, StackElement> getLocksAndStack(
			final MemoryHandlerPlusTemp<?> token, final Edge originIncomingEdge,
			final boolean getOrigin) throws CouldNotAcquireLockException {
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
					if (!getOrigin) {
						// don't lock the originInput
						continue;
					}
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
		return edgeToStack;
	}

	private static void releaseLocks(final Edge originEdge, final boolean releaseOrigin) {
		final Edge[] nodeIncomingEdges = originEdge.getTargetNode().getIncomingEdges();
		// release lock
		for (final Edge incomingEdge : nodeIncomingEdges) {
			if (!releaseOrigin && incomingEdge == originEdge)
				continue;
			incomingEdge.getSourceNode().getMemory().releaseReadLock();
		}
	}

	private static MemoryHandlerPlusTemp<?> newRegularBetaTemp(
			final MemoryHandlerMain originatingMainHandler, final AddressFilter filter,
			final MemoryHandlerPlusTemp<?> token, final Edge originEdge)
			throws CouldNotAcquireLockException {
		final LinkedHashMap<Edge, StackElement> edgeToStack =
				getLocksAndStack(token, originEdge, false);

		performJoin(filter, edgeToStack, originEdge);

		releaseLocks(originEdge, false);

		final ArrayList<Row> facts = edgeToStack.get(originEdge).getTable();
		final int numChildren = originEdge.getTargetNode().getNumberOfOutgoingEdges();
		return new MemoryHandlerPlusTempValidRowsAdder(originatingMainHandler, facts, numChildren,
				canOmitSemaphore(originEdge));
	}

	private static MemoryHandlerPlusTemp<?> newExistentialBetaTemp(
			final MemoryHandlerMainWithExistentials originatingMainHandler,
			final AddressFilter filter, final MemoryHandlerPlusTemp<?> token, final Edge originEdge)
			throws CouldNotAcquireLockException {
		final LinkedHashMap<Edge, StackElement> edgeToStack =
				getLocksAndStack(token, originEdge, true);

		final ArrayList<CounterUpdate> counterUpdates =
				performExistentialJoin(filter, edgeToStack, originEdge);
		performJoin(filter, edgeToStack, originEdge);

		releaseLocks(originEdge, true);

		final ArrayList<Row> facts = edgeToStack.get(originEdge).getTable();
		final int numChildren = originEdge.getTargetNode().getNumberOfOutgoingEdges();
		if (null == counterUpdates) {
			return new MemoryHandlerPlusTempValidRowsAdder(originatingMainHandler, facts,
					numChildren, canOmitSemaphore(originEdge));
		}
		return MemoryHandlerPlusTempNewRowsAndCounterUpdates.newInstance(originatingMainHandler,
				counterUpdates, facts, numChildren, canOmitSemaphore(originEdge));
	}

	private static ArrayList<CounterUpdate> performExistentialJoin(final AddressFilter filter,
			final LinkedHashMap<Edge, StackElement> edgeToStack, final Edge originEdge) {
		final AddressFilterElement[] filterPartsForCounterColumns =
				originEdge.getFilterPartsForCounterColumns();
		// if there are existential facts in the token, perform a join with the main memory
		if (filterPartsForCounterColumns.length == 0) {
			return null;
		}
		final ArrayList<CounterUpdate> counterUpdates = new ArrayList<>();
		final StackElement originElement = edgeToStack.get(originEdge);
		final FactAddressPartition partition = partitionFactAddresses(filter, originEdge);
		final MemoryHandlerMain memoryHandlerMain =
				(MemoryHandlerMain) originEdge.getTargetNode().getMemory();
		final ArrayList<Row> mainRows = memoryHandlerMain.getAllRows();
		final ArrayList<Row> tokenRows = originElement.getTable();
		final int mainSize = mainRows.size();
		final int tokenSize = tokenRows.size();
		final boolean[] tokenRowContainsOnlyOldFactsInRegularPart = new boolean[tokenSize];
		for (int mainIndex = 0; mainIndex < mainSize; ++mainIndex) {
			final Row mainRow = mainRows.get(mainIndex);
			final Fact[] mainFactTuple = mainRow.getFactTuple();
			CounterUpdate currentCounterUpdate = null;
			tokenloop: for (int tokenIndex = 0; tokenIndex < tokenSize; ++tokenIndex) {
				final Row tokenRow = tokenRows.get(tokenIndex);
				final Fact[] tokenFactTuple = tokenRow.getFactTuple();
				// check whether the allRows are the same in the regular fact part
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
		// for the join with the other inputs, delete the allRows that did not contain new
		// regular, but only new existential facts
		final LazyListCopy<Row> copy = new LazyListCopy<>(tokenRows);
		for (int i = 0; i < originElement.getTable().size(); ++i) {
			if (tokenRowContainsOnlyOldFactsInRegularPart[i])
				copy.drop(i);
			else
				copy.keep(i);
		}
		originElement.memStack.set(0, copy.getList());
		return counterUpdates;
	}

	/*
	 * Assumption: every existentially quantified path/address is only used in a single filter
	 * element.
	 */
	private static void performJoin(final AddressFilter filter,
			final LinkedHashMap<Edge, StackElement> edgeToStack, final Edge originEdge) {
		final Node targetNode = originEdge.getTargetNode();
		final StackElement originElement = edgeToStack.get(originEdge);

		final Counter counter =
				((MemoryHandlerMain) originEdge.getTargetNode().getMemory()).counter;

		// get filter steps
		final AddressFilterElement filterSteps[] = filter.getFilterElements();
		for (final AddressFilterElement filterElement : filterSteps) {
			final Collection<StackElement> stack = new ArrayList<>(filterSteps.length);
			final PredicateWithArguments predicate = filterElement.getFunction();
			final SlotInFactAddress addresses[] = filterElement.getAddressesInTarget();
			final CounterColumn counterColumn = (CounterColumn) filterElement.getCounterColumn();
			final boolean existential = (counterColumn == null);

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

			final ArrayList<Row> TR = new ArrayList<>();
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Row> TR, final StackElement originElement) {
					final int paramLength = addresses.length;
					final Object params[] = new Object[paramLength];
					// determine parameters
					for (int i = 0; i < paramLength; ++i) {
						final SlotInFactAddress slotInTargetAddress = addresses[i];
						final org.jamocha.dn.memory.FactAddress targetAddress =
								slotInTargetAddress.getFactAddress();
						final AddressPredecessor upwardsAddress =
								targetNode.delocalizeAddress(targetAddress);
						final StackElement se = edgeToStack.get(upwardsAddress.getEdge());
						params[i] =
								se.getValue(upwardsAddress, slotInTargetAddress.getSlotAddress());
					}
					// copy result to new TR if facts match predicate or existentials are present
					final boolean match = predicate.evaluate(params);
					if (match || existential) {
						// copy current row from old TR
						final Row row = originElement.getRow().copy();
						// insert information from new inputs
						for (final Edge edge : newEdges) {
							// source is some temp, destination new TR
							final StackElement se = edgeToStack.get(edge);
							row.copy(se.getOffset(), se.getRow());
						}
						if (match) {
							// use counter to set counterColumn to 1 if counterColumn is not null
							counter.increment(row, counterColumn, 1);
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
			final ArrayList<Row> TR = new ArrayList<>();
			loop(new FunctionPointer() {
				@Override
				public void apply(final ArrayList<Row> TR, final StackElement originElement) {
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

}
