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

import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 *
 * Describe difference between the Function parameters
 */
public class FunctionParam2 extends AbstractParam {

    protected Function func = null;
    protected String funcName = null;
    private Parameter[] params = null;
    private Rete engine = null;

	public FunctionParam2() {
		super();
	}
	
	public void setFunctionName(String name) {
		this.funcName = name;
	}
	
	public String getFunctionName() {
		return this.funcName;
	}
	
	public void setEngine(Rete engine) {
		this.engine = engine;
	}
	
	public void configure(Rete engine, Rule util) {
		if (this.engine == null) {
			this.engine = engine;
		}
		for (int idx=0; idx < this.params.length; idx++) {
			if (this.params[idx] instanceof BoundParam) {
				// we need to set the row value if the binding is a slot or fact
				BoundParam bp = (BoundParam)this.params[idx];
				Binding b1 = util.getBinding(bp.getVariableName());
				if (b1 != null) {
					bp.setRow(b1.getLeftRow());
					if (b1.getLeftIndex() == -1) {
						bp.setObjectBinding(true);
					}
				}
			}
		}
	}
	
	public void setParameters(Parameter[] params) {
		this.params = params;
	}
	
    public void lookUpFunction() {
        this.func = engine.findFunction(this.funcName);
    }

    public int getValueType() {
		return this.func.getReturnType();
	}

	public Object getValue() {
        if (this.params != null) {
            return this.func.executeFunction(engine,this.params);
        } else {
            return null;
        }
	}

	public void reset() {
		this.engine = null;
		this.params = null;
	}

	public String toPPString() {
		this.lookUpFunction();
		return this.func.toPPString(this.params,1);
	}
}
