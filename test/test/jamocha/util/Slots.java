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

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Slots {
	public static Slot newDouble(final String name) {
		return Slot.newSlot(SlotType.DOUBLE, name, 0.0);
	}

	public static Slot newLong(final String name) {
		return Slot.newSlot(SlotType.LONG, name, 0L);
	}

	public static Slot newString(final String name) {
		return Slot.newSlot(SlotType.STRING, name, "");
	}

	public static Slot newBoolean(final String name) {
		return Slot.newSlot(SlotType.BOOLEAN, name, Boolean.FALSE);
	}

	/**
	 * Template holding exactly one {@link SlotType#STRING} type.
	 */
	final public static Template STRING = MemoryFactory.getMemoryFactory().newTemplate("STRING",
			"Simple template holding exactly one string type.", newString("string slot"));
	/**
	 * Template holding exactly one {@link SlotType#BOOLEAN} type.
	 */
	final public static Template BOOLEAN = MemoryFactory.getMemoryFactory().newTemplate("BOOLEAN",
			"Simple template holding exactly one boolean type.", newBoolean("boolean slot"));
	/**
	 * Template holding exactly one {@link SlotType#DOUBLE} type.
	 */
	final public static Template DOUBLE = MemoryFactory.getMemoryFactory().newTemplate("DOUBLE",
			"Simple template holding exactly one double type.", newDouble("double slot"));
	/**
	 * Template holding exactly one {@link SlotType#LONG} type.
	 */
	final public static Template LONG = MemoryFactory.getMemoryFactory().newTemplate("LONG",
			"Simple template holding exactly one long type.", newLong("long slot"));

}
