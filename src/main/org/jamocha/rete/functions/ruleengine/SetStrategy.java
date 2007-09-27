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
package org.jamocha.rete.functions.ruleengine;

import java.util.Set;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.agenda.ConflictResolutionStrategy;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * Sets the strategy of the current focus to the given argument. If no argument
 * is provided a list of all available strategies is printed out. Returns
 * <code>true</code> if the given strategy is found or no argument was
 * provided and <code>false</code> if the strategy was not found.
 */
public class SetStrategy extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Sets the strategy of the current focus to the given argument. If no argument is provided a list of all available strategies is printed out. Returns true if the given strategy is found or no argument was provided and false if the strategy was not found.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "The Strategy to use";
		}

		public String getParameterName(int parameter) {
			return "strategy";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return true;
		}

		public String getExample() {
			return "(set-strategy)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "set-strategy";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length == 1) {
			String strategyName = params[0].getValue(engine).getStringValue();
			ConflictResolutionStrategy strategy = null;
			try {
				strategy = ConflictResolutionStrategy.getStrategy(strategyName);
			} catch (InstantiationException e) {
				throw new EvaluationException(
						"Error while setting the strategy to " + strategyName,
						e);
			} catch (IllegalAccessException e) {
				throw new EvaluationException(
						"Error while setting the strategy to " + strategyName,
						e);
			}
			if (strategy == null)
				return JamochaValue.FALSE;
			engine.getAgendas().getAgenda(engine.getCurrentFocus())
					.setConflictResolutionStrategy(strategy);
		} else {
			StringBuilder buffer = new StringBuilder("Available Strategies:");
			buffer.append(Constants.LINEBREAK);
			Set<String> strategies = ConflictResolutionStrategy.getStrategies();
			for (String strategy : strategies) {
				buffer.append("- ");
				buffer.append(strategy);
				buffer.append(Constants.LINEBREAK);
			}
			engine.writeMessage(buffer.toString());
		}
		return JamochaValue.TRUE;
	}
}
