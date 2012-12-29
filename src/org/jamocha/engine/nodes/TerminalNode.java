/*
 * Copyright 2002-2008 The Jamocha Team
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

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;
import org.jamocha.rules.Rule;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> the sink in our rete
 *         network. each fact-tuple, which arrives here, adds a new entry in the
 *         agenda.
 */
public class TerminalNode extends BetaNode {

	protected class TerminalNodeInputImpl extends BetaNodeInputImpl {

		public TerminalNodeInputImpl(final Node sourceNode,
				final Node targetNode, final int startIndex,
				final int numberOfFactTuples) {
			super(sourceNode, targetNode, startIndex, numberOfFactTuples);
		}

		@Override
		public Message[] acceptPlusToken(final PlusToken token) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Message[] acceptMinusToken(final MinusToken token) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private final Rule rule;

	private boolean autoFocus;

	public TerminalNode(final Memory memory, final Rule rule) {
		super(memory);
		this.rule = rule;
		// TODO Auto-generated constructor stub
	}

	public Rule getRule() {
		return rule;
	}

	public void autoFocus() {
		autoFocus = true;
	}

	@Override
	protected TerminalNodeInputImpl newBetaNodeInput(Node sourceNode,
			Node targetNode, int startIndex, int numberOfFactTuples) {
		return new TerminalNodeInputImpl(sourceNode, targetNode, startIndex,
				numberOfFactTuples);
	}

}
