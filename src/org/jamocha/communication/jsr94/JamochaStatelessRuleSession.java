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
import org.jamocha.parser.Expression;
import org.jamocha.parser.RuleException;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
@SuppressWarnings("unchecked")
public class JamochaStatelessRuleSession implements StatelessRuleSession {

	private JamochaStatefulRuleSession statefulSession;


	public JamochaStatelessRuleSession(JamochaRuleExecutionSet ruleExecutionSet, String uri, Map properties) {
		statefulSession = new JamochaStatefulRuleSession(ruleExecutionSet, uri, properties);
	}

	public List executeRules(List arg0) throws InvalidRuleSessionException,	RemoteException {
		statefulSession.addObjects(arg0);
		statefulSession.executeRules();
		List result = statefulSession.getObjects();
		statefulSession.reset();
		return result;
	}

	public List executeRules(List arg0, ObjectFilter arg1) throws InvalidRuleSessionException, RemoteException {
		for (Object o: arg0)
		{
			Object r = arg1.filter(o);
			if (r == null) continue;
			statefulSession.addObject(r);
		}
		statefulSession.executeRules();
		List result = statefulSession.getObjects();
		statefulSession.reset();
		return result;
	}

	public RuleExecutionSetMetadata getRuleExecutionSetMetadata()throws InvalidRuleSessionException, RemoteException {
		return statefulSession.getRuleExecutionSetMetadata();
	}

	public int getType() throws RemoteException, InvalidRuleSessionException {
		return RuleRuntime.STATELESS_SESSION_TYPE;
	}

	public void release() throws RemoteException, InvalidRuleSessionException {
		statefulSession.release();
		statefulSession = null;
	}


}
