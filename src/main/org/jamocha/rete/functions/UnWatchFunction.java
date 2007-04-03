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
 * WatchFunction allows users to watch different engine process, like
 * activations, facts and rules.
 */
public class UnWatchFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final String NAME = "unwatch";
	
	/**
	 * 
	 */
	public UnWatchFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
        if (params != null) {
            // the params are not null, now check the parameter count
            if (params.length > 0) {
                for (int idx=0; idx < params.length; idx++) {
                    String cmd = params[idx].getValue(engine).getStringValue();
                    setWatch(engine,cmd);
                }
            }
        }
        return JamochaValue.NIL;
	}
    
    protected void setWatch(Rete engine, String cmd) {
        if (cmd.equals("all")) {
            engine.setUnWatch(Rete.WATCH_ALL);
        } else if (cmd.equals("facts")) {
            engine.setUnWatch(Rete.WATCH_FACTS);
        } else if (cmd.equals("activations")) {
            engine.setUnWatch(Rete.WATCH_ACTIVATIONS);
        } else if (cmd.equals("rules")) {
            engine.setUnWatch(Rete.WATCH_RULES);
        }
    }

	public String getName() {
		return NAME;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(unwatch)";
	}

}
