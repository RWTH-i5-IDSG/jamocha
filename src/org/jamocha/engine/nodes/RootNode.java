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

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> the root in our rete
 *         network. here, all facts will come into play and will be propagated
 *         to the child nodes. so, here, no filter logic is implemented.
 */
public class RootNode extends Node {

	@Deprecated
	public RootNode(final int id, final WorkingMemory memory, final ReteNet net) {
		super(id, memory, net);
	}
	
	public RootNode(Engine e) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
	}

	int drin=0;
	
	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		// the root note must not ignore new WMEs while deactivated!
		synchronized (RootNode.class) {
			try {
				addAndPropagate(newElem);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

	}

	@Override
	public Node[] getParentNodes() {
		final Node[] empty = {};
		return empty;
	}
	
	@Override
	public void removeWME(Node sender, final WorkingMemoryElement oldElem) throws NodeException {
		synchronized (RootNode.class) {
			try {
				removeAndPropagate(oldElem);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

	@Override
	public Node registerParent(final Node n) {
		return this;
		// do nothing here, because a RootNode never can become a parent
	}


	@Override
	protected void unbindFromParents() {
		// the root node has no parents!
	}

	@Override
	public void accept(final NodeVisitor visitor) {
		visitor.visit(this);
	}
}
