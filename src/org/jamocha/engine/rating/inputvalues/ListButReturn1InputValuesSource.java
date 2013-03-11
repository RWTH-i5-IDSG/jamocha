package org.jamocha.engine.rating.inputvalues;

import java.util.HashMap;
import java.util.HashSet;

public class ListButReturn1InputValuesSource extends AbstractInputValuesImpl {

	final static String newline = System.getProperty("line.separator");

	final HashSet<NodeContainer> fdelete, finsert, size;
	final HashMap<Integer, HashSet<NodeContainerPair>> jsf;

	public ListButReturn1InputValuesSource() {
		this.fdelete = new HashSet<NodeContainer>();
		this.finsert = new HashSet<NodeContainer>();
		this.size = new HashSet<NodeContainer>();
		this.jsf = new HashMap<Integer, HashSet<NodeContainerPair>>();
	}

	private HashSet<NodeContainerPair> jsfGet(final int conditionNode) {
		HashSet<NodeContainerPair> inner = jsf.get(conditionNode);
		if (null != inner) {
			return inner;
		}
		inner = new HashSet<NodeContainerPair>();
		jsf.put(conditionNode, inner);
		return inner;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("FDelete values needed for nodes: ");
		sb.append(fdelete);
		sb.append(newline);
		sb.append("FInsert values needed for nodes: ");
		sb.append(finsert);
		sb.append(newline);
		sb.append("Size values needed for nodes: ");
		sb.append(size);
		sb.append(newline);
		sb.append("jsf values needed for node pairs: ");
		sb.append(jsf);
		return sb.toString();
	}

	public void setSize(NodeContainer node, double value) {
	}

	public void setFInsert(NodeContainer node, double value) {
	}

	public void setFDelete(NodeContainer node, double value) {
	}

	@Override
	protected void setJSF(final int conditionNode,
			final NodeContainerPair nodePair, final double value) {
	}

	@Override
	protected Double requestSize(final NodeContainer node) {
		this.size.add(node);
		return 1.0;
	}

	@Override
	protected Double requestFInsert(final NodeContainer node) {
		this.finsert.add(node);
		return 1.0;
	}

	@Override
	protected Double requestFDelete(final NodeContainer node) {
		this.fdelete.add(node);
		return 1.0;
	}

	@Override
	protected Double requestJSF(final int conditionNode,
			final NodeContainerPair nodePair) {
		jsfGet(conditionNode).add(nodePair);
		return 1.0;
	}

}