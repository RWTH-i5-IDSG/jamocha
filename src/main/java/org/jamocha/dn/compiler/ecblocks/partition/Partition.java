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
import org.jamocha.dn.compiler.ecblocks.RowIdentifier;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.IdentityMapExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.MapCombiner;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.SetExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.IdentityMapReducer;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.MapSubtractor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.SetReducer;

import java.util.*;
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
    abstract static class SubSet<T, S extends SubSet<T, S>> {
        protected final Map<RowIdentifier, T> elements;

        SubSet(final SubSet<T, S> copy) {
            this(new IdentityHashMap<>(copy.elements));
        }

        public T get(final RowIdentifier rule) {
            return this.elements.get(rule);
        }

        public abstract S add(final RowIdentifier key, final T value);

        protected S add(final RowIdentifier key, final T value, final Function<Map<RowIdentifier, T>, S> subsetCtor) {
            return subsetCtor.apply(IdentityMapExtender.with(this.elements, key, value));
        }

        public abstract S remove(final RowIdentifier key);

        protected S remove(final RowIdentifier key, final Function<Map<RowIdentifier, T>, S> subsetCtor) {
            return subsetCtor.apply(IdentityMapReducer.without(this.elements, key));
        }
    }

    protected final Set<S> subSets;
    protected final Map<T, S> lookup;

    public Partition() {
        this(new HashSet<>(), new HashMap<>());
    }

    public Partition(final Partition<T, S, P> copy, final Function<? super S, ? extends S> copyCtor) {
        this();
        copy.subSets.stream().map(copyCtor).forEach(this.subSets::add);
        for (final S s : this.subSets) {
            for (final T t : s.elements.values()) {
                this.lookup.put(t, s);
            }
        }
    }

    public abstract P add(final S newSubSet);

    protected P add(final S newSubSet, final BiFunction<Set<S>, Map<T, S>, P> ctor) {
        assert !newSubSet.elements.values().contains(null);
        assert this.subSets.stream().allMatch(ss -> ss.getElements().keySet().equals(newSubSet.elements.keySet()));
        assert this.subSets.stream()
                .allMatch(ss -> Collections.disjoint(ss.getElements().values(), newSubSet.elements.values()));
        final Set<S> subsets = SetExtender.with(this.subSets, newSubSet);
        final MapCombiner<T, S> lookup =
                MapCombiner.with(this.lookup, Maps.toMap(newSubSet.elements.values(), x -> newSubSet));
        return ctor.apply(subsets, lookup);
    }

    public abstract P extend(final RowIdentifier row, final IdentityHashMap<S, T> extension);

    protected P extend(final RowIdentifier row, final IdentityHashMap<S, T> extension,
            final BiFunction<Set<S>, Map<T, S>, P> ctor) {
        assert !extension.containsValue(null);
        for (final S subset : this.subSets) {
            final T newElement = extension.get(subset);
            assert null != newElement;
            subset.elements.put(row, newElement);
        }
        assert !this.subSets.stream().map(extension::get).filter(Objects::isNull).findAny().isPresent();
        final Set<S> subsets =
                this.subSets.stream().map(subset -> subset.add(row, extension.get(subset))).collect(toSet());
        final Map<T, S> lookup = MapCombiner.with(this.lookup, invert(extension));
        return ctor.apply(subsets, lookup);
    }

    protected static <K, V> Map<K, V> invert(final Map<V, K> map) {
        return map.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public abstract P remove(final RowIdentifier row);

    protected P remove(final RowIdentifier row, final BiFunction<Set<S>, Map<T, S>, P> ctor) {
        assert this.subSets.stream().allMatch(subset -> subset.elements.containsKey(row));
        final Set<S> subsets = this.subSets.stream().map(subset -> subset.remove(row)).collect(toSet());
        final Map<T, S> lookup = MapSubtractor.without(this.lookup,
                this.subSets.stream().collect(toMap(subset -> subset.get(row), Function.identity())));
        return ctor.apply(subsets, lookup);
    }

    public abstract P remove(final S s);

    protected P remove(final S s, final BiFunction<Set<S>, Map<T, S>, P> ctor) {
        assert this.subSets.contains(s);
        final Set<S> subsets = SetReducer.without(this.subSets, s);
        final MapSubtractor<T, S> lookup = MapSubtractor.without(this.lookup, Maps.toMap(s.elements.values(), x -> s));
        return ctor.apply(subsets, lookup);
    }

    public S lookup(final T element) {
        return this.lookup.get(element);
    }
}
