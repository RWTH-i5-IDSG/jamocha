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
package org.jamocha.dn;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;

/**
 * This class contains a {@link MemoryHandlerPlusTemp} and the {@linkplain Edge edge} it has to be
 * processed by. The processing is triggered by a {@link #run()} call.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor
public abstract class Token {
	final MemoryHandlerTemp temp;
	final Edge edge;

	/**
	 * Triggers the {@linkplain Edge edge} to process the contained {@link MemoryHandlerPlusTemp}.
	 * 
	 * @throws CouldNotAcquireLockException
	 *             iff a required lock could not be acquired
	 */
	public abstract void run() throws CouldNotAcquireLockException;

	/**
	 * {@link Token} containing a {@link MemoryHandlerPlusTemp} which should be added to the
	 * {@link Node node}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	public static class PlusToken extends Token {
		public PlusToken(final MemoryHandlerTemp temp, final Edge edge) {
			super(temp, edge);
		}

		@Override
		public void run() throws CouldNotAcquireLockException {
			this.edge.processPlusToken(this.temp);
			final MemoryHandlerTemp mem = this.temp.releaseLock();
			if (mem == null)
				return;
			final Node sourceNode = this.edge.getSourceNode();
			for (Edge e : sourceNode.getOutgoingEdges()) {
				mem.enqueueInEdge(e);
			}
		}
	}

	/**
	 * {@link Token} containing a {@link MemoryHandlerPlusTemp} which should be removed from the
	 * {@link Node node}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	public static class MinusToken extends Token {
		public MinusToken(final MemoryHandlerTemp temp, final Edge edge) {
			super(temp, edge);
		}

		@Override
		public void run() throws CouldNotAcquireLockException {
			this.edge.processMinusToken(this.temp);
			this.temp.releaseLock();
		}
	}
}
