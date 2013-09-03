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

import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.Template;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 */
public abstract class AlphaNode extends Node {

	protected abstract class AlphaEdgeImpl extends EdgeImpl {

		public AlphaEdgeImpl(final Node sourceNode, final Node targetNode) {
			super(sourceNode, targetNode);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void processPlusToken(final MemoryHandler memory) {
			// TODO Auto-generated method stub
		}

		@Override
		public void processMinusToken(final MemoryHandler memory) {
			// TODO Auto-generated method stub
		}

		@Override
		public FactAddress localizeAddress(final FactAddress addressInParent) {
			throw new UnsupportedOperationException(
					"The Input of an AlphaNode is not supposed to be used as an address");
		}

	}

	public AlphaNode(final MemoryFactory memory, final Template template,
			final Path... paths) {
		super(memory, template, paths);
	}

	public AlphaNode(final MemoryFactory memoryFactory, final Filter filter) {
		super(memoryFactory, filter);
		// TODO Auto-generated constructor stub
	}

}
