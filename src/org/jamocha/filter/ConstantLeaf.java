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

import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.Node;

public class ConstantLeaf implements FunctionWithArguments {
	final Object value;
	final SlotType type;

	public ConstantLeaf(final Object value, final SlotType type) {
		super();
		this.value = value;
		this.type = type;
	}

	@Override
	public SlotType[] paramTypes() {
		return SlotType.empty;
	}

	@Override
	public SlotType returnType() {
		return type;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Object evaluate(final Object... params) {
		return value;
	}

	@Override
	public FunctionWithArguments translatePath(
			final PathTranslation translation, final Node childNode) {
		return this;
	}

	/**
	 * @see org.jamocha.filter.Function#accept(org.jamocha.filter.FunctionVisitor)
	 */
	@Override
	public <Proxy> Proxy accept(
			final FunctionWithArgumentsVisitor<Proxy> visitor, final Proxy proxy) {
		return visitor.visit(this, proxy);
	}
}
