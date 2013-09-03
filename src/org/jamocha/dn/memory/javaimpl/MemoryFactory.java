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
package org.jamocha.dn.memory.javaimpl;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class MemoryFactory implements org.jamocha.dn.memory.MemoryFactory {

	private final static MemoryFactory singleton = new MemoryFactory();

	public static org.jamocha.dn.memory.MemoryFactory getMemoryFactory() {
		return singleton;
	}

	private MemoryFactory() {
	}

	/**
	 * @see MemoryFactory#newMemoryHandlerMain(Template, Path...)
	 */
	@Override
	public MemoryHandlerMain newMemoryHandlerMain(final Template template,
			final Path... paths) {
		return new MemoryHandlerMain(template, paths);
	}

	/**
	 * @see MemoryFactory#newMemoryHandlerMain(Edge...)
	 */
	@Override
	public MemoryHandlerMain newMemoryHandlerMain(
			final Edge... inputsToBeJoined) {
		return new MemoryHandlerMain(inputsToBeJoined);
	}

	/**
	 * @see MemoryFactory#processTokenInBeta(MemoryHandlerMain,
	 *      MemoryHandlerTemp, Edge, Filter)
	 */
	@Override
	public MemoryHandlerTemp processTokenInBeta(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final org.jamocha.dn.memory.MemoryHandlerTemp token,
			final Edge originInput, final Filter filter)
			throws CouldNotAcquireLockException {
		return MemoryHandlerTemp.newBetaTemp(
				(MemoryHandlerMain) originatingMainHandler,
				(MemoryHandlerTemp) token, originInput, filter);
	}

	/**
	 * @see MemoryFactory#processTokenInAlpha(MemoryHandlerMain,
	 *      MemoryHandlerTemp, Node, Filter)
	 */
	@Override
	public MemoryHandlerTemp processTokenInAlpha(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final org.jamocha.dn.memory.MemoryHandlerTemp token,
			final Edge originInput, final Filter filter)
			throws CouldNotAcquireLockException {
		return MemoryHandlerTemp.newAlphaTemp(
				(MemoryHandlerMain) originatingMainHandler,
				(MemoryHandlerTemp) token, originInput, filter);
	}

	/**
	 * @see MemoryFactory#newToken(MemoryHandlerMain, Node,
	 *      org.jamocha.dn.memory.Fact...)
	 */
	@Override
	public MemoryHandlerTemp newToken(
			final org.jamocha.dn.memory.MemoryHandlerMain originatingMainHandler,
			final Node otn, final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerTemp.newRootTemp(
				(MemoryHandlerMain) originatingMainHandler, otn, facts);
	}

}
