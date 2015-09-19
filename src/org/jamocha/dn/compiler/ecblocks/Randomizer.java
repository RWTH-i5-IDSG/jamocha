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
package org.jamocha.dn.compiler.ecblocks;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.iterable;
import static org.jamocha.util.Lambdas.toArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.Network;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.BlockConflict;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterInstanceTypePartitioner;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Theta;
import org.jamocha.dn.compiler.ecblocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.dn.compiler.ecblocks.Filter.ExplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitECFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitElementFilterInstance;
import org.jamocha.dn.compiler.ecblocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.Partition.SubSet;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.optimizer.Optimizer;
import org.jamocha.filter.optimizer.SamePathsFilterCombiningOptimizer;
import org.jamocha.filter.optimizer.SamePathsNodeFilterSetCombiningOptimizer;
import org.jamocha.filter.optimizer.SubsetPathsNodeFilterSetCombiningOptimizer;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.rating.fraj.RatingProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Randomizer {

	final static DoubleBinaryOperator cpuCost = (cpu, mem) -> cpu;
	final static DoubleBinaryOperator memCost = (cpu, mem) -> mem;
	final static DoubleBinaryOperator mixCost = (cpu, mem) -> Math.log(cpu) + Math.log10(mem);

	/**
	 * Takes a set of rules and a conflict-free set of blocks
	 * 
	 * @param rules
	 *            rules to consider
	 * @param blockSet
	 *            blocks to consider
	 * @return a list of PathRule that the randomizer is content with
	 */
	public static Collection<PathRule> randomize(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet blockSet) {
		final Randomizer randomizer = new Randomizer(rules, blockSet);

		Collection<PathRule> compiledState = compile(rules, randomizer.state);

		// 0. initialize "best" rating and state
		double bestRating = Double.MAX_VALUE;
		ECBlockSet bestState = randomizer.state;
		Collection<PathRule> bestCompiledState = null;

		for (int i = 0; i < 100; ++i) {

			// 1. randomly modify state (returns if state was actually changed)
			// options:
			// add row
			// remove row
			// add column
			// remove column
			// new block
			// delete block?
			{
				boolean changed = false;
				int tries = 100;
				do {
					switch (randomizer.rand.nextInt(5)) {
					case 0: {
						if (!randomizer.state.blocks.isEmpty()) {
							final Block block = getRandomElement(randomizer.state.blocks, randomizer.rand);
							final List<Either<Rule, ExistentialProxy>> otherRules =
									ListUtils.removeAll(randomizer.rules, block.getRulesOrProxies());
							if (!otherRules.isEmpty()) {
								changed = randomizer.tryToAddRow(block, getRandomElement(otherRules, randomizer.rand));
							}
						}
						break;
					}
					case 1: {
						if (!randomizer.state.blocks.isEmpty()) {
							final Block block = getRandomElement(randomizer.state.blocks, randomizer.rand);
							changed =
									randomizer.tryToRemoveRow(block,
											getRandomElement(block.getRulesOrProxies(), randomizer.rand));
						}
						break;
					}
					case 2: {
						if (!randomizer.state.blocks.isEmpty()) {
							final Block block = getRandomElement(randomizer.state.blocks, randomizer.rand);
							changed = randomizer.tryToAddARandomColumn(block);
						}
						break;
					}
					case 3: {
						if (!randomizer.state.blocks.isEmpty()) {
							final Block block = getRandomElement(randomizer.state.blocks, randomizer.rand);
							changed =
									randomizer.tryToRemoveColumn(
											block,
											getRandomElement(block.getFilterInstancePartition().getSubSets(),
													randomizer.rand));
						}
						break;
					}
					case 4: {
						// TODO create a new block
						changed = false;
						break;
					}
					default:
						changed = false;
					}
					--tries;
					if (tries <= 0) {
						break;
					}
				} while (!changed);
			}
			// 2. rate
			final double stateRating;
			{
				compiledState = compile(rules, randomizer.state);
				// uses CPU cost only
				stateRating = rate(compiledState);
			}

			// 3. save if better (smaller is better)
			if (stateRating < bestRating) {
				// ...
				bestRating = stateRating;
				// do we need this? needed if one wants to continue modifying a previous best state
				bestState = new ECBlockSet(randomizer.state);
				bestCompiledState = compiledState;
			}
		}
		// 4. decide whether to stop

		// 5. return the best stuff we got
		
		return compile(rules, bestState);
//		return bestCompiledState;
	}

	static <T> T getRandomElement(final Set<T> elements, final Random rand) {
		return Iterables.get(elements, rand.nextInt(elements.size()));
	}

	static <T> T getRandomElement(final List<T> elements, final Random rand) {
		return elements.get(rand.nextInt(elements.size()));
	}

	final List<Either<Rule, ExistentialProxy>> rules;
	final ECBlockSet state;
	final Random rand = new Random();

	public Randomizer(final List<Either<Rule, ExistentialProxy>> rules, final ECBlockSet blockSet) {
		this.rules = rules;
		this.state = blockSet;
	}

	public boolean tryToAddRow(final Block block, final Either<Rule, ExistentialProxy> rule) {
		final Set<FilterInstance> filterInstances = Util.getFilterInstances(rule);
		// if the rule doesn't contain enough filter instances, fail
		if (block.getNumberOfColumns() > filterInstances.size()) {
			return false;
		}
		// if the rule doesn't provide the right filters, fail
		final Set<Filter> filters = Util.getFilters(rule);
		if (!filters.containsAll(block.getFilters())) {
			return false;
		}
		// rule could actually end up working, so lets try
		final IdentityHashMap<FilterInstanceSubSet, FilterInstance> fiExtension = new IdentityHashMap<>();
		for (final Filter filter : block.getFilters()) {
			final Set<FilterInstanceSubSet> subsets = block.getFilterInstancePartition().lookupByFilter(filter);
			final LinkedList<FilterInstance> allInstances = new LinkedList<>(filter.getAllInstances(rule));
			if (allInstances.size() < subsets.size()) {
				return false;
			}
			for (final FilterInstanceSubSet subset : subsets) {
				fiExtension.put(subset, allInstances.remove(rand.nextInt(allInstances.size())));
			}
		}
		// got a filter instance for every filter instance subset
		// => go get a fact variable partition
		final Set<SingleFactVariable> factVariables = Util.getFactVariables(rule);
		final Map<Template, ArrayList<SingleFactVariable>> fvsByTemplate =
				factVariables.stream().collect(groupingBy(SingleFactVariable::getTemplate, toArrayList()));
		final IdentityHashMap<FactVariableSubSet, SingleFactVariable> fvExtension = new IdentityHashMap<>();
		for (final Template template : block.getFactVariablePartition().templateLookup.keySet()) {
			final Set<FactVariableSubSet> subsets = block.getFactVariablePartition().lookupByTemplate(template);
			final ArrayList<SingleFactVariable> fvs = fvsByTemplate.get(template);
			if (fvs.size() < subsets.size()) {
				return false;
			}
			for (final FactVariableSubSet fvSubSet : subsets) {
				final SingleFactVariable singleFactVariable =
						Iterables.get(factVariables, rand.nextInt(factVariables.size()));
				factVariables.remove(singleFactVariable);
				fvExtension.put(fvSubSet, singleFactVariable);
			}
		}
		final IdentityHashMap<SubSet<Element>, Element> elExtension = new IdentityHashMap<>();
		final Set<Element> elements = ElementCollector.getElements(filterInstances);
		for (final SubSet<Element> subset : block.getElementPartition().getSubSets()) {
			final Element element = Iterables.get(elements, rand.nextInt(elements.size()));
			elements.remove(element);
			elExtension.put(subset, element);
		}
		// is this element partition extension compatible with the element partition?
		// this also checks whether the resulting theta is compatible with the current version
		if (!ElementCompare.compare(elements, block.getFactVariablePartition(), fvExtension)) {
			return false;
		}
		// add it to the theta
		final Theta thetaExtension = new Theta();
		elExtension.values().stream().forEach(thetaExtension::add);

		// check identical conflict index sets for an arbitrary row A and the new one R
		// test for all pairs of filter instance subsets: for every pair a,b in one and the
		// corresponding pair f,g in the other (of the same rules) have to have identical conflicts
		// c(a,f) == c(b,g)
		final Either<Rule, ExistentialProxy> chosenRule = block.rulesOrProxies.iterator().next();
		for (final FilterInstanceSubSet aSubset : block.getFilterInstancePartition().getSubSets()) {
			for (final FilterInstanceSubSet bSubset : block.getFilterInstancePartition().getSubSets()) {
				if (aSubset == bSubset)
					continue;
				final FilterInstance a = aSubset.get(chosenRule);
				final FilterInstance b = fiExtension.get(aSubset);

				final FilterInstance f = bSubset.get(chosenRule);
				final FilterInstance g = fiExtension.get(bSubset);

				if (!Objects.equals(a.getConflict(f, block.theta, block.theta),
						b.getConflict(g, thetaExtension, thetaExtension))) {
					return false;
				}
			}
		}

		state.remove(block);
		block.addRow(rule, fvExtension, fiExtension, elExtension);
		state.addDuringHorizontalRecursion(block);
		solveConflicts(block);
		return true;
	}

	public boolean tryToRemoveRow(final Block block, final Either<Rule, ExistentialProxy> rule) {
		state.remove(block);
		if (block.getNumberOfRows() > 1) {
			block.remove(rule);
			state.addDuringHorizontalRecursion(block);
		} // else: block vanishes
		solveConflicts(block);
		return true;
	}

	public boolean tryToAddARandomColumn(final Block block) {

		/* pretty much the ordinary procedure to find candidates, just simplified and randomized */
		final Set<FilterInstance> neighbours = block.getConflictNeighbours();
		if (neighbours.isEmpty()) {
			return false;
		}
		// group them by their filter
		final Map<Filter, List<FilterInstance>> nFilterToInstances =
				neighbours.stream().collect(groupingBy(FilterInstance::getFilter));
		// get all the rules in the block
		final Set<Either<Rule, ExistentialProxy>> bRules = block.getRulesOrProxies();
		// get a map from filter to all rules containing instances of that filter
		final Map<Filter, Set<Either<Rule, ExistentialProxy>>> nFilterToRulesContainingIt =
				nFilterToInstances
						.entrySet()
						.stream()
						.collect(
								toMap(Entry::getKey, e -> e.getValue().stream().map(FilterInstance::getRuleOrProxy)
										.collect(toSet())));
		// get the filters that are contained in every rule
		final Set<Filter> nRelevantFilters =
				nFilterToInstances.keySet().stream().filter(f -> nFilterToRulesContainingIt.get(f).containsAll(bRules))
						.collect(toSet());
		// if no filters are left to add, the block is horizontally maximized, add it
		if (nRelevantFilters.isEmpty()) {
			return false;
		}

		final FilterInstanceTypePartitioner nTypePartition =
				FilterInstanceTypePartitioner.partition(iterable(nRelevantFilters.stream().flatMap(
						f -> nFilterToInstances.get(f).stream())));
		final Map<Filter, List<ExplicitFilterInstance>> nRelevantFilterToExplicitInstances =
				nTypePartition.getExplicitFilterInstances().stream().collect(groupingBy(FilterInstance::getFilter));
		final List<ImplicitElementFilterInstance> nRelevantImplicitElementFilterInstances =
				nTypePartition.getImplicitElementFilterInstances();
		final List<ImplicitECFilterInstance> nRelevantImplicitECFilterInstances =
				nTypePartition.getImplicitECFilterInstances();

		// divide into filters without multiple instances and filters with multiple instances
		final List<Filter> nSingleCellFilters, nMultiCellFilters;
		{
			final Map<Boolean, List<Filter>> partition =
					nRelevantFilterToExplicitInstances
							.entrySet()
							.stream()
							.collect(
									partitioningBy(e -> e.getValue().size() > bRules.size(),
											mapping(Entry::getKey, toList())));
			nSingleCellFilters = partition.get(Boolean.FALSE);
			nMultiCellFilters = partition.get(Boolean.TRUE);
		}
		// list of rule-filter-matchings that may be added
		final List<Pair<Block, List<FilterInstance>>> matchingFilters = new ArrayList<>();
		final List<FilterInstance> incompatibleFilters = new ArrayList<>();

		switch (this.rand.nextInt(4)) {
		case 0:
			if (!nRelevantImplicitElementFilterInstances.isEmpty()) {
				ECBlocks.findMatchingImplicitElementFilters(nRelevantImplicitElementFilterInstances, block,
						matchingFilters);
			}
			break;
		case 1:
			if (!nSingleCellFilters.isEmpty()) {
				ECBlocks.findMatchingAndIncompatibleExplicitFilters(nRelevantFilterToExplicitInstances,
						nSingleCellFilters, block, matchingFilters, incompatibleFilters);
			}
			break;
		case 2:
			if (!nRelevantImplicitECFilterInstances.isEmpty()) {
				ECBlocks.findMatchingAndIncompatibleImplicitECFilters(nRelevantImplicitECFilterInstances, block,
						matchingFilters, incompatibleFilters);
			}
			break;
		case 3:
			if (!nMultiCellFilters.isEmpty()) {
				ECBlocks.findMatchingAndIncompatibleExplicitFilters(nRelevantFilterToExplicitInstances,
						nMultiCellFilters, block, matchingFilters, incompatibleFilters);
			}
			break;
		}
		if (matchingFilters.isEmpty()) {
			return false;
		}

		final Pair<Block, List<FilterInstance>> randomElement = getRandomElement(matchingFilters, rand);
		final Block newBlock = randomElement.getLeft();

		state.remove(block);
		state.addDuringHorizontalRecursion(newBlock);
		solveConflicts(newBlock);
		return true;
	}

	public boolean tryToRemoveColumn(final Block block, final FilterInstanceSubSet subset) {
		state.remove(block);
		block.remove(subset);
		if (block.getNumberOfColumns() >= 1) {
			state.addDuringHorizontalRecursion(block);
		} // else: block vanishes
		solveConflicts(block);
		return true;
	}

	protected void solveConflicts(final Block block) {
		final DirectedGraph<Block, BlockConflict> blockConflictGraph = new SimpleDirectedGraph<>(BlockConflict::of);
		state.getBlocks().forEach(blockConflictGraph::addVertex);
		for (final Block x : blockConflictGraph.vertexSet()) {
			ECBlocks.createArcs(blockConflictGraph, state, x);
		}
		final ECBlockSet deletedBlocks = new ECBlockSet();
		while (true) {
			final Optional<BlockConflict> mostUsefulConflictsFirst =
					blockConflictGraph.edgeSet().stream().sorted(Comparator.comparingInt(BlockConflict::getQuality))
							.filter(bc -> bc.replaceBlock != block).findFirst();
			if (!mostUsefulConflictsFirst.isPresent()) {
				break;
			}
			final BlockConflict blockConflict = mostUsefulConflictsFirst.get();
			ECBlocks.solveConflictDuringRandomization(blockConflict, blockConflictGraph, state, deletedBlocks);
		}
	}

	protected static Collection<PathRule> compile(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet blockSet) {
		Collection<PathRule> transformedRules = ECBlocks.compile(rules, blockSet);
		for (final Optimizer optimizer : ImmutableList.of(
		/*
		 * node filter sets using the same paths can be combined
		 */
		SamePathsNodeFilterSetCombiningOptimizer.instance,
		/*
		 * filters using the same paths can be combined
		 */
		SamePathsFilterCombiningOptimizer.instance,
		/*
		 * node filter sets using only a subset of the paths of their predecessors can be combined
		 */
		SubsetPathsNodeFilterSetCombiningOptimizer.instance)) {
			transformedRules = optimizer.optimize(transformedRules);
		}
		return transformedRules;
	}

	protected static double rate(final Collection<PathRule> compiledState) {
		final Network network = new Network();
		for (final PathRule pathRule : compiledState) {
			final TerminalNode terminalNode = network.buildRule(pathRule);
			network.getTerminalNodes().add(terminalNode);
		}
		// uses CPU cost only
		final double rating = new RatingProvider(cpuCost).rateNetwork(network);
		return rating;
	}
}
