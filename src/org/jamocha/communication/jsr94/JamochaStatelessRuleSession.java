/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.communication.jsr94;

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

import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.communication.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.JavaFact;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.parser.EvaluationException;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
@SuppressWarnings("unchecked")
public class JamochaStatelessRuleSession extends JamochaAbstractRuleSession implements StatelessRuleSession {

	private final JamochaTransactionManager session;

	private final RuleExecutionSet res;

	private final String uri;

	protected Map properties;

	public JamochaStatelessRuleSession(JamochaRuleExecutionSet res, String uri,
			Map properties) {
		super();
		session = new JamochaTransactionManager();
		this.res = res;
		this.uri = uri;
		this.properties = properties != null ? properties : new HashMap();
		try {
			addRules(res);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.commit();
	}
	public List executeRules(List objects) throws InvalidRuleSessionException,
			RemoteException {
		ObjectFilter filter = null;
		if (res.getDefaultObjectFilter() != null) {
			Class<? extends ObjectFilter> defaultFilterClass;
			try {
				defaultFilterClass = (Class<? extends ObjectFilter>) Class
						.forName(res.getDefaultObjectFilter());
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

	public List executeRules(List objects, ObjectFilter filter)
			throws InvalidRuleSessionException, RemoteException {
		boolean onlyJavaObjects = true;
		{
			Object onlyJO = properties.get("only-java-objects");
			if (onlyJO != null && onlyJO instanceof Boolean
					&& !((Boolean) onlyJO))
				onlyJavaObjects = false;
		}
		// put facts into the engine
		for (Object o : objects)
			// TODO do something with the filter
			try {
				if (o instanceof Fact)
					session.getEngine().assertFact((Fact) o);
				else {
					session.getEngine().assertFact(new JavaFact(o,getEngine()));
				}
			} catch (AssertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		// fire rules
		try {
			session.getEngine().fire();
		} catch (ExecuteException e) {
			throw new InvalidRuleSessionException("error while firing rules", e);
		}

		// collect facts and return them as a list
		List<Object> results = new LinkedList<Object>();

		for (WorkingMemoryElement f : session.getEngine().getModules().getAllFacts()) {
			if (f instanceof Deffact) {
				if (!onlyJavaObjects) results.add(f);
			} else if (f instanceof JavaFact) {
				results.add(((JavaFact)f).getObject());
			}
		}

		// rollback session since we make stateless rule evaluation here
		session.rollback();
		return results;
	}

	public RuleExecutionSetMetadata getRuleExecutionSetMetadata()
			throws InvalidRuleSessionException, RemoteException {
		return new JamochaRuleExecutionSetMetadata(res, uri);
	}

	public int getType() throws RemoteException, InvalidRuleSessionException {
		return RuleRuntime.STATELESS_SESSION_TYPE;
	}

	public void release() throws RemoteException, InvalidRuleSessionException {
		session.release();
	}

	@Override
	protected Engine getEngine() {
		return session.getEngine();
	}

}
