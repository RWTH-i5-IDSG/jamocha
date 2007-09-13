/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.adapter.sl.configurations;

import java.util.LinkedList;
import java.util.List;

public class WffSLConfiguration implements SLConfiguration {

	boolean braces = true;

	private List<SLConfiguration> expressions = new LinkedList<SLConfiguration>();

	public boolean isBraces() {
		return braces;
	}

	public void setBraces(boolean braces) {
		this.braces = braces;
	}

	public void addExpression(SLConfiguration expression) {
		expressions.add(expression);
	}

	public List<SLConfiguration> getExpressions() {
		return expressions;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		// If we just have boolean constants we have to transform them to
		// regular CLIPS expressions. Here we use simple testconditions.
		if (!braces && expressions.size() == 1
				&& compileType == SLCompileType.RULE_LHS) {
			String possBoolean = expressions.get(0).compile(compileType);
			if (possBoolean.equalsIgnoreCase("true"))
				return "(test (eq true true))";
			else if (possBoolean.equalsIgnoreCase("false"))
				return "(test (eq true false))";
		}
		if (braces)
			res.append("(");
		for (int i = 0; i < expressions.size(); ++i) {
			if (i > 0)
				res.append(" ");
			res.append(expressions.get(i).compile(compileType));
		}
		if (braces)
			res.append(")");
		return res.toString();
	}

}
