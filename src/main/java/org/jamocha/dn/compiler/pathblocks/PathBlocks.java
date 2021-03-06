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
package org.jamocha.dn.compiler.pathblocks;

import com.google.common.collect.*;
import com.google.common.collect.Sets.SetView;
import io.atlassian.fugue.Either;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.collections4.list.CursorableLinkedList;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.ConstructCache.Defrule.PathSetRule;
import org.jamocha.dn.compiler.pathblocks.PathBlocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.pathblocks.PathBlocks.Filter.FilterInstance.Conflict;
import org.jamocha.filter.*;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.function.fwatransformer.FWAPathLeafToTemplateSlotLeafTranslator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.VertexCovers;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
public class PathBlocks {

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode(of = {"predicate"})
    @ToString(of = {"predicate"})
    static class Filter {
        final FunctionWithArguments<TemplateSlotLeaf> predicate;
        final Map<Either<Rule, ExistentialProxy>, Set<FilterInstance>> ruleToInstances = new HashMap<>();

        static final Map<Filter, Filter> CACHE = new HashMap<>();

        static Filter newFilter(final FunctionWithArguments<TemplateSlotLeaf> predicate) {
            return CACHE.computeIfAbsent(new Filter(predicate), Function.identity());
        }

        public FilterInstance addInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
                final PathFilter pathFilter) {
            final ArrayList<Path> parameterPaths = OrderedPathCollector.collect(pathFilter.getFunction());
            final FilterInstance instance = new FilterInstance(pathFilter, parameterPaths, ruleOrProxy);
            this.ruleToInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
            return instance;
        }

