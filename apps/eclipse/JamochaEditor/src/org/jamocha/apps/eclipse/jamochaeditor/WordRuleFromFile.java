package org.jamocha.apps.eclipse.jamochaeditor;

import org.eclipse.jface.text.rules.*;


import org.eclipse.swt.widgets.Shell;


//import java.io.*;
import java.util.*;

/*
 * Extends WorldRule 
 * Adds a constructor that allows adding words from a textfile
 */
public class WordRuleFromFile extends WordRule {
	private IToken token;
	
	/**
	 * 
	 * @param filename	name of the file that contains words to highlight
	 * @param default_token	token to apply to text that does NOT match
	 * @param highlightcolor string that represents rgb values (e.g. "255,100,100")
	 */
	public WordRuleFromFile(String filename,IToken default_token,String highlightcolor,boolean bold,boolean italic) {
		super(new LetterDigitWordDetector(),default_token);
		token = new EasyToken(highlightcolor,bold,italic);
		
		Vector<String> vec = new VectorFromFile("files/functions.txt");
		for(Iterator<String> it = vec.iterator();it.hasNext();)
			this.addWord(it.next(), token);

	}
		
	
}
