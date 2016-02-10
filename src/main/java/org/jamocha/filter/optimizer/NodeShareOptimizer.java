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
package org.jamocha.filter.optimizer;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathNodeFilterSet;

import com.google.common.collect.Sets;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class NodeShareOptimizer implements Optimizer {

    final Map<Path, Set<PathNodeFilterSet>> path2Filters = new HashMap<>();

    final Map<PathNodeFilterSet, Set<Set<PathNodeFilterSet>>> filter2Blocks = new HashMap<>();

    final Map<PathNodeFilterSet, Set<Path>> filter2Paths = new HashMap<>();

    final Map<PathNodeFilterSet, Defrule.PathRule> filter2Rule = new HashMap<>();

    final Set<Defrule.PathRule> allRules = new HashSet<>();

    final PathFilterPool pool = new PathFilterPool();

    @Override
    public Collection<PathRule> optimize(final Collection<PathRule> rules) {
        for (final Defrule.PathRule pathRule : rules) {
            allRules.add(pathRule);
            for (final PathNodeFilterSet pathFilter : pathRule.getCondition()) {
                pool.addFilter(pathFilter);
                filter2Rule.put(pathFilter, pathRule);
                final Set<Path> paths = PathCollector.newHashSet().collectAllInLists(pathFilter).getPaths();
                paths.addAll(pathFilter.getNegativeExistentialPaths());
                paths.addAll(pathFilter.getPositiveExistentialPaths());
                filter2Paths.put(pathFilter, paths);
                for (final Path path : paths) {
                    final Set<PathNodeFilterSet> set = path2Filters.computeIfAbsent(path, p -> new HashSet<>());
                    set.add(pathFilter);
                }
            }
        }
        for (final Defrule.PathRule pathRule : rules) {
            for (final PathNodeFilterSet pathFilter : pathRule.getCondition()) {
                buildBlock(pathRule, pathFilter);
            }
        }
        // TBD perform actual optimisation
        return rules;
    }

    private void buildBlock(final Defrule.PathRule rule, final PathNodeFilterSet pathFilter) {
        final Set<PathNodeFilterSet> preBlock = new HashSet<>();
        final Map<Defrule.PathRule, Map<Path, Path>> rule2PathMap = new HashMap<>();
        preBlock.add(pathFilter);
        // add all filters producing conflicts in the same rule
        {
            Set<PathNodeFilterSet> newFilters = new HashSet<>();
            newFilters.add(pathFilter);
            while (!newFilters.isEmpty()) {
                final Set<PathNodeFilterSet> conflictFilters = new HashSet<>();
                for (final PathNodeFilterSet newFilter : newFilters) {
                    for (final Path path : filter2Paths.get(newFilter)) {
                        conflictFilters.addAll(path2Filters.getOrDefault((path), Collections.emptySet()));
                    }
                }
                conflictFilters.removeAll(preBlock);
                newFilters = new HashSet<>();
                for (final PathNodeFilterSet conflictFilter : conflictFilters) {
                    final Set<PathNodeFilterSet> conflictingInBlock =
                            filter2Paths.get(conflictFilter).stream().flatMap(p -> path2Filters.get(p).stream())
                                    .filter(f -> preBlock.contains(f)).collect(toSet());
                    if (!filter2Blocks.get(conflictFilter).stream().allMatch(b -> b.containsAll(conflictingInBlock))) {
                        newFilters.add(conflictFilter);
                        preBlock.add(conflictFilter);
                    }
                }
            }
        }
        // Add all rules which do not produce conflicts
        {
            final Set<PathNodeFilterSet> preBlockAdds = new HashSet<>();
            final Set<Defrule.PathRule> possibleRules = new HashSet<>();
            possibleRules.addAll(allRules);
            for (final PathNodeFilterSet filter : preBlock) {
                possibleRules
                        .retainAll(pool.getEqualFilters(filter).stream().map(f -> filter2Rule.get(f)).collect(toSet()));
            }
            for (final Defrule.PathRule possibleRule : possibleRules) {
                final Map<Path, Path> pathMap = new HashMap<>();
                final Map<PathNodeFilterSet, PathNodeFilterSet> filterMap =
                        comparePathFilters(preBlock, Sets.newHashSet(possibleRule.getCondition()), pathMap);
                if (null != filterMap && checkForConflicts(filterMap.values())) {
                    preBlockAdds.addAll(filterMap.values());
                    rule2PathMap.put(possibleRule, pathMap);
                }
            }
        }
        {
            for (final PathNodeFilterSet filter : rule.getCondition()) {
                if (preBlock.contains(filter)) continue;
                for (final Defrule.PathRule otherRule : rule2PathMap.keySet()) {
                    // TBD hier weiterarbeiten
                    otherRule.toString();
                }
            }
        }
    }

    private boolean checkForConflicts(final Collection<PathNodeFilterSet> values) {
        return values.stream().flatMap(filter -> (Stream<Boolean>) (filter2Paths.get(filter).stream()
                .map(path -> path2Filters.get(path).stream().allMatch(
                        conflictingFilter -> filter2Blocks.get(conflictingFilter).stream()
                                .allMatch(block -> block.contains(filter)))))).allMatch(noConflict -> noConflict);
    }

    private Map<PathNodeFilterSet, PathNodeFilterSet> comparePathFilters(final Set<PathNodeFilterSet> filters1,
            final Set<PathNodeFilterSet> filters2) {
        return comparePathFilters(filters1, filters2, new HashMap<>());
    }

    private Map<PathNodeFilterSet, PathNodeFilterSet> comparePathFilters(final Set<PathNodeFilterSet> filters1,
            final Set<PathNodeFilterSet> filters2, final Map<Path, Path> pathMap) {
        if (filters1.size() == 0) return new HashMap<>();
        final Iterator<PathNodeFilterSet> iterator = filters1.iterator();
        final PathNodeFilterSet filter1 = iterator.next();
        iterator.remove();
        try {
            for (final PathNodeFilterSet filter2 : IteratorUtils
                    .asIterable(pool.getEqualFilters(filter1).stream().filter(f -> filters2.contains(f)).iterator())) {
                final Map<Path, Path> tmpPathMap = new HashMap<>(pathMap);
                if (PathNodeFilterSet.equals(filter1, filter2, tmpPathMap)) {
                    final Map<PathNodeFilterSet, PathNodeFilterSet> filterMap =
                            comparePathFilters(filters1, filters2, tmpPathMap);
                    if (null != filterMap) {
                        filterMap.put(filter1, filter2);
                        return filterMap;
                    }
                }
            }
            return null;
        } finally {
            filters1.add(filter1);
        }
    }

    private class PathFilterPool {

        Map<Integer, Set<Set<PathNodeFilterSet>>> pool = new HashMap<>();

        public Set<PathNodeFilterSet> getEqualFilters(final PathNodeFilterSet filter) {
            final Set<Set<PathNodeFilterSet>> sets = pool.get(filter.getHashCode());
            for (final Set<PathNodeFilterSet> set : sets) {
                if (set.contains(filter)) return set;
                if (PathNodeFilterSet.equals(set.iterator().next(), filter)) return set;
            }
            return null;
        }

        public void addFilter(final PathNodeFilterSet filter) {
            final Set<Set<PathNodeFilterSet>> setOfSets = pool.get(filter.getHashCode());
            if (null == setOfSets) {
                final Set<Set<PathNodeFilterSet>> newSetOfSets = new HashSet<>();
                final Set<PathNodeFilterSet> newSet = new HashSet<>();
                newSet.add(filter);
                newSetOfSets.add(newSet);
                pool.put(filter.getHashCode(), newSetOfSets);
                return;
            }
            for (final Set<PathNodeFilterSet> set : setOfSets) {
                if (PathNodeFilterSet.equals(set.iterator().next(), filter)) {
                    set.add(filter);
                    return;
                }
            }
            final Set<PathNodeFilterSet> newSet = new HashSet<>();
            newSet.add(filter);
            setOfSets.add(newSet);
        }

    }

    private class Block {
        final Map<Defrule.PathRule, Set<PathNodeFilterSet>> rule2PathFilters = new HashMap<>();

        void addFilter(final Defrule.PathRule rule, final PathNodeFilterSet filter) {
            Set<PathNodeFilterSet> filters = rule2PathFilters.get(rule);
            if (null == filters) {
                filters = new HashSet<>();
                rule2PathFilters.put(rule, filters);
            }
            filters.add(filter);
        }
    }

}
