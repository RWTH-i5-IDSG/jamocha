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

package org.jamocha.dn.compiler.ecblocks.partition;

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.RowIdentifier;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.OccurrenceType;
import org.jamocha.dn.compiler.ecblocks.partition.OccurrencePartition.OccurrenceSubSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class OccurrencePartition
        extends InformedPartition<ECOccurrenceNode, OccurrenceType, OccurrenceSubSet, OccurrencePartition> {
    public static class OccurrenceSubSet extends
            InformedPartition.InformedSubSet<ECOccurrenceNode, OccurrenceType, OccurrencePartition.OccurrenceSubSet> {
        public OccurrenceSubSet(final Map<RowIdentifier, ECOccurrenceNode> elements, final OccurrenceType info) {
            super(elements, info);
        }

        public OccurrenceSubSet(final Map<RowIdentifier, ECOccurrenceNode> elements) {
            this(elements, elements.values().iterator().next().getNodeType());
        }

        public OccurrenceSubSet(final OccurrenceSubSet copy) {
            super(copy);
        }

        @Override
        public OccurrenceSubSet add(final RowIdentifier key, final ECOccurrenceNode value) {
            return super.informedAdd(key, value, info -> elements -> new OccurrenceSubSet(elements, info));
        }

        @Override
        public OccurrenceSubSet remove(final RowIdentifier key) {
            return super.informedRemove(key, info -> elements -> new OccurrenceSubSet(elements, info));
        }
    }

    public OccurrencePartition(final OccurrencePartition copy) {
        super(copy, OccurrenceSubSet::new);
    }

    public OccurrencePartition(final Set<OccurrenceSubSet> subSets,
            final Map<ECOccurrenceNode, OccurrenceSubSet> lookup,
            final Map<OccurrenceType, Set<OccurrenceSubSet>> informedLookup) {
        super(subSets, lookup, informedLookup);
    }

    @Override
    public OccurrencePartition add(final OccurrenceSubSet newSubSet) {
        return super.informedAdd(newSubSet,
                informedLookup -> (set, map) -> new OccurrencePartition(set, map, informedLookup));
    }

    @Override
    public OccurrencePartition extend(final RowIdentifier row,
            final IdentityHashMap<OccurrenceSubSet, ECOccurrenceNode> extension) {
        return super.informedExtend(row, extension,
                informedLookup -> (set, map) -> new OccurrencePartition(set, map, informedLookup));
    }

    @Override
    public OccurrencePartition remove(final RowIdentifier row) {
        return super
                .informedRemove(row, informedLookup -> (set, map) -> new OccurrencePartition(set, map, informedLookup));
    }

    @Override
    public OccurrencePartition remove(final OccurrenceSubSet subSet) {
        return super.informedRemove(subSet,
                informedLookup -> (set, map) -> new OccurrencePartition(set, map, informedLookup));
    }
}
