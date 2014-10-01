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

import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ConflictResolutionStrategy {

	public Optional<ConflictSet.RuleAndToken> pick(final ConflictSet conflictSet);

	public static ConflictResolutionStrategy random =
			(final ConflictSet conflictSet) -> 0 == conflictSet.rulesAndTokens.size() ? Optional
					.empty() : Optional.of(conflictSet.rulesAndTokens.get(RandomUtils.nextInt(0,
					conflictSet.rulesAndTokens.size())));

	public static ConflictResolutionStrategy maxSalience =
			(final ConflictSet conflictSet) -> StreamSupport
					.stream(conflictSet.spliterator(), true).max(
							(a, b) -> Integer.compare(a.getRule().getParent().getSalience(), b
									.getRule().getParent().getSalience()));
}
