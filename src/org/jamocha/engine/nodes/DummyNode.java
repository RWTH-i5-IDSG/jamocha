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

import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

/**
 * This is a dummy node, which only should be used for debugging
 * purposes and for test cases.
 * @author Josef Alexander Hahn <mail@josef-hahn.de> 
 */
public class DummyNode extends OneInputNode {

	public DummyNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement elem) throws NodeException {
		if (!isActivated())
			return;
		addAndPropagate(elem);
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement elem) throws NodeException {
		removeAndPropagate(elem);
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

}
