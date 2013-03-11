package org.jamocha.engine.rating;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.engine.rating.exceptions.MissingDataException;
import org.jamocha.engine.rating.exceptions.MissingJoinListException;
import org.jamocha.engine.rating.inputvalues.InputValuesSource;
import org.jamocha.engine.rating.inputvalues.NodeContainer;

public class RatingNode {
	protected final int id;
	protected final InputValuesSource source;
	protected final RatingNodeWithFilterState filtered, unfiltered;
	protected final double tuplesPerPage, effectiveFInsert, effectiveFDelete,
			memoryCost, runtimeCost;
	protected final int tupleSize;

	protected double getJSF(final JoinListEntry entry) {
		return this.source.getJSF(this.getId(), entry.getSourceNode(),
				entry.getTargetNode());
	}

	protected double getJSF(final RatingNodeWithFilterState node1,
			final RatingNodeWithFilterState node2) {
		return this.source.getJSF(this.getId(), node1, node2);
	}

	protected double getJSF(final NodeContainer node1, final NodeContainer node2) {
		return this.source.getJSF(this.getId(), node1, node2);
	}

	public static interface Calculator {
		double getSize(final RatingNodeWithFilterState node)
				throws MissingDataException;

		int getTupleSize();

		double getEffectiveFInsert(final RatingNode node)
				throws MissingDataException;

		double getEffectiveFDelete(final RatingNode node)
				throws MissingDataException;

		double getMemoryCost(final RatingNode node) throws MissingDataException;

		double getRuntimeCost(final RatingNode node)
				throws MissingDataException;
	}

	protected RatingNode(final int id, final InputValuesSource source,
			final Calculator calculator) throws MissingDataException {
		this.id = id;
		this.source = source;
		this.unfiltered = new RatingNodeWithFilterState(this,
				FilterState.UNFILTERED, calculator);
		this.filtered = new RatingNodeWithFilterState(this,
				FilterState.FILTERED, calculator);
		this.tupleSize = calculator.getTupleSize();
		this.tuplesPerPage = source.getTuplesPerPage() / this.tupleSize;
		this.effectiveFInsert = calculator.getEffectiveFInsert(this);
		this.effectiveFDelete = calculator.getEffectiveFDelete(this);
		this.memoryCost = calculator.getMemoryCost(this);
		this.runtimeCost = calculator.getRuntimeCost(this);
	}

	public RatingNodeWithFilterState getFiltered() {
		return this.filtered;
	}

	public RatingNodeWithFilterState getUnfiltered() {
		return this.unfiltered;
	}

	protected double getEffectiveFInsert() {
		return this.effectiveFInsert;
	}

	protected double getEffectiveFDelete() {
		return this.effectiveFDelete;
	}

	protected int getTupleSize() {
		return this.tupleSize;
	}

	protected double getTuplesPerPage() {
		return this.tuplesPerPage;
	}

	public double getMemoryCost() {
		return this.memoryCost;
	}

	public double getRuntimeCost() {
		return this.runtimeCost;
	}

	@Override
	public String toString() {
		return Integer.toString(this.id);
	}

	public int getId() {
		return this.id;
	}

	static private double cardenas(final double m, final double k) {
		if (k <= 1)
			return k;
		return m * (1 - Math.pow((1 - 1 / m), k));
	}

	static private double VerbK(final RatingNode baseNode,
			final JoinListEntry entry, final double l) {
		return VerbK(baseNode.getJSF(entry), entry.getTargetNode(), l);
	}

	static private double VerbK(final RatingNode baseNode,
			final RatingNodeWithFilterState L,
			final RatingNodeWithFilterState R, final double l) {
		return VerbK(baseNode.getJSF(L, R), R, l);
	}

	static private double VerbK(final double jsf,
			final RatingNodeWithFilterState R, final double l) {
		return cardenas(
				Math.ceil(R.getSize() / R.getNode().getTuplesPerPage()),
				l * R.getSize() * jsf);
	}

