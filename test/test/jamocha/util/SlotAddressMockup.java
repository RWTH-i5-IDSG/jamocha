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

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;

import lombok.Value;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
@Value
public class SlotAddressMockup implements SlotAddress {
	int index;

	@Override
	public SlotType getSlotType(final Template template) {
		return ((org.jamocha.dn.memory.javaimpl.Template) template).getSlotType(getIndex());
	}

	@Override
	public String getSlotName(final Template template) {
		return ((org.jamocha.dn.memory.javaimpl.Template) template).getSlotName(getIndex());
	}

	@Override
	public Slot getSlot(final Template template) {
		return ((org.jamocha.dn.memory.javaimpl.Template) template).getSlot(getIndex());
	}

	@Override
	public int compareTo(org.jamocha.dn.memory.SlotAddress o) {
		return Integer.compare(index, ((SlotAddressMockup) o).index);
	}

	@Override
	public MatchingAddressFactory newMatchingAddressFactory() {
		return null;
	}
}
