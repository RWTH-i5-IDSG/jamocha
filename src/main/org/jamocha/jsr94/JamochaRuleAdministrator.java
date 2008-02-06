package org.jamocha.jsr94;

import java.rmi.RemoteException;
import java.util.Map;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSetRegisterException;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleAdministrator implements RuleAdministrator {

	private JamochaRuleExecutionSetMap ruleExecutionSets;
	
	private JamochaLocalRuleExecutionSetProvider localREprov;
	
	public JamochaRuleAdministrator(JamochaRuleExecutionSetMap ruleExecutionSets) {
		super();
		this.ruleExecutionSets = ruleExecutionSets;
	}

	public void deregisterRuleExecutionSet(String uri, Map properties) throws RuleExecutionSetDeregistrationException, RemoteException {
		ruleExecutionSets.removeRuleExecutionSet(uri);
	}

	public LocalRuleExecutionSetProvider getLocalRuleExecutionSetProvider(Map properties) throws RemoteException {
		if (localREprov == null) {
			localREprov = new JamochaLocalRuleExecutionSetProvider();
		}
		return localREprov;
	}

	public RuleExecutionSetProvider getRuleExecutionSetProvider(Map properties) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerRuleExecutionSet(String uri, RuleExecutionSet execSet, Map properties) throws RuleExecutionSetRegisterException, RemoteException {
		ruleExecutionSets.putRuleExecutionSet(uri, execSet);
	}

}
