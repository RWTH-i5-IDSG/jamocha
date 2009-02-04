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
 * @author Josef Alexander Hahn <mail@josef-hahn.de> A left input adaptor node
 *         converts incoming alpha-wmes to beta-wmes by wrapping them in a
 *         length-1-tuple
 */
public class LeftInputAdaptorNode extends OneInputNode {

	@Deprecated
	public LeftInputAdaptorNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}
	
	public LeftInputAdaptorNode(Engine e) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement elem) throws NodeException {
		if (!isActivated())
			return;
		final WorkingMemoryElement newElem = elem.getFactTuple();
		addAndPropagate(newElem);
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement elem) throws NodeException {
		final WorkingMemoryElement newElem = elem.getFactTuple();
		removeAndPropagate(newElem);
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

}
