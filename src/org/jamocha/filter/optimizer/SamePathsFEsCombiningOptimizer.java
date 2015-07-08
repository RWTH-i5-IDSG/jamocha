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
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterListVisitor;
import org.jamocha.filter.PathFilterVisitor;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.filter.PathNodeFilterSet.DummyPathFilter;
import org.jamocha.filter.PathNodeFilterSet.PathFilter;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.impls.predicates.And;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamePathsFEsCombiningOptimizer implements Optimizer {

	static final String name = "SamePathsFEsCombiningOptimizer";
	static final SamePathsFEsCombiningOptimizer instance = new SamePathsFEsCombiningOptimizer();
	static {
		OptimizerFactory.addImpl(name, () -> instance);
	}

	static class Partitioner implements PathFilterVisitor {
		final Set<PathFilter> regular = new HashSet<>(), dummies = new HashSet<>();

		@Override
		public void visit(final PathFilter f) {
			this.regular.add(f);
		}

		@Override
		public void visit(final DummyPathFilter f) {
			dummies.add(f);
		}

	}

	@RequiredArgsConstructor
	static class Identifier implements PathFilterListVisitor {
		final HashMap<Path, Set<Path>> path2JoinedWith;
		PathFilterList result;

		@Override
		public void visit(final PathNodeFilterSet filterSet) {
			final Set<PathFilter> filters = filterSet.getFilters();
			final List<PathFilter> resultFilters = new ArrayList<PathFilter>();
			final HashMap<Set<Path>, PathFilter> joinSet2FilterElement = new HashMap<>();
			final Partitioner partitioner = new Partitioner();
			filters.forEach(f -> f.accept(partitioner));
			for (final PathFilter filter : partitioner.regular) {
				final HashSet<Path> currentPaths = PathCollector.newHashSet().collect(filter).getPaths();
				if (resultFilters.isEmpty()) {
					save(resultFilters, joinSet2FilterElement, filter, currentPaths);
					continue;
				}
				final HashSet<Path> joined =
						currentPaths.stream()
								.flatMap(p -> path2JoinedWith.getOrDefault(p, Collections.singleton(p)).stream())
								.collect(toCollection(HashSet::new));
				final PathFilter samePathsFilterElement = joinSet2FilterElement.get(joined);
				if (null == samePathsFilterElement) {
					save(resultFilters, joinSet2FilterElement, filter, joined);
					continue;
				}
				resultFilters.remove(samePathsFilterElement);
				save(resultFilters, joinSet2FilterElement, combineTwoFiltersElements(samePathsFilterElement, filter),
						joined);
			}
			final PathCollector<HashSet<Path>> pathCollector = PathCollector.newHashSet();
			partitioner.dummies.forEach(d -> d.accept(pathCollector));
			save(resultFilters, joinSet2FilterElement, new DummyPathFilter(pathCollector.getPathsArray()),
					pathCollector.getPaths());
			result =
					PathNodeFilterSet.newExistentialPathNodeFilterSet(filterSet.getPositiveExistentialPaths(),
							filterSet.getNegativeExistentialPaths(), toArray(resultFilters, PathFilter[]::new));
		}

		PathFilter combineTwoFiltersElements(final PathFilter samePathsFilterElement, final PathFilter fe) {
			final PredicateWithArguments<PathLeaf> lastFunction = samePathsFilterElement.getFunction();
			final PredicateWithArguments<PathLeaf> nextFunction = fe.getFunction();
			final PredicateWithArguments<PathLeaf> newFunction =
					GenericWithArgumentsComposite.newPredicateInstance(And.inClips, lastFunction, nextFunction);
			return new PathFilter(newFunction);
		}

		private void save(final List<PathFilter> result, final HashMap<Set<Path>, PathFilter> joinSet2FilterElement,
				final PathFilter fe, final HashSet<Path> currentPaths) {
			result.add(fe);
			currentPaths.forEach(p -> path2JoinedWith.put(p, currentPaths));
			joinSet2FilterElement.put(currentPaths, fe);
		}

		@Override
		public void visit(final PathExistentialList filter) {
			result =
					new PathExistentialList(filter.getInitialPath(), filter.getEquivalenceClasses(),
							processShared(filter.getPurePart()), filter.getExistentialClosure());
		}

		@Override
		public void visit(final PathSharedList filter) {
			result = processShared(filter);
		}

		private PathSharedList processShared(final PathSharedList filter) {
			return filter.getWrapper().replace(filter, combine(filter.getFilters()));
		}

		List<PathFilterList> combine(final List<PathFilterList> filters) {
			return filters.stream().map(f -> f.accept(new Identifier(path2JoinedWith)).result).collect(toList());
		}
	}

	static PathSharedList optimize(final PathSharedList condition) {
		return (PathSharedList) condition.accept(new Identifier(new HashMap<>())).result;
	}

	@Override
	public Collection<PathRule> optimize(final Collection<PathRule> rules) {
		return rules
				.stream()
				.map(rule -> {
					return rule.getParent().new PathRule(optimize(rule.getCondition()), rule.getActionList(),
							rule.getEquivalenceClassToPathLeaf(), rule.getSpecificity());
				}).collect(toList());
	}
}
