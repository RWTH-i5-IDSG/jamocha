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

import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.column.Column;
import org.jamocha.dn.compiler.ecblocks.partition.BindingPartition;
import org.jamocha.dn.compiler.ecblocks.partition.OccurrencePartition;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface BlockInterface {
    AssignmentGraph getGraph();

    Block.RowContainer getRowContainer();

    Set<Column<ECOccurrenceNode, BindingNode>> getColumns();

    Set<SingleFactVariable> getFactVariablesUsed();

    BindingPartition getBindingPartition();

    OccurrencePartition getOccurrencePartition();

    int getNumberOfRows();

    int getNumberOfColumns();
}