	public static RatingNode newAlphaRatingNode(final int id,
			final InputValuesSource source) throws MissingDataException {
		return new RatingNode(id, source, new Calculator() {

			public double getSize(final RatingNodeWithFilterState node)
					throws MissingDataException {
				switch (node.getFilterState()) {
				case FILTERED:
					return source.getSize(node.getNode().getUnfiltered());
				case UNFILTERED:
					return source.getSize(node);
				}
				throw new IllegalArgumentException();
			}

			public int getTupleSize() {
				return 1;
			}

			public double getEffectiveFInsert(final RatingNode node)
					throws MissingDataException {
				return source.getFInsert(node.getFiltered());
			}

			public double getEffectiveFDelete(final RatingNode node)
					throws MissingDataException {
				return source.getFDelete(node.getFiltered());
			}

			public double getMemoryCost(final RatingNode node) {
				return node.getFiltered().getSize();
			}

			public double getRuntimeCost(final RatingNode node) {
				// use multiplier 2 for delete costs too as there is no negation
				// in the alpha network causing the additional runtime unit
				return node.getEffectiveFInsert() * 2
						+ node.getEffectiveFDelete() * 2;
			}
		});
	}

	public static RatingNode newTerminalRatingNode(final int id,
			final InputValuesSource source, final RatingNode input)
			throws MissingDataException {
		return new RatingNode(id, source, new Calculator() {
			public double getSize(final RatingNodeWithFilterState node)
					throws MissingDataException {
				return input.getFiltered().getSize();
			}

			public int getTupleSize() {
				return input.getTupleSize();
			}

			public double getEffectiveFInsert(final RatingNode node)
					throws MissingDataException {
				return input.getEffectiveFInsert();
			}

			public double getEffectiveFDelete(final RatingNode node)
					throws MissingDataException {
				return input.getEffectiveFDelete();
			}

			public double getMemoryCost(final RatingNode node)
					throws MissingDataException {
				return input.getTupleSize() * input.getFiltered().getSize();
			}

			public double getRuntimeCost(final RatingNode node)
					throws MissingDataException {
				return input.getEffectiveFInsert()
						+ input.getEffectiveFDelete();
			}
		});
	}

	public static RatingNode newLoopThroughRatingNode(final int id,
			final InputValuesSource source, final RatingNode loopThrough)
			throws MissingDataException {
		return new RatingNode(id, source, new Calculator() {

			public int getTupleSize() {
				return loopThrough.getTupleSize();
			}

			public double getSize(final RatingNodeWithFilterState node)
					throws MissingDataException {
				switch (node.getFilterState()) {
				case UNFILTERED:
					return loopThrough.getUnfiltered().getSize();
				case FILTERED:
					return loopThrough.getFiltered().getSize();
				}
				throw new IllegalArgumentException();
			}

			public double getRuntimeCost(final RatingNode node)
					throws MissingDataException {
				return 0;
			}

			public double getMemoryCost(final RatingNode node)
					throws MissingDataException {
				return 0;
			}

			public double getEffectiveFInsert(final RatingNode node)
					throws MissingDataException {
				return loopThrough.getEffectiveFInsert();
			}

			public double getEffectiveFDelete(final RatingNode node)
					throws MissingDataException {
				return loopThrough.getEffectiveFDelete();
			}
		});
	}

	public static class JoinListEntry {
		private final RatingNodeWithFilterState sourceNode, targetNode;

		public JoinListEntry(final RatingNode sourceNode,
				final RatingNode targetNode) {
			this.sourceNode = sourceNode.getFiltered();
			this.targetNode = targetNode.getFiltered();
		}

		public JoinListEntry(final RatingNodeWithFilterState sourceNode,
				final RatingNodeWithFilterState targetNode) {
			this.sourceNode = sourceNode;
			this.targetNode = targetNode;
		}

		public RatingNodeWithFilterState getSourceNode() {
			return sourceNode;
		}

		public RatingNodeWithFilterState getTargetNode() {
			return targetNode;
		}

		public double getTargetSize() {
			return this.targetNode.getSize();
		}

		@Override
		public String toString() {
			return "JoinListEntry [sourceNode=" + sourceNode + ", targetNode="
					+ targetNode + "]";
		}
	}

