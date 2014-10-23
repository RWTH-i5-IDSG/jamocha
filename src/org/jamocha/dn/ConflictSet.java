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

import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.Translated;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * Simple conflict set implementation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class ConflictSet implements Iterable<ConflictSet.RuleAndToken> {

	final SideEffectFunctionToNetwork network;
	final ConstructCache constructCache;

	/**
	 * Combination of {@link TerminalNode} and {@link AssertOrRetract}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	public static class RuleAndToken {
		Defrule.Translated rule;
		AssertOrRetract<?> token;
	}

	final LinkedList<RuleAndToken> rulesAndTokens = new LinkedList<>();

	/**
	 * Adds an {@link Assert} belonging to {@link TerminalNode}.
	 * 
	 * @param terminal
	 *            {@link TerminalNode} the {@link Assert} belongs to
	 * @param plus
	 *            {@link Assert} to add
	 */
	public void addAssert(final TerminalNode terminal, final Assert plus) {
		final Translated rule = terminal.getRule();
		network.getLogFormatter().messageRuleActivation(network, rule, plus);
		this.rulesAndTokens.add(new RuleAndToken(rule, plus));
	}

	/**
	 * Adds a {@link Retract} belonging to {@link TerminalNode}.
	 * 
	 * @param terminal
	 *            {@link TerminalNode} the {@link Retract} belongs to
	 * @param minus
	 *            {@link Retract} to add
	 */
	public void addRetract(final TerminalNode terminal, final Retract minus) {
		final Translated rule = terminal.getRule();
		network.getLogFormatter().messageRuleDeactivation(network, rule, minus);
		this.rulesAndTokens.add(new RuleAndToken(rule, minus));
	}

	/**
	 * Deletes all asserts and retracts.
	 */
	public void flush() {
		this.rulesAndTokens.clear();
	}

	/**
	 * Deletes all revoked asserts and all retracts.
	 */
	public void deleteRevokedEntries() {
		final Iterator<RuleAndToken> iterator = this.rulesAndTokens.iterator();
		while (iterator.hasNext()) {
			final RuleAndToken nodeAndToken = iterator.next();
			final AssertOrRetract<?> token = nodeAndToken.getToken();
			if (token.isRevokedOrMinus()) {
				iterator.remove();
			}
		}
	}

	@Override
	public Iterator<RuleAndToken> iterator() {
		return this.rulesAndTokens.iterator();
	}

	public boolean remove(final RuleAndToken ruleAndToken) {
		return this.rulesAndTokens.remove(ruleAndToken);
	}
}
