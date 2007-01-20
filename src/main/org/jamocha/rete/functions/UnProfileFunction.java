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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 *
 * ProfileFunction is used to turn on profiling. It provides basic
 * profiling of assert, retract, add activation, remove activation
 * and fire.
 */
public class UnProfileFunction implements Function, Serializable {

    public static final String PROFILE = "unprofile";
    
	/**
	 * 
	 */
	public UnProfileFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        if (params != null && params.length > 0) {
            for (int idx=0; idx < params.length; idx++) {
                if (params[idx].getStringValue().equals("all")) {
                    engine.setProfile(Rete.PROFILE_ALL);
                } else if (params[idx].getStringValue().equals("assert-fact")) {
                    engine.setProfile(Rete.PROFILE_ASSERT);
                } else if (params[idx].getStringValue().equals("add-activation")) {
                    engine.setProfile(Rete.PROFILE_ADD_ACTIVATION);
                } else if (params[idx].getStringValue().equals("fire")) {
                    engine.setProfile(Rete.PROFILE_FIRE);
                } else if (params[idx].getStringValue().equals("retract-fact")) {
                    engine.setProfile(Rete.PROFILE_RETRACT);
                } else if (params[idx].getStringValue().equals("remove-activation")) {
                    engine.setProfile(Rete.PROFILE_RM_ACTIVATION);
                }
            }
        }
        DefaultReturnVector ret = new DefaultReturnVector();
        return ret;
	}

	public String getName() {
		return PROFILE;
	}

	public Class[] getParameter() {
        return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(unprofile assert|all|retract|fire|add-activation|remove-activation)";
	}

}
