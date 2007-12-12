/*
 * Copyright (C) 2007 Alexander Wilden
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
 */
package org.jamocha.apps.jamochaagent.userfunctions;

import org.jamocha.apps.jamochaagent.JamochaAgent;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Alexander Wilden
 * 
 * 
 */
public class AgentLocalName extends AbstractFunction {
	
	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Return the local name of the agent";
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
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(local-agent-name)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "agent-local-name";

	private JamochaAgent agent;

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public AgentLocalName(JamochaAgent agent) {
		this.agent = agent;
		// just for backwards compatibility
		aliases.add("local-agent-name");
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		return JamochaValue.newString(agent.getLocalName());
	}

}
