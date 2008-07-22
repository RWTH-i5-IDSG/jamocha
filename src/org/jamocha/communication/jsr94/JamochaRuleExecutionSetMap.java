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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleExecutionSetMap {

	private final Map<String, JamochaRuleExecutionSet> map;

	public JamochaRuleExecutionSetMap() {
		map = new HashMap<String, JamochaRuleExecutionSet>();
	}

	public JamochaRuleExecutionSet getRuleExecutionSet(String uri) {
		return map.get(uri);
	}

	public void putRuleExecutionSet(String uri, JamochaRuleExecutionSet res) {
		map.put(uri, res);
	}

	public void removeRuleExecutionSet(String uri) {
		map.remove(uri);
	}

}
