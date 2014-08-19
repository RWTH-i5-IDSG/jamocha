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
package test.jamocha.util;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Marker;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.fwa.FunctionWithArguments;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
@Getter
public class TemplateMockup implements Template {

	final String name;
	final String description;
	final List<Slot> slots;
	final SlotAddress[] slotAddresses;
	
	public TemplateMockup(String name, String description, List<Slot> slots) {
		this.name = name;
		this.description = description;
		this.slots = slots;
		slotAddresses = new SlotAddress[slots.size()];
	}

	@RequiredArgsConstructor
	public static class SlotAddressMockup implements SlotAddress {

		final int index;

		@Override
		public SlotType getSlotType(Template template) {
			return template.getSlotType(this);
		}

		@Override
		public String getSlotName(Template template) {
			return template.getSlotName(this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#getSlotType(org.jamocha.dn.memory.SlotAddress)
	 */
	@Override
	public SlotType getSlotType(SlotAddress slotAddress) {
		return this.slots.get(((SlotAddressMockup) slotAddress).index).getSlotType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#getSlotName(org.jamocha.dn.memory.SlotAddress)
	 */
	@Override
	public String getSlotName(SlotAddress slotAddress) {
		return this.slots.get(((SlotAddressMockup) slotAddress).index).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#getSlotAddress(java.lang.String)
	 */
	@Override
	public SlotAddress getSlotAddress(String name) {
		int i = 0;
		for (Slot slot : this.slots) {
			if (slot.getName().equals(name)) {
				if (slotAddresses[i] == null)
					slotAddresses[i] = new SlotAddressMockup(i);
				return slotAddresses[i];
			}
			i++;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#newFact(java.lang.Object[])
	 */
	@Override
	public Fact newFact(Object... values) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#newFact(java.util.Map)
	 */
	@Override
	public Fact newFact(Map<SlotAddress, Object> valuesMap) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#setValue(org.jamocha.dn.memory.Fact,
	 * org.jamocha.dn.memory.SlotAddress, java.lang.Object)
	 */
	@Override
	public void setValue(Fact fact, SlotAddress slot, Object value) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#getValue(org.jamocha.dn.memory.Fact,
	 * org.jamocha.dn.memory.SlotAddress)
	 */
	@Override
	public Object getValue(Fact fact, SlotAddress slot) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#getValue(org.jamocha.dn.memory.MemoryFact,
	 * org.jamocha.dn.memory.SlotAddress)
	 */
	@Override
	public Object getValue(MemoryFact fact, SlotAddress slot) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#applyDefaultsAndOrder(java.util.Map)
	 */
	@Override
	public FunctionWithArguments[] applyDefaultsAndOrder(
			Map<SlotAddress, FunctionWithArguments> values) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.dn.memory.Template#getInstanceMarker()
	 */
	@Override
	public Marker getInstanceMarker() {
		// TODO Auto-generated method stub
		return null;
	}

}
