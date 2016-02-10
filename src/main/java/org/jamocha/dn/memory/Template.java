/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.memory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Value;

import org.apache.logging.log4j.Marker;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.SymbolLeaf;

/**
 * A Template consists of slots which in turn have a {@link SlotType slot type} and a name. Facts always comply with
 * some Template.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see SlotType
 * @see Fact
 */
public interface Template {

    @Value
    class Slot {
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
            return new Slot(slotType, name, Default.staticDefault(new ConstantLeaf<>(defaultValue, slotType)),
                    slotConstraints);
        }
    }

    enum DefaultType {
        NONE, STATIC, DYNAMIC;
    }

    @Data
    abstract class Default {
        final DefaultType defaultType;

        public abstract FunctionWithArguments<?> getValue();

        static final FunctionWithArguments<SymbolLeaf> NULL_FWA = new ConstantLeaf<>(null, SlotType.NIL);
        static final Default NONE = new Default(DefaultType.NONE) {
            @Override
            public FunctionWithArguments<SymbolLeaf> getValue() {
                return NULL_FWA;
            }
        };

        public static Default noDefault() {
            return NONE;
        }

        static class StaticDefault extends Default {
            final FunctionWithArguments<SymbolLeaf> value;

            StaticDefault(final FunctionWithArguments<SymbolLeaf> value) {
                super(DefaultType.STATIC);
                this.value = new ConstantLeaf<SymbolLeaf>(value.evaluate(), value.getReturnType());
            }

            @Override
            public FunctionWithArguments<SymbolLeaf> getValue() {
                return value;
            }
        }

        public static Default staticDefault(final FunctionWithArguments<SymbolLeaf> value) {
            return new StaticDefault(value);
        }

        static class DynamicDefault extends Default {
            final FunctionWithArguments<SymbolLeaf> value;

            DynamicDefault(final FunctionWithArguments<SymbolLeaf> value) {
                super(DefaultType.DYNAMIC);
                this.value = value;
            }

            @Override
            public FunctionWithArguments<SymbolLeaf> getValue() {
                return value;
            }
        }

        public static Default dynamicDefault(final FunctionWithArguments<SymbolLeaf> value) {
            return new DynamicDefault(value);
        }
    }

    enum ConstraintType {
        ALLOWED_CONSTANTS, RANGE, CARDINALITY;
    }

    @Data
    abstract class SlotConstraint {
        final ConstraintType constraintType;

        public abstract boolean matchesConstraint(final Object value);

        public abstract FunctionWithArguments<SymbolLeaf> derivedDefaultValue(final SlotType type, final Object value);

        public abstract List<Object> getInterestingValues();

        public static SlotConstraint integerRange(final boolean singleSlot, final Long from, final Long to) {
            final ConstantLeaf<SymbolLeaf> defaultValue = new ConstantLeaf<SymbolLeaf>(from, SlotType.LONG);
            return new SlotConstraint(ConstraintType.RANGE) {
                @Override
                public boolean matchesConstraint(final Object value) {
                    if (singleSlot) return check(value);
                    for (final Object v : (Object[]) value) {
                        if (!check(v)) return false;
                    }
                    return true;
                }

                private boolean check(final Object value) {
                    try {
                        final Long typedValue = (Long) value;
                        if (null != from && from > typedValue) return false;
                        if (null != to && to < typedValue) return false;
                        return true;
                    } catch (final ClassCastException e) {
                        return false;
                    }
                }

                @Override
                public FunctionWithArguments<SymbolLeaf> derivedDefaultValue(final SlotType type, final Object value) {
                    return defaultValue;
                }

                @Override
                public List<Object> getInterestingValues() {
                    return Arrays.asList(from, to);
                }
            };
        }

        public static SlotConstraint doubleRange(final boolean singleSlot, final Double from, final Double to) {
            final ConstantLeaf<SymbolLeaf> defaultValue = new ConstantLeaf<SymbolLeaf>(from, SlotType.DOUBLE);
            return new SlotConstraint(ConstraintType.RANGE) {
                @Override
                public boolean matchesConstraint(final Object value) {
                    if (singleSlot) return check(value);
                    for (final Object v : (Object[]) value) {
                        if (!check(v)) return false;
                    }
                    return true;
                }

                private boolean check(final Object value) {
                    try {
                        final Double typedValue = (Double) value;
                        if (null != from && from > typedValue) return false;
                        if (null != to && to < typedValue) return false;
                        return true;
                    } catch (final ClassCastException e) {
                        return false;
                    }
                }

                @Override
                public FunctionWithArguments<SymbolLeaf> derivedDefaultValue(final SlotType type, final Object value) {
                    return defaultValue;
                }

                @Override
                public List<Object> getInterestingValues() {
                    return Arrays.asList(from, to);
                }
            };
        }

        public static SlotConstraint allowedConstants(final boolean singleSlot, final SlotType type,
                final List<Object> values) {
            final ConstantLeaf<SymbolLeaf> defaultValue = new ConstantLeaf<>(values.get(0), type);
            return new SlotConstraint(ConstraintType.ALLOWED_CONSTANTS) {
                @Override
                public boolean matchesConstraint(final Object value) {
                    if (singleSlot) return check(value);
                    for (final Object v : (Object[]) value) {
                        if (!check(v)) return false;
                    }
                    return true;
                }

                private boolean check(final Object value) {
                    return values.contains(value);
                }

                @Override
                public FunctionWithArguments<SymbolLeaf> derivedDefaultValue(final SlotType type, final Object value) {
                    return defaultValue;
                }

                @Override
                public List<Object> getInterestingValues() {
                    return values;
                }
            };
        }

        public static SlotConstraint cardinality(final Long min, final Long max) {
            return new SlotConstraint(ConstraintType.CARDINALITY) {
                @Override
                public boolean matchesConstraint(final Object value) {
                    final Object[] values = (Object[]) value;
                    return min <= values.length && values.length <= max;
                }

                @Override
                public FunctionWithArguments<SymbolLeaf> derivedDefaultValue(final SlotType type, final Object value) {
                    if (0 == min) {
                        return new ConstantLeaf<>(new Object[0], type);
                    }
                    final Object[] array = new Object[min.intValue()];
                    Arrays.fill(array, value);
                    return new ConstantLeaf<>(array, type);
                }

                @Override
                public List<Object> getInterestingValues() {
                    return Arrays.asList(min, max);
                }
            };
        }
    }

    /**
     * Returns the name of the template.
     *
     * @return the name of the template
     */
    String getName();

    /**
     * Returns the description of the template.
     *
     * @return the description of the template
     */
    String getDescription();

    /**
     * Returns the slots in the order given at construction time, possibly deviating from the internal ordering.
     *
     * @return the slots in the order given at construction time, possibly deviating from the internal ordering
     */
    Collection<Slot> getSlots();

    /**
     * Returns the slot object corresponding to the slot address given.
     *
     * @return the slot object corresponding to the slot address given
     */
    Slot getSlot(final SlotAddress slotAddress);

    /**
     * Gets the {@link SlotType} corresponding to the position specified by the given index.
     *
     * @param slotAddress
     *         address for the position in the template
     * @return {@link SlotType} corresponding to the position specified by the given index
     */
    SlotType getSlotType(final SlotAddress slotAddress);

    /**
     * Gets the name corresponding to the position specified by the given index.
     *
     * @param slotAddress
     *         address for the position in the template
     * @return name corresponding to the position specified by the given index
     */
    String getSlotName(final SlotAddress slotAddress);

    /**
     * Returns the {@link SlotAddress} of the first slot matching the name given or null if no slot name matched.
     *
     * @param name
     *         string to match against the slot names
     * @return the {@link SlotAddress} of the first slot matching the name given or null if no slot name matched.
     */
    SlotAddress getSlotAddress(final String name);

    /**
     * Ease-of-use method to create facts with type-check for its arguments.
     *
     * @param values
     *         values to store in the fact instance to create
     * @return newly created fact instance holding the values specified
     */
    Fact newFact(final Object... values);

    /**
     * Ease-of-use method to create facts with type-check for its arguments.
     *
     * @param valuesMap
     *         valuesMap to store in the fact instance to create
     * @return newly created fact instance holding the values specified
     */
    Fact newFact(final Map<SlotAddress, Object> valuesMap);

    /**
     * @param fact
     * @param slot
     * @param value
     */
    void setValue(final Fact fact, final SlotAddress slot, final Object value);

    /**
     * @param fact
     * @param slot
     * @return
     */
    Object getValue(final Fact fact, final SlotAddress slot);

    /**
     * @param fact
     * @param slot
     * @return
     */
    Object getValue(final MemoryFact fact, final SlotAddress slot);

    /**
     * Return a list of FunctionWithArguments which - after evaluation - can be used to construct an instance of this
     * template. This includes inserting default values where appropriate and giving the return list the correct order.
     *
     * @param values
     *         given values for the corresponding slots
     * @return list that can be used to construct a template after evaluation
     */
    <L extends ExchangeableLeaf<L>> FunctionWithArguments<L>[] applyDefaultsAndOrder(
            final Map<SlotAddress, FunctionWithArguments<L>> values);

    /**
     * Returns a marker uniquely identifying the template instance as a child of the template.
     *
     * @return a marker uniquely identifying the template instance as a child of the template
     */
    Marker getInstanceMarker();
}
