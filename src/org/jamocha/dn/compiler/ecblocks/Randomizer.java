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
import static org.jamocha.util.Lambdas.toArrayList;
import static org.jamocha.util.Lambdas.toIdentityHashSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

import org.apache.commons.collections4.ListUtils;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.Network;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.BlockConflict;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Theta;
import org.jamocha.dn.compiler.ecblocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.Partition.SubSet;
import org.jamocha.dn.memory.Template;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.rating.fraj.RatingProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

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
	public static List<PathRule> randomize(final List<Either<Rule, ExistentialProxy>> rules, final ECBlockSet blockSet) {
		final List<Filter> filters =
				ImmutableList.copyOf(rules.stream().map(Util::getFilters).flatMap(Set::stream)
						.collect(toIdentityHashSet()));
		final Randomizer randomizer = new Randomizer(rules, blockSet);

		List<PathRule> compiledState = ECBlocks.compile(rules, randomizer.state);

		// 0. initialize "best" rating and state
		double bestRating = Double.MAX_VALUE;
		ECBlockSet bestState = randomizer.state;
		List<PathRule> bestCompiledState = null;

		// 1. randomly modify state (returns if state was actually changed)
		{
			final Block block = getRandomElement(randomizer.state.blocks, randomizer.rand);
			boolean changed = false;
			switch (randomizer.rand.nextInt(4)) {
			case 0: {
				changed =
						randomizer.tryToAddRow(
								block,
								getRandomElement(ListUtils.removeAll(randomizer.rules, block.getRulesOrProxies()),
										randomizer.rand));
				break;
			}
			case 1: {
				changed =
						randomizer.tryToRemoveRow(block, getRandomElement(block.getRulesOrProxies(), randomizer.rand));
				break;
			}
			case 2: {
				final Set<Either<Rule, ExistentialProxy>> bRules = block.getRulesOrProxies();
				final Filter filter = getRandomElement(filters, randomizer.rand);
				final Function<Either<Rule, ExistentialProxy>, Set<? extends FilterInstance>> getter;
				switch (randomizer.rand.nextInt(3)) {
				case 0:
					getter = filter::getExplicitInstances;
					break;
				case 1:
					getter = filter::getImplicitElementInstances;
					break;
				case 2:
					getter = filter::getImplicitECInstances;
					break;
				default:
					getter = null;
					break;
				}
				final ImmutableMap<Either<Rule, ExistentialProxy>, FilterInstance> column =
						Maps.toMap(bRules, rule -> getRandomElement(getter.apply(rule), randomizer.rand));
				changed = randomizer.tryToAddColumn(block, new FilterInstanceSubSet(column));
				break;
			}
			case 3: {
				changed =
						randomizer.tryToRemoveColumn(block,
								getRandomElement(block.getFilterInstancePartition().getSubSets(), randomizer.rand));
				break;
			}
			default:
				changed = false;
			}
		}
		// 2. rate
		final double stateRating;
		{
			compiledState = ECBlocks.compile(rules, randomizer.state);
			final Network network = new Network();
			compiledState.forEach(network::buildRule);
			// uses CPU cost only
			stateRating = new RatingProvider(cpuCost).rateNetwork(network);
		}

		// 3. save if better (smaller is better)
		if (stateRating < bestRating) {
			// ...
			bestRating = stateRating;
			// do we need this? needed if one wants to continue modifying a previous best state
			bestState = new ECBlockSet(randomizer.state);
			bestCompiledState = compiledState;
		}

		// 4. decide whether to stop

		// 5. return the best stuff we got
		return bestCompiledState;
	}

	static <T> T getRandomElement(final Set<T> elements, final Random rand) {
		return Iterables.get(elements, rand.nextInt(elements.size()));
	}

	static <T> T getRandomElement(final List<T> elements, final Random rand) {
		return elements.get(rand.nextInt(elements.size()));
	}

	final DirectedGraph<Block, BlockConflict> blockConflictGraph;
	final List<Either<Rule, ExistentialProxy>> rules;
	final ECBlockSet state;
	final Random rand = new Random();

	public Randomizer(final List<Either<Rule, ExistentialProxy>> rules, final ECBlockSet blockSet) {
		this.rules = rules;
		this.state = blockSet;
		this.blockConflictGraph = new SimpleDirectedGraph<>(BlockConflict::of);
		blockSet.blocks.forEach(this.blockConflictGraph::addVertex);
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
		if (!ElementCompare.compare(elements, block.getFactVariablePartition(), fvExtension)) {
			return false;
		}
		final Theta thetaExtension = new Theta();
		elExtension.values().stream().forEach(thetaExtension::add);

		final Either<Rule, ExistentialProxy> chosenRule = block.rulesOrProxies.iterator().next();
		for (final FilterInstanceSubSet aSubset : block.getFilterInstancePartition().getSubSets()) {
			for (final FilterInstanceSubSet bSubset : block.getFilterInstancePartition().getSubSets()) {
				if (aSubset == bSubset)
					continue;
				final FilterInstance a = aSubset.get(chosenRule);
				final FilterInstance b = bSubset.get(chosenRule);

				final FilterInstance f = fiExtension.get(aSubset);
				final FilterInstance g = fiExtension.get(bSubset);

				if (!Objects.equals(a.getConflict(b, block.theta, block.theta),
						f.getConflict(g, thetaExtension, thetaExtension))) {
					return false;
				}
			}
		}

		block.addRow(rule, fvExtension, fiExtension, elExtension);
		solveConflicts(block);
		return true;
	}

	public boolean tryToRemoveRow(final Block block, final Either<Rule, ExistentialProxy> rule) {
		state.remove(block);
		blockConflictGraph.removeVertex(block);
		if (block.getNumberOfRows() > 1 || !block.getRulesOrProxies().contains(rule)) {
			block.remove(rule);
			state.addDuringHorizontalRecursion(block);
		} // else: block vanishes
		solveConflicts(block);
		return true;
	}

	public boolean tryToAddColumn(final Block block, final FilterInstanceSubSet subset) {
		// TODO impl

		// check restrictions before adding!
		block.addFilterInstanceSubSet(subset);
		solveConflicts(block);
		return true;
	}

	public boolean tryToRemoveColumn(final Block block, final FilterInstanceSubSet subset) {
		state.remove(block);
		blockConflictGraph.removeVertex(block);
		if (block.getNumberOfColumns() > 1 || !block.containsColumn(subset)) {
			block.remove(subset);
			state.addDuringHorizontalRecursion(block);
		} // else: block vanishes
		solveConflicts(block);
		return true;
	}

	protected void solveConflicts(final Block block) {
		ECBlocks.createArcs(blockConflictGraph, state, block);
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
}
