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
package org.jamocha.dn.memory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Value;

import org.apache.logging.log4j.Marker;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;

/**
 * A Template consists of slots which in turn have a {@link SlotType slot type} and a name. Facts
 * always comply with some Template.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see SlotType
 * @see Fact
 */
public interface Template {

	@Value
	public static class Slot {
		final SlotType slotType;
		final String name;
		final Default defaultValue;
		final SlotConstraint[] slotConstraints;

		public Slot(final SlotType slotType, final String name, final Default defaultValue,
				final SlotConstraint... slotConstraints) {
			this.slotType = slotType;
			this.name = name;
			this.defaultValue = defaultValue;
			this.slotConstraints = slotConstraints;
		}

		public static Slot newSlot(final SlotType slotType, final String name, final Object defaultValue,
				final SlotConstraint... slotConstraints) {
			return new Slot(slotType, name, Default.staticDefault(new ConstantLeaf(defaultValue, slotType)),
					slotConstraints);
		}

		public static final Slot LONG = Slot.newSlot(SlotType.LONG, "Long slot", Long.valueOf(0L));
		public static final Slot DOUBLE = Slot.newSlot(SlotType.DOUBLE, "Double slot", Double.valueOf(0.0));
		public static final Slot STRING = Slot.newSlot(SlotType.STRING, "String slot", "");
		public static final Slot BOOLEAN = Slot.newSlot(SlotType.BOOLEAN, "Boolean slot", Boolean.FALSE);
	}

	public static enum DefaultType {
		NONE, STATIC, DYNAMIC;
	}

	@Data
	public abstract static class Default {
		final DefaultType defaultType;

		public abstract FunctionWithArguments getValue();

		static final FunctionWithArguments nullFWA = new ConstantLeaf(null, SlotType.NIL);
		static final Default none = new Default(DefaultType.NONE) {
			@Override
			public FunctionWithArguments getValue() {
				return nullFWA;
			}
		};

		public static Default noDefault() {
			return none;
		}

		static class StaticDefault extends Default {
			final FunctionWithArguments value;

			public StaticDefault(final FunctionWithArguments value) {
				super(DefaultType.STATIC);
				this.value = new ConstantLeaf(value.evaluate(), value.getReturnType());
			}

			@Override
			public FunctionWithArguments getValue() {
				return value;
			}
		}

		public static Default staticDefault(final FunctionWithArguments value) {
			return new StaticDefault(value);
		}

		static class DynamicDefault extends Default {
			final FunctionWithArguments value;

			public DynamicDefault(final FunctionWithArguments value) {
				super(DefaultType.DYNAMIC);
				this.value = value;
			}

			@Override
			public FunctionWithArguments getValue() {
				return value;
			}
		}

		public static Default dynamicDefault(final FunctionWithArguments value) {
			return new DynamicDefault(value);
		}
	}

	public static enum ConstraintType {
		ALLOWED_CONSTANTS, RANGE, CARDINALITY;
	}

	@Data
	public static abstract class SlotConstraint {
		final ConstraintType constraintType;

		public abstract boolean matchesConstraint(final Object value);

		public abstract FunctionWithArguments derivedDefaultValue();

		public static SlotConstraint integerRange(final Long from, final Long to) {
			return new SlotConstraint(ConstraintType.RANGE) {
				@Override
				public boolean matchesConstraint(final Object value) {
					try {
						final Long typedValue = (Long) value;
						if (null != from && from > typedValue)
							return false;
						if (null != to && to < typedValue)
							return false;
						return true;
					} catch (final ClassCastException e) {
						return false;
					}
				}

				@Override
				public FunctionWithArguments derivedDefaultValue() {
					return new ConstantLeaf(from, SlotType.LONG);
				}
			};
		}

		public static SlotConstraint doubleRange(final Double from, final Double to) {
			return new SlotConstraint(ConstraintType.RANGE) {
				@Override
				public boolean matchesConstraint(final Object value) {
					try {
						final Double typedValue = (Double) value;
						if (null != from && from > typedValue)
							return false;
						if (null != to && to < typedValue)
							return false;
						return true;
					} catch (final ClassCastException e) {
						return false;
					}
				}

				@Override
				public FunctionWithArguments derivedDefaultValue() {
					return new ConstantLeaf(from, SlotType.DOUBLE);
				}
			};
		}

