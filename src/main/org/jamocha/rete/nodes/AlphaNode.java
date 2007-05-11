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

import org.jamocha.rete.CompositeIndex;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * AlphaNode is a single input node. The design is influenced by CLIPS
 * PatternNode and PartialMatch. I've tried to stay as close to CLIPS design as
 * practical. A obvious difference between CLIPS and this implementation is the
 * lack of memory allocation or passing pointers.<br/> <br/>
 * 
 */
public class AlphaNode extends SlotAlpha {

	private static final long serialVersionUID = 1L;

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
	 * 
	 * @param opr
	 */
	public void setOperator(int opr) {
		this.operator = opr;
	}

	/**
	 * Set the slot id. The slot id is the deftemplate slot id
	 * 
	 * @param id
	 */
	public void setSlot(Slot sl) {
		// the slot must have the correct ID!
		this.slot = sl;
	}

	/**
	 * the implementation will first check to see if the fact already matched.
	 * If it did, the fact stops and doesn't go any further. If it doesn't, it
	 * will attempt to evaluate it and add the fact if it matches.
	 * @param engine
	 * @param factInstance
	 */
	@Override
	protected boolean assertFact(Fact fact, Rete engine, BaseNode sender) throws AssertException {
		if (evaluate(fact)){
			facts.add(fact);
			return true;
		}
		return false;
	}

	/**
	 * Retract a fact from the node
	 * 
	 * @param factInstance
	 * @param engine
	 */
	@Override
	public void retractFact(Fact fact, Rete engine, BaseNode sender) throws RetractException {
		facts.remove(fact);
	}

	/**
	 * evaluate the node's value against the slot's value. The method uses
	 * Evaluate class to perform the evaluation
	 * 
	 * @param factInstance
	 * @param engine
	 * @return
	 */
	protected boolean evaluate(Fact factInstance) {
		return Evaluate.evaluate(this.operator, factInstance.getSlotValue(this.slot.getId()), this.slot.getValue());
	}

	/**
	 * Method returns the string format of the node's condition. later on this
	 * should be cleaned up.
	 */
	public String toString() {
		return "slot(" + this.slot.getId() + ") " + ConversionUtils.getPPOperator(this.operator) + " " + this.slot.getValue().toString() + " - useCount=" + this.useCount;
	}


	/**
	 * Method returns the pretty printer formatted string of the node's
	 * condition. For now, the method just replaces the operator. It might be
	 * nice to replace the slot id with the slot name.
	 * 
	 * @return
	 */
	public String toPPString() {
		return "AlphaNode-" + this.nodeID + "> slot(" + this.slot.getName() + ") " + ConversionUtils.getPPOperator(this.operator) + " " + ConversionUtils.formatSlot(this.slot.getValue()) + " - useCount="
				+ this.useCount;
	}

}