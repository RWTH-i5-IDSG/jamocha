package jamochaeditor;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * 
 * @author skka
 * NEEDS TO BE EXTENDED FOR * ** + < <= etc
 */
public class LetterDigitWordDetector implements IWordDetector {
	
	public boolean isWordPart(char c) {
		return (Character.isLetterOrDigit(c) || c=='$' || c=='*' ||
				c=='+' || c=='-' || c=='/' || c=='<' || c=='=' || c=='>');
		//return !(c < 'a') || !(c > 'z');
		//return true;
	}
	
	public boolean isWordStart(char c) {
		return Character.isLetterOrDigit(c);
		//return !(c < 'a') || !(c > 'z');
		//return true;
	}

}