	public static class JoinList implements Iterable<JoinListEntry> {
		private final JoinListEntry[] joinList;

		public JoinList(final Collection<JoinListEntry> joinList) {
			this.joinList = joinList
					.toArray(new JoinListEntry[joinList.size()]);
		}

		public Iterator<JoinListEntry> iterator() {
			return Arrays.asList(this.joinList).iterator();
		}

		public JoinListEntry get(final int index) {
			return this.joinList[index];
		}

		public int size() {
			return this.joinList.length;
		}
	}

	public static class InputList implements Iterable<RatingNode> {
		private final RatingNode[] inputList;

		public InputList(final Collection<RatingNode> inputList) {
			this.inputList = inputList
					.toArray(new RatingNode[inputList.size()]);
		}

		public Iterator<RatingNode> iterator() {
			return Arrays.asList(this.inputList).iterator();
		}

		public RatingNode get(final int index) {
			return this.inputList[index];
		}

		public int size() {
			return this.inputList.length;
		}
	}

	public static class RatingNodeInputToJoinList {
		private final Map<RatingNode, JoinList> inputToJoinList;
		private final InputList inputList;

		public RatingNodeInputToJoinList(
				final Map<RatingNode, JoinList> inputToJoinList) {
			this.inputToJoinList = inputToJoinList;
			this.inputList = new InputList(inputToJoinList.keySet());
		}

		public JoinList getJoinList(final RatingNode node) {
			return this.inputToJoinList.get(node);
		}

		public InputList getInputNodes() {
			return this.inputList;
		}
	}

	static private int calcBetaTupleSize(
			final RatingNodeInputToJoinList positiveInputLists) {
		int sum = 0;
		final InputList inputs = positiveInputLists.getInputNodes();
		for (final RatingNode input : inputs) {
			sum += input.getTupleSize();
		}
		return sum;
	}

	static final private int joinListOffset = 2;

	static private JoinListEntry getJoinElem(final JoinList joinList,
			final int index) {
		return joinList.get(index - joinListOffset);
	}

	static private double VerbGr(final RatingNode baseNode,
			final RatingNode input, final RatingNodeInputToJoinList inputs)
			throws MissingDataException {
		final JoinList joinList = inputs.getJoinList(input);
		if (null == joinList)
			throw new MissingJoinListException(input, inputs);
		double prod = 1;
		for (int k = joinListOffset; k < joinList.size() + joinListOffset; ++k) {
			final JoinListEntry entry = getJoinElem(joinList, k);
			prod *= entry.getTargetSize() * baseNode.getJSF(entry);
		}
		return prod;
	}

	static private double calcBetaUnfilteredSize(final RatingNode baseNode,
			final RatingNodeInputToJoinList positiveInputs)
			throws MissingDataException {
		double size = 0;
		final InputList inputNodes = positiveInputs.getInputNodes();
		for (final RatingNode input : inputNodes) {
			size += input.getFiltered().getSize()
					* VerbGr(baseNode, input, positiveInputs);
		}
		return size / inputNodes.size();
	}

	static private double KostenPosEinfVarEins(final RatingNode baseNode,
			final RatingNode input,
			final RatingNodeInputToJoinList positiveInputLists)
			throws MissingDataException {
		final JoinList joinList = positiveInputLists.getJoinList(input);
		if (null == joinList) {
			throw new MissingJoinListException(input, positiveInputLists);
		}
		double cost = 0;
		double size = 1;
		for (int k = joinListOffset; k < joinList.size() + joinListOffset; ++k) {
			final JoinListEntry entry = getJoinElem(joinList, k);
			final double jsf = baseNode.getJSF(entry);
			cost += VerbK(baseNode, entry, size);
			size *= jsf * entry.getTargetSize();
		}
		// TODO this is new
		// cost += size * baseNode.getTupleSize(); // construct tuple
		// cost += cardenas(
		// Math.ceil(baseNode.getFiltered().getSize()
		// / baseNode.getTuplesPerPage()), size);
		// tupleSize / baseNode.getTuplesPerPage(); // write to node
		return cost;
	}

