/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
import java.util.Iterator;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Prints out a list of all defined functions. The return
 * value is NIL.
 */
public class ListFunctions implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out a list of all defined functions. The return value is NIL.";
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
			return "(list-deffunctions)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "list-deffunctions";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		Map fgroups = engine.getFunctionMemory().getFunctionGroups();
		Iterator itr = fgroups.values().iterator();
		int counter = 0;
		while (itr.hasNext()) {
			// we iterate over the function groups and print out the
			// functions in each group
			FunctionGroup fg = (FunctionGroup) itr.next();
			engine.writeMessage("++++ " + fg.getName() + " ++++"
					+ Constants.LINEBREAK, "t");
			Iterator listitr = fg.listFunctions().iterator();
			while (listitr.hasNext()) {
				Function f = (Function) listitr.next();
				engine.writeMessage("  " + f.getName() + Constants.LINEBREAK,
						"t");
				counter++;
			}
		}
		engine.writeMessage(counter + " functions" + Constants.LINEBREAK, "t");
		return JamochaValue.NIL;
	}
}