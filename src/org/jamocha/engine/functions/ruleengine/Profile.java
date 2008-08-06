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
import org.jamocha.engine.util.ProfileStats;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Turns on profiling. Provides basic profiling of assert, retract, add
 * activation, remove activation, and fire. Returns NIL.
 */
public class Profile extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Turns on profiling. Provides basic profiling of assert, retract, add activation, "
					+ "remove activation, and fire. Returns NIL.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "What to profile. One or several of: all, assert-fact, add-activation, fire, retract-fact or remove-activation.";
		}

		public String getParameterName(int parameter) {
			return "profile";
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
			return parameter > 0;
		}

		public String getExample() {
			return "(profile assert-fact fire)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "profile";

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

		// TODO we must reimplement that. profiling is now controlled via the
		// global jamocha settings class
		if (params != null)
			if (params.length > 0)
				for (int idx = 0; idx < params.length; idx++) {
					JamochaValue param = params[idx].getValue(engine);
					if (param.getIdentifierValue().equals("all")) {
						// engine.setProfile(Constants.PROFILE_ALL);
					} else if (param.getIdentifierValue().equals("assert-fact")) {
						// engine.setProfile(Constants.PROFILE_ASSERT);
					} else if (param.getIdentifierValue().equals(
							"add-activation")) {
						// engine.setProfile(Constants.PROFILE_ADD_ACTIVATION);
					} else if (param.getIdentifierValue().equals("fire")) {
						// engine.setProfile(Constants.PROFILE_FIRE);
					} else if (param.getIdentifierValue()
							.equals("retract-fact")) {
						// engine.setProfile(Constants.PROFILE_RETRACT);
					} else if (param.getIdentifierValue().equals(
							"remove-activation")) {
						// engine.setProfile(Constants.PROFILE_RM_ACTIVATION);
					}
				}
			else
				// printout results:
				engine.writeMessage(ProfileStats.printResults());
		return JamochaValue.NIL;
	}
}