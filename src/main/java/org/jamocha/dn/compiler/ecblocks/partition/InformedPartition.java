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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.RowIdentifier;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.MapToSetExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.MapToSetReducer;
import org.jamocha.dn.compiler.ecblocks.partition.InformedPartition.InformedSubSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class InformedPartition<T, I, S extends InformedSubSet<T, I>, P extends InformedPartition<T, I, S, P>>
        extends Partition<T, S, P> {
    public static class InformedSubSet<T, I> extends Partition.SubSet<T> {
        @Getter
        protected final I info;

        public InformedSubSet(final Map<RowIdentifier, T> elements, final I info) {
            super(elements);
            this.info = info;
        }

        public InformedSubSet(final InformedSubSet<T, I> copy) {
            super(copy);
            this.info = copy.info;
        }
    }

    protected final Map<I, Set<S>> informedLookup;

    public InformedPartition() {
        super();
        this.informedLookup = new IdentityHashMap<>();
    }

    public InformedPartition(final Set<S> subSets, final Map<T, S> lookup, final Map<I, Set<S>> informedLookup) {
        super(subSets, lookup);
        this.informedLookup = informedLookup;
    }

    public InformedPartition(final InformedPartition<T, I, S, P> copy, final Function<S, S> copyCtor) {
        super(copy, copyCtor);
        this.informedLookup = new IdentityHashMap<>(copy.informedLookup);
    }

    public Set<S> lookupInformed(final I key) {
        return this.informedLookup.get(key);
    }

    protected P informedAdd(final S newSubSet,
            final BiFunction<Set<S>, Map<T, S>, Function<Map<I, Set<S>>, P>> partitionCtor) {
        return super.add(newSubSet, (set, map) -> partitionCtor.apply(set, map)
                .apply(MapToSetExtender.with(this.informedLookup, newSubSet.info, newSubSet)));
    }

    protected P informedExtend(final RowIdentifier row, final IdentityHashMap<S, T> extension,
            final BiFunction<S, Map<RowIdentifier, T>, Function<I, S>> subsetCtor,
            final BiFunction<Set<S>, Map<T, S>, Function<Map<I, Set<S>>, P>> partitionCtor) {
        return super.extend(row, extension, (oldss, map) -> subsetCtor.apply(oldss, map).apply(oldss.info),
                (set, map) -> partitionCtor.apply(set, map).apply(this.informedLookup));
    }

    protected P informedRemove(final RowIdentifier row,
            final BiFunction<S, Map<RowIdentifier, T>, Function<I, S>> subsetCtor,
            final BiFunction<Set<S>, Map<T, S>, Function<Map<I, Set<S>>, P>> partitionCtor) {
        return super.remove(row, (oldss, map) -> subsetCtor.apply(oldss, map).apply(oldss.info),
                (set, map) -> partitionCtor.apply(set, map).apply(this.informedLookup));
    }

    protected P informedRemove(final S s,
            final BiFunction<Set<S>, Map<T, S>, Function<Map<I, Set<S>>, P>> partitionCtor) {
        return super.remove(s, (set, map) -> partitionCtor.apply(set, map)
                .apply(MapToSetReducer.without(this.informedLookup, s.info, s)));
    }
}
