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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Template;

/**
 */
public class RootNode extends Node {

	protected class RootNodeInputImpl extends NodeInputImpl {

		private class TemplateToInput {

			private final Map<Template, List<NodeInput>> map = new HashMap<>();

			public void add(final Template template,
					final NodeInput nodeInput) {
				List<NodeInput> inputs = this.map.get(template);
				if (null == inputs) {
					inputs = new ArrayList<>();
					this.map.put(template, inputs);
				}
				inputs.add(nodeInput);
			}
			
			public void remove(final Template template, final NodeInput nodeInput) {
				this.map.get(template).remove(nodeInput);
			}
			
			public List<NodeInput> get(final Template template) {
				return this.map.get(template);
			}
		}

		final TemplateToInput templateToInput = new TemplateToInput();

		public RootNodeInputImpl(final Node sourceNode, final Node targetNode) {
			super(sourceNode, targetNode);
		}

		private Message[] acceptToken(final Token token) {
			final List<Message> messages = new ArrayList<>();
			final Set<FactTuple> factTuples = token.getFactTuples();
			for (final FactTuple factTuple : factTuples) {
				assert 1 == factTuple.length();// TODO error
				final Fact fact = factTuple.getFirstFact();
				Template template = fact.getTemplate();
				do {
					final List<NodeInput> inputs = this.templateToInput
							.get(template);
					for (final NodeInput input : inputs) {
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

		@Override
		public FactAddress getAddress(FactAddress add) {
			throw new UnsupportedOperationException("The Input of the RootNode is not supposed to be used as an address");
		}
	}

	final RootNodeInputImpl nodeInput = new RootNodeInputImpl(
			this, null);

	public RootNode(final Memory memory) {
		super(memory);
	}

	@Override
	protected void acceptChild(final NodeInput child) {
		super.acceptChild(child);
		try {
			final ObjectTypeNode otn = (ObjectTypeNode) child
					.getTargetNode();
			final Template template = otn.getTemplate();
			this.nodeInput.templateToInput.add(template, child);
		} catch (final ClassCastException e) {
			throw new Error("Only ObjectTypeNodes are supposed to be connected to the RootNode.");
		}
	}
	
	protected void removeChild(final NodeInput child) {
		super.removeChild(child);
		try {
			final ObjectTypeNode otn = (ObjectTypeNode) child
					.getTargetNode();
			final Template template = otn.getTemplate();
			this.nodeInput.templateToInput.remove(template, child);
		} catch (final ClassCastException e) {
			throw new Error("Only ObjectTypeNodes are supposed to be connected to the RootNode.");
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
	protected NodeInputImpl newNodeInput(final Node source) {
		throw new UnsupportedOperationException(
				"The root node can only have one single input! "
						+ "This single valid input is created in its ctor "
						+ "and can be accessed via getNetworkInput()");
	}

}
