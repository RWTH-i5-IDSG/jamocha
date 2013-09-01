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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.engine.memory.Template;
import org.jamocha.engine.memory.javaimpl.Fact;

/**
 */
public class RootNode {

	private class TemplateToInput {

		private final Map<Template, List<ObjectTypeNode>> map = new HashMap<>();

		public void add(final Template template, final ObjectTypeNode nodeInput) {
			List<ObjectTypeNode> inputs = this.map.get(template);
			if (null == inputs) {
				inputs = new ArrayList<>();
				this.map.put(template, inputs);
			}
			inputs.add(nodeInput);
		}

		public void remove(final Template template,
				final ObjectTypeNode nodeInput) {
			this.map.get(template).remove(nodeInput);
		}

		public List<ObjectTypeNode> get(final Template template) {
			return this.map.get(template);
		}
	}

	final TemplateToInput templateToInput = new TemplateToInput();

	private interface AssertOrRetractInterface {
		public void call(final ObjectTypeNode otn, final Fact fact);

		static AssertOrRetractInterface assertCall = new AssertOrRetractInterface() {
			@Override
			public void call(final ObjectTypeNode otn, final Fact fact) {
				otn.assertFact(fact);
			}
		};

		static AssertOrRetractInterface retractCall = new AssertOrRetractInterface() {
			@Override
			public void call(final ObjectTypeNode otn, final Fact fact) {
				otn.retractFact(fact);
			}
		};
	}

	private void processFact(final Fact fact,
			final AssertOrRetractInterface methodPointer) {
		Template template = fact.getTemplate();
		do {
			final List<ObjectTypeNode> matchingOTNs = this.templateToInput
					.get(template);
			for (final ObjectTypeNode matchingOTN : matchingOTNs) {
				methodPointer.call(matchingOTN, fact);
			}
			template = template.getParentTemplate();
		} while (null != template);
	}

	public void assertFact(final Fact fact) {
		processFact(fact, AssertOrRetractInterface.assertCall);
	}

	public void retractFact(final Fact fact) {
		processFact(fact, AssertOrRetractInterface.retractCall);
	}

	public void addOTN(final ObjectTypeNode otn) {
		this.templateToInput.add(otn.template, otn);
	}

	public void removeOTN(final ObjectTypeNode otn) {
		this.templateToInput.remove(otn.template, otn);
	}
}
