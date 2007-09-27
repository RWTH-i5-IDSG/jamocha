/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Turns off profiling. There is the choice to turn off profiling
 * of assert, retract, add activation, remove activation, and fire. 
 * Returns NIL.
 */
public class UnProfile extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Turns off profiling. There is the choice to turn off profiling of assert, retract, add activation, " +
					"remove activation, and fire. Returns NIL.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "What to unprofile. One or several of: all, assert-fact, add-activation, fire, retract-fact or remove-activation.";
		}

		public String getParameterName(int parameter) {
			return "unprofile";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.IDENTIFIERS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter > 0);
		}

		public String getExample() {
			return "(profile all)\n" +
					"(deftemplate templ1 (slot name))\n" +
					"(print-profile)\n" +
					"(unprofile assert-fact)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "unprofile";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				JamochaValue param = params[idx].getValue(engine);
				if (param.getStringValue().equals("all")) {
					engine.setProfile(Constants.PROFILE_ALL);
				} else if (param.getStringValue().equals("assert-fact")) {
					engine.setProfile(Constants.PROFILE_ASSERT);
				} else if (param.getStringValue().equals("add-activation")) {
					engine.setProfile(Constants.PROFILE_ADD_ACTIVATION);
				} else if (param.getStringValue().equals("fire")) {
					engine.setProfile(Constants.PROFILE_FIRE);
				} else if (param.getStringValue().equals("retract-fact")) {
					engine.setProfile(Constants.PROFILE_RETRACT);
				} else if (param.getStringValue().equals("remove-activation")) {
					engine.setProfile(Constants.PROFILE_RM_ACTIVATION);
				}
			}
		}
		return JamochaValue.NIL;
	}
}