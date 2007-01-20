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
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


/**
 * @author Peter Lin
 *
 * The purpose of Shell function is to make it easy to parse text in the shell
 * and execute the real function. ShellFunction expects the parser to pass
 * the name of the real function and parameter values.
 */
public class ShellFunction implements Function, Serializable {

    public String funcName = null;
    
    private Function actualFunction = null;
    private Parameter[] params = null;
    
	/**
	 * 
	 */
	public ShellFunction() {
		super();
	}
    
    public void lookUpFunction(Rete engine) {
        this.actualFunction = engine.findFunction(this.funcName);
    }

	public int getReturnType() {
		return this.actualFunction.getReturnType();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        this.lookUpFunction(engine);
        if (this.params != null && this.actualFunction != null) {
            return this.actualFunction.executeFunction(engine,this.params);
        } else {
        	DefaultReturnVector rv = new DefaultReturnVector();
    		DefaultReturnValue rval = 
    			new DefaultReturnValue(Constants.BOOLEAN_OBJECT,new Boolean(false));
    		rv.addReturnValue(rval);
            return rv;
        }
	}

	public String getName() {
		return funcName;
	}

	public Class[] getParameter() {
		return this.actualFunction.getParameter();
	}

    /**
     * The name of the function to call
     * @param name
     */
    public void setName(String name) {
        this.funcName = name;
    }
    
    public Parameter[] getParameters() {
        return this.params;
    }
    
    public void setParameters(Parameter[] params) {
        this.params = params;
    }
    
    public void setFunction(Function func) {
        this.actualFunction = func;
    }
    
    public Function getFunction() {
    	return this.actualFunction;
    }

	public String toPPString(Parameter[] params, int indents) {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}
