/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.dn.memory.javaimpl;

import java.util.Map;
import java.util.Set;

import org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;

/**
 * Implementation of the {@link org.jamocha.dn.memory.MemoryFactory} interface. Supplies a singleton
 * instance via {@link MemoryFactory#getMemoryFactory()}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryFactory
 */
public class MemoryFactory implements org.jamocha.dn.memory.MemoryFactory {

	private final static MemoryFactory singleton = new MemoryFactory();

	/**
	 * Retrieves a singleton instance of the {@link org.jamocha.dn.memory.MemoryFactory}
	 * implementation.
	 * 
	 * @return a singleton instance of the {@link org.jamocha.dn.memory.MemoryFactory}
	 *         implementation
	 */
	public static org.jamocha.dn.memory.MemoryFactory getMemoryFactory() {
		return singleton;
	}

	@Override
	public Template newTemplate(String description, Slot... slots) {
		return new org.jamocha.dn.memory.javaimpl.Template(description, slots);
	}

	private MemoryFactory() {
	}

	@Override
	public MemoryHandlerMain newMemoryHandlerMain(final Template template, final Path... paths) {
		return new MemoryHandlerMain(template, paths);
	}

	@Override
	public MemoryHandlerMainAndCounterColumnMatcher newMemoryHandlerMain(final PathFilter filter,
			final Map<Edge, Set<Path>> edgesAndPaths) {
		return MemoryHandlerMain.newMemoryHandlerMain(filter, edgesAndPaths);
	}

}
