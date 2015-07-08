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
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

import org.jamocha.dn.ConstructCache.Defrule.PathSetBasedRule;
import org.jamocha.dn.compiler.simpleblocks.Matrix.Filter.FilterInstance;
import org.jamocha.dn.compiler.simpleblocks.Matrix.Filter.FilterInstance.Conflict;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterSet;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.filter.PathFilterSetVisitor;
import org.jamocha.filter.PathLeafCollector;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.VertexCovers;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Pair;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class Matrix {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	@EqualsAndHashCode(of = { "predicate", "arguments" })
	static class Filter {
		final Predicate predicate;
		final List<Pair<Template, SlotAddress>> arguments;
		final Map<Either<Rule, ExistentialProxy>, Set<FilterInstance>> ruleToInstances = new HashMap<>();

		public FilterInstance addInstance(final Either<Rule, ExistentialProxy> ruleOrProxy, final PathFilter pathFilter) {
			final ArrayList<PathLeaf> parameterLeafs = PathLeafCollector.collect(pathFilter.getFunction());
			final List<Path> parameterPaths = parameterLeafs.stream().map(PathLeaf::getPath).collect(toList());
			final FilterInstance instance = new FilterInstance(pathFilter, parameterPaths, ruleOrProxy);
			ruleToInstances.computeIfAbsent(ruleOrProxy, x -> new HashSet<>()).add(instance);
			return instance;
		}

		public Set<FilterInstance> getInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
			return ruleToInstances.get(ruleOrProxy);
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
									.collect(groupingBy(i -> targetParameters.get(i)));
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

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	@AllArgsConstructor
	@EqualsAndHashCode(of = { "stacks" })
	static class FilterInstancesSideBySide implements Iterable<FilterInstance> {
		final LinkedHashSet<FilterInstancesStack> stacks;
		final Filter filter;
		final Either<Rule, ExistentialProxy> ruleOrProxy;

		public FilterInstancesSideBySide(final LinkedHashSet<FilterInstancesStack> stacks) {
			this(stacks, stacks.iterator().next().getFilter(), stacks.iterator().next().getRuleOrProxy());
			assert 1 == stacks.stream().map(FilterInstancesStack::getFilter).collect(toSet()).size();
		}

		public FilterInstancesSideBySide(final FilterInstancesStack stack) {
			this(new LinkedHashSet<>(Collections.singleton(stack)), stack.getFilter(), stack.getRuleOrProxy());
		}

		@Override
		public Iterator<FilterInstance> iterator() {
			return Iterables.concat(stacks).iterator();
		}

		public Stream<FilterInstance> stream() {
			return StreamSupport.stream(spliterator(), false);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	@AllArgsConstructor
	@EqualsAndHashCode(of = { "instances" })
	static class FilterInstancesStack implements Iterable<FilterInstance> {
		final LinkedHashSet<FilterInstance> instances;
		final Filter filter;
		final Either<Rule, ExistentialProxy> ruleOrProxy;

		public FilterInstancesStack(final LinkedHashSet<FilterInstance> instances) {
			this(instances, instances.iterator().next().getFilter(), instances.iterator().next().getRuleOrProxy());
			assert 1 == instances.stream().map(FilterInstance::getFilter).collect(toSet()).size();
		}

		public FilterInstancesStack(final FilterInstance instance) {
			this(new LinkedHashSet<>(Collections.singleton(instance)), instance.getFilter(), instance.getRuleOrProxy());
		}

		@Override
		public Iterator<FilterInstance> iterator() {
			return instances.iterator();
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	static class Rule {
		final PathSetBasedRule original;
		final Set<Filter> filters = new HashSet<>();
		final Set<ExistentialProxy> existentialProxies = new HashSet<>();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	static class ExistentialProxy {
		final Rule rule;
		final PathExistentialSet existential;
		final Set<Filter> filters = new HashSet<>();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	@EqualsAndHashCode(of = { "filterInstances" })
	@RequiredArgsConstructor
	public class Block {
		// conflict graph
		final UndirectedGraph<FilterInstance, ConflictEdge> graph;
		// abstract filters of the block
		final Set<Filter> filters = new HashSet<>();
		// mapping from rule to cells in that row
		// keySet of this map implicitly gives all the rules
		final Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> ruleToRow = new HashMap<>();
		// contains the filterInstances of this block correctly arranged (side-by-side/stacked)
		final Set<FilterInstancesSideBySide> filterInstances = new HashSet<>();
		// all conflicts between filter instances inside of the block
		final Set<ConflictEdge> innerConflicts = new HashSet<>();
		// conflicts between filter instances, where the source has to be outside and the target
		// inside of the block, grouped by the one outside
		final Map<FilterInstance, Set<ConflictEdge>> borderConflicts = new HashMap<>();

		public Block(final Block block) {
			graph = block.graph;
			filters.addAll(block.filters);
			for (final Entry<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> entry : block.ruleToRow
					.entrySet()) {
				ruleToRow.put(entry.getKey(), new HashSet<>(entry.getValue()));
			}
			filterInstances.addAll(block.filterInstances);
			innerConflicts.addAll(block.innerConflicts);
			for (final Entry<FilterInstance, Set<ConflictEdge>> entry : block.borderConflicts.entrySet()) {
				borderConflicts.put(entry.getKey(), new HashSet<>(entry.getValue()));
			}
		}

		Set<Rule> getActualRuleInstances() {
			return ruleToRow.keySet().stream().filter(Either::isLeft).map(a -> a.left().get()).collect(toSet());
		}

		Set<ExistentialProxy> getExistentialProxies() {
			return ruleToRow.keySet().stream().filter(Either::isRight).map(a -> a.right().get()).collect(toSet());
		}

		public void addFilterInstance(final Either<Rule, ExistentialProxy> rule, final FilterInstance filterInstance) {
			addFilterInstances(Collections.singletonMap(rule,
					Collections.singleton(new FilterInstancesSideBySide(new FilterInstancesStack(filterInstance)))));
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
					for (final FilterInstancesStack stack : sideBySide.stacks) {
						for (final FilterInstance prevOutside : stack.instances) {
							final Set<ConflictEdge> newConflicts = new HashSet<>(graph.edgesOf(prevOutside));
							final Set<ConflictEdge> oldConflicts = borderConflicts.remove(prevOutside);
							if (null != oldConflicts) {
								innerConflicts.addAll(oldConflicts);
								newConflicts.removeAll(oldConflicts);
							}
							// group conflict edges by nodes that are outside now
							for (final ConflictEdge conflictEdge : newConflicts) {
								borderConflicts.computeIfAbsent(conflictEdge.getForSource(prevOutside).getTarget(),
										x -> new HashSet<>()).add(conflictEdge);
							}
						}
					}
					final Set<FilterInstancesSideBySide> filterInstancesOfThisRule =
							ruleToRow.computeIfAbsent(rule, x -> new HashSet<>());
					final Optional<FilterInstancesSideBySide> optionalFISBS =
							filterInstancesOfThisRule.stream().filter(sbs -> sbs.filter == sideBySide.filter).findAny();
					if (optionalFISBS.isPresent()) {
						final FilterInstancesSideBySide presentSideBySide = optionalFISBS.get();
						filterInstancesOfThisRule.remove(presentSideBySide);
						filterInstances.remove(presentSideBySide);
						final LinkedHashSet<FilterInstancesStack> stacks = new LinkedHashSet<FilterInstancesStack>();
						stacks.addAll(presentSideBySide.getStacks());
						stacks.addAll(sideBySide.getStacks());
						final FilterInstancesSideBySide newSideBySide = new FilterInstancesSideBySide(stacks);
						filterInstancesOfThisRule.add(newSideBySide);
						filterInstances.add(newSideBySide);
					} else {
						filterInstancesOfThisRule.add(sideBySide);
						filterInstances.add(sideBySide);
					}
				}
			}
		}

		public Set<Block> initializeHorizontally(final FilterInstance filterInstance) {
			assert filterInstances.isEmpty() : "Block already initialized!";
			final Filter filter = filterInstance.getFilter();
			final Either<Rule, ExistentialProxy> ruleOrProxy = filterInstance.getRuleOrProxy();
			final Set<FilterInstance> possibleSiblings = filterInstance.getSiblings();
			if (1 == possibleSiblings.size()) {
				// simple case
				boolean multipleFilterInstances = false;
				// add starting filter instance
				addFilterInstances(Collections.singletonMap(ruleOrProxy,
						Collections.singleton(new FilterInstancesSideBySide(new FilterInstancesStack(filterInstance)))));
				assert !filterInstances.isEmpty();
				// get relevant filter instances by looking at the conflicting edges while there are
				// conflicts remaining within the rule
				while (!borderConflicts.keySet().isEmpty()) {
					final Set<ConflictEdge> conflictEdges =
							new HashSet<>(borderConflicts.get(borderConflicts.keySet().iterator().next()));
					final Map<Boolean, List<FilterInstance>> partitions =
							conflictEdges.stream().map(e -> e.getForSource(filterInstance).getTarget())
									.collect(partitioningBy(t -> t.getSiblings().size() == 1));
					addFilterInstances(Collections.singletonMap(
							ruleOrProxy,
							partitions.get(Boolean.TRUE).stream()
									.map(fi -> new FilterInstancesSideBySide(new FilterInstancesStack(fi)))
									.collect(toSet())));

					final Map<Filter, Set<FilterInstance>> filterToSiblings =
							partitions.get(Boolean.FALSE).stream()
									.collect(groupingBy(fi -> fi.getFilter(), HashMap::new, toSet()));
					for (final Set<FilterInstance> fis : filterToSiblings.values()) {
						multipleFilterInstances = true;
						addFilterInstances(Collections.singletonMap(
								ruleOrProxy,
								Collections.singleton(new FilterInstancesSideBySide(fis.stream()
										.map(FilterInstancesStack::new)
										.collect(toCollection(() -> new LinkedHashSet<>()))))));
					}
				}
				// at this point, the block is maximally extended horizontally
				// yet, it might be possible to put some of the filter instances within side-by-side
				// sets on top of each other
				final HashSet<Block> resultBlocks = new HashSet<>();
				resultBlocks.add(this);
				if (multipleFilterInstances) {
					recursivelyEnumeratePossibleBlocks(this, resultBlocks,
							toArray(filterInstances, FilterInstancesSideBySide[]::new), 0);
				}
				return resultBlocks;
			} else {
				// harder case
				return new HashSet<>();
			}
		}

		private void recursivelyEnumeratePossibleBlocks(final Block last, final Set<Block> resultBlocks,
				final FilterInstancesSideBySide[] sideBySides, final int currentIndex) {
			if (sideBySides.length == currentIndex) {
				resultBlocks.add(last);
			}
			final FilterInstancesSideBySide current = sideBySides[currentIndex];
			if (1 == current.getStacks().size()) {
				// since the upcoming stacks all contain single filter instances, we don't need to
				// inspect the sizes of the stacks
				recursivelyEnumeratePossibleBlocks(last, resultBlocks, sideBySides, currentIndex + 1);
			} else {
				// collect all siblings that are also part of the block
				final Set<FilterInstance> siblingsInBlock =
						current.getStacks().stream().flatMap(s -> s.getInstances().stream()).collect(toSet());
				final UndirectedSubgraph<FilterInstance, ConflictEdge> subgraph =
						new UndirectedSubgraph<>(graph, siblingsInBlock, (Set<ConflictEdge>) null);
				// simplified: just one possibility is checked
				// could be improved by DOI 10.1137/0206036
				final Set<FilterInstance> vertexCover = VertexCovers.find2ApproximationCover(subgraph);
				final SetView<FilterInstance> stableSet = Sets.difference(subgraph.vertexSet(), vertexCover);
				// stable set -> one stack
				// vertex cover into n stacks
				final LinkedHashSet<FilterInstancesStack> newStacks = new LinkedHashSet<>();
				newStacks.add(new FilterInstancesStack(new LinkedHashSet<>(stableSet)));
				for (final FilterInstance filterInstance : vertexCover) {
					newStacks.add(new FilterInstancesStack(filterInstance));
				}
				final Block next = new Block(last);
				next.filterInstances.remove(current);
				next.filterInstances.add(new FilterInstancesSideBySide(newStacks));
				// final List<Set<FilterInstance>> connectedComponents =
				// new ConnectivityInspector<FilterInstance,
				// ConflictEdge>(subgraph).connectedSets();
				recursivelyEnumeratePossibleBlocks(next, resultBlocks, sideBySides, currentIndex + 1);
			}
		}

		public void initializeVertically() {
			assert filterInstances.isEmpty() : "Block already initialized!";
		}

		public boolean expandVertically(final List<Either<Rule, ExistentialProxy>> rules) {
			assert !filterInstances.isEmpty() : "Block not initialized!";
			return false;
		}

		public boolean expandHorizontally() {
			assert !filterInstances.isEmpty() : "Block not initialized!";
			for (final Entry<FilterInstance, Set<ConflictEdge>> entry : this.borderConflicts.entrySet()) {
				final FilterInstance filterInstance = entry.getKey();
				final Set<ConflictEdge> conflictEdges = entry.getValue();
				for (final ConflictEdge conflictEdge : conflictEdges) {
					final Conflict conflict = conflictEdge.getForSource(filterInstance);
					final Set<Either<Rule, ExistentialProxy>> rulesInBlock = ruleToRow.keySet();

				}
			}
			return false;
		}

		// Idee: alle Methoden, die den Block verändern, geben eine Kopie zurück.
	}

	final private List<Either<Rule, ExistentialProxy>> rules = new ArrayList<>();
	final private Set<Block> blocks = new HashSet<>();

	private static Set<Filter> getFilters(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFilters, ExistentialProxy::getFilters);
	}

	public void addRule(final PathSetBasedRule pathBasedRule) {
		final Rule rule = new Rule(pathBasedRule);
		// first step: create all filter instances
		final Set<PathFilterSet> condition = pathBasedRule.getCondition();
		final Either<Rule, ExistentialProxy> ruleEither = Either.left(rule);
		final RuleConverter converter = new RuleConverter(ruleEither);
		for (final PathFilterSet filter : condition) {
			filter.accept(converter);
		}
		// from this point on, the rule won't change any more (aka the filters and the existential
		// proxies have been added) => it can be used as a key in a HashMap

		// second step: determine conflicts between filter instances according to the paths used
		{
			final Set<FilterInstance> filterInstances =
					rule.filters.stream().flatMap(f -> f.getInstances(ruleEither).stream()).collect(toSet());
			for (final FilterInstance source : filterInstances) {
				for (final FilterInstance target : filterInstances) {
					source.addConflict(target);
				}
			}
		}
		for (final ExistentialProxy proxy : rule.existentialProxies) {
			final Set<FilterInstance> filterInstances =
					proxy.filters.stream().flatMap(f -> f.getInstances(Either.right(proxy)).stream()).collect(toSet());
			for (final FilterInstance source : filterInstances) {
				for (final FilterInstance target : filterInstances) {
					source.addConflict(target);
				}
			}
		}
		// add rule and proxies to rule list
		rules.add(ruleEither);
		rule.existentialProxies.forEach(p -> rules.add(Either.right(p)));
	}

	public static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraph(
			final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return determineConflictGraph(Collections.singleton(ruleOrProxy));
	}

	public static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraph(
			final Iterable<Either<Rule, ExistentialProxy>> ruleOrProxies) {
		final UndirectedGraph<FilterInstance, ConflictEdge> graph = new SimpleGraph<>(ConflictEdge::new);
		for (final Either<Rule, ExistentialProxy> ruleOrProxy : ruleOrProxies) {
			final List<FilterInstance> instances =
					getFilters(ruleOrProxy).stream().flatMap(f -> f.getInstances(ruleOrProxy).stream())
							.collect(toList());
			instances.forEach(v -> graph.addVertex(v));
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
		final Either<Rule, ExistentialProxy> ruleOrProxy;

		@Override
		public void visit(final PathFilter pathFilter) {
			final PredicateWithArguments<PathLeaf> predicate = pathFilter.getFunction();
			assert predicate instanceof PredicateWithArgumentsComposite;
			final Filter filter =
					new Filter(((PredicateWithArgumentsComposite<PathLeaf>) predicate).getFunction(), PathLeafCollector
							.collect(predicate).stream().map(pl -> Pair.pair(pl.getPath().getTemplate(), pl.getSlot()))
							.collect(toList()));
			getFilters(ruleOrProxy).add(filter);
			filter.addInstance(ruleOrProxy, pathFilter);
		}

		@Override
		public void visit(final PathExistentialSet existentialSet) {
			final Rule rule =
					ruleOrProxy.left().getOrThrow(() -> new UnsupportedOperationException("Nested Existentials!"));
			final ExistentialProxy proxy = new ExistentialProxy(rule, existentialSet);
			rule.existentialProxies.add(proxy);
			final RuleConverter visitor = new RuleConverter(Either.right(proxy));
			existentialSet.getPurePart().forEach(f -> f.accept(visitor));
			existentialSet.getExistentialClosure().forEach(f -> f.accept(visitor));
		}
	}

	public void caller(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		final Set<Filter> filters = getFilters(ruleOrProxy);
		final Map<Filter, Set<FilterInstance>> filterToInstanceSet =
				filters.stream().collect(toMap(Function.identity(), f -> f.getInstances(ruleOrProxy)));
		final UndirectedGraph<FilterInstance, ConflictEdge> conflictGraph = determineConflictGraph(ruleOrProxy);
		final Set<List<FilterInstance>> cartesianProduct =
				Sets.cartesianProduct(new ArrayList<>(filterToInstanceSet.values()));
		for (final List<FilterInstance> instances : cartesianProduct) {
			recursivelyMaximizeHorizontally(new HashSet<>(instances), new Block(conflictGraph), new Stack<>(), blocks);
		}
	}

	public void recursivelyMaximizeHorizontally(final Set<FilterInstance> neighbouringFilterCandidates,
			final Block block, final Stack<Set<FilterInstance>> exclusionStack, final Set<Block> resultBlocks) {
		final Set<FilterInstance> neighbours =
				block.getBorderConflicts()
						.keySet()
						.stream()
						.filter(fi -> neighbouringFilterCandidates.contains(fi)
								&& !exclusionStack.stream().anyMatch(as -> as.contains(fi))).collect(toSet());
		if (neighbours.isEmpty()) {
			resultBlocks.add(block);
		}
		final Set<FilterInstance> furtherExcludes = new HashSet<>();
		exclusionStack.push(furtherExcludes);
		for (final FilterInstance neighbour : neighbours) {
			final Block newBlock = new Block(block);
			newBlock.addFilterInstances(Collections.singletonMap(neighbour.ruleOrProxy,
					Collections.singleton(new FilterInstancesSideBySide(new FilterInstancesStack(neighbour)))));
			recursivelyMaximizeHorizontally(neighbouringFilterCandidates, newBlock, exclusionStack, resultBlocks);
			furtherExcludes.add(neighbour);
		}
		exclusionStack.pop();
	}

	public void maximizeVertically(final Block block, final Set<Block> resultBlocks) {
		final Set<Filter> filters = block.getFilters();
		final Set<Either<Rule, ExistentialProxy>> ruleCandidates =
				filters.stream().flatMap(f -> f.getRuleToInstances().keySet().stream()).distinct()
						.filter(r -> getFilters(r).containsAll(filters)).collect(toSet());
		for (final Either<Rule, ExistentialProxy> ruleOrProxy : ruleCandidates) {

		}

		// FIXME and here
		// resultBlocks.add(block);
	}

	public void start() {
		final UndirectedGraph<FilterInstance, ConflictEdge> conflictGraph = determineConflictGraph(rules);
		for (final Either<Rule, ExistentialProxy> rule : rules) {
			final Set<Filter> filters = getFilters(rule);
			for (final Filter filter : filters) {
				vertical(conflictGraph, filter);
			}
		}
	}

	public void vertical(final UndirectedGraph<FilterInstance, ConflictEdge> graph, final Filter filter) {
		final Set<Set<Set<FilterInstance>>> filterInstancesPowerSet =
				Sets.powerSet(new HashSet<>(filter.getRuleToInstances().values()));
		for (final Set<Set<FilterInstance>> powerSetElement : filterInstancesPowerSet) {
			final Set<List<FilterInstance>> cartesianProduct = Sets.cartesianProduct(new ArrayList<>(powerSetElement));
			for (final List<FilterInstance> filterInstances : cartesianProduct) {
				final Block newBlock = new Block(graph);
				newBlock.addFilterInstances(filterInstances.stream().collect(
						toMap(fi -> fi.getRuleOrProxy(), fi -> Collections.singleton(new FilterInstancesSideBySide(
								new FilterInstancesStack(fi))))));
				horizontal(newBlock);
			}
		}
	}

	public void horizontal(final Block block) {
		horizontalRecursion(block, new Stack<>());
	}

	public void horizontalRecursion(final Block block, final Stack<Set<FilterInstance>> exclusionStack) {
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
		final Set<Either<Rule, ExistentialProxy>> rulesInBlock = block.getRuleToRow().keySet();
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
			blocks.add(block);
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
				block.filterInstances.stream().collect(
						groupingBy(FilterInstancesSideBySide::getFilter,
								toMap(FilterInstancesSideBySide::getRuleOrProxy, Function.identity())));
		// iterate over every single-/multi-cell filter and check that its instances have the same
		// conflicts in every rule
		for (final Filter filter : singleCellFilters.isEmpty() ? multiCellFilters : singleCellFilters) {
			// get the mapping from rule to filter instance for the current filter
			final Map<Either<Rule, ExistentialProxy>, FilterInstancesSideBySide> ruleToBlockInstances =
					filterToRuleToBlockInstances.get(filter);
			// list of conflicts for this filter
			final List<Conflict> conflicts = new ArrayList<>();
			boolean matchingConstellationFound = false;

			// iterate over the possible mappings: (filter,rule) -> filter instance
			final List<Set<FilterInstance>> listForCartesianProduct =
					filter.getRuleToInstances().entrySet().stream().filter(e -> rulesInBlock.contains(e.getKey()))
							.map(e -> e.getValue()).collect(toList());
			// create the cartesian product
			final Set<List<FilterInstance>> cartesianProduct = Sets.cartesianProduct(listForCartesianProduct);
			// iterate over the possible filter instance combinations
			cartesianProductLoop: for (final List<FilterInstance> currentOutsideFilterInstances : cartesianProduct) {
				// create a map for faster lookup: rule -> filter instance (outside)
				final Map<Either<Rule, ExistentialProxy>, FilterInstance> ruleToCurrentOutsideFilterInstance =
						currentOutsideFilterInstances.stream().collect(
								toMap(fi -> fi.getRuleOrProxy(), Function.identity()));
				// iterate over the Rule-BlockFilterInstance-mappings
				for (final Entry<Either<Rule, ExistentialProxy>, FilterInstancesSideBySide> entry : ruleToBlockInstances
						.entrySet()) {
					final Either<Rule, ExistentialProxy> rule = entry.getKey();
					final FilterInstancesSideBySide sideBySide = entry.getValue();
					// get the corresponding filter instance(s) that may be added
					final FilterInstance source = ruleToCurrentOutsideFilterInstance.get(rule);
					int i = 0;
					// iterate over the filter instance stacks (which all only contain one instance)
					for (final FilterInstancesStack filterInstancesStack : sideBySide.getStacks()) {
						// stacks should only contain one instance at this point
						assert 1 == filterInstancesStack.getInstances().size();
						// get the single instance within the stack
						final FilterInstance target = filterInstancesStack.iterator().next();

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
								new FilterInstancesStack(ruleToCurrentOutsideFilterInstance.get(rule)))))));
				matchingConstellationFound = true;
			}
			if (!matchingConstellationFound) {
				incompatibleFilters.add(filter);
			}
		}
		if (matchingFilters.isEmpty()) {
			blocks.add(block);
			return;
		}

		final Set<FilterInstance> furtherExcludes = new HashSet<>();
		exclusionStack.push(furtherExcludes);
		for (final Filter incompatibleFilter : incompatibleFilters) {
			for (final Either<Rule, ExistentialProxy> rule : rulesInBlock) {
				furtherExcludes.addAll(incompatibleFilter.getInstances(rule));
			}
		}
		for (final Map<Either<Rule, ExistentialProxy>, Set<FilterInstancesSideBySide>> neighbourMap : matchingFilters) {
			final Block newBlock = new Block(block);
			newBlock.addFilterInstances(neighbourMap);
			horizontalRecursion(newBlock, exclusionStack);
			for (final Set<FilterInstancesSideBySide> set : neighbourMap.values()) {
				for (final FilterInstancesSideBySide filterInstancesSideBySide : set) {
					for (final FilterInstance filterInstance : filterInstancesSideBySide) {
						furtherExcludes.add(filterInstance);
					}
				}
			}
		}
		exclusionStack.pop();
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

	public SetView<FilterInstance> getStableSet(final UndirectedGraph<FilterInstance, ConflictEdge> graph) {
		// could be improved by DOI 10.1137/0206036
		return Sets.difference(graph.vertexSet(), VertexCovers.find2ApproximationCover(graph));
	}
}
