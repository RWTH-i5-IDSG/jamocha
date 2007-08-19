/* Copyright 2007 Alexander Wilden
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
package org.jamocha.apps.jamochaagent;

import org.jamocha.adapter.sl.CLIPS2SLFunction;
import org.jamocha.adapter.sl.SL2CLIPSFunction;
import org.jamocha.adapter.sl.SLMessageCompare;
import org.jamocha.rete.functions.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class AgentFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	private JamochaAgent agent;

	public AgentFunctions(JamochaAgent agent) {
		super();
		name = "AgentFunctions";
		this.agent = agent;
	}

	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new AgentName(agent));
		addFunction(functionMem, new SendMessageFunction(agent));
		addFunction(functionMem, new SLMessageCompare());
		addFunction(functionMem, new SL2CLIPSFunction());
		addFunction(functionMem, new CLIPS2SLFunction());
	}

}