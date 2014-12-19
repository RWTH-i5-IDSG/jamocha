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
	DEPTH(new Comparator<ConflictSet.RuleAndToken>() {
		@Override
		public int compare(final RuleAndToken o1, final RuleAndToken o2) {
			return Long.compare(o1.getActivationCounter(), o2.getActivationCounter());
		}
	}), BREADTH(new Comparator<ConflictSet.RuleAndToken>() {
		@Override
		public int compare(final RuleAndToken o1, final RuleAndToken o2) {
			return -Long.compare(o1.getActivationCounter(), o2.getActivationCounter());
		}
	}), SIMPLICITY(null), COMPLEXITY(null), LEX(null), MEA(null), RANDOM(new Comparator<ConflictSet.RuleAndToken>() {
		@Override
		public int compare(final RuleAndToken o1, final RuleAndToken o2) {
			return new CompareToBuilder().append(o1.getRandom(), o2.getRandom())
					.append(o1.getActivationCounter(), o2.getActivationCounter()).build();
		}
	});

	final Comparator<ConflictSet.RuleAndToken> strategy;

	@Override
	public int compare(RuleAndToken o1, RuleAndToken o2) {
		return strategy.compare(o1, o2);
	}
}