		public static SlotConstraint allowedConstants(final SlotType type, final List<?> values) {
			final ConstantLeaf defaultValue = new ConstantLeaf(values.get(0), type);
			return new SlotConstraint(ConstraintType.ALLOWED_CONSTANTS) {
				@Override
				public boolean matchesConstraint(final Object value) {
					return values.contains(value);
				}

				@Override
				public FunctionWithArguments derivedDefaultValue() {
					return defaultValue;
				}
			};
		}

		// TBD cardinality
	}

	/**
	 * Returns the name of the template.
	 * 
	 * @return the name of the template
	 */
	public String getName();

	/**
	 * Returns the description of the template.
	 * 
	 * @return the description of the template
	 */
	public String getDescription();

	/**
	 * Returns the slots in the order given at construction time, possibly deviating from the
	 * internal ordering.
	 * 
	 * @return the slots in the order given at construction time, possibly deviating from the
	 *         internal ordering
	 */
	public Collection<Slot> getSlots();

	/**
	 * Returns the slot object corresponding to the slot address given.
	 * 
	 * @return the slot object corresponding to the slot address given
	 */
	public Slot getSlot(final SlotAddress slotAddress);

	/**
	 * Gets the {@link SlotType} corresponding to the position specified by the given index.
	 * 
	 * @param index
	 *            position in the template
	 * @return {@link SlotType} corresponding to the position specified by the given index
	 */
	public SlotType getSlotType(final SlotAddress slotAddress);

	/**
	 * Gets the name corresponding to the position specified by the given index.
	 * 
	 * @param index
	 *            position in the template
	 * @return name corresponding to the position specified by the given index
	 */
	public String getSlotName(final SlotAddress slotAddress);

	/**
	 * Returns the {@link SlotAddress} of the first slot matching the name given or null if no slot
	 * name matched.
	 * 
	 * @param name
	 *            string to match against the slot names
	 * @return the {@link SlotAddress} of the first slot matching the name given or null if no slot
	 *         name matched.
	 */
	public SlotAddress getSlotAddress(final String name);

	/**
	 * Ease-of-use method to create facts with type-check for its arguments.
	 * 
	 * @param values
	 *            values to store in the fact instance to create
	 * @return newly created fact instance holding the values specified
	 */
	public Fact newFact(final Object... values);

	/**
	 * Ease-of-use method to create facts with type-check for its arguments.
	 * 
	 * @param values
	 *            values to store in the fact instance to create
	 * @return newly created fact instance holding the values specified
	 */
	public Fact newFact(final Map<SlotAddress, Object> valuesMap);

	/**
	 * 
	 * @param fact
	 * @param slot
	 * @param value
	 */
	public void setValue(final Fact fact, final SlotAddress slot, final Object value);

	/**
	 * 
	 * @param fact
	 * @param slot
	 * @return
	 */
	public Object getValue(final Fact fact, final SlotAddress slot);

	/**
	 * 
	 * @param fact
	 * @param slot
	 * @return
	 */
	public Object getValue(final MemoryFact fact, final SlotAddress slot);

	/**
	 * Return a list of FunctionWithArguments which - after evaluation - can be used to construct an
	 * instance of this template. This includes inserting default values where appropriate and
	 * giving the return list the correct order.
	 * 
	 * @param values
	 *            given values for the corresponding slots
	 * @return list that can be used to construct a template after evaluation
	 */
	public FunctionWithArguments[] applyDefaultsAndOrder(final Map<SlotAddress, FunctionWithArguments> values);

	/**
	 * Returns a marker uniquely identifying the template instance as a child of
	 * {@link Template#templateMarker}.
	 * 
	 * @return a marker uniquely identifying the template instance as a child of
	 *         {@link Template#templateMarker}
	 */
	public Marker getInstanceMarker();
}
