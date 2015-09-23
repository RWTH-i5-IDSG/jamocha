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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import java.util.function.DoubleUnaryOperator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.Partition.SubSet;
import org.jamocha.dn.compiler.ecblocks.rand.IterativeImprovement;
import org.jamocha.dn.compiler.ecblocks.rand.SimulatedAnnealing;
import org.jamocha.dn.compiler.ecblocks.rand.TwoPhaseOptimization;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.optimizer.Optimizer;
import org.jamocha.filter.optimizer.SamePathsFilterCombiningOptimizer;
import org.jamocha.filter.optimizer.SamePathsNodeFilterSetCombiningOptimizer;
import org.jamocha.filter.optimizer.SubsetPathsNodeFilterSetCombiningOptimizer;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.rating.fraj.RatingProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
@Getter
public class Randomizer {

	final static DoubleBinaryOperator cpuCost = (cpu, mem) -> cpu;
	final static DoubleBinaryOperator memCost = (cpu, mem) -> mem;
	final static DoubleBinaryOperator mixCost = (cpu, mem) -> Math.log(cpu) + Math.log10(mem);

	@Getter
	public class State {
		double rating;
		ECBlockSet blockSet;
		Collection<PathRule> compiledState;

		public State(final ECBlockSet blockSet) {
			this.blockSet = blockSet;
			clear();
		}

		public State(final State copy) {
			this.blockSet = new ECBlockSet(copy.blockSet);
		}

		public double rate() {
			if (null == compiledState) {
				this.compiledState = Randomizer.compile(rules, blockSet);
				this.rating = Randomizer.rate(compiledState);
			}
			return this.rating;
		}

		private void clear() {
			this.compiledState = null;
			this.rating = Double.MAX_VALUE;
		}

		public State move() {
			final State nextState = new State(this);
			switch (rand.nextInt(5)) {
			case 0: { // add a row
				log.debug("MOVE: add a row");
				if (!nextState.blockSet.blocks.isEmpty()) {
					final Block block = getRandomElement(nextState.blockSet.blocks, rand);
					final List<Either<Rule, ExistentialProxy>> otherRules =
							ListUtils.removeAll(rules, block.getRulesOrProxies());
					if (!otherRules.isEmpty()) {
						int tries = 100;
						boolean changed = false;
						do {
							changed = tryToAddRow(nextState, block, getRandomElement(otherRules, rand));
						} while (!changed && --tries > 0);
					}
				}
				break;
			}
			case 1: { // remove a row
				log.debug("MOVE: remove a row");
				if (!nextState.blockSet.blocks.isEmpty()) {
					final Block block = getRandomElement(nextState.blockSet.blocks, rand);
					tryToRemoveRow(nextState, block, getRandomElement(block.getRulesOrProxies(), rand));
				}
				break;
			}
			case 2: { // add a column
				log.debug("MOVE: add a column");
				if (!nextState.blockSet.blocks.isEmpty()) {
					final Block block = getRandomElement(nextState.blockSet.blocks, rand);
					tryToAddARandomColumn(nextState, block);
				}
				break;
			}
			case 3: { // remove a column
				log.debug("MOVE: remove a column");
				if (!nextState.blockSet.blocks.isEmpty()) {
					final Block block = getRandomElement(nextState.blockSet.blocks, rand);
					final Set<FilterInstanceSubSet> subSets = block.getFilterInstancePartition().getSubSets();
					assert !subSets.isEmpty();
					tryToRemoveColumn(nextState, block, getRandomElement(subSets, rand));
				}
				break;
			}
			case 4: { // create a new block
				log.debug("MOVE: create a new block");
				createRandomBlock(nextState, rules);
				break;
			}
			}
			return nextState;
		}
	}

	final List<Either<Rule, ExistentialProxy>> rules;
	final Random rand = new Random(0);
	State currentState, bestState, initialState;

	public void setCurrentState(final State state) {
		currentState = state;
	}

	public void reset() {
		currentState = initialState;
	}

	public void setCurrentAsBestState() {
		bestState = currentState;
	}

	/**
	 * Takes a set of rules and a conflict-free set of blocks
	 * 
	 * @param rules
	 *            rules to consider
	 * @param blockSet
	 *            blocks to consider
	 * @return a list of PathRule that the randomizer is content with
	 */
	public static Collection<PathRule> randomizeII(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet blockSet) {
		final Randomizer randomizer = newRandomizer(rules, blockSet);
		/* number of nodes in the condition graph */
		final long localOptimizations = rules.stream().map(Util::getFactVariables).flatMap(Set::stream).count();
		final long rLocalMinimum = 20;
		return resetPaths(new IterativeImprovement(randomizer, localOptimizations, rLocalMinimum).optimize());
	}

