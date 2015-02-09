package org.jamocha.engine.rating.inputvalues;

import org.jamocha.engine.rating.RatingNodeWithFilterState;
import org.jamocha.engine.rating.exceptions.MissingDataException;

public abstract class AbstractInputValuesImpl implements InputValuesSink,
		InputValuesSource {
	protected double tps = 10;

	@Override
	public double getSize(final RatingNodeWithFilterState node)
			throws MissingDataException {
		return getSize(new NodeContainer(node.getNode().getId(),
				node.getFilterState()));
	}

	@Override
	public double getFInsert(final RatingNodeWithFilterState node)
			throws MissingDataException {
		return getFInsert(new NodeContainer(node.getNode().getId(),
				node.getFilterState()));
	}

	@Override
	public double getFDelete(final RatingNodeWithFilterState node)
			throws MissingDataException {
		return getFDelete(new NodeContainer(node.getNode().getId(),
				node.getFilterState()));
	}

	@Override
	public double getJSF(final int conditionNode,
			final RatingNodeWithFilterState node1,
			final RatingNodeWithFilterState node2) throws MissingDataException {
		return getJSF(conditionNode, new NodeContainer(node1.getNode().getId(),
				node1.getFilterState()), new NodeContainer(node2.getNode()
				.getId(), node2.getFilterState()));
	}

	@Override
	public void addCalculatedSize(final NodeContainer node, final double value) {
		final Double was = requestSize(node);
		if (null == was) {
			setSize(node, value);
		}
	}

	@Override
	public void addCalculatedFInsert(final NodeContainer node,
			final double value) {
		final Double was = requestFInsert(node);
		if (null == was) {
			setFInsert(node, value);
		}
	}

	@Override
	public void addCalculatedFDelete(final NodeContainer node,
			final double value) {
		final Double was = requestFDelete(node);
		if (null == was) {
			setFDelete(node, value);
		}
	}

	@Override
	public void addCalculatedJSF(final int conditionNode,
			final NodeContainer node1, final NodeContainer node2,
			final double value) {
		final Double was = requestJSF(conditionNode, node1, node2);
		if (null == was) {
			setJSF(conditionNode, node1, node2, value);
		}
	}

	@Override
	public void setJSF(final int conditionNode, final NodeContainer node1,
			final NodeContainer node2, final double value) {
		setJSF(conditionNode, new NodeContainerPair(node1, node2), value);
	}

	abstract protected void setJSF(final int conditionNode,
			final NodeContainerPair nodePair, final double value);

	abstract protected Double requestSize(final NodeContainer node);

	abstract protected Double requestFInsert(final NodeContainer node);

	abstract protected Double requestFDelete(final NodeContainer node);

	private Double requestJSF(final int conditionNode,
			final NodeContainer node1, final NodeContainer node2) {
		final Double value = requestJSF(conditionNode, new NodeContainerPair(
				node1, node2));
		if (null != value) {
			return value;
		}
		return requestJSF(conditionNode, new NodeContainerPair(node2, node1));
	}

	abstract protected Double requestJSF(final int conditionNode,
			final NodeContainerPair nodePair);

	@Override
	public double getSize(final NodeContainer node) throws MissingDataException {
		final Double size = requestSize(node);
		if (null != size)
			return size;
		throw new MissingDataException("Size missing!", node);
	}

	@Override
	public double getFDelete(final NodeContainer node)
			throws MissingDataException {
		final Double finsert = requestFInsert(node);
		if (null != finsert)
			return finsert;
		throw new MissingDataException("FInsert missing!", node);
	}

	@Override
	public double getFInsert(final NodeContainer node)
			throws MissingDataException {
		final Double fdelete = requestFDelete(node);
		if (null != fdelete)
			return fdelete;
		throw new MissingDataException("FDelete missing!", node);
	}

	@Override
	public double getJSF(final int conditionNode, final NodeContainer node1,
			final NodeContainer node2) throws MissingDataException {
		final Double jsf = requestJSF(conditionNode, node1, node2);
		if (null != jsf)
			return jsf;
		throw new MissingDataException("JSF missing in node " + conditionNode
				+ "!", node1, node2);
	}

	@Override
	public void setTuplesPerPage(final double value) {
		this.tps = value;
	}

	@Override
	public double getTuplesPerPage() throws MissingDataException {
		return this.tps;
	}

}
