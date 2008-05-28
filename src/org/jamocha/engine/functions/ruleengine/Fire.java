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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Starts the execution of rules in the currently focused module. If the
 * optional argument is positive, execution will cease after the specified
 * number of rule firings or when the agenda contains no more rule activations.
 * If there are no arguments or the argument is a negative integer, execution
 * will cease when the agenda contains no more rule activations. If the focus
 * stack is empty the MAIN module is automatically in the current focus. Returns
 * the number of rules fired.
 */
public class Fire extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Starts the execution of rules. If the optional argument is positive, execution will cease "
					+ "after the specified number of rule firings or when the agenda contains no more rule "
					+ "activations. If there are no arguments or the argument is a negative integer, "
					+ "execution will cease when the agenda contains no more rule activations. "
					+ "If the focus stack is empty the MAIN module is automatically in the current "
					+ "focus. Returns the number of rules fired.";
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
			return "(fire)\n" + "(fire 7)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "fire";

	public Fire() {
		super();
		aliases.add("run");
	}

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		long count = 0;
		if (params != null && params.length == 1) {
			long fc = params[0].getValue(engine).implicitCast(JamochaType.LONG)
					.getLongValue();
			try {
				Logging.logger(this.getClass()).debug("Start firing "+ fc+" times");
				count = engine.fire((int) fc);
				Logging.logger(this.getClass()).debug("finished firing");
			} catch (ExecuteException e) {
				throw new EvaluationException(e);
			}
		} else
			try {
				Logging.logger(this.getClass()).debug("Start firing (unbounded)");
				count = engine.fire();
				Logging.logger(this.getClass()).debug("finished firing");
			} catch (ExecuteException e) {
				throw new EvaluationException("Error during fire. ", e);
			}
		return JamochaValue.newLong(count);
	}
}