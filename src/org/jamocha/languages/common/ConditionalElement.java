/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.languages.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class ConditionalElement {

	final List<ConditionalElement> children;

	public static class ExistentialConditionalElement extends ConditionalElement {
		@Getter
		final List<SingleVariable> variables;

		public ExistentialConditionalElement(final List<ConditionalElement> children,
				final List<SingleVariable> variables) {
			super(children);
			this.variables = variables;
		}
	}

	public static class NegatedExistentialConditionalElement extends ConditionalElement {
		@Getter
		final List<SingleVariable> variables;

		public NegatedExistentialConditionalElement(final List<ConditionalElement> children,
				final List<SingleVariable> variables) {
			super(children);
			this.variables = variables;
		}
	}

	public static class TestConditionalElement extends ConditionalElement {
		@Getter
		final FunctionCall functionCall;

		public TestConditionalElement(final FunctionCall functionCall) {
			super(new ArrayList<>());
			this.functionCall = functionCall;
		}
	}

	public static class OrFunctionConditionalElement extends ConditionalElement {
		public OrFunctionConditionalElement(final List<ConditionalElement> children) {
			super(children);
		}
	}

	public static class AndFunctionConditionalElement extends ConditionalElement {
		public AndFunctionConditionalElement(final List<ConditionalElement> children) {
			super(children);
		}
	}

	public static class NotFunctionConditionalElement extends ConditionalElement {
		public NotFunctionConditionalElement(final List<ConditionalElement> children) {
			super(children);
		}
	}

	/**
	 * This class is inserted into a {@link RuleCondition} iff there are no variable bindings in the
	 * {@link RuleCondition}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public static class InitialFactConditionalElement extends ConditionalElement {
		public InitialFactConditionalElement() {
			super(new ArrayList<>(0));
		}
	}
}
