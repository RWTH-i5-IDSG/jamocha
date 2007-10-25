package org.jamocha.jsr94;

/**
 * @author Josef Alexander Hahn
 */

public class JamochaRule implements javax.rules.admin.Rule {

	private static final long serialVersionUID = 1L;

	private final org.jamocha.rule.Rule rule;
	
	public JamochaRule(org.jamocha.rule.Rule rule) {
		this.rule = rule;
	}
	
	@Override
	public String getDescription() {
		return rule.getDescription();
	}

	@Override
	public String getName() {
		return rule.getName();
	}

	@Override
	public Object getProperty(Object arg0) {
		// TODO do something useful with this method
		return null;
	}

	@Override
	public void setProperty(Object arg0, Object arg1) {
		//TODO do something useful with this method
	}

}
