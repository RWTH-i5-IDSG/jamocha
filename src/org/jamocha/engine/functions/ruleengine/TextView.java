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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.RootNode;

/**
 * @author Josef Alexander Hahn
 * 
 * Opens a visualisation window for the rete net.
 */
public class TextView extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints the rete network in text view";
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
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(text-view)";
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

	public static final String NAME = "text-view";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public void dump(Node n, StringBuilder s, int indent) {
		StringBuilder s2 = new StringBuilder();
		for (int i = 0; i < indent; i++)
			s2.append(" ");
		s.append(s2);
		String ind = "\n" + s2;
		s.append(n.toString().replace("\n", ind).trim()).append("\n");
		for (Node child : n.getChildNodes())
			dump(child, s, indent + 3);
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		RootNode root = engine.getNet().getRoot();
		StringBuilder s = new StringBuilder();
		dump(root, s, 0);
		engine.writeMessage(s.toString());
		return JamochaValue.NIL;
	}

}