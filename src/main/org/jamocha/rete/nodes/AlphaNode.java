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
	 * 
	 * @param engine
	 * @param factInstance
	 */
	@Override
	public void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		if (evaluate((Fact) fact)) {
			facts.add((Fact) fact);
			propogateAssert(fact, engine);
		}
	}

	/**
	 * Retract a fact from the node
	 * 
	 * @param fact
	 * @param engine
	 */
	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		facts.remove((Fact)fact);
		propogateRetract(fact, engine);
	}

	@Override
	protected void mountChild(BaseNode newChild, Rete engine) throws AssertException {
		for (Fact fact : facts)
			//eval before send down:
			if (evaluate((Fact) fact))
				newChild.assertFact(fact, engine, this);
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine) throws RetractException {
		for (Fact fact : facts)
//			eval before send down:
			if (evaluate((Fact) fact))
				oldChild.retractFact(fact, engine, this);
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
		return "slot(" + this.slot.getId() + ") " + ConversionUtils.getPPOperator(this.operator) + " " + this.slot.getValue().toString();
	}

	/**
	 * Method returns the pretty printer formatted string of the node's
	 * condition. For now, the method just replaces the operator. It might be
	 * nice to replace the slot id with the slot name.
	 * 
	 * @return
	 */
	public String toPPString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AlphaNode: ID ");
		sb.append(getNodeId());
		sb.append(" Slot Condition: ");
		sb.append(slot.getName());
		sb.append(" ");
		sb.append(ConversionUtils.getPPOperator(this.operator));
		sb.append(" ");
		sb.append(ConversionUtils.formatSlot(this.slot.getValue()));
		sb.append(" ; SubNodes: ");
		sb.append(getChildCount());
		sb.append(" \nAlpha-Memory: ");
		sb.append(facts.toPPString());
		return sb.toString();
	}

}