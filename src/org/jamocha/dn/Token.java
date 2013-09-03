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
package org.jamocha.dn;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.dn.nodes.Node.Edge;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor
public abstract class Token implements Runnable {
	final MemoryHandlerTemp temp;
	final Edge edge;

	public static class PlusToken extends Token {
		public PlusToken(final MemoryHandlerTemp temp, final Edge edge) {
			super(temp, edge);
		}

		@Override
		public void run() {
			this.edge.processPlusToken(temp);
		}
	}

	public static class MinusToken extends Token {
		public MinusToken(final MemoryHandlerTemp temp, final Edge edge) {
			super(temp, edge);
		}

		@Override
		public void run() {
			this.edge.processMinusToken(temp);
		}
	}
}
