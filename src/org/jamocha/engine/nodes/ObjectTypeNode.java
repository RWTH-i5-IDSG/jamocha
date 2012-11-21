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

import java.util.Set;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;
import org.jamocha.engine.workingmemory.elements.Template;
import org.junit.Ignore;

public class ObjectTypeNode extends AlphaNode {

	protected class ObjectTypeNodeInputImpl extends AlphaNodeInputImpl {

		public ObjectTypeNodeInputImpl(final Node sourceNode,
				final Node targetNode) {
			super(sourceNode, targetNode);
		}

		/**
		 * Method only needed if the RootNode does not skip the OTNs directly
		 * 
		 * @param token
		 * @return
		 */
		@Ignore("unused")
		private Message[] acceptToken(final Token token) {
			final Set<NodeInput> children = this.targetNode.children;
			final Message[] messages = new Message[children.size()];
			int i = 0;
			for (final NodeInput child : children) {
				messages[i++] = new Message(child, token);
			}
			return messages;
		}

		@Override
		public Message[] acceptMinusToken(final MinusToken token) {
			throw new UnsupportedOperationException(
					"The RootNode has to accept the Tokens for OTNs!");
		}

		@Override
		public Message[] acceptPlusToken(final PlusToken token) {
			throw new UnsupportedOperationException(
					"The RootNode has to accept the Tokens for OTNs!");
		}

	}

	protected final Template template;

	public ObjectTypeNode(final Memory memory, final Template template) {
		super(memory);
		this.template = template;
	}

	/**
	 * returns the template belonging to this node
	 */
	public Template getTemplate() {
		return template;
	}

	@Override
	protected NodeInputImpl newNodeInput(final Node source) {
		return new ObjectTypeNodeInputImpl(source, this);
	}

}
