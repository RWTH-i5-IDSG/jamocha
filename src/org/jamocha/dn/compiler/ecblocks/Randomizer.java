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

import static org.jamocha.util.Lambdas.toIdentityHashSet;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.Network;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.BlockConflict;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.FilterInstancePartition.FilterInstanceSubSet;
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

		double bestRating = Double.MAX_VALUE;
		ECBlockSet bestState = randomizer.state;
		List<PathRule> bestCompiledState = null;

		// 1. randomly modify state (returns if state was actually changed)
		{
			final Block block = getRandomElement(randomizer.state.blocks, randomizer.rand);
			boolean changed = false;
			switch (randomizer.rand.nextInt(4)) {
			case 0: {
				changed = randomizer.tryToAddRow(block, getRandomElement(randomizer.rules, randomizer.rand));
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
								getRandomElement(block.getFilterInstancePartition().getElements(), randomizer.rand));
				break;
			}
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
		return Iterables.skip(elements, rand.nextInt(elements.size())).iterator().next();
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
		if (block.getNumberOfColumns() > filterInstances.size()) {
			return false;
		}
		if (!Util.getFilters(rule).containsAll(block.getFilters())) {
			return false;
		}

		return true;
	}

	public boolean tryToRemoveRow(final Block block, final Either<Rule, ExistentialProxy> rule) {
		state.remove(block);
		blockConflictGraph.removeVertex(block);
		if (block.getNumberOfRows() > 1 || !block.getRulesOrProxies().contains(rule)) {
			block.remove(rule);
			state.addDuringHorizontalRecursion(block);
		} // else: block vanishes
		ECBlocks.createArcs(blockConflictGraph, state, block);
		return true;
	}

	public boolean tryToAddColumn(final Block block, final FilterInstanceSubSet subset) {
		return true;
	}

	public boolean tryToRemoveColumn(final Block block, final FilterInstanceSubSet subset) {
		return true;
	}
}