	/**
	 * Takes a set of rules and a conflict-free set of blocks
	 * 
	 * @param rules
	 *            rules to consider
	 * @param blockSet
	 *            blocks to consider
	 * @return a list of PathRule that the randomizer is content with
	 */
	public static Collection<PathRule> randomizeSA(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet blockSet) {
		final Randomizer randomizer = newRandomizer(rules, blockSet);
		final DoubleUnaryOperator cooldown = t -> 0.95 * t;
		/* Gator uses: number of edges in the condition graph */
		/* we just use the number of nodes here !? */
		final long innerLoopOpimizations = rules.stream().map(Util::getFactVariables).flatMap(Set::stream).count();
		final double initialTemp = 2 * randomizer.getBestState().rate();
		return resetPaths(new SimulatedAnnealing(randomizer, cooldown, innerLoopOpimizations, initialTemp).optimize());
	}

	/**
	 * Takes a set of rules and a conflict-free set of blocks
	 * 
	 * @param rules
	 *            rules to consider
	 * @param blockSet
	 *            blocks to consider
	 * @return a list of PathRule that the randomizer is content with
	 */
	public static Collection<PathRule> randomizeTPO(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet blockSet) {
		final Randomizer randomizer = newRandomizer(rules, blockSet);
		final long ii_localOptimizations = rules.stream().map(Util::getFactVariables).flatMap(Set::stream).count();
		final long ii_rLocalMinimum = 20;
		final DoubleUnaryOperator sa_cooldown = t -> 0.95 * t;
		/* Gator uses: number of edges in the condition graph */
		/* we just use the number of nodes here !? */
		final long sa_innerLoopOpimizations = rules.stream().map(Util::getFactVariables).flatMap(Set::stream).count();
		return resetPaths(new TwoPhaseOptimization(randomizer, ii_localOptimizations, ii_rLocalMinimum, sa_cooldown,
				sa_innerLoopOpimizations).optimize());
	}

	private static Collection<PathRule> resetPaths(final Collection<PathRule> optimized) {
		for (final PathRule pathRule : optimized) {
			final PathFilterList condition = pathRule.getCondition();
			final HashSet<Path> paths = PathCollector.newHashSet().collectAllInLists(condition).getPaths();
			paths.addAll(pathRule.getResultPaths());
			for (final Path path : paths) {
				path.setCurrentlyLowestNode(null);
				path.setFactAddressInCurrentlyLowestNode(null);
				path.setJoinedWith(Sets.newHashSet(path));
			}
		}
		return optimized;
	}

	private static Randomizer newRandomizer(final List<Either<Rule, ExistentialProxy>> rules, final ECBlockSet blockSet) {
		final Randomizer randomizer = new Randomizer(rules);
		randomizer.initialState = randomizer.new State(blockSet);
		randomizer.bestState = randomizer.new State(randomizer.initialState);
		randomizer.currentState = randomizer.new State(randomizer.initialState);
		return randomizer;
	}

	protected boolean createRandomBlock(final State state, final List<Either<Rule, ExistentialProxy>> rules) {
		final Either<Rule, ExistentialProxy> rule = getRandomElement(rules, rand);
		final Set<FilterInstance> filterInstances = Util.getFilterInstances(rule);
		if (filterInstances.isEmpty()) {
			log.debug("no filter instances for rule {}", rule);
			return false;
		}
		final FilterInstance randomFI = getRandomElement(filterInstances, rand);
		final FactVariablePartition fvPart = new FactVariablePartition();
		final Set<SingleFactVariable> factVariables = Util.getFactVariables(rule);
		for (final SingleFactVariable factVariable : factVariables) {
			fvPart.add(new FactVariableSubSet(Collections.singletonMap(rule, factVariable)));
		}

		final Block block = new Block(Collections.singleton(rule), fvPart);
		block.addFilterInstanceSubSet(new FilterInstanceSubSet(Collections.singletonMap(rule, randomFI)));
		if (randomFI instanceof ImplicitFilterInstance) {
			final ImplicitFilterInstance implicitFilterInstance = (ImplicitFilterInstance) randomFI;
			block.addFilterInstanceSubSet(new FilterInstanceSubSet(Collections.singletonMap(rule,
					implicitFilterInstance.getDual())));
			block.addElementSubSet(new SubSet<>(Collections.singletonMap(rule, implicitFilterInstance.getLeft())));
			block.addElementSubSet(new SubSet<>(Collections.singletonMap(rule, implicitFilterInstance.getRight())));
		} else {
			final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> ecColumns =
					ECBlocks.determineECColumns(Collections.singletonList(randomFI));
			for (final Map<Either<Rule, ExistentialProxy>, EquivalenceClass> ecColumn : ecColumns.values()) {
				final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> ecIntersection =
						ECBlocks.determineEquivalenceClassIntersection(ecColumn, fvPart);
				final Map<Either<Rule, ExistentialProxy>, ? extends Element> elementSubSet =
						getRandomElement(ecIntersection, rand);
				block.addElementSubSet(new SubSet<>(elementSubSet));
			}
		}
		final boolean added = state.blockSet.addDuringHorizontalRecursion(block);
		log.debug("added? ({}) new block {}", added, block);
		return added;
	}

	static <T> T getRandomElement(final Set<T> elements, final Random rand) {
		return Iterables.get(elements, rand.nextInt(elements.size()));
	}

