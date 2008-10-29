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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn
 * 
 */
public class NodeMemory extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints the memory content of a node. Returns 'True', iff there was a node with the given id.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			if (parameter==0) return "The node ID";
			return null;
		}

		public String getParameterName(int parameter) {
			if (parameter==0) return "node-id";
			return null;
		}

		public JamochaType[] getParameterTypes(int parameter) {
			if (parameter==0) return JamochaType.LONGS;
			return null;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(node-memory 0)";
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}

		public Object getExpectedResult() {
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "node-memory";

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
		Node n = null;
		long nodeId = params[0].getValue(engine).getLongValue();
		for(Node m: engine.getNet().getAllNodes()) {
			if (m.getId() == nodeId) {
				n = m;
			}
		}
		if (n==null) return JamochaValue.FALSE;

		engine.writeMessage("Memory from node "+nodeId+":\n");
		for(WorkingMemoryElement wme : n.memory() ) {
			engine.writeMessage(wme.toString());
		}
		return JamochaValue.TRUE;
	}

}