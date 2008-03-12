package org.jamocha.rete.nodes;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.SlotFilterNodeDrawer;
import org.jamocha.rete.wme.Slot;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * this node type is an alpha-node. it filters the input by a given
 * slot, an operator and a constant value. 
 */
public class SlotFilterNode extends OneInputNode {

	private int operator;
	
	private Slot slot;
	
	public SlotFilterNode(int id, WorkingMemory memory, int operator, Slot slot, ReteNet net) {
		super(id, memory, net);
		this.operator = operator;
		this.slot = slot;
	}

	protected boolean evaluate(WorkingMemoryElement elem) throws NodeException {
		try {
			return Evaluate.evaluate(this.operator, elem.getFirstFact().getSlotValue(this.slot.getId()), this.slot.getValue());
		} catch (EvaluationException e) {
			throw new NodeException(e, this);
		}
	}
	
	@Override
	public void addWME(WorkingMemoryElement newElem) throws NodeException {
		if (evaluate(newElem)) addAndPropagate(newElem);
	}

	@Override
	public void removeWME(WorkingMemoryElement oldElem) throws NodeException {
		if (evaluate(oldElem)) removeAndPropagate(oldElem);
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	public void getDescriptionString(StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|").append(slot.getId()).append( (operator==Constants.EQUAL?"==":"!=") ).append(slot.getValue());
	}
	
	protected NodeDrawer newNodeDrawer() {
		return new SlotFilterNodeDrawer(this);
	}

}
