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

package org.jamocha.dn.compiler.ecblocks.lazycollections.extend;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.ReplacingSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedHashSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalSetWrapper;

import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public final class IndexedIdentityMapToSetExtender<K, V> extends
        AbstractMapToSetExtender<K, V, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                .Entry<K, IndexedImmutableSet<V>>>> {
    private IndexedIdentityMapToSetExtender(
            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, IndexedImmutableSet<V>>>> wrapped,
            final K additionalKey, final V additionalValue) {
        super(wrapped, additionalKey, additionalValue, (a, b) -> a == b,
                new CtorStrategy<K, V, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                        .Entry<K, IndexedImmutableSet<V>>>>() {
                    @Override
                    public IndexedImmutableSet<V> getValueAsSet(final V additionalValue) {
                        return new IndexedHashSet<>(ImmutableSet.of(additionalValue));
                    }

                    @Override
                    public Map.Entry<K, IndexedImmutableSet<V>> getEntry(final K additionalKey,
                            final IndexedImmutableSet<V> valueAsSet) {
                        return Pair.of(additionalKey, valueAsSet);
                    }

                    @Override
                    public IndexedImmutableSet<K> getKeySet(
                            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>>> wrapped,
                            final K additionalKey) {
                        return IdentitySetExtender.with(wrapped.keySet(), additionalKey);
                    }

                    @Override
                    public ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>> getEntrySet(
                            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>>> wrapped,
                            final K additionalKey, final V additionalValue,
                            final Map.Entry<K, IndexedImmutableSet<V>> entry) {
                        final IndexedImmutableSet<V> toExtend = wrapped.get(additionalKey);
                        final ImmutableMinimalSet<Map.Entry<K, IndexedImmutableSet<V>>> wrappedEntries =
                                wrapped.entrySet();
                        if (null == toExtend) {
                            return IdentitySetExtender.with(wrappedEntries, entry);
                        }
                        final IndexedImmutableSet<V> extended = IdentitySetExtender.with(toExtend, additionalValue);
                        final Map.Entry<K, IndexedImmutableSet<V>> replacer = Pair.of(additionalKey, extended);
                        return new ReplacingSet<>(wrappedEntries, entry, replacer, Objects::equal);
                    }
                });
    }

    public static <K, V> IndexedIdentityMapToSetExtender<K, V> with(
            final ImmutableMinimalMap<K, IndexedImmutableSet<V>, IndexedImmutableSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, IndexedImmutableSet<V>>>> toWrap,
            final K additionalKey, final V additionalValue) {
        return new IndexedIdentityMapToSetExtender<>(toWrap, additionalKey, additionalValue);
    }

    @Override
    protected IndexedImmutableSet<V> extendViaIdentitySetExtender(final IndexedImmutableSet<V> toExtend,
            final V additionalValue) {
        return IdentitySetExtender.with(toExtend, additionalValue);
    }
}
