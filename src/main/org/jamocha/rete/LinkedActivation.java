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

import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rule.Action;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 *
 * LinkedActivation is different than BasicActivation in a couple of
 * ways. LinkedActivation makes it easier to remove Activations from
 * an ActivationList, without having to iterate over the activations.
 * When the activation is executed or removed, it needs to make sure
 * it checks the previous and next and set them correctly.
 */
public class LinkedActivation implements Activation {

	private LinkedActivation prev = null;

	private LinkedActivation next = null;

	private Rule theRule;

	private long timetag;

	private Index index;

	private long aggreTime = 0;

	private TerminalNode2 tnode = null;

	/**
	 * 
	 */
	public LinkedActivation(Rule rule, Index inx) {
		super();
		this.theRule = rule;
		this.index = inx;
		this.timetag = System.nanoTime();
        calculateTime(index.getFacts());
	}

    protected void calculateTime(Fact[] facts) {
        for (int idx=0; idx < facts.length; idx++) {
            this.aggreTime += facts[idx].timeStamp();
        }
    }
    
	public long getAggregateTime() {
		return this.aggreTime;
	}

	public Fact[] getFacts() {
		return this.index.getFacts();
	}

	public Index getIndex() {
		return this.index;
	}

	public Rule getRule() {
		return this.theRule;
	}

	public long getTimeStamp() {
		return this.timetag;
	}

	/**
	 * the method will set the previous activation to the one
	 * passed to the method and it will also set previous.next
	 * to this instance.
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
	 * the method will set the next activation to the one
	 * passed to the method and it will set next.prev to
	 * this instance.
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

	public void setTerminalNode(TerminalNode2 node) {
		this.tnode = node;
	}

	public TerminalNode2 getTerminalNode() {
		return this.tnode;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Activation#compare(woolfel.engine.rete.Activation)
	 */
	public boolean compare(Activation act) {
		if (act == this) {
			return true;
		}
		if (act.getRule() == this.theRule && act.getIndex().equals(this.index)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Remove the Activation from the list and set the previous
	 * and next activation correctly. There's basically 3 cases
	 * we have to handle.
	 * 1. first
	 * 2. last
	 * 3. somewhere in between
	 * The current implementation will first set the previous
	 * and next. Once they are correctly set, it will set
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
	 * method is used to make sure the activation is removed from
	 * TerminalNode2.
	 * @param engine
	 */
	protected void remove(Rete engine) {
		if (tnode != null) {
			tnode.removeActivation(engine.getWorkingMemory(),this);
		}
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Activation#executeActivation(woolfel.engine.rete.Rete)
	 */
	public void executeActivation(Rete engine) throws ExecuteException {
		// if previous and next are not null, set the previous/next
		// of each and then set the reference to null
		remove(engine);
		try {
            this.theRule.setTriggerFacts(this.index.getFacts());
			Action[] actions = this.theRule.getActions();
			for (int idx = 0; idx < actions.length; idx++) {
				if (actions[idx] != null) {
					actions[idx].executeAction(engine, this.index.getFacts());
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
        this.tnode = null;
    }

    public String toPPString() {
    	StringBuffer buf = new StringBuffer();
    	buf.append("Activation: " + this.theRule.getName());
    	Fact[] facts = this.index.getFacts();
    	for (int idx=0; idx < facts.length; idx++) {
    		buf.append(", id-" + facts[idx].getFactId());
    	}
    	buf.append(" AggrTime-" + this.aggreTime);
    	return buf.toString();
    }
}
