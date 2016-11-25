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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalHashSet;

import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public final class MapToSetExtender<K, V> extends
        AbstractMapToSetExtender<K, V, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                .Entry<K, ImmutableMinimalSet<V>>>>
        implements SimpleImmutableMinimalMap<K, ImmutableMinimalSet<V>> {
    private MapToSetExtender(
            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, ImmutableMinimalSet<V>>>> wrapped,
            final K additionalKey, final V additionalValue) {
        super(wrapped, additionalKey, additionalValue, (a, b) -> a == b,
                new CtorStrategy<K, V, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                        .Entry<K, ImmutableMinimalSet<V>>>>() {
                    @Override
                    public ImmutableMinimalSet<V> getValueAsSet(final V additionalValue) {
                        return new MinimalHashSet<>(ImmutableSet.of(additionalValue));
                    }

                    @Override
                    public Map.Entry<K, ImmutableMinimalSet<V>> getEntry(final K additionalKey,
                            final ImmutableMinimalSet<V> valueAsSet) {
                        return Pair.of(additionalKey, valueAsSet);
                    }

                    @Override
                    public ImmutableMinimalSet<K> getKeySet(
                            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>>> wrapped,
                            final K additionalKey) {
                        return SetExtender.with(wrapped.keySet(), additionalKey);
                    }

                    @Override
                    public ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>> getEntrySet(
                            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>,
                                    ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>>> wrapped,
                            final K additionalKey, final V additionalValue,
                            final Map.Entry<K, ImmutableMinimalSet<V>> entry) {
                        final ImmutableMinimalSet<V> toExtend = wrapped.get(additionalKey);
                        final ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>> wrappedEntries =
                                wrapped.entrySet();
                        if (null == toExtend) {
                            return SetExtender.with(wrappedEntries, entry);
                        }
                        final ImmutableMinimalSet<V> extended = SetExtender.with(toExtend, additionalValue);
                        final Map.Entry<K, ImmutableMinimalSet<V>> replacer = Pair.of(additionalKey, extended);
                        return new ReplacingSet<>(wrappedEntries, entry, replacer, Objects::equal);
                    }
                });
    }

    public static <K, V> MapToSetExtender<K, V> with(
            final ImmutableMinimalMap<K, ImmutableMinimalSet<V>, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map
                    .Entry<K, ImmutableMinimalSet<V>>>> toWrap,
            final K additionalKey, final V additionalValue) {
        return new MapToSetExtender<>(toWrap, additionalKey, additionalValue);
    }

    @Override
    protected ImmutableMinimalSet<V> extendViaIdentitySetExtender(final ImmutableMinimalSet<V> toExtend,
            final V additionalValue) {
        return IdentitySetExtender.with(toExtend, additionalValue);
    }
}