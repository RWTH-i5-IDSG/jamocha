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
package org.jamocha.dn;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lombok.Value;

import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class ConflictSet implements Iterable<ConflictSet.NodeAndToken> {

	@Value
	public static class NodeAndToken {
		TerminalNode terminal;
		AssertOrRetract<?> token;
	}

	final List<NodeAndToken> nodesAndTokens = new LinkedList<>();

	public void addAssert(final TerminalNode terminal, final Assert plus) {
		this.nodesAndTokens.add(new NodeAndToken(terminal, plus));
	}

	public void addRetract(final TerminalNode terminal, final Retract minus) {
		this.nodesAndTokens.add(new NodeAndToken(terminal, minus));
	}

	/**
	 * deletes all asserts and retracts
	 */
	public void flush() {
		this.nodesAndTokens.clear();
	}

	/**
	 * deletes all asserts and retracts with duals
	 */
	public void deleteRevokedEntries() {
		final Iterator<NodeAndToken> iterator = this.nodesAndTokens.iterator();
		while (iterator.hasNext()) {
			final NodeAndToken nodeAndToken = iterator.next();
			final AssertOrRetract<?> token = nodeAndToken.getToken();
			if (token.isRevoked()) {
				iterator.remove();
			}
		}
	}

	@Override
	public Iterator<NodeAndToken> iterator() {
		return this.nodesAndTokens.iterator();
	}

}
