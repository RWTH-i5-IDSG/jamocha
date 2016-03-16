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

package org.jamocha.dn.compiler.ecblocks;

import com.google.common.collect.Sets;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.column.Column;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IncompleteBlock implements BlockInterface {
    final BlockInterface block;
    final Set<ECOccurrence> unboundOccurrences = new HashSet<>();

    public IncompleteBlock(final BlockInterface block) {
        this.block = block;
    }

    private IncompleteBlock extendByBindings(
            final List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edgeCombination) {

    }

    Set<IncompleteBlock> extendByBindings(final Column<ECOccurrenceNode, BindingNode> column) {
        final Set<BindingNode> containedBindings = null; // block.getGraph().getDirectBindingNodes()
        final Set<ECOccurrenceNode> containedOccurrences = null; // block.getGraph().getDirectBindingNodes()
        assert containedBindings != null;
        assert containedOccurrences != null;

        final Set<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> relevantEdges =
                column.getEdges().stream().filter(e -> containedBindings.contains(e.getTarget())).collect(toSet());
        final Map<Boolean, List<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> partition =
                relevantEdges.stream().collect(partitioningBy(e -> containedOccurrences.contains(e.getSource())));

        final List<Set<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> edgesByBindingNodeOccContained =
                partition.get(true).stream().collect(
                        collectingAndThen(groupingBy(AssignmentGraph.Edge::getTarget, toSet()),
                                map -> new ArrayList<>(map.values())));
        final List<Set<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>>> edgesByBindingNodeOccNotContained =
                partition.get(false).stream().collect(
                        collectingAndThen(groupingBy(AssignmentGraph.Edge::getTarget, toSet()),
                                map -> new ArrayList<>(map.values())));
        return Stream.concat(
                Sets.cartesianProduct(edgesByBindingNodeOccContained).stream().map(  ),
                Sets.cartesianProduct(edgesByBindingNodeOccNotContained).stream().map(  ))
            .filter(Objects::nonNull).collect(toSet());
    }


    Set<IncompleteBlock> extendByOccurences(final Column<ECOccurrenceNode, BindingNode> column) {

    }

    Block finish() {
        if (this.unboundOccurrences.size() > 0) throw new Error();
        return new Block(this);
    }

    @Override
    public int getNumberOfRows() {
        return this.block.getNumberOfRows();
    }

    @Override
    public int getNumberOfColumns() {
        return this.block.getNumberOfColumns();
    }

    @Override
    public AssignmentGraph getGraph() {
        return this.block.getGraph();
    }

    @Override
    public Block.BlockRows getRows() {
        return this.block.getRows();
    }

    @Override
    public Set<Column<ECOccurrenceNode, BindingNode>> getColumns() {
        return this.block.getColumns();
    }

    @Override
    public Set<SingleFactVariable> getFactVariablesUsed() {
        return this.block.getFactVariablesUsed();
    }

    @Override
    public TemplateInstancePartition getTemplateInstancePartition() {
        return this.block.getTemplateInstancePartition();
    }

    @Override
    public FilterPartition getFilterPartition() {
        return this.block.getFilterPartition();
    }
}
