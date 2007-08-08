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
package org.jamocha.rete.nodes;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.AlphaMemory;

/**
 * @author Peter Lin
 * 
 * BaseAlpha is the abstract base class for 1-input nodes. Alpha nodes have an
 * boolean field for Just-In-Time optimization. The algorithm for JIT is the
 * following:
 * 
 * Let's start out with an example rule to provide some context.
 * 
 * <pre>
 * Rule:
 *   if
 *     the account purchases for the last 12 months exceed 500
 *     the account type is premium
 *     the account last activity was less than 1 week
 *     the purchase total is greater than 150.00
 *   then
 *     give the user a 15% discount
 * </pre>
 * 
 * Since RETE remembers which alpha nodes matched, we know how many facts
 * matched for each condition. When an user writes the rule, they shouldn't have
 * to know the optimal order of the conditions. Lets say the match count for the
 * rule above is this:
 * 
 * <pre>
 * Rule:
 *   if
 *     the account purchases for the last 12 months exceed 500 (300)
 *     the account type is premium (250)
 *     the account last activity was less than 1 week (100)
 *     the purchase total is greater than 150.00 (80)
 *   then
 *     give the user a 15% discount
 * </pre>
 * 
 * By re-ordering the alpha nodes, we can reduce the number of partial matches.
 * Although it may seem trivial, it can result in a dramatic decrease in memory
 * usage and provide a significant performance boost. If the nodes are ordered
 * in reverse, that means there are 450 fewer partial matches. If we scale this
 * problem up to thousands of rules and half million records, it's easy to see
 * it ends up saving a ton of partial matches. The problem isn't noticeable for
 * small applications, but as an application scales up in the number of rules
 * and facts, it can potentially reduce partial matches by an order of magnitude
 * or more.
 */
public abstract class AbstractAlpha extends BaseNode {

	@Override
	public String toPPString() {
		StringBuffer result = new StringBuffer();
		result.append(super.toPPString());
		result.append("Alpha-Memory: ");
		result.append(facts.toPPString(10));
		result.append("\n");
		return result.toString();
	}

	/**
	 * The operator to compare two values
	 */
	protected int operator = Constants.EQUAL;

	protected AlphaMemory facts = null;

	public AbstractAlpha(int id) {
		super(id);
		this.maxChildCount = Integer.MAX_VALUE;
		facts = new AlphaMemory();
	}

	/**
	 * Abstract implementation returns an int code for the operator. To get the
	 * string representation, it should be converted.
	 */
	public int getOperator() {
		return this.operator;
	}

	protected void mountChild(BaseNode newChild, ReteNet net)
			throws AssertException {
		for (Fact fact : facts)
			newChild.assertFact(fact, net, this);
	}

	protected void unmountChild(BaseNode oldChild, ReteNet net)
			throws RetractException {
		for (Fact fact : facts)
			oldChild.retractFact(fact, net, this);
	}

	public void clear() {
		facts.clear();
	}

}