package org.jamocha.engine.rating.exceptions;

import org.jamocha.engine.rating.RatingNode;
import org.jamocha.engine.rating.RatingNode.RatingNodeInputToJoinList;

public class MissingJoinListException extends RuntimeException {

	private static final long serialVersionUID = -7889835837041005986L;

	final RatingNode input;
	final RatingNodeInputToJoinList inputs;

	public MissingJoinListException(final RatingNode input,
			final RatingNodeInputToJoinList inputs) {
		this.input = input;
		this.inputs = inputs;
	}

	@Override
	public String getMessage() {
		final StringBuilder sb = new StringBuilder();
		sb.append("The InpuToJoinList[");
		sb.append(this.inputs);
		sb.append("] did not contain an entry for input [");
		sb.append(this.input);
		sb.append(']');
		return sb.toString();
	}

}
