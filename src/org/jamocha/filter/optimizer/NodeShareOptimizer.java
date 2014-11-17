/*
 * Copyright 2002-2014 The Jamocha Team
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.IteratorUtils;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;

import com.google.common.collect.Sets;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class NodeShareOptimizer implements Optimizer {

	final Map<Path, Set<PathFilter>> path2Filter = new HashMap<>();

	final Map<PathFilter, Set<Set<PathFilter>>> filter2Block = new HashMap<>();

	final Map<PathFilter, Set<Path>> filter2Paths = new HashMap<>();

	final Map<PathFilter, Defrule.TranslatedPath> filter2Rule = new HashMap<>();

	final Set<Defrule.TranslatedPath> allRules = new HashSet<>();

	final PathFilterPool pool = new PathFilterPool();

	public void optimize(Defrule... rules) {
		for (Defrule rule : rules) {
			for (Defrule.TranslatedPath translatedPath : rule.getTranslatedPathVersions()) {
				allRules.add(translatedPath);
				for (PathFilter pathFilter : translatedPath.getCondition()) {
					pool.addFilter(pathFilter);
					filter2Rule.put(pathFilter, translatedPath);
					Set<Path> paths = PathCollector.newHashSet().collectAll(pathFilter).getPaths();
					paths.addAll(pathFilter.getNegativeExistentialPaths());
					paths.addAll(pathFilter.getPositiveExistentialPaths());
					filter2Paths.put(pathFilter, paths);
					for (Path path : paths) {
						Set<PathFilter> set = path2Filter.computeIfAbsent(path, p -> new HashSet<>());
						set.add(pathFilter);
					}
				}
			}
		}
		for (Defrule rule : rules) {
			for (Defrule.TranslatedPath translatedPath : rule.getTranslatedPathVersions()) {
				for (PathFilter pathFilter : translatedPath.getCondition()) {
					buildBlock(translatedPath, pathFilter);
				}
			}
		}
	}

	private void buildBlock(final Defrule.TranslatedPath rule, final PathFilter pathFilter) {
		Set<PathFilter> preBlock = new HashSet<>();
		preBlock.add(pathFilter);
		// add all filters producing conflicts in the same rule
		{
			Set<PathFilter> newFilters = new HashSet<>();
			newFilters.add(pathFilter);
			while (!newFilters.isEmpty()) {
				final Set<PathFilter> conflictFilters = new HashSet<>();
				for (PathFilter newFilter : newFilters) {
					for (Path path : filter2Paths.get(newFilter)) {
						conflictFilters.addAll(path2Filter.getOrDefault((path), Collections.emptySet()));
					}
				}
				conflictFilters.removeAll(preBlock);
				newFilters = new HashSet<>();
				for (PathFilter conflictFilter : conflictFilters) {
					final Set<PathFilter> conflictingInBlock =
							filter2Paths.get(conflictFilter).stream().flatMap(p -> path2Filter.get(p).stream())
									.filter(f -> preBlock.contains(f)).collect(toSet());
					if (!filter2Block.get(conflictFilter).stream().allMatch(b -> b.containsAll(conflictingInBlock))) {
						newFilters.add(conflictFilter);
						preBlock.add(conflictFilter);
					}
				}
			}
		}
		// Add all rules which do not produce conflicts
		{
			final Set<PathFilter> preBlockAdds = new HashSet<>();
			final Set<Defrule.TranslatedPath> possibleRules = new HashSet<>();
			possibleRules.addAll(allRules);
			for (PathFilter filter : preBlock) {
				possibleRules.retainAll(pool.getEqualFilters(filter).stream().map(f -> filter2Rule.get(f))
						.collect(toSet()));
			}
			for (Defrule.TranslatedPath possibleRule : possibleRules) {
				final Map<PathFilter, PathFilter> filterMap =
						comparePathFilters(preBlock, Sets.newHashSet(possibleRule.getCondition()));
				if (null != filterMap && checkForConflicts(filterMap.values())) {
					preBlockAdds.addAll(filterMap.values());
				}
			}
		}
	}

	private boolean checkForConflicts(final Collection<PathFilter> values) {
		// FIXME hier weiterarbeiten
		return false;
	}

	private Map<PathFilter, PathFilter> comparePathFilters(final Set<PathFilter> filters1,
			final Set<PathFilter> filters2) {
		return comparePathFilters(filters1, filters2, new HashMap<>());
	}

	private Map<PathFilter, PathFilter> comparePathFilters(final Set<PathFilter> filters1,
			final Set<PathFilter> filters2, final Map<Path, Path> pathMap) {
		if (filters1.size() == 0)
			return new HashMap<>();
		final Iterator<PathFilter> iterator = filters1.iterator();
		final PathFilter filter1 = iterator.next();
		iterator.remove();
		try {
			for (PathFilter filter2 : IteratorUtils.asIterable(pool.getEqualFilters(filter1).stream()
					.filter(f -> filters2.contains(f)).iterator())) {
				final Map<Path, Path> tmpPathMap = new HashMap<>(pathMap);
				if (PathFilter.equals(filter1, filter2, tmpPathMap)) {
					final Map<PathFilter, PathFilter> filterMap = comparePathFilters(filters1, filters2, tmpPathMap);
					if (null != filterMap) {
						filterMap.put(filter1, filter2);
						return filterMap;
					}
				}
			}
			return null;
		} finally {
			filters1.add(filter1);
		}
	}

	private class PathFilterPool {

		Map<Integer, Set<Set<PathFilter>>> pool = new HashMap<>();

		public Set<PathFilter> getEqualFilters(PathFilter filter) {
			Set<Set<PathFilter>> sets = pool.get(filter.getHashCode());
			for (Set<PathFilter> set : sets) {
				if (set.contains(filter))
					return set;
				if (PathFilter.equals(set.iterator().next(), filter))
					return set;
			}
			return null;
		}

		public void addFilter(PathFilter filter) {
			final Set<Set<PathFilter>> setOfSets = pool.get(filter.getHashCode());
			if (null == setOfSets) {
				final Set<Set<PathFilter>> newSetOfSets = new HashSet<>();
				final Set<PathFilter> newSet = new HashSet<>();
				newSet.add(filter);
				newSetOfSets.add(newSet);
				pool.put(filter.getHashCode(), newSetOfSets);
				return;
			}
			for (Set<PathFilter> set : setOfSets) {
				if (PathFilter.equals(set.iterator().next(), filter)) {
					set.add(filter);
					return;
				}
			}
			final Set<PathFilter> newSet = new HashSet<>();
			newSet.add(filter);
			setOfSets.add(newSet);
		}

	}

	private class Block {
		final Map<Defrule.TranslatedPath, Set<PathFilter>> rule2PathFilters = new HashMap<>();

		void addFilter(Defrule.TranslatedPath rule, PathFilter filter) {
			Set<PathFilter> filters = rule2PathFilters.get(rule);
			if (null == filters) {
				filters = new HashSet<>();
				rule2PathFilters.put(rule, filters);
			}
			filters.add(filter);
		}
	}

}
