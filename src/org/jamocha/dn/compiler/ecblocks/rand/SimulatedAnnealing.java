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
import java.util.function.DoubleUnaryOperator;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.Randomizer;
import org.jamocha.dn.compiler.ecblocks.Randomizer.State;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class SimulatedAnnealing {
	final Randomizer randomizer;
	final DoubleUnaryOperator COOLDOWN;
	final long INNERLOOPOPTIMIZATIONS;
	final double INITIALTEMP;

	public Collection<PathRule> optimize() {
		double t = INITIALTEMP;
		long noNewBestCounter = 0;

		do {
			long innerLoopCounter = 0;
			do {
				++noNewBestCounter;
				final State nextState = randomizer.getCurrentState().move();
				final double delta = nextState.rate() - randomizer.getCurrentState().rate();
				if (delta <= 0) {
					randomizer.setCurrentState(nextState);
					if (randomizer.getCurrentState().rate() < randomizer.getBestState().rate()) {
						randomizer.setCurrentAsBestState();
						noNewBestCounter = 0;
					}
				} else if (randomizer.getRand().nextInt(1000) < 1000.0 * Math.exp(-delta / t)) {
					randomizer.setCurrentState(nextState);
				}
				++innerLoopCounter;
			} while (innerLoopCounter < INNERLOOPOPTIMIZATIONS);
			t = COOLDOWN.applyAsDouble(t);
		} while (t > INITIALTEMP / 1000.0 || noNewBestCounter <= 5);

		return randomizer.getBestState().getCompiledState();
	}
}
