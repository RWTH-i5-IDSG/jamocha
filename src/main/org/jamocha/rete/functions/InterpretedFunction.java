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

import java.util.HashMap;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * 
 * @author Peter Lin
 */
public class InterpretedFunction implements Function {

    private String name = null;
    protected String ppString = null;
    protected Parameter[] inputParams = null;
    private Function internalFunction = null;
    /**
     * these are the functions we pass to the top level function.
     * they may be different than the input parameters for the
     * function.
     */
    private Parameter[] functionParams = null;
    private HashMap bindings = new HashMap();

    /**
     * 
     */
    public InterpretedFunction(String name, Parameter[] params, Function func,
            Parameter[] functionParams) {
        this.name = name;
        this.inputParams = params;
        this.internalFunction = func;
        this.functionParams = functionParams;
    }

    public void configureFunction(Rete engine) {
        
    }
    
    /* (non-Javadoc)
     * @see org.jamocha.rete.Function#executeFunction(org.jamocha.rete.Rete, org.jamocha.rete.Parameter[])
     */
    public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        // the first thing we do is set the values
        DefaultReturnVector ret = new DefaultReturnVector();
        if (params.length == this.inputParams.length) {
            for (int idx=0; idx < this.inputParams.length; idx++) {
                BoundParam bp = (BoundParam)this.inputParams[idx];
                this.bindings.put(bp.getVariableName(), params[idx].getValue());
            }
            engine.setInterpretedFunction(this);
            ret =  (DefaultReturnVector)this.internalFunction.executeFunction(engine, this.functionParams);
            engine.setInterpretedFunction(null);
            return ret;
        } else {
            DefaultReturnValue rv = new DefaultReturnValue(
                    Constants.BOOLEAN_OBJECT, new Boolean(false));
            ret.addReturnValue(rv);
            DefaultReturnValue rv2 = new DefaultReturnValue(
                    Constants.STRING_TYPE, "incorrect number of parameters");
            ret.addReturnValue(rv2);
            return ret;
        }
    }

    public String getName() {
        return this.name;
    }

    public Class[] getParameter() {
        return new Class[]{BoundParam.class};
    }

    public int getReturnType() {
        return this.internalFunction.getReturnType();
    }

    public String toPPString(Parameter[] params, int indents) {
        return ppString;
    }

    public Parameter[] getInputParameters() {
        return inputParams;
    }

    public Parameter[] getFunctionParams() {
        return functionParams;
    }

    public void setFunctionParams(Parameter[] functionParams) {
        this.functionParams = functionParams;
    }

    public Object getBinding(String var) {
        return this.bindings.get(var);
    }
}
