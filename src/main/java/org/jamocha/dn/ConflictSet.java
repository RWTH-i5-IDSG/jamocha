/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.apache.commons.lang3.ArrayUtils;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.Translated;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.nodes.TerminalNode;

import java.util.*;
import java.util.Map.Entry;

import static org.jamocha.util.ToArray.toArray;

/**
 * Simple conflict set implementation.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ConflictSet {

    private final SideEffectFunctionToNetwork network;
    private long activationCounter = 0;
    private final Random random = new Random();
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
        int[] recencyArray;

        RuleAndToken(final Defrule.Translated rule, final AssertOrRetract<?> token, final long activationCounter,
                final int random) {
            this.rule = rule;
            this.token = token;
            this.activationCounter = activationCounter;
            this.random = random;
            this.recencyArray =
                    Arrays.stream(token.getFactIdentifiers()).mapToInt(fi -> null == fi ? -1 : fi.getId()).sorted()
                            .toArray();
            ArrayUtils.reverse(this.recencyArray);
        }
    }

    private final TreeMap<Integer, TreeSet<RuleAndToken>> rulesAndTokensBySalience = new TreeMap<>();

    private TreeSet<RuleAndToken> getRATSet(final Integer salience) {
        final TreeSet<RuleAndToken> ratSet = this.rulesAndTokensBySalience
                .computeIfAbsent(salience, x -> new TreeSet<>(this.conflictResolutionStrategy));
        return correctStrategy(salience, ratSet);
    }

    private TreeSet<RuleAndToken> correctStrategy(final Integer salience, final TreeSet<RuleAndToken> ratSet) {
        if (this.conflictResolutionStrategy == ratSet.comparator()) return ratSet;
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
     *         {@link TerminalNode} the {@link Assert} belongs to
     * @param plus
     *         {@link Assert} to add
     */
    public synchronized void addAssert(final TerminalNode terminal, final Assert plus) {
        if (plus.getMem().size() <= 0) return;
        final Translated rule = terminal.getRule();
        this.network.getLogFormatter().messageRuleActivation(this.network, rule, plus);
        getRATSet(rule.getParent().getSalience())
                .add(new RuleAndToken(rule, plus, ++this.activationCounter, this.random.nextInt()));
    }

    /**
     * Adds a {@link Retract} belonging to {@link TerminalNode}.
     *
     * @param terminal
     *         {@link TerminalNode} the {@link Retract} belongs to
     * @param minus
     *         {@link Retract} to add
     */
    public synchronized void addRetract(final TerminalNode terminal, final Retract minus) {
        if (minus.getMem().size() <= 0) return;
        final Translated rule = terminal.getRule();
        this.network.getLogFormatter().messageRuleDeactivation(this.network, rule, minus);
        getRATSet(rule.getParent().getSalience())
                .add(new RuleAndToken(rule, minus, ++this.activationCounter, this.random.nextInt()));
    }

    /**
     * Deletes all asserts and retracts.
     */
    public synchronized void flush() {
        this.rulesAndTokensBySalience.clear();
    }

    /**
     * Deletes all revoked asserts and all retracts.
     */
    public synchronized void deleteRevokedEntries() {
        for (final Iterator<Entry<Integer, TreeSet<RuleAndToken>>> entryIterator =
                this.rulesAndTokensBySalience.entrySet().iterator(); entryIterator.hasNext(); ) {
            final Entry<Integer, TreeSet<RuleAndToken>> entry = entryIterator.next();
            final TreeSet<RuleAndToken> rulesAndTokens = entry.getValue();
            final Iterator<RuleAndToken> iterator = rulesAndTokens.iterator();
            while (iterator.hasNext()) {
                final RuleAndToken nodeAndToken = iterator.next();
                final AssertOrRetract<?> token = nodeAndToken.getToken();
                if (token.isRevokedOrMinus() || 0 == token.getMem().size()) {
                    iterator.remove();
                }
            }
            if (rulesAndTokens.isEmpty()) {
                entryIterator.remove();
            }
        }
    }

    public synchronized RuleAndToken[] getAllRulesAndTokens() {
        return toArray(this.rulesAndTokensBySalience.values().stream().flatMap(TreeSet::stream), RuleAndToken[]::new);
    }

    public synchronized TreeSet<RuleAndToken> getConflictingRulesAndTokensForMaxSalience() {
        if (this.rulesAndTokensBySalience.isEmpty()) return null;
        final Entry<Integer, TreeSet<RuleAndToken>> lastEntry = this.rulesAndTokensBySalience.lastEntry();
        return correctStrategy(lastEntry.getKey(), lastEntry.getValue());
    }

    public synchronized RuleAndToken getCurrentlySelectedRuleAndToken() {
        final TreeSet<RuleAndToken> conflictingRulesAndTokensForMaxSalience =
                getConflictingRulesAndTokensForMaxSalience();
        if (null == conflictingRulesAndTokensForMaxSalience || conflictingRulesAndTokensForMaxSalience.isEmpty()) {
            return null;
        }
        return conflictingRulesAndTokensForMaxSalience.last();
    }

    public synchronized boolean remove(final RuleAndToken ruleAndToken) {
        final Integer salience = ruleAndToken.rule.getParent().getSalience();
        final TreeSet<RuleAndToken> ratSet = this.rulesAndTokensBySalience.get(salience);
        if (null == ratSet) return false;
        final boolean removed = ratSet.remove(ruleAndToken);
        if (ratSet.isEmpty()) {
            this.rulesAndTokensBySalience.remove(salience);
        }
        return removed;
    }
}
