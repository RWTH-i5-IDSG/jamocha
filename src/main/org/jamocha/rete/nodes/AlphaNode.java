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

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.CompositeIndex;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Slot;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;

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
	public AlphaNode(int id, WorkingMemory memory) {
		super(id, memory);
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
	public void assertFact(WorkingMemoryElement fact, ReteNet net, BaseNode sender) throws AssertException {
		if (evaluate((Fact) fact)) {
			facts.add((Fact) fact);
			propogateAssert(fact, net);
		}
	}

	/**
	 * Retract a fact from the node
	 * 
	 * @param fact
	 * @param engine
	 */
	@Override
	public void retractFact(WorkingMemoryElement fact, ReteNet net, BaseNode sender) throws RetractException {
		facts.remove((Fact) fact);
		propogateRetract(fact, net);
	}

	@Override
	protected void mountChild(BaseNode newChild, ReteNet net) throws AssertException {
		for (Fact fact : facts)
			// eval before send down:
			if (evaluate((Fact) fact))
				newChild.assertFact(fact, net, this);
	}

	@Override
	protected void unmountChild(BaseNode oldChild, ReteNet net) throws RetractException {
		for (Fact fact : facts)
			// eval before send down:
			if (evaluate((Fact) fact))
				oldChild.retractFact(fact, net, this);
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
		try {
			return Evaluate.evaluate(this.operator, factInstance.getSlotValue(this.slot.getId()), this.slot.getValue());
		} catch (EvaluationException e) {
			//should not occur
			e.printStackTrace();
			return false;
		}
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
		sb.append(super.toPPString());
		sb.append("Slot Condition: ");
		sb.append(slot.getName());
		sb.append(" ");
		sb.append(ConversionUtils.getOperatorDescription(this.operator));
		sb.append(" ");
		sb.append(ConversionUtils.formatSlot(this.slot.getValue()));
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public boolean mergableTo(BaseNode other) {
		boolean result = false;
		// equals if same type, same operator, same slot
		if (other instanceof AlphaNode) {
			AlphaNode an = (AlphaNode) other;
			result = ((this.operator == an.operator) && (this.slot.mergableTo(an.slot)));
		}
		return result;
	}

}