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

public class ConnectedActionSLConfiguration implements SLConfiguration {

	private String connector;

	private SLConfiguration firstAction;

	private SLConfiguration secondAction;

	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public SLConfiguration getFirstAction() {
		return firstAction;
	}

	public void setFirstAction(SLConfiguration firstAction) {
		this.firstAction = firstAction;
	}

	public SLConfiguration getSecondAction() {
		return secondAction;
	}

	public void setSecondAction(SLConfiguration secondAction) {
		this.secondAction = secondAction;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		res.append("(");
		if (connector.equals(";"))
			res.append("&");
		else
			res.append(connector);
		res.append(connector);

		res.append(" ").append(firstAction.compile(compileType)).append(" ")
				.append(secondAction.compile(compileType)).append(")");
		return res.toString();
	}

}
