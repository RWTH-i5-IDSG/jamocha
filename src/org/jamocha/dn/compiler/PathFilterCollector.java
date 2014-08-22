/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.dn.compiler;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.SingleFactVariable;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
@RequiredArgsConstructor
public class PathFilterCollector implements DefaultConditionalElementsVisitor {

	private final Map<SingleFactVariable, Path> paths;
	private final boolean negated;

	@Getter
	private List<PathFilter> pathFilters = null;

	public PathFilterCollector(Map<SingleFactVariable, Path> paths) {
		this.paths = paths;
		this.negated = false;
	}

	public static List<PathFilter> processExistentialCondition(final ConditionalElement ce,
			final Map<SingleFactVariable, Path> fact2Path, final boolean isPositive) {
		// FIXME Fix to work if we only have existential Paths

		// Collect the existential FactVariables and corresponding paths from the existentialCE
		final Map<SingleFactVariable, Path> existentialFact2Path =
				FactVariableCollector.collectPaths(ce);

		// combine existential FactVariables and Paths with non existential ones for PathFilter
		// generation
		final Map<SingleFactVariable, Path> combinedFact2Path =
				new HashMap<SingleFactVariable, Path>(fact2Path);
		combinedFact2Path.putAll(existentialFact2Path);

		// Only existential Paths without Variables
		final Set<Path> existentialPaths = new HashSet<>(existentialFact2Path.values());

		// Generate PathFilters from CE
		final List<PathFilter> filters =
				ce.accept(new PathFilterCollector(combinedFact2Path)).getPathFilters();

		// Collect all used Paths for every PathFilter
		final Map<PathFilter, Set<Path>> filter2Paths =
				filters.stream().collect(
						Collectors.toMap(filter -> filter, filter -> PathCollector.newHashSet()
								.collect(filter).getPaths()));

		// Split PathFilters into those only using existential Paths and those also using non
		// existential Paths
		final Map<Boolean, List<PathFilter>> tmp =
				filters.stream().collect(
						Collectors.partitioningBy(filter -> existentialPaths
								.containsAll(filter2Paths.get(filter))));
		final List<PathFilter> nonPureExistentialFilters = tmp.get(false);
		final List<PathFilter> pureExistentialFilters = tmp.get(true);

		// Add all pureExistentialFilters to result List because they don't have to be combined
		// or ordered
		final List<PathFilter> resultFilters = new ArrayList<>(pureExistentialFilters);

		// Construct Hashmap from Paths to Filters
		final Map<Path, Set<PathFilter>> path2Filters = new HashMap<>();
		for (Entry<PathFilter, Set<Path>> filterAndPaths : filter2Paths.entrySet()) {
			for (final Path path : filterAndPaths.getValue()) {
				Set<PathFilter> value = path2Filters.get(path);
				if (value == null) {
					value = new HashSet<>();
					path2Filters.put(path, value);
				}
				value.add(filterAndPaths.getKey());
			}
		}

		// Find connected components of the existential Paths
		final Set<Set<Path>> joinedExistentialPaths = new HashSet<>();
		// While there are unjoined Filters continue
		final Set<PathFilter> reductionSet = new HashSet<>(pureExistentialFilters);
		while (!reductionSet.isEmpty()) {
			// Take one arbitrary filter
			final Iterator<PathFilter> i = reductionSet.iterator();
			final Set<PathFilter> collectFilters = new HashSet<>();
			collectFilters.add(i.next());
			i.remove();
			Set<PathFilter> newCollectFilters = new HashSet<>(collectFilters);
			final Set<Path> collectPaths = new HashSet<>();
			// While we found new PathFilters in the last round
			while (!newCollectFilters.isEmpty()) {
				// search for all Paths used by the new Filters
				final Set<Path> newCollectPaths =
						newCollectFilters.stream().flatMap(f -> filter2Paths.get(f).stream())
								.collect(Collectors.toSet());
				// removed already known paths
				newCollectPaths.removeAll(collectPaths);
				// add the new ones to the collect set
				collectPaths.addAll(newCollectPaths);
				// search for all filters containing the new found paths
				newCollectFilters =
						newCollectPaths.stream()
								.flatMap(path -> path2Filters.get(path).stream())
								.collect(Collectors.toSet());
				// remove already known filters
				newCollectFilters.removeAll(collectFilters);
				// add them all to the collect set
				collectFilters.addAll(newCollectFilters);
				// remove them from the set of unassigned filters
				reductionSet.removeAll(newCollectFilters);
			}
			// added the join set to the result
			joinedExistentialPaths.add(collectPaths);
		}

		// Combine nonPureExistentialFilters if necessary and add them to result List
		while (!nonPureExistentialFilters.isEmpty()) {
			final Iterator<PathFilter> i = nonPureExistentialFilters.iterator();
			final List<PathFilter> collectFilters = Arrays.asList(i.next());
			i.remove();
			List<PathFilter> newCollectFilters = new ArrayList<>(collectFilters);
			final Set<Path> collectExistentialPaths = new HashSet<>();

			while (!newCollectFilters.isEmpty()) {
				// search for all existential Paths used by the new Filters
				final Set<Path> newCollectExistentialPaths =
						newCollectFilters.stream()
								.flatMap((PathFilter f) -> filter2Paths.get(f).stream())
								.collect(Collectors.toSet());
				// removed already known paths
				newCollectExistentialPaths.retainAll(existentialPaths);
				newCollectExistentialPaths.removeAll(collectExistentialPaths);
				// add the new ones to the collect set
				collectExistentialPaths.addAll(newCollectExistentialPaths);
				// search for all filters containing the new found paths
				newCollectFilters =
						newCollectExistentialPaths.stream()
								.flatMap(path -> path2Filters.get(path).stream())
								.collect(Collectors.toList());
				// remove already known filters
				newCollectFilters.retainAll(nonPureExistentialFilters);
				newCollectFilters.removeAll(collectFilters);
				// add them all to the collect set
				collectFilters.addAll(newCollectFilters);
				// remove them from the set of unassigned filters
				reductionSet.removeAll(newCollectFilters);
			}
			List<PathFilterElement> filterElements = new ArrayList<>();
			for (final PathFilter filter : collectFilters) {
				filterElements.addAll(Arrays.asList(filter.getFilterElements()));
			}

			if (isPositive)
				resultFilters.add(new PathFilter(collectExistentialPaths, new HashSet<>(),
						toArray(filterElements, PathFilterElement[]::new)));
			else
				resultFilters.add(new PathFilter(new HashSet<>(), collectExistentialPaths,
						toArray(filterElements, PathFilterElement[]::new)));
		}

		return resultFilters;
	}

