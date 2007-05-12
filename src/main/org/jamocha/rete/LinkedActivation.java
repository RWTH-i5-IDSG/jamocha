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
package org.jamocha.rete;

import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.nodes.FactTuple;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rule.Action;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * LinkedActivation is different than BasicActivation in a couple of ways.
 * LinkedActivation makes it easier to remove Activations from an
 * ActivationList, without having to iterate over the activations. When the
 * activation is executed or removed, it needs to make sure it checks the
 * previous and next and set them correctly.
 */
public class LinkedActivation implements Activation {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private static final int MAX_POOL_SIZE = 10000;

	private LinkedActivation prev = null;

	private LinkedActivation next = null;

	private Rule theRule;

	/**
	 * these are the facts that activated the rule. It's important to keep in
	 * mind that any combination of facts may fire a rule.
	 */
	private FactTuple facts;

	private long timetag;

	private long aggreTime = 0;

	private TerminalNode tnode = null;

	/**
	 * 
	 */
	private LinkedActivation() {
	}

	public LinkedActivation(Rule rule, FactTuple tuple) {
		super();
		init(rule, tuple);
	}

	private void init(Rule rule, FactTuple facts) {
		this.theRule = rule;
		this.facts = facts;
		this.timetag = System.nanoTime();
        calculateTime(facts.getFacts());
	}

	private static int instances = 0;

	private static int maxInstances = 0;

	private static final LinkedActivation[] instancePool = new LinkedActivation[MAX_POOL_SIZE];

	/**
	 * The facts that matched the rule
	 * 
	 * @return
	 */
	public Fact[] getFacts() {
		return this.facts.getFacts();
	}

	protected static LinkedActivation acquire(Rule rule, FactTuple facts) {
		LinkedActivation result;
		if (instances == 0) {
			result = new LinkedActivation();
		} else {
			result = instancePool[--instances];
			instancePool[instances] = null;
		}
		result.init(rule, facts);
		return result;
	}

	public static void release(LinkedActivation activation) {
		if (instances < maxInstances || maxInstances < MAX_POOL_SIZE - 1) {
			instancePool[instances++] = activation;
			if (maxInstances < instances) {
				++maxInstances;
			}
		}
	}

	protected void calculateTime(Fact[] facts) {
		for (int idx = 0; idx < facts.length; idx++) {
			this.aggreTime += facts[idx].timeStamp();
		}
	}

	public long getAggregateTime() {
		return this.aggreTime;
	}

	public FactTuple getFactTuple() {
		return facts;
	}

	public Rule getRule() {
		return this.theRule;
	}

	public long getTimeStamp() {
		return this.timetag;
	}

	/**
	 * the method will set the previous activation to the one passed to the
	 * method and it will also set previous.next to this instance.
	 * 
	 * @param previous
	 */
	public void setPrevious(LinkedActivation previous) {
		this.prev = previous;
		if (previous != null) {
			previous.next = this;
		}
	}

	public LinkedActivation getPrevious() {
		return this.prev;
	}

	/**
	 * the method will set the next activation to the one passed to the method
	 * and it will set next.prev to this instance.
	 * 
	 * @param next
	 */
	public void setNext(LinkedActivation next) {
		this.next = next;
		if (next != null) {
			next.prev = this;
		}
	}

	public LinkedActivation getNext() {
		return this.next;
	}

	public void setTerminalNode(TerminalNode node) {
		this.tnode = node;
	}

	public TerminalNode getTerminalNode() {
		return this.tnode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Activation#compare(woolfel.engine.rete.Activation)
	 */
	public boolean compare(Activation act) {
		if (act == this) {
			return true;
		}
		return (act.getRule() == this.theRule && act.getFactTuple().equals(this.facts));
	}

	/**
	 * Remove the Activation from the list and set the previous and next
	 * activation correctly. There's basically 3 cases we have to handle. 1.
	 * first 2. last 3. somewhere in between The current implementation will
	 * first set the previous and next. Once they are correctly set, it will set
	 * the references to those LinkedActivation to null.
	 */
	public void remove() {
		if (this.prev != null && this.next != null) {
			this.prev.setNext(this.next);
			this.next.setPrevious(this.prev);
		} else if (this.prev != null && this.next == null) {
			this.prev.setNext(null);
		} else if (this.prev == null && this.next != null) {
			this.next.setPrevious(null);
		}
		this.prev = null;
		this.next = null;
	}

	/**
	 * method is used to make sure the activation is removed from TerminalNode2.
	 * 
	 * @param engine
	 */
	protected void remove(Rete engine) {
		if (tnode != null) {
			// tnode.removeActivation(engine.getWorkingMemory(),this);
			// TODO: Implement an equivalent of that removeActivation-call,
			// which doesnt exist in TerminalNode(1)
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Activation#executeActivation(woolfel.engine.rete.Rete)
	 */
	public void executeActivation(Rete engine) throws ExecuteException {
		// if previous and next are not null, set the previous/next
		// of each and then set the reference to null
		remove(engine);
		try {
			this.theRule.setTriggerFacts(this.facts.getFacts());
			Action[] actions = this.theRule.getActions();
			for (int idx = 0; idx < actions.length; idx++) {
				if (actions[idx] != null) {
					actions[idx].executeAction(engine, this.facts.getFacts());
				} else {
					throw new ExecuteException(ExecuteException.NULL_ACTION);
				}
			}
		} catch (ExecuteException e) {
			throw e;
		}
	}

	public void clear() {
		this.theRule = null;
		this.facts = null;
		this.tnode = null;
		this.aggreTime = 0;
		release(this);
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Activation: " + this.theRule.getName());
		Fact[] facts = this.facts.getFacts();
		for (int idx = 0; idx < facts.length; idx++) {
			buf.append(", id-" + facts[idx].getFactId());
		}
		buf.append(" AggrTime-" + this.aggreTime);
		return buf.toString();
	}

	public LinkedActivation clone() {
		LinkedActivation la = LinkedActivation.acquire(this.theRule, this.facts);
		return la;
	}

}
