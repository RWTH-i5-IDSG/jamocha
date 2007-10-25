package org.jamocha.jsr94;

import javax.rules.ConfigurationException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.RuleAdministrator;

/**
 * @author Josef Alexander Hahn
 */

public class JamochaRuleServiceProvider extends RuleServiceProvider {

	private static String RULE_SERVICE_PROVIDER = "org.jamocha.jsr94";

	private JamochaRuleExecutionSetMap ruleSets;
	
    private RuleRuntime ruleRuntime;

    private RuleAdministrator ruleAdministrator;
	
    // register our implementation
    static {
    	try {
    		RuleServiceProviderManager.registerRuleServiceProvider(RULE_SERVICE_PROVIDER, JamochaRuleServiceProvider.class);
    	} catch (ConfigurationException e) {
    		System.err.println("Error while registering us as rule service provider");
    	}
    }
    
    private JamochaRuleExecutionSetMap getRuleSets() {
    	if (ruleSets == null) ruleSets = new JamochaRuleExecutionSetMap();
    	return ruleSets;
    }

    @Override
	public RuleAdministrator getRuleAdministrator()	throws ConfigurationException {
    	if (ruleAdministrator == null) {
    		//TODO initialize it
    	}
    	return ruleAdministrator;
	}

	@Override
	public RuleRuntime getRuleRuntime() throws ConfigurationException {
		if (ruleRuntime == null) {
			//TODO initialize it
		}
		return ruleRuntime;
	}
	


}
