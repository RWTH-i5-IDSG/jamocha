/*
 * Copyright 2002-2012 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jamocha.dn.nodes;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ObjectTypeNode extends AlphaNode {

	protected final Template template;
	protected final FactAddress factAddress;

	public ObjectTypeNode(final Network network, final Path... paths) {
		super(network, getTemplateFromPaths(paths), paths);
		this.template = this.memory.getTemplate()[0];
		this.factAddress = paths[0].getFactAddressInCurrentlyLowestNode();
		for (final Path path : paths) {
			path.setCurrentlyLowestNode(this);
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
		return this.template;
	}

	@Override
	protected PositiveEdge newPositiveEdge(final Node source) {
		throw new UnsupportedOperationException("ObjectTypeNodes can not have inputs!");
	}

	public void assertFact(final Fact fact) {
		final MemoryHandlerPlusTemp mem = this.memory.newPlusToken(this, fact);
		for (final Edge edge : this.outgoingPositiveEdges) {
			edge.enqueuePlusMemory(mem);
		}
	}

	public void retractFact(final Fact fact) {
		final MemoryHandlerMinusTemp mem = this.memory.newMinusToken(fact);
		for (final Edge edge : this.outgoingPositiveEdges) {
			edge.enqueueMinusMemory(mem);
		}
	}

	@Override
	public Edge[] getIncomingEdges() {
		throw new UnsupportedOperationException("No incoming edges in an OTN.");
	}

	@Override
	public AddressPredecessor delocalizeAddress(final FactAddress localFactAddress) {
		throw new UnsupportedOperationException("No previous addresses for addresses in an OTN.");
	}

	@Override
	public void shareNode(final Path... paths) {
		assert null != paths;
		for (final Path path : paths) {
			path.setCurrentlyLowestNode(this);
			path.setFactAddressInCurrentlyLowestNode(this.factAddress);
		}
	}

}
