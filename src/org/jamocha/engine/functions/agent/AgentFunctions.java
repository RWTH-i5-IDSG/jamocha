/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.functions.agent;

import org.jamocha.communication.agent.JamochaAgent;
import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;
import org.jamocha.languages.sl.sl2clips_adapter.CLIPS2SLFunction;
import org.jamocha.languages.sl.sl2clips_adapter.SL2CLIPSFunction;
import org.jamocha.languages.sl.sl2clips_adapter.SLMessageCompare;

public class AgentFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	private final JamochaAgent agent;

	public AgentFunctions(JamochaAgent agent) {
		super();
		name = "AgentFunctions";
		this.agent = agent;
	}

	@Override
	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new AgentName(agent));
		addFunction(functionMem, new AgentLocalName(agent));
		addFunction(functionMem, new AgentSendMessage(agent));
		addFunction(functionMem, new NewConversationId(agent));
		addFunction(functionMem, new SLMessageCompare());
		addFunction(functionMem, new SL2CLIPSFunction());
		addFunction(functionMem, new CLIPS2SLFunction());

		addFunction(functionMem, new AgentRegisterFunction(agent));
		addFunction(functionMem, new AgentUnregisterFunction(agent));
		addFunction(functionMem, new AgentSearchFunction(agent));
	}

}