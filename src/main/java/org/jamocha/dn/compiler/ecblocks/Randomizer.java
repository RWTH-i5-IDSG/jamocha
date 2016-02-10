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
package org.jamocha.dn.compiler.ecblocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.Network;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.rand.IterativeImprovement;
import org.jamocha.dn.compiler.ecblocks.rand.SimulatedAnnealing;
import org.jamocha.dn.compiler.ecblocks.rand.TwoPhaseOptimization;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.optimizer.Optimizer;
import org.jamocha.filter.optimizer.SamePathsFilterCombiningOptimizer;
import org.jamocha.filter.optimizer.SamePathsNodeFilterSetCombiningOptimizer;
import org.jamocha.filter.optimizer.SubsetPathsNodeFilterSetCombiningOptimizer;
import org.jamocha.rating.fraj.RatingProvider;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
@Getter
public class Randomizer {

    static final DoubleBinaryOperator CPU_COST = (cpu, mem) -> cpu;
    static final DoubleBinaryOperator MEM_COST = (cpu, mem) -> mem;
    static final DoubleBinaryOperator MIX_COST = (cpu, mem) -> Math.log(cpu) + Math.log10(mem);

    class ECBlockSet {
        ECBlockSet(final ECBlockSet other) {
        }
    }

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
            if (null == this.compiledState) {
                this.compiledState = Randomizer.compile(Randomizer.this.assignmentGraph, this.blockSet);
                this.rating = Randomizer.rate(this.compiledState);
            }
            return this.rating;
        }

        private void clear() {
            this.compiledState = null;
            this.rating = Double.MAX_VALUE;
        }

        public State move() {
            // TODO impl
            return this;
        }
    }

    final AssignmentGraph assignmentGraph;
    final Random rand = new Random(0);
    State currentState, bestState, initialState;

    public void setCurrentState(final State state) {
        this.currentState = state;
    }

    public void reset() {
        this.currentState = this.initialState;
    }

    public void setCurrentAsBestState() {
        this.bestState = this.currentState;
    }

    /**
     * Takes a set of rules and a conflict-free set of blocks
     *
     * @param assignmentGraph
     *         assignmentGraph to consider
     * @param blockSet
     *         blocks to consider
     * @return a list of PathRule that the randomizer is content with
     */
    public static Collection<PathRule> randomizeII(final AssignmentGraph assignmentGraph, final ECBlockSet blockSet) {
        final Randomizer randomizer = newRandomizer(assignmentGraph, blockSet);
        /* number of nodes in the condition graph */
        final long localOptimizations = 0; // TBD number of fact variables
        final long rLocalMinimum = 20;
        return resetPaths(new IterativeImprovement(randomizer, localOptimizations, rLocalMinimum).optimize());
    }

    /**
     * Takes a set of rules and a conflict-free set of blocks
     *
     * @param assignmentGraph
     *         assignmentGraph to consider
     * @param blockSet
     *         blocks to consider
     * @return a list of PathRule that the randomizer is content with
     */
    public static Collection<PathRule> randomizeSA(final AssignmentGraph assignmentGraph, final ECBlockSet blockSet) {
        final Randomizer randomizer = newRandomizer(assignmentGraph, blockSet);
        final DoubleUnaryOperator cooldown = t -> 0.95 * t;
        /* Gator uses: number of edges in the condition graph */
        /* we just use the number of nodes here !? */
        final long innerLoopOpimizations = 0; // TBD number of fact variables
        final double initialTemp = 2 * randomizer.getBestState().rate();
        return resetPaths(new SimulatedAnnealing(randomizer, cooldown, innerLoopOpimizations, initialTemp).optimize());
    }

    /**
     * Takes a set of rules and a conflict-free set of blocks
     *
     * @param assignmentGraph
     *         assignmentGraph to consider
     * @param blockSet
     *         blocks to consider
     * @return a list of PathRule that the randomizer is content with
     */
    public static Collection<PathRule> randomizeTPO(final AssignmentGraph assignmentGraph, final ECBlockSet blockSet) {
        final Randomizer randomizer = newRandomizer(assignmentGraph, blockSet);
        // TBD number of fact variables
        final long iiLocalOptimizations = 0;
        final long iiRLocalMinimum = 20;
        final DoubleUnaryOperator saCooldown = t -> 0.95 * t;
        /* Gator uses: number of edges in the condition graph */
        /* we just use the number of nodes here !? */
        // TBD number of fact variables
        final long saInnerLoopOpimizations = 0;
        return resetPaths(new TwoPhaseOptimization(randomizer, iiLocalOptimizations, iiRLocalMinimum, saCooldown,
                saInnerLoopOpimizations).optimize());
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

    private static Randomizer newRandomizer(final AssignmentGraph assignmentGraph, final ECBlockSet blockSet) {
        final Randomizer randomizer = new Randomizer(assignmentGraph);
        randomizer.initialState = randomizer.new State(blockSet);
        randomizer.bestState = randomizer.new State(randomizer.initialState);
        randomizer.currentState = randomizer.new State(randomizer.initialState);
        return randomizer;
    }

    static <T> T getRandomElement(final Set<T> elements, final Random rand) {
        return Iterables.get(elements, rand.nextInt(elements.size()));
    }

    static <T> T getRandomElement(final List<T> elements, final Random rand) {
        return elements.get(rand.nextInt(elements.size()));
    }

    protected static Collection<PathRule> compile(final AssignmentGraph assignmentGraph, final ECBlockSet blockSet) {
        Collection<PathRule> transformedRules = null; // ECBlocks.compile(rules, blockSet);
        for (final Optimizer optimizer : ImmutableList.of(
        /*
         * now perform the actual optimization of the filter order
         */
                // PathFilterOrderOptimizer.INSTANCE,
        /*
         * node filter sets using the same paths can be combined
         */
                SamePathsNodeFilterSetCombiningOptimizer.INSTANCE,
        /*
         * filters using the same paths can be combined
         */
                SamePathsFilterCombiningOptimizer.INSTANCE,
        /*
         * now that the order of the node filter sets is fixed, we can combine node filter sets
         * using only a subset of the paths of their predecessors
         */
                SubsetPathsNodeFilterSetCombiningOptimizer.INSTANCE)) {
            transformedRules = optimizer.optimize(transformedRules);
        }
        return transformedRules;
    }

    protected static double rate(final Collection<PathRule> compiledState) {
        final Network network = new Network();
        for (final PathRule pathRule : compiledState) {
            network.buildRule(pathRule);
        }
        // uses CPU cost only
        final double rating = new RatingProvider(CPU_COST).rateNetwork(network);
        return rating;
    }
}
