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

package org.jamocha.dn.compiler.ecblocks.lazycollections.reduce;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.ReplacingSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedHashSet;

import java.util.Map;
import java.util.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MapToSetReducer<K, V> extends
        AbstractMapToSetReducer<K, V, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                .Entry<K, ImmutableMinimalSet<V>>>>
        implements SimpleImmutableMinimalMap<K, ImmutableMinimalSet<V>> {
    private MapToSetReducer(
            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, ImmutableMinimalSet<V>>>> wrapped,
            final K reductionKey, final V reductionValue) {
        super(wrapped, reductionKey, reductionValue, Objects::equals,
                new CtorStrategy<K, V, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                        .Entry<K, ImmutableMinimalSet<V>>>>() {
                    @Override
                    public ImmutableMinimalSet<V> getValueAsSet(final V reductionValue) {
                        return new IndexedHashSet<>(ImmutableSet.of(reductionValue));
                    }

                    @Override
                    public Map.Entry<K, ImmutableMinimalSet<V>> getEntry(final K reductionKey,
                            final ImmutableMinimalSet<V> valueAsSet) {
                        return Pair.of(reductionKey, valueAsSet);
                    }

                    @Override
                    public ImmutableMinimalSet<K> getKeySet(
                            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>>> wrapped,
                            final K reductionKey) {
                        return IdentitySetReducer.without(wrapped.keySet(), reductionKey);
                    }

                    @Override
                    public ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>> getEntrySet(
                            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>>> wrapped,
                            final K reductionKey, final V reductionValue,
                            final Map.Entry<K, ImmutableMinimalSet<V>> entry) {
                        final ImmutableMinimalSet<V> toReduce = wrapped.get(reductionKey);
                        final ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>> wrappedEntries =
                                wrapped.entrySet();
                        if (1 == toReduce.size()) {
                            // reduced set would be empty, hide the entire entry
                            // can't use identity hash set since getting the original Entry is too cumbersome
                            return SetReducer.without(wrappedEntries, Pair.of(reductionKey, toReduce));
                        }
                        final ImmutableMinimalSet<V> reduced = IdentitySetReducer.without(toReduce, reductionValue);
                        return new ReplacingSet<>(wrappedEntries, entry, Pair.of(reductionKey, reduced),
                                Objects::equals);
                    }
                });
    }

    public static <K, V> MapToSetReducer<K, V> without(
            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, ImmutableMinimalSet<V>>>> toWrap,
            final K reductionKey, final V reductionValue) {
        final ImmutableMinimalSet<V> vs = toWrap.get(reductionKey);
        if (null == vs) {
            throw new IllegalArgumentException("Wrapped map doesn't contain reduction key!");
        }
        if (!vs.contains(reductionValue)) {
            throw new IllegalArgumentException("Wrapped map entry doesn't contain reduction value!");
        }
        return new MapToSetReducer<>(toWrap, reductionKey, reductionValue);
    }

    @Override
    protected ImmutableMinimalSet<V> reduceViaIdentitySetReducer(final ImmutableMinimalSet<V> toRecude,
            final V reductionValue) {
        return IdentitySetReducer.without(toRecude, reductionValue);
    }
}
