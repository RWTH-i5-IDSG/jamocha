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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;

import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class BindingPartition
        extends InformedPartition<BindingNode, BindingType, BindingPartition.BindingSubSet, BindingPartition> {
    public static class BindingSubSet
            extends InformedPartition.InformedSubSet<BindingNode, BindingType, BindingSubSet> {
        public BindingSubSet(final SimpleImmutableMinimalMap<RowIdentifier, BindingNode> elements,
                final BindingType info) {
            super(elements, info);
        }

        public BindingSubSet(final InformedSubSet<BindingNode, BindingType, BindingSubSet> copy) {
            super(copy);
        }

        public BindingSubSet(final SimpleImmutableMinimalMap<RowIdentifier, BindingNode> elements) {
            this(elements, elements.entrySet().iterator().next().getValue().getNodeType());
        }

        @Override
        public BindingSubSet add(final RowIdentifier key, final BindingNode value) {
            return super.informedAdd(key, value, info -> elements -> new BindingSubSet(elements, info));
        }

        @Override
        public BindingSubSet remove(final RowIdentifier key) {
            return super.informedRemove(key, info -> elements -> new BindingSubSet(elements, info));
        }

        @Override
        public BindingSubSet remove(final Set<RowIdentifier> keys) {
            return super.informedRemove(keys, info -> elements -> new BindingSubSet(elements, info));
        }
    }

    public BindingPartition(final BindingPartition copy) {
        super(copy, BindingSubSet::new);
    }

    public BindingPartition(final ImmutableMinimalSet<BindingSubSet> subSets,
            final SimpleImmutableMinimalMap<BindingNode, BindingSubSet> lookup,
            final SimpleImmutableMinimalMap<BindingType, ImmutableMinimalSet<BindingSubSet>> informedLookup) {
        super(subSets, lookup, informedLookup);
    }

    @Override
    protected BindingPartition getCorrectlyTypedThis() {
        return this;
    }

    @Override
    public BindingPartition add(final BindingSubSet newSubSet) {
        return super.informedAdd(newSubSet,
                infLookup -> (subsets, lookup) -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition extend(final RowIdentifier row,
            final SimpleImmutableMinimalMap<BindingSubSet, BindingNode> extension) {
        return super.informedExtend(row, extension,
                infLookup -> (subsets, lookup) -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition remove(final RowIdentifier row) {
        return super.informedRemove(row,
                infLookup -> (subsets, lookup) -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition remove(final BindingSubSet oldSubSet) {
        return super.informedAdd(oldSubSet,
                infLookup -> (subsets, lookup) -> new BindingPartition(subsets, lookup, infLookup));
    }

    @Override
    public BindingPartition remove(final Set<RowIdentifier> rows) {
        return super.informedRemove(rows,
                infLookup -> (subsets, lookup) -> new BindingPartition(subsets, lookup, infLookup));
    }
}
