package org.jamocha.jsr94;

import java.util.HashMap;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 */
public class JamochaRuleExecutionSetMap {

	private Map<String,RuleExecutionSet> map;
	
	public JamochaRuleExecutionSetMap() {
		map = new HashMap<String, RuleExecutionSet>();
	}

	public RuleExecutionSet getRuleExecutionSet(String uri) {
		return map.get(uri);
	}
	
	public void putRuleExecutionSet(String uri, RuleExecutionSet res) {
		map.put(uri, res);
	}
	
	public void removeRuleExecutionSet(String uri) {
		map.remove(uri);
	}
	
}
