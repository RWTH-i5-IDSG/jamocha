package org.jamocha.jsr94;

import javax.rules.RuleExecutionSetMetadata;
import javax.rules.admin.RuleExecutionSet;

public class JamochaRuleExecutionSetMetadata implements	RuleExecutionSetMetadata {

	private RuleExecutionSet res;
	
	private String uri;
	
	public JamochaRuleExecutionSetMetadata(RuleExecutionSet res, String uri) {
		this.res = res;
		this.uri = uri;
	}
	

	public String getDescription() {
		return res.getDescription();
	}

	public String getName() {
		return res.getName();
	}

	public String getUri() {
		return uri;
	}

}
