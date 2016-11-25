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

package org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.MinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalHashSet;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IdentityHashMapWithIndexedIdentityKeySet<K, V>
        implements MinimalMap<K, V, IndexedImmutableSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>> {
    private final IndexedIdentityHashSet<K> keySet = new IndexedIdentityHashSet<>();
    private final ArrayList<V> values = new ArrayList<>();
    private final MinimalHashSet<Map.Entry<K, V>> entrySet = new MinimalHashSet<>();

    public IdentityHashMapWithIndexedIdentityKeySet() {
    }

    public IdentityHashMapWithIndexedIdentityKeySet(final Map<K, V> map) {
        map.forEach(this::put);
    }

    public IdentityHashMapWithIndexedIdentityKeySet(
            final ImmutableMinimalMap<K, V, IndexedImmutableSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>> map) {
        map.entrySet().forEach(e -> this.put(e.getKey(), e.getValue()));
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.keySet.contains(key);
    }

    @Override
    public V get(final Object key) {
        return this.values.get(this.keySet.getInternalPosition(key));
    }

    @Override
    public IndexedImmutableSet<K> keySet() {
        return this.keySet;
    }

    @Override
    public ImmutableMinimalSet<Map.Entry<K, V>> entrySet() {
        return this.entrySet;
    }

    @Override
    public V put(final K key, final V value) {
        if (this.keySet.add(key)) {
            // new key
            this.values.add(value);
            this.entrySet.add(Pair.of(key, value));
            return null;
        }
        final V oldValue = this.values.set(this.keySet.getInternalPosition(key), value);
        this.entrySet.remove(Pair.of(key, oldValue));
        this.entrySet.add(Pair.of(key, value));
        return oldValue;
    }
}
