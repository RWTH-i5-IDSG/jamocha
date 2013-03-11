package org.jamocha.engine.rating.inputvalues;

import org.jamocha.engine.rating.RatingNodeWithFilterState;
import org.jamocha.engine.rating.exceptions.MissingDataException;

public interface InputValuesSource {
	public double getSize(final RatingNodeWithFilterState node)
			throws MissingDataException;

	public double getFInsert(final RatingNodeWithFilterState node)
			throws MissingDataException;

	public double getFDelete(final RatingNodeWithFilterState node)
			throws MissingDataException;

	public double getJSF(final int conditionNode,
			final RatingNodeWithFilterState node1,
			final RatingNodeWithFilterState node2) throws MissingDataException;

	public double getSize(final NodeContainer node) throws MissingDataException;

	public double getFInsert(final NodeContainer node)
			throws MissingDataException;

	public double getFDelete(final NodeContainer node)
			throws MissingDataException;

	public double getJSF(final int conditionNode, final NodeContainer node1,
			final NodeContainer node2) throws MissingDataException;

	public double getTuplesPerPage() throws MissingDataException;
}
