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

import java.lang.ref.WeakReference;
import java.util.Set;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;

/**
 */
public class RootNode extends Node {

	protected class RootNodeInputImpl extends NodeInputImpl {

		public RootNodeInputImpl(final WeakReference<Node> shelteringNode,
				final WeakReference<Node> parent) {
			super(shelteringNode, parent);
		}

		@Override
		public Message[] acceptPlusToken(final PlusToken token) {
			final Set<NodeInput> nodeInputs = this.shelteringNode.get()
					.getChildren();
			final Message[] messages = new Message[nodeInputs.size()];
			int index = 0;
			for (final NodeInput nodeInput : nodeInputs) {
				messages[index] = new Message(nodeInput.getWeakReference(),
						token);
				++index;
			}
			return messages;
		}

		@Override
		public Message[] acceptMinusToken(final MinusToken token) {
			final Set<NodeInput> nodeInputs = this.shelteringNode.get()
					.getChildren();
			final Message[] messages = new Message[nodeInputs.size()];
			int index = 0;
			for (final NodeInput nodeInput : nodeInputs) {
				messages[index] = new Message(nodeInput.getWeakReference(),
						token);
				++index;
			}
			return messages;
		}

	}

	public RootNode(final Memory memory) {
		super(memory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected NodeInputImpl newNodeInput(final WeakReference<Node> parent) {
		return new RootNodeInputImpl(this.weakReference, parent);
	}
}
