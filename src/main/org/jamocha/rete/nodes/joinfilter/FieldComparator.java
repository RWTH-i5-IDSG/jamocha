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
package org.jamocha.rete.nodes.joinfilter;

import java.io.Serializable;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.nodes.FactTuple;

/**
 * @author Josef Alexander Hahn
 */

public class FieldComparator implements Serializable, Cloneable, JoinFilter {

	private static final long serialVersionUID = 1L;

	protected int operator = Constants.EQUAL;
	protected String varName = null;
    protected RightFieldAddress right = null;
    protected LeftFieldAddress  left  = null;
    
	public FieldComparator(String varName, LeftFieldAddress left, int operator, RightFieldAddress right) {
		this(varName,left,right);
		this.operator = operator;
	}

	public FieldComparator(String varName, LeftFieldAddress left, RightFieldAddress right) {
		super();
		this.varName=varName;
		this.left=left;
		this.right=right;
	}

	public int getOperator() {
		return this.operator;
	}
	
	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public boolean evaluate(Fact rightinput, FactTuple leftinput) throws JoinFilterException {
		JamochaValue rightValue = null, leftValue = null;
		if (right.refersWholeFact()) {
			rightValue = rightinput.getSlotValue( -1 );
		} else {
			rightValue = rightinput.getSlotValue( right.getSlotIndex() );			
		}
		
		if (left.refersWholeFact()) {
			leftValue = leftinput.getFacts()[left.getRowIndex()].getSlotValue( -1 );
		} else {
			leftValue = leftinput.getFacts()[left.getRowIndex()].getSlotValue( left.getSlotIndex() );			
		}

		return Evaluate.evaluate(operator, leftValue, rightValue);
	}
    
    public String getVarName(){
        return this.varName;
    }
    
    public void setVarName(String name){
        this.varName = name;
    }

  
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("?" + varName + " ");
        buf.append(left.toPPString());
        buf.append(" ");
        buf.append(ConversionUtils.getOperatorDescription(operator));
        buf.append(" ");
        buf.append(right.toPPString());
        return buf.toString();
    }

}
