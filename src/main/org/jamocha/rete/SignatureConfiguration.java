/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * Describe difference between the Function parameters
 */
public class SignatureConfiguration extends AbstractSignature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String funcName = null;

	private Parameter[] params = null;

	public SignatureConfiguration() {
		super();
	}

	public SignatureConfiguration(String functionName) {
		super();
		this.funcName = functionName;
	}

	public void setFunctionName(String name) {
		this.funcName = name;
	}

	public String getFunctionName() {
		return this.funcName;
	}

	public void configure(Rete engine, Rule util) {
		for (int idx = 0; idx < this.params.length; idx++) {
			if (this.params[idx] instanceof BoundParam) {
				// we need to set the row value if the binding is a slot or fact
				BoundParam bp = (BoundParam) this.params[idx];
				Binding b1 = util.getBinding(bp.getVariableName());
				if (b1 != null) {
					bp.setRow(b1.getLeftRow());
					if (b1.getLeftIndex() == -1) {
						bp.setObjectBinding(true);
					}
				}
			}
		}
	}

	public Parameter[] getParameters() {
		return params;
	}

	public void setParameters(Parameter[] params) {
		this.params = params;
	}

	public Function lookUpFunction(Rete engine) {
		return engine.findFunction(this.funcName);
	}

	public JamochaType getValueType() {
		return JamochaType.UNDEFINED;
	}

	/**
	 * TODO we may want to check the value type and throw and exception for now
	 * just getting it to work.
	 */
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		if (this.params != null) {
			Function func = lookUpFunction(engine);
			if (func != null) {
				return func.executeFunction(engine, this.params);
			}
		}
		return null;
	}

	public void reset() {
		this.params = null;
	}

	public String getExpressionString() {
		return ParserFactory.getFormatter().formatExpression(this);
	}
}
