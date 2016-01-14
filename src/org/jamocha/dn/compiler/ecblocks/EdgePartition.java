/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.compiler.ecblocks;

import java.util.IdentityHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.OccurrenceToBindingEdge;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class EdgePartition extends Partition<OccurrenceToBindingEdge, EdgePartition.EdgeSubSet> {
	@Getter
	public static class EdgeSubSet extends Partition.SubSet<OccurrenceToBindingEdge> {
		public EdgeSubSet(final IdentityHashMap<RowIdentifier, OccurrenceToBindingEdge> elements) {
			super(elements);
		}

		public EdgeSubSet(final Map<RowIdentifier, ? extends OccurrenceToBindingEdge> elements) {
			this(new IdentityHashMap<>(elements));
		}

		public EdgeSubSet(final EdgeSubSet copy) {
			super(copy);
		}

		public boolean contains(final EdgePartition.EdgeSubSet other) {
			return this.elements.entrySet().containsAll(other.elements.entrySet());
		}
	}

	public EdgePartition(final EdgePartition copy) {
		super(copy, EdgeSubSet::new);
	}
}