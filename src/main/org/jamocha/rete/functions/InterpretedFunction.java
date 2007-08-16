/*
 * Copyright 2002-2007 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BlockingScope;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Peter Lin
 * 
 * An <code>InterpretedFunction</code> is the Class that represents a function
 * defined through Deffunction. All actions of the deffunction are evaluated
 * with the given parameters for the deffunction.
 */
public class InterpretedFunction extends AbstractFunction {

	public class InterpretedFunctionDescription implements FunctionDescription {

		public String getDescription() {
			return descriptionText;
		}

		public int getParameterCount() {
			if (inputParams != null)
				return inputParams.length;
			else
				return 0;
		}

		public String getParameterDescription(int parameter) {
			return "- not available -";
		}

		public String getParameterName(int parameter) {
			if (inputParams != null && parameter < inputParams.length
					&& parameter >= 0) {
				BoundParam bp = (BoundParam) inputParams[parameter];
				return bp.getVariableName();
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			if (inputParams != null && parameter < inputParams.length
					&& parameter >= 0)
				return JamochaType.ANY;
			else
				return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	private FunctionDescription description = new InterpretedFunctionDescription();

	private String name = null;

	private String descriptionText = null;

	protected Expression[] inputParams = null;

	private ExpressionSequence actions = null;

	public InterpretedFunction(String name, String descriptionText,
			Expression[] params, ExpressionSequence actions) {
		this.name = name;
		this.descriptionText = descriptionText;
		this.inputParams = params;
		this.actions = actions;
	}

	public FunctionDescription getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		// the first thing we do is set the values
		JamochaValue result = JamochaValue.NIL;
		if (params.length == inputParams.length) {
			JamochaValue[] evaluatedParams = new JamochaValue[inputParams.length];
			for (int idx = 0; idx < inputParams.length; idx++) {
				evaluatedParams[idx] = params[idx].getValue(engine);
			}
			engine.pushScope(new BlockingScope());
			for (int idx = 0; idx < inputParams.length; ++idx) {
				BoundParam bp = (BoundParam) inputParams[idx];
				engine.setBinding(bp.getVariableName(), evaluatedParams[idx]);
			}
			try {
				result = actions.getValue(engine);
			} catch (Exception e) {
				throw new EvaluationException("Unknown Error in function "
						+ name, e);
			} finally {
				engine.popScope();
			}
		} else {
			throw new IllegalParameterException(inputParams.length);
		}
		return result;
	}
}
