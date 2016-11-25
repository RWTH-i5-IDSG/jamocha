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

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.RowIdentifier;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.IdentityMapExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.MapCombiner;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.SetExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalSetWrapper;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.SimpleMinimalHashMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.SimpleMinimalIdentityHashMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.IdentityMapReducer;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.MapSubtractor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.SetReducer;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString(of = {"subSets"})
public abstract class Partition<T, S extends Partition.SubSet<T, S>, P extends Partition<T, S, P>> {
    @RequiredArgsConstructor
    @Getter
    @ToString
    public abstract static class SubSet<T, S extends SubSet<T, S>> {
        protected final SimpleImmutableMinimalMap<RowIdentifier, T> elements;

        SubSet(final SubSet<T, S> copy) {
            this(new SimpleMinimalIdentityHashMap<>(copy.elements));
        }

        public T get(final RowIdentifier rule) {
            return this.elements.get(rule);
        }

        public abstract S add(final RowIdentifier key, final T value);

        protected S add(final RowIdentifier key, final T value,
                final Function<SimpleImmutableMinimalMap<RowIdentifier, T>, S> subsetCtor) {
            return subsetCtor.apply(IdentityMapExtender.with(this.elements, key, value));
        }

        public abstract S remove(final RowIdentifier key);

        protected S remove(final RowIdentifier key,
                final Function<SimpleImmutableMinimalMap<RowIdentifier, T>, S> subsetCtor) {
            return subsetCtor.apply(IdentityMapReducer.without(this.elements, key));
        }

        public abstract S remove(final Set<RowIdentifier> keys);

        protected S remove(final Set<RowIdentifier> keys,
                final Function<SimpleImmutableMinimalMap<RowIdentifier, T>, S> subsetCtor) {
            return subsetCtor.apply(MapSubtractor
                    .without(this.elements, new SimpleMinimalIdentityHashMap<>(Maps.asMap(keys, this.elements::get))));
        }
    }

    protected final ImmutableMinimalSet<S> subSets;
    protected final SimpleImmutableMinimalMap<T, S> lookup;

    public Partition() {
        this(MinimalSetWrapper.newIdentityHashSet(), new SimpleMinimalHashMap<>());
    }

    public Partition(final Partition<T, S, P> copy, final Function<? super S, ? extends S> copyCtor) {
        this.subSets = copy.subSets.stream().map(copyCtor).collect(toCollection(MinimalSetWrapper::newIdentityHashSet));
        this.lookup = new SimpleMinimalHashMap<>(
                this.subSets.stream().flatMap(s -> s.elements.entrySet().stream().map(t -> Pair.of(t.getValue(), s)))
                        .collect(toMap(Pair::getKey, Pair::getValue)));
    }

    protected abstract P getCorrectlyTypedThis();

    public abstract P add(final S newSubSet);

    protected P add(final S newSubSet,
            final BiFunction<ImmutableMinimalSet<S>, SimpleImmutableMinimalMap<T, S>, P> ctor) {
        assert newSubSet.elements.entrySet().stream().map(Map.Entry::getValue).noneMatch(Objects::isNull);
        assert this.subSets.stream().allMatch(ss -> ss.getElements().keySet().equals(newSubSet.elements.keySet()));
        assert this.subSets.stream().allMatch(ss -> ss.elements.entrySet().stream().map(Map.Entry::getValue)
                .noneMatch(v -> newSubSet.elements.entrySet().stream().anyMatch(nv -> Objects.equals(v, nv))));
        final ImmutableMinimalSet<S> subsets = SetExtender.with(this.subSets, newSubSet);
        final SimpleMinimalHashMap<T, S> added = new SimpleMinimalHashMap<>(
                newSubSet.elements.entrySet().stream().collect(toMap(Map.Entry::getValue, e -> newSubSet)));
        final MapCombiner<T, S> lookup = MapCombiner.with(this.lookup, added);
        return ctor.apply(subsets, lookup);
    }

