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

import java.util.Arrays;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;

public class BetaNode extends Node {

	protected class BetaNodeInputImpl extends NodeInputImpl {

		protected int startIndex;
		protected FactAddress[] localAddresses;
		protected final FactAddress alphaToBetaAddress = new FactAddress(
				this.getTargetNode(), this.startIndex);

		public BetaNodeInputImpl(final Node sourceNode, final Node targetNode,
				final int startIndex, final int numberOfFactTuples) {
			super(sourceNode, targetNode);
			this.startIndex = startIndex;
			this.localAddresses = new FactAddress[numberOfFactTuples];
			for (int index = 0; index < numberOfFactTuples; ++index) {
				this.localAddresses[index] = new FactAddress(targetNode,
						startIndex + index);
			}
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
		public FactAddress localizeAddress(final FactAddress addressInParent) {
			if (addressInParent == null) {
				return this.alphaToBetaAddress;
			}
			if (addressInParent.localNode != this.sourceNode) {
				throw new IllegalArgumentException(
						"FactAdress objects can only localize fact addresses local to their source nodes!");
			}
			try {
				return this.localAddresses[addressInParent.localIndex
						- this.startIndex];
			} catch (final IndexOutOfBoundsException e) {
				// new Input added to sourceNode
				final FactAddress localizedAddress = new FactAddress(
						targetNode, ++targetNode.factTupleCardinality);
				final int previousLength = this.localAddresses.length;
				this.localAddresses = Arrays.copyOf(this.localAddresses,
						previousLength + 1);
				this.localAddresses[previousLength] = localizedAddress;
				return localizedAddress;
			}
		}
	}

	public BetaNode(final Memory memory) {
		super(memory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected NodeInputImpl newNodeInput(final Node source) {
		final int startIndex = this.factTupleCardinality;
		this.factTupleCardinality += source.factTupleCardinality;
		return new BetaNodeInputImpl(source, this, startIndex,
				source.factTupleCardinality);
	}
}
