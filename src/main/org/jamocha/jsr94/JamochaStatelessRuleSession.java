package org.jamocha.jsr94;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleRuntime;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleExecutionSet;

import org.jamocha.parser.RuleException;
import org.jamocha.rete.Fact;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn
 */

@SuppressWarnings("unchecked")
public class JamochaStatelessRuleSession implements StatelessRuleSession {

	JamochaTransactionBasedSession session;
	
	private RuleExecutionSet res;
	
	private String uri;
	
	public JamochaStatelessRuleSession(RuleExecutionSet res, String uri) {
		session = new JamochaTransactionBasedSession();
		this.res = res;
		this.uri = uri;
		try {
			addRules(res);
		} catch (AssertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.commit();
	}
	
	private void addRules(RuleExecutionSet res2) throws AssertException, RuleException {
		for(Object orule : res2.getRules()) {
			JamochaRule rule = (JamochaRule) orule;
			session.getEngine().addRule((Rule)rule.getProperty(JamochaRule.JAMOCHA_RULE_OBJECT));
		}
	}



	@Override
	public List executeRules(List objects) throws InvalidRuleSessionException,	RemoteException {
		ObjectFilter filter = null;
		if (res.getDefaultObjectFilter() != null) {
			Class<? extends ObjectFilter> defaultFilterClass;
			try {
				defaultFilterClass = (Class<? extends ObjectFilter>) Class.forName(res.getDefaultObjectFilter());
				filter = defaultFilterClass.newInstance();
			} catch (ClassNotFoundException e) {
				// TODO do something more nice with this exception
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO do something more nice with this exception
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO do something more nice with this exception
				e.printStackTrace();
			}
		}
		return executeRules(objects, filter);
	}

	@Override
	public List executeRules(List objects, ObjectFilter filter)	throws InvalidRuleSessionException, RemoteException {
		for (Object o : objects) {
			// TODO do something with the filter
			try {
				session.getEngine().assertFact(o);
			} catch (AssertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Fact> results = new LinkedList<Fact>();
		// TODO it would be nice to return the results. otherwise, its senseless ;)
		
		session.rollback();
		return results;
	}

	@Override
	public RuleExecutionSetMetadata getRuleExecutionSetMetadata() throws InvalidRuleSessionException, RemoteException {
		return new JamochaRuleExecutionSetMetadata(res, uri);
	}

	@Override
	public int getType() throws RemoteException, InvalidRuleSessionException {
		return RuleRuntime.STATELESS_SESSION_TYPE;
	}

	@Override
	public void release() throws RemoteException, InvalidRuleSessionException {
		session.release();
	}

}
