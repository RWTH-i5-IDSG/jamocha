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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.partition.TemplateInstancePartition.TemplateInstanceSubSet;
import org.jamocha.dn.memory.Template;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class TemplateInstancePartition
        extends InformedPartition<SingleFactVariable, Template, TemplateInstanceSubSet, TemplateInstancePartition> {
    public static class TemplateInstanceSubSet
            extends InformedPartition.InformedSubSet<SingleFactVariable, Template, TemplateInstanceSubSet> {
        public TemplateInstanceSubSet(final SimpleImmutableMinimalMap<RowIdentifier, SingleFactVariable> elements,
                final Template info) {
            super(elements, info);
        }

        public TemplateInstanceSubSet(final SimpleImmutableMinimalMap<RowIdentifier, SingleFactVariable> elements) {
            this(elements, elements.entrySet().iterator().next().getValue().getTemplate());
        }

        public TemplateInstanceSubSet(final TemplateInstanceSubSet copy) {
            super(copy);
        }

        @Override
        public TemplateInstanceSubSet add(final RowIdentifier key, final SingleFactVariable value) {
            return super.informedAdd(key, value, info -> elements -> new TemplateInstanceSubSet(elements, info));
        }

        @Override
        public TemplateInstanceSubSet remove(final RowIdentifier key) {
            return super.informedRemove(key, info -> elements -> new TemplateInstanceSubSet(elements, info));
        }

        @Override
        public TemplateInstanceSubSet remove(final Set<RowIdentifier> keys) {
            return super.informedRemove(keys, info -> elements -> new TemplateInstanceSubSet(elements, info));
        }
    }

    public TemplateInstancePartition(final TemplateInstancePartition copy) {
        super(copy, TemplateInstanceSubSet::new);
    }

    public TemplateInstancePartition(final ImmutableMinimalSet<TemplateInstanceSubSet> subSets,
            final SimpleImmutableMinimalMap<SingleFactVariable, TemplateInstanceSubSet> lookup,
            final SimpleImmutableMinimalMap<Template, ImmutableMinimalSet<TemplateInstanceSubSet>> informedLookup) {
        super(subSets, lookup, informedLookup);
    }

    @Override
    protected TemplateInstancePartition getCorrectlyTypedThis() {
        return this;
    }

    @Override
    public TemplateInstancePartition add(final TemplateInstanceSubSet newSubSet) {
        return super.informedAdd(newSubSet,
                informedLookup -> (set, map) -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition extend(final RowIdentifier row,
            final SimpleImmutableMinimalMap<TemplateInstanceSubSet, SingleFactVariable> extension) {
        return super.informedExtend(row, extension,
                informedLookup -> (set, map) -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition remove(final RowIdentifier row) {
        return super.informedRemove(row,
                informedLookup -> (set, map) -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition remove(final TemplateInstanceSubSet templateInstanceSubSet) {
        return super.informedRemove(templateInstanceSubSet,
                informedLookup -> (set, map) -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition remove(final Set<RowIdentifier> rows) {
        return super.informedRemove(rows,
                informedLookup -> (set, map) -> new TemplateInstancePartition(set, map, informedLookup));
    }
}
