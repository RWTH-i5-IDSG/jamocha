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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jamocha.dn.ConstructCache.Defrule.TranslatedPath;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper;

import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class SurplusNodesCombiningOptimizer implements Optimizer {

	static final String name = "SurplusNodesCombiner";
	static final SurplusNodesCombiningOptimizer instance = new SurplusNodesCombiningOptimizer();
	static {
		OptimizerFactory.addImpl(name, () -> instance);
	}

	@Override
	public Collection<TranslatedPath> optimize(final Collection<TranslatedPath> rules) {
		return rules
				.stream()
				.map(rule -> {
					final LinkedList<PathFilterList> filters = new LinkedList<>();
					final HashMap<Path, Set<Path>> path2JoinedWith = new HashMap<>();
					final HashMap<Set<Path>, PathFilter> joinSet2Filter = new HashMap<>();
					for (final PathFilter filter : rule.getCondition()) {
						final HashSet<Path> currentPaths = PathCollector.newHashSet().collectAll(filter).getPaths();
						if (filters.isEmpty()) {
							filters.add(filter);
							currentPaths.forEach(p -> path2JoinedWith.put(p, currentPaths));
							joinSet2Filter.put(currentPaths, filter);
							continue;
						}
						final HashSet<Path> joined =
								currentPaths
										.stream()
										.flatMap(
												p -> path2JoinedWith.getOrDefault(p, Collections.singleton(p)).stream())
										.collect(toCollection(HashSet::new));
						final PathFilter samePathsFilter = joinSet2Filter.get(joined);
						if (null == samePathsFilter) {
							filters.add(filter);
							joined.forEach(p -> path2JoinedWith.put(p, joined));
							joinSet2Filter.put(joined, filter);
							continue;
						}
						filters.remove(samePathsFilter);
						final PathFilterElement[] lastFEs = samePathsFilter.getFilterElements();
						final PathFilterElement[] nextFEs = filter.getFilterElements();
						final PathFilterElement[] pfes = new PathFilterElement[lastFEs.length + nextFEs.length];
						System.arraycopy(lastFEs, 0, pfes, 0, lastFEs.length);
						System.arraycopy(nextFEs, 0, pfes, lastFEs.length, nextFEs.length);
						final PathFilter combinedFilter =
								new PathFilter(Sets.union(samePathsFilter.getPositiveExistentialPaths(),
										filter.getPositiveExistentialPaths()), Sets.union(
										samePathsFilter.getNegativeExistentialPaths(),
										filter.getNegativeExistentialPaths()), pfes);
						filters.add(combinedFilter);
						joined.forEach(p -> path2JoinedWith.put(p, joined));
						joinSet2Filter.put(joined, combinedFilter);
					}
					return rule.getParent().new TranslatedPath(new PathFilterSharedListWrapper()
							.newSharedElement(filters), rule.getActionList(), rule.getSpecificity());
				}).collect(toList());
	}

}
