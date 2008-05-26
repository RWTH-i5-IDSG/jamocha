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

import javax.rules.RuleExecutionSetMetadata;
import javax.rules.admin.RuleExecutionSet;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleExecutionSetMetadata implements
		RuleExecutionSetMetadata {

	private static final long serialVersionUID = 1L;

	private final RuleExecutionSet res;

	private final String uri;

	public JamochaRuleExecutionSetMetadata(RuleExecutionSet res, String uri) {
		this.res = res;
		this.uri = uri;
	}

	public String getDescription() {
		return res.getDescription();
	}

	public String getName() {
		return res.getName();
	}

	public String getUri() {
		return uri;
	}

}
