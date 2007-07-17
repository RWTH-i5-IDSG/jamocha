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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Peter Lin
 * 
 * A TestCondition is a pattern that uses a function. For example, in CLIPS,
 * (test (> ?var1 ?var2) )
 */
public class TestCondition extends AbstractCondition {

	private static final long serialVersionUID = 1L;

	protected Signature func = null;
	

	/**
	 * 
	 */
	public TestCondition() {
		super();
	}
	
	public TestCondition(Signature function) {
		this.func = function;
	}

	public Signature getFunction() {
		return this.func;
	}

	public void setFunction(Signature function) {
		this.func = function;
	}

	public boolean executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		func.setParameters(params);
		JamochaValue rv = func.getValue(engine);
		return rv.getBooleanValue();
	}

	public boolean compare(Complexity cond) {
		return false;
	}

	public void clear() {
	}
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}

	public List<Constraint> getConstraints() {
		return null;
	}

	public List getAllBoundConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getBoundConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasBindings() {
		return false;
	}

	public BaseNode getLastNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toClipsFormat(int indent) {
		String ind = "";
		while (ind.length() < indent*blanksPerIndent) ind+=" ";
		StringBuffer result = new StringBuffer();
		result.append(ind+"(test\n");
		result.append(func.toClipsFormat(indent+1));
		result.append(ind+")");
		return result.toString();
	}
	
	public Object clone() throws CloneNotSupportedException {
		TestCondition result = new TestCondition();

		result.negated = this.negated;
		result.totalComplexity = this.totalComplexity;
		result.func = (Signature)this.func.clone();
		
		return result;
	}
	

}
