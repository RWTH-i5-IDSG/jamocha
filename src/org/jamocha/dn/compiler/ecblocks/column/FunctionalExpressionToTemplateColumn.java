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
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.SlotOrFactBindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;

import java.util.Set;

import static org.jamocha.util.Lambdas.newIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FunctionalExpressionToTemplateColumn
		extends AbstractColumn<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>
		implements FromFunctionalExpressionColumn, ToTemplateColumn {
	public FunctionalExpressionToTemplateColumn(
			final Set<AssignmentGraph.Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>> edges) {
		super(edges);
	}

	@Override
	public FunctionalExpressionToTemplateColumn copy() {
		return new FunctionalExpressionToTemplateColumn(newIdentityHashSet(this.edges));
	}
}
