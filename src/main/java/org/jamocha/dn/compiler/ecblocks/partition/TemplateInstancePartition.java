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
import org.jamocha.dn.compiler.ecblocks.partition.TemplateInstancePartition.TemplateInstanceSubSet;
import org.jamocha.dn.memory.Template;
import org.jamocha.languages.common.SingleFactVariable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class TemplateInstancePartition
        extends InformedPartition<SingleFactVariable, Template, TemplateInstanceSubSet, TemplateInstancePartition> {
    public static class TemplateInstanceSubSet extends InformedPartition.InformedSubSet<SingleFactVariable, Template> {
        public TemplateInstanceSubSet(final TemplateInstanceSubSet copy) {
            super(copy);
        }

        public TemplateInstanceSubSet(final IdentityHashMap<RowIdentifier, SingleFactVariable> elements,
                final Template info) {
            super(elements, info);
        }

        public TemplateInstanceSubSet(final Map<RowIdentifier, ? extends SingleFactVariable> elements,
                final Template info) {
            super(elements, info);
        }
    }

    public TemplateInstancePartition(final TemplateInstancePartition copy) {
        super(copy, TemplateInstanceSubSet::new);
    }

    public TemplateInstancePartition(final Set<TemplateInstanceSubSet> subSets,
            final Map<SingleFactVariable, TemplateInstanceSubSet> lookup,
            final Map<Template, Set<TemplateInstanceSubSet>> informedLookup) {
        super(subSets, lookup, informedLookup);
    }

    @Override
    public TemplateInstancePartition add(final TemplateInstanceSubSet newSubSet) {
        return super.informedAdd(newSubSet,
                (set, map) -> informedLookup -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition extend(final RowIdentifier row,
            final IdentityHashMap<TemplateInstanceSubSet, SingleFactVariable> extension) {
        return super.informedExtend(row, extension, (oldss, map) -> info -> new TemplateInstanceSubSet(map, info),
                (set, map) -> informedLookup -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition remove(final RowIdentifier row) {
        return super.informedRemove(row, (oldss, map) -> info -> new TemplateInstanceSubSet(map, info),
                (set, map) -> informedLookup -> new TemplateInstancePartition(set, map, informedLookup));
    }

    @Override
    public TemplateInstancePartition remove(final TemplateInstanceSubSet templateInstanceSubSet) {
        return super.informedRemove(templateInstanceSubSet,
                (set, map) -> informedLookup -> new TemplateInstancePartition(set, map, informedLookup));
    }
}
