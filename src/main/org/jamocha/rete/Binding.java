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

import java.io.Serializable;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.nodes.FactTuple;

/**
 * @author Peter Lin
 *
 * A binding can be an object or a field of a given object. When binding
 * is an Object binding, the column id will not be set. The binding
 * would be the row for the left memory.</br>
 * It is up to classes using the binding to check if it is an object
 * binding and get the appropriate fact using getLeftRow().
 * One thing about the current design is the binding is position based.
 * The benefit is it avoids having to set the binding and reset it
 * multiple times. BetaNodes use the binding to get the correct slot
 * value and use it to evaluate an atomic condition. A significant
 * downside of this approach is when deftemplates are re-declared at
 * runtime. It means that we might need to recompute the bindings, which
 * could be a very costly process. More thought and research is needed
 * to figure out the best way to handle re-declaring deftemplates.
 */

//TODO unite Binding, BoundParam, PreBinding and so on (maybe in OO fashion)

public class Binding implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected int operator = Constants.EQUAL;
	
	/**
     * This is the name of the variable. Every binding must
     * have a variable name. It can be user defined or auto-
     * generated by the rule compiler.
     */
    protected String varName = null;

    /**
     * if the binding is to an object, the field should
     * be true. by default it is false.
     */
    protected boolean isObjVar = false;
    
    /**
     * by default the row index is -1, which means
     * it's not set. Any index that is negative indicates
     * it's not set.
     */
    protected int leftrow = -1;
    
    /**
     * The indexes of the left deftemplate
     */
    protected int leftIndex;

    /**
     * by default the row index is -1, which means
     * it's not set. Any index that is negative indicates
     * it's not set.
     */
    protected int rightrow = -1;

    /**
     * the indexes for the right deftemplate
     */
    protected int rightIndex;
    
    
	public Binding(int operator) {
		this();
		this.operator = operator;
	}

	public Binding() {
		super();
	}

	public int getOperator() {
		return this.operator;
	}
	
	public boolean evaluate(Fact right, FactTuple left) throws EvaluationException {
			return Evaluate.evaluate(operator, left.getFact(leftrow).getSlotValue(leftIndex), right.getSlotValue(rightIndex));
	}
    
    /**
     * Return the name of the variable
     * @return
     */
    public String getVarName(){
        return this.varName;
    }
    
    /**
     * Set the variable name. This is important, since the join
     * nodes will use it at runtime.
     * @param name
     */
    public void setVarName(String name){
        this.varName = name;
    }

    /**
     * If the binding is for an object, the method returns true.
     * @return
     */
    public boolean getIsObjectVar(){
        return this.isObjVar;
    }

    /**
     * Set whether the binding is an object binding.
     * @param obj
     */
    public void setIsObjectVar(boolean obj){
        this.isObjVar = obj;
    }
    
    /**
     * Return the left Deftemplate 
     * @return
     */
    public int getLeftRow(){
        return this.leftrow;
    }

    /**
     * Set the left deftemplate
     * @param temp
     */
    public void setLeftRow(int left){
        this.leftrow = left;
    }
    
    /**
     * get the left index
     * @return
     */
    public int getLeftIndex(){
        return this.leftIndex;
    }

    /**
     * set the left index
     * @param indx
     */
    public void setLeftIndex(int indx){
        this.leftIndex = indx;
    }
    
    /**
     * get the right deftemplate
     * @return
     */
    public int getRightRow(){
        return this.rightrow;
    }

    /**
     * set the right deftemplate
     * @param temp
     */
    public void setRightRow(int right){
        this.rightrow = right;
    }

    /**
     * get the right index
     * @return
     */
    public int getRightIndex(){
        return this.rightIndex;
    }
    
    /**
     * set the right index
     * @param indx
     */
    public void setRightIndex(int indx){
        this.rightIndex = indx;
    }
    
  
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("?" + varName + " left(");
        buf.append(this.leftrow);
        buf.append(",");
        buf.append(this.leftIndex);
        buf.append(") ");
        buf.append(ConversionUtils.getOperatorDescription(operator));
        buf.append(" right(");
        buf.append(rightrow);
        buf.append(",");
        buf.append(this.rightIndex);
        buf.append(")");
        return buf.toString();
    }

	public void setOperator(int operator) {
		this.operator = operator;
	}
}
