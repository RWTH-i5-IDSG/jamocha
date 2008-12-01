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
import org.jamocha.engine.TemporalThread;
import org.jamocha.engine.TemporalValidity;
import org.jamocha.engine.TemporalValidity.EventPoint;
import org.jamocha.engine.nodes.joinfilter.GeneralizedJoinFilter;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

public class BetaTemporalFilterNode extends AbstractBetaFilterNode {

	protected class BetaTemporalThread extends TemporalThread {

		TemporalValidity tv;
		
		public BetaTemporalThread(Engine e, TemporalValidity tv) {
			super(e);
			this.tv=tv;
			EventPoint ep = tv.getNextEvent(now());
			eventPoints.add(ep);
		}

		@Override
		protected void handle(EventPoint nextEventPoint) {
			try{
				if (nextEventPoint.getType().equals(EventPoint.Type.START)) {
					temporalStart();
				} else {
					temporalStop();
				}
			} catch (NodeException e) {
				notifyForException(e);
			}
		}

		@Override
		protected void skipToNextEventPoint(EventPoint actEventPoint) {
			EventPoint newEP = tv.getNextEvent(actEventPoint.getTimestamp()+1l);
			eventPoints.add(newEP);
		}
		
	}
	
	protected BetaTemporalThread thread;
	
	@Deprecated
	private BetaTemporalFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	public BetaTemporalFilterNode(Engine e, TemporalValidity tv) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
		thread = new BetaTemporalThread(e,tv);
		thread.start();
	}

	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
	
	}

	

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new TemporalNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|filters:");
		for (final GeneralizedJoinFilter f : getFilters())
			sb.append(f.toPPString() + " & ");
	}
	
	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
	
	}
	
	protected void temporalStart() throws NodeException {
		Node parent = getParentNodes()[0];
		for (WorkingMemoryElement wme : workingMemory.getMemory(parent)) {
			addAndPropagate(wme);
		}
	}
	
	protected void temporalStop() throws NodeException {
		Node parent = getParentNodes()[0];
		for (WorkingMemoryElement wme : workingMemory.getMemory(parent) ){
			removeAndPropagate(wme);
		}
	}

}
