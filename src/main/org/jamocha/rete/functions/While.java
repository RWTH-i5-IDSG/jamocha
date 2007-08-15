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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.AbstractFunction;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.WhileDoConfiguration;

/**
 * @author Alexander Wilden
 * 
 * Implementation of the while-loop. An ActionList is executed as long as the
 * given Condition holds. Returns the result of the last action executed.
 */
public class While extends AbstractFunction {

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

	private static final long serialVersionUID = 1L;

	private static AbstractFunction _instance = null;
	
	public static AbstractFunction getInstance() {
		if(_instance == null) {
			_instance = new While();
		}
		return _instance;
	}
	
	private While() {
		name = "while";
		description = new Description();
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
