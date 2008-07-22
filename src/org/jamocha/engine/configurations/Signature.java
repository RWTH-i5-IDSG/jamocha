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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

/**
 * @author Peter Lin
 * 
 * Describe difference between the Function parameters
 */
public class Signature extends AbstractSignature implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String signatureName = null;

	private Parameter[] params = null;

	public Signature() {
		super();
	}

	@Override
	public Object clone() {
		Signature result = new Signature();
		result.setSignatureName(signatureName);
		result.setParameters(params.clone());
		return result;
	}

	public Signature(String signatureName) {
		super();
		this.signatureName = signatureName;
	}

	public void setSignatureName(String name) {
		signatureName = name;
	}

	public String getSignatureName() {
		return signatureName;
	}

	// TODO remove public void configure(Engine engine, Rule rule) {
	// for (int idx = 0; idx < this.params.length; idx++) {
	// if (this.params[idx] instanceof BoundParam) {
	// // we need to set the row value if the binding is a slot or fact
	// BoundParam bp = (BoundParam) this.params[idx];
	// Binding b1 = rule.getBinding(bp.getVariableName());
	// if (b1 != null) {
	// bp.setRow(b1.getLeftRow());
	// if (b1.getLeftIndex() == -1) {
	// bp.setObjectBinding(true);
	// }
	// }
	// }
	// }
	// }

	public Parameter[] getParameters() {
		return params;
	}

	public void setParameters(Parameter[] params) {
		this.params = params;
	}
	
	public void setParameters(List<Parameter> p) {
		Parameter[] params = new Parameter[p.size()];
		for (int i=0; i<p.size(); i++) {
			params[i] = p.get(i);
		}
		setParameters(params);
	}

	public List<BoundParam> getBoundParameters() {
		ArrayList<BoundParam> result = new ArrayList<BoundParam>();
		for (Parameter param : params)
			if (param instanceof BoundParam)
				result.add((BoundParam) param);
			else if (param instanceof Signature)
				result.addAll(((Signature) param).getBoundParameters());
		return result;
	}

	public Function lookUpFunction(Engine engine)
			throws FunctionNotFoundException {
		return engine.getFunctionMemory().findFunction(signatureName);
	}

	public JamochaType getValueType() {
		return JamochaType.UNDEFINED;
	}

	/**
	 * TODO we may want to check the value type and throw and exception for now
	 * just getting it to work.
	 */
	public JamochaValue getValue(Engine engine) throws EvaluationException {
		if (params != null) {
			Function func;
			try {
				func = lookUpFunction(engine);
			} catch (FunctionNotFoundException e) {
				throw new EvaluationException(e);
			}
			return func.executeFunction(engine, params);
		}
		return JamochaValue.FALSE;
	}

	public void reset() {
		params = null;
	}

	public String getExpressionString() {
		return format(ParserFactory.getFormatter());
	}

	@Override
	public String toString() {
		return format(ParserFactory.getFormatter());
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
}
