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

package org.jamocha.languages.sl.sl2clips_adapter.configurations;

import java.util.LinkedList;
import java.util.List;

public class FunctionCallSLConfiguration implements SLConfiguration {

	private SLConfiguration functionName;
	
	private List<SLConfiguration> parameters = new LinkedList<SLConfiguration>();
	
	public SLConfiguration getFunctionName() {
		return functionName;
	}

	public void setFunctionName(SLConfiguration functionName) {
		this.functionName = functionName;
	}

	public List<SLConfiguration> getParameters() {
		return parameters;
	}

	public void addParameter(SLConfiguration parameter) {
		parameters.add(parameter);
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		res.append("(").append(functionName.compile(compileType));
		for (SLConfiguration parameter : parameters) {
			res.append(" ");
			res.append(parameter.compile(SLCompileType.ASSERT));
		}
		res.append(")");
		return res.toString();
	}

}
