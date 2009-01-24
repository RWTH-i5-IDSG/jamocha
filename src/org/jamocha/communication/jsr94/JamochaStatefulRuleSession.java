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

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.JavaFact;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.Rule;

import com.sun.org.apache.bcel.internal.Constants;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
@SuppressWarnings("unchecked")
public class JamochaStatefulRuleSession implements StatefulRuleSession{

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
			e.printStackTrace();
		}
	}

	protected void addRules(JamochaRuleExecutionSet res2) throws EvaluationException, RuleException {
		Expression[] exprs = res2.getExpressions();
		for (Expression e : exprs)
			e.getValue(getEngine());
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
		return addObject(o,-1);
	}
	
	protected Handle addObject(Object o, long id) throws RemoteException, InvalidRuleSessionException {
		try{
			if (o instanceof Fact) {
				Fact f = (Fact) o;
				if (id > 0) f.setFactId(id);
				engine.assertFact(f);
				return new JamochaFactHandle(f.getFactId());
			} else {
				Fact f = new JavaFact(o,getEngine());
				if (id > 0) f.setFactId(id);
				engine.assertFact(f);
				return new JamochaFactHandle(f.getFactId());
			}
		} catch (AssertException e) {
			throw new InvalidRuleSessionException("error while adding object", e);
		}
	}

	public List addObjects(List arg0) throws RemoteException, InvalidRuleSessionException {
		List result = new ArrayList();
		for (Object o : arg0) {result.add(addObject(o));}
		return result;
	}

	public boolean containsObject(Handle arg0) throws RemoteException, InvalidRuleSessionException, InvalidHandleException {
		return (getEngine().getFactById( ((JamochaFactHandle)arg0).getId() ) != null  );
	}

	public void executeRules() throws RemoteException,	InvalidRuleSessionException {
		try {
			getEngine().fire();
		} catch (ExecuteException e) {
			throw new InvalidRuleSessionException("error while firing engine",e);
		}
	}

	public List getHandles() throws RemoteException, InvalidRuleSessionException {
		List result = new ArrayList();
		for (Fact f : engine.getModules().getAllFacts()) {
			if (!(f instanceof Deffact))
				result.add(new JamochaFactHandle(f.getFactId()));
		}
		return result;
	}

	public Object getObject(Handle arg0) throws RemoteException,
			InvalidHandleException, InvalidRuleSessionException {
		Fact f = engine.getFactById( ((JamochaFactHandle)arg0).getId() );
		if (f instanceof Deffact) {
			return f;
		} else if (f instanceof JavaFact) {
			JavaFact jaf = (JavaFact) f;
			return jaf.getObject();
		}
		throw new InvalidHandleException("no known type of fact");
	}

	public List getObjects() throws RemoteException,
			InvalidRuleSessionException {
		List result = new ArrayList();
		for (Object h : getHandles() ){
			JamochaFactHandle fh = (JamochaFactHandle) h;
			try {
				if (!(getObject(fh) instanceof Deffact))
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
			Object r = arg0.filter(o);
			if (r!=null) result.add(r);
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
		for (Fact f : getEngine().getModules().getAllFacts()) {
			if (f.getTemplate().getName().equals(org.jamocha.Constants.INITIAL_FACT)) continue;
			try {
				getEngine().retractFact(f);
			} catch (RetractException e) {
				Logging.logger(this.getClass()).warn("Error while resetting session. Cannot retract fact "+f);
			}
		}
	}

	public void updateObject(Handle arg0, Object arg1) throws RemoteException,
			InvalidRuleSessionException, InvalidHandleException {
		long id = ((JamochaFactHandle)arg0).getId();
		removeObject(arg0);
		addObject(arg1,id);
	}

	protected Engine getEngine() {
		return engine;
	}

}
