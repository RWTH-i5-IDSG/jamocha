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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;

import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapSubtractor<K, V> implements SimpleImmutableMinimalMap<K, V> {
    private final ImmutableMinimalMap<K, V, ? extends ImmutableMinimalSet<K>, ? extends ImmutableMinimalSet<Map
            .Entry<K, V>>>
            wrapped;
    private final ImmutableMinimalMap<K, V, ? extends ImmutableMinimalSet<K>, ? extends ImmutableMinimalSet<Map
            .Entry<K, V>>>
            reductionEntries;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final ImmutableMinimalSet<K> keySet =
            SetSubtractor.without(this.wrapped.keySet(), this.reductionEntries.keySet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final ImmutableMinimalSet<Map.Entry<K, V>> entrySet =
            SetSubtractor.without(this.wrapped.entrySet(), this.reductionEntries.entrySet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() - this.reductionEntries.size();

    public static <K, V> MapSubtractor<K, V> without(
            final ImmutableMinimalMap<K, V, ? extends ImmutableMinimalSet<K>, ? extends ImmutableMinimalSet<Map
                    .Entry<K, V>>> toWrap,
            final ImmutableMinimalMap<K, V, ? extends ImmutableMinimalSet<K>, ? extends ImmutableMinimalSet<Map
                    .Entry<K, V>>> reductionEntries) {
        if (!SetSubtractor.without(reductionEntries.keySet(), toWrap.keySet()).isEmpty()) {
            throw new UnsupportedOperationException(
                    "Not all of the elements that are to be hidden are actually in the wrapped map!");
        }
        return new MapSubtractor<>(toWrap, reductionEntries);
    }

    public static <K, V> MapSubtractor<K, V> without(final SimpleImmutableMinimalMap<K, V> toWrap,
            final SimpleImmutableMinimalMap<K, V> reductionEntries) {
        return without((ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>>) toWrap,
                (ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>>)
                        reductionEntries);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public ImmutableMinimalSet<K> keySet() {
        return getKeySet();
    }

    @Override
    public ImmutableMinimalSet<Map.Entry<K, V>> entrySet() {
        return getEntrySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        return !this.reductionEntries.containsKey(key) && this.wrapped.containsKey(key);
    }

    @Override
    public V get(final Object key) {
        final V v = this.reductionEntries.get(key);
        if (null != v) return null;
        return this.wrapped.get(key);
    }
}
