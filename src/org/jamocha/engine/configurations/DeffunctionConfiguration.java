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

package org.jamocha.engine.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.ExpressionSequence;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;

/**
 * @author charlie
 * 
 */
public class DeffunctionConfiguration extends AbstractConfiguration {

	private String functionName = null;

	private String functionDescription = null;

	private Parameter[] params = null;

	private ExpressionSequence actions = null;

	private String functionGroup = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.Parameter#isObjectBinding()
	 */
	public boolean isFactBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.parser.Expression#getExpressionString()
	 */
	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.parser.Expression#getValue(org.jamocha.rete.Rete)
	 */
	public JamochaValue getValue(Engine engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionSequence getActions() {
		return actions;
	}

	public void setActions(ExpressionSequence actions) {
		this.actions = actions;
	}

	public String getFunctionDescription() {
		return functionDescription;
	}

	public void setFunctionDescription(String functionDescription) {
		this.functionDescription = functionDescription;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Parameter[] getParams() {
		return params;
	}

	public void setParams(Parameter[] params) {
		this.params = params;
	}

	public boolean definesFunctionGroup() {
		return functionGroup != null;
	}

	public String getFunctionGroup() {
		return functionGroup;
	}

	public void setFunctionGroup(String functionGroup) {
		this.functionGroup = functionGroup;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
