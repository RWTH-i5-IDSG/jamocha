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

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class ObjectTypeNode extends AlphaNode {

	protected final Template template;

	public ObjectTypeNode(final MemoryFactory memory, final Template template,
			final Path... paths) {
		super(memory, template, paths);
		this.template = template;
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
		final MemoryHandlerTemp token = memoryFactory.newToken(this.memory,
				this, fact);
		for (final Edge edge : this.children) {
			edge.processPlusToken(token);
		}
	}

	public void retractFact(final Fact fact) {
		throw new UnsupportedOperationException(
				"retraction of facts not implemented yet");
		// TODO
	}
	
	@Override
	public Edge[] getIncomingEdges() {
		throw new UnsupportedOperationException("No incoming edges in an OTN.");
	}
	
	@Override
	public AddressPredecessor delocalizeAddress(FactAddress localFactAddress) {
		throw new UnsupportedOperationException("No previouse addresses for addresses in an OTN.");
	}

}
