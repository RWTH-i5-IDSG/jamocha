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

import static org.jamocha.util.ToArray.toArray;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template.Default;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.Template.SlotConstraint;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.SymbolLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class SlotBuilder {
	final String name;
	SlotType slotType;
	Default defaultValue;
	SlotConstraint range;
	SlotConstraint allowedConstants;
	SlotType inferedType;

	public SlotBuilder setType(final SlotType type) {
		if (null != slotType) {
			throw new IllegalArgumentException("Type already set!");
		}
		if (null != inferedType && inferedType != type) {
			throw new IllegalArgumentException("Incompatible types in slot restrictions!");
		}
		this.slotType = this.inferedType = type;
		return this;
	}

	public SlotBuilder setStaticDefault(final FunctionWithArguments<SymbolLeaf> value) {
		if (null != defaultValue) {
			throw new IllegalArgumentException("Default value already set!");
		}
		if (null != inferedType && inferedType != value.getReturnType()) {
			throw new IllegalArgumentException("Incompatible types for default value!");
		}
		this.inferedType = value.getReturnType();
		this.defaultValue = Default.staticDefault(value);
		return this;
	}

	public SlotBuilder setDynamicDefault(final FunctionWithArguments<SymbolLeaf> value) {
		if (null != defaultValue) {
			throw new IllegalArgumentException("Default value already set!");
		}
		if (null != inferedType && inferedType != value.getReturnType()) {
			throw new IllegalArgumentException("Incompatible types for default value!");
		}
		this.inferedType = value.getReturnType();
		this.defaultValue = Default.dynamicDefault(value);
		return this;
	}

	public SlotBuilder setNoDefault() {
		if (null != defaultValue) {
			throw new IllegalArgumentException("Default value already set!");
		}
		this.defaultValue = Default.noDefault();
		return this;
	}

	public SlotBuilder setDeriveDefault() {
		if (null != defaultValue) {
			throw new IllegalArgumentException("Default value already set!");
		}
		return this;
	}

	public SlotBuilder setRangeConstraint(final ConstantLeaf<SymbolLeaf> from, final ConstantLeaf<SymbolLeaf> to) {
		if (null != range) {
			throw new IllegalArgumentException("Range constraint already set!");
		}
		if (null == from && null == to) {
			return this;
		}
		final SlotType rangeType;
		if (null != from) {
			if (null != to && from.getType() != to.getType()) {
				throw new IllegalArgumentException("Incompatible types in range specification!");
			}
			rangeType = from.getType();
		} else {
			rangeType = to.getType();
		}
		if (null != inferedType && inferedType != rangeType) {
			throw new IllegalArgumentException("Incompatible types in slot restrictions!");
		}
		this.inferedType = rangeType;
		if (rangeType == SlotType.LONG) {
			this.range = SlotConstraint.integerRange((Long) from.getValue(), (Long) to.getValue());
			return this;
		}
		if (rangeType == SlotType.DOUBLE) {
			this.range = SlotConstraint.doubleRange((Double) from.getValue(), (Double) to.getValue());
			return this;
		}
		throw new IllegalArgumentException();
	}

	public SlotBuilder setAllowedConstaintsConstraint(final SlotType type, final List<?> constants) {
		if (null != allowedConstants) {
			throw new IllegalArgumentException("Allowed constants constraint already set!");
		}
		if (null != inferedType && inferedType != type) {
			throw new IllegalArgumentException("Incompatible types in slot restrictions!");
		}
		this.allowedConstants = SlotConstraint.allowedConstants(type, constants);
		return this;
	}

	public Slot build(final EnumMap<SlotType, Object> defaultValues) {
		if (null == this.slotType) {
			if (null == this.inferedType) {
				this.slotType = SlotType.SYMBOL;
			} else {
				this.slotType = this.inferedType;
			}
		}
		final Default defaultValue;
		if (null != this.defaultValue) {
			defaultValue = this.defaultValue;
		} else {
			final FunctionWithArguments<SymbolLeaf> value;
			// derive the default value
			if (null != allowedConstants) {
				value = allowedConstants.derivedDefaultValue();
			} else if (null != range) {
				value = range.derivedDefaultValue();
			} else {
				value = new ConstantLeaf<>(defaultValues.get(this.slotType), this.slotType);
			}
			defaultValue = Default.staticDefault(value);
		}
		return new Slot(slotType, name, defaultValue, toArray(
				Stream.of(range, allowedConstants).filter(Objects::nonNull), SlotConstraint[]::new));
	}
}
