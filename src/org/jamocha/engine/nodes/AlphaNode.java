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

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;

public class AlphaNode extends Node {

	protected class AlphaNodeInputImpl extends NodeInputImpl {
		
		public AlphaNodeInputImpl(final Node sourceNode, final Node targetNode) {
			super(sourceNode, targetNode);
			// TODO Auto-generated constructor stub
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

		@Override
		public FactAddress getAddress(FactAddress add) {
			throw new UnsupportedOperationException("The Input of an AlphaNode is not supposed to be used as an address");
		}

	}

	public AlphaNode(final Memory memory) {
		super(memory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected NodeInputImpl newNodeInput(final Node source) {
		// TODO Auto-generated method stub
		return new AlphaNodeInputImpl(source, this);
	}

}
