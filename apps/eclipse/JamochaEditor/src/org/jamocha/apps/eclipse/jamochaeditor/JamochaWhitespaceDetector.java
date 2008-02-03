package org.jamocha.apps.eclipse.jamochaeditor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;



public class JamochaWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char character) {
		return (Character.isWhitespace(character) || character == ' ');
	}
}