        public Set<FilterInstance> getInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
            return this.ruleToInstances.computeIfAbsent(ruleOrProxy, newHashSet());
        }

        public PathFilterList convert(final FilterInstance instance) {
            return PathNodeFilterSet.newRegularPathNodeFilterSet(instance.pathFilter);
        }

        /**
         * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
         */
        @Getter
        @Setter
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
                // no EqualsAndHashCode
        class FilterInstance {
            final PathFilter pathFilter;
            final List<Path> parameters;
            final Either<Rule, ExistentialProxy> ruleOrProxy;
            final Map<FilterInstance, Conflict> conflicts = new HashMap<>();

            @Override
            public String toString() {
                return Objects.toString(this.pathFilter);
            }

            public Conflict addConflict(final FilterInstance targetFilterInstance) {
                final Conflict conflict = new Conflict(targetFilterInstance);
                if (conflict.samePathsIndices.isEmpty()) {
                    this.conflicts.put(targetFilterInstance, null);
                    return null;
                }
                this.conflicts.put(targetFilterInstance, conflict);
                return conflict;
            }

            public Conflict getOrDetermineConflicts(final FilterInstance targetFilterInstance) {
                // call to containsKey prevents recalculation of null conflicts
                // (design currently doesn't easily allow for a better null-object)
                return this.conflicts.containsKey(targetFilterInstance) ? this.conflicts.get(targetFilterInstance)
                        : addConflict(targetFilterInstance);
            }

            public Filter getFilter() {
                return Filter.this;
            }

            public PathFilterList convert() {
                return getFilter().convert(this);
            }

            /**
             * Returns the filter instances of the same filter within the same rule (result contains the filter INSTANCE
             * this method is called upon).
             *
             * @return the filter instances of the same filter within the same rule
             */
            public Set<FilterInstance> getSiblings() {
                return getInstances(this.ruleOrProxy);
            }

            /**
             * A conflict represents the fact that two filter instances are using the same data (possibly on different
             * parameter positions). The parameters of the enclosing INSTANCE are compared to the parameters of the
             * given target INSTANCE. <p> It holds for every pair c in {@code samePathsIndices} that parameter {@code
             * c.left} of the enclosing filter INSTANCE uses the same {@link Path} as parameter {@code c.right} of the
             * target filter INSTANCE.
             *
             * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
             */
            @RequiredArgsConstructor
            @Getter
            class Conflict {
                // left refers to the source, right to the target of the conflicts
                final Set<Pair<Integer, Integer>> samePathsIndices;
                final FilterInstance target;

                Conflict(final FilterInstance target) {
                    this.target = target;
                    this.samePathsIndices = new HashSet<>();
                    final List<Path> sourceParameters = FilterInstance.this.parameters;
                    final List<Path> targetParameters = target.parameters;
                    final Map<Path, List<Integer>> targetPathIndices =
                            IntStream.range(0, targetParameters.size()).boxed()
                                    .collect(groupingBy(targetParameters::get));
                    final int size = sourceParameters.size();
                    for (int i = 0; i < size; ++i) {
                        final Integer oi = Integer.valueOf(i);
                        for (final Integer ji : targetPathIndices
                                .getOrDefault(sourceParameters.get(oi), Collections.emptyList())) {
                            this.samePathsIndices.add(Pair.of(oi, ji));
                        }
                    }
                }

                public boolean hasEqualConflicts(final Conflict other) {
                    if (null == other) return false;
                    if (this == other) return true;
                    return this.samePathsIndices == other.samePathsIndices || (this.samePathsIndices != null
                            && this.samePathsIndices.equals(other.samePathsIndices));
                }

                public FilterInstance getSource() {
                    return FilterInstance.this;
                }
            }
        }
    }

    @Getter
    static final class FilterProxy extends Filter {
        final Set<ExistentialProxy> proxies;

        private FilterProxy(final FunctionWithArguments<TemplateSlotLeaf> predicate, final ExistentialProxy proxy) {
            super(predicate);
            this.proxies = Sets.newHashSet(proxy);
        }

        static final Map<FilterProxy, FilterProxy> PROXY_CACHE = new HashMap<>();

        static FilterProxy newFilterProxy(final FunctionWithArguments<TemplateSlotLeaf> predicate,
                final ExistentialProxy proxy) {
            return PROXY_CACHE.computeIfAbsent(new FilterProxy(predicate, proxy), Function.identity());
        }

        static Set<FilterProxy> getFilterProxies() {
            return PROXY_CACHE.keySet();
        }

        @Override
        public PathFilterList convert(final FilterInstance instance) {
            assert instance.getRuleOrProxy().isLeft() : "Nested Existentials Unsupported!";
            final Rule rule = instance.getRuleOrProxy().left().get();
            final ExistentialProxy existentialProxy = rule.getExistentialProxies().get(instance);
            final PathExistentialSet existential = existentialProxy.getExistential();
            return new PathFilterList.PathExistentialList(existential.getInitialPath(),
                    PathFilterList.toSimpleList(Collections.emptyList()), PathNodeFilterSet
                    .newExistentialPathNodeFilterSet(!existential.isPositive(), existential.getExistentialPaths(),
                            instance.getPathFilter()));
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof FilterProxy;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof FilterProxy)) return false;
            final FilterProxy other = (FilterProxy) o;
            if (!other.canEqual(this)) return false;
            if (!super.equals(other)) return false;
            return equalProxies(this, other);
        }

        private static boolean equalProxies(final FilterProxy aFilterProxy, final FilterProxy bFilterProxy) {
            final ExistentialProxy aProxy = aFilterProxy.proxies.iterator().next();
            final ExistentialProxy bProxy = bFilterProxy.proxies.iterator().next();
            if (aProxy.existential.getExistentialPaths().size() != bProxy.existential.getExistentialPaths().size()) {
                return false;
            }
            if (aProxy.existential.isPositive() != bProxy.existential.isPositive()) return false;
            final Set<Filter> aFilters = aProxy.getFilters();
            final Set<Filter> bFilters = bProxy.getFilters();
            if (aFilters.size() != bFilters.size()) return false;
            if (aFilters.size() == 0) {
                return true;
            }

            final List<Set<FilterInstance>> aFilterInstanceSets =
                    aFilters.stream().map(f -> f.getInstances(Either.right(aProxy)))
                            .collect(toCollection(ArrayList::new));
            aFilterInstanceSets.add(Collections.singleton(aProxy.getExistentialClosure()));
            final List<Set<FilterInstance>> bFilterInstanceSets =
                    bFilters.stream().map(f -> f.getInstances(Either.right(bProxy)))
                            .collect(toCollection(ArrayList::new));
            bFilterInstanceSets.add(Collections.singleton(bProxy.getExistentialClosure()));

            final List<FilterInstance> aFlatFilterInstances =
                    aFilterInstanceSets.stream().flatMap(Set::stream).collect(toList());
            final List<FilterInstance> bFlatFilterInstances =
                    bFilterInstanceSets.stream().flatMap(Set::stream).collect(toList());
            final UndirectedGraph<FilterInstance, ConflictEdge> graph =
                    determineConflictGraph(ImmutableSet.of(aFlatFilterInstances, bFlatFilterInstances));

            final Set<List<List<FilterInstance>>> cartesianProduct = Sets.cartesianProduct(bFilterInstanceSets.stream()
                    .map(set -> Sets.newHashSet(new PermutationIterator<FilterInstance>(set))).collect(toList()));

            final HashMap<FilterInstance, Pair<Integer, Integer>> aFI2IndexPair = new HashMap<>();
            {
                int i = 0;
                for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
                    int j = 0;
                    for (final FilterInstance filterInstance : aCell) {
                        aFI2IndexPair.put(filterInstance, Pair.of(i, j));
                        ++j;
                    }
                    ++i;
                }
            }
            bijectionLoop:
            for (final List<List<FilterInstance>> bijection : cartesianProduct) {
                int i = 0;
                for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
                    final List<FilterInstance> bCell = bijection.get(i);
                    int j = 0;
                    for (final FilterInstance aSource : aCell) {
                        final FilterInstance bSource = bCell.get(j);
                        final Set<ConflictEdge> aConflicts = graph.edgesOf(aSource);
                        for (final ConflictEdge edge : aConflicts) {
                            final Conflict aConflict = edge.getForSource(aSource);
                            final Pair<Integer, Integer> indexPair = aFI2IndexPair.get(aConflict.getTarget());
                            final FilterInstance bTarget = bijection.get(indexPair.getLeft()).get(indexPair.getRight());
                            final Conflict bConflict = bSource.getOrDetermineConflicts(bTarget);
                            if (!hasEqualConflicts(aConflict, bConflict)) {
                                continue bijectionLoop;
                            }
                        }
                        ++j;
                    }
                    ++i;
                }
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            final int prime = 59;
            int result = 1;
            result = (result * prime) + super.hashCode();
            result = (result * prime) + (this.proxies == null ? 0 : (this.proxies.iterator().next().filters == null ? 0
                    : this.proxies.iterator().next().filters.hashCode()));
            return result;
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @AllArgsConstructor
    @Getter
    static class FilterInstancesSideBySide {
        final LinkedHashSet<FilterInstance> instances;
        final Filter filter;
        final Either<Rule, ExistentialProxy> ruleOrProxy;

        FilterInstancesSideBySide(final LinkedHashSet<FilterInstance> stacks) {
            this(stacks, stacks.iterator().next().getFilter(), stacks.iterator().next().getRuleOrProxy());
            assert 1 == stacks.stream().map(FilterInstance::getFilter).collect(toSet()).size();
        }

        FilterInstancesSideBySide(final FilterInstance stack) {
            this(new LinkedHashSet<>(Collections.singleton(stack)), stack.getFilter(), stack.getRuleOrProxy());
        }

        @Override
        public String toString() {
            return Objects.toString(this.instances);
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @RequiredArgsConstructor
    @Getter
    static class Rule {
        final PathSetRule original;
        final Set<Filter> filters = new HashSet<>();
        final BiMap<FilterInstance, ExistentialProxy> existentialProxies = HashBiMap.create();

        @Override
        public String toString() {
            return this.original.getParent().getName();
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @RequiredArgsConstructor
    @Getter
    @ToString(of = {"rule", "filters"})
    static class ExistentialProxy {
        final Rule rule;
        final PathExistentialSet existential;
        final Set<Filter> filters = new HashSet<>();

        public FilterInstance getExistentialClosure() {
            return this.rule.getExistentialProxies().inverse().get(this);
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @Getter
    @RequiredArgsConstructor
    public static class Block {
        // conflict graph
        final UndirectedGraph<FilterInstance, ConflictEdge> graph;
        // abstract filters of the block
        final Set<Filter> filters = new HashSet<>();
        // mapping from rule to cells in that row
        // keySet of this map implicitly gives all the rules
        final Map<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> ruleToFilterToRow =
                new HashMap<>();
        // contains the filterInstances of this block correctly arranged (side-by-side/stacked)
        final Set<FilterInstancesSideBySide> filterInstances = new HashSet<>();
        // contains the filterInstances without the correct arrangement, just to avoid having to
        // flat map the filterInstances every time
        final Set<FilterInstance> flatFilterInstances = new HashSet<>();
        // conflicts between filter instances, where the source has to be outside and the target
        // inside of the block, grouped by the one outside
        final Map<FilterInstance, Set<ConflictEdge>> borderConflicts = new HashMap<>();

        public Block(final Block block) {
            this.graph = block.graph;
            this.filters.addAll(block.filters);
            for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> ruleAndMap
                    : block.ruleToFilterToRow.entrySet()) {
                this.ruleToFilterToRow.put(ruleAndMap.getKey(), new HashMap<>(ruleAndMap.getValue()));
            }
            this.filterInstances.addAll(block.filterInstances);
            this.flatFilterInstances.addAll(block.flatFilterInstances);
            for (final Entry<FilterInstance, Set<ConflictEdge>> entry : block.borderConflicts.entrySet()) {
                this.borderConflicts.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
        }

        @Override
        public String toString() {
            return "Block: " + Objects.toString(this.filterInstances);
        }

        Set<Either<Rule, ExistentialProxy>> getRulesOrProxies() {
            return this.ruleToFilterToRow.keySet();
        }

        Set<Rule> getActualRuleInstances() {
            return this.ruleToFilterToRow.keySet().stream().filter(Either::isLeft).map(a -> a.left().get())
                    .collect(toSet());
        }

        Set<ExistentialProxy> getExistentialProxies() {
            return this.ruleToFilterToRow.keySet().stream().filter(Either::isRight).map(a -> a.right().get())
                    .collect(toSet());
        }

        public int getNumberOfColumns() {
            return (this.flatFilterInstances.size() - this.filterInstances.size()) / getRulesOrProxies().size()
                    + this.filters.size();
        }

        public void addFilterInstance(final Either<Rule, ExistentialProxy> rule, final FilterInstance filterInstance) {
            addFilterInstances(Collections
                    .singletonMap(rule, Collections.singleton(new FilterInstancesSideBySide(filterInstance))));
        }

        public void addFilterInstances(final Either<Rule, ExistentialProxy> rule,
                final FilterInstancesSideBySide sideBySides) {
            addFilterInstances(Collections.singletonMap(rule, Collections.singleton(sideBySides)));
        }

        public void addFilterInstances(
                final Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>>
                        ruleToNewFilterInstancesSideBySide) {
            for (final Entry<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> entry
                    : ruleToNewFilterInstancesSideBySide.entrySet()) {
                final Either<Rule, ExistentialProxy> rule = entry.getKey();
                final Set<FilterInstancesSideBySide> sideBySides = entry.getValue();
                for (final FilterInstancesSideBySide sideBySide : sideBySides) {
                    for (final FilterInstance prevOutside : sideBySide.instances) {
                        final Set<ConflictEdge> newConflicts = new HashSet<>(this.graph.edgesOf(prevOutside));
                        final Set<ConflictEdge> oldConflicts = this.borderConflicts.remove(prevOutside);
                        if (null != oldConflicts) {
                            newConflicts.removeAll(oldConflicts);
                        }
                        // group conflict edges by nodes that are outside now
                        for (final ConflictEdge conflictEdge : newConflicts) {
                            this.borderConflicts
                                    .computeIfAbsent(conflictEdge.getForSource(prevOutside).getTarget(), newHashSet())
                                    .add(conflictEdge);
                        }
                    }
                    final Map<Filter, FilterInstancesSideBySide> filterInstancesOfThisRule =
                            this.ruleToFilterToRow.computeIfAbsent(rule, newHashMap());
                    final Filter filter = sideBySide.filter;
                    final FilterInstancesSideBySide presentSideBySide = filterInstancesOfThisRule.get(filter);
                    if (null != presentSideBySide) {
                        filterInstancesOfThisRule.remove(presentSideBySide.getFilter(), presentSideBySide);
                        this.filterInstances.remove(presentSideBySide);
                        final LinkedHashSet<FilterInstance> instances = new LinkedHashSet<FilterInstance>();
                        instances.addAll(presentSideBySide.getInstances());
                        instances.addAll(sideBySide.getInstances());
                        final FilterInstancesSideBySide newSideBySide = new FilterInstancesSideBySide(instances);
                        filterInstancesOfThisRule.put(filter, newSideBySide);
                        this.filterInstances.add(newSideBySide);
                    } else {
                        filterInstancesOfThisRule.put(filter, sideBySide);
                        this.filterInstances.add(sideBySide);
                        this.filters.add(filter);
                    }
                    this.flatFilterInstances.addAll(sideBySide.getInstances());
                }
            }
        }

        public boolean containedIn(final Block other) {
            final Set<Either<Rule, ExistentialProxy>> otherRules = other.ruleToFilterToRow.keySet();
            final Set<Either<Rule, ExistentialProxy>> thisRules = this.ruleToFilterToRow.keySet();
            if (otherRules.size() < thisRules.size() || !otherRules.containsAll(thisRules)) {
                return false;
            }
            if (other.filters.size() < this.filters.size() || !other.filters.containsAll(this.filters)) {
                return false;
            }
            if (other.flatFilterInstances.size() < this.flatFilterInstances.size()) {
                return false;
            }
            if (other.flatFilterInstances.containsAll(this.flatFilterInstances)) {
                return true;
            }
            /*
             * before really considering multi cell filters, just check the sizes and containment
             * for single cell filters
             */
            final Set<Filter> multiFilters = new HashSet<>();
            for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> entry : this
                    .ruleToFilterToRow
                    .entrySet()) {
                final Map<Filter, FilterInstancesSideBySide> thisRow = entry.getValue();
                final Map<Filter, FilterInstancesSideBySide> otherRow = other.ruleToFilterToRow.get(entry.getKey());
                for (final Filter filter : this.filters) {
                    final LinkedHashSet<FilterInstance> thisFIs = thisRow.get(filter).getInstances();
                    final LinkedHashSet<FilterInstance> otherFIs = otherRow.get(filter).getInstances();
                    final int otherFIsCount = otherFIs.size();
                    final int thisFIsCount = thisFIs.size();
                    if (thisFIsCount > otherFIsCount) {
                        return false;
                    }
                    if (1 == thisFIsCount) {
                        if (!otherFIs.contains(thisFIs.iterator().next())) {
                            return false;
                        }
                    } else {
                        multiFilters.add(filter);
                    }
                }
            }
            if (multiFilters.isEmpty()) {
                return true;
            }
            /*
             * for every multi filter, we have to check whether the same filter instances are in the
             * corresponding columns, otherwise its not the same filter or this is not contained in
             * other
             */
            // since we will compare lists, fix the iteration order
            final List<Either<Rule, ExistentialProxy>> rules = new ArrayList<>(thisRules);
            final Set<List<FilterInstance>> thisFilterInstanceColumns =
                    getFilterInstanceColumns(multiFilters, this.ruleToFilterToRow, rules);
            final Set<List<FilterInstance>> otherFilterInstanceColumns =
                    getFilterInstanceColumns(multiFilters, other.ruleToFilterToRow, rules);
            for (final List<FilterInstance> thisFilterInstanceColumn : thisFilterInstanceColumns) {
                if (!otherFilterInstanceColumns.contains(thisFilterInstanceColumn)) {
                    return false;
                }
            }
            return true;
        }

        private static Set<List<FilterInstance>> getFilterInstanceColumns(final Set<Filter> filters,
                final Map<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> ruleToFilterToRow,
                final List<Either<Rule, ExistentialProxy>> rules) {
            final Set<List<FilterInstance>> filterInstanceColumns = new HashSet<>();
            for (final Filter filter : filters) {
                final List<Iterator<FilterInstance>> iterators = new ArrayList<>();
                for (final Either<Rule, ExistentialProxy> rule : rules) {
                    iterators.add(ruleToFilterToRow.get(rule).get(filter).getInstances().iterator());
                }
                final int size = ruleToFilterToRow.get(rules.get(0)).get(filter).getInstances().size();
                for (int i = 0; i < size; ++i) {
                    filterInstanceColumns.add(iterators.stream().map(Iterator::next).collect(toList()));
                }
            }
            return filterInstanceColumns;
        }
    }

    @Getter
    static class PathBlockSet {
        final HashSet<Block> blocks = new HashSet<>();
        final TreeMap<Integer, HashSet<Block>> ruleCountToBlocks = new TreeMap<>();
        final TreeMap<Integer, HashSet<Block>> filterCountToBlocks = new TreeMap<>();
        final HashMap<Either<Rule, ExistentialProxy>, HashSet<Block>> ruleInstanceToBlocks = new HashMap<>();
        final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> ruleCountToFilterCountToBlocks = new TreeMap<>();
        final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> filterCountToRuleCountToBlocks = new TreeMap<>();

        private static int getRuleCount(final Block block) {
            return block.getRulesOrProxies().size();
        }

        private static int getFilterCount(final Block block) {
            return block.getFlatFilterInstances().size() / getRuleCount(block);
        }

        private boolean addDuringHorizontalRecursion(final Block block) {
            final Integer ruleCount = getRuleCount(block);
            final Integer filterCount = getFilterCount(block);
            // first check if there is a block of the same height with more filter instances
            {
                final NavigableMap<Integer, HashSet<Block>> fixedRuleCountFilters =
                        this.ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
                                .tailMap(filterCount, true);
                for (final Set<Block> fixedFilterCountRule : fixedRuleCountFilters.values()) {
                    for (final Block candidate : fixedFilterCountRule) {
                        if (block.containedIn(candidate)) {
                            return false;
                        }
                    }
                }
            }
            // then check if there is a block of the same width with more rules
            {
                final NavigableMap<Integer, HashSet<Block>> fixedFilterCountRules =
                        this.filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
                                .tailMap(ruleCount, true);
                for (final Set<Block> fixedFilterCountRule : fixedFilterCountRules.values()) {
                    for (final Block candidate : fixedFilterCountRule) {
                        if (block.containedIn(candidate)) {
                            return false;
                        }
                    }
                }
            }
            // finally check if there is a block larger in both dimensions
            {
                for (final TreeMap<Integer, HashSet<Block>> fixedFilterCountMap : this.filterCountToRuleCountToBlocks
                        .tailMap(filterCount, false).values()) {
                    for (final HashSet<Block> candidates : fixedFilterCountMap.tailMap(ruleCount, false).values()) {
                        for (final Block candidate : candidates) {
                            if (block.containedIn(candidate)) {
                                return false;
                            }
                        }
                    }
                }
            }
            actuallyInsertBlockIntoAllCaches(block);
            return true;
        }

        private void removeContainedBlocks(final Block block) {
            final Integer ruleCount = getRuleCount(block);
            final Integer filterCount = getFilterCount(block);

            final List<Block> toRemove = new ArrayList<>();

            final Collection<TreeMap<Integer, HashSet<Block>>> filterCountToBlocksRuleCountHead =
                    this.ruleCountToFilterCountToBlocks.headMap(ruleCount, true).values();
            for (final TreeMap<Integer, HashSet<Block>> filterCountToBlocksFixedRuleCount
                    : filterCountToBlocksRuleCountHead) {
                final Collection<HashSet<Block>> blocksFixedRuleCountFilterCountHead =
                        filterCountToBlocksFixedRuleCount.headMap(filterCount, true).values();
                for (final HashSet<Block> blocksFixedRuleCountFixedFilterCount : blocksFixedRuleCountFilterCountHead) {
                    for (final Block candidate : blocksFixedRuleCountFixedFilterCount) {
                        if (candidate != block && candidate.containedIn(block)) {
                            // can't remove right now since we are iterating over a collection that
                            // would be changed
                            toRemove.add(candidate);
                        }
                    }
                }
            }
            for (final Block remove : toRemove) {
                remove(remove);
            }
        }

        private void actuallyInsertBlockIntoAllCaches(final Block block) {
            this.blocks.add(block);
            final Integer ruleCount = getRuleCount(block);
            final Integer filterCount = getFilterCount(block);
            this.ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).add(block);
            this.filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).add(block);
            this.ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
                    .computeIfAbsent(filterCount, newHashSet()).add(block);
            this.filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
                    .computeIfAbsent(ruleCount, newHashSet()).add(block);
            block.getRulesOrProxies()
                    .forEach(r -> this.ruleInstanceToBlocks.computeIfAbsent(r, newHashSet()).add(block));
            removeContainedBlocks(block);
        }

        public boolean isContained(final Block block) {
            final Integer ruleCount = getRuleCount(block);
            final Integer filterCount = getFilterCount(block);
            for (final TreeMap<Integer, HashSet<Block>> treeMap : this.filterCountToRuleCountToBlocks
                    .tailMap(filterCount).values()) {
                for (final HashSet<Block> blocks : treeMap.tailMap(ruleCount).values()) {
                    if (blocks.stream().anyMatch(block::containedIn)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean addDuringConflictResolution(final Block block) {
            if (isContained(block)) {
                return false;
            }
            actuallyInsertBlockIntoAllCaches(block);
            return true;
        }

        public boolean remove(final Block block) {
            if (!this.blocks.remove(block)) return false;
            final Integer ruleCount = getRuleCount(block);
            final Integer filterCount = getFilterCount(block);
            this.ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).remove(block);
            this.filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).remove(block);
            this.ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
                    .computeIfAbsent(filterCount, newHashSet()).remove(block);
            this.filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
                    .computeIfAbsent(ruleCount, newHashSet()).remove(block);
            block.getRulesOrProxies()
                    .forEach(r -> this.ruleInstanceToBlocks.computeIfAbsent(r, newHashSet()).remove(block));
            return true;
        }
    }

    protected static Set<Filter> getFilters(final Either<Rule, ExistentialProxy> ruleOrProxy) {
        return ruleOrProxy.fold(Rule::getFilters, ExistentialProxy::getFilters);
    }

    protected static void addRule(final PathSetRule pathBasedRule, final List<Either<Rule, ExistentialProxy>> rules) {
        final Rule rule = new Rule(pathBasedRule);
        // first step: create all filter instances
        final Set<PathFilterSet> condition = pathBasedRule.getCondition();
        final Either<Rule, ExistentialProxy> ruleEither = Either.left(rule);
        RuleConverter.convert(rules, ruleEither, condition);
        // from this point on, the rule won't change any more (aka the filters and the existential
        // proxies have been added) => it can be used as a key in a HashMap

        // second step: determine conflicts between filter instances according to the paths used
        determineAllConflicts(rule.filters.stream().flatMap(f -> f.getInstances(ruleEither).stream()).collect(toSet()));
        for (final ExistentialProxy proxy : rule.existentialProxies.values()) {
            final Either<Rule, ExistentialProxy> proxyEither = Either.right(proxy);
            determineAllConflicts(
                    proxy.filters.stream().flatMap(f -> f.getInstances(proxyEither).stream()).collect(toSet()));
        }
        // add rule to rule list
        rules.add(ruleEither);
    }

    protected static void determineAllConflicts(final Set<FilterInstance> filterInstances) {
        for (final FilterInstance source : filterInstances) {
            for (final FilterInstance target : filterInstances) {
                if (source == target) continue;
                source.addConflict(target);
            }
        }
    }

    protected static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraphForRules(
            final List<Either<Rule, ExistentialProxy>> ruleOrProxies) {
        return determineConflictGraph(ruleOrProxies.stream()
                .map(ruleOrProxy -> getFilters(ruleOrProxy).stream().flatMap(f -> f.getInstances(ruleOrProxy).stream())
                        .collect(toList())).collect(toList()));
    }

    protected static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraph(
            final Iterable<? extends List<FilterInstance>> filterInstancesGroupedByRule) {
        final UndirectedGraph<FilterInstance, ConflictEdge> graph = new SimpleGraph<>(ConflictEdge::of);
        for (final List<FilterInstance> instances : filterInstancesGroupedByRule) {
            for (final FilterInstance filterInstance : instances) {
                assert null != filterInstance;
                graph.addVertex(filterInstance);
            }
            // instances.forEach(graph::addVertex);
            final int numInstances = instances.size();
            for (int i = 0; i < numInstances; i++) {
                final FilterInstance fi1 = instances.get(i);
                for (int j = i + 1; j < numInstances; j++) {
                    final FilterInstance fi2 = instances.get(j);
                    final ConflictEdge edge = ConflictEdge.of(fi1, fi2);
                    if (null == edge) continue;
                    graph.addEdge(fi1, fi2, edge);
                }
            }
        }
        return graph;
    }

    @AllArgsConstructor
    static class RuleConverter implements PathFilterSetVisitor {
        final List<Either<Rule, ExistentialProxy>> rules;
        final Either<Rule, ExistentialProxy> ruleOrProxy;

        public static void convert(final List<Either<Rule, ExistentialProxy>> rules,
                final Either<Rule, ExistentialProxy> ruleOrProxy, final Collection<PathFilterSet> filters) {
            final RuleConverter ruleConverter = new RuleConverter(rules, ruleOrProxy);
            for (final PathFilterSet filter : filters) {
                filter.accept(ruleConverter);
            }
        }

        @Override
        public void visit(final PathFilter pathFilter) {
            final Filter filter = convertFilter(pathFilter, Filter::newFilter);
            filter.addInstance(this.ruleOrProxy, pathFilter);
            getFilters(this.ruleOrProxy).add(filter);
        }

        protected static <T extends Filter> T convertFilter(final PathFilter pathFilter,
                final Function<FunctionWithArguments<TemplateSlotLeaf>, T> ctor) {
            final PredicateWithArguments<PathLeaf> predicate = pathFilter.getFunction();
            return ctor.apply(FWAPathLeafToTemplateSlotLeafTranslator.getArguments(predicate));
        }

        @Override
        public void visit(final PathExistentialSet existentialSet) {
            final Rule rule =
                    this.ruleOrProxy.left().getOrThrow(() -> new UnsupportedOperationException("Nested Existentials!"));
            // we may be able to share the existential closure part
            // existential closure filter instances are put into the same column if and only if they
            // have the same conflicts to their pure part and the pure parts have the same inner
            // conflicts

            final PathFilter existentialClosure = existentialSet.getExistentialClosure();
            final Set<PathFilterSet> purePart = existentialSet.getPurePart();

            final ExistentialProxy proxy = new ExistentialProxy(rule, existentialSet);
            final Either<Rule, ExistentialProxy> proxyEither = Either.right(proxy);
            final RuleConverter visitor = new RuleConverter(this.rules, proxyEither);

            // insert all pure filters into the proxy
            for (final PathFilterSet pathFilterSet : purePart) {
                pathFilterSet.accept(visitor);
            }
            // create own row for the pure part
            this.rules.add(proxyEither);

            final FilterProxy convertedExCl =
                    convertFilter(existentialClosure, pred -> FilterProxy.newFilterProxy(pred, proxy));
            getFilters(this.ruleOrProxy).add(convertedExCl);
            final FilterInstance filterInstance = convertedExCl.addInstance(this.ruleOrProxy, existentialClosure);
            rule.existentialProxies.put(filterInstance, proxy);
        }
    }

    public static List<PathRule> transform(final List<PathSetRule> rules) {
        final List<Either<Rule, ExistentialProxy>> translatedRules = new ArrayList<>();
        for (final PathSetRule rule : rules) {
            addRule(rule, translatedRules);
        }
        // find all maximal blocks
        final PathBlockSet resultBlockSet = new PathBlockSet();
        findAllMaximalBlocks(translatedRules, resultBlockSet);
        // solve the conflicts
        determineAndSolveConflicts(resultBlockSet);
        assert checkContainment(resultBlockSet);
        // transform into PathFilterList
        final List<PathRule> output = createOutput(translatedRules, resultBlockSet);
        Filter.CACHE.clear();
        FilterProxy.PROXY_CACHE.clear();
        return output;
    }

    protected static List<PathRule> createOutput(final List<Either<Rule, ExistentialProxy>> rules,
            final PathBlockSet resultBlockSet) {
        final Function<? super Block, ? extends Integer> characteristicNumber =
                block -> block.getFlatFilterInstances().size() / block.getRulesOrProxies().size();
        final TreeMap<Integer, CursorableLinkedList<Block>> blockMap = resultBlockSet.getBlocks().stream()
                .collect(groupingBy(characteristicNumber, TreeMap::new, toCollection(CursorableLinkedList::new)));
        // iterate over all the filter proxies ever used
        for (final FilterProxy filterProxy : FilterProxy.getFilterProxies()) {
            final Set<ExistentialProxy> existentialProxies = filterProxy.getProxies();
            // determine the largest characteristic number of the blocks containing filter instances
            // of one of the existential proxies (choice is arbitrary, since the filters and the
            // conflicts are identical if they belong to the same filter).
            final OptionalInt optMax = resultBlockSet.getRuleInstanceToBlocks()
                    .computeIfAbsent(Either.right(existentialProxies.iterator().next()), newHashSet()).stream()
                    .mapToInt(composeToInt(characteristicNumber, Integer::intValue)).max();
            if (!optMax.isPresent()) continue;
            final int eCN = optMax.getAsInt();
            // get the list to append the blocks using the existential closure filter INSTANCE to
            final CursorableLinkedList<Block> targetList = blockMap.get(eCN);
            // for every existential part
            for (final ExistentialProxy existentialProxy : existentialProxies) {
                final FilterInstance exClosure = existentialProxy.getExistentialClosure();
                // create a list storing the blocks to move
                final List<Block> toMove = new ArrayList<>();
                for (final CursorableLinkedList<Block> blockList : blockMap.headMap(eCN, true).values()) {
                    // iterate over the blocks in the current list
                    for (final ListIterator<Block> iterator = blockList.listIterator(); iterator.hasNext(); ) {
                        final Block current = iterator.next();
                        // if the current block uses the current existential closure filter
                        // INSTANCE, it has to be moved
                        if (current.getFlatFilterInstances().contains(exClosure)) {
                            iterator.remove();
                            toMove.add(current);
                        }
                    }
                }
                // append the blocks to be moved (they were only removed so far)
                targetList.addAll(toMove);
            }
        }
        final Set<FilterInstance> constructedFIs = new HashSet<>();
        final Map<Either<Rule, ExistentialProxy>, Map<FilterInstance, Set<FilterInstance>>> ruleToJoinedWith =
                new HashMap<>();
        final Map<Set<FilterInstance>, PathFilterList> joinedWithToComponent = new HashMap<>();
        // at this point, the network can be constructed
        for (final CursorableLinkedList<Block> blockList : blockMap.values()) {
            for (final Block block : blockList) {
                final List<Either<Rule, ExistentialProxy>> blockRules = Lists.newArrayList(block.getRulesOrProxies());
                final Set<List<FilterInstance>> filterInstanceColumns =
                        Block.getFilterInstanceColumns(block.getFilters(), block.getRuleToFilterToRow(), blockRules);
                // since we are considering blocks, it is either the case that all filter
                // instances of the column have been constructed or none of them have
                final PathSharedListWrapper sharedListWrapper = new PathSharedListWrapper(blockRules.size());
                final Map<Either<Rule, ExistentialProxy>, PathSharedList> ruleToSharedList =
                        IntStream.range(0, blockRules.size()).boxed()
                                .collect(toMap(blockRules::get, sharedListWrapper.getSharedSiblings()::get));
                final List<List<FilterInstance>> columnsToConstruct, columnsAlreadyConstructed;
                {
                    final Map<Boolean, List<List<FilterInstance>>> partition = filterInstanceColumns.stream()
                            .collect(partitioningBy(column -> Collections.disjoint(column, constructedFIs)));
                    columnsAlreadyConstructed = partition.get(Boolean.FALSE);
                    columnsToConstruct = partition.get(Boolean.TRUE);
                }

                if (!columnsAlreadyConstructed.isEmpty()) {
                    final Map<PathSharedList, LinkedHashSet<PathFilterList>> sharedPart = new HashMap<>();
                    for (final List<FilterInstance> column : columnsAlreadyConstructed) {
                        for (final FilterInstance fi : column) {
                            sharedPart.computeIfAbsent(ruleToSharedList.get(fi.getRuleOrProxy()), newLinkedHashSet())
                                    .add(joinedWithToComponent.get(ruleToJoinedWith.get(fi.getRuleOrProxy()).get(fi)));
                        }
                    }
                    sharedListWrapper.addSharedColumns(sharedPart);
                }

                for (final List<FilterInstance> column : columnsToConstruct) {
                    sharedListWrapper.addSharedColumn(column.stream()
                            .collect(toMap(fi -> ruleToSharedList.get(fi.getRuleOrProxy()), FilterInstance::convert)));
                }
                constructedFIs.addAll(block.getFlatFilterInstances());
                for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> entry : block
                        .getRuleToFilterToRow().entrySet()) {
                    final Either<Rule, ExistentialProxy> rule = entry.getKey();
                    final Set<FilterInstance> joined =
                            entry.getValue().values().stream().flatMap(sbs -> sbs.getInstances().stream())
                                    .collect(toSet());
                    final Map<FilterInstance, Set<FilterInstance>> joinedWithMapForThisRule =
                            ruleToJoinedWith.computeIfAbsent(rule, newHashMap());
                    joined.forEach(fi -> joinedWithMapForThisRule.put(fi, joined));
                    joinedWithToComponent.put(joined, ruleToSharedList.get(rule));
                }
            }
        }
        final List<PathRule> pathRules = new ArrayList<>();
        for (final Either<Rule, ExistentialProxy> either : rules) {
            if (either.isRight()) {
                continue;
            }
            final List<PathFilterList> pathFilterLists =
                    Stream.concat(either.left().get().existentialProxies.values().stream().map(p -> Either.right(p)),
                            Stream.of(either)).flatMap(
                            e -> ruleToJoinedWith.getOrDefault(e, Collections.emptyMap()).values().stream().distinct())
                            .map(joinedWithToComponent::get).collect(toList());
            pathRules.add(either.left().get().getOriginal().toPathRule(PathFilterList.toSimpleList(pathFilterLists),
                    pathFilterLists.size() > 1 ? InitialFactPathsFinder.gather(pathFilterLists)
                            : Collections.emptySet()));
        }
        return pathRules;
    }

    public static class InitialFactPathsFinder implements PathFilterListVisitor {
        final Set<Path> initialFactPaths = Sets.newHashSet();

        public static Set<Path> gather(final Iterable<PathFilterList> filters) {
            final InitialFactPathsFinder instance = new InitialFactPathsFinder();
            for (final PathFilterList filter : filters) {
                filter.accept(instance);
            }
            return instance.initialFactPaths;
        }

        @Override
        public void visit(final PathSharedList filter) {
            final ImmutableList<PathFilterList> elements = filter.getUnmodifiableFilterListCopy();
            if (1 != elements.size()) {
                return;
            }
            elements.get(0).accept(new InitialFactPathsFinderHelper());
        }

        class InitialFactPathsFinderHelper implements PathFilterListVisitor {
            @Override
            public void visit(final PathExistentialList filter) {
                InitialFactPathsFinder.this.initialFactPaths.add(filter.getInitialPath());
            }

            @Override
            public void visit(final PathNodeFilterSet filter) {
            }

            @Override
            public void visit(final PathSharedList filter) {
            }
        }

        @Override
        public void visit(final PathNodeFilterSet filter) {
        }

        @Override
        public void visit(final PathExistentialList filter) {
        }
    }

    public static boolean hasEqualConflicts(final Conflict a, final Conflict b) {
        return (a == b) || (a != null && a.hasEqualConflicts(b));
    }

    protected static void findAllMaximalBlocks(final List<Either<Rule, ExistentialProxy>> rules,
            final PathBlockSet resultBlocks) {
        final UndirectedGraph<FilterInstance, ConflictEdge> conflictGraph = determineConflictGraphForRules(rules);
        final Set<Filter> filters = rules.stream().flatMap(rule -> getFilters(rule).stream()).collect(toSet());
        for (final Filter filter : filters) {
            vertical(conflictGraph,
                    rules.stream().map(r -> filter.getInstances(r)).filter(negate(Set::isEmpty)).collect(toSet()),
                    resultBlocks);
        }
    }

    protected static <T, K, D> Collector<T, ?, Set<D>> groupingIntoSets(
            final Function<? super T, ? extends K> classifier, final Collector<? super T, ?, D> downstream) {
        final Collector<T, ?, Map<K, D>> groupingBy = groupingBy(classifier, downstream);
        return Collectors.collectingAndThen(groupingBy, map -> new HashSet<D>(map.values()));
    }

    protected static PathBlockSet findAllMaximalBlocksInReducedScope(final Set<FilterInstance> filterInstances,
            final PathBlockSet resultBlocks) {
        final Iterable<List<FilterInstance>> filterInstancesGroupedByRule = filterInstances.stream()
                .collect(Collectors.collectingAndThen(groupingBy(FilterInstance::getRuleOrProxy), Map::values));
        final UndirectedGraph<FilterInstance, ConflictEdge> conflictGraph =
                determineConflictGraph(filterInstancesGroupedByRule);
        final Set<Set<Set<FilterInstance>>> filterInstancesGroupedByFilterAndByRule = filterInstances.stream().collect(
                groupingIntoSets(FilterInstance::getFilter, groupingIntoSets(FilterInstance::getRuleOrProxy, toSet())));
        for (final Set<Set<FilterInstance>> filterInstancesOfOneFilterGroupedByRule
                : filterInstancesGroupedByFilterAndByRule) {
            vertical(conflictGraph, filterInstancesOfOneFilterGroupedByRule, resultBlocks);
        }
        return resultBlocks;
    }

    protected static void determineAndSolveConflicts(final PathBlockSet resultBlocks) {
        // determine conflicts
        final PathBlockSet deletedBlocks = new PathBlockSet();
        final DirectedGraph<Block, BlockConflict> blockConflictGraph = new SimpleDirectedGraph<>(BlockConflict::of);
        for (final Block block : resultBlocks.getBlocks()) {
            blockConflictGraph.addVertex(block);
        }
        for (final Block x : blockConflictGraph.vertexSet()) {
            createArcs(blockConflictGraph, resultBlocks, x);
        }
        // solve conflicts
        while (true) {
            final Optional<BlockConflict> mostUsefulConflictsFirst =
                    blockConflictGraph.edgeSet().stream().max(Comparator.comparingInt(BlockConflict::getQuality));
            if (!mostUsefulConflictsFirst.isPresent()) {
                break;
            }
            final BlockConflict blockConflict = mostUsefulConflictsFirst.get();
            solveConflict(blockConflict, blockConflictGraph, resultBlocks, deletedBlocks);
        }
    }

    protected static void createArcs(final DirectedGraph<Block, BlockConflict> blockConflictGraph,
            final PathBlockSet resultBlocks, final Block x) {
        for (final Either<Rule, ExistentialProxy> rule : x.getRulesOrProxies()) {
            for (final Block y : resultBlocks.getRuleInstanceToBlocks().get(rule)) {
                if (x == y || blockConflictGraph.containsEdge(x, y)) {
                    continue;
                }
                addArc(blockConflictGraph, x, y);
                addArc(blockConflictGraph, y, x);
            }
        }
    }

    protected static void addArc(final DirectedGraph<Block, BlockConflict> blockConflictGraph, final Block x,
            final Block y) {
        // arc is a conflict between X and Y
        // conf will always be a conflict between X and B
        final BlockConflict arc = BlockConflict.of(x, y);
        if (null == arc) return;
        final Set<BlockConflict> oldArcs = blockConflictGraph.outgoingEdgesOf(x);
        // determine arc's quality
        final Set<FilterInstance> xFIs = x.getFlatFilterInstances();
        final Set<FilterInstance> yFIs = y.getFlatFilterInstances();
        arc.quality = oldArcs.stream().mapToInt(
                conf -> usefulness(arc, xFIs, conf, conf.getConflictingBlock().getFlatFilterInstances()) - (
                        Sets.difference(arc.cfi, yFIs).size() + (Sets.intersection(xFIs, yFIs).isEmpty() ? 0 : 1)))
                .sum();
        // update quality of all arcs affected
        for (final BlockConflict conf : oldArcs) {
            final Set<FilterInstance> bFIs = conf.getConflictingBlock().getFlatFilterInstances();
            conf.quality += usefulness(arc, xFIs, conf, bFIs);
        }
        // add the new edge
        blockConflictGraph.addEdge(x, y, arc);
    }

    protected static int usefulness(final BlockConflict xyArc, final Set<FilterInstance> xFIs,
            final BlockConflict xbArc, final Set<FilterInstance> bFIs) {
        return Sets.intersection(Sets.difference(xbArc.cfi, bFIs), xyArc.cfi).size() + (
                Sets.difference(Sets.intersection(xFIs, bFIs), xyArc.cfi).isEmpty() ? 0 : 1);
    }

    protected static void removeArc(final DirectedGraph<Block, BlockConflict> blockConflictGraph,
            final BlockConflict arc) {
        final Block b = arc.getReplaceBlock();
        final Set<FilterInstance> bFIs = b.getFlatFilterInstances();
        // about updating the quality of all affected arcs:
        // the outgoing arcs of b only influence each other, but all get deleted
        // the incoming arcs of b influence arcs not getting deleted
        for (final BlockConflict xbArc : blockConflictGraph.incomingEdgesOf(b)) {
            // for every neighbor x of b
            final Block x = xbArc.getReplaceBlock();
            final Set<FilterInstance> xFIs = x.getFlatFilterInstances();
            for (final BlockConflict xyArc : blockConflictGraph.outgoingEdgesOf(x)) {
                // for every conflict of that neighbor
                if (xbArc == xyArc) {
                    // excluding the arc getting deleted
                    continue;
                }
                // decrement the quality of the block conflict
                // remove the influence of the xb arc regarding the xy arc
                xyArc.quality -= usefulness(xyArc, xFIs, xbArc, bFIs);
            }
        }
        // remove the block to be replaced
        final boolean vertexRemoved = blockConflictGraph.removeVertex(b);
        assert vertexRemoved;
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    static class BlockConflict {
        final Block replaceBlock, conflictingBlock;
        final Set<FilterInstance> cfi;
        int quality;

        public static BlockConflict of(final Block replaceBlock, final Block conflictingBlock) {
            // determine if there is a pair of FIs in conflict
            final Map<Either<Rule, ExistentialProxy>, List<FilterInstance>> yFIsByRule =
                    conflictingBlock.getFlatFilterInstances().stream()
                            .collect(groupingBy(FilterInstance::getRuleOrProxy));
            final Set<FilterInstance> cfi = replaceBlock.getFlatFilterInstances().stream()
                    .filter(xFI -> yFIsByRule.getOrDefault(xFI.getRuleOrProxy(), Collections.emptyList()).stream()
                            .anyMatch(yFI -> null != yFI.getOrDetermineConflicts(xFI))).collect(toSet());
            if (cfi.isEmpty()) return null;
            // if non-overlapping
            if (Collections
                    .disjoint(replaceBlock.getFlatFilterInstances(), conflictingBlock.getFlatFilterInstances())) {
                return new BlockConflict(replaceBlock, conflictingBlock, cfi);
            }
            // else overlapping
            final int rnoc = replaceBlock.getNumberOfColumns();
            final int cnoc = conflictingBlock.getNumberOfColumns();
            if (rnoc == cnoc) {
                // containment of columns impossible (only if one block is fully contained within
                // the other, which we won't consider here)
                return new BlockConflict(replaceBlock, conflictingBlock, cfi);
            }
            // x will be wider, y will be taller
            final Block x, y;
            if (rnoc >= cnoc) {
                x = replaceBlock;
                y = conflictingBlock;
            } else {
                x = conflictingBlock;
                y = replaceBlock;
            }
            // will only work if y.getFilters() subseteq x.getFilters()
            if (!x.getFilters().containsAll(y.getFilters())) {
                return new BlockConflict(replaceBlock, conflictingBlock, cfi);
            }
            // will only work if x.getRules subseteq y.getRules
            if (!y.getRulesOrProxies().containsAll(x.getRulesOrProxies())) {
                return new BlockConflict(replaceBlock, conflictingBlock, cfi);
            }
            // only consider the rules of x (the wider block)
            final List<Either<Rule, ExistentialProxy>> rules = ImmutableList.copyOf(x.getRulesOrProxies());
            // only consider the filters of y (the taller block)
            final Set<Filter> filters = y.getFilters();
            // the result will be the columns within the intersection
            final Set<List<FilterInstance>> xColumns =
                    Block.getFilterInstanceColumns(filters, x.getRuleToFilterToRow(), rules);
            final Set<List<FilterInstance>> yColumns =
                    Block.getFilterInstanceColumns(filters, y.getRuleToFilterToRow(), rules);
            // if one of the (shrinked) columns of the taller block is not present in the
            // intersection part of the wider block, the blocks are in conflict
            for (final List<FilterInstance> yColumn : yColumns) {
                if (!xColumns.contains(yColumn)) return new BlockConflict(replaceBlock, conflictingBlock, cfi);
            }
            return null;
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    // no @EqualsAndHashCode so equals will not prevent new edges
    static class BlockConflictEdge {
        final BlockConflict a, b;

        public BlockConflict getForReplaceBlock(final Block replaceBlock) {
            assert this.a.getReplaceBlock() == replaceBlock || this.b.getReplaceBlock() == replaceBlock;
            return this.a.getReplaceBlock() == replaceBlock ? this.a : this.b;
        }

        public BlockConflict getForConflictingBlock(final Block conflictingBlock) {
            assert this.a.getConflictingBlock() == conflictingBlock || this.b.getConflictingBlock() == conflictingBlock;
            return this.a.getConflictingBlock() == conflictingBlock ? this.a : this.b;
        }

        public static BlockConflictEdge of(final Block x, final Block y) {
            final BlockConflict xy = BlockConflict.of(x, y);
            if (null == xy) return null;
            return new BlockConflictEdge(xy, BlockConflict.of(y, x));
        }
    }

    protected static void solveConflict(final BlockConflict blockConflict,
            final DirectedGraph<Block, BlockConflict> blockConflictGraph, final PathBlockSet resultBlocks,
            final PathBlockSet deletedBlocks) {
        final Block replaceBlock = blockConflict.getReplaceBlock();
        final Set<FilterInstance> xWOcfi =
                replaceBlock.getFlatFilterInstances().stream().filter(negate(blockConflict.getCfi()::contains))
                        .collect(toSet());
        resultBlocks.remove(replaceBlock);
        // remove replaceBlock and update qualities
        removeArc(blockConflictGraph, blockConflict);
        // find the horizontally maximal blocks within xWOcfi
        final PathBlockSet newBlocks = findAllMaximalBlocksInReducedScope(xWOcfi, new PathBlockSet());
        // for every such block,
        for (final Block block : newBlocks.getBlocks()) {
            if (!deletedBlocks.isContained(block)) {
                if (resultBlocks.addDuringConflictResolution(block)) {
                    blockConflictGraph.addVertex(block);
                    createArcs(blockConflictGraph, resultBlocks, block);
                }
            }
        }
        deletedBlocks.addDuringConflictResolution(replaceBlock);
    }

    private static boolean checkContainment(final PathBlockSet pathBlockSet) {
        final HashSet<Block> blocks = pathBlockSet.getBlocks();
        for (final Block a : blocks) {
            for (final Block b : blocks) {
                if (a == b) continue;
                if (a.containedIn(b)) throw new IllegalStateException();
            }
        }
        return true;
    }

    protected static void vertical(final UndirectedGraph<FilterInstance, ConflictEdge> graph,
            final Set<Set<FilterInstance>> filterInstancesGroupedByRule, final PathBlockSet resultBlocks) {
        final Set<Set<Set<FilterInstance>>> filterInstancesPowerSet = Sets.powerSet(filterInstancesGroupedByRule);
        final Iterator<Set<Set<FilterInstance>>> iterator = filterInstancesPowerSet.iterator();
        // skip empty set
        iterator.next();
        while (iterator.hasNext()) {
            final Set<Set<FilterInstance>> powerSetElement = iterator.next();
            final Set<List<FilterInstance>> cartesianProduct =
                    Sets.cartesianProduct(ImmutableList.copyOf(powerSetElement));
            for (final List<FilterInstance> filterInstances : cartesianProduct) {
                final Block newBlock = new Block(graph);
                newBlock.addFilterInstances(filterInstances.stream().collect(toMap(FilterInstance::getRuleOrProxy,
                        fi -> Collections.singleton(new FilterInstancesSideBySide(fi)))));
                horizontalRecursion(newBlock, new Stack<>(), resultBlocks);
            }
        }
    }

    protected static void horizontalRecursion(final Block block, final Stack<Set<FilterInstance>> exclusionStack,
            final PathBlockSet resultBlocks) {
        // needed: the filters that are contained in every rule of the block, where for every
        // filter it is the case that: every rule contains at least one INSTANCE not already
        // excluded by the exclusion stack
        // thus: get the non-excluded filter instances
        final Set<FilterInstance> neighbours = block.getBorderConflicts().keySet().stream()
                .filter(fi -> !exclusionStack.stream().anyMatch(as -> as.contains(fi))).collect(toSet());
        if (neighbours.isEmpty()) {
            resultBlocks.addDuringHorizontalRecursion(block);
            return;
        }
        // group them by their filter
        final Map<Filter, List<FilterInstance>> nFilterToInstances =
                neighbours.stream().collect(groupingBy(FilterInstance::getFilter));
        // get all the rules in the block
        final Set<Either<Rule, ExistentialProxy>> bRules = block.getRulesOrProxies();
        // get a map from filter to all rules containing instances of that filter
        final Map<Filter, Set<Either<Rule, ExistentialProxy>>> nFilterToRulesContainingIt =
                nFilterToInstances.entrySet().stream().collect(toMap(Entry::getKey,
                        e -> e.getValue().stream().map(FilterInstance::getRuleOrProxy).collect(toSet())));
        // get the filters that are contained in every rule
        final Set<Filter> nRelevantFilters =
                nFilterToInstances.keySet().stream().filter(f -> nFilterToRulesContainingIt.get(f).containsAll(bRules))
                        .collect(toSet());
        // if no filters are left to add, the block is horizontally maximized, add it
        if (nRelevantFilters.isEmpty()) {
            resultBlocks.addDuringHorizontalRecursion(block);
            return;
        }
        // divide into filters without multiple instances and filters with multiple instances
        final List<Filter> nSingleCellFilters, nMultiCellFilters;
        {
            final Map<Boolean, List<Filter>> partition = nRelevantFilters.stream()
                    .collect(partitioningBy(f -> nFilterToInstances.get(f).size() > bRules.size()));
            nSingleCellFilters = partition.get(Boolean.FALSE);
            nMultiCellFilters = partition.get(Boolean.TRUE);
        }
        // list of rule-filter-matchings that may be added
        final List<Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>>> matchingFilters =
                new ArrayList<>();
        final List<Filter> incompatibleFilters = new ArrayList<>();

        // there is a 1 to 1 mapping from filter instances (side-by-side) to rules
        // for every filter INSTANCE, the conflicts have to be the same in all rules
        final Map<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>>
                bRuleToFilterToBlockInstances = block.getFilterInstances().stream().collect(
                groupingBy(FilterInstancesSideBySide::getRuleOrProxy,
                        toMap(FilterInstancesSideBySide::getFilter, Function.identity())));
        // prefer singleCellFilters
        final List<Filter> bFilters = ImmutableList.copyOf(block.getFilters());
        findMatchingAndIncompatibleFilters(nFilterToInstances, bRules, nSingleCellFilters, bFilters, matchingFilters,
                incompatibleFilters, bRuleToFilterToBlockInstances);
        // if none matched, try multiCellFilters, otherwise defer them
        if (matchingFilters.isEmpty()) {
            findMatchingAndIncompatibleFilters(nFilterToInstances, bRules, nMultiCellFilters, bFilters, matchingFilters,
                    incompatibleFilters, bRuleToFilterToBlockInstances);
            // if still none matched, the block is maximal, add it to the result blocks
            if (matchingFilters.isEmpty()) {
                resultBlocks.addDuringHorizontalRecursion(block);
                return;
            }
        }

        // create the next exclusion layer
        final Set<FilterInstance> furtherExcludes = new HashSet<>();
        // add it to the stack
        exclusionStack.push(furtherExcludes);
        // add all incompatible filter instances to the exclusion stack
        for (final Filter incompatibleFilter : incompatibleFilters) {
            for (final Either<Rule, ExistentialProxy> rule : bRules) {
                furtherExcludes.addAll(incompatibleFilter.getInstances(rule));
            }
        }
        // for every matching filter INSTANCE set, create a new block
        for (final Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> neighbourMap : matchingFilters) {
            final Block newBlock = new Block(block);
            newBlock.addFilterInstances(neighbourMap);
            // recurse for that block
            horizontalRecursion(newBlock, exclusionStack, resultBlocks);
            // after the recursion, exclude all filter instances just used
            for (final Set<FilterInstancesSideBySide> set : neighbourMap.values()) {
                for (final FilterInstancesSideBySide filterInstancesSideBySide : set) {
                    for (final FilterInstance filterInstance : filterInstancesSideBySide.getInstances()) {
                        furtherExcludes.add(filterInstance);
                    }
                }
            }
        }
        // eliminate top layer of the exclusion stack
        exclusionStack.pop();
    }

    protected static void findMatchingAndIncompatibleFilters(final Map<Filter, List<FilterInstance>> nFilterToInstances,
            final Set<Either<Rule, ExistentialProxy>> bRules, final List<Filter> nFilters, final List<Filter> bFilters,
            final List<Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>>> matchingFilters,
            final List<Filter> incompatibleFilters,
            final Map<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>>
                    bRuleToFilterToBlockInstances) {
        // iterate over every single-/multi-cell filter and check that its instances have the same
        // conflicts in every rule
        for (final Filter nFilter : nFilters) {
            boolean matchingConstellationFound = false;

            // iterate over the possible mappings: (filter,rule) -> filter INSTANCE
            final List<Set<FilterInstance>> nListOfRelevantFilterInstancesGroupedByRule = new ArrayList<>(
                    nFilterToInstances.get(nFilter).stream()
                            .collect(groupingBy(FilterInstance::getRuleOrProxy, toSet())).values());
            // create the cartesian product
            final Set<List<FilterInstance>> nRelevantFilterInstanceCombinations =
                    Sets.cartesianProduct(nListOfRelevantFilterInstancesGroupedByRule);
            // iterate over the possible filter INSTANCE combinations
            cartesianProductLoop:
            for (final List<FilterInstance> nCurrentOutsideFilterInstances : nRelevantFilterInstanceCombinations) {
                // list of conflicts for this filter INSTANCE combination
                final List<Conflict> conflicts = new ArrayList<>();
                // create a map for faster lookup: rule -> filter INSTANCE (outside)
                final Map<Either<Rule, ExistentialProxy>, FilterInstance> nRuleToCurrentOutsideFilterInstance =
                        nCurrentOutsideFilterInstances.stream()
                                .collect(toMap(FilterInstance::getRuleOrProxy, Function.identity()));
                if (bRules.size() > 1) {
                    // iterate over the Rule-BlockFilterInstance-mappings
                    for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> entry
                            : bRuleToFilterToBlockInstances.entrySet()) {
                        int i = 0;
                        final Either<Rule, ExistentialProxy> rule = entry.getKey();
                        final Map<Filter, FilterInstancesSideBySide> bFilterToBlockInstances = entry.getValue();
                        // for every filter of the block check that the conflicts are the same as
                        // those of the first rule
                        for (final Filter bFilter : bFilters) {
                            // get the mapping from rule to filter INSTANCE for the current filter
                            final FilterInstancesSideBySide bSideBySide = bFilterToBlockInstances.get(bFilter);
                            // get the corresponding filter INSTANCE(s) that may be added
                            final FilterInstance nSource = nRuleToCurrentOutsideFilterInstance.get(rule);
                            // iterate over the filter instances
                            for (final FilterInstance bTarget : bSideBySide.getInstances()) {
                                // determine conflict between inside INSTANCE and outside INSTANCE
                                final Conflict conflict = nSource.getOrDetermineConflicts(bTarget);
                                // if this is the first loop iteration, just add the conflict to be
                                // compared later on
                                if (i >= conflicts.size()) {
                                    conflicts.add(conflict);
                                } else if (!hasEqualConflicts(conflicts.get(i), conflict)) {
                                    // if the conflicts don't match, continue with next filter
                                    continue cartesianProductLoop;
                                }
                                ++i;
                            }
                        }
                    }
                }
                // conflict identical for all rules
                matchingFilters.add(bRules.stream().collect(toMap(Function.identity(), rule -> Collections
                        .singleton(new FilterInstancesSideBySide(nRuleToCurrentOutsideFilterInstance.get(rule))))));
                matchingConstellationFound = true;
            }
            if (!matchingConstellationFound) {
                incompatibleFilters.add(nFilter);
            }
        }
    }

    @Getter
    @Setter
    // no @EqualsAndHashCode so equals will not prevent new edges
    static class ConflictEdge {
        final Conflict a, b;

        ConflictEdge(final FilterInstance x, final FilterInstance y) {
            this.a = x.getOrDetermineConflicts(y);
            this.b = y.getOrDetermineConflicts(x);
        }

        public Conflict getForSource(final FilterInstance sourceFilterInstance) {
            assert this.a.getSource() == sourceFilterInstance || this.b.getSource() == sourceFilterInstance;
            return this.a.getSource() == sourceFilterInstance ? this.a : this.b;
        }

        public Conflict getForTarget(final FilterInstance targetFilterInstance) {
            assert this.a.getTarget() == targetFilterInstance || this.b.getTarget() == targetFilterInstance;
            return this.a.getTarget() == targetFilterInstance ? this.a : this.b;
        }

        public static ConflictEdge of(final FilterInstance x, final FilterInstance y) {
            final Conflict a = x.getOrDetermineConflicts(y);
            if (null == a) return null;
            return new ConflictEdge(x, y);
        }
    }

    protected static SetView<FilterInstance> getStableSet(final UndirectedGraph<FilterInstance, ConflictEdge> graph) {
        // could be improved by DOI 10.1137/0206036
        return Sets.difference(graph.vertexSet(), VertexCovers.find2ApproximationCover(graph));
    }
}
