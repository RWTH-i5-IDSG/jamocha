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
import org.jamocha.rete.util.ProfileStats;


/**
 * @author Peter Lin
 *
 * PrintProfileFunction will print out the profile information.
 */
public class PrintProfileFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PRINT_PROFILE = "print-profile";
    
	/**
	 * 
	 */
	public PrintProfileFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
        engine.writeMessage("fire ET=" + ProfileStats.fireTime + 
                " ms" + Constants.LINEBREAK,"t");
        engine.writeMessage("assert ET=" + ProfileStats.assertTime +
                " ms" + Constants.LINEBREAK,"t");
        engine.writeMessage("retract ET=" + ProfileStats.retractTime +
                " ms" + Constants.LINEBREAK,"t");
        engine.writeMessage("add Activation ET=" + ProfileStats.addActivation +
                " ms" + Constants.LINEBREAK,"t");
        engine.writeMessage("remove Activation ET=" + ProfileStats.rmActivation +
                " ms" + Constants.LINEBREAK,"t");
        engine.writeMessage("Activation added=" + ProfileStats.addcount +
                Constants.LINEBREAK,"t");
        engine.writeMessage("Activation removed=" + ProfileStats.rmcount +
                Constants.LINEBREAK,"t");
        return JamochaValue.NIL;
	}

	public String getName() {
		return PRINT_PROFILE;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(print-profile)";
	}

}
