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
import java.util.List;
import java.util.Map;

import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleRuntime implements RuleRuntime {

	private static final long serialVersionUID = 1L;

	private final JamochaRuleExecutionSetMap ruleSets;

	public JamochaRuleRuntime(JamochaRuleExecutionSetMap ruleSets) {
		this.ruleSets = ruleSets;
	}

	@SuppressWarnings("unchecked")
	public RuleSession createRuleSession(String uri, Map properties,
			int sessionType) throws RuleSessionTypeUnsupportedException,
			RuleSessionCreateException, RuleExecutionSetNotFoundException,
			RemoteException {
		switch (sessionType) {
		case RuleRuntime.STATELESS_SESSION_TYPE:
			return new JamochaStatelessRuleSession(ruleSets
					.getRuleExecutionSet(uri), uri, properties);
		case RuleRuntime.STATEFUL_SESSION_TYPE:
			return new JamochaStatefulRuleSession(ruleSets.getRuleExecutionSet(uri), uri, properties);
		default:
			throw new RuleSessionTypeUnsupportedException(
					"unknown session type.");
		}
	}

	@SuppressWarnings("unchecked")
	public List getRegistrations() throws RemoteException {
		return ruleSets.getRegistrators();
	}

}
