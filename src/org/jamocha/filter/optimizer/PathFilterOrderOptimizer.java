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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathFilterExistentialList;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper.PathFilterSharedList;
import org.jamocha.filter.PathFilterListVisitor;

/**
 * A class to optimize the order of a list of {@link PathFilter}s
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilterOrderOptimizer {

	static interface GraphComponent {
		Set<Path> getPaths();

		Set<PathFilterList> getPathFilters();

		GraphComponent merge(final Graph graph);

		GraphComponent mergeIntoOther(final Graph graph, final GraphComponent other);
	}

	@RequiredArgsConstructor
	static class CreatedGraphComponent implements GraphComponent {
		final Set<PathFilterList> pathFilters;
		final Set<Path> paths;

		@Override
		public Set<PathFilterList> getPathFilters() {
			return this.pathFilters;
		}

		@Override
		public Set<Path> getPaths() {
			return this.paths;
		}

		@Override
		public GraphComponent merge(final Graph graph) {
			throw new IllegalArgumentException("Component already created!");
		}

		@Override
		public GraphComponent mergeIntoOther(final Graph graph, final GraphComponent other) {
			// set me to be responsible for all filters of the other component
			for (final PathFilterList filter : other.getPathFilters()) {
				graph.overrideComponent(filter, this);
			}
			// add all other filters to my filters
			this.pathFilters.addAll(other.getPathFilters());
			// add all other paths to my paths
			this.paths.addAll(other.getPaths());
			// remove all edges of other and re-add them for me
			final Set<GraphComponent> otherNeighbours = graph.removeEdges(other);
			for (final GraphComponent otherNeighbour : otherNeighbours) {
				graph.addEdge(this, otherNeighbour);
			}
			return this;
		}
	}

	@RequiredArgsConstructor
	static class InitialGraphComponent implements GraphComponent {
		final PathFilterList pathFilter;
		final Set<Path> paths = new HashSet<>();

		@Override
		public Set<PathFilterList> getPathFilters() {
			return Collections.singleton(pathFilter);
		}

		@Override
		public Set<Path> getPaths() {
			return this.paths;
		}

		@Override
		public GraphComponent merge(final Graph graph) {
			final Set<GraphComponent> neighbours = graph.getNeighbours(this);
			// merge components
			GraphComponent result = this;
			for (final GraphComponent neighbour : neighbours) {
				result = neighbour.mergeIntoOther(graph, result);
			}
			if (this == result) {
				// none of the neighbours has already been created or there are no neighbours
				result = new CreatedGraphComponent(new HashSet<>(getPathFilters()), this.paths);
				graph.overrideComponent(this.pathFilter, result);
			}
			// push all paths outwards
			final Set<Path> paths = result.getPaths();
			for (final GraphComponent neighbour : neighbours) {
				neighbour.getPaths().addAll(paths);
			}
			return result;
		}

		@Override
		public GraphComponent mergeIntoOther(final Graph graph, final GraphComponent other) {
			// do nothing
			return other;
		}
	}

	static class Graph {
		final Map<PathFilterList, GraphComponent> filterToComponent = new HashMap<>();
		private final Map<GraphComponent, Set<GraphComponent>> neighbourMap = new HashMap<>();

		public void addEdge(final GraphComponent v, final GraphComponent w) {
			this.neighbourMap.computeIfAbsent(v, x -> new HashSet<>()).add(w);
			this.neighbourMap.computeIfAbsent(w, x -> new HashSet<>()).add(v);
		}

		public boolean removeEdge(final GraphComponent v, final GraphComponent w) {
			final boolean left = this.neighbourMap.get(v).remove(w), right = this.neighbourMap.get(w).remove(v);
			assert left == right;
			return left;
		}

		public Set<GraphComponent> removeEdges(final GraphComponent v) {
			return this.neighbourMap.remove(v);
		}

		public GraphComponent getComponent(final PathFilterList filter) {
			return this.filterToComponent.computeIfAbsent(filter, pf -> new InitialGraphComponent(pf));
		}

		public void overrideComponent(final PathFilterList filter, final GraphComponent component) {
			final GraphComponent graphComponent = this.filterToComponent.put(filter, component);
			assert null != graphComponent;
		}

		public Set<GraphComponent> getNeighbours(final GraphComponent component) {
			return this.neighbourMap.computeIfAbsent(component, x -> new HashSet<>());
		}
	}

	static class Partitioner implements PathFilterListVisitor {
		final ArrayList<PathFilter> pathFilters = new ArrayList<>();
		final ArrayList<PathFilterSharedList> pathFilterSharedLists = new ArrayList<>();
		final ArrayList<PathFilterExistentialList> pathFilterExistentialLists = new ArrayList<>();

		@Override
		public void visit(final PathFilter filter) {
			this.pathFilters.add(filter);
		}

		@Override
		public void visit(final PathFilterExistentialList filter) {
			this.pathFilterExistentialLists.add(filter);
		}

		@Override
		public void visit(final PathFilterSharedList filter) {
			this.pathFilterSharedLists.add(filter);
		}
	}

	public ArrayList<PathFilterList> optimizeUnsharedList(final ArrayList<PathFilterList> list) {
		if (list.isEmpty())
			return list;
		// initialize maps
		final Map<PathFilterList, Set<Path>> filterToPaths = new HashMap<>();
		final Map<Path, Set<PathFilterList>> pathToFilters = new HashMap<>();
		final Graph graph = new Graph();
		for (final PathFilterList pathFilter : list) {
			final HashSet<Path> onlyNonExistentialPaths =
					PathCollector.newHashSet().collectOnlyNonExistential(pathFilter).getPaths();
			filterToPaths.put(pathFilter, onlyNonExistentialPaths);
			for (final Path path : onlyNonExistentialPaths) {
				pathToFilters.computeIfAbsent(path, x -> new HashSet<>()).add(pathFilter);
			}
		}
		// add paths to nodes and create edges between nodes sharing paths
		for (final Entry<Path, Set<PathFilterList>> pathAndFilters : pathToFilters.entrySet()) {
			final Path path = pathAndFilters.getKey();
			final Set<PathFilterList> filters = pathAndFilters.getValue();
			for (final PathFilterList filter : filters) {
				final GraphComponent component = graph.getComponent(filter);
				component.getPaths().add(path);
				for (final PathFilterList other : filters) {
					// don't insert loops
					if (filter == other)
						continue;
					graph.addEdge(component, graph.getComponent(other));
				}
			}
		}
		final LinkedList<PathFilterList> workspace = new LinkedList<>(list);
		list.clear();
		final Comparator<PathFilterList> compareByOurCriteria =
				(final PathFilterList a, final PathFilterList b) -> {
					final GraphComponent componentA = graph.getComponent(a);
					final GraphComponent componentB = graph.getComponent(b);
					final int numEdgesAsFirstCriterion =
							Integer.compare(graph.getNeighbours(componentA).size(), graph.getNeighbours(componentB)
									.size());
					if (0 != numEdgesAsFirstCriterion)
						return numEdgesAsFirstCriterion;
					final int numPathsAsSecondCriterion =
							Integer.compare(componentA.getPaths().size(), componentB.getPaths().size());
					return numPathsAsSecondCriterion;
				};
		while (!workspace.isEmpty()) {
			final PathFilterList min = workspace.stream().min(compareByOurCriteria).get();
			workspace.remove(min);
			list.add(min);
			graph.getComponent(min).merge(graph);
		}
		return list;
	}

	public void optimize(final PathFilterSharedList list) {
		final Partitioner partitioner = new Partitioner();
		final List<PathFilterList> elements = list.getFilterElements();
		elements.forEach(e -> e.accept(partitioner));
		partitioner.pathFilterSharedLists.forEach(this::optimize);
		partitioner.pathFilterExistentialLists.stream().map(PathFilterExistentialList::getNonExistentialPart)
				.forEach(this::optimize);
		elements.clear();
		elements.addAll(partitioner.pathFilterSharedLists);
		{
			final ArrayList<PathFilterList> unshared =
					new ArrayList<>(partitioner.pathFilterExistentialLists.size() + partitioner.pathFilters.size());
			unshared.addAll(partitioner.pathFilterExistentialLists);
			unshared.addAll(partitioner.pathFilters);
			elements.addAll(optimizeUnsharedList(unshared));
		}
	}
}
