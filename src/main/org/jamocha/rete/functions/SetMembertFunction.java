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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Peter Lin
 * 
 * SetMemberFunction is equivalent to JESS set-member function. This is a
 * completely clean implementation from scratch. The name and function signature
 * are similar, but the design and implementation are different. The design of
 * the function is strongly influenced by CLIPS, since the primary goal is full
 * CLIPS compatability.
 */
public class SetMembertFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String SET_MEMBER = "set-member";

	/**
	 * 
	 */
	public SetMembertFunction() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#executeFunction(woolfel.engine.rete.Rete,
	 *      woolfel.engine.rete.Parameter[])
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (engine != null && params != null && params.length == 3) {
			Object instance = params[0].getValue(engine).getObjectValue();
			String slot = params[1].getValue(engine).getIdentifierValue();
			Object val = params[2].getValue(engine).getObjectValue();
			Defclass dc = engine.findDefclass(instance);
			// we check to make sure the Defclass exists
			if (dc != null) {
				Method setm = dc.getWriteMethod(slot);
				try {
					setm.invoke(instance, new Object[] { val });
				} catch (IllegalAccessException e) {

				} catch (InvocationTargetException e) {

				}
			}
		} else {
			throw new IllegalParameterException(3);
		}
		return JamochaValue.NIL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getName()
	 */
	public String getName() {
		return SET_MEMBER;
	}


	public String toPPString(Parameter[] params, int indents) {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}
