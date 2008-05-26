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

package org.jamocha.languages.sl.sl2clips_adapter.configurations;

public class ActionSLConfiguration implements SLConfiguration {

	private SLConfiguration agent;

	private SLConfiguration action;

	public SLConfiguration getAction() {
		return action;
	}

	public void setAction(SLConfiguration action) {
		this.action = action;
	}

	public SLConfiguration getAgent() {
		return agent;
	}

	public void setAgent(SLConfiguration agent) {
		this.agent = agent;
	}

	public String compile(SLCompileType compileType) {
		return action.compile(compileType);
	}

}
