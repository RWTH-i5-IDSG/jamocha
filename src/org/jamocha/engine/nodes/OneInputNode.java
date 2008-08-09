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
 * @author Josef Alexander Hahn <mail@josef-hahn.de> an abstract class for
 *         nodes, which has only one (alpha-) input
 */
public abstract class OneInputNode extends Node {

	protected Node alphaInput;

	public OneInputNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
		alphaInput = null;
	}

	@Override
	public Node[] getParentNodes() {
		final Node[] empty = {};
		if (alphaInput == null)
			return empty;
		final Node[] result = { alphaInput };
		return result;
	}

	/**
	 * returns the alpha input node
	 */
	public Node getAlphaInput() {
		return alphaInput;
	}

	protected void plugAlphaParent(final Node node)
			throws InvalidOperationException, NodeException {
		if (alphaInput != null)
			throw new InvalidOperationException(
					"there already is an alpha input");
		alphaInput = node;
		node.addChild(this);
	}

	@Override
	protected Node registerParent(final Node n) throws NodeException {
		alphaInput = n;
		return this;
	}

	/**
	 * returns the alpha input wmes
	 */
	protected Iterable<WorkingMemoryElement> alpha() {
		return getAlphaInput().memory();
	}

	@Override
	protected void unbindFromParents() {
		alphaInput = null;
	}
	
}
