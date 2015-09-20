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
package org.jamocha.dn.compiler.ecblocks;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.languages.common.SingleFactVariable;

import com.atlassian.fugue.Either;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class Rule {
	final ECSetRule original;
	final Set<Filter> filters = new HashSet<>();
	final Set<SingleFactVariable> factvariables;
	final BiMap<Filter.FilterInstance, ExistentialProxy> existentialProxies = HashBiMap.create();
	final Either<Rule, ExistentialProxy> either;

	public Rule(final ECSetRule original) {
		assert !original.getFactVariables().isEmpty();
		this.original = original;
		this.factvariables = ImmutableSet.copyOf(original.getFactVariables());
		this.either = Either.left(this);
	}

	@Override
	public String toString() {
		return this.original.getParent().getName() + "@" + Integer.toHexString(System.identityHashCode(this));
	}
}