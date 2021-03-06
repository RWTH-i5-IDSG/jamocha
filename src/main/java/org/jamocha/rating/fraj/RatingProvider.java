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
package org.jamocha.rating.fraj;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import io.atlassian.fugue.Iterables;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.*;
import org.jamocha.rating.StatisticsProvider;
import org.jamocha.rating.StatisticsProvider.Data;
import org.jamocha.util.Lambdas;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.DoubleStream.Builder;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.toHashSet;

/**
 * Actual implementation of the Rating algorithm.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
public class RatingProvider implements org.jamocha.rating.RatingProvider {
    final DoubleBinaryOperator cpuAndMemCostCombiner;

    @Override
    public double rateVirtualAlpha(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Set<PathFilterList> preNetwork) {
        return calculateAlphaInformation(statisticsProvider, toRate, preNetwork, 0, 1);
    }

    @Override
    public double rateMaterialisedAlpha(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Set<PathFilterList> preNetwork) {
        return calculateAlphaInformation(statisticsProvider, toRate, preNetwork, 1, 2);
    }

    private double calculateAlphaInformation(final StatisticsProvider statisticsProvider,
            final PathNodeFilterSet toRate, final Set<PathFilterList> preNetwork, final int memMulti,
            final int cpuMulti) {
        final Data data = statisticsProvider.getData(preNetwork);
        final double selectivity = statisticsProvider.getSelectivity(toRate, preNetwork);
        final double newRows = selectivity * data.getRowCount();
        final double newFinsert = selectivity * data.getFinsert();
        final double newFdelete = selectivity * data.getFdelete();
        final Data newData = new Data(newFinsert, newFdelete, newRows, 1);
        final HashSet<PathFilterList> newPreNetwork = new HashSet<>(preNetwork);
        newPreNetwork.add(toRate);
        statisticsProvider.setData(newPreNetwork, newData);
        return cpuAndMemCostCombiner.applyAsDouble(memMulti * newData.getRowCount(),
                cpuMulti * (newData.getFinsert() + newData.getFdelete()));
    }

    @Override
    public double rateBeta(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Map<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>>
                    componentToJoinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        if (toRate.getNegativeExistentialPaths().isEmpty() && toRate.getPositiveExistentialPaths().isEmpty()) {
            return rateBetaWithoutExistentials(statisticsProvider, toRate, componentToJoinOrder,
                    pathToPreNetworkComponents);
        }
        return rateBetaWithExistentials(statisticsProvider, toRate, componentToJoinOrder, pathToPreNetworkComponents);
    }

    private double cardenas(final double m, final double k) {
        if (k <= 1) return k;
        if (m == 0) {
            return 0;
        }
        return m * (1 - Math.pow((1 - 1 / m), k));
    }

    private double jc(final StatisticsProvider statisticsProvider, final double jsf, final Data r, final double size) {
        return cardenas(m(statisticsProvider, r), size * r.getRowCount() * jsf);
    }

    private double m(final StatisticsProvider statisticsProvider, final Data data) {
        return m(data.getRowCount(), statisticsProvider.getPageSize() / data.getTupleSize());
    }

    private double m(final double rowCount, final double tuplesPerPage) {
        return Math.ceil(rowCount / tuplesPerPage);
    }

    private double calcBetaUnfilteredSize(final StatisticsProvider statisticsProvider,
            final Map<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>>
                    componentToJoinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents,
            final Set<Set<PathFilterList>> regularComponents) {
        double size = 0;
        for (final Set<PathFilterList> component : regularComponents) {
            final Data data = statisticsProvider.getData(component);
            size += data.getRowCount() * joinSize(statisticsProvider, component, componentToJoinOrder.get(component),
                    regularComponents, pathToPreNetworkComponents);
        }
        return size / regularComponents.size();
    }

    /**
     * dnrating (38)
     *
     * @param statisticsProvider
     * @param inputComponent
     * @param joinOrder
     * @param positiveExistentialComponents
     * @param negativeExistentialComponents
     * @param pathToPreNetworkComponents
     * @param xOverUX
     * @return
     */
    private double joinSize(final StatisticsProvider statisticsProvider, final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Set<Set<PathFilterList>> positiveExistentialComponents,
            final Set<Set<PathFilterList>> negativeExistentialComponents,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents, final double xOverUX) {
        final double[] jsfs = statisticsProvider.getAllJSFs(inputComponent, joinOrder, pathToPreNetworkComponents);
        // dnrating (35)
        final double sum;
        {
            final Builder values = DoubleStream.builder();
            int i = 0;
            for (final Pair<List<Set<PathFilterList>>, List<PathFilter>> element : joinOrder) {
                assert i < jsfs.length;
                final List<Set<PathFilterList>> components = element.getLeft();
                for (final Set<PathFilterList> component : components) {
                    if (positiveExistentialComponents.contains(component)) {
                        values.accept(statisticsProvider.getData(component).getRowCount() * (1 - jsfs[i]));
                    } else if (negativeExistentialComponents.contains(component)) {
                        values.accept(statisticsProvider.getData(component).getRowCount() * jsfs[i]);
                    }
                }
                ++i;
            }
            sum = values.build().sum();
        }

        final Builder sizes = DoubleStream.builder();
        int i = 0;
        for (final Pair<List<Set<PathFilterList>>, List<PathFilter>> element : joinOrder) {
            final List<Set<PathFilterList>> components = element.getLeft();
            final Iterator<Set<PathFilterList>> iterator = components.iterator();
            final Set<PathFilterList> component = iterator.next();
            if (positiveExistentialComponents.contains(component)) {
                sizes.accept(
                        Math.pow(xOverUX, statisticsProvider.getData(component).getRowCount() * (1 - jsfs[i]) / sum));
            } else if (negativeExistentialComponents.contains(component)) {
                sizes.accept(Math.pow(xOverUX, statisticsProvider.getData(component).getRowCount() * jsfs[i] / sum));
            } else {
                sizes.accept(jsfs[i]);
                sizes.accept(statisticsProvider.getData(component).getRowCount());
                while (iterator.hasNext()) {
                    sizes.accept(statisticsProvider.getData(iterator.next()).getRowCount());
                }
            }
            ++i;
        }
        return sizes.build().sum();
    }

    /**
     * dnrating (24)
     *
     * @param statisticsProvider
     * @param inputComponent
     * @param joinOrder
     * @param regularComponents
     * @param pathToPreNetworkComponents
     * @return
     */
    private double joinSize(final StatisticsProvider statisticsProvider, final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Set<Set<PathFilterList>> regularComponents,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        final double[] jsfs = statisticsProvider
                .getRegularJSFs(inputComponent, joinOrder, regularComponents, pathToPreNetworkComponents);
        double size = 1;
        for (int i = 0; i < jsfs.length; ++i) {
            size *= jsfs[i];
            final List<Set<PathFilterList>> components = joinOrder.get(i).getLeft();
            for (final Set<PathFilterList> component : components) {
                if (!regularComponents.contains(component)) continue;
                size *= statisticsProvider.getData(component).getRowCount();
            }
        }
        return size;
    }

    private double costPosInsVarI(final StatisticsProvider statisticsProvider, final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Set<Set<PathFilterList>> regularComponents,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        final double[] jsfs = statisticsProvider
                .getRegularJSFs(inputComponent, joinOrder, regularComponents, pathToPreNetworkComponents);
        double costs = 0;
        double size = 1;
        for (int i = 0; i < joinOrder.size(); i++) {
            final Pair<List<Set<PathFilterList>>, List<PathFilter>> joinElement = joinOrder.get(i);
            final List<Set<PathFilterList>> inputs = joinElement.getLeft();
            assert inputs.size() > 0 : joinOrder;
            for (int j = 0; j < inputs.size() - 1; ++j) {
                final Set<PathFilterList> component = inputs.get(j);
                final Data rData = statisticsProvider.getData(component);
                costs += jc(statisticsProvider, jsfs[i], rData, rData.getRowCount());
                size *= rData.getRowCount();
            }
            final Set<PathFilterList> r = inputs.get(inputs.size() - 1);
            final Data rData = statisticsProvider.getData(r);
            costs += jc(statisticsProvider, jsfs[i], rData, size);
            size *= rData.getRowCount() * jsfs[i];
        }
        return costs;
    }

    private double rateBetaWithoutExistentials(final StatisticsProvider statisticsProvider,
            final PathNodeFilterSet toRate,
            final Map<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>>
                    componentToJoinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        final IdentityHashMap<Set<PathFilterList>, Data> preNetworkComponentToData = new IdentityHashMap<>();
        for (final Set<PathFilterList> comp : componentToJoinOrder.keySet()) {
            preNetworkComponentToData.put(comp, statisticsProvider.getData(comp));
        }
        final double tupleSize = preNetworkComponentToData.values().stream().mapToDouble(Data::getTupleSize).sum();
        final double tuplesPerPage = statisticsProvider.getPageSize() / tupleSize;
        final double rowCount =
                calcBetaUnfilteredSize(statisticsProvider, componentToJoinOrder, pathToPreNetworkComponents,
                        componentToJoinOrder.keySet());
        // joinsize is needed twice per component, thus pre-calculate it
        final Map<Set<PathFilterList>, Double> preNetworkComponentToJoinSize =
                preNetworkComponentToData.keySet().stream().collect(toMap(Function.identity(),
                        component -> joinSize(statisticsProvider, component, componentToJoinOrder.get(component),
                                componentToJoinOrder.keySet(), pathToPreNetworkComponents)));
        final double finsert = preNetworkComponentToData.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getFinsert() * preNetworkComponentToJoinSize.get(entry.getKey()))
                .sum();
        final double fdelete = preNetworkComponentToData.values().stream().mapToDouble(Data::getFdelete).sum();
        // publish information to statistics provider
        {
            final Set<PathFilterList> filters = new HashSet<>();
            componentToJoinOrder.keySet().forEach(filters::addAll);
            filters.add(toRate);
            statisticsProvider.setData(filters, new Data(finsert, fdelete, rowCount, tupleSize));
        }
        final double mxBeta = m(rowCount, tuplesPerPage);
        final double runtimeCost = preNetworkComponentToData.entrySet().stream().mapToDouble(entry -> {
            final Set<PathFilterList> component = entry.getKey();
            final Data data = entry.getValue();
            return data.getFinsert() * costPosInsVarI(statisticsProvider, component,
                    componentToJoinOrder.get(component), componentToJoinOrder.keySet(), pathToPreNetworkComponents)
                    + data.getFdelete() * (mxBeta + cardenas(mxBeta, preNetworkComponentToJoinSize.get(component)));
        }).sum();
        final double memoryCost = rowCount * tupleSize;
        return cpuAndMemCostCombiner.applyAsDouble(runtimeCost, memoryCost);
    }

    private double rateBetaWithExistentials(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Map<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>>
                    componentToJoinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        final Set<Path> positiveExistentialPaths = toRate.getPositiveExistentialPaths();
        final Set<Path> negativeExistentialPaths = toRate.getNegativeExistentialPaths();
        final Set<Set<PathFilterList>> positiveExistentialComponents = new HashSet<>(), negativeExistentialComponents =
                new HashSet<>(), regularComponents = new HashSet<>();
        final Set<Set<PathFilterList>> preNetworkComponents = new HashSet<>(pathToPreNetworkComponents.values());
        for (final Set<PathFilterList> preNetworkComponent : preNetworkComponents) {
            final PathCollector<HashSet<Path>> pathCollector = PathCollector.newHashSet();
            preNetworkComponent.forEach(pathCollector::collectAllInLists);
            final HashSet<Path> paths = pathCollector.getPaths();
            if (!Collections.disjoint(paths, positiveExistentialPaths)) {
                positiveExistentialComponents.add(preNetworkComponent);
            } else if (!Collections.disjoint(paths, negativeExistentialPaths)) {
                negativeExistentialComponents.add(preNetworkComponent);
            } else {
                regularComponents.add(preNetworkComponent);
            }
        }
        final Map<Set<PathFilterList>, Data> preNetworkComponentToData =
                preNetworkComponents.stream().collect(toMap(Function.identity(), statisticsProvider::getData));
        final Map<Set<PathFilterList>, PathFilter> existentialComponentToFilter =
                componentToJoinOrder.values().iterator().next().stream()
                        .filter(p -> !regularComponents.contains(p.getLeft().iterator().next()))
                        .collect(toMap(p -> p.getLeft().iterator().next(), p -> p.getRight().iterator().next()));

        final double tupleSize =
                regularComponents.stream().mapToDouble(c -> preNetworkComponentToData.get(c).getTupleSize()).sum();
        final double tuplesPerPage = statisticsProvider.getPageSize() / tupleSize;
        final double unfilteredRowCount =
                calcBetaUnfilteredSize(statisticsProvider, componentToJoinOrder, pathToPreNetworkComponents,
                        regularComponents);
        final double rowCount = unfilteredRowCount * DoubleStream.concat(positiveExistentialComponents.stream()
                        .mapToDouble(component -> 1 - Math.pow((1 - statisticsProvider
                                .getJSF(regularComponents, component, existentialComponentToFilter.get(component),
                                        pathToPreNetworkComponents)), preNetworkComponentToData.get(component)
                                .getRowCount())),
                negativeExistentialComponents.stream().mapToDouble(component -> Math.pow((1 - statisticsProvider
                        .getJSF(regularComponents, component, existentialComponentToFilter.get(component),
                                pathToPreNetworkComponents)), preNetworkComponentToData.get(component).getRowCount())))
                .reduce(1.0, (a, b) -> a * b);
        final double xOverUX = rowCount / unfilteredRowCount;
        // joinsize is needed twice per component, thus pre-calculate it
        final Map<Set<PathFilterList>, Double> regularComponentToJoinSize = regularComponents.stream().collect(
                toMap(Function.identity(),
                        component -> joinSize(statisticsProvider, component, componentToJoinOrder.get(component),
                                positiveExistentialComponents, negativeExistentialComponents,
                                pathToPreNetworkComponents, xOverUX)));
        // dnrating (30a)
        final double finsert = xOverUX * regularComponents.stream().mapToDouble(
                component -> preNetworkComponentToData.get(component).getFinsert() * regularComponentToJoinSize
                        .get(component)).sum() + DoubleStream
                .concat(negativeExistentialComponents.stream().mapToDouble(component -> {
                    final double jsf = statisticsProvider
                            .getJSF(regularComponents, component, existentialComponentToFilter.get(component),
                                    pathToPreNetworkComponents);
                    return preNetworkComponentToData.get(component).getFdelete() * rowCount * (jsf / (1 - jsf));
                }), positiveExistentialComponents.stream().mapToDouble(
                        component -> preNetworkComponentToData.get(component).getFinsert() * rowCount
                                * statisticsProvider
                                .getJSF(regularComponents, component, existentialComponentToFilter.get(component),
                                        pathToPreNetworkComponents))).sum();
        // dnrating (30b)
        final double fdelete = DoubleStream
                .concat(regularComponents.stream().mapToDouble(c -> preNetworkComponentToData.get(c).getFdelete()),
                        DoubleStream.concat(negativeExistentialComponents.stream().mapToDouble(
                                component -> preNetworkComponentToData.get(component).getFdelete() * rowCount
                                        * statisticsProvider.getJSF(regularComponents, component,
                                        existentialComponentToFilter.get(component), pathToPreNetworkComponents)),
                                positiveExistentialComponents.stream().mapToDouble(component -> {
                                    final double jsf = statisticsProvider.getJSF(regularComponents, component,
                                            existentialComponentToFilter.get(component), pathToPreNetworkComponents);
                                    return preNetworkComponentToData.get(component).getFinsert() * rowCount * (jsf / (1
                                            - jsf));
                                }))).sum();
        // publish information to statistics provider
        {
            final Set<PathFilterList> filters = new HashSet<>();
            componentToJoinOrder.keySet().forEach(filters::addAll);
            filters.add(toRate);
            statisticsProvider.setData(filters, new Data(finsert, fdelete, rowCount, tupleSize));
        }
        final double mUxBeta = m(unfilteredRowCount, tuplesPerPage);
        // dnrating (40)
        final double runtimeCost = DoubleStream.concat(regularComponents.stream().mapToDouble(component -> {
            final Data data = preNetworkComponentToData.get(component);
            return data.getFinsert() * costPosInsVarII(statisticsProvider, component,
                    componentToJoinOrder.get(component), regularComponents, pathToPreNetworkComponents)
                    + data.getFdelete() * (mUxBeta + cardenas(mUxBeta, regularComponentToJoinSize.get(component)));
        }), Stream.concat(positiveExistentialComponents.stream(), negativeExistentialComponents.stream())
                .mapToDouble(component -> {
                    final Data data = preNetworkComponentToData.get(component);
                    return data.getFinsert() * 2 * jc(statisticsProvider, statisticsProvider
                            .getJSF(regularComponents, component, existentialComponentToFilter.get(component),
                                    pathToPreNetworkComponents), data, 1) + data.getFdelete() * costNegDelVarII(
                            statisticsProvider, component, componentToJoinOrder.get(component),
                            pathToPreNetworkComponents);
                })).sum();
        final double memoryCost = unfilteredRowCount * (tupleSize + 0.15 * (positiveExistentialComponents.size()
                + negativeExistentialComponents.size()));
        return cpuAndMemCostCombiner.applyAsDouble(runtimeCost, memoryCost);
    }

    private double costPosInsVarII(final StatisticsProvider statisticsProvider,
            final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Set<Set<PathFilterList>> regularComponents,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        final double[] jsfs = statisticsProvider.getAllJSFs(inputComponent, joinOrder, pathToPreNetworkComponents);
        assert jsfs.length == joinOrder.size();
        final Builder costs = DoubleStream.builder();
        double size = 1.0;
        int i = 0;
        for (final Pair<List<Set<PathFilterList>>, List<PathFilter>> pair : joinOrder) {
            final List<Set<PathFilterList>> components = pair.getLeft();
            final Set<PathFilterList> component = components.get(0);
            if (regularComponents.contains(component)) {
                if (component.size() > 1) {
                    final int lastIndex = components.size() - 1;
                    for (int ci = 0; ci < lastIndex; ++ci) {
                        costs.accept(jc(statisticsProvider, 1.0, statisticsProvider.getData(components.get(ci)), size));
                    }
                    costs.accept(jc(statisticsProvider, jsfs[i], statisticsProvider.getData(components.get(lastIndex)),
                            size));
                } else {
                    costs.accept(jc(statisticsProvider, jsfs[i], statisticsProvider.getData(component), size));
                }
                size *= statisticsProvider.getData(component).getRowCount() * jsfs[i];
            } else {
                costs.accept(jc(statisticsProvider, statisticsProvider
                                .getJSF(regularComponents, component, pair.getRight().get(0),
                                        pathToPreNetworkComponents),
                        statisticsProvider.getData(component), size));
            }
            ++i;
        }
        return costs.add(size).build().sum();
    }

    private double costNegDelVarII(final StatisticsProvider statisticsProvider,
            final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
        final double[] jsfs = statisticsProvider.getAllJSFs(inputComponent, joinOrder, pathToPreNetworkComponents);
        assert jsfs.length == joinOrder.size();
        final Builder costs = DoubleStream.builder();
        double size = 1.0;
        int i = 0;
        for (final Pair<List<Set<PathFilterList>>, List<PathFilter>> pair : joinOrder) {
            final List<Set<PathFilterList>> components = pair.getLeft();
            final Set<PathFilterList> component = components.get(0);
            if (component.size() > 1) {
                final int lastIndex = components.size() - 1;
                for (int ci = 0; ci < lastIndex; ++ci) {
                    costs.accept(jc(statisticsProvider, 1.0, statisticsProvider.getData(components.get(ci)), size));
                }
                costs.accept(
                        jc(statisticsProvider, jsfs[i], statisticsProvider.getData(components.get(lastIndex)), size));
            } else {
                costs.accept(jc(statisticsProvider, jsfs[i], statisticsProvider.getData(component), size));
            }
            size *= statisticsProvider.getData(component).getRowCount() * jsfs[i];
            ++i;
        }
        return costs.add(size).build().sum();
    }

    @Override
    public double rateNetwork(final SideEffectFunctionToNetwork network) {
        final Map<Node, Double> nodeToCost = new IdentityHashMap<>();
        final Map<Node, Set<Pair<Set<Path>, Set<PathFilterList>>>> preNetwork = new HashMap<>();
        final Set<Node> nodes = network.getTerminalNodes().stream().map(TerminalNode::getEdge).map(Edge::getSourceNode)
                .collect(toHashSet());
        final StatisticsProvider statProvider = new org.jamocha.rating.fraj.StatisticsProvider();
        for (final Node node : nodes) {
            recursiveRateNode(node, nodeToCost, preNetwork, statProvider);
        }
        final double cost = nodeToCost.values().stream().mapToDouble(Double::doubleValue).sum();
        return cost;
    }

    private void recursiveRateNode(final Node node, final Map<Node, Double> nodeToCost,
            final Map<Node, Set<Pair<Set<Path>, Set<PathFilterList>>>> preNetwork,
            final StatisticsProvider statProvider) {

        // If node costs have been calculated return pre network filters
        if (preNetwork.containsKey(node)) {
            assert nodeToCost.containsKey(node);
            return;
        }
        // Else calculate costs and then return pre network filters

        // Get the Set PathNodeFilterSets for each edge from the pre-Network by recursively calling
        // this method for every parent node
        final Edge[] incomingEdges;
        try {
            incomingEdges = node.getIncomingEdges();
        } catch (final UnsupportedOperationException e) {
            // node instanceof OTN
            preNetwork.put(node, node.getPathNodeFilterSets().stream().map(pnfs -> Pair
                    .of(Lambdas.newIdentityHashSet(PathCollector.newHashSet().collectAllInLists(pnfs).getPaths()),
                            Collections.<PathFilterList>singleton(pnfs))).collect(toSet()));
            nodeToCost.put(node, 0.0);
            return;
        }
        PathNodeFilterSet chosenPnfs = null;
        Pair<Set<Path>, Set<PathFilterList>> chosenPair = null;
        Set<Set<PathFilterList>> chosenEdgeSet = null;
        Map<Path, Set<PathFilterList>> chosenPathToComponent = null;
        for (final PathNodeFilterSet localPnfs : node.getPathNodeFilterSets()) {
            chosenEdgeSet = new HashSet<>();
            chosenPathToComponent = new HashMap<>();
            final Set<Path> localPaths =
                    Lambdas.newIdentityHashSet(PathCollector.newHashSet().collectAllInLists(localPnfs).getPaths());
            final Set<Path> resultPaths = new HashSet<Path>();
            final Set<PathFilterList> resultFilters = new HashSet<>();
            final Set<Set<PathFilterList>> preNetworkFilters = new HashSet<>();
            for (final Edge edge : incomingEdges) {
                final Node sourceNode = edge.getSourceNode();
                recursiveRateNode(sourceNode, nodeToCost, preNetwork, statProvider);
                final Set<Pair<Set<Path>, Set<PathFilterList>>> set = preNetwork.get(sourceNode);
                for (final Pair<Set<Path>, Set<PathFilterList>> pair : set) {
                    final Set<Path> paths = pair.getLeft();
                    final Set<PathFilterList> filters = pair.getRight();
                    if (!Collections.disjoint(paths, localPaths)) {
                        preNetworkFilters.add(filters);
                        resultFilters.addAll(filters);
                        resultPaths.addAll(paths);
                        chosenEdgeSet.add(filters);
                        for (final Path path : paths) {
                            chosenPathToComponent.put(path, filters);
                        }
                    }
                }
            }
            resultFilters.add(localPnfs);
            final Pair<Set<Path>, Set<PathFilterList>> localPair = Pair.of(resultPaths, resultFilters);
            preNetwork.computeIfAbsent(node, Lambdas.newIdentityHashSet()).add(localPair);
            chosenPnfs = localPnfs;
            chosenPair = localPair;
        }
        assert null != chosenPnfs;
        assert null != chosenPair;
        assert null != chosenEdgeSet;
        assert null != chosenPathToComponent;

        // Create a List of all PathFilters in this node
        final List<PathFilter> pathFilters = new ArrayList<>(chosenPnfs.getFilters());
        // Create a Map that maps each Set of PathNodeFilterSets to a singletonList with a Pair of a
        // List of all other PathNodeFilterSetLists and the PathFilter List from this Node
        final Map<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>> pathToComponents =
                new HashMap<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>>();
        for (final Set<PathFilterList> edge : chosenEdgeSet) {
            final SetView<Set<PathFilterList>> otherEdges = Sets.difference(chosenEdgeSet, Collections.singleton(edge));
            pathToComponents.put(edge, Collections.singletonList(Pair.of(new ArrayList<>(otherEdges), pathFilters)));
        }

        // Rate the node, depending on the Type of node either Alpha or Beta
        final double cost;
        if (incomingEdges.length > 1) {
            // Create a Map that maps each path to the corresponding Set of PathNodeFilterSets
            cost = rateBeta(statProvider, chosenPnfs, pathToComponents, chosenPathToComponent);
        } else if (!Objects.isNull(node.getMemory())) {
            cost = rateMaterialisedAlpha(statProvider, chosenPnfs,
                    Iterables.first(chosenEdgeSet).getOrElse(new HashSet<PathFilterList>()));
        } else {
            cost = rateVirtualAlpha(statProvider, chosenPnfs,
                    Iterables.first(chosenEdgeSet).getOrElse(new HashSet<PathFilterList>()));
        }
        nodeToCost.put(node, cost);
        // Return the Set of PathNodeFilterSet that represents the pre-network including this
        // node
    }
}
