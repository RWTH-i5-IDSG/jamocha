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
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingType;
import org.jamocha.dn.compiler.ecblocks.partition.BindingPartition.BindingSubSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class BindingPartition extends InformedPartition<BindingNode, BindingType, BindingSubSet, BindingPartition> {
    public static class BindingSubSet extends InformedPartition.InformedSubSet<BindingNode, BindingType> {
        public BindingSubSet(final Map<RowIdentifier, BindingNode> elements, final BindingType info) {
            super(elements, info);
        }

        public BindingSubSet(final Map<RowIdentifier, BindingNode> elements) {
            this(elements, elements.values().iterator().next().getNodeType());
        }

        public BindingSubSet(final BindingSubSet copy) {
            super(copy);
        }
    }

    public BindingPartition(final BindingPartition copy) {
        super(copy, BindingSubSet::new);
    }

    public BindingPartition(final Set<BindingSubSet> subSets, final Map<BindingNode, BindingSubSet> lookup,
            final Map<BindingType, Set<BindingSubSet>> informedLookup) {
        super(subSets, lookup, informedLookup);
    }

    @Override
    public BindingPartition add(final BindingSubSet newSubSet) {
        return super.informedAdd(newSubSet,
                (subsets, lookup) -> infLookup -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition extend(final RowIdentifier row,
            final IdentityHashMap<BindingSubSet, BindingNode> extension) {
        return super.informedExtend(row, extension, (oldss, map) -> info -> new BindingSubSet(map, info),
                (subsets, lookup) -> infLookup -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition remove(final RowIdentifier row) {
        return super.informedRemove(row, (oldss, map) -> info -> new BindingSubSet(map, info),
                (subsets, lookup) -> infLookup -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition remove(final BindingSubSet oldSubSet) {
        return super.informedAdd(oldSubSet,
                (subsets, lookup) -> infLookup -> new BindingPartition(subsets, lookup, infLookup));
    }
}
