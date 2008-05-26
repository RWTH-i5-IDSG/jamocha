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
package org.jamocha.rules;

import java.util.List;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Complexity;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;

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
	protected TestCondition() {
		super();
	}
	
	public TestCondition(Signature function) {
		this.func = function;
	}

	public Signature getFunction() {
		return this.func;
	}

	protected boolean executeFunction(Engine engine, Parameter[] params) throws EvaluationException {
		func.setParameters(params);
		JamochaValue rv = func.getValue(engine);
		return rv.getBooleanValue();
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
	public Condition clone() throws CloneNotSupportedException {
		TestCondition result = new TestCondition();
		result.func = (Signature)this.func.clone();
		return result;
	}

	public List<Constraint> getConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getComplexity() {
		/* here, we can't say good things about the complexity,
		 * since we can't know, what the underlying function will do
		 */
		return 100;
	}
	

}