	public static RatingNode newSimpleBetaRatingNode(final int id,
			final InputValuesSource source,
			final RatingNodeInputToJoinList positiveInputLists)
			throws MissingDataException {
		return new RatingNode(id, source, new Calculator() {
			public double getSize(final RatingNodeWithFilterState node)
					throws MissingDataException {
				switch (node.getFilterState()) {
				case UNFILTERED:
					return calcBetaUnfilteredSize(node.getNode(),
							positiveInputLists);
				case FILTERED:
					// no difference for simple beta nodes
					return node.getNode().getUnfiltered().getSize();
				}
				throw new IllegalArgumentException();
			}

			public int getTupleSize() {
				return calcBetaTupleSize(positiveInputLists);
			}

			public double getEffectiveFInsert(final RatingNode node)
					throws MissingDataException {
				double sum = 0;
				final InputList inputs = positiveInputLists.getInputNodes();
				for (final RatingNode input : inputs) {
					sum += input.getEffectiveFInsert()
							* VerbGr(node, input, positiveInputLists);
				}
				return sum;
			}

			public double getEffectiveFDelete(final RatingNode node)
					throws MissingDataException {
				double sum = 0;
				final InputList inputs = positiveInputLists.getInputNodes();
				for (final RatingNode input : inputs) {
					sum += input.getEffectiveFDelete();
				}
				return sum;
			}

			public double getMemoryCost(final RatingNode node) {
				return node.getFiltered().getSize() * getTupleSize();
			}

			public double getRuntimeCost(final RatingNode node)
					throws MissingDataException {
				double cost = 0;
				final InputList inputs = positiveInputLists.getInputNodes();
				final double m = Math.ceil(node.getFiltered().getSize()
						/ node.getTuplesPerPage());
				for (final RatingNode input : inputs) {
					final double insC = Math.ceil(input.getEffectiveFInsert()
							* KostenPosEinfVarEins(node, input,
									positiveInputLists));
					final double delC = Math.ceil(input.getEffectiveFDelete()
							* (m + cardenas(m,
									VerbGr(node, input, positiveInputLists))));
					// System.out.println("node " + node.getId() + " m " + m
					// + " insC " + insC + " delC " + delC + " fodel "
					// + input.getEffectiveFDelete());
					cost += input.getEffectiveFInsert()
							* KostenPosEinfVarEins(node, input,
									positiveInputLists)
							+ input.getEffectiveFDelete()
							* (m + cardenas(m,
									VerbGr(node, input, positiveInputLists)));
				}
				return cost;
			}
		});
	}

	static private double KostenPosEinfVar2(final RatingNode baseNode,
			final RatingNode posInput,
			final RatingNodeInputToJoinList positiveInputLists,
			final RatingNodeInputToJoinList negativeInputLists)
			throws MissingDataException {
		double cost = 0;
		double size = 1;
		{
			final JoinList joinList = positiveInputLists.getJoinList(posInput);
			for (int k = joinListOffset; k < joinList.size() + joinListOffset; ++k) {
				final JoinListEntry entry = getJoinElem(joinList, k);
				cost += VerbK(baseNode, entry, size);
				size *= baseNode.getJSF(entry) * entry.getTargetSize();
			}
		}
		{
			final InputList negInputs = negativeInputLists.getInputNodes();
			for (final RatingNode N : negInputs) {
				cost += VerbK(baseNode, baseNode.getUnfiltered(),
						N.getFiltered(), size);
			}
		}
		cost += size;
		return cost;
	}

	static private double KostenNegLoeschVar2(final RatingNode baseNode,
			final RatingNode negInput,
			final RatingNodeInputToJoinList negativeInputLists)
			throws MissingDataException {
		double cost = 0;
		double size = 1;
		{
			final JoinList joinList = negativeInputLists.getJoinList(negInput);
			for (int k = joinListOffset; k < joinList.size() + joinListOffset; ++k) {
				final JoinListEntry entry = getJoinElem(joinList, k);
				cost += VerbK(baseNode, entry, size);
				size *= baseNode.getJSF(entry) * entry.getTargetSize();
			}
		}
		cost += size;
		return cost;
	}

