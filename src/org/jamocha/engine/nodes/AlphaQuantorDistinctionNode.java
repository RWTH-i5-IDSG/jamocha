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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.QuantorBetaFilterNodeDrawer;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
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
	
	@Deprecated
	public AlphaQuantorDistinctionNode(int id, WorkingMemory memory, ReteNet net, int distinctSlot) {
		super(id, memory, net);
		this.distinctionSlot = distinctSlot;
	}
	
	public AlphaQuantorDistinctionNode(Engine e, int distinctSlot) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet(), distinctSlot);
	}

	@Override
	public void addWME(Node sender, WorkingMemoryElement newElem) throws NodeException {
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
	public void removeWME(Node sender, WorkingMemoryElement oldElem) throws NodeException {
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
