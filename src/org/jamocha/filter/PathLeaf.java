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
import org.jamocha.engine.nodes.NetworkFactAddress;
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
		return SlotType.empty;
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

	public static class AddressLeaf implements FunctionWithArguments {
		final NetworkFactAddress addr;
		final SlotAddress slotAddr;

		public AddressLeaf(final NetworkFactAddress addr,
				final SlotAddress slotAddr) {
			super();
			this.addr = addr;
			this.slotAddr = slotAddr;
		}

		public NetworkFactAddress getNetworkFactAddress() {
			return addr;
		}

		public SlotAddress getSlotAddress() {
			return slotAddr;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}

		@Override
		public SlotType[] paramTypes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SlotType returnType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object evaluate(Object... params) {
			// TODO Auto-generated method stub
			return null;
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
				final FunctionWithArgumentsVisitor<Proxy> visitor,
				final Proxy proxy) {
			return visitor.visit(this, proxy);
		}

	}

	@Override
	public AddressLeaf translatePath(final PathTranslation translation,
			final Node childNode) {
		// TODO impl Christoph's algorithm
		return null;
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
