/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.dn;

import java.util.Comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jamocha.dn.ConflictSet.RuleAndToken;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public enum ConflictResolutionStrategy implements Comparator<ConflictSet.RuleAndToken> {
	activationCounterComparator((o1, o2) -> Long.compare(o1.getActivationCounter(), o2.getActivationCounter())),

	activationCounterComparatorInverse((o1, o2) -> activationCounterComparator.compare(o2, o1)),

	specificityComparator((o1, o2) -> Integer.compare(o1.getRule().getSpecificity(), o2.getRule().getSpecificity())),

	specificityComparatorInverse((o1, o2) -> specificityComparator.compare(o2, o1)),

	recencyArrayComparator((o1, o2) -> {
		final int[] a1 = o1.getRecencyArray();
		final int[] a2 = o2.getRecencyArray();
		final int minLength = Math.min(a1.length, a2.length);
		for (int i = 0; i < minLength; ++i) {
			final int compare = Integer.compare(a1[i], a2[i]);
			if (0 != compare) {
				return compare;
			}
		}
		return Integer.compare(a1.length, a2.length);
	}),

	firstRecencyEntryComparator((o1, o2) -> {
		return Integer.compare(o1.getRecencyArray()[0], o2.getRecencyArray()[0]);
	}),

	DEPTH(activationCounterComparator),

	BREADTH(activationCounterComparatorInverse),

	SIMPLICITY((o1, o2) -> new CompareToBuilder().append(o1, o2, specificityComparatorInverse).append(o1, o2, DEPTH)
			.toComparison()),

	COMPLEXITY((o1, o2) -> new CompareToBuilder().append(o1, o2, specificityComparator).append(o1, o2, DEPTH)
			.toComparison()),

	LEX((o1, o2) -> new CompareToBuilder().append(o1, o2, recencyArrayComparator).append(o1, o2, specificityComparator)
			.toComparison()),

	MEA((o1, o2) -> new CompareToBuilder().append(o1, o2, firstRecencyEntryComparator).append(o1, o2, LEX)
			.toComparison()),

	RANDOM((o1, o2) -> new CompareToBuilder().append(o1.getRandom(), o2.getRandom()).append(o1, o2, DEPTH)
			.toComparison());

	final Comparator<ConflictSet.RuleAndToken> strategy;

	@Override
	public int compare(RuleAndToken o1, RuleAndToken o2) {
		return strategy.compare(o1, o2);
	}
}
