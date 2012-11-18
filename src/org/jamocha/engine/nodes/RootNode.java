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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;
import org.jamocha.engine.util.WeakList;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Template;

/**
 */
public class RootNode extends Node {

	protected class RootNodeInputImpl extends NodeInputImpl {

		private class TemplateToInput {
			private final Map<Template, WeakList<NodeInput>> map = new HashMap<>();

			public void append(final Template template,
					final WeakReference<NodeInput> nodeInput) {
				WeakList<NodeInput> inputs = this.map.get(template);
				if (null == inputs) {
					inputs = new WeakList<>();
					this.map.put(template, inputs);
				}
				inputs.append(nodeInput);
			}

			public List<WeakReference<NodeInput>> get(final Template template) {
				return this.map.get(template).get();
			}
		}

		final TemplateToInput templateToInput = new TemplateToInput();

		public RootNodeInputImpl(
				final WeakReference<? extends Node> shelteringNode,
				final WeakReference<? extends Node> parent) {
			super(shelteringNode, parent);
		}

		private Message[] acceptToken(final Token token) {
			final List<Message> messages = new ArrayList<>();
			final Set<FactTuple> factTuples = token.getFactTuples();
			for (final FactTuple factTuple : factTuples) {
				assert 1 == factTuple.length();
				final Fact fact = factTuple.getFirstFact();
				Template template = fact.getTemplate();
				do {
					final List<WeakReference<NodeInput>> inputs = this.templateToInput
							.get(template);
					for (final WeakReference<NodeInput> input : inputs) {
						messages.add(new Message(input, token));
					}
					template = template.getParentTemplate();
				} while (null != template);
			}
			return messages.toArray(new Message[messages.size()]);
		}

		@Override
		public Message[] acceptPlusToken(final PlusToken token) {
			return acceptToken(token);
		}

		@Override
		public Message[] acceptMinusToken(final MinusToken token) {
			return acceptToken(token);
		}

	}

	final RootNodeInputImpl nodeInput = new RootNodeInputImpl(
			this.weakReference, null);

	public RootNode(final Memory memory) {
		super(memory);
	}

	@Override
	protected void acceptChild(final WeakReference<NodeInput> child) {
		super.acceptChild(child);
		try {
			final ObjectTypeNode otn = (ObjectTypeNode) child.get()
					.getTargetNode().get();
			final Template template = otn.getTemplate();
			this.nodeInput.templateToInput.append(template, child);
		} catch (final ClassCastException e) {
			// child of root node can not be cast to ObjectTypeNode
			// TODO allow?
		}
	}

	/**
	 * Returns the single input of the root node.
	 * 
	 * @return root node input
	 */
	public WeakReference<NodeInput> getNetworkInput() {
		return this.nodeInput.weakReference;
	}

	@Override
	protected NodeInputImpl newNodeInput(
			final WeakReference<? extends Node> parent) {
		throw new UnsupportedOperationException(
				"The root node can only have one single input! "
						+ "This single valid input is created in its ctor "
						+ "and can be accessed via getNetworkInput()");
	}

}
