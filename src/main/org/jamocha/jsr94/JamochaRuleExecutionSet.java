package org.jamocha.jsr94;

import java.util.ArrayList;
import java.util.List;

import javax.rules.admin.RuleExecutionSet;

import org.jamocha.rule.Rule;

public class JamochaRuleExecutionSet implements RuleExecutionSet {

	private static final long serialVersionUID = 1L;

	private String description;
	
	private String name;
	
	private String defaultFilter;
	
	private Rule[] rules;
	
	public JamochaRuleExecutionSet(String description, String name, Rule[] rules) {
		this.description = description;
		this.name = name;
		this.rules = rules;
	}
	
	public String getDefaultObjectFilter() {
		return defaultFilter;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public Object getProperty(Object arg0) {
		// TODO do something useful with them
		return null;
	}

	@SuppressWarnings("unchecked")
	public List getRules() {
		List result = new ArrayList(rules.length);
		for (Rule r : rules) {
			result.add(r);
		}
		return result;
	}

	public void setDefaultObjectFilter(String defFilter) {
		this.defaultFilter = defFilter;
	}

	public void setProperty(Object arg0, Object arg1) {
		// TODO do something useful with them
	}

}
