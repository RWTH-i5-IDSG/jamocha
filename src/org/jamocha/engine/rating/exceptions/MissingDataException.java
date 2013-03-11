package org.jamocha.engine.rating.exceptions;

import org.jamocha.engine.rating.inputvalues.NodeContainer;

public class MissingDataException extends RuntimeException {
	public static String newline = System.getProperty("line.separator");
	private static final long serialVersionUID = -6964578511683349899L;
	final String message;
	final NodeContainer nodes[];

	public MissingDataException(final String message,
			final NodeContainer... nodes) {
		this.message = message;
		this.nodes = nodes;
	}

	@Override
	public String getMessage() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.message);
		for (final NodeContainer node : this.nodes) {
			sb.append(newline);
			sb.append(node.toString());
		}
		return sb.toString();
	}
}