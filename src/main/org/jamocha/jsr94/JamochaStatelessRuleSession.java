package org.jamocha.jsr94;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleRuntime;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleExecutionSet;

import org.jamocha.jsr94.internal.Template2JavaClassAdaptor;
import org.jamocha.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rete.wme.Template;
import org.jamocha.rete.wme.tags.Tag;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
@SuppressWarnings("unchecked")
public class JamochaStatelessRuleSession implements StatelessRuleSession {

	private JamochaTransactionBasedSession session;
	
	private RuleExecutionSet res;
	
	private String uri;
	
	private Map<Class,Template2JavaClassAdaptor> javaClassAdaptor;
	
	protected Template2JavaClassAdaptor getJavaClassAdaptor(Class c) {
		Template2JavaClassAdaptor res = javaClassAdaptor.get(c);
		if (res == null) {
			String templName = c.getCanonicalName();
			Template t = session.getEngine().findTemplate(templName);
			if (t == null) return null;
			Iterator<Tag> tags= t.getTags(TemplateFromJavaClassTag.class);
			if (tags.hasNext()){
				TemplateFromJavaClassTag tfjct = (TemplateFromJavaClassTag) tags.next();
				Template2JavaClassAdaptor adaptor = tfjct.getAdaptor();
				javaClassAdaptor.put(c, adaptor);
				return adaptor;
			}
			return null;
		}
		return res;
	}
	
	public JamochaStatelessRuleSession(JamochaRuleExecutionSet res, String uri) {
		session = new JamochaTransactionBasedSession();
		this.res = res;
		this.uri = uri;
		this.javaClassAdaptor = new HashMap<Class, Template2JavaClassAdaptor>();
		try {
			addRules(res);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.commit();
	}
	
	private void addRules(JamochaRuleExecutionSet res2) throws EvaluationException, RuleException {
		Expression[] exprs = res2.getExpressions();
		for (Expression e : exprs) {
			e.getValue(session.getEngine());
		}
	}



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

	public List executeRules(List objects, ObjectFilter filter)	throws InvalidRuleSessionException, RemoteException {

		
		// put facts into the engine
		for (Object o : objects) {
			// TODO do something with the filter
			try {
				if (o instanceof Fact) {
					session.getEngine().assertFact((Fact)o);
				} else {
					Fact f = getJavaClassAdaptor(o.getClass()).getFactFromObject(o,session.getEngine());
					session.getEngine().assertFact(f);
				}
			} catch (AssertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Template2JavaClassAdaptorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
		// fire rules
		try {
			session.getEngine().fire();
		} catch (ExecuteException e) {
			throw new InvalidRuleSessionException("error while firing rules",e);
		}
		
		// collect facts and return them as a list
		List<Object> results = new LinkedList<Object>();
		
		for(WorkingMemoryElement f : session.getEngine().getNet().getRoot().memory()  ) {
			Iterator<Tag> itr = f.getFirstFact().getTemplate().getTags(TemplateFromJavaClassTag.class);
			if (itr.hasNext()) {
				TemplateFromJavaClassTag ttag = (TemplateFromJavaClassTag) itr.next();
				Class cl = ttag.getJavaClass();
				Object o = null;
				try {
					o = cl.newInstance();
				} catch (Exception e) {
					//TODO exception handling
				}
				try {
					ttag.getAdaptor().storeToObject(f.getFirstFact(), o, session.getEngine());
					results.add(o);
				} catch (Template2JavaClassAdaptorException e) {
					results.add(f);
				}
			} else {
				results.add(f);
			}
		}
		
		// rollback session since we make stateless rule evaluation here
		session.rollback();
		return results;
	}


	public RuleExecutionSetMetadata getRuleExecutionSetMetadata() throws InvalidRuleSessionException, RemoteException {
		return new JamochaRuleExecutionSetMetadata(res, uri);
	}

	public int getType() throws RemoteException, InvalidRuleSessionException {
		return RuleRuntime.STATELESS_SESSION_TYPE;
	}

	public void release() throws RemoteException, InvalidRuleSessionException {
		session.release();
	}

}
