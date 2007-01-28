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
package org.jamocha.rete;

/**
 * @author Peter Lin
 *
 * FunctionParam is a parameter which gets a value from a nested
 * function call. As a general rule, a function parameter may need
 * to assert/modify/retract facts; therefore the default constructor
 * takes Rete. This can happen when a user writes a test pattern
 * like (test (> 10 (* .5 ?var) ) ) .
 * In the example above, the test would first multiple the bound
 * variable with .5. The product is then compared to 10. At runtime,
 * the TestNode would pass the necessary fact to a function that
 * uses a bound variable.
 */
public class FunctionParam extends AbstractParam {

    /**
     * The function to call
     */
    protected Function func = null;
    protected int valueType = -1;
    protected Object value = null;
    protected Fact[] facts;
    protected BoundParam[] params = null;
    protected Rete engine = null;
    
	/**
	 * The constructor takes a parameter
	 */
	public FunctionParam(Function func, Rete rete) {
		super();
        this.func = func;
        this.engine = rete;
	}

	/**
     * Return the return value type.
	 */
	public int getValueType() {
		return this.valueType;
	}

	/**
     * getValue() should trigger the function 
	 */
	public Object getValue() {
        // execute the function and return the value
        initParams();
        value = this.func.executeFunction(this.engine,this.params);
		return this.value;
	}

    public Object getValue(Rete engine, int valueType) {
        initParams();
        value = this.func.executeFunction(engine,this.params);
        return this.value;
    }
    
    /**
     * 
     * @param facts
     */
    public void setFacts(Fact[] facts){
        this.facts = facts;
    }
    
    /**
     * 
     *
     */
    protected void initParams(){
        for (int idx=0; idx < params.length; idx++){
        	if (params[idx].isObjectBinding()) {
                params[idx].setFact(this.facts);
        	} else if (params[idx] instanceof BoundParam){
        		// we look up the value
        		BoundParam bp = (BoundParam)params[idx];
        		Object val = this.engine.getDefglobalValue(bp.getVariableName());
        		bp.setResolvedValue(val);
        	}
        }
    }
    
    /**
     * reset the function and set the references to the facts
     * to null
     */
    public void reset(){
        this.facts = null;
        this.value = null;
    }
}
