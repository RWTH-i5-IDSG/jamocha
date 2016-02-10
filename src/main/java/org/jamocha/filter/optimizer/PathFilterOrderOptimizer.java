/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.filter.optimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList.ModificationProxy;
import org.jamocha.filter.PathFilterListVisitor;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * A class to optimize the order of a list of {@link PathNodeFilterSet}s
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilterOrderOptimizer implements Optimizer {

    public static final PathFilterOrderOptimizer INSTANCE = new PathFilterOrderOptimizer();

    /**
     * Graph class storing the mapping from filters to graph components and the edges between the graph components.
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    static class Graph {
        /**
         * Component of the "surrounding" graph
         *
         * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
         */
        interface GraphComponent {
            /**
             * Returns the paths that will be joined into the node if this component is constructed.
             *
             * @return the paths that will be joined into the node if this component is constructed
             */
            Set<Path> getPaths();

            /**
             * Create this graph component.
             *
             * @return the created component
             */
            GraphComponent create();

            /**
             * Tries to merge the given other component into this component
             *
             * @param other
             *         component to merge into this component
             * @return this component if the merge was successful, the other component otherwise
             */
            GraphComponent merge(final GraphComponent other);
        }

        @RequiredArgsConstructor
        class CreatedGraphComponent implements GraphComponent {
            final Set<Path> paths;

            @Override
            public Set<Path> getPaths() {
                return this.paths;
            }

            @Override
            public GraphComponent create() {
                throw new IllegalArgumentException("Component already created!");
            }

            @Override
            public GraphComponent merge(final GraphComponent other) {
                // add all other paths to my paths
                this.paths.addAll(other.getPaths());
                // remove all edges of other and re-add them for me
                final Set<GraphComponent> otherNeighbours = removeEdges(other);
                for (final GraphComponent otherNeighbour : otherNeighbours) {
                    addEdge(this, otherNeighbour);
                }
                return this;
            }
        }

        @RequiredArgsConstructor
        class InitialGraphComponent implements GraphComponent {
            final PathFilterList pathFilter;
            final Set<Path> paths;

            @Override
            public Set<Path> getPaths() {
                return this.paths;
            }

            @Override
            public GraphComponent create() {
                final Set<GraphComponent> neighbours = getNeighbours(this);
                // merge components
                GraphComponent result = this;
                for (final GraphComponent neighbour : neighbours) {
                    result = neighbour.merge(result);
                }
                if (this == result) {
                    // none of the neighbours has already been created or there are no neighbours
                    result = new CreatedGraphComponent(this.paths);
                    removeComponentMapping(this.pathFilter);
                }
                // push all paths outwards
                final Set<Path> paths = result.getPaths();
                for (final GraphComponent neighbour : neighbours) {
                    neighbour.getPaths().addAll(paths);
                }
                return result;
            }

            @Override
            public GraphComponent merge(final GraphComponent other) {
                // do nothing
                return other;
            }
        }

        private final Map<PathFilterList, InitialGraphComponent> filterToComponent = new HashMap<>();
        private final Map<GraphComponent, Set<GraphComponent>> neighbourMap = new HashMap<>();

        /**
         * Adds an edge between v and w
         *
         * @param v
         *         node to connect to w
         * @param w
         *         node to connect to v
         */
        public void addEdge(final GraphComponent v, final GraphComponent w) {
            this.neighbourMap.computeIfAbsent(v, x -> new HashSet<>()).add(w);
            this.neighbourMap.computeIfAbsent(w, x -> new HashSet<>()).add(v);
        }

        /**
         * Remove all edges of v and return the previous neighbours
         *
         * @param v
         *         node have the edges removed
         * @return previous neighbours of v
         */
        public Set<GraphComponent> removeEdges(final GraphComponent v) {
            return this.neighbourMap.remove(v);
        }

        /**
         * Create an initial component for the given PathFilterList and set the mapping
         *
         * @param filter
         *         filter to use
         * @return the initial component created and set
         */
        public InitialGraphComponent createInitialComponent(final PathFilterList filter, final Set<Path> paths) {
            final InitialGraphComponent initialGraphComponent = new InitialGraphComponent(filter, paths);
            this.filterToComponent.put(filter, initialGraphComponent);
            return initialGraphComponent;
        }

        /**
         * Returns the mapped graph component for the given PathFilterList.
         *
         * @param filter
         *         filter to use
         * @return the mapped graph component for the given PathFilterList
         */
        public GraphComponent getComponent(final PathFilterList filter) {
            return this.filterToComponent.get(filter);
        }

        /**
         * Removes the mapping for the given PathFilterList.
         *
         * @param filter
         *         filter to use
         * @return the previous mapping for the given PathFilterList
         */
        public InitialGraphComponent removeComponentMapping(final PathFilterList filter) {
            return this.filterToComponent.remove(filter);
        }

        /**
         * Returns the set of neighbours for the given graph component.
         *
         * @param component
         *         graph component to get the neighbours for
         * @return the set of neighbours for the given graph component
         */
        public Set<GraphComponent> getNeighbours(final GraphComponent component) {
            return this.neighbourMap.computeIfAbsent(component, x -> new HashSet<>());
        }
    }

    /**
     * Simple PathFilterListVisitor partitioning the filters into three buckets according to their type: PathFilter,
     * PathFilterSharedList, PathFilterExistentialList
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @Getter
    public static class Partitioner implements PathFilterListVisitor {
        final ArrayList<PathNodeFilterSet> pathFilters = new ArrayList<>();
        final ArrayList<PathSharedList> pathFilterSharedLists = new ArrayList<>();
        final ArrayList<PathExistentialList> pathFilterExistentialLists = new ArrayList<>();

        @Override
        public void visit(final PathNodeFilterSet filter) {
            this.pathFilters.add(filter);
        }

        @Override
        public void visit(final PathExistentialList filter) {
            this.pathFilterExistentialLists.add(filter);
        }

        @Override
        public void visit(final PathSharedList filter) {
            this.pathFilterSharedLists.add(filter);
        }
    }

    /**
     * Optimize the order of the given list of PathFilterLists in-place. Every element of the list is taken to be an
     * atomic block of filters which has to be order-optimized elsewhere.
     *
     * @param list
     *         list of filters to order-optimize
     */
    public void optimizeUnsharedList(final List<PathFilterList> list) {
        if (list.isEmpty()) return;
        // collect the set of PathFilterLists that share paths by creating a mapping from path to
        // all filters using this path
        final Map<Path, Set<PathFilterList>> pathToFilters = new HashMap<>();
        // create a new graph
        final Graph graph = new Graph();
        // for every filter
        for (final PathFilterList pathFilter : list) {
            // collect the paths used in the filter
            final HashSet<Path> allPaths = PathCollector.newHashSet().collectAllInLists(pathFilter).getPaths();
            // create a component for the filter using the collected paths
            graph.createInitialComponent(pathFilter, allPaths);
            // put the filter into the path to filters map using every collected path as a key
            for (final Path path : allPaths) {
                pathToFilters.computeIfAbsent(path, x -> new HashSet<>()).add(pathFilter);
            }
        }
        // create edges between nodes sharing paths
        for (final Set<PathFilterList> filters : pathToFilters.values()) {
            for (final PathFilterList filter : filters) {
                final Graph.GraphComponent component = graph.getComponent(filter);
                for (final PathFilterList other : filters) {
                    // don't insert loops
                    if (filter == other) continue;
                    graph.addEdge(component, graph.getComponent(other));
                }
            }
        }
        // create a copy to be able to repeatedly remove the minimal filter and reduce the scope
        final LinkedList<PathFilterList> workspace = new LinkedList<>(list);
        list.clear();
        // using this comparator, we determine the minimal cost filter
        final Comparator<PathFilterList> compareByOurCriteria = (final PathFilterList a, final PathFilterList b) -> {
            final Graph.GraphComponent componentA = graph.getComponent(a);
            final Graph.GraphComponent componentB = graph.getComponent(b);
            final int numEdgesAsFirstCriterion =
                    Integer.compare(graph.getNeighbours(componentA).size(), graph.getNeighbours(componentB).size());
            if (0 != numEdgesAsFirstCriterion) return numEdgesAsFirstCriterion;
            final int numPathsAsSecondCriterion =
                    Integer.compare(componentA.getPaths().size(), componentB.getPaths().size());
            return numPathsAsSecondCriterion;
        };
        // until the workspace is empty
        while (!workspace.isEmpty()) {
            // determine the minimal filter
            final PathFilterList min = workspace.stream().min(compareByOurCriteria).get();
            // remove it
            workspace.remove(min);
            // append it to the optimal list
            list.add(min);
            // create the component and let the graph push around the paths
            graph.getComponent(min).create();
        }
    }

    /**
     * Optimize the order of the given PathFilterSharedList in-place. Uses recursion to optimize the order of children
     * of the list elements.
     *
     * @param list
     *         list of filters to order-optimize
     */
    public void optimize(final PathSharedList list) {
        final PathSharedListWrapper wrapper = list.getWrapper();
        if (wrappersAlreadyDone.contains(wrapper)) {
            return;
        }
        wrappersAlreadyDone.add(wrapper);
        // partition the children of the list
        final Partitioner partitioner = new Partitioner();
        list.getUnmodifiableFilterListCopy().forEach(e -> e.accept(partitioner));
        // recurse on the children of the shared list elements
        partitioner.pathFilterSharedLists.forEach(this::optimize);
        // recurse on the pure parts of the existential list elements
        partitioner.pathFilterExistentialLists.stream().map(PathExistentialList::getPurePart)
                .forEach(this::identifyAndOptimize);
        // create modification proxy
        final ModificationProxy modificationProxy = list.startModifying();
        // clear the list to re-insert the elements in an optimal way
        modificationProxy.clear();
        // first, insert the shared elements
        modificationProxy.addAll(partitioner.pathFilterSharedLists);
        {
            // afterwards, combine the existential parts (as blocks) and the PathFilters in an
            // optimal order
            final ArrayList<PathFilterList> unshared =
                    new ArrayList<>(partitioner.pathFilterExistentialLists.size() + partitioner.pathFilters.size());
            unshared.addAll(partitioner.pathFilterExistentialLists);
            unshared.addAll(partitioner.pathFilters);
            optimizeUnsharedList(unshared);
            modificationProxy.addAll(unshared);
        }
    }

    public void identifyAndOptimize(final PathFilterList list) {
        list.accept(new PathFilterListVisitor() {
            @Override
            public void visit(final PathSharedList filter) {
                optimize(filter);
            }

            @Override
            public void visit(final PathExistentialList filter) {
                filter.getPurePart().forEach(p -> p.accept(this));
            }

            @Override
            public void visit(final PathNodeFilterSet filter) {
            }
        });
    }

    public Set<PathSharedListWrapper> wrappersAlreadyDone = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public Collection<PathRule> optimize(final Collection<PathRule> rules) {
        for (final PathRule rule : rules) {
            identifyAndOptimize(rule.getCondition());
        }
        wrappersAlreadyDone.clear();
        return rules;
    }
}
