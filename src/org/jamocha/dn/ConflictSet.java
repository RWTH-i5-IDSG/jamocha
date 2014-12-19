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

import static org.jamocha.util.ToArray.toArray;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;
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
public class ConflictSet {

	private final SideEffectFunctionToNetwork network;
	private long activationCounter = 0;
	private Random random = new Random();
	@Getter
	@Setter
	private ConflictResolutionStrategy conflictResolutionStrategy;

	public ConflictSet(final SideEffectFunctionToNetwork network,
			final ConflictResolutionStrategy conflictResolutionStrategy) {
		this.network = network;
		this.conflictResolutionStrategy = conflictResolutionStrategy;
	}

	/**
	 * Combination of {@link TerminalNode} and {@link AssertOrRetract}.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	public static class RuleAndToken {
		Defrule.Translated rule;
		AssertOrRetract<?> token;
		long activationCounter;
		int random;
	}

	private final TreeMap<Integer, TreeSet<RuleAndToken>> rulesAndTokensBySalience = new TreeMap<>();

	private TreeSet<RuleAndToken> getRATSet(final Integer salience) {
		final TreeSet<RuleAndToken> ratSet =
				this.rulesAndTokensBySalience.computeIfAbsent(salience, x -> new TreeSet<>(
						this.conflictResolutionStrategy));
		return correctStrategy(salience, ratSet);
	}

	private TreeSet<RuleAndToken> correctStrategy(final Integer salience, final TreeSet<RuleAndToken> ratSet) {
		if (conflictResolutionStrategy == ratSet.comparator())
			return ratSet;
		// if wrong comparator was used, create new set and copy the values
		final TreeSet<RuleAndToken> newRatSet = new TreeSet<>(this.conflictResolutionStrategy);
		newRatSet.addAll(ratSet);
		this.rulesAndTokensBySalience.put(salience, newRatSet);
		return newRatSet;
	}

	/**
	 * Adds an {@link Assert} belonging to {@link TerminalNode}.
	 * 
	 * @param terminal
	 *            {@link TerminalNode} the {@link Assert} belongs to
	 * @param plus
	 *            {@link Assert} to add
	 */
	synchronized public void addAssert(final TerminalNode terminal, final Assert plus) {
		final Translated rule = terminal.getRule();
		network.getLogFormatter().messageRuleActivation(network, rule, plus);
		getRATSet(rule.getParent().getSalience()).add(
				new RuleAndToken(rule, plus, ++activationCounter, random.nextInt()));
	}

	/**
	 * Adds a {@link Retract} belonging to {@link TerminalNode}.
	 * 
	 * @param terminal
	 *            {@link TerminalNode} the {@link Retract} belongs to
	 * @param minus
	 *            {@link Retract} to add
	 */
	synchronized public void addRetract(final TerminalNode terminal, final Retract minus) {
		final Translated rule = terminal.getRule();
		network.getLogFormatter().messageRuleDeactivation(network, rule, minus);
		getRATSet(rule.getParent().getSalience()).add(
				new RuleAndToken(rule, minus, ++activationCounter, random.nextInt()));
	}

	/**
	 * Deletes all asserts and retracts.
	 */
	synchronized public void flush() {
		this.rulesAndTokensBySalience.clear();
	}

	/**
	 * Deletes all revoked asserts and all retracts.
	 */
	synchronized public void deleteRevokedEntries() {
		for (final TreeSet<RuleAndToken> rulesAndTokens : this.rulesAndTokensBySalience.values()) {
			final Iterator<RuleAndToken> iterator = rulesAndTokens.iterator();
			while (iterator.hasNext()) {
				final RuleAndToken nodeAndToken = iterator.next();
				final AssertOrRetract<?> token = nodeAndToken.getToken();
				if (token.isRevokedOrMinus()) {
					iterator.remove();
				}
			}
		}
	}

	synchronized public RuleAndToken[] getAllRulesAndTokens() {
		return toArray(this.rulesAndTokensBySalience.values().stream().flatMap(TreeSet::stream), RuleAndToken[]::new);
	}

	synchronized public TreeSet<RuleAndToken> getConflictingRulesAndTokensForMaxSalience() {
		if (this.rulesAndTokensBySalience.isEmpty())
			return null;
		final Entry<Integer, TreeSet<RuleAndToken>> lastEntry = this.rulesAndTokensBySalience.lastEntry();
		return correctStrategy(lastEntry.getKey(), lastEntry.getValue());
	}

	synchronized public RuleAndToken getCurrentlySelectedRuleAndToken() {
		final TreeSet<RuleAndToken> conflictingRulesAndTokensForMaxSalience =
				getConflictingRulesAndTokensForMaxSalience();
		if (null == conflictingRulesAndTokensForMaxSalience || conflictingRulesAndTokensForMaxSalience.isEmpty())
			return null;
		return conflictingRulesAndTokensForMaxSalience.last();
	}

	synchronized public boolean remove(final RuleAndToken ruleAndToken) {
		final Integer salience = ruleAndToken.rule.getParent().getSalience();
		final TreeSet<RuleAndToken> ratSet = this.rulesAndTokensBySalience.get(salience);
		if (null == ratSet)
			return false;
		final boolean removed = ratSet.remove(ruleAndToken);
		if (ratSet.isEmpty()) {
			this.rulesAndTokensBySalience.remove(salience);
		}
		return removed;
	}
}
