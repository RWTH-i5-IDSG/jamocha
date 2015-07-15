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

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerMinusTemp.MemoryHandlerMinusTempComplete;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
@RequiredArgsConstructor
public class MemoryHandlerTerminal implements org.jamocha.dn.memory.MemoryHandlerTerminal {

	final MemoryHandlerMain originatingMainHandler;
	final Queue<Assert> plusTokenCache = new LinkedList<Assert>();

	@Override
	public void addPlusMemory(final TerminalNode terminalNode, final org.jamocha.dn.memory.MemoryHandlerPlusTemp mem) {
		for (final MemoryHandler handler : mem.splitIntoChunksOfSize(1)) {
			final Assert plus = new Assert(handler);
			this.plusTokenCache.add(plus);
			terminalNode.enqueueAssert(plus);
		}
	}

	@Override
	public void addMinusMemory(final TerminalNode terminalNode, final org.jamocha.dn.memory.MemoryHandlerMinusTemp mem) {
		if (!(mem instanceof MemoryHandlerMinusTempComplete)) {
			// handle partial minus token
			final MemoryHandlerMinusTemp minusTemp = (MemoryHandlerMinusTemp) mem;
			final FactAddress[] factAddresses = minusTemp.factAddresses;
			for (final Row minusRow : minusTemp.validRows) {
				for (final Iterator<Assert> tokenIterator = this.plusTokenCache.iterator(); tokenIterator.hasNext();) {
					final AssertOrRetract<?> token = tokenIterator.next();
					final MemoryHandlerBase tokenMem = (MemoryHandlerBase) token.getMem();
					final Row tokenRow = tokenMem.validRows.get(0);
					if (EqualityChecker.beta.equals(tokenRow, minusRow, null, 0, factAddresses)
							&& !token.isRevokedOrMinus()) {
						final Retract minus = new Retract(token.getMem());
						token.setFollowingRetract(minus);
						terminalNode.enqueueRetract(minus);
						tokenIterator.remove();
					}
				}
			}
			return;
		}
		for (final MemoryHandler handler : mem.splitIntoChunksOfSize(1)) {
			final Retract minus = new Retract(handler);
			terminalNode.enqueueRetract(minus);
			for (final Iterator<Assert> iterator = this.plusTokenCache.iterator(); iterator.hasNext();) {
				final AssertOrRetract<?> token = iterator.next();
				if (token.getMem().equals(handler) && token.setFollowingRetract(minus)) {
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public Iterator<Assert> iterator() {
		return this.plusTokenCache.iterator();
	}

	@Override
	public boolean containsUnrevokedTokens() {
		for (final AssertOrRetract<?> token : this.plusTokenCache) {
			if (!token.isRevokedOrMinus()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void flush() {
		this.plusTokenCache.clear();
	}
}
