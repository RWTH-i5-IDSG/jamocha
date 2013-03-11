package org.jamocha.engine.rating.inputvalues;

public class DummyInputValuesSource extends AbstractInputValuesImpl {

	public void setSize(final NodeContainer node, double value) {
	}

	public void setFInsert(final NodeContainer node, double value) {
	}

	public void setFDelete(final NodeContainer node, double value) {
	}

	@Override
	protected void setJSF(final int conditionNode,
			final NodeContainerPair nodePair, final double value) {
	}

	@Override
	protected Double requestSize(final NodeContainer node) {
		return null;
	}

	@Override
	protected Double requestFInsert(final NodeContainer node) {
		return null;
	}

	@Override
	protected Double requestFDelete(final NodeContainer node) {
		return null;
	}

	@Override
	protected Double requestJSF(final int conditionNode,
			final NodeContainerPair nodePair) {
		return null;
	}

}