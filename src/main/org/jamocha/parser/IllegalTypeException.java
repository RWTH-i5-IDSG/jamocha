package org.jamocha.parser;

public class IllegalTypeException extends EvaluationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalTypeException(JamochaType[] expected, JamochaType found) {
		super(createMessage(expected, found));
	}

	private static String createMessage(JamochaType[] expected, JamochaType found) {
		StringBuilder sb = new StringBuilder();
		sb.append("Illegal type, expected ");
		for(int i=0; i<expected.length -2; ++i) {
			sb.append(expected[i]).append(", ");
		}
		if(expected.length > 1) {
			sb.append(expected[expected.length-2]).append(" or ");
		}
		if(expected.length > 0) {
			sb.append(expected[expected.length-1]).append(", ");
		}
		sb.append("found ").append(found);
		return sb.toString();
	}

}
