package org.jamocha.engine.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.QuantorBetaFilterNodeDrawer;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;

public class AlphaQuantorDistinctionNode extends OneInputNode {

	private int distinctionSlot;
	
	Map<Object,List<WorkingMemoryElement>> facts;
	
	private boolean addFact(Object key, WorkingMemoryElement f) {
		List<WorkingMemoryElement> factList = facts.get(key);
		if (factList == null) {
			factList = new ArrayList<WorkingMemoryElement>();
			facts.put(key, factList);
		}
		int sizeBefore = factList.size();
		factList.add(f);
		return ( sizeBefore == 0 );
	}
	
	private boolean removeFact(Object key, WorkingMemoryElement f) {
		List<WorkingMemoryElement> factList = facts.get(key);
		if (factList == null) {
			return false;
		}
		int sizeBefore = factList.size();
		factList.remove(f);
		return (sizeBefore == 1 && factList.size() == 0);		
	}
	
	public AlphaQuantorDistinctionNode(int id, WorkingMemory memory, ReteNet net, int distinctSlot) {
		super(id, memory, net);
		this.distinctionSlot = distinctSlot;
	}

	@Override
	public void addWME(WorkingMemoryElement newElem) throws NodeException {
		try{
			if (!isActivated())
				return;
			if ( addFact(key(newElem) , newElem)) {
				addAndPropagate(newElem);
			}
		} catch (EvaluationException e) {
			throw new NodeException(e,this);
		}
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new QuantorBetaFilterNodeDrawer(this);
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	public void removeWME(WorkingMemoryElement oldElem) throws NodeException {
		try{
			if (!isActivated())
				return;
			if ( addFact(key(oldElem) , oldElem)) {
				addAndPropagate(oldElem);
			}
		} catch (EvaluationException e) {
			throw new NodeException(e,this);
		}
	}

	private Object key(WorkingMemoryElement oldElem) throws EvaluationException {
		return (distinctionSlot == -1 ) ?
					oldElem.getFirstFact()
				:
					oldElem.getFirstFact().getSlotValue(distinctionSlot);
	}

}
