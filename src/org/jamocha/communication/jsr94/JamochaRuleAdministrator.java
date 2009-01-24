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

	private final JamochaRuleExecutionSetMap ruleExecutionSets;

	private JamochaLocalRuleExecutionSetProvider localRuleExecSetprovider;
	
	private JamochaRuleExecutionSetProvider ruleExecSetprovider;

	public JamochaRuleAdministrator(JamochaRuleExecutionSetMap ruleExecutionSets) {
		super();
		this.ruleExecutionSets = ruleExecutionSets;
	}

	@SuppressWarnings("unchecked")
	public void deregisterRuleExecutionSet(String uri, Map properties)
			throws RuleExecutionSetDeregistrationException, RemoteException {
		ruleExecutionSets.removeRuleExecutionSet(uri);
	}

	@SuppressWarnings("unchecked")
	public LocalRuleExecutionSetProvider getLocalRuleExecutionSetProvider(Map properties) throws RemoteException {
		if (localRuleExecSetprovider == null)
			localRuleExecSetprovider = new JamochaLocalRuleExecutionSetProvider();
		return localRuleExecSetprovider;
	}

	@SuppressWarnings("unchecked")
	public RuleExecutionSetProvider getRuleExecutionSetProvider(Map properties)	throws RemoteException {
		if (ruleExecSetprovider == null)
			ruleExecSetprovider = new JamochaRuleExecutionSetProvider();
		return ruleExecSetprovider;
	}

	@SuppressWarnings("unchecked")
	public void registerRuleExecutionSet(String uri, RuleExecutionSet execSet,
			Map properties) throws RuleExecutionSetRegisterException,
			RemoteException {
		ruleExecutionSets.putRuleExecutionSet(uri,
				(JamochaRuleExecutionSet) execSet);
	}

}
