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


import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.ConstantBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.FactBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.FunctionalExpressionBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.SlotBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FilterOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ImplicitOccurrenceNode;
import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface AssignmentGraphNodeVisitor extends Visitor {
    void visit(final ConstantBindingNode node);

    void visit(final FactBindingNode node);

    void visit(final FunctionalExpressionBindingNode node);

    void visit(final SlotBindingNode node);

    void visit(final FilterOccurrenceNode node);

    void visit(final FunctionalExpressionOccurrenceNode node);

    void visit(final ImplicitOccurrenceNode node);
}
