package org.jamocha.engine.rating.inputvalues;

public interface InputValuesSink {
	public void setSize(final NodeContainer node, final double value);

	public void addCalculatedSize(final NodeContainer node, final double value);

	public void setFInsert(final NodeContainer node, final double value);

	public void addCalculatedFInsert(final NodeContainer node,
			final double value);

	public void setFDelete(final NodeContainer node, final double value);

	public void addCalculatedFDelete(final NodeContainer node,
			final double value);

	public void setJSF(final int conditionNode, final NodeContainer node1,
			final NodeContainer node2, final double value);

	public void addCalculatedJSF(final int conditionNode,
			final NodeContainer node1, final NodeContainer node2,
			final double value);

	public void setTuplesPerPage(final double value);
}
