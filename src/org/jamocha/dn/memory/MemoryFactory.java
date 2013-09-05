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

package org.jamocha.dn.memory;

import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Path;

/**
 * Interface for the different implementations of the memory component. The network uses this
 * interface to acquire new instances of MemoryHandlers.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see MemoryHandlerMain
 * @see MemoryHandlerTemp
 */
public interface MemoryFactory {

	/**
	 * Creates a new {@link MemoryHandlerMain} capable of storing facts meeting the restrictions of
	 * the template given. For the {@link Path paths} given, all but the {@link Node node}
	 * information are set.
	 * 
	 * @param template
	 *            the template of facts in {@link MemoryHandlerMain}
	 * @param paths
	 *            the paths that need the address in the {@link MemoryHandlerMain}
	 * @return a new {@link MemoryHandlerMain} capable of storing facts of the given
	 *         {@link Template template}
	 * @see MemoryHandlerMain
	 * @see Template
	 * @see Path
	 */
	public MemoryHandlerMain newMemoryHandlerMain(final Template template, final Path... paths);

	/**
	 * Creates a new {@link MemoryHandlerMain} capable of storing facts merged from the {@link Edge
	 * edges} given. The {@link Edge edges} given have their
	 * {@link Edge#setAddressMap(java.util.Map)} called with a map able to localize addresses from
	 * their parent into addresses in the {@link Node node} the {@link MemoryHandlerMain} is for.
	 * 
	 * @param edgesToBeJoined
	 *            {@link Edge edges} producing the facts that will be joined and have to be stored
	 *            in the {@link MemoryHandlerMain} created here
	 * @return a {@link MemoryHandlerMain} capable of storing facts merged from the {@link Edge
	 *         edges} given
	 * @see MemoryHandlerMain
	 * @see Edge
	 */
	public MemoryHandlerMain newMemoryHandlerMain(final Edge... edgesToBeJoined);
}
