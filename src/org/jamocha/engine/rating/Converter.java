package org.jamocha.engine.rating;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.AlphaQuantorDistinctionNode;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.DummyNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeVisitor;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.OneInputNode;
import org.jamocha.engine.nodes.QuantorBetaFilterNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.nodes.TwoInputNode;
import org.jamocha.engine.rating.RatingNode.JoinList;
import org.jamocha.engine.rating.RatingNode.JoinListEntry;
import org.jamocha.engine.rating.RatingNode.RatingNodeInputToJoinList;
import org.jamocha.engine.rating.inputvalues.InputValuesSource;

public class Converter implements NodeVisitor {
	final InputValuesSource source;
	final Map<Integer, RatingNode> nodes = new Hashtable<Integer, RatingNode>();
	protected double memCost = 0, runCost = 0;

	public Converter(final InputValuesSource source) {
		this.source = source;
	}

	public void convert(final ReteNet net) {
		convert(net.getRoot());
	}

	private LinkedHashSet<Node> getAllNodes(final RootNode root) {
		final Stack<Node> active = new Stack<Node>();
		final LinkedHashSet<Node> result = new LinkedHashSet<Node>();
		active.add(root);
		result.add(root);
		while (!active.isEmpty()) {
			final Node n = active.pop();
			for (Node child : n.getChildNodes()) {
				active.add(child);
				result.add(child);
			}
		}
		return result;
	}

	private void refreshCost() {
		final double[] mem = new double[this.nodes.size()];
		final double[] run = new double[this.nodes.size()];
		int i = 0;
		for (final RatingNode node : this.nodes.values()) {
			mem[i] = node.getMemoryCost();
			run[i] = node.getRuntimeCost();
			++i;
		}
		// sorting is necessary to optimize problem condition
		Arrays.sort(mem);
		Arrays.sort(run);
		this.memCost = 0;
		for (final double value : mem)
			this.memCost += value;
		this.runCost = 0;
		for (final double value : run)
			this.runCost += value;
	}

	public void convert(final RootNode root) {
		final LinkedHashSet<Node> allNodes = getAllNodes(root);
		for (final Node node : allNodes) {
			node.accept(this);
		}
		refreshCost();
	}

	public String getDetails() {
		final StringBuffer str = new StringBuffer();
		final SortedSet<Integer> keySet = new TreeSet<Integer>(nodes.keySet());
		assert keySet.size() == nodes.size();
		for (final Integer key : keySet) {
			final RatingNode node = nodes.get(key);
			str.append("node: " + node.getId() + "\tmem: "
					+ node.getMemoryCost() + "\tcpu: " + node.getRuntimeCost()
					+ "\tefi: " + node.getEffectiveFInsert() + "\tefd: "
					+ node.getEffectiveFDelete() + "\n");
		}
		return str.toString();
	}

	public double getMemCost() {
		return memCost;
	}

	public double getRunCost() {
		return runCost;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("network memory cost: ");
		sb.append(Math.ceil(memCost));
		sb.append(", network runtime cost: ");
		sb.append(Math.ceil(runCost));
		return sb.toString();
	}

	/**
	 * negatable<br/>
	 * <ul>
	 * <li>positive: only beta-tuple for every alpha-fact matching</li>
	 * <li>negative: negated beta-node with positive beta input, negative alpha
	 * input</li>
	 * </ul>
	 * cost: beta-node Var2 (both cases)
	 */
	public void visit(final QuantorBetaFilterNode node) {
		if (this.nodes.containsKey(node.getId()))
			return;
		final RatingNode alpha = getAlphaInput(node);
		final RatingNode beta = getBetaInput(node);
		final Map<RatingNode, JoinList> positiveInputLists = new HashMap<RatingNode, JoinList>();
		final Map<RatingNode, JoinList> negativeInputLists = new HashMap<RatingNode, JoinList>();
		if (node.isNegated()) {
			positiveInputLists.put(beta,
					new JoinList(Arrays.<JoinListEntry> asList()));
			negativeInputLists.put(
					alpha,
					new JoinList(Arrays
							.<JoinListEntry> asList(new JoinListEntry(alpha,
									beta))));
		} else {
			positiveInputLists.put(
					alpha,
					new JoinList(Arrays
							.<JoinListEntry> asList(new JoinListEntry(alpha,
									beta))));
			positiveInputLists.put(
					beta,
					new JoinList(Arrays
							.<JoinListEntry> asList(new JoinListEntry(beta,
									alpha))));
		}
		this.nodes.put(node.getId(), RatingNode.newVar2BetaRatingNode(node
				.getId(), this.source, new RatingNodeInputToJoinList(
				positiveInputLists), new RatingNodeInputToJoinList(
				negativeInputLists)));
	}

	private void visitAlphaNode(final int id) {
		if (this.nodes.containsKey(id))
			return;
		this.nodes.put(id, RatingNode.newAlphaRatingNode(id, this.source));
	}

	/**
	 * filters facts having the same values in distinctSlots<br/>
	 * cost: alpha-node
	 */
	public void visit(final AlphaQuantorDistinctionNode node) {
		visitAlphaNode(node.getId());
	}

	/**
	 * alpha-node<br/>
	 * cost: alpha-node
	 */
	public void visit(final AlphaSlotComparatorNode node) {
		visitAlphaNode(node.getId());
	}

	public void visit(final DummyNode node) {
		// no operation needed
	}

	/**
	 * wraps alpha to beta, no cost
	 */
	public void visit(final LeftInputAdaptorNode node) {
		if (this.nodes.containsKey(node.getId()))
			return;
		// no values needed from source
		this.nodes.put(node.getId(), RatingNode.newLoopThroughRatingNode(
				node.getId(), this.source, getAlphaInput(node)));
	}

