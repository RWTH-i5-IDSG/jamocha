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
import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;


public class MemoryUsedFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MEMORY_FREE = "mem-used";
	
	public MemoryUsedFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long total = rt.totalMemory();
		long used = total - free;
		used = used/1024/1024;
		total = total/1024;
		long mbtotal = total/1024;
		engine.writeMessage(String.valueOf(used) + "Mb used of " +
				String.valueOf(mbtotal) + "Mb " + String.valueOf(total) +
				"Kb " + Constants.LINEBREAK,"t");
		return JamochaValue.NIL;
	}

	public String getName() {
		return MEMORY_FREE;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(mem-used)";
	}

}