	static <T> T getRandomElement(final List<T> elements, final Random rand) {
		return elements.get(rand.nextInt(elements.size()));
	}

	public boolean tryToAddRow(final State state, final Block block, final Either<Rule, ExistentialProxy> rule) {
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
		final Set<SingleFactVariable> factVariables = Sets.newHashSet(Util.getFactVariables(rule));
		final Map<Template, ArrayList<SingleFactVariable>> fvsByTemplate =
				factVariables.stream().collect(groupingBy(SingleFactVariable::getTemplate, toArrayList()));
		final IdentityHashMap<FactVariableSubSet, SingleFactVariable> fvExtension = new IdentityHashMap<>();
		for (final Template template : block.getFactVariablePartition().templateLookup.keySet()) {
			final Set<FactVariableSubSet> subsets = block.getFactVariablePartition().lookupByTemplate(template);
			final ArrayList<SingleFactVariable> fvs = fvsByTemplate.getOrDefault(template, new ArrayList<>());
			if (fvs.size() < subsets.size()) {
				return false;
			}
			for (final FactVariableSubSet fvSubSet : subsets) {
				final SingleFactVariable singleFactVariable = getRandomElement(factVariables, rand);
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

		state.blockSet.remove(block);
		final Block newBlock = new Block(block);
		newBlock.addRow(rule, fvExtension, fiExtension, elExtension);
		state.blockSet.addDuringHorizontalRecursion(newBlock);
		solveConflicts(state, newBlock);
		log.debug("rule added!");
		return true;
	}

	public boolean tryToRemoveRow(final State state, final Block block, final Either<Rule, ExistentialProxy> rule) {
		log.debug("removing rule {} from block {}", rule, block);
		state.blockSet.remove(block);
		final Block newBlock = new Block(block);
		if (newBlock.getNumberOfRows() > 1) {
			newBlock.remove(rule);
			state.blockSet.addDuringHorizontalRecursion(newBlock);
		} // else: block vanishes
		else {
			log.debug("block vanished");
		}
		solveConflicts(state, newBlock);
		return true;
	}

	public boolean tryToAddARandomColumn(final State state, final Block block) {
		log.debug("trying to a random column to block {}", block);
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
			log.debug("no filters in conflict found");
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
			log.debug("chose to add an implicit element column");
			if (!nRelevantImplicitElementFilterInstances.isEmpty()) {
				ECBlocks.findMatchingImplicitElementFilters(nRelevantImplicitElementFilterInstances, block,
						matchingFilters);
			}
			break;
		case 1:
			log.debug("chose to add an explicit single column");
			if (!nSingleCellFilters.isEmpty()) {
				ECBlocks.findMatchingAndIncompatibleExplicitFilters(nRelevantFilterToExplicitInstances,
						nSingleCellFilters, block, matchingFilters, incompatibleFilters);
			}
			break;
		case 2:
			log.debug("chose to add an implicit EC column");
			if (!nRelevantImplicitECFilterInstances.isEmpty()) {
				ECBlocks.findMatchingAndIncompatibleImplicitECFilters(nRelevantImplicitECFilterInstances, block,
						matchingFilters, incompatibleFilters);
			}
			break;
		case 3:
			log.debug("chose to add an explicit multi column");
			if (!nMultiCellFilters.isEmpty()) {
				ECBlocks.findMatchingAndIncompatibleExplicitFilters(nRelevantFilterToExplicitInstances,
						nMultiCellFilters, block, matchingFilters, incompatibleFilters);
			}
			break;
		}
		if (matchingFilters.isEmpty()) {
			log.debug("no matching filters found");
			return false;
		}

		final Pair<Block, List<FilterInstance>> randomElement = getRandomElement(matchingFilters, rand);
		final Block newBlock = randomElement.getLeft();
		log.debug("chose the new block {}", newBlock);
		state.blockSet.remove(block);
		state.blockSet.addDuringHorizontalRecursion(newBlock);
		solveConflicts(state, newBlock);
		return true;
	}

	public boolean tryToRemoveColumn(final State state, final Block block, final FilterInstanceSubSet subset) {
		log.debug("removing column {} from block {}", subset, block);
		state.blockSet.remove(block);
		final Block newBlock = new Block(block);
		newBlock.remove(subset);
		if (newBlock.getNumberOfColumns() >= 1) {
			state.blockSet.addDuringHorizontalRecursion(newBlock);
			solveConflicts(state, newBlock);
		} // else: block vanishes
		else {
			log.debug("block vanished");
		}
		return true;
	}

	protected void solveConflicts(final State state, final Block block) {
		final DirectedGraph<Block, BlockConflict> blockConflictGraph = new SimpleDirectedGraph<>(BlockConflict::of);
		state.blockSet.blocks.forEach(blockConflictGraph::addVertex);
		for (final Block x : blockConflictGraph.vertexSet()) {
			ECBlocks.createArcs(blockConflictGraph, state.blockSet, x);
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
			ECBlocks.solveConflict(blockConflict, blockConflictGraph, state.blockSet, deletedBlocks);
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
