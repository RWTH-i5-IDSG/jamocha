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
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ShellBoundParam;


/**
 * @author Peter Lin
 * 
 * EchoFunction is used to echo variable bindings in the shell.
 */
public class EchoFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ECHO = "echo";

	/**
	 * 
	 */
	public EchoFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.UNDEFINED;
	}

	/**
	 * The method expects an array of ShellBoundParam. The method will use
	 * StringBuffer to resolve the binding and print out 1 binding per
	 * line.
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		for (int idx = 0; idx < params.length; idx++) {
			if (params[idx] instanceof ShellBoundParam) {
				ShellBoundParam bp = (ShellBoundParam) params[idx];
				bp.resolveBinding(engine);
				sb.append(bp.getStringValue()).append(Constants.LINEBREAK);
			}
		}
		return new JamochaValue(JamochaType.STRING, sb.toString());
	}

	public String getName() {
		return ECHO;
	}

	public Class[] getParameter() {
		return new Class[] { ShellBoundParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(echo");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?").append(bp.getVariableName());
				} else {
					buf.append(" \"").append(params[idx].getParameterString()).append("\"");
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(echo [parameter])";
		}
	}
}
