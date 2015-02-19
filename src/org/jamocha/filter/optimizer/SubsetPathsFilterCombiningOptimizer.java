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

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.jamocha.filter.optimizer.SamePathsFilterCombiningOptimizer.combineTwoFilters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.TranslatedPath;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathFilterExistentialList;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper.PathFilterSharedList;
import org.jamocha.filter.PathFilterListVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SubsetPathsFilterCombiningOptimizer implements Optimizer {

	static final String name = "SubsetPathsFilterCombiningOptimizer";
	static final SamePathsFilterCombiningOptimizer instance = new SamePathsFilterCombiningOptimizer();
	static {
		OptimizerFactory.addImpl(name, () -> instance);
	}

	/*
	 * Do not consider merging a filter with another filter if one of them is used as an existential
	 * closure. Otherwise we may need to recalculate lots of counter column values over and over
	 * when the new FEs kick out rows that keep reappearing.
	 * 
	 * Thus only consider filters on the same level within
	 * PathFilterList.PathFilterExistentialList#nonExistentialPart and
	 * PathFilterList.PathFilterSharedListWrapper.PathFilterSharedList#filterElements.
	 */

	@RequiredArgsConstructor
	static class Identifier implements PathFilterListVisitor {
		final HashMap<Path, Set<Path>> path2JoinedWith;
		final List<PathFilterList> result = new ArrayList<>();
		final List<PathFilter> filtersOnThisLevel = new LinkedList<>();
		final HashMap<PathFilter, Set<Path>> filter2Paths = new HashMap<>();

		private void save(final PathFilter filter, final HashSet<Path> paths) {
			filtersOnThisLevel.add(filter);
			result.add(filter);
			paths.forEach(p -> path2JoinedWith.put(p, paths));
			filter2Paths.put(filter, paths);
		}

		@Override
		public void visit(final PathFilter filter) {
			final HashSet<Path> currentPaths = PathCollector.newHashSet().collectAll(filter).getPaths();
			if (filtersOnThisLevel.isEmpty()) {
				save(filter, currentPaths);
				return;
			}
			final HashSet<Path> joined =
					currentPaths.stream()
							.flatMap(p -> path2JoinedWith.getOrDefault(p, Collections.singleton(p)).stream())
							.collect(toCollection(HashSet::new));
			for (final ListIterator<PathFilter> listIterator =
					filtersOnThisLevel.listIterator(filtersOnThisLevel.size()); listIterator.hasPrevious();) {
				final PathFilter samePathsFilter = listIterator.previous();
				final Set<Path> set = filter2Paths.get(samePathsFilter);
				if (set.containsAll(joined)) {
					listIterator.remove();
					result.remove(samePathsFilter);
					save(combineTwoFilters(samePathsFilter, filter), joined);
					return;
				}
			}
			save(filter, joined);
		}

		@Override
		public void visit(final PathFilterExistentialList filter) {
			result.add(new PathFilterExistentialList(combine(filter.getNonExistentialPart().getFilterElements()),
					filter.getExistentialClosure()));
		}

		@Override
		public void visit(final PathFilterSharedList filter) {
			result.add(filter.getWrapper().replace(filter, combine(filter.getFilterElements())));
		}

		List<PathFilterList> combine(final List<PathFilterList> filters) {
			final Identifier instance = new Identifier(path2JoinedWith);
			filters.forEach(f -> f.accept(instance));
			return instance.result;
		}
	}

	static PathFilterSharedList optimize(final PathFilterSharedList condition) {
		return (PathFilterSharedList) condition.accept(new Identifier(new HashMap<>())).result.get(0);
	}

	@Override
	public Collection<TranslatedPath> optimize(final Collection<TranslatedPath> rules) {
		return rules
				.stream()
				.map(rule -> {
					return rule.getParent().new TranslatedPath(optimize(rule.getCondition()), rule.getActionList(),
							rule.getEquivalenceClassToPathLeaf(), rule.getSpecificity());
				}).collect(toList());
	}
}