	@Override
	public void defaultAction(ConditionalElement ce) {
		// Just ignore. InittialFactCEs and TemplateCEs already did their job during
		// FactVariable collection
	}

	@Override
	public void visit(AndFunctionConditionalElement ce) {
		pathFilters = new ArrayList<>();
		pathFilters = ce.getChildren().stream()
		// Process all children CEs
				.map(child -> child.accept(new PathFilterCollector(paths)).getPathFilters())
				// merge Lists
				.flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void visit(OrFunctionConditionalElement ce) {
		throw new Error("There should not be any OrFunctionCEs at this level.");
	}

	@Override
	public void visit(ExistentialConditionalElement ce) {
		assert ce.getChildren().size() == 1;
		this.pathFilters = processExistentialCondition(ce.getChildren().get(0), paths, true);
	}

	@Override
	public void visit(NegatedExistentialConditionalElement ce) {
		assert ce.getChildren().size() == 1;
		this.pathFilters = processExistentialCondition(ce.getChildren().get(0), paths, false);
	}

	@Override
	public void visit(NotFunctionConditionalElement ce) {
		assert ce.getChildren().size() == 1;
		// Call a PathFilterCollector for the child of the NotFunctionCE with toggeled negated
		// flag.
		this.pathFilters =
				ce.getChildren().get(0).accept(new PathFilterCollector(paths, !negated))
						.getPathFilters();
	}

	@Override
	public void visit(SharedConditionalElementWrapper ce) {
		// Just ignore the SharedCEWrapper and continue with the inner CE.
		// TODO maybe it will be required to mark the resulting PathFilters for later
		// optimization
		ce.getCe().accept(this);
	}

	@Override
	public void visit(TestConditionalElement ce) {
		// FIXME just copied from old version
		PredicateWithArguments pwa = ce.getPredicateWithArguments();
		pwa.accept(new SymbolToPathTranslator(paths));
		this.pathFilters = Arrays.asList(new PathFilter(new PathFilter.PathFilterElement(pwa)));
	}
	
	/*
	 * Collect all PathFilters inside all children of an OrFunctionConditionalElement, returning a
	 * List of Lists. Each inner List contains the PathFilters of one child.
	 */
	public static class OrPathFilterCollector implements DefaultConditionalElementsVisitor {

		@Getter
		private List<List<PathFilter>> pathFilters = null;

		@Override
		public void defaultAction(ConditionalElement ce) {
			// If there is no OrFunctionConditionalElement just proceed with the CE as it were
			// the only child of an OrFunctionConditionalElement.
			pathFilters =
					Arrays.asList(ce.accept(
							new PathFilterCollector(FactVariableCollector.collectPaths(ce)))
							.getPathFilters());
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			// For each child of the OrCE ...
			pathFilters =
					ce.getChildren()
							.stream()
							.map(child -> child.accept(
							// ... collect all PathFilters in the child
									new PathFilterCollector(FactVariableCollector
											.collectPaths(child))).getPathFilters())
							.collect(Collectors.toCollection(ArrayList::new));
		}
	}
}