	/**
	 * Gator-node<br/>
	 * cost: simple beta-node
	 */
	public void visit(final MultiBetaJoinNode node) {
		if (this.nodes.containsKey(node.getId()))
			return;
		final RatingNode[] inputs = getInputs(node);
		final Map<RatingNode, JoinList> map = new HashMap<RatingNode, JoinList>();
		for (int index = 0; index < inputs.length; ++index) {
			map.put(inputs[index],
					createJamochaJoinListForInput(node.getId(), index, inputs));
		}
		this.nodes.put(node.getId(), RatingNode.newSimpleBetaRatingNode(
				node.getId(), this.source, new RatingNodeInputToJoinList(map)));
	}

	private JoinList createJamochaJoinListForInput(final int conditionNode,
			final int index, final RatingNode[] inputs) {
		final Vector<JoinListEntry> joinList = new Vector<JoinListEntry>(
				inputs.length - 1);
		createJamochaJoinList(conditionNode, index, inputs, joinList);
		return new JoinList(joinList);
	}

	private void createJamochaJoinList(final int conditionNode,
			final int index, final RatingNode[] inputs,
			final List<JoinListEntry> joinList) {
		if (1 == inputs.length) {
			return;
		}
		final RatingNodeWithFilterState source = inputs[index].getFiltered();
		final RatingNode[] remain = new RatingNode[inputs.length - 1];
		System.arraycopy(inputs, 0, remain, 0, index);
		System.arraycopy(inputs, index + 1, remain, index, inputs.length - 1
				- index);
		// try to find a node in the list that can be joined to the latest one
		if (createJamochaJoinList(conditionNode, source, index, remain,
				joinList))
			return;
		// try to find a node in the list that can be joined to another
		// already chosen node
		for (ListIterator<JoinListEntry> iter = joinList.listIterator(joinList
				.size()); iter.hasPrevious();) {
			final JoinListEntry previous = iter.previous();
			if (createJamochaJoinList(conditionNode, previous.getSourceNode(),
					index, remain, joinList)) {
				return;
			}
		}
		// there is no node that can be joined, just do the Cartesian product
		final int fallbackIndex = index > 0 ? index - 1 : index;
		joinList.add(new JoinListEntry(source, remain[fallbackIndex]
				.getFiltered()));
		createJamochaJoinList(conditionNode, fallbackIndex, remain, joinList);
	}

	private boolean createJamochaJoinList(final int conditionNode,
			final RatingNodeWithFilterState source, final int index,
			final RatingNode[] remain, final List<JoinListEntry> joinList) {
		for (int i = 0; i < remain.length; ++i) {
			if (1 != this.source.getJSF(conditionNode, source,
					remain[i].getFiltered())) {
				joinList.add(new JoinListEntry(source, remain[i].getFiltered()));
				createJamochaJoinList(conditionNode, i, remain, joinList);
				return true;
			}
		}
		return false;
	}

	/**
	 * object type filter<br/>
	 * cost: alpha-node
	 */
	public void visit(final ObjectTypeNode node) {
		visitAlphaNode(node.getId());
	}

	/**
	 * root node<br/>
	 * cost: none
	 */
	public void visit(final RootNode node) {
		// no operation needed
	}

	/**
	 * RETE-node<br/>
	 * cost: simple beta-node
	 */
	public void visit(final SimpleBetaFilterNode node) {
		if (this.nodes.containsKey(node.getId()))
			return;
		final RatingNode alpha = getAlphaInput(node);
		final RatingNode beta = getBetaInput(node);
		final Map<RatingNode, JoinList> map = new HashMap<RatingNode, JoinList>();
		map.put(alpha,
				new JoinList(Arrays.<JoinListEntry> asList(new JoinListEntry(
						alpha, beta))));
		map.put(beta,
				new JoinList(Arrays.<JoinListEntry> asList(new JoinListEntry(
						beta, alpha))));
		this.nodes.put(node.getId(), RatingNode.newSimpleBetaRatingNode(
				node.getId(), this.source, new RatingNodeInputToJoinList(map)));
	}

	/**
	 * alpha-node<br/>
	 * cost: alpha-node
	 */
	public void visit(final SlotFilterNode node) {
		visitAlphaNode(node.getId());
	}

	/**
	 * terminal node<br/>
	 * cost: terminal node
	 */
	public void visit(final TerminalNode node) {
		if (this.nodes.containsKey(node.getId()))
			return;
		// no values needed from source
		this.nodes.put(node.getId(), RatingNode.newTerminalRatingNode(
				node.getId(), this.source, getAlphaInput(node)));
	}

	public RatingNode getRatingNode(final Node node) {
		RatingNode ratingNode = this.nodes.get(node.getId());
		if (null == ratingNode) {
			node.accept(this);
			ratingNode = this.nodes.get(node.getId());
			assert null != ratingNode;
		}
		return ratingNode;
	}

	protected RatingNode getAlphaInput(final OneInputNode node) {
		return getRatingNode(node.getAlphaInput());
	}

	protected RatingNode getBetaInput(final TwoInputNode node) {
		return getRatingNode(node.getBetaInput());
	}

	protected RatingNode[] getInputs(final MultiBetaJoinNode node) {
		final Node[] inputs = node.getParentNodes();
		final RatingNode[] ratingInputs = new RatingNode[inputs.length];
		for (int index = 0; index < inputs.length; ++index) {
			ratingInputs[index] = getRatingNode(inputs[index]);
		}
		return ratingInputs;
	}

}
