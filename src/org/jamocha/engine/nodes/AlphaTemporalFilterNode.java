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

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.TemporalNodeDrawer;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.TemporalFactThread;
import org.jamocha.engine.TemporalValidity.EventPoint;
import org.jamocha.engine.TemporalValidity.EventPoint.Type;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Fact;

public class AlphaTemporalFilterNode extends OneInputNode {

	private class AlphaTemporalFilterThread extends TemporalFactThread {

		public AlphaTemporalFilterThread(Engine e) {
			super(e);
		}
		
		protected void handle(EventPoint nextEventPoint) {
			engine.setLag(threadLag, this);
			Fact f = eventPoint2Fact.get(nextEventPoint);
			if (nextEventPoint.getType() == Type.START) {
				try {
					addAndPropagate(f);
				} catch (NodeException e) {
					notifyForException(e);
				}
			} else {
				try {
					removeAndPropagate(f);
				} catch (NodeException e) {
					notifyForException(e);
				}
			}
		}
	}
	
	protected AlphaTemporalFilterThread thread;

	@Deprecated
	private AlphaTemporalFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	public AlphaTemporalFilterNode(Engine e) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
		thread = new AlphaTemporalFilterThread(e);
		thread.setName("Alpha Temporal Thread for Node "+nodeId);
		//thread.start();
	}
	
	@Override
	protected void afterActivationHook() {
		System.out.println("started fred");
		thread.start();
	}
	
	@Override
	protected void propagateAddition(WorkingMemoryElement elem)	throws NodeException {
		synchronized (RootNode.class) {
			super.propagateAddition(elem);
		}
	}

	@Override
	protected void propagateRemoval(WorkingMemoryElement elem)	throws NodeException {
		synchronized (RootNode.class) {
			super.propagateRemoval(elem);
		}
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		if (newElem.getFirstFact().getTemporalValidity()==null) {
			addAndPropagate(newElem);
		} else 
			thread.insertFact(newElem.getFirstFact());
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
		if (oldElem.getFirstFact().getTemporalValidity()==null) {
			removeAndPropagate(oldElem);
		} else
			thread.removeFact(oldElem.getFirstFact());
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new TemporalNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
	}

}
