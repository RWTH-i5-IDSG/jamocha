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
package org.jamocha.dn.memory.javaimpl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.Marker;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.logging.MarkerType;

import java.util.*;
import java.util.Map.Entry;

import static org.jamocha.util.ToArray.toArray;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@ToString(of = {"name"})
public class Template implements org.jamocha.dn.memory.Template {

    @Getter(onMethod = @__(@Override))
    final String name;
    @Getter(onMethod = @__(@Override))
    final String description;
    final HashMap<String, SlotAddress> slotNames = new HashMap<>();
    final BiMap<SlotAddress, Slot> slots;
    final SlotType[] slotTypes;
    @Getter(onMethod = @__(@Override))
    final Marker instanceMarker;

    Template(final String name, final String description, final Slot... slots) {
        this.name = name;
        this.description = description;
        this.slotTypes = toArray(Arrays.stream(slots).map(s -> s.getSlotType()), SlotType[]::new);
        this.slots = HashBiMap.create(slots.length);
        for (int i = 0; i < slots.length; ++i) {
            final SlotAddress slotAddress = new SlotAddress(i);
            final Slot slot = slots[i];
            this.slots.put(slotAddress, slot);
            this.slotNames.put(slot.getName(), slotAddress);
        }
        this.instanceMarker = MarkerType.FACTS.createChild(name);
    }

    @Override
    public SlotType getSlotType(final org.jamocha.dn.memory.SlotAddress slotAddress) {
        if (null == slotAddress) return SlotType.FACTADDRESS;
        return slotAddress.getSlotType(this);
    }

    public SlotType getSlotType(final int index) {
        return this.slotTypes[index];
    }

    @Override
    public String getSlotName(final org.jamocha.dn.memory.SlotAddress slotAddress) {
        return slotAddress.getSlotName(this);
    }

    public String getSlotName(final int index) {
        return Optional.ofNullable(getSlot(index)).map(Slot::getName).orElse(null);
    }

    @Override
    public Collection<Slot> getSlots() {
        return this.slots.values();
    }

    @Override
    public Collection<SlotAddress> getSlotAddresses() {
        return this.slots.keySet();
    }

    @Override
    public SlotAddress getSlotAddress(final Slot slot) {
        return this.slots.inverse().get(slot);
    }

    @Override
    public SlotAddress getSlotAddress(final String name) {
        return this.slotNames.get(name);
    }

    @Override
    public Slot getSlot(final org.jamocha.dn.memory.SlotAddress slotAddress) {
        return slotAddress.getSlot(this);
    }

    public Slot getSlot(final int index) {
        for (final Entry<SlotAddress, Slot> entry : this.slots.entrySet()) {
            if (entry.getKey().getIndex() == index) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getValue(final MemoryFact fact, final org.jamocha.dn.memory.SlotAddress slot) {
        return ((org.jamocha.dn.memory.javaimpl.Fact) fact).getValue(slot);
    }

    @Override
    public Object getValue(final org.jamocha.dn.memory.Fact fact, final org.jamocha.dn.memory.SlotAddress slot) {
        return fact.getValue(((SlotAddress) slot).index);
    }

    private void checkTypeAndConstraints(final int i, final Object value) {
        final Slot slot = this.slots.get(i);
        assert slot.getSlotType().getJavaClass().isInstance(value);
        for (final SlotConstraint slotConstraint : slot.getSlotConstraints()) {
            if (!slotConstraint.matchesConstraint(value)) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void setValue(final org.jamocha.dn.memory.Fact fact, final org.jamocha.dn.memory.SlotAddress slot,
            final Object value) {
        checkTypeAndConstraints(((SlotAddress) slot).index, value);
        fact.setValue(((SlotAddress) slot).index, value);
    }

    @Override
    public Fact newFact(final Object... values) {
        for (int i = 0; i < this.slotTypes.length; ++i) {
            checkTypeAndConstraints(i, values[i]);
        }
        return new Fact(this, values);
    }

    @Override
    public Fact newFact(final Map<org.jamocha.dn.memory.SlotAddress, Object> values) {
        values.forEach((s, o) -> checkTypeAndConstraints(((SlotAddress) s).index, o));
        final Set<Entry<org.jamocha.dn.memory.SlotAddress, Object>> entrySet = values.entrySet();
        assert !entrySet.stream()
                .filter(e -> !this.slotTypes[((SlotAddress) e.getKey()).index].getJavaClass().isInstance(e.getValue()))
                .findAny().isPresent();
        final OptionalInt max = entrySet.stream().mapToInt(e -> ((SlotAddress) e.getKey()).index).max();
        assert max.isPresent();
        final Object[] args = new Object[max.getAsInt() + 1];
        entrySet.forEach(e -> args[((SlotAddress) e.getKey()).index] = e.getValue());
        return new Fact(this, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <L extends ExchangeableLeaf<L>> FunctionWithArguments<L>[] applyDefaultsAndOrder(
            final Map<org.jamocha.dn.memory.SlotAddress, FunctionWithArguments<L>> values) {
        assert this.slotNames.values().containsAll(values.keySet());
        return (FunctionWithArguments<L>[]) toArray(this.slotNames.values().stream().sorted().map(slotAddress -> {
            if (values.containsKey(slotAddress)) {
                return values.get(slotAddress);
            }
            final org.jamocha.dn.memory.Template.Default defaultValue =
                    this.slots.get(slotAddress.index).getDefaultValue();
            if (DefaultType.NONE == defaultValue.getDefaultType()) {
                throw new IllegalArgumentException(
                        "No value given for slot " + this.slots.get(slotAddress.index).getName() + " in template "
                                + getName() + ", but the slot has no default value!");
            }
            return defaultValue.getValue();
        }), FunctionWithArguments[]::new);
    }
}
