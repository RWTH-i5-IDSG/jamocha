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

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */

public class JamochaRule implements javax.rules.admin.Rule {

	public static final String JAMOCHA_RULE_OBJECT = "jamocha_rule_object";

	private static final long serialVersionUID = 1L;

	private final org.jamocha.rules.Rule rule;

	public JamochaRule(org.jamocha.rules.Rule rule) {
		this.rule = rule;
	}

	public String getDescription() {
		return rule.getDescription();
	}

	public String getName() {
		return rule.getName();
	}

	public Object getProperty(Object arg0) {
		if (arg0.equals(JAMOCHA_RULE_OBJECT))
			return rule;

		// TODO do something useful with this method
		return null;
	}

	public void setProperty(Object arg0, Object arg1) {
		// TODO do something useful with this method
	}

}
