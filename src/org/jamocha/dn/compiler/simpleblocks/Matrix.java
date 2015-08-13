/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.dn.compiler.simpleblocks;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.composeToInt;
import static org.jamocha.util.Lambdas.negate;
import static org.jamocha.util.Lambdas.newHashMap;
import static org.jamocha.util.Lambdas.newHashSet;
import static org.jamocha.util.Lambdas.newTreeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.collections4.list.CursorableLinkedList;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.ConstructCache.Defrule.PathSetBasedRule;
import org.jamocha.dn.compiler.simpleblocks.Matrix.Filter.FilterInstance;
import org.jamocha.dn.compiler.simpleblocks.Matrix.Filter.FilterInstance.Conflict;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterSet;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.filter.PathFilterSetVisitor;
import org.jamocha.filter.PathLeafCollector;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.VertexCovers;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Pair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Matrix {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@lombok.Data
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@EqualsAndHashCode(of = { "predicate", "arguments" })
	static class Filter {
		final Predicate predicate;
		final List<Pair<Template, SlotAddress>> arguments;
		final Map<Either<Rule, ExistentialProxy>, Set<FilterInstance>> ruleToInstances = new HashMap<>();

		static final Map<Filter, Filter> cache = new HashMap<>();

		static Filter newFilter(final Predicate predicate, final List<Pair<Template, SlotAddress>> arguments) {
			return cache.computeIfAbsent(new Filter(predicate, arguments), Function.identity());
		}

		public FilterInstance addInstance(final Either<Rule, ExistentialProxy> ruleOrProxy, final PathFilter pathFilter) {
			final ArrayList<PathLeaf> parameterLeafs = PathLeafCollector.collect(pathFilter.getFunction());
			final List<Path> parameterPaths = parameterLeafs.stream().map(PathLeaf::getPath).collect(toList());
			final FilterInstance instance = new FilterInstance(pathFilter, parameterPaths, ruleOrProxy);
			ruleToInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			return instance;
		}

		public Set<FilterInstance> getInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
			return ruleToInstances.get(ruleOrProxy);
		}

		public PathFilterList convert(
				final FilterInstance instance,
				@SuppressWarnings("unused") final Map<Either<Rule, ExistentialProxy>, Map<FilterInstance, Set<FilterInstance>>> ruleToJoinedWith,
				@SuppressWarnings("unused") final Map<Set<FilterInstance>, PathFilterList> joinedWithToComponent) {
			return PathNodeFilterSet.newRegularPathNodeFilterSet(instance.pathFilter);
		}

		/**
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		@Getter
		@Setter
		@ToString
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		// no EqualsAndHashCode
		class FilterInstance {
			final PathFilter pathFilter;
			final List<Path> parameters;
			final Either<Rule, ExistentialProxy> ruleOrProxy;
			final Map<FilterInstance, Conflict> conflicts = new HashMap<>();

			public Conflict addConflict(final FilterInstance targetFilterInstance) {
				final Conflict conflict = new Conflict(targetFilterInstance);
				if (conflict.samePathsIndices.isEmpty()) {
					conflicts.put(targetFilterInstance, null);
					return null;
				}
				conflicts.put(targetFilterInstance, conflict);
				return conflict;
			}

			public Conflict getOrDetermineConflicts(final FilterInstance targetFilterInstance) {
				// call to containsKey prevents recalculation of null conflicts
				// (design currently doesn't easily allow for a better null-object)
				return conflicts.containsKey(targetFilterInstance) ? conflicts.get(targetFilterInstance)
						: addConflict(targetFilterInstance);
			}

			public Filter getFilter() {
				return Filter.this;
			}

			public PathFilterList convert(
					final Map<Either<Rule, ExistentialProxy>, Map<FilterInstance, Set<FilterInstance>>> ruleToJoinedWith,
					final Map<Set<FilterInstance>, PathFilterList> joinedWithToComponent) {
				return getFilter().convert(this, ruleToJoinedWith, joinedWithToComponent);
			}

			/**
			 * Returns the filter instances of the same filter within the same rule (result contains
			 * the filter instance this method is called upon).
			 *
			 * @return the filter instances of the same filter within the same rule
			 */
			public Set<FilterInstance> getSiblings() {
				return getInstances(ruleOrProxy);
			}

			/**
			 * A conflict represents the fact that two filter instances are using the same data
			 * (possibly on different parameter positions). The parameters of the enclosing instance
			 * are compared to the parameters of the given target instance.
			 * <p>
			 * It holds for every pair c in {@code samePathsIndices} that parameter {@code c.left}
			 * of the enclosing filter instance uses the same {@link Path} as parameter
			 * {@code c.right} of the target filter instance.
			 *
			 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
			 */
			@Value
			class Conflict {
				// left refers to the source, right to the target of the conflicts
				final Set<Pair<Integer, Integer>> samePathsIndices;
				final FilterInstance target;

				public Conflict(final FilterInstance target) {
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
						for (final Integer ji : targetPathIndices.get(sourceParameters.get(oi))) {
							this.samePathsIndices.add(Pair.pair(oi, ji));
						}
					}
				}

				public boolean hasCompatibleFiltersAndEqualConflicts(final Conflict conflict) {
					return Filter.this.equals(conflict.getSource().getFilter())
							&& target.getFilter().equals(conflict.getTarget().getFilter())
							&& samePathsIndices.equals(conflict.samePathsIndices);
				}

				public FilterInstance getSource() {
					return FilterInstance.this;
				}
			}
		}
	}

	@Getter
	static class FilterProxy extends Filter {
		final Set<ExistentialProxy> proxies;

		private FilterProxy(final Predicate predicate, final List<Pair<Template, SlotAddress>> arguments,
				final ExistentialProxy proxy) {
			super(predicate, arguments);
			this.proxies = Sets.newHashSet(proxy);
		}

		static final Map<FilterProxy, FilterProxy> cache = new HashMap<>();

		static FilterProxy newFilterProxy(final Predicate predicate, final List<Pair<Template, SlotAddress>> arguments,
				final ExistentialProxy proxy) {
			return cache.computeIfAbsent(new FilterProxy(predicate, arguments, proxy), Function.identity());
		}

		static Set<FilterProxy> getFilterProxies() {
			return cache.keySet();
		}

		@Override
		public PathFilterList convert(final FilterInstance instance,
				final Map<Either<Rule, ExistentialProxy>, Map<FilterInstance, Set<FilterInstance>>> ruleToJoinedWith,
				final Map<Set<FilterInstance>, PathFilterList> joinedWithToComponent) {
			final Map<FilterInstance, Set<FilterInstance>> joinedWithMap =
					ruleToJoinedWith.get(instance.getRuleOrProxy());
			final List<PathFilterList> components =
					joinedWithMap.values().stream().distinct().map(joinedWithToComponent::get).collect(toList());
			final PathFilterList component =
					(1 == components.size()) ? components.get(0) : new PathSharedListWrapper()
							.newSharedElement(components);
			final PathExistentialSet existential = instance.getRuleOrProxy().right().get().getExistential();
			return new PathFilterList.PathExistentialList(existential.getInitialPath(), component,
					PathNodeFilterSet.newExistentialPathNodeFilterSet(!existential.isPositive(),
							existential.getExistentialPaths(), instance.getPathFilter()));
		}

		@Override
		protected boolean canEqual(final Object other) {
			return other instanceof FilterProxy;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this)
				return true;
			if (!(o instanceof FilterProxy))
				return false;
			final FilterProxy other = (FilterProxy) o;
			if (!other.canEqual((Object) this))
				return false;
			if (!super.equals(other))
				return false;
			if (!equalProxies(this, other))
				return false;
			return true;
		}

		private static boolean equalProxies(final FilterProxy aFilterProxy, final FilterProxy bFilterProxy) {
			final ExistentialProxy aProxy = aFilterProxy.proxies.iterator().next();
			final ExistentialProxy bProxy = bFilterProxy.proxies.iterator().next();
			if (aProxy.existential.getExistentialPaths().size() != bProxy.existential.getExistentialPaths().size())
				return false;
			if (aProxy.existential.isPositive() != bProxy.existential.isPositive())
				return false;
			final Set<Filter> aFilters = aProxy.getFilters();
			final Set<Filter> bFilters = bProxy.getFilters();
			if (aFilters.size() != bFilters.size())
				return false;

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

			final Set<List<List<FilterInstance>>> cartesianProduct =
					Sets.cartesianProduct(bFilterInstanceSets.stream()
							.map(set -> Sets.newHashSet(new PermutationIterator<FilterInstance>(set)))
							.collect(toList()));

			final HashMap<FilterInstance, Pair<Integer, Integer>> aFI2IndexPair = new HashMap<>();
			{
				int i = 0;
				for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
					int j = 0;
					for (final FilterInstance filterInstance : aCell) {
						aFI2IndexPair.put(filterInstance, Pair.pair(i, j));
						++j;
					}
					++i;
				}
			}
			bijectionLoop: for (final List<List<FilterInstance>> bijection : cartesianProduct) {
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
							final FilterInstance bTarget = bijection.get(indexPair.left()).get(indexPair.right());
							final Conflict bConflict = bSource.getOrDetermineConflicts(bTarget);
							if (!aConflict.equals(bConflict)) {
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
			final int PRIME = 59;
			int result = 1;
			result = (result * PRIME) + super.hashCode();
			result =
					(result * PRIME)
							+ (this.proxies == null ? 0 : (this.proxies.iterator().next().filters == null ? 0
									: this.proxies.iterator().next().filters.hashCode()));
			return result;
		}

	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	@AllArgsConstructor
	@EqualsAndHashCode(of = { "instances" })
	static class FilterInstancesSideBySide {
		final LinkedHashSet<FilterInstance> instances;
		final Filter filter;
		final Either<Rule, ExistentialProxy> ruleOrProxy;

		public FilterInstancesSideBySide(final LinkedHashSet<FilterInstance> stacks) {
			this(stacks, stacks.iterator().next().getFilter(), stacks.iterator().next().getRuleOrProxy());
			assert 1 == stacks.stream().map(FilterInstance::getFilter).collect(toSet()).size();
		}

		public FilterInstancesSideBySide(final FilterInstance stack) {
			this(new LinkedHashSet<>(Collections.singleton(stack)), stack.getFilter(), stack.getRuleOrProxy());
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	static class Rule {
		final PathSetBasedRule original;
		final Set<Filter> filters = new HashSet<>();
		final BiMap<FilterInstance, ExistentialProxy> existentialProxies = HashBiMap.create();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
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
	@Value
	@EqualsAndHashCode(of = { "filterInstances" })
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
			graph = block.graph;
			filters.addAll(block.filters);
			for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> ruleAndMap : block.ruleToFilterToRow
					.entrySet()) {
				ruleToFilterToRow.put(ruleAndMap.getKey(), new HashMap<>(ruleAndMap.getValue()));
			}
			filterInstances.addAll(block.filterInstances);
			flatFilterInstances.addAll(block.flatFilterInstances);
			for (final Entry<FilterInstance, Set<ConflictEdge>> entry : block.borderConflicts.entrySet()) {
				borderConflicts.put(entry.getKey(), new HashSet<>(entry.getValue()));
			}
		}

		Set<Either<Rule, ExistentialProxy>> getRulesOrProxies() {
			return ruleToFilterToRow.keySet();
		}

		Set<Rule> getActualRuleInstances() {
			return ruleToFilterToRow.keySet().stream().filter(Either::isLeft).map(a -> a.left().get()).collect(toSet());
		}

		Set<ExistentialProxy> getExistentialProxies() {
			return ruleToFilterToRow.keySet().stream().filter(Either::isRight).map(a -> a.right().get())
					.collect(toSet());
		}

		public void addFilterInstance(final Either<Rule, ExistentialProxy> rule, final FilterInstance filterInstance) {
			addFilterInstances(Collections.singletonMap(rule,
					Collections.singleton(new FilterInstancesSideBySide(filterInstance))));
		}

		public void addFilterInstances(final Either<Rule, ExistentialProxy> rule,
				final FilterInstancesSideBySide sideBySides) {
			addFilterInstances(Collections.singletonMap(rule, Collections.singleton(sideBySides)));
		}

		public void addFilterInstances(
				final Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> ruleToNewFilterInstancesSideBySide) {
			for (final Entry<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> entry : ruleToNewFilterInstancesSideBySide
					.entrySet()) {
				final Either<Rule, ExistentialProxy> rule = entry.getKey();
				final Set<FilterInstancesSideBySide> sideBySides = entry.getValue();
				for (final FilterInstancesSideBySide sideBySide : sideBySides) {
					for (final FilterInstance prevOutside : sideBySide.instances) {
						final Set<ConflictEdge> newConflicts = new HashSet<>(graph.edgesOf(prevOutside));
						final Set<ConflictEdge> oldConflicts = borderConflicts.remove(prevOutside);
						if (null != oldConflicts) {
							newConflicts.removeAll(oldConflicts);
						}
						// group conflict edges by nodes that are outside now
						for (final ConflictEdge conflictEdge : newConflicts) {
							borderConflicts.computeIfAbsent(conflictEdge.getForSource(prevOutside).getTarget(),
									newHashSet()).add(conflictEdge);
						}
					}
					final Map<Filter, FilterInstancesSideBySide> filterInstancesOfThisRule =
							ruleToFilterToRow.computeIfAbsent(rule, newHashMap());
					final Filter filter = sideBySide.filter;
					final FilterInstancesSideBySide presentSideBySide = filterInstancesOfThisRule.get(filter);
					if (null != presentSideBySide) {
						filterInstancesOfThisRule.remove(presentSideBySide.getFilter(), presentSideBySide);
						filterInstances.remove(presentSideBySide);
						final LinkedHashSet<FilterInstance> instances = new LinkedHashSet<FilterInstance>();
						instances.addAll(presentSideBySide.getInstances());
						instances.addAll(sideBySide.getInstances());
						final FilterInstancesSideBySide newSideBySide = new FilterInstancesSideBySide(instances);
						filterInstancesOfThisRule.put(filter, newSideBySide);
						filterInstances.add(newSideBySide);
					} else {
						filterInstancesOfThisRule.put(filter, sideBySide);
						filterInstances.add(sideBySide);
					}
					flatFilterInstances.addAll(sideBySide.getInstances());
				}
			}
		}

		public boolean containedIn(final Block other) {
			if (!other.ruleToFilterToRow.keySet().containsAll(this.ruleToFilterToRow.keySet())) {
				return false;
			}
			if (!other.filters.containsAll(this.filters)) {
				return false;
			}
			/*
			 * before really considering multi cell filters, just check the sizes and containment
			 * for single cell filters
			 */
			final Set<Filter> multiFilters = new HashSet<>();
			for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> entry : this.ruleToFilterToRow
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
			final List<Either<Rule, ExistentialProxy>> rules = new ArrayList<>(this.ruleToFilterToRow.keySet());
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
	static class BlockSet {
		final HashSet<Block> blocks = new HashSet<>();
		final TreeMap<Integer, HashSet<Block>> ruleCountToBlocks = new TreeMap<>();
		final TreeMap<Integer, HashSet<Block>> filterCountToBlocks = new TreeMap<>();
		final HashMap<Either<Rule, ExistentialProxy>, HashSet<Block>> ruleInstanceToBlocks = new HashMap<>();
		final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> ruleCountToFilterCountToBlocks = new TreeMap<>();
		final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> filterCountToRuleCountToBlocks = new TreeMap<>();

		public BlockSet(final Set<Block> horizontallyMaximalBlocks) {
			horizontallyMaximalBlocks
					.stream()
					.sorted(Collections.reverseOrder(Comparator.comparingInt(composeToInt(Block::getRulesOrProxies,
							Set::size)))).forEachOrdered(this::orderedInsertionOfHorizontallyMaximalBlock);
		}

		private void orderedInsertionOfHorizontallyMaximalBlock(final Block block) {
			/*
			 * block is horizontally maximal, thus there can not be a block containing more filters
			 * with these or even more rules, so we just need to check the blocks containing more
			 * rules and the same filters. To make it feasible, we use the counts instead of the
			 * actual instances regarding rules and filters.
			 */
			final Integer ruleCount = block.getRulesOrProxies().size();
			final Integer filterCount = block.getFilters().size();
			final SortedMap<Integer, HashSet<Block>> fixedFilterCountRules =
					filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap()).tailMap(ruleCount, false);
			for (final Set<Block> fixedFilterCountRule : fixedFilterCountRules.values()) {
				for (final Block candidate : fixedFilterCountRule) {
					if (block.containedIn(candidate)) {
						return;
					}
				}
			}
			actuallyInsertBlockIntoAllCaches(block);
		}

		private void actuallyInsertBlockIntoAllCaches(final Block block) {
			blocks.add(block);
			final Integer ruleCount = block.getRulesOrProxies().size();
			final Integer filterCount = block.getFilters().size();
			ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).add(block);
			filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).add(block);
			ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
					.computeIfAbsent(filterCount, newHashSet()).add(block);
			filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
					.computeIfAbsent(ruleCount, newHashSet()).add(block);
			block.getRulesOrProxies().forEach(r -> ruleInstanceToBlocks.computeIfAbsent(r, newHashSet()).add(block));
		}

		public boolean isContained(final Block block) {
			final Integer ruleCount = block.getRulesOrProxies().size();
			final Integer filterCount = block.getFilters().size();
			for (final TreeMap<Integer, HashSet<Block>> treeMap : filterCountToRuleCountToBlocks.tailMap(filterCount)
					.values()) {
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
			if (!blocks.remove(block))
				return false;
			final Integer ruleCount = block.getRulesOrProxies().size();
			final Integer filterCount = block.getFilters().size();
			ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).remove(block);
			filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).remove(block);
			ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
					.computeIfAbsent(filterCount, newHashSet()).remove(block);
			filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
					.computeIfAbsent(ruleCount, newHashSet()).remove(block);
			block.getRulesOrProxies().forEach(r -> ruleInstanceToBlocks.computeIfAbsent(r, newHashSet()).remove(block));
			return true;
		}
	}

	private static Set<Filter> getFilters(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFilters, ExistentialProxy::getFilters);
	}

	private static void addRule(final PathSetBasedRule pathBasedRule, final List<Either<Rule, ExistentialProxy>> rules) {
		final Rule rule = new Rule(pathBasedRule);
		// first step: create all filter instances
		final Set<PathFilterSet> condition = pathBasedRule.getCondition();
		final Either<Rule, ExistentialProxy> ruleEither = Either.left(rule);
		final RuleConverter converter = new RuleConverter(rules, ruleEither);
		for (final PathFilterSet filter : condition) {
			filter.accept(converter);
		}
		// from this point on, the rule won't change any more (aka the filters and the existential
		// proxies have been added) => it can be used as a key in a HashMap

		// second step: determine conflicts between filter instances according to the paths used
		determineAllConflicts(rule.filters.stream().flatMap(f -> f.getInstances(ruleEither).stream()).collect(toSet()));
		for (final ExistentialProxy proxy : rule.existentialProxies.values()) {
			final Either<Rule, ExistentialProxy> proxyEither = Either.right(proxy);
			determineAllConflicts(proxy.filters.stream().flatMap(f -> f.getInstances(proxyEither).stream())
					.collect(toSet()));
		}
		// add rule to rule list
		rules.add(ruleEither);
	}

	private static void determineAllConflicts(final Set<FilterInstance> filterInstances) {
		for (final FilterInstance source : filterInstances) {
			for (final FilterInstance target : filterInstances) {
				if (source == target)
					continue;
				source.addConflict(target);
			}
		}
	}

	public static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraphForRules(
			final List<Either<Rule, ExistentialProxy>> ruleOrProxies) {
		return determineConflictGraph(ruleOrProxies
				.stream()
				.map(ruleOrProxy -> getFilters(ruleOrProxy).stream().flatMap(f -> f.getInstances(ruleOrProxy).stream())
						.collect(toList())).collect(toList()));
	}

	public static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraph(
			final Iterable<? extends List<FilterInstance>> filterInstancesGroupedByRule) {
		final UndirectedGraph<FilterInstance, ConflictEdge> graph = new SimpleGraph<>(ConflictEdge::of);
		for (final List<FilterInstance> instances : filterInstancesGroupedByRule) {
			instances.forEach(graph::addVertex);
			final int numInstances = instances.size();
			for (int i = 0; i < numInstances; i++) {
				final FilterInstance fi1 = instances.get(i);
				for (int j = i + 1; j < numInstances; j++) {
					final FilterInstance fi2 = instances.get(j);
					final ConflictEdge edge = ConflictEdge.of(fi1, fi2);
					if (null == edge)
						continue;
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

		@Override
		public void visit(final PathFilter pathFilter) {
			final Filter filter = convertFilter(pathFilter, Filter::newFilter);
			filter.addInstance(ruleOrProxy, pathFilter);
			getFilters(ruleOrProxy).add(filter);
		}

		protected static <T extends Filter> T convertFilter(final PathFilter pathFilter,
				final BiFunction<Predicate, List<Pair<Template, SlotAddress>>, T> ctor) {
			final PredicateWithArguments<PathLeaf> predicate = pathFilter.getFunction();
			assert predicate instanceof PredicateWithArgumentsComposite;
			return ctor.apply(((PredicateWithArgumentsComposite<PathLeaf>) predicate).getFunction(), PathLeafCollector
					.collect(predicate).stream().map(pl -> Pair.pair(pl.getPath().getTemplate(), pl.getSlot()))
					.collect(toList()));
		}

		@Override
		public void visit(final PathExistentialSet existentialSet) {
			final Rule rule =
					ruleOrProxy.left().getOrThrow(() -> new UnsupportedOperationException("Nested Existentials!"));
			// we may be able to share the existential closure part
			// existential closure filter instances are put into the same column if and only if they
			// have the same conflicts to their pure part and the pure parts have the same inner
			// conflicts

			final PathFilter existentialClosure = existentialSet.getExistentialClosure();
			final Set<PathFilterSet> purePart = existentialSet.getPurePart();

			final ExistentialProxy proxy = new ExistentialProxy(rule, existentialSet);
			final Either<Rule, ExistentialProxy> proxyEither = Either.right(proxy);
			final RuleConverter visitor = new RuleConverter(rules, proxyEither);

			// insert all pure filters into the proxy
			for (final PathFilterSet pathFilterSet : purePart) {
				pathFilterSet.accept(visitor);
			}
			// create own row for the pure part
			rules.add(proxyEither);

			final FilterProxy convertedExCl =
					convertFilter(existentialClosure, (pred, args) -> FilterProxy.newFilterProxy(pred, args, proxy));
			getFilters(ruleOrProxy).add(convertedExCl);
			final FilterInstance filterInstance = convertedExCl.addInstance(ruleOrProxy, existentialClosure);
			rule.existentialProxies.put(filterInstance, proxy);
		}
	}

	public static Collection<PathRule> transform(final Collection<PathSetBasedRule> rules) {
		final List<Either<Rule, ExistentialProxy>> translatedRules = new ArrayList<>();
		for (final PathSetBasedRule rule : rules) {
			addRule(rule, translatedRules);
		}
		// find all horizontally maximal blocks
		final Set<Block> resultBlocks = new HashSet<>();
		findAllHorizontallyMaximalBlocks(translatedRules, resultBlocks);
		// convert to maximal blocks via BlockSet-Constructor
		final BlockSet resultBlockSet = new BlockSet(resultBlocks);
		// solve the conflicts
		determineAndSolveConflicts(resultBlockSet);
		// transform into PathFilterList
		return createOutput(translatedRules, resultBlockSet);
	}

	private static Collection<PathRule> createOutput(final List<Either<Rule, ExistentialProxy>> rules,
			final BlockSet resultBlockSet) {
		final Function<? super Block, ? extends Integer> characteristicNumber =
				block -> block.getFlatFilterInstances().size() / block.getRulesOrProxies().size();
		final Map<Integer, CursorableLinkedList<Block>> blockMap =
				resultBlockSet.getBlocks().stream()
						.collect(groupingBy(characteristicNumber, toCollection(CursorableLinkedList::new)));

		// iterate over all the filter proxies ever used
		for (final FilterProxy filterProxy : FilterProxy.getFilterProxies()) {
			final Set<ExistentialProxy> existentialProxies = filterProxy.getProxies();
			// determine the largest characteristic number of the blocks containing filter instances
			// of one of the existential proxies (choice is arbitrary, since the filters and the
			// conflicts are identical if they belong to the same filter).
			final int eCN =
					resultBlockSet.getRuleInstanceToBlocks().get(Either.right(existentialProxies.iterator().next()))
							.stream().mapToInt(composeToInt(characteristicNumber, Integer::intValue)).max().getAsInt();
			// get the list to append the blocks using the existential closure filter instance to
			final CursorableLinkedList<Block> targetList = blockMap.get(eCN);
			// for every existential part
			for (final ExistentialProxy existentialProxy : existentialProxies) {
				final FilterInstance exClosure = existentialProxy.getExistentialClosure();
				// create a list storing the blocks to move
				final List<Block> toMove = new ArrayList<>();
				// scan all lists up to characteristic number eCN
				for (int i = 0; i <= eCN; ++i) {
					// iterate over the blocks in the current list
					for (final ListIterator<Block> iterator = blockMap.get(i).listIterator(); iterator.hasNext();) {
						final Block current = iterator.next();
						// if the current block uses the current existential closure filter
						// instance, it has to be moved
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
				final PathSharedListWrapper sharedListWrapper = new PathSharedListWrapper();
				for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
					final List<FilterInstance> fisToConstruct =
							block.ruleToFilterToRow.get(rule).values().stream()
									.flatMap(sbs -> sbs.getInstances().stream())
									.filter(negate(constructedFIs::contains)).collect(toList());
					final Map<FilterInstance, Set<FilterInstance>> fiToJoinedWith =
							ruleToJoinedWith.computeIfAbsent(rule, newHashMap());
					final Set<Set<FilterInstance>> joinedWithSets =
							fisToConstruct.stream()
									.map(fi -> fiToJoinedWith.computeIfAbsent(fi, x -> Sets.newHashSet(x)))
									.collect(toSet());
					final Set<FilterInstance> joinedWith =
							joinedWithSets.stream().flatMap(Set::stream).collect(toSet());
					joinedWith.forEach(fi -> fiToJoinedWith.put(fi, joinedWith));
					final PathSharedList newSharedElement =
							sharedListWrapper.newSharedElement(joinedWithSets
									.stream()
									.map(set -> joinedWithToComponent.computeIfAbsent(set, x -> x.iterator().next()
											.convert(ruleToJoinedWith, joinedWithToComponent))).collect(toList()));
					joinedWithToComponent.put(joinedWith, newSharedElement);
					constructedFIs.addAll(fisToConstruct);
				}
			}
		}
		final List<PathRule> pathRules = new ArrayList<>();
		for (final Either<Rule, ExistentialProxy> either : rules) {
			if (either.isRight()) {
				continue;
			}
			final List<PathFilterList> pathFilterLists =
					ruleToJoinedWith.get(either).values().stream().distinct().map(joinedWithToComponent::get)
							.collect(toList());
			pathRules.add(either.left().get().getOriginal()
					.toPathRule(new PathSharedListWrapper().newSharedElement(pathFilterLists)));
		}
		return pathRules;
	}

	private static void findAllHorizontallyMaximalBlocks(final List<Either<Rule, ExistentialProxy>> rules,
			final Set<Block> resultBlocks) {
		final UndirectedGraph<FilterInstance, ConflictEdge> conflictGraph = determineConflictGraphForRules(rules);
		final Set<Filter> filters = rules.stream().flatMap(rule -> getFilters(rule).stream()).collect(toSet());
		for (final Filter filter : filters) {
			vertical(conflictGraph, new HashSet<>(filter.getRuleToInstances().values()), resultBlocks);
		}
	}

	private static <T, K, D> Collector<T, ?, Set<D>> groupingIntoSets(
			final Function<? super T, ? extends K> classifier, final Collector<? super T, ?, D> downstream) {
		final Collector<T, ?, Map<K, D>> groupingBy = groupingBy(classifier, downstream);
		return Collectors.collectingAndThen(groupingBy, map -> new HashSet<D>(map.values()));
	}

	private static Set<Block> findAllHorizontallyMaximalBlocksInReducedScope(final Set<FilterInstance> filterInstances,
			final Set<Block> resultBlocks) {
		final Iterable<List<FilterInstance>> filterInstancesGroupedByRule =
				filterInstances.stream().collect(
						Collectors.collectingAndThen(groupingBy(FilterInstance::getRuleOrProxy), Map::values));
		final UndirectedGraph<FilterInstance, ConflictEdge> conflictGraph =
				determineConflictGraph(filterInstancesGroupedByRule);
		final Set<Set<Set<FilterInstance>>> filterInstancesGroupedByFilterAndByRule =
				filterInstances.stream().collect(
						groupingIntoSets(FilterInstance::getFilter,
								groupingIntoSets(FilterInstance::getRuleOrProxy, toSet())));
		for (final Set<Set<FilterInstance>> filterInstancesOfOneFilterGroupedByRule : filterInstancesGroupedByFilterAndByRule) {
			vertical(conflictGraph, filterInstancesOfOneFilterGroupedByRule, resultBlocks);
		}
		return resultBlocks;
	}

	public static void determineAndSolveConflicts(final BlockSet resultBlocks) {
		// determine conflicts
		final BlockSet deletedBlocks = new BlockSet(Collections.emptySet());
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
			final BlockSet resultBlocks, final Block x) {
		for (final Either<Rule, ExistentialProxy> rule : x.getRulesOrProxies()) {
			for (final Block y : resultBlocks.getRuleInstanceToBlocks().get(rule)) {
				if (x == y) {
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
		if (null == arc)
			return;
		final Set<BlockConflict> oldArcs = blockConflictGraph.outgoingEdgesOf(x);
		// determine arc's quality
		final Set<FilterInstance> xFIs = x.getFlatFilterInstances();
		final Set<FilterInstance> yFIs = y.getFlatFilterInstances();
		arc.quality =
				oldArcs.stream()
						.mapToInt(
								conf -> usefulness(arc, xFIs, conf, conf.getConflictingBlock().getFlatFilterInstances())
										- (Sets.difference(arc.cfi, yFIs).size() + (Sets.intersection(xFIs, yFIs)
												.isEmpty() ? 0 : 1))).sum();
		// update quality of all arcs affected
		for (final BlockConflict conf : oldArcs) {
			final Set<FilterInstance> bFIs = conf.getConflictingBlock().getFlatFilterInstances();
			conf.quality += usefulness(arc, xFIs, conf, bFIs);
		}
		// add the new edge
		final boolean arcAdded = blockConflictGraph.addEdge(x, y, arc);
		assert arcAdded;
	}

	private static int usefulness(final BlockConflict xyArc, final Set<FilterInstance> xFIs, final BlockConflict xbArc,
			final Set<FilterInstance> bFIs) {
		return Sets.intersection(Sets.difference(xbArc.cfi, bFIs), xyArc.cfi).size()
				+ (Sets.difference(Sets.intersection(xFIs, bFIs), xyArc.cfi).isEmpty() ? 0 : 1);
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
				if (xbArc == xyArc)
					// excluding the arc getting deleted
					continue;
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
			final Set<FilterInstance> cfi =
					replaceBlock
							.getFlatFilterInstances()
							.stream()
							.filter(xFI -> yFIsByRule.get(xFI.getRuleOrProxy()).stream()
									.anyMatch(yFI -> null != yFI.getOrDetermineConflicts(xFI))).collect(toSet());
			if (cfi.isEmpty())
				return null;
			// if non-overlapping
			if (Collections.disjoint(replaceBlock.getFlatFilterInstances(), conflictingBlock.getFlatFilterInstances())) {
				return new BlockConflict(replaceBlock, conflictingBlock, cfi);
			}
			// else overlapping
			// check whether X\Y and Y\X are blocks
			if (!allColumnsSameHeight(replaceBlock, conflictingBlock)
					|| !allColumnsSameHeight(conflictingBlock, replaceBlock)) {
				return new BlockConflict(replaceBlock, conflictingBlock, cfi);
			}
			return null;
		}

		private static boolean allColumnsSameHeight(final Block x, final Block y) {
			// check whether X\Y is a block by checking whether all columns of X\Y have the same
			// height
			final Set<FilterInstance> yFIs = y.getFlatFilterInstances();
			final Set<List<FilterInstance>> columns =
					Block.getFilterInstanceColumns(x.getFilters(), x.getRuleToFilterToRow(),
							Lists.newArrayList(x.getRulesOrProxies()));
			if (1L != columns.stream().mapToLong(col -> col.stream().filter(negate(yFIs::contains)).count()).distinct()
					.count()) {
				// not a block
				return false;
			}
			return true;
		}
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	// no @EqualsAndHashCode so equals will not prevent new edges
	static class BlockConflictEdge {
		final BlockConflict a, b;

		public BlockConflict getForReplaceBlock(final Block replaceBlock) {
			assert a.getReplaceBlock() == replaceBlock || b.getReplaceBlock() == replaceBlock;
			return a.getReplaceBlock() == replaceBlock ? a : b;
		}

		public BlockConflict getForConflictingBlock(final Block conflictingBlock) {
			assert a.getConflictingBlock() == conflictingBlock || b.getConflictingBlock() == conflictingBlock;
			return a.getConflictingBlock() == conflictingBlock ? a : b;
		}

		public static BlockConflictEdge of(final Block x, final Block y) {
			final BlockConflict xy = BlockConflict.of(x, y);
			if (null == xy)
				return null;
			return new BlockConflictEdge(xy, BlockConflict.of(y, x));
		}
	}

	public static void solveConflict(final BlockConflict blockConflict,
			final DirectedGraph<Block, BlockConflict> blockConflictGraph, final BlockSet resultBlocks,
			final BlockSet deletedBlocks) {
		final Block replaceBlock = blockConflict.getReplaceBlock();
		final Set<FilterInstance> xWOcfi =
				replaceBlock.getFlatFilterInstances().stream().filter(negate(blockConflict.getCfi()::contains))
						.collect(toSet());
		resultBlocks.remove(replaceBlock);
		// remove replaceBlock and update qualities
		removeArc(blockConflictGraph, blockConflict);
		// find the horizontally maximal blocks within xWOcfi
		final Set<Block> newBlocks = findAllHorizontallyMaximalBlocksInReducedScope(xWOcfi, new HashSet<>());
		// for every such block,
		for (final Block block : newBlocks) {
			if (!deletedBlocks.isContained(block)) {
				if (resultBlocks.addDuringConflictResolution(block)) {
					blockConflictGraph.addVertex(block);
					createArcs(blockConflictGraph, resultBlocks, block);
				}
			}
		}
		deletedBlocks.addDuringConflictResolution(replaceBlock);
	}

	public static void vertical(final UndirectedGraph<FilterInstance, ConflictEdge> graph,
			final Set<Set<FilterInstance>> filterInstancesGroupedByRule, final Set<Block> resultBlocks) {
		final Set<Set<Set<FilterInstance>>> filterInstancesPowerSet = Sets.powerSet(filterInstancesGroupedByRule);
		for (final Set<Set<FilterInstance>> powerSetElement : filterInstancesPowerSet) {
			final Set<List<FilterInstance>> cartesianProduct = Sets.cartesianProduct(new ArrayList<>(powerSetElement));
			for (final List<FilterInstance> filterInstances : cartesianProduct) {
				final Block newBlock = new Block(graph);
				newBlock.addFilterInstances(filterInstances.stream().collect(
						toMap(FilterInstance::getRuleOrProxy,
								fi -> Collections.singleton(new FilterInstancesSideBySide(fi)))));
				horizontalRecursion(newBlock, new Stack<>(), resultBlocks);
			}
		}
	}

	public static void horizontalRecursion(final Block block, final Stack<Set<FilterInstance>> exclusionStack,
			final Set<Block> resultBlocks) {
		// needed: the filters that are contained in every rule of the block, where for every
		// filter it is the case that: every rule contains at least one instance not already
		// excluded by the exclusion stack
		// thus: get the non-excluded filter instances
		final Set<FilterInstance> neighbours =
				block.getBorderConflicts().keySet().stream()
						.filter(fi -> !exclusionStack.stream().anyMatch(as -> as.contains(fi))).collect(toSet());
		// group them by their filter
		final Map<Filter, List<FilterInstance>> filterToInstances =
				neighbours.stream().collect(groupingBy(FilterInstance::getFilter));
		// get all the rules in the block
		final Set<Either<Rule, ExistentialProxy>> rulesInBlock = block.getRuleToFilterToRow().keySet();
		// get a map from filter to all rules containing instances of that filter
		final Map<Filter, Set<Either<Rule, ExistentialProxy>>> filterToRulesContainingIt =
				filterToInstances
						.entrySet()
						.stream()
						.collect(
								toMap(Entry::getKey, e -> e.getValue().stream().map(FilterInstance::getRuleOrProxy)
										.collect(toSet())));
		// get the filters that are contained in every rule
		final Set<Filter> relevantFilters =
				filterToInstances.keySet().stream()
						.filter(f -> filterToRulesContainingIt.get(f).containsAll(rulesInBlock)).collect(toSet());
		// if no filters are left to add, the block is horizontally maximized, add it
		if (relevantFilters.isEmpty()) {
			resultBlocks.add(block);
			return;
		}
		// divide into filters without multiple instances and filters with multiple instances
		final List<Filter> singleCellFilters, multiCellFilters;
		{
			final Map<Boolean, List<Filter>> partition =
					relevantFilters.stream().collect(
							partitioningBy(f -> filterToInstances.get(f).size() > rulesInBlock.size()));
			singleCellFilters = partition.get(Boolean.FALSE);
			multiCellFilters = partition.get(Boolean.TRUE);
		}
		// list of rule-filter-matchings that may be added
		final List<Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>>> matchingFilters =
				new ArrayList<>();
		final List<Filter> incompatibleFilters = new ArrayList<>();

		// there is a 1 to 1 mapping from filter instances (side-by-side) to rules
		// for every filter instance, the conflicts have to be the same in all rules
		final Map<Filter, Map<Either<Rule, ExistentialProxy>, FilterInstancesSideBySide>> filterToRuleToBlockInstances =
				block.getFilterInstances()
						.stream()
						.collect(
								groupingBy(FilterInstancesSideBySide::getFilter,
										toMap(FilterInstancesSideBySide::getRuleOrProxy, Function.identity())));
		// prefer singleCellFilters
		findMatchingAndIncompatibleFilters(filterToInstances, rulesInBlock, singleCellFilters, matchingFilters,
				incompatibleFilters, filterToRuleToBlockInstances);
		// if none matched, try multiCellFilters, otherwise defer them
		if (matchingFilters.isEmpty()) {
			findMatchingAndIncompatibleFilters(filterToInstances, rulesInBlock, multiCellFilters, matchingFilters,
					incompatibleFilters, filterToRuleToBlockInstances);
			// if still none matched, the block is maximal, add it to the result blocks
			if (matchingFilters.isEmpty()) {
				resultBlocks.add(block);
				return;
			}
		}

		// create the next exclusion layer
		final Set<FilterInstance> furtherExcludes = new HashSet<>();
		// add it to the stack
		exclusionStack.push(furtherExcludes);
		// add all incompatible filter instances to the exclusion stack
		for (final Filter incompatibleFilter : incompatibleFilters) {
			for (final Either<Rule, ExistentialProxy> rule : rulesInBlock) {
				furtherExcludes.addAll(incompatibleFilter.getInstances(rule));
			}
		}
		// for every matching filter instance set, create a new block
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

	private static void findMatchingAndIncompatibleFilters(
			final Map<Filter, List<FilterInstance>> filterToInstances,
			final Set<Either<Rule, ExistentialProxy>> rulesInBlock,
			final List<Filter> filters,
			final List<Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>>> matchingFilters,
			final List<Filter> incompatibleFilters,
			final Map<Filter, Map<Either<Rule, ExistentialProxy>, FilterInstancesSideBySide>> filterToRuleToBlockInstances) {
		// iterate over every single-/multi-cell filter and check that its instances have the same
		// conflicts in every rule
		for (final Filter filter : filters) {
			// get the mapping from rule to filter instance for the current filter
			final Map<Either<Rule, ExistentialProxy>, FilterInstancesSideBySide> ruleToBlockInstances =
					filterToRuleToBlockInstances.get(filter);
			// list of conflicts for this filter
			final List<Conflict> conflicts = new ArrayList<>();
			boolean matchingConstellationFound = false;

			// iterate over the possible mappings: (filter,rule) -> filter instance
			final List<Set<FilterInstance>> listOfRelevantFilterInstancesGroupedByRule =
					new ArrayList<>(filterToInstances.get(filter).stream()
							.collect(groupingBy(FilterInstance::getRuleOrProxy, toSet())).values());
			// create the cartesian product
			final Set<List<FilterInstance>> relevantFilterInstanceCombinations =
					Sets.cartesianProduct(listOfRelevantFilterInstancesGroupedByRule);
			// iterate over the possible filter instance combinations
			cartesianProductLoop: for (final List<FilterInstance> currentOutsideFilterInstances : relevantFilterInstanceCombinations) {
				// create a map for faster lookup: rule -> filter instance (outside)
				final Map<Either<Rule, ExistentialProxy>, FilterInstance> ruleToCurrentOutsideFilterInstance =
						currentOutsideFilterInstances.stream().collect(
								toMap(FilterInstance::getRuleOrProxy, Function.identity()));
				// iterate over the Rule-BlockFilterInstance-mappings
				for (final Entry<Either<Rule, ExistentialProxy>, FilterInstancesSideBySide> entry : ruleToBlockInstances
						.entrySet()) {
					final Either<Rule, ExistentialProxy> rule = entry.getKey();
					final FilterInstancesSideBySide sideBySide = entry.getValue();
					// get the corresponding filter instance(s) that may be added
					final FilterInstance source = ruleToCurrentOutsideFilterInstance.get(rule);
					int i = 0;
					// iterate over the filter instances
					for (final FilterInstance target : sideBySide.getInstances()) {
						// determine conflict between inside instance and outside instance
						final Conflict conflict = source.getOrDetermineConflicts(target);
						// if this is the first loop iteration, just add the conflict to be compared
						// later on
						if (i >= conflicts.size()) {
							conflicts.add(conflict);
						}
						// if the conflicts don't match, continue with next filter
						else if (!conflicts.get(i).equals(conflict)) {
							continue cartesianProductLoop;
						}
						++i;
					}
				}
				// conflict identical for all rules
				matchingFilters.add(rulesInBlock.stream().collect(
						toMap(Function.identity(), rule -> Collections.singleton(new FilterInstancesSideBySide(
								ruleToCurrentOutsideFilterInstance.get(rule))))));
				matchingConstellationFound = true;
			}
			if (!matchingConstellationFound) {
				incompatibleFilters.add(filter);
			}
		}
	}

	@Getter
	@Setter
	// no @EqualsAndHashCode so equals will not prevent new edges
	static class ConflictEdge {
		final Conflict a, b;

		public ConflictEdge(final FilterInstance x, final FilterInstance y) {
			this.a = x.getOrDetermineConflicts(y);
			this.b = y.getOrDetermineConflicts(x);
		}

		public Conflict getForSource(final FilterInstance sourceFilterInstance) {
			assert a.getSource() == sourceFilterInstance || b.getSource() == sourceFilterInstance;
			return a.getSource() == sourceFilterInstance ? a : b;
		}

		public Conflict getForTarget(final FilterInstance targetFilterInstance) {
			assert a.getTarget() == targetFilterInstance || b.getTarget() == targetFilterInstance;
			return a.getTarget() == targetFilterInstance ? a : b;
		}

		public static ConflictEdge of(final FilterInstance x, final FilterInstance y) {
			final Conflict a = x.getOrDetermineConflicts(y);
			if (null == a)
				return null;
			return new ConflictEdge(x, y);
		}
	}

	static SetView<FilterInstance> getStableSet(final UndirectedGraph<FilterInstance, ConflictEdge> graph) {
		// could be improved by DOI 10.1137/0206036
		return Sets.difference(graph.vertexSet(), VertexCovers.find2ApproximationCover(graph));
	}
}
