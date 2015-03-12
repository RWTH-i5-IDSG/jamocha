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

import org.jamocha.dn.ConstructCache.Defrule.TranslatedPath;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.DummyPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathFilterExistentialList;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper.PathFilterSharedList;
import org.jamocha.filter.PathFilterListVisitor;
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

	@RequiredArgsConstructor
	static class Identifier implements PathFilterListVisitor {
		final HashMap<Path, Set<Path>> path2JoinedWith;
		PathFilterList result;

		@Override
		public void visit(final PathFilter filter) {
			final PathFilterElement[] filterElements = filter.getFilterElements();
			final List<PathFilterElement> resultFEs = new ArrayList<PathFilterElement>();
			final HashMap<Set<Path>, PathFilterElement> joinSet2FilterElement = new HashMap<>();
			for (final PathFilterElement fe : filterElements) {
				if (fe != filterElements[filterElements.length - 1] && fe instanceof DummyPathFilterElement) {
					continue;
				}
				final HashSet<Path> currentPaths = PathCollector.newHashSet().collect(fe).getPaths();
				if (resultFEs.isEmpty()) {
					save(resultFEs, joinSet2FilterElement, fe, currentPaths);
					continue;
				}
				final HashSet<Path> joined =
						currentPaths.stream()
								.flatMap(p -> path2JoinedWith.getOrDefault(p, Collections.singleton(p)).stream())
								.collect(toCollection(HashSet::new));
				final PathFilterElement samePathsFilterElement = joinSet2FilterElement.get(joined);
				if (null == samePathsFilterElement) {
					save(resultFEs, joinSet2FilterElement, fe, joined);
					continue;
				}
				// TODO handle dummy filter elements more robustly
				resultFEs.remove(samePathsFilterElement);
				save(resultFEs, joinSet2FilterElement, combineTwoFiltersElements(samePathsFilterElement, fe), joined);
			}
			result =
					new PathFilter(filter.getPositiveExistentialPaths(), filter.getNegativeExistentialPaths(), toArray(
							resultFEs, PathFilterElement[]::new));
		}

		PathFilterElement combineTwoFiltersElements(final PathFilterElement samePathsFilterElement,
				final PathFilterElement fe) {
			final PredicateWithArguments<PathLeaf> lastFunction = samePathsFilterElement.getFunction();
			final PredicateWithArguments<PathLeaf> nextFunction = fe.getFunction();
			final PredicateWithArguments<PathLeaf> newFunction =
					GenericWithArgumentsComposite.newPredicateInstance(And.inClips, lastFunction, nextFunction);
			return new PathFilterElement(newFunction);
		}

		private void save(final List<PathFilterElement> result,
				final HashMap<Set<Path>, PathFilterElement> joinSet2FilterElement, final PathFilterElement fe,
				final HashSet<Path> currentPaths) {
			result.add(fe);
			currentPaths.forEach(p -> path2JoinedWith.put(p, currentPaths));
			joinSet2FilterElement.put(currentPaths, fe);
		}

		@Override
		public void visit(final PathFilterExistentialList filter) {
			result =
					new PathFilterExistentialList(combine(filter.getNonExistentialPart().getFilterElements()),
							filter.getExistentialClosure());
		}

		@Override
		public void visit(final PathFilterSharedList filter) {
			result = filter.getWrapper().replace(filter, combine(filter.getFilterElements()));
		}

		List<PathFilterList> combine(final List<PathFilterList> filters) {
			return filters.stream().map(f -> f.accept(new Identifier(path2JoinedWith)).result).collect(toList());
		}
	}

	static PathFilterSharedList optimize(final PathFilterSharedList condition) {
		return (PathFilterSharedList) condition.accept(new Identifier(new HashMap<>())).result;
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
