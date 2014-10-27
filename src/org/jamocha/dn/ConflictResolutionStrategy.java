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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.RandomUtils;
import org.jamocha.dn.ConflictSet.RuleAndToken;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ConflictResolutionStrategy {

	public Optional<ConflictSet.RuleAndToken> pick(final ConflictSet conflictSet);

	public static ConflictResolutionStrategy random = new ConflictResolutionStrategy() {

		@Override
		public Optional<RuleAndToken> pick(ConflictSet conflictSet) {
			final RuleAndToken[] rulesAndTokens = conflictSet.getRulesAndTokens();
			return 0 == rulesAndTokens.length ? Optional.empty() : Optional
					.of(rulesAndTokens[RandomUtils.nextInt(0, rulesAndTokens.length)]);
		}
	};

	public static ConflictResolutionStrategy maxSalience =
			(final ConflictSet conflictSet) -> StreamSupport.stream(
					Arrays.stream(conflictSet.getRulesAndTokens()).spliterator(), true).max(
					(a, b) -> Integer.compare(a.getRule().getParent().getSalience(), b.getRule()
							.getParent().getSalience()));
}
