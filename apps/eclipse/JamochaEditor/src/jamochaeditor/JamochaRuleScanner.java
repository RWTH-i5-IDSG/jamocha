package jamochaeditor;

import java.util.Vector;


import org.eclipse.jface.text.rules.*;




public class JamochaRuleScanner extends RuleBasedScanner {
	private IToken defaultToken;
	
	
	public JamochaRuleScanner() {
		defaultToken = new EasyToken("0,0,0");//this is the default token for default text
		Vector rules = new Vector();
		
		
		//whitespaces
		rules.add(new WhitespaceRule(new JamochaWhitespaceDetector()));
		
		//single line comments
	    EndOfLineRule rule = new EndOfLineRule(";", new EasyToken("180,180,180",false,true)); 
		rules.add( rule );
		
		//rules for strings
		rules.add(new MultiLineRule("'", "'", new EasyToken("210,0,240")));
		rules.add(new MultiLineRule("\"", "\"", new EasyToken("210,0,240")));
		
		//rules.add(new SingleLineRule("?",null, new EasyToken("0,240,230")));
		//rules.add(new SingleLineRule("$?",null, new EasyToken("0,240,230")));
		
		//functions
		WordRuleFromFile functions = new WordRuleFromFile("files/functions.txt",
				defaultToken,"0,180,220",true,false);
		rules.add(functions);
		
		
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
		
	}

}
