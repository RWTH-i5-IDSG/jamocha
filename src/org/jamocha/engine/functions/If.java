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

package org.jamocha.engine.functions;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.IfElseConfiguration;

/**
 * @author Christoph Emonds, Alexander Wilden, Sebastian Reinartz
 * 
 * Implementation of the if condition as Jamocha Function. Returns either the
 * result of the last then action executed if condition holds or otherwise the
 * result of the last else action executed if any.
 */
public class If extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Implementation of the if condition as Jamocha Function. Returns either the result of the last then action executed if condition holds or otherwise the result of the last else action executed if any.";
		}

		public int getParameterCount() {
			return 0;
		}

		public String getParameterDescription(int parameter) {
			return "";
		}

		public String getParameterName(int parameter) {
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
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
			return null;
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "if";

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
		JamochaValue result = JamochaValue.NIL;
		if (params != null && params.length == 1) {
			IfElseConfiguration ifElseConf = (IfElseConfiguration) params[0];
			engine.pushScope();
			boolean conditionValue = ifElseConf.getCondition().getValue(engine)
					.getBooleanValue();
			if (conditionValue) {
				if (ifElseConf.getThenActions() != null)
					result = ifElseConf.getThenActions().getValue(engine);
			} else if (ifElseConf.getElseActions() != null)
				result = ifElseConf.getElseActions().getValue(engine);
			engine.popScope();
		} else
			throw new IllegalParameterException(1);
		return result;
	}
}
