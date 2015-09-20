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

import static org.jamocha.util.Lambdas.newHashSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
class FilterInstancePartition extends Partition<FilterInstance, FilterInstancePartition.FilterInstanceSubSet> {
	@Getter
	static class FilterInstanceSubSet extends Partition.SubSet<FilterInstance> {
		final Filter filter;

		public FilterInstanceSubSet(final IdentityHashMap<Either<Rule, ExistentialProxy>, FilterInstance> elements) {
			super(elements);
			this.filter = elements.values().iterator().next().getFilter();
		}

		public FilterInstanceSubSet(final Map<Either<Rule, ExistentialProxy>, ? extends FilterInstance> elements) {
			this(new IdentityHashMap<>(elements));
		}

		public FilterInstanceSubSet(final FilterInstanceSubSet copy) {
			super(copy);
			this.filter = copy.filter;
		}

		public boolean contains(final FilterInstancePartition.FilterInstanceSubSet other) {
			return this.elements.entrySet().containsAll(other.elements.entrySet());
		}
	}

	final IdentityHashMap<Filter, Set<FilterInstancePartition.FilterInstanceSubSet>> filterLookup =
			new IdentityHashMap<>();

	public FilterInstancePartition(final FilterInstancePartition copy) {
		super(copy, FilterInstanceSubSet::new);
		for (final FilterInstanceSubSet subSet : this.subSets) {
			this.filterLookup.computeIfAbsent(subSet.filter, newHashSet()).add(subSet);
		}
	}

	@Override
	public void add(final FilterInstancePartition.FilterInstanceSubSet newSubSet) {
		super.add(newSubSet);
		this.filterLookup.computeIfAbsent(newSubSet.filter, newHashSet()).add(newSubSet);
	}

	public Set<FilterInstancePartition.FilterInstanceSubSet> lookupByFilter(final Filter filter) {
		return this.filterLookup.get(filter);
	}

	@Override
	public boolean remove(final FilterInstanceSubSet s) {
		final boolean removed = super.remove(s);
		if (removed) {
			final Set<FilterInstanceSubSet> set = filterLookup.get(s.filter);
			set.remove(s);
			if (set.isEmpty()) {
				filterLookup.remove(s.filter);
			}
		}
		return removed;
	}
}