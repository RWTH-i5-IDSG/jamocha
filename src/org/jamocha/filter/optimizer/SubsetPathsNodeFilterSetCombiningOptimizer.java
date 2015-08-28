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

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterListVisitor;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.filter.optimizer.SamePathsNodeFilterSetCombiningOptimizer.PathNodeFilterSetCollector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SubsetPathsNodeFilterSetCombiningOptimizer implements Optimizer {

	public static final SubsetPathsNodeFilterSetCombiningOptimizer instance =
			new SubsetPathsNodeFilterSetCombiningOptimizer();

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
	static class Identifier implements PathFilterListVisitor {
		private final Map<Path, Set<Path>> pathJoins = new HashMap<>();

		protected Set<Path> collectAndBlowUpPaths(final PathFilterList filter) {
			return blowUp(PathCollector.newHashSet().collectOnlyInFilterLists(filter).getPaths());
		}

		protected Set<Path> blowUp(final Set<Path> paths) {
			return paths.stream().flatMap(p -> pathJoins.computeIfAbsent(p, x -> Sets.newHashSet(x)).stream())
					.collect(toSet());
		}

		@Override
		public void visit(final PathNodeFilterSet filter) {
		}

		@Override
		public void visit(final PathExistentialList filter) {
			filter.getPurePart().accept(this);
		}

		@Override
		public void visit(final PathSharedList filter) {
			final ImmutableList<PathFilterList> elements = filter.getUnmodifiableFilterListCopy();
			final Set<PathNodeFilterSet> pathNodeFilterSets = PathNodeFilterSetCollector.collectSet(elements);

			// at every point in time, there will only be one single PathNodeFilterSet for each set
			// of paths relevant for merging
			final HashMap<Set<Path>, PathNodeFilterSet> pathSetToFilter = new HashMap<>();
			final HashMap<PathNodeFilterSet, LinkedHashSet<PathNodeFilterSet>> joins = new HashMap<>();

			for (final PathFilterList element : elements) {
				// recurse
				element.accept(this);
				// gather current paths
				final Set<Path> currentPaths = collectAndBlowUpPaths(element);
				// only consider PathNodeFilterSet
				if (pathNodeFilterSets.contains(element)) {
					final PathNodeFilterSet currentFilter = (PathNodeFilterSet) element;
					// search for filters containing exactly our (blown up) paths
					final PathNodeFilterSet previousFilter = pathSetToFilter.get(currentPaths);
					if (null != previousFilter) {
						// merge targetFilter and currentFilter
						final LinkedHashSet<PathNodeFilterSet> joinSet =
								joins.computeIfAbsent(previousFilter, x -> Sets.newLinkedHashSet(x));
						joinSet.add(currentFilter);
						joins.put(currentFilter, joinSet);
					}
					pathSetToFilter.put(currentPaths, currentFilter);
					// publish the joined paths
					currentPaths.forEach(path -> pathJoins.put(path, currentPaths));
				}
			}

			final HashSet<LinkedHashSet<PathNodeFilterSet>> joinSets = Sets.newHashSet(joins.values());
			for (final Iterable<? extends PathFilterList> joinSet : joinSets) {
				filter.combine(joinSet, SamePathsNodeFilterSetCombiningOptimizer::combineFilters);
			}
		}
	}

	@Override
	public Collection<PathRule> optimize(final Collection<PathRule> rules) {
		for (final PathRule pathRule : rules) {
			pathRule.getCondition().accept(new Identifier());
		}
		return rules;
	}
}