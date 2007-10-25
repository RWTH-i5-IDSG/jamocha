package org.jamocha.jsr94;

import java.rmi.RemoteException;
import java.util.List;

import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleRuntime;
import javax.rules.StatelessRuleSession;

public class JamochaStatelessRuleSession implements StatelessRuleSession {

	@Override
	public List executeRules(List objects) throws InvalidRuleSessionException,	RemoteException {
		return executeRules(objects, null);
	}

	@Override
	public List executeRules(List objects, ObjectFilter filter)	throws InvalidRuleSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleExecutionSetMetadata getRuleExecutionSetMetadata()
			throws InvalidRuleSessionException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() throws RemoteException, InvalidRuleSessionException {
		return RuleRuntime.STATELESS_SESSION_TYPE;
	}

	@Override
	public void release() throws RemoteException, InvalidRuleSessionException {
		// TODO Auto-generated method stub

	}

}
