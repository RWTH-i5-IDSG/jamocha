/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
package org.jamocha.rete.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Starts the execution of rules in the currently focused module.
 * If the optional argument is positive, execution will cease after the 
 * specified number of rule firings or when the agenda contains no more 
 * rule activations. If there are no arguments or the argument is a 
 * negative integer, execution will cease when the agenda contains
 * no more rule activations. 
 * If the focus stack is empty the MAIN module is automatically in the
 * current focus. Returns the number of rules fired.
 */
public class Fire extends AbstractFunction {

	public static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Starts the execution of rules. If the optional argument is positive, execution will cease " +
					"after the specified number of rule firings or when the agenda contains no more rule " +
					"activations. If there are no arguments or the argument is a negative integer, " +
					"execution will cease when the agenda contains no more rule activations. " +
					"If the focus stack is empty the MAIN module is automatically in the current " +
					"focus. Returns the number of rules fired.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Number of rules to fire at most.";
		}

		public String getParameterName(int parameter) {
			return "ruleCount";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.LONGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LONGS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			return "(fire)\n" +
					"(fire 7)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "fire";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		long count = 0;
		if (params != null && params.length == 1) {
			long fc = params[0].getValue(engine).implicitCast(JamochaType.LONG)
					.getLongValue();
			try {
				count = engine.fire((int) fc);
			} catch (ExecuteException e) {
				throw new EvaluationException(e);
			}
		} else {
			try {
				count = engine.fire();
			} catch (ExecuteException e) {
				throw new EvaluationException("Error during fire. ", e);
			}
		}
		return JamochaValue.newLong(count);
	}
}