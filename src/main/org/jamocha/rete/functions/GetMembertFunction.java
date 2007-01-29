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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
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
public class GetMembertFunction implements Function, Serializable {

	public static final String GET_MEMBER = "get-member";

	/**
	 * 
	 */
	public GetMembertFunction() {
		super();
	}

	/**
	 * By default, the function returns Object type. Since the function
	 * can be used to call any number of getXXX methods and we wrap
	 * all primitives in their object equivalent, returning Object type
	 * makes the most sense.
	 */
	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#executeFunction(woolfel.engine.rete.Rete, woolfel.engine.rete.Parameter[])
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Object rtn = null;
		DefaultReturnVector drv = new DefaultReturnVector();
		if (engine != null && params != null && params.length == 3) {
			BoundParam bp = (BoundParam) params[0];
			StringParam slot = (StringParam) params[1];
			ValueParam val = (ValueParam) params[2];
			Object instance = bp.getObjectRef();
			Defclass dc = engine.findDefclass(instance);
			// we check to make sure the Defclass exists
			if (dc != null) {
				Method getm = dc.getWriteMethod(slot.getStringValue());
				try {
					rtn = getm.invoke(instance, new Object[] { val });
					int rtype = getMethodReturnType(getm);
					DefaultReturnValue rvalue = new DefaultReturnValue(rtype,
							rtn);
					drv.addReturnValue(rvalue);
				} catch (IllegalAccessException e) {
					// TODO we should handle error, for now not implemented
				} catch (InvocationTargetException e) {
					// TODO we should handle error, for now not implemented
				}
			}
		}
		return drv;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getName()
	 */
	public String getName() {
		return GET_MEMBER;
	}

	/**
	 * The current implementation expects 3 parameters in the following
	 * sequence:<br/>
	 * BoundParam - the bound object
	 * StringParam - the slot name
	 * ValueParam - the value to set the field
	 * <br/>
	 * Example: (set-member ?objectVariable slotName value)
	 */
	public Class[] getParameter() {
		return new Class[] { BoundParam.class, StringParam.class,
				ValueParam.class };
	}

	/**
	 * For now, this utility method is here, but maybe I should move it
	 * to some place else later.
	 * @param m
	 * @return
	 */
	public int getMethodReturnType(Method m) {
		if (m.getReturnType() == String.class) {
			return Constants.STRING_TYPE;
		} else if (m.getReturnType() == int.class
				|| m.getReturnType() == Integer.class) {
			return Constants.INT_PRIM_TYPE;
		} else if (m.getReturnType() == short.class
				|| m.getReturnType() == Short.class) {
			return Constants.SHORT_PRIM_TYPE;
		} else if (m.getReturnType() == long.class
				|| m.getReturnType() == Long.class) {
			return Constants.LONG_PRIM_TYPE;
		} else if (m.getReturnType() == float.class
				|| m.getReturnType() == Float.class) {
			return Constants.FLOAT_PRIM_TYPE;
		} else if (m.getReturnType() == double.class
				|| m.getReturnType() == Double.class) {
			return Constants.DOUBLE_PRIM_TYPE;
		} else {
			return Constants.OBJECT_TYPE;
		}
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(get-member)";
		}
	}
}
