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

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.LazyMap;

import java.util.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapCombiner<K, V> extends LazyMap<K, V> {
    private final Map<K, V> wrapped;
    private final Map<K, V> additionalEntries;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<K> keySet = Sets.union(this.wrapped.keySet(), this.additionalEntries.keySet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final CollectionCombiner<V> values =
            CollectionCombiner.with(this.wrapped.values(), this.additionalEntries.values());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<Entry<K, V>> entrySet = Sets.union(this.wrapped.entrySet(), this.additionalEntries.entrySet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + this.additionalEntries.size();

    public static <K, V> MapCombiner<K, V> with(final Map<K, V> toWrap, final Map<K, V> additionalEntries) {
        if (!Collections.disjoint(toWrap.keySet(), additionalEntries.keySet())) {
            throw new UnsupportedOperationException(
                    "Hiding keys of the wrapped map is not supported, since it is too error-prone!");
        }
        return new MapCombiner<>(toWrap, additionalEntries);
    }

    public HashMap<K, V> toHashMap() {
        return new HashMap<>(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public Set<K> keySet() {
        return getKeySet();
    }

    @Override
    public Collection<V> values() {
        return getValues();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getEntrySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.additionalEntries.containsKey(key) || this.wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.additionalEntries.containsValue(value) || this.wrapped.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        final V v = this.additionalEntries.get(key);
        if (null != v) return v;
        return this.wrapped.get(key);
    }
}
