/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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
import org.jamocha.application.gui.retevisualisation.nodedrawers.RIANodeDrawer;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 *  A right input adaptor node
 *  converts incoming beta-wmes to alpha-wmes by truncating each
 *  wme to only its first tuple-element
 */
public class RightInputAdaptorNode extends OneInputNode {

	public RightInputAdaptorNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	@Override
	public void addWME(final WorkingMemoryElement elem) throws NodeException {
		if (!isActivated())
			return;
		final WorkingMemoryElement newElem = elem.getFirstFact();
		addAndPropagate(newElem);
	}

	@Override
	public void removeWME(final WorkingMemoryElement elem) throws NodeException {
		final WorkingMemoryElement newElem = elem.getFactTuple();
		removeAndPropagate(newElem);
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new RIANodeDrawer(this);
	}

}
