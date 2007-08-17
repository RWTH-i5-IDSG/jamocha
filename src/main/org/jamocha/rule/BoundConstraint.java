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
package org.jamocha.rule;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Peter Lin
 * 
 * BoundConstraint is a basic implementation of Constraint interface for bound
 * constraints. When a rule declares a slot as a binding, a BoundConstraint is
 * used.
 */
public class BoundConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 1L;



	/**
	 * In the case of BoundConstraints, the value is the name of the variable
	 * given my the user
	 */
	protected JamochaValue value;

	protected boolean isObjectBinding = false;

	protected boolean negated = false;

	protected boolean firstDeclaration = false;

	/**
	 * if the binding is for a multislot, it should be set to true. by default,
	 * it is false.
	 */
	protected boolean isMultislot = false;

	/**
	 * 
	 */
	public BoundConstraint() {
		super();
	}

	public BoundConstraint(String name, boolean isObjBind) {
		super();
		setName(name);
		this.isObjectBinding = isObjBind;
	}

	/**
	 * The name of the slot or object field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * the name is the name of the slot or object field.
	 */
	public void setName(String name) {
		if (name.startsWith("?")) {
			this.name = name.substring(1);
		} else {
			this.name = name;
		}
	}

	/**
	 * The value is the name of the variable. In the case of CLIPS, if the rule
	 * as "?name", the value returned is "name" without the question mark
	 * prefix.
	 */
	public JamochaValue getValue() {
		return value;
	}

	/**
	 * The input parameter should be a string and it should be the name of the
	 * variable. Make sure to parse out the prefix. For example, CLIPS uses "?"
	 * to denote a variable.
	 */
	public void setValue(JamochaValue val) {
		this.value = val;
	}

	public String getVariableName() {
		return this.value.getIdentifierValue();
	}

	/**
	 * Set the constraint to true if the binding is for an object or a deffact.
	 * 
	 * @param obj
	 */
	public void setIsObjectBinding(boolean obj) {
		this.isObjectBinding = obj;
	}

	/**
	 * if the binding is to an object or deffact, the method will return true.
	 * 
	 * @return
	 */
	public boolean getIsObjectBinding() {
		return this.isObjectBinding;
	}

	/**
	 * if the binding is for a multislot, it will return true. by default is is
	 * false.
	 * 
	 * @return
	 */
	public boolean isMultislot() {
		return this.isMultislot;
	}

	/**
	 * only set the multislot to true if the slot is defined as a multislot
	 * 
	 * @param multi
	 */
	public void setIsMultislot(boolean multi) {
		this.isMultislot = multi;
	}

	/**
	 * if the literal constraint is negated with a "~" tilda, call the method
	 * pass true.
	 * 
	 * @param negate
	 */
	public void setNegated(boolean negate) {
		this.negated = negate;
	}

	/**
	 * if the literal constraint is negated, the method returns true
	 * 
	 * @return
	 */
	public boolean getNegated() {
		return this.negated;
	}

	public void setFirstDeclaration(boolean first) {
		this.firstDeclaration = first;
	}

	/**
	 * by default the method returns false, unless it is set to true
	 * 
	 * @return
	 */
	public boolean firstDeclaration() {
		return this.firstDeclaration;
	}

	public String toFactBindingPPString() {
		return "  ?" + this.value.toString() + " <-";
	}
	
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append(super.toString());
		res.append(":  ?").append(value).append(" ");
		res.append(ConversionUtils.getOperator(getOperator())  );
		if (isObjectBinding) {
			res.append(" whole fact");
		} else {
			res.append(" slot# ");
			if (getSlot() != null) {res.append(getSlot().getId());} else {res.append("null");}
		}
		return res.toString();
	}
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}
	
	public int getOperator(){
		return (negated? Constants.NOTEQUAL : Constants.EQUAL);
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
}
