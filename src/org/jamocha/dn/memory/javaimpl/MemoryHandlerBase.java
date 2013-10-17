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
package org.jamocha.dn.memory.javaimpl;

import java.util.List;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class MemoryHandlerBase implements MemoryHandler {
	final Template[] template;
	final List<Fact[]> facts;

	public MemoryHandlerBase(final Template[] template, final List<Fact[]> facts) {
		this.template = template;
		this.facts = facts;
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#getTemplate()
	 */
	@Override
	public Template[] getTemplate() {
		return this.template;
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#getValue(FactAddress, SlotAddress, int)
	 */
	@Override
	public Object getValue(final FactAddress address, final SlotAddress slot, int row) {
		return this.facts.get(row)[((org.jamocha.dn.memory.javaimpl.FactAddress) address)
				.getIndex()].getValue(slot);
	}

	/**
	 * @see org.jamocha.dn.memory.MemoryHandler#size()
	 */
	@Override
	public int size() {
		return this.facts.size();
	}

	public boolean canEqual(final Object other) {
		return other instanceof MemoryHandlerBase;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof MemoryHandlerBase))
			return false;
		final MemoryHandlerBase other = (MemoryHandlerBase) obj;
		if (!other.canEqual((Object) this))
			return false;
		if (this.getTemplate() == null ? other.getTemplate() != null : !this.getTemplate().equals(
				other.getTemplate()))
			return false;
		if (this.facts == null ? other.facts != null : !this.facts.equals(other.facts))
			return false;
		return true;
	}
}
