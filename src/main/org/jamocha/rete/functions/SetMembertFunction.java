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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.StringParam;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 *
 * SetMemberFunction is equivalent to JESS set-member function. This is a completely
 * clean implementation from scratch. The name and function signature are similar,
 * but the design and implementation are different. The design of the function is
 * strongly influenced by CLIPS, since the primary goal is full CLIPS compatability.
 */
public class SetMembertFunction implements Function, Serializable {

    public static final String SET_MEMBER = "set-member";
    
	/**
	 * 
	 */
	public SetMembertFunction() {
		super();
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#executeFunction(woolfel.engine.rete.Rete, woolfel.engine.rete.Parameter[])
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        if (engine != null && params != null && params.length == 3) {
            BoundParam bp = (BoundParam)params[0];
            StringParam slot = (StringParam)params[1];
            ValueParam val = (ValueParam)params[2];
            Object instance = bp.getObjectRef();
            Defclass dc = engine.findDefclass(instance);
            // we check to make sure the Defclass exists
            if (dc != null) {
                Method setm = dc.getWriteMethod(slot.getStringValue());
                try {
                    setm.invoke(instance,new Object[]{val});
                } catch (IllegalAccessException e) {
                    
                } catch (InvocationTargetException e) {
                    
                }
            }
        }
		return new DefaultReturnVector();
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getName()
	 */
	public String getName() {
		return SET_MEMBER;
	}

	/**
     * The current implementation expects 3 parameters in the following
     * sequence:<br/>
     * BoundParam
     * StringParam
     * ValueParam
	 * <br/>
     * Example: (set-member ?objectVariable slotName value)
	 */
	public Class[] getParameter() {
		return new Class[] {BoundParam.class,StringParam.class,ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}
