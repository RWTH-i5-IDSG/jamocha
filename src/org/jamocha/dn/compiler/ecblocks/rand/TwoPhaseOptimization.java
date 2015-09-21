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
	final long II_NUMLOCALOPTIMISATIONS;
	final long II_RLOCALMINIMUM;
	final DoubleUnaryOperator SA_COOLDOWN;
	final long SA_INNERLOOPOPTIMIZATIONS;

	public Collection<PathRule> optimize() {
		log.entry(II_NUMLOCALOPTIMISATIONS, II_RLOCALMINIMUM, SA_INNERLOOPOPTIMIZATIONS);
		new IterativeImprovement(randomizer, II_NUMLOCALOPTIMISATIONS, II_RLOCALMINIMUM).optimize();
		final double bestCost = randomizer.getBestState().rate();
		log.debug("II done with best state cost {}", bestCost);
		final double sa_initialTemp = bestCost < 20000 ? 0.5 * bestCost : 0.05 * bestCost;
		new SimulatedAnnealing(randomizer, SA_COOLDOWN, SA_INNERLOOPOPTIMIZATIONS, sa_initialTemp).optimize();
		log.exit(randomizer.getBestState().getRating());
		return randomizer.getBestState().getCompiledState();
	}
}
