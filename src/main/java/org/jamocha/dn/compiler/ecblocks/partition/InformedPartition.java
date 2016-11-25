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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalSetWrapper;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.SimpleMinimalIdentityHashMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.MapToSetReducer;
import org.jamocha.dn.compiler.ecblocks.partition.InformedPartition.InformedSubSet;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class InformedPartition<T, I, S extends InformedSubSet<T, I, S>, P extends InformedPartition<T, I, S,
        P>>
        extends Partition<T, S, P> {
    abstract static class InformedSubSet<T, I, S extends InformedSubSet<T, I, S>> extends Partition.SubSet<T, S> {
        @Getter
        protected final I info;

        InformedSubSet(final SimpleImmutableMinimalMap<RowIdentifier, T> elements, final I info) {
            super(elements);
            this.info = info;
        }

        InformedSubSet(final InformedSubSet<T, I, S> copy) {
            super(copy);
            this.info = copy.info;
        }

        protected S informedAdd(final RowIdentifier key, final T value,
                final Function<I, Function<SimpleImmutableMinimalMap<RowIdentifier, T>, S>> ctor) {
            return super.add(key, value, ctor.apply(this.info));
        }

        protected S informedRemove(final RowIdentifier key,
                final Function<I, Function<SimpleImmutableMinimalMap<RowIdentifier, T>, S>> ctor) {
            return super.remove(key, ctor.apply(this.info));
        }

        protected S informedRemove(final Set<RowIdentifier> keys,
                final Function<I, Function<SimpleImmutableMinimalMap<RowIdentifier, T>, S>> ctor) {
            return super.remove(keys, ctor.apply(this.info));
        }
    }

    protected final SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>> informedLookup;

    public InformedPartition() {
        super();
        this.informedLookup = new SimpleMinimalIdentityHashMap<>();
    }

    public InformedPartition(final ImmutableMinimalSet<S> subSets, final SimpleImmutableMinimalMap<T, S> lookup,
            final SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>> informedLookup) {
        super(subSets, lookup);
        this.informedLookup = informedLookup;
    }

    public InformedPartition(final InformedPartition<T, I, S, P> copy,
            final Function<? super S, ? extends S> copyCtor) {
        super(copy, copyCtor);
        final Map<I, ImmutableMinimalSet<S>> mapOfPendants = copy.informedLookup.entrySet().stream().collect(Collectors
                .toMap(Map.Entry::getKey, i -> i.getValue().stream()
                        .map(s -> this.lookup.get(s.elements.entrySet().iterator().next().getValue()))
                        .collect(toCollection(MinimalSetWrapper::newIdentityHashSet))));
        this.informedLookup = new SimpleMinimalIdentityHashMap<>(mapOfPendants);
    }

    public ImmutableMinimalSet<S> lookupInformed(final I key) {
        return this.informedLookup.get(key);
    }

    protected P informedAdd(final S newSubSet,
            final Function<SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>>, BiFunction<ImmutableMinimalSet<S>,
                    SimpleImmutableMinimalMap<T, S>, P>> ctor) {
        return super.add(newSubSet, ctor.apply(MapToSetExtender.with(this.informedLookup, newSubSet.info, newSubSet)));
    }

    protected P informedExtend(final RowIdentifier row, final SimpleImmutableMinimalMap<S, T> extension,
            final Function<SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>>, BiFunction<ImmutableMinimalSet<S>,
                    SimpleImmutableMinimalMap<T, S>, P>> ctor) {
        return super.extend(row, extension, ctor.apply(this.informedLookup));
    }

    protected P informedRemove(final RowIdentifier row,
            final Function<SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>>, BiFunction<ImmutableMinimalSet<S>,
                    SimpleImmutableMinimalMap<T, S>, P>> ctor) {
        return super.remove(row, ctor.apply(this.informedLookup));
    }

    protected P informedRemove(final S s,
            final Function<SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>>, BiFunction<ImmutableMinimalSet<S>,
                    SimpleImmutableMinimalMap<T, S>, P>> ctor) {
        return super.remove(s, ctor.apply(MapToSetReducer.without(this.informedLookup, s.info, s)));
    }

    protected P informedRemove(final Set<RowIdentifier> rows,
            final Function<SimpleImmutableMinimalMap<I, ImmutableMinimalSet<S>>, BiFunction<ImmutableMinimalSet<S>,
                    SimpleImmutableMinimalMap<T, S>, P>> ctor) {
        return super.remove(rows, ctor.apply(this.informedLookup));
    }
}
