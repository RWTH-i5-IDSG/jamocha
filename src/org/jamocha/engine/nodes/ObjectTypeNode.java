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

package org.jamocha.engine.nodes;

import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.memory.javaimpl.Fact;

public class ObjectTypeNode extends AlphaNode {

	protected final Template template;

	public ObjectTypeNode(final MemoryFactory memory, final Template template) {
		super(memory);
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
		// TODO
	}

	public void retractFact(final Fact fact) {
		// TODO
	}

}
