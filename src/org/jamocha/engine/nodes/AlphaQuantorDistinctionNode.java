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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class AlphaQuantorDistinctionNode extends OneInputNode {

	private List<Integer> distinctionSlots;
	
	Map<Key,List<WorkingMemoryElement>> facts;
	
	private class Key {
		
		public JamochaValue[] v;
		
		public Key(JamochaValue[] v) {
			this.v=v;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(v);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(v, other.v))
				return false;
			return true;
		}

		private AlphaQuantorDistinctionNode getOuterType() {
			return AlphaQuantorDistinctionNode.this;
		}
		
		
	}
	
	private boolean addFact(Key key, WorkingMemoryElement f) {
		List<WorkingMemoryElement> factList = facts.get(key);
		if (factList == null) {
			factList = new ArrayList<WorkingMemoryElement>();
			facts.put(key, factList);
		}
		int sizeBefore = factList.size();
		factList.add(f);
		return ( sizeBefore == 0 );
	}
	
	private boolean removeFact(Key key, WorkingMemoryElement f) {
		List<WorkingMemoryElement> factList = facts.get(key);
		if (factList == null) {
			return false;
		}
		int sizeBefore = factList.size();
		factList.remove(f);
		return (sizeBefore == 1 && factList.size() == 0);		
	}
	
	@Deprecated
	public AlphaQuantorDistinctionNode(int id, WorkingMemory memory, ReteNet net, List<Integer> distinctSlot) {
		super(id, memory, net);
		this.distinctionSlots = distinctSlot;
	}
	
	public AlphaQuantorDistinctionNode(Engine e, List<Integer> distinctSlot) {
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
	public boolean outputsBeta() {
		return false;
	}

	@Override
	public void removeWME(Node sender, WorkingMemoryElement oldElem) throws NodeException {
		try{
			if (!isActivated())
				return;
			if ( removeFact( key(oldElem) , oldElem)) {
				removeAndPropagate(oldElem);
			}
		} catch (EvaluationException e) {
			throw new NodeException(e,this);
		}
	}

	private Key key(WorkingMemoryElement oldElem) throws EvaluationException {
		Key key = new Key(new JamochaValue[distinctionSlots.size()]);
		for(int i=0; i< key.v.length; i++) {
			int slotIdx = distinctionSlots.get(i);
			key.v[i] = oldElem.getFirstFact().getSlotValue(slotIdx);
		}
		return key;
	}

	@Override
	public void accept(final NodeVisitor visitor) {
		visitor.visit(this);
	}

}
