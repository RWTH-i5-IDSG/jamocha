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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedHashSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;

import java.util.Map;
import java.util.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IndexedIdentityMapToSetReducer<K, V> extends
        AbstractMapToSetReducer<K, V, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                .Entry<K, IndexedImmutableSet<V>>>> {
    private IndexedIdentityMapToSetReducer(
            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, IndexedImmutableSet<V>>>> wrapped,
            final K reductionKey, final V reductionValue) {
        super(wrapped, reductionKey, reductionValue, (a, b) -> a == b,
                new CtorStrategy<K, V, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                        .Entry<K, IndexedImmutableSet<V>>>>() {
                    @Override
                    public IndexedImmutableSet<V> getValueAsSet(final V reductionValue) {
                        return new IndexedHashSet<>(ImmutableSet.of(reductionValue));
                    }

                    @Override
                    public Map.Entry<K, IndexedImmutableSet<V>> getEntry(final K reductionKey,
                            final IndexedImmutableSet<V> valueAsSet) {
                        return Pair.of(reductionKey, valueAsSet);
                    }

                    @Override
                    public IndexedImmutableSet<K> getKeySet(
                            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>>> wrapped,
                            final K reductionKey) {
                        return IdentitySetReducer.without(wrapped.keySet(), reductionKey);
                    }

                    @Override
                    public ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>> getEntrySet(
                            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>>> wrapped,
                            final K reductionKey, final V reductionValue,
                            final Map.Entry<K, IndexedImmutableSet<V>> entry) {
                        final IndexedImmutableSet<V> toReduce = wrapped.get(reductionKey);
                        final ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>> wrappedEntries =
                                wrapped.entrySet();
                        if (1 == toReduce.size()) {
                            // reduced set would be empty, hide the entire entry
                            // can't use identity hash set since getting the original Entry is too cumbersome
                            return SetReducer.without(wrappedEntries, Pair.of(reductionKey, toReduce));
                        }
                        final IndexedImmutableSet<V> reduced = IdentitySetReducer.without(toReduce, reductionValue);
                        return new ReplacingSet<>(wrappedEntries, entry, Pair.of(reductionKey, reduced),
                                Objects::equals);
                    }
                });
    }

    public static <K, V> IndexedIdentityMapToSetReducer<K, V> without(
            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, IndexedImmutableSet<V>>>> toWrap,
            final K reductionKey, final V reductionValue) {
        final IndexedImmutableSet<V> vs = toWrap.get(reductionKey);
        if (null == vs) {
            throw new IllegalArgumentException("Wrapped map doesn't contain reduction key!");
        }
        if (!vs.contains(reductionValue)) {
            throw new IllegalArgumentException("Wrapped map entry doesn't contain reduction value!");
        }
        return new IndexedIdentityMapToSetReducer<>(toWrap, reductionKey, reductionValue);
    }

    @Override
    protected IndexedImmutableSet<V> reduceViaIdentitySetReducer(final IndexedImmutableSet<V> toRecude,
            final V reductionValue) {
        return IdentitySetReducer.without(toRecude, reductionValue);
    }
}
