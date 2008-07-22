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

import java.util.Iterator;
import java.util.Map;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.functions.FunctionGroup;

/**
 * @author Peter Lin
 * 
 * Prints out a list of all defined functions. The return value is NIL.
 */
public class ListFunctions extends AbstractFunction {

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
			return "(list-functions)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "list-functions";

	public ListFunctions() {
		super();
		aliases.add("list-deffunctions");
		aliases.add("functions");
	}

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
		Map<String, FunctionGroup> fgroups = engine.getFunctionMemory()
				.getFunctionGroups();
		Iterator<FunctionGroup> itr = fgroups.values().iterator();
		int counter = 0;
		FunctionGroup fg;
		Function f;
		while (itr.hasNext()) {
			// we iterate over the function groups and print out the
			// functions in each group
			fg = itr.next();
			engine.writeMessage(fg.getName() + ":" + Constants.LINEBREAK, "t");
			Iterator<Function> listitr = fg.listFunctions().iterator();
			while (listitr.hasNext()) {
				f = listitr.next();
				engine.writeMessage(" - " + f.getName() + Constants.LINEBREAK,
						"t");
				counter++;
			}
		}
		engine.writeMessage(counter + " functions" + Constants.LINEBREAK, "t");
		return JamochaValue.NIL;
	}
}