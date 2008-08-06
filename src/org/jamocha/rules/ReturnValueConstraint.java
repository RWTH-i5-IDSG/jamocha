/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.rules;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.engine.Parameter;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn
 * 
 * Return value constraints will become evaluated to
 * the return value of a function call
 */
public class ReturnValueConstraint extends AbstractConstraint {

	static final long serialVersionUID = 1;

	/**
	 * the name of the function
	 */
	protected String functionName = null;

	protected List<Parameter> parameters;

	public ReturnValueConstraint(String functionName, List<Parameter> parameters) {
		this.functionName = functionName;
		this.parameters = parameters;
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public boolean isNegated() {
		return false;
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return null;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public JamochaValue getValue() {
		return null;
	}

	public String getConstraintName() {
		return null;
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}


	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
//	public <T, S> S acceptVisitor(LHSVisitor<T, S> visitor, T data) {
//		return visitor.visit(this, data);
//	}



}