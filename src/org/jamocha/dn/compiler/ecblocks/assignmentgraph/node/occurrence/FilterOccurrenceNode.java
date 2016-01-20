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

package org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence;

import lombok.Getter;
import org.jamocha.dn.compiler.ecblocks.ECOccurrence;
import org.jamocha.dn.compiler.ecblocks.ExistentialInfo;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraphNodeVisitor;
import org.jamocha.filter.ECFilter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class FilterOccurrenceNode extends ECOccurrenceNode {
	final ExistentialInfo.FunctionWithExistentialInfo functionWithExistentialInfo;
	final ECFilter filter;
	final int parameterPosition;

	public FilterOccurrenceNode(final ECOccurrence occurrence,
			final ExistentialInfo.FunctionWithExistentialInfo functionWithExistentialInfo, final ECFilter filter,
			final int parameterPosition) {
		super(occurrence);
		this.functionWithExistentialInfo = functionWithExistentialInfo;
		this.filter = filter;
		this.parameterPosition = parameterPosition;
	}

	@Override
	public OccurrenceType getNodeType() {
		return OccurrenceType.FILTER_OCCURRENCE;
	}

	@Override
	public <V extends AssignmentGraphNodeVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}
}
