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
import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.SlotFilterNodeDrawer;
import org.jamocha.engine.Evaluate;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> this node type is an
 *         alpha-node. it filters the input by an operator and two slot names.
 */
public class AlphaSlotComparatorNode extends OneInputNode {

	private final int operator;

	private final TemplateSlot slot1, slot2;
	
	private int id1, id2;

	public AlphaSlotComparatorNode(final int id, final WorkingMemory memory,
			final int operator, final TemplateSlot slot1, TemplateSlot slot2, final ReteNet net) {
		super(id, memory, net);
		this.operator = operator;
		this.slot1 = slot1;
		this.slot2 = slot2;
		id1 = slot1.getId();
		id2 = slot2.getId();
	}

	protected boolean evaluate(final WorkingMemoryElement elem)	throws NodeException {
		try {
			JamochaValue val1 = elem.getFirstFact().getSlotValue(id1);
			JamochaValue val2 = elem.getFirstFact().getSlotValue(id2);
			boolean eq = val1.equals(val2);
			if (operator == Constants.NOTEQUAL) eq = !eq;
			return eq;
		} catch (EvaluationException e) {
			throw new NodeException("error evaluating. must be a bug.",this);
		}
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		if (evaluate(newElem))
			addAndPropagate(newElem);
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
		if (evaluate(oldElem))
			removeAndPropagate(oldElem);
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|").append(slot1.getId()).append(
				(operator == Constants.EQUAL ? "==" : "!=")).append(
				slot2.getId());
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new SlotFilterNodeDrawer(this);
	}

}
