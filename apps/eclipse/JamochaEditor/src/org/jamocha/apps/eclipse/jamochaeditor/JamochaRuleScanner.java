package org.jamocha.apps.eclipse.jamochaeditor;

import java.util.Vector;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.*;




public class JamochaRuleScanner extends RuleBasedScanner {
	private IToken defaultToken;
	
	
	public JamochaRuleScanner() {
		defaultToken = new EasyToken("0,0,0");//this is the default token for default text
		Vector rules = new Vector();
		IPreferenceStore prefs = JamochaPlugin.getDefault().getPreferenceStore();
		
		
		//whitespaces
		rules.add(new WhitespaceRule(new JamochaWhitespaceDetector()));
		
		//single line comments
	    //EndOfLineRule rule = new EndOfLineRule(";", new EasyToken("180,180,180",false,true));
		EndOfLineRule rule = new EndOfLineRule(";",
				new EasyToken(prefs.getString(JamochaPlugin.PREF_COLOR_COMMENT)
						,false,true));
		
		rules.add( rule );
		
		//rules for strings
		rules.add(new MultiLineRule("'", "'", new EasyToken(
				prefs.getString(JamochaPlugin.PREF_COLOR_STRING))));
		rules.add(new MultiLineRule("\"", "\"", new EasyToken(
				prefs.getString(JamochaPlugin.PREF_COLOR_STRING))));
		
		//rules.add(new SingleLineRule("?",null, new EasyToken("0,240,230")));
		//rules.add(new SingleLineRule("$?",null, new EasyToken("0,240,230")));
		
		//functions
		WordRuleFromFile functions = new WordRuleFromFile("files/functions.txt",
				defaultToken,
				prefs.getString(JamochaPlugin.PREF_COLOR_FUNCTION),
				true,false);
		rules.add(functions);
		
		
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
		
	}

}
