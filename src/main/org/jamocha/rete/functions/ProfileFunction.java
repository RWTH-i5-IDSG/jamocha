/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 *
 * ProfileFunction is used to turn on profiling. It provides basic
 * profiling of assert, retract, add activation, remove activation
 * and fire.
 */
public class ProfileFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PROFILE = "profile";
    
	/**
	 * 
	 */
	public ProfileFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
        if (params != null && params.length > 0) {
            for (int idx=0; idx < params.length; idx++) {
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
                } else if (param.getIdentifierValue().equals("remove-activation")) {
                    engine.setProfile(Rete.PROFILE_RM_ACTIVATION);
                }
            }
        }
        return JamochaValue.NIL;
	}

	public String getName() {
		return PROFILE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(profile assert|all|retract|fire|add-activation|remove-activation)";
	}

}
