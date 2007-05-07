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

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * profile is used to turn on profiling. It provides basic profiling of assert,
 * retract, add activation, remove activation and fire.
 */
public class Profile implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "profile is used to turn on profiling. It provides basic profiling of assert, retract, add activation, remove activation and fire.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "What to profile. Can be one or more of: all, assert-fact, add-activation, fire, retract-fact or remove-activation.";
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
			return (parameter > 0);
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "profile";

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
				if (param.getIdentifierValue().equals("all")) {
					engine.setProfile(Rete.PROFILE_ALL);
				} else if (param.getIdentifierValue().equals("assert-fact")) {
					engine.setProfile(Rete.PROFILE_ASSERT);
				} else if (param.getIdentifierValue().equals("add-activation")) {
					engine.setProfile(Rete.PROFILE_ADD_ACTIVATION);
				} else if (param.getIdentifierValue().equals("fire")) {
					engine.setProfile(Rete.PROFILE_FIRE);
				} else if (param.getIdentifierValue().equals("retract-fact")) {
					engine.setProfile(Rete.PROFILE_RETRACT);
				} else if (param.getIdentifierValue().equals(
						"remove-activation")) {
					engine.setProfile(Rete.PROFILE_RM_ACTIVATION);
				}
			}
		}
		return JamochaValue.NIL;
	}
}
