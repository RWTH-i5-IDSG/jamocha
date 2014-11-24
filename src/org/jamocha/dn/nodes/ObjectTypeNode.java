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

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;

/**
 * Object-type {@link Node} implementation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ObjectTypeNode extends AlphaNode {

	protected final Template template;
	protected final FactAddress factAddress;

	private ObjectTypeNode(final Network network, final Template template, final Path path) {
		super(network, template, path);
		this.template = template;
		this.factAddress = path.getFactAddressInCurrentlyLowestNode();
	}

	public ObjectTypeNode(final Network network, final Template template) {
		this(network, template, new Path(template));
	}

	/**
	 * returns the template belonging to this node
	 */
	public Template getTemplate() {
		return this.template;
	}

	@Override
	protected Edge newEdge(final Node source) {
		throw new UnsupportedOperationException("ObjectTypeNodes can not have inputs!");
	}

	/**
	 * Creates a new plus token for the given fact and passes it to children.
	 * 
	 * @param fact
	 *            {@link Fact} to be asserted
	 */
	public MemoryFact[] assertFact(final Fact... facts) {
		assert !Arrays.stream(facts).filter(fact -> fact.getTemplate() != this.template).findAny().isPresent();
		final Pair<? extends MemoryHandlerPlusTemp, MemoryFact[]> pair = this.memory.newPlusToken(this, facts);
		for (final Edge edge : this.outgoingEdges) {
			edge.enqueueMemory(pair.getLeft());
		}
		return pair.getRight();
	}

	/**
	 * Creates a new minus token for the given fact and passes it to children.
	 * 
	 * @param fact
	 *            {@link Fact} to be retracted
	 */
	public void retractFact(final MemoryFact... facts) {
		assert !Arrays.stream(facts).filter(fact -> fact.getTemplate() != this.template).findAny().isPresent();
		final MemoryHandlerMinusTemp mem = this.memory.newMinusToken(facts);
		for (final Edge edge : this.outgoingEdges) {
			edge.enqueueMemory(mem);
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
	public void shareNode(final Map<Path, FactAddress> map, final Path... paths) {
		for (final Path path : paths) {
			path.setCurrentlyLowestNode(this);
			path.setFactAddressInCurrentlyLowestNode(this.factAddress);
		}
	}
	
	@Override
	public <V extends NodeVisitor> V accept(V visitor) {
		visitor.visit(this);
		return visitor;
	}

}
