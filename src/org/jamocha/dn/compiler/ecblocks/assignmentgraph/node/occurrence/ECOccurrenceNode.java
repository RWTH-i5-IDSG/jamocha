/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.ECOccurrence;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.AssignmentGraphNode;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public abstract class ECOccurrenceNode implements AssignmentGraphNode<OccurrenceType> {
	final ECOccurrence occurrence;
}