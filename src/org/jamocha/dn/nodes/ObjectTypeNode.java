/*
 * Copyright 2002-2012 The Jamocha Team
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

package org.jamocha.dn.nodes;

import org.jamocha.dn.Network;
import org.jamocha.dn.Token;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class ObjectTypeNode extends AlphaNode {

	protected final Template template;

	public ObjectTypeNode(final Network network, final Template template) {
		super(network, template);
		this.template = template;
	}

	public ObjectTypeNode(final Network network, final Path... paths) {
		super(network, getTemplateFromPaths(paths));
		this.template = this.memory.getTemplate()[0];
		for (final Path path : paths) {
			PathTransformation.setCurrentlyLowestNode(path, this);
		}
	}

	private static Template getTemplateFromPaths(final Path[] paths) {
		if (paths.length < 1)
			throw new Error(
					"ObjectTypeNode constructor must be called with Template or at least one Path");
		return paths[0].getTemplate();
	}

	/**
	 * returns the template belonging to this node
	 */
	public Template getTemplate() {
		return template;
	}

	@Override
	protected EdgeImpl newEdge(final Node source) {
		throw new UnsupportedOperationException(
				"ObjectTypeNodes can not have inputs!");
	}

	public void assertFact(final Fact fact) {
		final MemoryHandlerTemp mem = this.network.getMemoryFactory().newToken(
				this.memory, this, fact);
		for (final Edge edge : this.outgoingEdges) {
			edge.getTargetNode().enqueue(new Token.PlusToken(mem, edge));
		}
	}

	public void retractFact(final Fact fact) {
		throw new UnsupportedOperationException(
				"retraction of facts not implemented yet");
		// TODO retract Fact
	}

	@Override
	public Edge[] getIncomingEdges() {
		throw new UnsupportedOperationException("No incoming edges in an OTN.");
	}

	@Override
	public AddressPredecessor delocalizeAddress(FactAddress localFactAddress) {
		throw new UnsupportedOperationException(
				"No previous addresses for addresses in an OTN.");
	}

}
