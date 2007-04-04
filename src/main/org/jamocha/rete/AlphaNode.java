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

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * AlphaNode is a single input node. The design is influenced by CLIPS
 * PatternNode and PartialMatch. I've tried to stay as close to CLIPS
 * design as practical. A obvious difference between CLIPS and this
 * implementation is the lack of memory allocation or passing pointers.<br/>
 * <br/>
 * 
 */
public class AlphaNode extends BaseAlpha2 {

	/**
	 * The use of Slot(s) is similar to CLIPS design
	 */
	protected Slot slot = null;

    protected String hashstring = null;
    
    protected CompositeIndex compIndex = null;

	/**
	 * 
	 */
	public AlphaNode(int id) {
		super(id);
	}

	/**
	 * Set the operator using the int value
	 * @param opr
	 */
	public void setOperator(int opr) {
		this.operator = opr;
	}

	/**
	 * the first time the RETE compiler makes the node shared,
	 * it needs to increment the useCount.
	 * @param share
	 */
	public boolean isShared() {
		if (this.useCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set the slot id. The slot id is the deftemplate slot id
	 * @param id
	 */
	public void setSlot(Slot sl) {
		this.slot = sl;
	}

	/**
	 * return the times the node is shared
	 * @return
	 */
	public int getUseCount() {
		return this.useCount;
	}

	/**
	 * the implementation will first check to see if the fact already matched.
	 * If it did, the fact stops and doesn't go any further. If it doesn't,
	 * it will attempt to evaluate it and add the fact if it matches.
	 * @param factInstance
	 * @param engine
	 */
	public void assertFact(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        AlphaMemory alpha = (AlphaMemory) mem.getAlphaMemory(this);
		if (evaluate(fact)) {
			alpha.addPartialMatch(fact);
			// if watch is on, we notify the engine. Rather than
			// create an event class here, we let Rete do that.
			propogateAssert(fact, engine, mem);
		}
	}
    
	/**
	 * Retract a fact from the node
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void retractFact(Fact fact, Rete engine, WorkingMemory mem) 
    throws RetractException
    {
        AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
        if (alpha.isPartialMatch(fact)) {
            alpha.removePartialMatch(fact);
            // if watch is on, we notify the engine. Rather than
            // create an event class here, we let Rete do that.
            propogateRetract(fact,engine,mem);
        }
	}

	/**
	 * evaluate the node's value against the slot's value. The method
	 * uses Evaluate class to perform the evaluation
	 * @param factInstance
	 * @param engine
	 * @return
	 */
	public boolean evaluate(Fact factInstance) {
		return Evaluate.evaluate(this.operator,
                factInstance.getSlotValue(this.slot.getId()),
                this.slot.value);
	}

	/**
	 * Method returns the string format of the node's condition. later on
	 * this should be cleaned up.
	 */
	public String toString() {
		return "slot(" + this.slot.getId() + ") " + 
			ConversionUtils.getPPOperator(this.operator) + " "
			+ this.slot.value.toString()+ " - useCount=" +
            this.useCount;
	}

    public CompositeIndex getHashIndex() {
        if (compIndex == null) {
            compIndex = 
                new CompositeIndex(slot.getName(),this.operator,slot.value);
        }
        return compIndex;
    }
    
    /**
     * Method returns a hash string for ObjectTypeNode. The format is
     * slotName:operator:value
     * @return
     */
    public String hashString() {
        if (this.hashstring == null) {
            this.hashstring = this.slot.getId() + ":" + this.operator + ":"
            + String.valueOf(this.slot.value);
        }
        return this.hashstring;
    }

    /**
	 * Method returns the pretty printer formatted string of the node's
	 * condition. For now, the method just replaces the operator. It might
	 * be nice to replace the slot id with the slot name.
	 * @return
	 */
	public String toPPString() {
		return "AlphaNode-" + this.nodeID + "> slot(" + this.slot.getName() + ") "	+ 
                ConversionUtils.getPPOperator(this.operator) + " "
				+ ConversionUtils.formatSlot(this.slot.value) + 
				" - useCount=" + this.useCount;
	}
}