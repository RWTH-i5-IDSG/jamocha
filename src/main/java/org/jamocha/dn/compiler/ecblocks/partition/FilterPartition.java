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
import org.jamocha.dn.compiler.ecblocks.ExistentialInfo;
import org.jamocha.dn.compiler.ecblocks.RowIdentifier;
import org.jamocha.dn.compiler.ecblocks.partition.FilterPartition.FilterSubSet;
import org.jamocha.filter.ECFilter;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class FilterPartition extends
        InformedPartition<ECFilter, ExistentialInfo.FunctionWithExistentialInfo, FilterSubSet, FilterPartition> {
    public static class FilterSubSet
            extends InformedPartition.InformedSubSet<ECFilter, ExistentialInfo.FunctionWithExistentialInfo> {
        public FilterSubSet(final Map<RowIdentifier, ECFilter> elements,
                final ExistentialInfo.FunctionWithExistentialInfo info) {
            super(elements, info);
        }

        public FilterSubSet(final FilterSubSet copy) {
            super(copy);
        }
    }

    public FilterPartition(final FilterPartition copy) {
        super(copy, FilterSubSet::new);
    }

    public FilterPartition(final Set<FilterSubSet> subSets, final Map<ECFilter, FilterSubSet> lookup,
            final Map<ExistentialInfo.FunctionWithExistentialInfo, Set<FilterSubSet>> informedLookup) {
        super(subSets, lookup, informedLookup);
    }

    @Override
    public FilterPartition add(final FilterSubSet newSubSet) {
        return super
                .informedAdd(newSubSet, (set, map) -> informedLookup -> new FilterPartition(set, map, informedLookup));
    }

    @Override
    public FilterPartition extend(final RowIdentifier row, final IdentityHashMap<FilterSubSet, ECFilter> extension) {
        return super.informedExtend(row, extension, (oldss, map) -> info -> new FilterSubSet(map, info),
                (set, map) -> informedLookup -> new FilterPartition(set, map, informedLookup));
    }

    @Override
    public FilterPartition remove(final RowIdentifier row) {
        return super.informedRemove(row, (oldss, map) -> info -> new FilterSubSet(map, info),
                (set, map) -> informedLookup -> new FilterPartition(set, map, informedLookup));
    }

    @Override
    public FilterPartition remove(final FilterSubSet filterSubSet) {
        return super.informedRemove(filterSubSet,
                (set, map) -> informedLookup -> new FilterPartition(set, map, informedLookup));
    }
}
