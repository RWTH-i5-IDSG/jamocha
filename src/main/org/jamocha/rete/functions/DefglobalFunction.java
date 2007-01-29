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
 */
public class DefglobalFunction implements Serializable, Function {

	/**
	 * 
	 */
	public DefglobalFunction() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#executeFunction(woolfel.engine.rete.Rete, woolfel.engine.rete.Parameter[])
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getParameter()
	 */
	public Class[] getParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#toPPString(woolfel.engine.rete.Parameter[], int)
	 */
	public String toPPString(Parameter[] params, int indents) {
		// TODO Auto-generated method stub
		return null;
	}

}
