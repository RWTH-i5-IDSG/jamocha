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
 * the specific language governing permissions and limitations under
 * the License.
 */

package org.jamocha.dn.compiler.ecblocks.column;

import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.FunctionalExpressionBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;

import java.util.Set;

import static org.jamocha.util.Lambdas.newIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FunctionalExpressionToFunctionalExpressionColumn
		extends AbstractColumn<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>
		implements FromFunctionalExpressionColumn<FunctionalExpressionBindingNode>,
		ToFunctionalExpressionColumn<FunctionalExpressionOccurrenceNode> {
	public FunctionalExpressionToFunctionalExpressionColumn(
			final Set<AssignmentGraph.Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>>
					edges) {
		super(edges);
	}

	@Override
	public FunctionalExpressionToFunctionalExpressionColumn copy() {
		return new FunctionalExpressionToFunctionalExpressionColumn(newIdentityHashSet(this.edges));
	}

	@Override
	public <V extends ColumnVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}
}