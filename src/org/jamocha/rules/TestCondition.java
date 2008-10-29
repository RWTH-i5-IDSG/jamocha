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

import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.RuleCompiler.SFRuleCompiler;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.nodes.Node;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

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

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
	public Condition clone() {
		TestCondition result = new TestCondition();
		result.func = (Signature)this.func.clone();
		return result;
	}

	
	
	public List<Constraint> getConstraints() {
		List<Constraint> result = new ArrayList<Constraint>();
		evaluateConstraints(func.getParameters(), result);
		return result;
	}

	private void evaluateConstraints(Parameter[] parameters, List<Constraint> result) {
		for (Parameter p : parameters) {
			if (p instanceof BoundParam) {
				BoundParam bp = (BoundParam) p;
				BoundConstraint bc = new BoundConstraint(null, bp.getVariableName(), false);
				result.add(bc);
			} else if (p instanceof Signature) {
				Signature s = (Signature) p;
				evaluateConstraints(s.getParameters(), result);
			}
		}
	}

	public int getComplexity() {
		/* here, we can't say good things about the complexity,
		 * since we can't know, what the underlying function will do
		 */
		return 10000;
	}

	public List<Constraint> getFlatConstraints() {
		return getConstraints();
	}
	
	public boolean testEquals(Condition o) {
		return false;
	}
	
	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
	public <T, S> S acceptVisitor(ConditionVisitor<T, S> visitor, T data) {
		return visitor.visit(this, data);
	}


	public String dump(String prefix) {
		return prefix + "test";
	}



}
