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
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.TestNode;
import org.jamocha.rete.configurations.Signature;

/**
 * @author Peter Lin
 * 
 * A TestCondition is a pattern that uses a function. For example, in CLIPS,
 * (test (> ?var1 ?var2) )
 */
public class TestCondition extends AbstractCondition {

	private static final long serialVersionUID = 1L;

	protected Signature func = null;

	protected TestNode node = null;

	protected ArrayList binds = new ArrayList();

	protected boolean negated = false;

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

	public void setTestNode(TestNode node) {
		this.node = node;
	}

	public TestNode getTestNode() {
		return this.node;
	}

	/**
	 * the current implementation creates a new ArrayList, adds the TestNode to
	 * it and returns the list.
	 */
	public List getNodes() {
		List<BaseNode> n = new ArrayList<BaseNode>();
		n.add(node);
		return n;
	}

	/**
	 * The current implementation checks to make sure the node is a TestNode. If
	 * it is, it will set the node. If not, it will ignore it.
	 */
	public void addNode(BaseNode node) {
		if (node instanceof TestNode) {
			this.node = (TestNode) node;
		}
	}

	public BaseNode getLastNode() {
		return this.node;
	}

	/**
	 * the implementation will look at the parameters for the function and see
	 * if it takes BoundParam
	 */
	public boolean hasBindings() {
		// if (this.func.getParameter() != null) {
		// Class[] pms = func.getParameter();
		// for (int idx=0; idx < pms.length; idx++) {
		// if (pms[idx] == BoundParam.class) {
		// binds.add(pms[idx]);
		// }
		// }
		// if (binds.size() > 0) {
		// return true;
		// } else {
		// return true;
		// }
		// } else {
		// return false;
		// }
		// TODO first we just return true. We don't know here, if any of the
		// parameter for the function are BoundParams
		return true;
	}

	/**
	 * return an List of the bindings. in the case of TestCondition, the
	 * bindings are BoundParam
	 */
	public List getAllBindings() {
		return binds;
	}

	public List getBindings() {
		return binds;
	}

	public boolean isNegated() {
		return this.negated;
	}

	public void setNegated(boolean negate) {
		this.negated = negate;
	}

	public void clear() {
		node = null;
	}
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}
}
