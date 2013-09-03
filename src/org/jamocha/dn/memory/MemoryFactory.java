/*
 * Copyright 2002-2013 The Jamocha Team
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

package org.jamocha.dn.memory;

import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;

/**
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public interface MemoryFactory {

	/**
	 * Note: PathTransformation should contain entries for all paths given as
	 * their addresses are set here.
	 * 
	 * @param template
	 * @param paths
	 * @return
	 */
	public MemoryHandlerMain newMemoryHandlerMain(final Template template,
			final Path... paths);

	public MemoryHandlerMain newMemoryHandlerMain(
			final Edge... inputsToBeJoined);

	public MemoryHandlerTemp processTokenInBeta(
			MemoryHandlerMain originatingMainHandler, MemoryHandlerTemp token,
			Edge originInput, Filter filter)
			throws CouldNotAcquireLockException;

	public MemoryHandlerTemp processTokenInAlpha(
			MemoryHandlerMain originatingMainHandler, MemoryHandlerTemp token,
			Node alphaNode, Filter filter) throws CouldNotAcquireLockException;

	public MemoryHandlerTemp newToken(MemoryHandlerMain originatingMainHandler,
			Node otn, Fact... facts);
}
