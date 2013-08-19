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
package org.jamocha.filter;

import org.jamocha.engine.memory.SlotAddress;
import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.Node;

/**
 * @author Fabian Ohler
 * 
 */
public class PathLeaf implements FunctionWithArguments {

	final Path path;
	final SlotAddress slot;

	public PathLeaf(final Path path, final SlotAddress slot) {
		super();
		this.path = path;
		this.slot = slot;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(path.toString());
		sb.append(" [);");
		sb.append(slot.toString());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public SlotType[] paramTypes() {
		return new SlotType[] { slot.getSlotType(path.template) };
	}

	@Override
	public SlotType returnType() {
		return slot.getSlotType(path.template);
	}

	@Override
	public Object evaluate(Object... params) {
		throw new UnsupportedOperationException(
				"Evaluate not allowed for PathLeafs!");
	}

	public static class ParameterLeaf implements FunctionWithArguments {
		final SlotType type;

		public ParameterLeaf(final SlotType type) {
			super();
			this.type = type;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(type);
			sb.append("]");
			return sb.toString();
		}

		@Override
		public SlotType[] paramTypes() {
			return new SlotType[] { this.type };
		}

		@Override
		public SlotType returnType() {
			return this.type;
		}

		@Override
		public Object evaluate(Object... params) {
			return params[0];
		}

		@Override
		public FunctionWithArguments translatePath(
				final PathTranslation translation, final Node childNode) {
			return this;
		}

	}

	@Override
	public ParameterLeaf translatePath(final PathTranslation translation,
			final Node childNode) {
		// TODO impl Christoph's algorithm
		return new ParameterLeaf(returnType());
	}

}
