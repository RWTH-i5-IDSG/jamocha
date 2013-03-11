package org.jamocha.engine.rating;

import org.jamocha.engine.rating.exceptions.MissingDataException;

public class RatingNodeWithFilterState {
	private final RatingNode node;
	private final FilterState filterState;
	private final double size;

	public RatingNodeWithFilterState(final RatingNode node,
			final FilterState filterState,
			final RatingNode.Calculator calculator) throws MissingDataException {
		this.node = node;
		this.filterState = filterState;
		this.size = calculator.getSize(this);
	}

	public RatingNode getNode() {
		return this.node;
	}

	public FilterState getFilterState() {
		return this.filterState;
	}

	public double getSize() {
		return this.size;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(this.node.id));
		sb.append(" [");
		sb.append(this.filterState.toString());
		sb.append("]");
		return sb.toString();
	}

}