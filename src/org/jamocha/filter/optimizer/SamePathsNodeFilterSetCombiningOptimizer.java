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
package org.jamocha.filter.optimizer;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterListVisitor;
import org.jamocha.filter.PathNodeFilterSet;

import com.google.common.collect.ImmutableList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamePathsNodeFilterSetCombiningOptimizer implements Optimizer {

	public static final SamePathsNodeFilterSetCombiningOptimizer instance =
			new SamePathsNodeFilterSetCombiningOptimizer();

	/*
	 * Do not consider merging a filter with another filter if one of them is used as an existential
	 * closure. Otherwise we may need to recalculate lots of counter column values over and over
	 * when the new FEs kick out rows that keep reappearing.
	 * 
	 * Thus only consider filters on the same level within
	 * PathFilterList.PathFilterExistentialList#purePart and
	 * PathFilterList.PathFilterSharedListWrapper.PathFilterSharedList#filters.
	 */

	@RequiredArgsConstructor
	static class PathNodeFilterSetCollector<T extends Collection<PathNodeFilterSet>> implements PathFilterListVisitor {
		final T pathNodeFilterSets;

		static ArrayList<PathNodeFilterSet> collectList(final Iterable<PathFilterList> filters) {
			return collect(filters, ArrayList::new);
		}

		static HashSet<PathNodeFilterSet> collectSet(final Iterable<PathFilterList> filters) {
			return collect(filters, HashSet::new);
		}

		static <T extends Collection<PathNodeFilterSet>> T collect(final Iterable<PathFilterList> filters,
				final Supplier<T> supplier) {
			final PathNodeFilterSetCollector<T> instance = new PathNodeFilterSetCollector<>(supplier.get());
			filters.forEach(f -> f.accept(instance));
			return instance.pathNodeFilterSets;
		}

		@Override
		public void visit(final PathNodeFilterSet filter) {
			pathNodeFilterSets.add(filter);
		}

		@Override
		public void visit(final PathExistentialList filter) {
		}

		@Override
		public void visit(final PathSharedList filter) {
		}
	}

	/*
	 * Since we only consider instances of PathNodeFilterSet and only the pure part of
	 * PathExistentialList, everything we combine will be a regular PathNodeFilterSet
	 */
	static PathNodeFilterSet combineFilters(final Iterable<? extends PathFilterList> samePathsFilterSets) {
		return PathNodeFilterSet.newRegularPathNodeFilterSet(stream(samePathsFilterSets)
				.map(a -> (PathNodeFilterSet) a).map(PathNodeFilterSet::getFilters).flatMap(Set::stream)
				.collect(toSet()));
	}

	@RequiredArgsConstructor
	static class Identifier implements PathFilterListVisitor {
		@Override
		public void visit(final PathNodeFilterSet filter) {
		}

		@Override
		public void visit(final PathExistentialList filter) {
			// recurse on pure part only
			filter.getPurePart().accept(this);
		}

		@Override
		public void visit(final PathSharedList filter) {
			final ImmutableList<PathFilterList> unmodifiableFilterListCopy = filter.getUnmodifiableFilterListCopy();
			// recurse
			for (final PathFilterList element : unmodifiableFilterListCopy) {
				element.accept(this);
			}
			final List<PathNodeFilterSet> pathNodeFilterSets =
					PathNodeFilterSetCollector.collectList(unmodifiableFilterListCopy);
			final Map<HashSet<Path>, Set<PathFilterList>> map =
					pathNodeFilterSets.stream().collect(
							groupingBy(pnfs -> PathCollector.newHashSet().collectOnlyInFilterLists(pnfs).getPaths(),
									toSet()));
			for (final Iterable<PathFilterList> set : map.values()) {
				filter.combine(set, SamePathsNodeFilterSetCombiningOptimizer::combineFilters);
			}
		}
	}

	@Override
	public Collection<PathRule> optimize(final Collection<PathRule> rules) {
		final Identifier identifier = new Identifier();
		for (final PathRule pathRule : rules) {
			pathRule.getCondition().accept(identifier);
		}
		return rules;
	}
}