    public abstract P extend(final RowIdentifier row, final SimpleImmutableMinimalMap<S, T> extension);

    protected P extend(final RowIdentifier row, final SimpleImmutableMinimalMap<S, T> extension,
            final BiFunction<ImmutableMinimalSet<S>, SimpleImmutableMinimalMap<T, S>, P> ctor) {
        // assert that no value in the extension is null
        assert extension.entrySet().stream().map(Map.Entry::getValue).noneMatch(Objects::isNull);
        // assert that all subsets are extended by the extension
        assert this.subSets.stream().allMatch(extension::containsKey);
        final ImmutableMinimalSet<S> subsets =
                this.subSets.stream().map(subset -> subset.add(row, extension.get(subset)))
                        .collect(toCollection(MinimalSetWrapper::newIdentityHashSet));
        final SimpleImmutableMinimalMap<T, S> inverted = invert(extension);
        final SimpleImmutableMinimalMap<T, S> lookup = MapCombiner.with(this.lookup, inverted);
        return ctor.apply(subsets, lookup);
    }

    protected static <K, V> SimpleImmutableMinimalMap<K, V> invert(final SimpleImmutableMinimalMap<V, K> map) {
        return new SimpleMinimalHashMap<>(
                map.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    public abstract P remove(final RowIdentifier row);

    protected P remove(final RowIdentifier row,
            final BiFunction<ImmutableMinimalSet<S>, SimpleImmutableMinimalMap<T, S>, P> ctor) {
        assert this.subSets.stream().allMatch(subset -> subset.elements.containsKey(row));
        final ImmutableMinimalSet<S> subsets = this.subSets.stream().map(subset -> subset.remove(row))
                .collect(toCollection(MinimalSetWrapper::newIdentityHashSet));
        final SimpleImmutableMinimalMap<T, S> subtract = new SimpleMinimalHashMap<>(
                this.subSets.stream().collect(toMap(subset -> subset.get(row), Function.identity())));
        final SimpleImmutableMinimalMap<T, S> lookup = MapSubtractor.without(this.lookup, subtract);
        return ctor.apply(subsets, lookup);
    }

    public abstract P remove(final S s);

    protected P remove(final S s, final BiFunction<ImmutableMinimalSet<S>, SimpleImmutableMinimalMap<T, S>, P> ctor) {
        assert this.subSets.contains(s);
        final ImmutableMinimalSet<S> subsets = SetReducer.without(this.subSets, s);
        final SimpleImmutableMinimalMap<T, S> remove =
                new SimpleMinimalHashMap<>(s.elements.entrySet().stream().collect(toMap(Map.Entry::getValue, e -> s)));
        final MapSubtractor<T, S> lookup = MapSubtractor.without(this.lookup, remove);
        return ctor.apply(subsets, lookup);
    }

    public abstract P remove(final Set<RowIdentifier> rows);

    protected P remove(final Set<RowIdentifier> rows,
            final BiFunction<ImmutableMinimalSet<S>, SimpleImmutableMinimalMap<T, S>, P> ctor) {
        if (rows.isEmpty()) {
            return getCorrectlyTypedThis();
        }
        assert this.subSets.stream().allMatch(subset -> rows.stream().allMatch(subset.elements::containsKey));
        final ImmutableMinimalSet<S> subsets = this.subSets.stream().map(subset -> subset.remove(rows))
                .collect(toCollection(MinimalSetWrapper::newIdentityHashSet));
        final Map<T, S> toHide = new IdentityHashMap<>();
        for (final RowIdentifier row : rows) {
            for (final S subSet : this.subSets) {
                final T t = subSet.get(row);
                toHide.put(t, subSet);
            }
        }
        final SimpleImmutableMinimalMap<T, S> lookup =
                MapSubtractor.without(this.lookup, new SimpleMinimalHashMap<>(toHide));
        return ctor.apply(subsets, lookup);
    }

    public S lookup(final T element) {
        return this.lookup.get(element);
    }
}
