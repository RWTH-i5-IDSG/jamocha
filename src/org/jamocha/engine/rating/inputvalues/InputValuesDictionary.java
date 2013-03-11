package org.jamocha.engine.rating.inputvalues;

import java.util.HashMap;

public class InputValuesDictionary extends AbstractInputValuesImpl {

	final HashMap<NodeContainer, Double> fdelete, finsert, size;
	final HashMap<Integer, HashMap<NodeContainerPair, Double>> condNodeToJSF;

	public InputValuesDictionary() {
		this.fdelete = new HashMap<NodeContainer, Double>();
		this.finsert = new HashMap<NodeContainer, Double>();
		this.size = new HashMap<NodeContainer, Double>();
		this.condNodeToJSF = new HashMap<Integer, HashMap<NodeContainerPair, Double>>();
	}

	private HashMap<NodeContainerPair, Double> jsfMapGet(final int conditionNode) {
		HashMap<NodeContainerPair, Double> inner = condNodeToJSF
				.get(conditionNode);
		if (null != inner) {
			return inner;
		}
		inner = new HashMap<NodeContainerPair, Double>();
		condNodeToJSF.put(conditionNode, inner);
		return inner;
	}

	public void setSize(final NodeContainer node, final double value) {
		this.size.put(node, value);
	}

	public void setFInsert(final NodeContainer node, final double value) {
		this.finsert.put(node, value);
	}

	public void setFDelete(final NodeContainer node, final double value) {
		this.fdelete.put(node, value);
	}

	@Override
	protected void setJSF(final int conditionNode,
			final NodeContainerPair nodePair, final double value) {
		jsfMapGet(conditionNode).put(nodePair, value);
	}

	@Override
	protected Double requestSize(final NodeContainer node) {
		return this.size.get(node);
	}

	@Override
	protected Double requestFInsert(final NodeContainer node) {
		return this.finsert.get(node);
	}

	@Override
	protected Double requestFDelete(final NodeContainer node) {
		return this.fdelete.get(node);
	}

	@Override
	protected Double requestJSF(final int conditionNode,
			final NodeContainerPair nodePair) {
		return jsfMapGet(conditionNode).get(nodePair);
	}

}