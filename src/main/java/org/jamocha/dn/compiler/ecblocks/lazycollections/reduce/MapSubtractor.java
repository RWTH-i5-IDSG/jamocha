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

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.LazyMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapSubtractor<K, V> implements LazyMap<K, V> {
    private final Map<K, V> wrapped;
    private final Map<K, V> reductionEntries;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<K> keySet = Sets.difference(this.wrapped.keySet(), this.reductionEntries.keySet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final CollectionSubtractor<V> values =
            CollectionSubtractor.without(this.wrapped.values(), this.reductionEntries.values());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<Entry<K, V>> entrySet =
            Sets.difference(this.wrapped.entrySet(), this.reductionEntries.entrySet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() - this.reductionEntries.size();

    public static <K, V> MapSubtractor<K, V> without(final Map<K, V> toWrap, final Map<K, V> reductionEntries) {
        if (!toWrap.keySet().containsAll(reductionEntries.keySet())) {
            throw new UnsupportedOperationException(
                    "Not all of the elements that are to be hidden are actually in the wrapped map!");
        }
        return new MapSubtractor<>(toWrap, reductionEntries);
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
        return !this.reductionEntries.containsKey(key) && this.wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return !this.reductionEntries.containsValue(value) && this.wrapped.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        final V v = this.reductionEntries.get(key);
        if (null != v) return null;
        return this.wrapped.get(key);
    }
}
