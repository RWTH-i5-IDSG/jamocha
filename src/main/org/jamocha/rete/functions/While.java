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
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.WhileDoConfiguration;

/**
 * @author Alexander Wilden
 * 
 * Implementation of the while-loop. An ActionList is executed as long as the
 * given Condition holds. Returns the result of the last action executed.
 */
public class While implements Serializable, Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Implementation of the while-loop. An ActionList is executed as long as the given Condition holds. Returns the result of the last action executed.";
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
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "while";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		if (params != null && params.length == 1) {
			engine.pushScope();
			WhileDoConfiguration whileDoConf = (WhileDoConfiguration) params[0];
			if (whileDoConf.getCondition() != null) {
				while (whileDoConf.getCondition().getValue(engine)
						.getBooleanValue()) {
					if (whileDoConf.getWhileActions() != null) {
						result = whileDoConf.getWhileActions().getValue(engine);
					}
				}
			}
			engine.popScope();
		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}

}
