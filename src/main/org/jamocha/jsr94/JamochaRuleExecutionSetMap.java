package org.jamocha.jsr94;

import java.util.HashMap;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleExecutionSetMap {

	private Map<String,JamochaRuleExecutionSet> map;
	
	public JamochaRuleExecutionSetMap() {
		map = new HashMap<String, JamochaRuleExecutionSet>();
	}

	public JamochaRuleExecutionSet getRuleExecutionSet(String uri) {
		return map.get(uri);
	}
	
	public void putRuleExecutionSet(String uri, JamochaRuleExecutionSet res) {
		map.put(uri, res);
	}
	
	public void removeRuleExecutionSet(String uri) {
		map.remove(uri);
	}
	
}
