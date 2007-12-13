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
public class AgentRegisterFunction extends AbstractFunction {
	
	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return ""; //TODO
		}

		public int getParameterCount() {
			return 0; //TODO
		}

		public String getParameterDescription(int parameter) {
			return ""; //TODO
		}

		public String getParameterName(int parameter) {
			return ""; //TODO
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS; //TODO
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS; //TODO
		}

		public boolean isParameterCountFixed() {
			return true; //TODO
		}

		public boolean isParameterOptional(int parameter) {
			return false; //TODO
		}

		public String getExample() { //TODO
			return "(local-agent-name)";
		}

		public boolean isResultAutoGeneratable() { //TODO
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "agent-register"; //TODO

	private JamochaAgent agent;

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public AgentRegisterFunction(JamochaAgent agent) {
		this.agent = agent;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		
		//TODO
		return null;
		
		
	}

}
