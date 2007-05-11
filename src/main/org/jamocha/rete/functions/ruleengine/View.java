/*
 * Copyright 2006 Josef Hahn, 2007 Alexander Wilden
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

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemoryImpl;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.visualisation.ViewGraphNode;
import org.jamocha.rete.visualisation.Visualiser;

/**
 * @author Josef Alexander Hahn
 * 
 * Opens a visualisation window for the rete net.
 */
public class View implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Opens a visualisation window for the rete net.";
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "view";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		RootNode root = ((WorkingMemoryImpl) engine.getWorkingMemory())
				.getRootNode();

		@SuppressWarnings("unused")
		ViewGraphNode t = ViewGraphNode.buildFromRete(root);
		Visualiser visualiser = new Visualiser(engine);
		visualiser.show();
		return JamochaValue.NIL;
	}

	void traverse(int indent, BaseNode b) {
		for (int i = 0; i < indent; i++)
			System.out.print(" ");
		System.out.println("+" + b.toString() + " id=" + b.getNodeId());
		for (int i = 0; i < b.getChildNodes().length; i++)
			traverse(indent + 2, (BaseNode) b.getChildNodes()[i]);
	}
}