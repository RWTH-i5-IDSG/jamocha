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

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import lombok.Getter;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
class FilterProxy extends Filter {
	final Set<ExistentialProxy> proxies;

	private FilterProxy(final FunctionWithArguments<TypeLeaf> predicate, final ExistentialProxy proxy) {
		super(predicate);
		this.proxies = Sets.newHashSet(proxy);
	}

	static final Map<FilterProxy, FilterProxy> cache = new HashMap<>();

	static FilterProxy newFilterProxy(final FunctionWithArguments<TypeLeaf> predicate, final ExistentialProxy proxy) {
		final FilterProxy instance = cache.computeIfAbsent(new FilterProxy(predicate, proxy), Function.identity());
		instance.proxies.add(proxy);
		return instance;
	}

	static Set<FilterProxy> getFilterProxies() {
		return cache.keySet();
	}

	@Override
	public <V extends FilterVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	protected boolean canEqual(final Object other) {
		return other instanceof FilterProxy;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this)
			return true;
		if (!(o instanceof FilterProxy))
			return false;
		final FilterProxy other = (FilterProxy) o;
		if (!other.canEqual(this))
			return false;
		if (!super.equals(other))
			return false;
		if (!equalProxies(this, other))
			return false;
		return true;
	}

	private static boolean equalProxies(final FilterProxy aFilterProxy, final FilterProxy bFilterProxy) {
		final ExistentialProxy aProxy = aFilterProxy.proxies.iterator().next();
		final ExistentialProxy bProxy = bFilterProxy.proxies.iterator().next();
		if (aProxy.existential.getExistentialFactVariables().size() != bProxy.existential.getExistentialFactVariables()
				.size())
			return false;
		if (aProxy.existential.isPositive() != bProxy.existential.isPositive())
			return false;
		final Set<Filter> aFilters = aProxy.getFilters();
		final Set<Filter> bFilters = bProxy.getFilters();
		if (aFilters.size() != bFilters.size())
			return false;
		if (aFilters.size() == 0) {
			return true;
		}

		final List<Set<FilterInstance>> aFilterInstanceSets =
				aFilters.stream().map(f -> f.getAllInstances(aProxy.either)).collect(toCollection(ArrayList::new));
		aFilterInstanceSets.add(Collections.singleton(aProxy.getExistentialClosure()));
		final List<Set<FilterInstance>> bFilterInstanceSets =
				bFilters.stream().map(f -> f.getAllInstances(bProxy.either)).collect(toCollection(ArrayList::new));
		bFilterInstanceSets.add(Collections.singleton(bProxy.getExistentialClosure()));

		final List<FilterInstance> aFlatFilterInstances =
				aFilterInstanceSets.stream().flatMap(Set::stream).collect(toList());

		final Set<List<List<FilterInstance>>> cartesianProduct =
				Sets.cartesianProduct(bFilterInstanceSets.stream()
						.map(set -> Sets.newHashSet(new PermutationIterator<FilterInstance>(set))).collect(toList()));

		final HashMap<FilterInstance, Pair<Integer, Integer>> aFI2IndexPair = new HashMap<>();
		{
			int i = 0;
			for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
				int j = 0;
				for (final FilterInstance filterInstance : aCell) {
					aFI2IndexPair.put(filterInstance, Pair.of(i, j));
					++j;
				}
				++i;
			}
		}
		bijectionLoop: for (final List<List<FilterInstance>> bijection : cartesianProduct) {
			int i = 0;
			for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
				final List<FilterInstance> bCell = bijection.get(i);
				int j = 0;
				for (final FilterInstance aSource : aCell) {
					final FilterInstance bSource = bCell.get(j);
					for (final FilterInstance aTarget : aFlatFilterInstances) {
						final Pair<Integer, Integer> indexPair = aFI2IndexPair.get(aTarget);
						final FilterInstance bTarget = bijection.get(indexPair.getLeft()).get(indexPair.getRight());
						final Set<Pair<Integer, Integer>> aConflict = ECBlocks.getECIndexSet(aSource, aTarget);
						final Set<Pair<Integer, Integer>> bConflict = ECBlocks.getECIndexSet(bSource, bTarget);
						if (!Objects.equals(aConflict, bConflict)) {
							continue bijectionLoop;
						}
					}
					++j;
				}
				++i;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = (result * PRIME) + super.hashCode();
		result =
				(result * PRIME)
						+ (this.proxies == null ? 0 : (this.proxies.iterator().next().filters == null ? 0
								: this.proxies.iterator().next().filters.hashCode()));
		return result;
	}
}