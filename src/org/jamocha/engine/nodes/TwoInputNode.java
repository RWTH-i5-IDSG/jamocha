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
 * @author Josef Alexander Hahn <mail@josef-hahn.de> an abstract node with two
 *         inputs: an alpha- and a beta- input. it is a subclass from
 *         OneInputNode, since a node with two inputs can be seen as a node with
 *         one input and an additional one ;)
 */
public abstract class TwoInputNode extends OneInputNode {

	protected Node betaInput;

	public TwoInputNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
		betaInput = null;
	}

	@Override
	public Node[] getParentNodes() {
		if (alphaInput == null && betaInput == null) {
			final Node[] result = {};
			return result;
		} else if (alphaInput == null) {
			final Node[] result = { betaInput };
			return result;
		} else if (betaInput == null) {
			final Node[] result = { alphaInput };
			return result;
		} else {
			final Node[] result = { alphaInput, betaInput };
			return result;
		}
	}

	/**
	 * returns the beta input node
	 */
	public Node getBetaInput() {
		return betaInput;
	}

	protected void plugBetaParent(final Node node)
			throws InvalidOperationException, NodeException {
		if (betaInput != null)
			throw new InvalidOperationException("there already is a beta input");
		betaInput = node;
		node.addChild(this);
	}

	@Override
	protected Node registerParent(final Node n) throws NodeException {
		if (n.outputsBeta()) {
			betaInput = n;
			return this;
		} else // maybe, we need an adaptor node
		if (alphaInput == null) {
			alphaInput = n;
			return this;
		} else {
			final LeftInputAdaptorNode lia = new LeftInputAdaptorNode(net.getEngine());
			lia.addChild(this);
			lia.registerParent(n);
			betaInput = lia;
			return lia;
		}
	}

	/**
	 * returns the beta input wmes
	 */
	protected Iterable<WorkingMemoryElement> beta() {
		if (getBetaInput() != null)
			return getBetaInput().memory();
		return null;
	}
	
	@Override
	protected void unbindFromParents() {
		super.unbindFromParents();
		betaInput = null;
	}

}
