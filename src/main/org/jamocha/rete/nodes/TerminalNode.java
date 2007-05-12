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
package org.jamocha.rete.nodes;

import org.jamocha.rete.Activation;
import org.jamocha.rete.BasicActivation;
import org.jamocha.rete.Fact;
import org.jamocha.rete.LinkedActivation;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * Terminal node indicates the rule has matched fully and should execute the
 * action of the rule. NOTE: currently this is not used directly. other terminal
 * nodes extend it.
 */
public class TerminalNode extends BaseNode {

	private static final long serialVersionUID = 1L;

	protected Rule theRule = null;

	/**
	 * @param id
	 */
	public TerminalNode(int id, Rule rl) {
		super(id);
		this.theRule = rl;
		this.maxChildCount = 0;
		this.maxParentCount = 1;
	}

	/**
	 * The terminal nodes doesn't have a memory, so the method does nothing.
	 */
	public void clear() {
	}

	/**
	 * Once the facts propogate to this point, it means all the conditions of
	 * the rule have been met. The method creates a new Activation and adds it
	 * to the activationList of the correct module. Note: we may want to change
	 * the design so that we don't create a new Activation object.
	 * 
	 * @param engine
	 * @param idx
	 */
	public void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		FactTuple tuple = null;
		if (sender.isRightNode())
			tuple = new FactTuple((Fact) fact);
		else
			tuple = (FactTuple) fact;

		Activation act = new LinkedActivation(this.theRule, tuple);
		engine.getAgenda().addActivation(act);
	}

	/**
	 * Retract means we need to remove the activation from the correct module
	 * agenda.
	 * 
	 * @param engine
	 * @param idx
	 */
	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		FactTuple tuple = null;
		if (sender.isRightNode())
			tuple = new FactTuple((Fact) fact);
		else
			tuple = (FactTuple) tuple;

		Activation act = new BasicActivation(this.theRule, tuple);
		engine.getAgenda().removeActivation(act);
	}

	public Rule getRule() {
		return this.theRule;
	}

	/**
	 * return the name of the rule
	 */
	public String toString() {
		return this.theRule.getName();
	}

	/**
	 * return a descriptive string
	 */
	public String toPPString() {
		return "TerminalNode-" + nodeID + ">";
	}


	@Override
	protected void mountChild(BaseNode newChild, Rete engine) {
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine) {
	}

}
