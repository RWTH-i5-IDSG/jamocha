package org.jamocha.jsr94;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;

/**
 * @author Josef Alexander Hahn
 */

public class JamochaRuleRuntime implements RuleRuntime {

	private static final long serialVersionUID = 1L;

	private JamochaRuleExecutionSetMap ruleSets;
	
	public JamochaRuleRuntime(JamochaRuleExecutionSetMap ruleSets) {
		this.ruleSets = ruleSets;
	}
	
	@Override
	public RuleSession createRuleSession(String uri, Map properties, int sessionType) throws RuleSessionTypeUnsupportedException, RuleSessionCreateException, RuleExecutionSetNotFoundException,RemoteException {
		switch (sessionType) {
		case RuleRuntime.STATELESS_SESSION_TYPE:
			return new JamochaStatelessRuleSession(ruleSets.getRuleExecutionSet(uri), uri);
		case RuleRuntime.STATEFUL_SESSION_TYPE:
			throw new RuleSessionTypeUnsupportedException("Stateful sessions not implemented yet");
		default:
			throw new RuleSessionTypeUnsupportedException("unknown session type.");
		}
	}

	@Override
	public List getRegistrations() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
