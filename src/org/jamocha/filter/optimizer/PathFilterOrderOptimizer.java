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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathFilterListExistential;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper.PathFilterSharedList;
import org.jamocha.filter.PathFilterListVisitor;

/**
 * A class to optimize the order of a list of {@link PathFilter}s
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilterOrderOptimizer {

	static class Graph {
		private final Map<PathFilterList, Map<PathFilterList, Set<Path>>> edges = new HashMap<>();

		private Set<Path> get(final PathFilterList v, final PathFilterList w) {
			final Set<Path> pathSet =
					this.edges.computeIfAbsent(v, x -> new HashMap<>()).computeIfAbsent(w, x -> new HashSet<>());
			final Set<Path> previous = this.edges.computeIfAbsent(w, x -> new HashMap<>()).put(v, pathSet);
			assert previous == null || previous == pathSet;
			return pathSet;
		}

		public void addPathToEdge(final PathFilterList v, final PathFilterList w, final Path path) {
			get(v, w).add(path);
		}

		public void addPathsToEdge(final PathFilterList v, final PathFilterList w, final Collection<Path> paths) {
			get(v, w).addAll(paths);
		}

		public void addPathToNode(final PathFilterList v, final Path path) {
			addPathToEdge(v, v, path);
		}

		public void addPathsToNode(final PathFilterList v, final Collection<Path> paths) {
			addPathsToEdge(v, v, paths);
		}
	}

	static class Partitioner implements PathFilterListVisitor {
		final ArrayList<PathFilter> pathFilters = new ArrayList<>();
		final ArrayList<PathFilterSharedList> pathFilterSharedLists = new ArrayList<>();
		final ArrayList<PathFilterListExistential> pathFilterListExistentials = new ArrayList<>();

		@Override
		public void visit(final PathFilter filter) {
			this.pathFilters.add(filter);
		}

		@Override
		public void visit(final PathFilterListExistential filter) {
			this.pathFilterListExistentials.add(filter);
		}

		@Override
		public void visit(final PathFilterSharedList filter) {
			this.pathFilterSharedLists.add(filter);
		}
	}

	final Map<PathFilterList, Set<Path>> filterToPaths = new HashMap<>();
	final Map<Path, Set<PathFilterList>> pathToFilters = new HashMap<>();
	final Graph graph = new Graph();

	public <T extends PathFilterList> void optimizeHomogenousList(final ArrayList<T> list) {
		if (list.isEmpty())
			return;
		// initialize maps
		for (final PathFilterList pathFilter : list) {
			final Set<Path> paths =
					this.filterToPaths.computeIfAbsent(pathFilter, pf -> PathCollector.newHashSet().collectAll(pf)
							.getPaths());
			for (final Path path : paths) {
				this.pathToFilters.computeIfAbsent(path, x -> new HashSet<>()).add(pathFilter);
			}
		}

		for (final Entry<Path, Set<PathFilterList>> pathAndFilters : this.pathToFilters.entrySet()) {
			final Path path = pathAndFilters.getKey();
			for (final PathFilterList filter : pathAndFilters.getValue()) {

			}
		}
	}

	public void optimize(final PathFilterSharedList list) {
		final Partitioner partitioner = new Partitioner();
		final List<PathFilterList> elements = list.getFilterElements();
		elements.forEach(e -> e.accept(partitioner));
		partitioner.pathFilterSharedLists.forEach(this::optimize);
		partitioner.pathFilterListExistentials.stream().map(PathFilterListExistential::getNonExistentialPart)
		.forEach(this::optimize);
		elements.clear();
		optimizeHomogenousList(partitioner.pathFilterSharedLists);
		optimizeHomogenousList(partitioner.pathFilterListExistentials);
		optimizeHomogenousList(partitioner.pathFilters);
		elements.addAll(partitioner.pathFilterSharedLists);
		elements.addAll(partitioner.pathFilterListExistentials);
		elements.addAll(partitioner.pathFilters);
	}
}
