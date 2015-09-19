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
package org.jamocha.dn.compiler.ecblocks.rand;

import java.util.Collection;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.Randomizer;
import org.jamocha.dn.compiler.ecblocks.Randomizer.State;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class IterativeImprovement {
	final Randomizer randomizer;
	final long RLOCALMINIMUM;
	final long NUMLOCALOPTIMISATIONS;

	public Collection<PathRule> optimize() {
		long numLocalOptimisations = 0;
		while (numLocalOptimisations < NUMLOCALOPTIMISATIONS) {
			// set "random" state
			randomizer.setCurrentState(randomizer.getInitialState());
			long noImprovementsCounter = 0;

			while (noImprovementsCounter < RLOCALMINIMUM) {
				final State nextState = randomizer.getCurrentState().move();
				if (nextState.rate() < randomizer.getCurrentState().rate()) {
					randomizer.setCurrentState(nextState);
					noImprovementsCounter = 0;
				} else {
					++noImprovementsCounter;
				}
			}
			if (randomizer.getCurrentState().rate() < randomizer.getBestState().rate()) {
				randomizer.setCurrentAsBestState();
			}
			++numLocalOptimisations;
		}
		return randomizer.getBestState().getCompiledState();
	}
}
