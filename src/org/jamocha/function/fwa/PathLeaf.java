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
package org.jamocha.function.fwa;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.Filter;
import org.jamocha.filter.PathFilterToAddressFilterTranslator;
import org.jamocha.filter.Path;
import org.jamocha.function.Function;

/**
 * A parameter of a {@link Function} may be a slot of a {@link Fact}. The corresponding
 * {@link SlotAddress} and {@link Path} are stored in this class. As soon as the {@link Node}
 * representing the surrounding {@link Filter} has been created, the {@link Filter} is
 * {@link PathFilterToAddressFilterTranslator#translate(org.jamocha.filter.PathFilter, org.jamocha.dn.memory.CounterColumnMatcher)
 * translated} and all {@link PathLeaf PathLeafs} are replaced with {@link ParameterLeaf
 * ParameterLeafs}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Path
 * @see SlotAddress
 * @see Node
 */
@EqualsAndHashCode
public class PathLeaf implements FunctionWithArguments {
	private final Path path;
	private final SlotAddress slot;
	@Getter(lazy = true)
	private final int hashCode = initHashCode();

	public PathLeaf(final Path path, final SlotAddress slot) {
		super();
		this.path = path;
		this.slot = slot;
	}

	private int initHashCode() {
		return FunctionWithArguments.hash(
				Arrays.asList(this.path.getTemplate(), this.slot).stream().mapToInt(Object::hashCode).toArray(),
				FunctionWithArguments.positionIsIrrelevant);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getPath().toString());
		sb.append(" [");
		sb.append(getSlot().toString());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public SlotType[] getParamTypes() {
		return new SlotType[] { getSlot().getSlotType(getPath().getTemplate()) };
	}

	@Override
	public SlotType getReturnType() {
		return getSlot().getSlotType(getPath().getTemplate());
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
	 * information are stored in the containing {@link AddressFilterElement}. A {@link PathLeaf} is
	 * translated into a {@link ParameterLeaf} as soon as the {@link Node} representing the
	 * surrounding {@link Filter} has been created. In doing so, the containing
	 * {@link AddressFilterElement} stores the corresponding {@link SlotInFactAddress} in
	 * {@link AddressFilterElement#getAddressesInTarget()}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @see AddressFilterElement
	 * @see SlotInFactAddress
	 */
	@EqualsAndHashCode
	public static class ParameterLeaf implements FunctionWithArguments {
		private final SlotType slotType;
		private final SlotType[] slotTypes;
		private final int hashCode;

		public ParameterLeaf(final SlotType type, final int hashCode) {
			super();
			this.slotType = type;
			this.slotTypes = new SlotType[] { type };
			this.hashCode = hashCode;
		}

		@Override
		public String toString() {
			return "[" + slotType + "]";
		}

		@Override
		public SlotType[] getParamTypes() {
			return slotTypes;
		}

		@Override
		public SlotType getReturnType() {
			return slotType;
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
		public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
			visitor.visit(this);
			return visitor;
		}

		/**
		 * @return the slot type
		 */
		public SlotType getType() {
			return slotType;
		}

		@Override
		public int hashPositionIsIrrelevant() {
			return hashCode;
		}
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * @return the slot
	 */
	public SlotAddress getSlot() {
		return this.slot;
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return getHashCode();
	}
}
