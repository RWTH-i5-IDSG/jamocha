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
package org.jamocha.dn.compiler.ecblocks.rand;

import java.util.Collection;
import java.util.function.DoubleUnaryOperator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.Randomizer;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Log4j2
public class TwoPhaseOptimization {
    final Randomizer randomizer;
    final long iiNumLocalOptimisations;
    final long iiRLocalMinimum;
    final DoubleUnaryOperator saCooldown;
    final long saInnerLoopOptimizations;

    public Collection<PathRule> optimize() {
        log.entry(this.iiNumLocalOptimisations, this.iiRLocalMinimum, this.saInnerLoopOptimizations);
        new IterativeImprovement(this.randomizer, this.iiNumLocalOptimisations, this.iiRLocalMinimum).optimize();
        final double bestCost = this.randomizer.getBestState().rate();
        log.debug("II done with best state cost {}", bestCost);
        final double saInitialTemp = bestCost < 20000 ? 0.5 * bestCost : 0.05 * bestCost;
        new SimulatedAnnealing(this.randomizer, this.saCooldown, this.saInnerLoopOptimizations, saInitialTemp)
                .optimize();
        log.exit(this.randomizer.getBestState().getRating());
        return this.randomizer.getBestState().getCompiledState();
    }
}
