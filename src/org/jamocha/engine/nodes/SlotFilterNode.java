/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.nodes;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Evaluate;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.parser.EvaluationException;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> this node type is an
 *         alpha-node. it filters the input by a given slot, an operator and a
 *         constant value.
 */
public class SlotFilterNode extends OneInputNode {

	private final int operator;

	private final Slot slot;

	@Deprecated
	public SlotFilterNode(final int id, final WorkingMemory memory,
			final int operator, final Slot slot, final ReteNet net) {
		super(id, memory, net);
		this.operator = operator;
		this.slot = slot;
	}
	
	public SlotFilterNode(Engine e, int operator, Slot slot) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), operator, slot, e.getNet());
	}


	protected boolean evaluate(final WorkingMemoryElement elem)
			throws NodeException {
		try {
			return Evaluate.evaluate(operator, elem.getFirstFact()
					.getSlotValue(slot.getId()), slot.getValue());
		} catch (final EvaluationException e) {
			throw new NodeException(e, this);
		}
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		if (evaluate(newElem)) {
			addAndPropagate(newElem);
		}
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
		if (evaluate(oldElem)) {
			removeAndPropagate(oldElem);
		}
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|").append(slot.getId()).append(
				(operator == Constants.EQUAL ? "==" : "!=")).append(
				slot.getValue());
	}

	@Override
	public void accept(final NodeVisitor visitor) {
		visitor.visit(this);
	}

}
