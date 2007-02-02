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

import java.io.Serializable;

import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rule.Action;
import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * The current implementation of Activation performs several steps
 * 1. get a timestamp for the activation
 * 2. add the timestamp for the facts
 */
public class BasicActivation implements Activation, Serializable {

	/**
	 * the rule to fire
	 */
	private Rule theRule;
	/**
	 * the time tag is the time stamp of when the activation was
	 * created and added to the agenda.
	 */
	private long timetag;
	/**
	 * these are the facts that activated the rule. It's important
	 * to keep in mind that any combination of facts may fire a
	 * rule. 
	 */
	private Index index;
	/**
	 * The aggregate time of the facts that triggered the rule.
	 */
	private long aggreTime = 0;

	/**
	 * 
	 */
	public BasicActivation(Rule rule, Index index) {
		super();
		this.theRule = rule;
		this.index = index;
		this.timetag = System.nanoTime();
		calculateTime(index.getFacts());
	}

	protected void calculateTime(Fact[] facts) {
		for (int idx = 0; idx < facts.length; idx++) {
			this.aggreTime += facts[idx].timeStamp();
		}
	}

	/**
	 * The facts that matched the rule
	 * @return
	 */
	public Fact[] getFacts() {
		return this.index.getFacts();
	}

	/**
	 * the index is used to compare the facts quickly
	 * @return
	 */
	public Index getIndex() {
		return index;
	}

	/**
	 * the rule that matched
	 * @return
	 */
	public Rule getRule() {
		return this.theRule;
	}

	/**
	 * the timestamp of when the activation was created. the time is in
	 * nanoseconds.
	 * @return
	 */
	public long getTimeStamp() {
		return this.timetag;
	}

	/**
	 * Convienant method for comparing two Activations in a module's
	 * activation list. If the rule is the same and the index is the
	 * same, the method returns true. This compare method isn't meant
	 * to be used for strategies. It is up to strategies to compare
	 * two activations against each other using various criteria.
	 * @param act
	 * @return
	 */
	public boolean compare(Activation act) {
		if (act == this) {
			return false;
		}
		if (act.getRule() == this.theRule && act.getIndex().equals(this.index)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The purpose of the method is to execute the actions of the
	 * rule. The current implementation calls Rule.setTriggerFacts()
	 * at the start and Rule.resetTriggerFacts() at the end.
	 * Note: Only one activation can be executing at any given time,
	 * so setting the trigger facts should not be an issue. Although
	 * one could queue up the assert/retract/modify in the rule
	 * action, that can lead to undesirable results. The only edge
	 * case that could occur is in backward chaining mode. If the
	 * actions of a rule results in the activation of a backward
	 * rule, it is possible to have nested execution of different
	 * rules. Generally speaking, a rule should not result in 
	 * infinite recursion, since that would product a Stack over flow
	 * in Java.
	 */
	public void executeActivation(Rete engine) throws ExecuteException {
		try {
			this.theRule.setTriggerFacts(this.getFacts());
			Action[] actions = this.theRule.getActions();
			for (int idx = 0; idx < actions.length; idx++) {
				if (actions[idx] != null) {
					actions[idx].executeAction(engine, this.getFacts());
				} else {
					throw new ExecuteException(ExecuteException.NULL_ACTION);
				}
			}
			this.theRule.resetTriggerFacts();
		} catch (ExecuteException e) {
			throw e;
		}
	}

	/**
	 * Return the sum of the fact timestamp triggering the rule
	 * @return
	 */
	public long getAggregateTime() {
		return this.aggreTime;
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Activation: " + this.theRule.getName());
		for (int idx = 0; idx < this.getFacts().length; idx++) {
			buf.append(", f-" + this.getFacts()[idx].getFactId());
		}
		buf.append(": AggrTime-" + this.aggreTime);
		return buf.toString();
	}

	/**
	 * clear will set the rule to null and call Index.clear
	 */
	public void clear() {
		this.theRule = null;
	}

}
