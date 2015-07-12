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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterListVisitor;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamePathsFilterCombiningOptimizer implements Optimizer {

	static final String name = "SamePathsFilterCombiningOptimizer";
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

	static PathNodeFilterSet combineTwoFilters(final PathNodeFilterSet samePathsFilterSet,
			final PathNodeFilterSet filterSet) {
		final HashSet<Path> pep = new HashSet<>();
		pep.addAll(samePathsFilterSet.getPositiveExistentialPaths());
		pep.addAll(filterSet.getPositiveExistentialPaths());
		final HashSet<Path> nep = new HashSet<>();
		nep.addAll(samePathsFilterSet.getNegativeExistentialPaths());
		nep.addAll(filterSet.getNegativeExistentialPaths());
		final HashSet<PathFilter> filters = new HashSet<>();
		filters.addAll(samePathsFilterSet.getFilters());
		filters.addAll(filterSet.getFilters());
		return PathNodeFilterSet.newExistentialPathNodeFilterSet(pep, nep, filters);
	}

	@RequiredArgsConstructor
	static class Identifier implements PathFilterListVisitor {
		final HashMap<Path, Set<Path>> path2JoinedWith;
		final List<PathFilterList> result = new ArrayList<>();
		final List<PathNodeFilterSet> filtersOnThisLevel = new LinkedList<>();
		final HashMap<Set<Path>, PathNodeFilterSet> joinSet2Filter = new HashMap<>();

		private void save(final PathNodeFilterSet filter, final HashSet<Path> paths) {
			filtersOnThisLevel.add(filter);
			result.add(filter);
			paths.forEach(p -> path2JoinedWith.put(p, paths));
			joinSet2Filter.put(paths, filter);
		}

		@Override
		public void visit(final PathNodeFilterSet filter) {
			final HashSet<Path> currentPaths = PathCollector.newHashSet().collectAll(filter).getPaths();
			if (filtersOnThisLevel.isEmpty()) {
				save(filter, currentPaths);
				return;
			}
			final HashSet<Path> joined =
					currentPaths.stream()
							.flatMap(p -> path2JoinedWith.getOrDefault(p, Collections.singleton(p)).stream())
							.collect(toCollection(HashSet::new));
			final PathNodeFilterSet samePathsFilter = joinSet2Filter.get(joined);
			if (null == samePathsFilter) {
				save(filter, joined);
				return;
			}
			filtersOnThisLevel.remove(samePathsFilter);
			result.remove(samePathsFilter);
			save(combineTwoFilters(samePathsFilter, filter), joined);
		}

		@Override
		public void visit(final PathExistentialList filter) {
			result.add(new PathExistentialList(filter.getInitialPath(), new PathSharedListWrapper()
					.newSharedElement(combine(filter.getPurePart().getFilters())), filter.getExistentialClosure()));
		}

		@Override
		public void visit(final PathSharedList filter) {
			result.add(filter.getWrapper().replace(filter, combine(filter.getFilters())));
		}

		List<PathFilterList> combine(final List<PathFilterList> filters) {
			final Identifier instance = new Identifier(path2JoinedWith);
			filters.forEach(f -> f.accept(instance));
			return instance.result;
		}
	}

	static PathSharedList optimize(final PathSharedList condition) {
		return (PathSharedList) condition.accept(new Identifier(new HashMap<>())).result.get(0);
	}

	@Override
	public Collection<PathRule> optimize(final Collection<PathRule> rules) {
		return rules
				.stream()
				.map(rule -> {
					return rule.getParent().new PathRule(optimize(rule.getCondition()), rule.getResultPaths(), rule
							.getActionList(), rule.getEquivalenceClassToPathLeaf(), rule.getSpecificity());
				}).collect(toList());
	}
}
