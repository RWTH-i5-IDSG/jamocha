/*
 * Copyright 2002-2007 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;

/**
 * @author Peter Lin
 * 
 * Predicate constraint binds the slot and then performs some function on it.
 * For example (myslot ?s&:(> ?s 100) )
 * 
 */
public class PredicateConstraint implements Constraint {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;
	
    /**
         * the name of the slot
         */
    protected String name = null;

    /**
         * the name of the variable
         */
    protected String varName = null;

    /**
         * the name of the function
         */
    protected String functionName = null;

    protected JamochaValue value = JamochaValue.NIL;

    protected ArrayList parameters = new ArrayList();

    protected boolean isPredicateJoin = false;

    /**
         * 
         */
    public PredicateConstraint() {
	super();
    }

    /*
         * (non-Javadoc)
         * 
         * @see woolfel.engine.rule.Constraint#getName()
         */
    public String getName() {
	return this.name;
    }

    /*
         * (non-Javadoc)
         * 
         * @see woolfel.engine.rule.Constraint#setName(java.lang.String)
         */
    public void setName(String name) {
	this.name = name;
    }

    /*
         * (non-Javadoc)
         * 
         * @see woolfel.engine.rule.Constraint#getValue()
         */
    public JamochaValue getValue() {
	return this.value;
    }

    /*
         * (non-Javadoc)
         * 
         * @see woolfel.engine.rule.Constraint#setValue(java.lang.Object)
         */
    public void setValue(JamochaValue val) {
	this.value = val;
    }

    public String getVariableName() {
	return this.varName;
    }

    public void setVariableName(String name) {
	this.varName = name;
    }

    public String getFunctionName() {
	return this.functionName;
    }

    public void setFunctionName(String func) {
	this.functionName = func;
    }

    public boolean isPredicateJoin() {
	return this.isPredicateJoin;
    }

    public void addParameters(List params) {
	this.parameters.addAll(params);
	int bcount = 0;
	// we try to set the value
	Iterator itr = parameters.iterator();
	while (itr.hasNext()) {
	    Object p = itr.next();
	    // for now, a simple implementation
	    if (p instanceof JamochaValue) {
		this.setValue((JamochaValue) p);

		break;
	    } else if (p instanceof BoundParam) {
		BoundParam bp = (BoundParam) p;
		if (!bp.getVariableName().equals(this.varName)) {
		    this.setValue(JamochaValue.newBinding(bp));
		}
		bcount++;
	    }
	}
	if (bcount > 1) {
	    this.isPredicateJoin = true;
	}
    }

    public void addParameter(Expression param) {
	this.parameters.add(param);
	if (param instanceof JamochaValue) {
	    this.setValue((JamochaValue) param);
	} else if (param instanceof BoundParam && this.varName == null) {
	    this.varName = ((BoundParam) param).getVariableName();
	}
    }

    public List getParameters() {
	return this.parameters;
    }

    public int parameterCount() {
	return this.parameters.size();
    }

    /**
         * the purpose of normalize is to look at the order of the parameters
         * and flip the operator if necessary
         * 
         */
    public void normalize() {

    }

	public boolean getNegated() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setNegated(boolean negate) {
		// TODO Auto-generated method stub
		
	}
}
