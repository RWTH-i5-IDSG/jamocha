/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.filter;

import java.util.ArrayList;
import java.util.Collection;

import lombok.EqualsAndHashCode;

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter.FilterElement;

/**
 * A parameter of a {@link Function} may be a slot of a {@link Fact}. The corresponding
 * {@link SlotAddress} and {@link Path} are stored in this class. As soon as the {@link Node}
 * representing the surrounding {@link Filter} has been created, the {@link Filter} is
 * {@link Filter#translatePath() translated} and all {@link PathLeaf PathLeafs} are replaced with
 * {@link ParameterLeaf ParameterLeafs}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Path
 * @see SlotAddress
 * @see Node
 */
@EqualsAndHashCode
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
	public SlotType[] getParamTypes() {
		return new SlotType[] { slot.getSlotType(path.template) };
	}

	@Override
	public SlotType getReturnType() {
		return slot.getSlotType(path.template);
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		throw new UnsupportedOperationException("Evaluate not allowed for PathLeafs!");
	}

	@Override
	public Object evaluate(final Object... params) {
		throw new UnsupportedOperationException("Evaluate not allowed for PathLeafs!");
	}

	/**
	 * This class stores the {@link SlotType} of the represented Slot only. All other relevant
	 * information are stored in the containing {@link FilterElement}. A {@link PathLeaf} is
	 * translated into a {@link ParameterLeaf} as soon as the {@link Node} representing the
	 * surrounding {@link Filter} has been created. In doing so, the containing
	 * {@link FilterElement} stores the corresponding {@link SlotInFactAddress} in
	 * {@link FilterElement#addressesInTarget}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @see FilterElement
	 * @see SlotInFactAddress
	 */
	@EqualsAndHashCode
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
		public SlotType[] getParamTypes() {
			return new SlotType[] { this.type };
		}

		@Override
		public SlotType getReturnType() {
			return this.type;
		}

		@Override
		public Function<?> lazyEvaluate(final Function<?>... params) {
			return params[0];
		}

		@Override
		public Object evaluate(final Object... params) {
			return params[0];
		}

		@Override
		public FunctionWithArguments translatePath(
				final ArrayList<SlotInFactAddress> addressesInTarget) {
			return this;
		}

		@Override
		public <T extends Collection<Path>> T gatherPaths(final T paths) {
			return paths;
		}

		@Override
		public boolean equalsInFunction(final FunctionWithArguments function) {
			return this.equals(function);
		}

	}

	@Override
	public ParameterLeaf translatePath(final ArrayList<SlotInFactAddress> addressesInTarget) {
		final FactAddress factAddressInCurrentlyLowestNode =
				this.path.getFactAddressInCurrentlyLowestNode();
		addressesInTarget.add(new SlotInFactAddress(factAddressInCurrentlyLowestNode, this.slot));
		return new ParameterLeaf(getReturnType());
	}

	@Override
	public <T extends Collection<Path>> T gatherPaths(final T paths) {
		paths.add(this.path);
		return paths;
	}

	@Override
	public boolean equalsInFunction(final FunctionWithArguments function) {
		if (function == this)
			return true;
		if (!(function instanceof PathLeaf))
			return false;
		final PathLeaf other = (PathLeaf) function;
		if (!other.canEqual(this))
			return false;
		if (this.path.template == null ? other.path.template != null : !this.path.template
				.equals(other.path.template))
			return false;
		if (this.slot == null ? other.slot != null : !this.slot.equals(other.slot))
			return false;
		return true;
	}

}
