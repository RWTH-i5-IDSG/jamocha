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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rules.Handle;
import javax.rules.InvalidHandleException;
import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleRuntime;
import javax.rules.StatefulRuleSession;
import javax.rules.admin.RuleExecutionSet;

import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.communication.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.Rule;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
@SuppressWarnings("unchecked")
public class JamochaStatefulRuleSession extends JamochaAbstractRuleSession implements StatefulRuleSession{

	private final RuleExecutionSet res;

	private final String uri;

	private Engine engine;

	protected Map properties;

	public JamochaStatefulRuleSession(JamochaRuleExecutionSet res, String uri,
			Map properties) {
		super();
		engine = new Engine();
		this.res = res;
		this.uri = uri;
		this.properties = properties != null ? properties : new HashMap();
		try {
			addRules(res);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public RuleExecutionSetMetadata getRuleExecutionSetMetadata()
			throws InvalidRuleSessionException, RemoteException {
		return new JamochaRuleExecutionSetMetadata(res, uri);
	}

	public int getType() throws RemoteException, InvalidRuleSessionException {
		return RuleRuntime.STATEFUL_SESSION_TYPE;
	}

	public void release() throws RemoteException, InvalidRuleSessionException {
		engine.dispose();
	}

	public Handle addObject(Object o) throws RemoteException, InvalidRuleSessionException {
		try{
			if (o instanceof Fact) {
				Fact f = (Fact) o;
				engine.assertFact(f);
				return new JamochaFactHandle(f.getFactId());
			} else {
				Fact f = getJavaClassAdaptor(o.getClass()).getFactFromObject(o, engine);
				engine.assertFact(f);
				return new JamochaFactHandle(f.getFactId());
			}
		} catch (AssertException e) {
			throw new InvalidRuleSessionException("error while adding object", e);
		} catch (Template2JavaClassAdaptorException e) {
			throw new InvalidRuleSessionException("error while adding object", e);
		}
	}

	public List addObjects(List arg0) throws RemoteException, InvalidRuleSessionException {
		List result = new ArrayList();
		for (Object o : arg0) {result.add(addObject(o));}
		return result;
	}

	public boolean containsObject(Handle arg0) throws RemoteException, InvalidRuleSessionException, InvalidHandleException {
		// TODO Auto-generated method stub
		return false;
	}

	public void executeRules() throws RemoteException,	InvalidRuleSessionException {
		// TODO Auto-generated method stub
		
	}

	public List getHandles() throws RemoteException, InvalidRuleSessionException {
		List result = new ArrayList();
		for (Fact f : engine.getModules().getAllFacts()) {
			result.add(new JamochaFactHandle(f.getFactId()));
		}
		return result;
	}

	public Object getObject(Handle arg0) throws RemoteException,
			InvalidHandleException, InvalidRuleSessionException {
		Fact f = engine.getFactById( ((JamochaFactHandle)arg0).getId() );
		return fact2Object(f);
	}

	public List getObjects() throws RemoteException,
			InvalidRuleSessionException {
		List result = new ArrayList();
		for (Object h : getHandles() ){
			JamochaFactHandle fh = (JamochaFactHandle) h;
			try {
				result.add( getObject(fh) );
			} catch (InvalidHandleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public List getObjects(ObjectFilter arg0) throws RemoteException,InvalidRuleSessionException {
		List result = new ArrayList();
		List all = getObjects();
		for ( Object o : all) {
			result.add( arg0.filter(o) );
		}
		return result;
	}

	public void removeObject(Handle arg0) throws RemoteException, InvalidHandleException, InvalidRuleSessionException {
		try {
			engine.retractById(((JamochaFactHandle)arg0).getId());
		} catch (RetractException e) {
			throw new InvalidHandleException("error while retracting",e);
		}
	}

	public void reset() throws RemoteException, InvalidRuleSessionException {
		engine.resetFacts();
		for(Rule r: engine.getModules().getAllRules()) {
			engine.removeRule(r);
		}
	}

	public void updateObject(Handle arg0, Object arg1) throws RemoteException,
			InvalidRuleSessionException, InvalidHandleException {
		removeObject(arg0);
		addObject(arg1);
	}

	@Override
	protected Engine getEngine() {
		return engine;
	}

}
