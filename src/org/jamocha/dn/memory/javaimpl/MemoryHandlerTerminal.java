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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class MemoryHandlerTerminal implements org.jamocha.dn.memory.MemoryHandlerTerminal {

	final MemoryHandlerMain originatingMainHandler;
	final Queue<AssertOrRetract<?>> tokens = new LinkedList<AssertOrRetract<?>>();

	public MemoryHandlerTerminal(final MemoryHandlerMain originatingMainHandler) {
		this.originatingMainHandler = originatingMainHandler;
	}

	@Override
	public Assert addPlusMemory(final MemoryHandler mem) {
		final Assert plus = new Assert(mem);
		this.tokens.add(plus);
		return plus;
	}

	@Override
	public Retract addMinusMemory(final MemoryHandler mem) {
		final Retract minus = new Retract(mem);
		for (final AssertOrRetract<?> token : this.tokens) {
			if (token.getMem().equals(mem) && token.setFollowingRetract(minus)) {
				break;
			}
		}
		this.tokens.add(minus);
		return minus;
	}

	@Override
	public void addPartialMinusMemory(final TerminalNode terminalNode,
			final org.jamocha.dn.memory.MemoryHandlerMinusTemp mem) {
		final MemoryHandlerMinusTemp minusTemp = (MemoryHandlerMinusTemp) mem;
		final FactAddress[] factAddresses = minusTemp.factAddresses;
		final Queue<Retract> retracts = new LinkedList<>();
		for (final Row minusRow : minusTemp.rows) {
			for (final AssertOrRetract<?> token : this.tokens) {
				final MemoryHandlerBase tokenMem = (MemoryHandlerBase) token.getMem();
				final Row tokenRow = tokenMem.getRowsForSucessorNodes().get(0);
				if (EqualityChecker.beta.equals(tokenRow, minusRow, null, 0, factAddresses)
						&& !token.isRevokedOrMinus()) {
					final Retract minus = new Retract(token.getMem());
					token.setFollowingRetract(minus);
					retracts.add(minus);
					terminalNode.enqueueRetract(minus);
				}
			}
		}
		this.tokens.addAll(retracts);
	}

	@Override
	public int size() {
		int size = 0;
		for (final AssertOrRetract<?> token : this.tokens) {
			size += token.getMem().size();
		}
		return size;
	}

	@Override
	public Template[] getTemplate() {
		return this.originatingMainHandler.getTemplate();
	}

	@Override
	public Object getValue(final org.jamocha.dn.memory.FactAddress address, final SlotAddress slot,
			final int row) {
		int index = row;
		for (final AssertOrRetract<?> token : this.tokens) {
			final int memSize = token.getMem().size();
			if (index >= memSize) {
				index -= memSize;
				continue;
			}
			return token.getMem().getValue(address, slot, index);
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public Iterator<AssertOrRetract<?>> iterator() {
		return this.tokens.iterator();
	}

	@Override
	public boolean containsUnrevokedTokens() {
		for (final AssertOrRetract<?> token : this.tokens) {
			if (!token.isRevokedOrMinus()) {
				return true;
			}
		}
		return false;
	}

}