	public static RatingNode newVar2BetaRatingNode(final int id,
			final InputValuesSource source,
			final RatingNodeInputToJoinList positiveInputLists,
			final RatingNodeInputToJoinList negativeInputLists)
			throws MissingDataException {
		return new RatingNode(id, source, new Calculator() {
			public double getSize(final RatingNodeWithFilterState node)
					throws MissingDataException {
				switch (node.getFilterState()) {
				case UNFILTERED:
					return calcBetaUnfilteredSize(node.getNode(),
							positiveInputLists);
				case FILTERED:
					double filteredSize = node.getNode().getUnfiltered()
							.getSize();
					final InputList negativeInputs = negativeInputLists
							.getInputNodes();
					for (final RatingNode negativeInput : negativeInputs) {
						filteredSize *= Math.pow(
								(1 - node.getNode().getJSF(
										node.getNode().getUnfiltered(),
										negativeInput.getFiltered())),
								negativeInput.getFiltered().getSize());
					}
					return filteredSize;
				}
				throw new IllegalArgumentException();
			}

			public int getTupleSize() {
				return calcBetaTupleSize(positiveInputLists);
			}

			public double getEffectiveFInsert(final RatingNode node)
					throws MissingDataException {
				double value = 0;
				{
					final InputList posInputs = positiveInputLists
							.getInputNodes();
					for (final RatingNode posInput : posInputs) {
						value += posInput.getEffectiveFInsert()
								* VerbGr(node, posInput, positiveInputLists);
					}
				}
				value *= node.getFiltered().getSize()
						/ node.getUnfiltered().getSize();
				{
					final InputList negInputs = negativeInputLists
							.getInputNodes();
					for (final RatingNode negInput : negInputs) {
						final double jsf = node.getJSF(node.getUnfiltered(),
								negInput.getFiltered());
						value += negInput.getEffectiveFDelete()
								* node.getFiltered().getSize() * jsf
								/ (1 - jsf);
					}
				}
				return value;
			}

			public double getEffectiveFDelete(final RatingNode node)
					throws MissingDataException {
				double value = 0;
				{
					final InputList posInputs = positiveInputLists
							.getInputNodes();
					for (final RatingNode posInput : posInputs) {
						value += posInput.getEffectiveFDelete();
					}
				}
				{
					final InputList negInputs = negativeInputLists
							.getInputNodes();
					for (final RatingNode negInput : negInputs) {
						value += negInput.getEffectiveFInsert()
								* node.getFiltered().getSize()
								* node.getJSF(node.getUnfiltered(),
										negInput.getFiltered());
					}
				}
				return value;
			}

			public double getMemoryCost(final RatingNode node) {
				return node.getUnfiltered().getSize()
						* (this.getTupleSize() + 0.15 * negativeInputLists
								.getInputNodes().size());
			}

			public double getRuntimeCost(final RatingNode node)
					throws MissingDataException {
				double value = 0;
				{
					final double m = Math.ceil(node.getFiltered().getSize()
							/ node.getTuplesPerPage());
					final InputList posInputs = positiveInputLists
							.getInputNodes();
					for (final RatingNode posInput : posInputs) {
						value += posInput.getEffectiveFDelete()
								* (m + cardenas(
										m,
										VerbGr(node, posInput,
												positiveInputLists)))
								+ posInput.getEffectiveFInsert()
								* KostenPosEinfVar2(node, posInput,
										positiveInputLists, negativeInputLists);
					}
				}
				{
					final InputList negInputs = negativeInputLists
							.getInputNodes();
					for (final RatingNode negInput : negInputs) {
						value += negInput.getEffectiveFInsert()
								* 2
								* VerbK(node, negInput.getFiltered(),
										node.getUnfiltered(), 1)
								+ negInput.getEffectiveFDelete()
								* KostenNegLoeschVar2(node, negInput,
										negativeInputLists);
					}
				}
				return value;
			}
		});
	}

}