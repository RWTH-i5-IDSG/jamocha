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
package org.jamocha.dn.compiler.ecblocks.assignmentgraph;


import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.FunctionalExpressionBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;

import java.util.Set;

/**
 * Checks whether there is a path from every occurrence node in every given functional expression binding node to a
 * direct binding (constant, slot, or fact binding).
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalExpressionBindingChecker {
    final AssignmentGraph assignmentGraph;
    final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph;

    final Set<FunctionalExpressionBindingNode> seen = Sets.newIdentityHashSet();
    final Set<FunctionalExpressionBindingNode> valid = Sets.newIdentityHashSet();

    public static boolean check(final AssignmentGraph assignmentGraph,
            final AssignmentGraph.UnrestrictedGraph.SubGraph subgraph,
            final Set<FunctionalExpressionBindingNode> nodes) {
        final FunctionalExpressionBindingChecker functionalExpressionBindingChecker =
                new FunctionalExpressionBindingChecker(assignmentGraph, subgraph);
        for (final FunctionalExpressionBindingNode node : nodes) {
            if (!functionalExpressionBindingChecker.check(node)) return false;
        }
        return true;
    }

    private boolean check(final FunctionalExpressionBindingNode node) {
        seen.add(node);
        for (final FunctionalExpressionOccurrenceNode functionalExpressionOccurrenceNode : assignmentGraph
                .getFunctionalExpressionBindingToOccurrenceNodes().get(node).values()) {
            if (!FunctionalExpressionBindingChecker.this.check(functionalExpressionOccurrenceNode)) {
                return false;
            }
        }
        valid.add(node);
        return true;
    }

    private boolean check(final FunctionalExpressionOccurrenceNode occurrenceNode) {
        final RecursiveChecker recursiveChecker = new RecursiveChecker();
        recursiveChecker.check(occurrenceNode);
        return recursiveChecker.bound;
    }

    class RecursiveChecker {
        boolean bound = false;

        void check(final FunctionalExpressionOccurrenceNode occurrenceNode) {
            final Set<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> edges =
                    subgraph.outgoingEdgesOf(occurrenceNode);
            for (final AssignmentGraph.Edge<ECOccurrenceNode, BindingNode> edge : edges) {
                final BindingNode target = edge.getTarget();
                switch (target.getNodeType()) {
                case FUNCTIONAL_EXPRESSION:
                    final FunctionalExpressionBindingNode node = (FunctionalExpressionBindingNode) target;
                    if (valid.contains(node)) {
                        bound = true;
                        return;
                    }
                    if (seen.contains(node)) continue;
                    if (!FunctionalExpressionBindingChecker.this.check(node)) continue;
                    // FALL THROUGH
                case SLOT_OR_FACT_BINDING:
                case CONSTANT_EXPRESSION:
                    bound = true;
                    return;
                }
            }
        }
    }
}
