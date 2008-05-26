/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import javax.rules.ConfigurationException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.RuleAdministrator;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */

// TODO thread-safety
public class JamochaRuleServiceProvider extends RuleServiceProvider {

	private static String RULE_SERVICE_PROVIDER = "org.jamocha.jsr94";

	private JamochaRuleExecutionSetMap ruleSets;

	private RuleRuntime ruleRuntime;

	private RuleAdministrator ruleAdministrator;

	// register our implementation
	static {
		try {
			RuleServiceProviderManager.registerRuleServiceProvider(
					RULE_SERVICE_PROVIDER, JamochaRuleServiceProvider.class);
		} catch (ConfigurationException e) {
			System.err
					.println("Error while registering us as rule service provider");
		}
	}

	private JamochaRuleExecutionSetMap getRuleSets() {
		if (ruleSets == null)
			ruleSets = new JamochaRuleExecutionSetMap();
		return ruleSets;
	}

	@Override
	public RuleAdministrator getRuleAdministrator()
			throws ConfigurationException {
		if (ruleAdministrator == null)
			ruleAdministrator = new JamochaRuleAdministrator(getRuleSets());
		return ruleAdministrator;
	}

	@Override
	public RuleRuntime getRuleRuntime() throws ConfigurationException {
		if (ruleRuntime == null)
			ruleRuntime = new JamochaRuleRuntime(getRuleSets());
		return ruleRuntime;
	}

}
