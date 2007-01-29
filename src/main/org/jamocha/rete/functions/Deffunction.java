/*
 * Copyright 2002-2007 Peter Lin
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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * Deffunction is used for functions that are declared in the
 * shell. It is different than a function written in java.
 * Deffunction run interpreted and are mapped to existing
 * functions.
 * 
 * @author Peter Lin
 */
public class Deffunction implements Function {

    protected String name = null;
    protected String ppString = null;
    protected Parameter[] parameters = null;
    protected Function function = null;
    protected Class[] functionParams = null;
    protected int returnType;
    
    /**
     * 
     */
    public Deffunction() {
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
        DefaultReturnVector ret = new DefaultReturnVector();
        boolean add = false;
        if (engine.findFunction(this.name) == null) {
            // first we get the actual function from the shell function
            ShellFunction sf = (ShellFunction)this.function;
            Function f = engine.findFunction(sf.getName());
            InterpretedFunction intrfunc = new InterpretedFunction(this.name,
                    this.parameters, f, sf.getParameters());
            intrfunc.configureFunction(engine);
            engine.declareFunction(intrfunc);
            add = true;
        }
        
        DefaultReturnValue rv = new DefaultReturnValue(
                Constants.BOOLEAN_OBJECT, new Boolean(add));
        ret.addReturnValue(rv);
        return ret;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public Class[] getParameter() {
        return this.functionParams;
    }

    public JamochaType getReturnType() {
        return this.returnType;
    }

    public void setPPString(String text) {
        this.ppString = text;
    }
    
    public String toPPString(Parameter[] params, int indents) {
        return this.ppString;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function functions) {
        this.function = functions;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }
}
