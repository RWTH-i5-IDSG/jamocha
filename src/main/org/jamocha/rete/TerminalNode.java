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
package org.jamocha.rete;

import java.util.Iterator;

import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 * 
 * Terminal node indicates the rule has matched fully and should execute the
 * action of the rule. NOTE: currently this is not used directly. other terminal
 * nodes extend it.
 */
public class TerminalNode extends BaseNode {

	protected Rule theRule = null;

	/**
	 * @param id
	 */
	public TerminalNode(int id, Rule rl) {
		super(id);
		this.theRule = rl;
	}

	/**
	 * The terminal nodes doesn't have a memory, so the method does nothing.
	 */
	public void clear(WorkingMemory mem) {
	}

	/**
	 * Once the facts propogate to this point, it means all the conditions of
	 * the rule have been met. The method creates a new Activation and adds it
	 * to the activationList of the correct module. Note: we may want to change
	 * the design so that we don't create a new Activation object.
	 * 
	 * @param idx
	 * @param engine
	 */
	public void assertFacts(Index idx, Rete engine, WorkingMemory mem) {
		engine.assertEvent(this, idx.getFacts());
		Activation act = new BasicActivation(this.theRule, idx);
		engine.getAgenda().addActivation(act);
	}

	/**
	 * Retract means we need to remove the activation from the correct module
	 * agenda.
	 * 
	 * @param idx
	 * @param engine
	 */
	public void retractFacts(Index idx, Rete engine, WorkingMemory mem) {
		Activation act = new BasicActivation(this.theRule, idx);
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
	 * return the name of the rule
	 */
	public String toPPString() {
		return this.theRule.getName();
	}

	/**
	 * The terminal node has no successors, so this method does nothing.
	 */
	public void removeAllSuccessors() {
	}
}
