package org.jamocha.jsr94;

import java.util.ArrayList;
import java.util.List;

import javax.rules.admin.RuleExecutionSet;

import org.jamocha.parser.Expression;
import org.jamocha.rete.configurations.DefruleConfiguration;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleExecutionSet implements RuleExecutionSet {

	private static final long serialVersionUID = 1L;

	private String description;
	
	private String name;
	
	private String defaultFilter;
	
	private Expression[] exprs;
	
	public JamochaRuleExecutionSet(String description, String name, Expression[] exprs) {
		this.description = description;
		this.name = name;
		this.exprs = exprs;
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
		List result = new ArrayList(exprs.length);
		for (Expression r : exprs) {
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

	public Expression[] getExpressions() {
		return exprs;
	}

}
