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

/**
 * Interface for addresses identifying a slot in a fact.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Template
 * @see SlotType
 */
public interface SlotAddress {
	/**
	 * Gets the {@link SlotType} of the slot this address identifies.
	 * 
	 * @param template
	 *            the {@link Template} this {@link SlotAddress} is valid for
	 * @return the {@link SlotType} of the slot this address identifies
	 */
	public SlotType getSlotType(final Template template);
}
