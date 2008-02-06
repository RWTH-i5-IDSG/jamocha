package org.jamocha.jsr94;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 */

public class JamochaRule implements javax.rules.admin.Rule {

	public static final String JAMOCHA_RULE_OBJECT = "jamocha_rule_object";
	
	private static final long serialVersionUID = 1L;

	private final org.jamocha.rule.Rule rule;
	
	public JamochaRule(org.jamocha.rule.Rule rule) {
		this.rule = rule;
	}
	
	public String getDescription() {
		return rule.getDescription();
	}

	public String getName() {
		return rule.getName();
	}

	public Object getProperty(Object arg0) {
		if (arg0.equals(JAMOCHA_RULE_OBJECT)) {
			return rule;
		}
		
		// TODO do something useful with this method
		return null;
	}

	public void setProperty(Object arg0, Object arg1) {
		//TODO do something useful with this method
	}
	

}
