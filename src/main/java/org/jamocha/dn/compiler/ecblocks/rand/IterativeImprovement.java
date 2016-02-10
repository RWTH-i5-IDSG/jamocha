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

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.Randomizer;
import org.jamocha.dn.compiler.ecblocks.Randomizer.State;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
public class IterativeImprovement {
    final Randomizer randomizer;
    final long rlocalminimum;
    final long numlocaloptimisations;

    public Collection<PathRule> optimize() {
        log.entry(this.rlocalminimum, this.numlocaloptimisations);
        long numLocalOptimisations = 0;
        while (numLocalOptimisations < this.numlocaloptimisations) {
            // set "random" state
            this.randomizer.setCurrentState(this.randomizer.getInitialState());
            long noImprovementsCounter = 0;

            while (noImprovementsCounter < this.rlocalminimum) {
                final State nextState = this.randomizer.getCurrentState().move();
                if (nextState.rate() < this.randomizer.getCurrentState().rate()) {
                    log.debug("downhill move ({})", nextState.rate());
                    this.randomizer.setCurrentState(nextState);
                    noImprovementsCounter = 0;
                } else {
                    log.debug("would be an uphill move ({}), skipping", nextState.rate());
                    ++noImprovementsCounter;
                }
            }
            log.debug("local minimum reached");
            if (this.randomizer.getCurrentState().rate() < this.randomizer.getBestState().rate()) {
                log.debug("local minimum better than previous best ({})", this.randomizer.getCurrentState().rate());
                this.randomizer.setCurrentAsBestState();
            }
            ++numLocalOptimisations;
        }
        log.exit(this.randomizer.getBestState().rate());
        return this.randomizer.getBestState().getCompiledState();
    }
}
