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
package org.jamocha.engine.memory.javaimpl;

import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.Node.Edge;
import org.jamocha.filter.Filter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class MemoryFactory implements org.jamocha.engine.memory.MemoryFactory {

	private final static MemoryFactory singleton = new MemoryFactory();

	public static org.jamocha.engine.memory.MemoryFactory getMemoryFactory() {
		return singleton;
	}

	@Override
	public MemoryHandlerMain newMemoryHandlerMain(
			final Edge... inputsToBeJoined) {
		return new MemoryHandlerMain(inputsToBeJoined);
	}

	@Override
	public MemoryHandlerTemp processTokenInBeta(
			final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerTemp token, final Edge originInput,
			final Filter filter) throws InterruptedException {
		return MemoryHandlerTemp.newBetaTemp(originatingMainHandler, token,
				originInput, filter);
	}

	@Override
	public MemoryHandlerTemp processTokenInAlpha(
			final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerTemp token, final Node alphaNode,
			final Filter filter) throws InterruptedException {
		return MemoryHandlerTemp.newAlphaTemp(originatingMainHandler, token,
				alphaNode, filter);
	}

	@Override
	public MemoryHandlerTemp newToken(
			final MemoryHandlerMain originatingMainHandler, final Node otn,
			final org.jamocha.engine.memory.Fact... facts)
			throws InterruptedException {
		return MemoryHandlerTemp
				.newRootTemp(originatingMainHandler, otn, facts);
	}

}
