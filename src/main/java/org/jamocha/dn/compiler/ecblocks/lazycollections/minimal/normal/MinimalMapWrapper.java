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

package org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal;

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.MapAsMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SetAsMinimalSet;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class MinimalMapWrapper<K, V> implements MapAsMinimalMap<K, V> {
    private final Map<K, V> wrapped;

    @Override
    public SetAsMinimalSet<K> keySet() {
        return new MinimalSetWrapper<>(this.wrapped.keySet());
    }

    @Override
    public SetAsMinimalSet<Entry<K, V>> entrySet() {
        return new MinimalSetWrapper<>(this.wrapped.entrySet());
    }

    @Override
    public String toString() {
        return this.wrapped.toString();
    }

    @Override
    public int size() {
        return this.wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return this.wrapped.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.wrapped.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return this.wrapped.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return this.wrapped.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return this.wrapped.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        this.wrapped.putAll(m);
    }

    @Override
    public void clear() {
        this.wrapped.clear();
    }

    @Override
    public Collection<V> values() {
        return this.wrapped.values();
    }

    @Override
    public boolean equals(final Object o) {
        return this.wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        return this.wrapped.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        this.wrapped.forEach(action);
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        this.wrapped.replaceAll(function);
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.wrapped.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return this.wrapped.remove(key, value);
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return this.wrapped.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(final K key, final V value) {
        return this.wrapped.replace(key, value);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return this.wrapped.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.wrapped.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.wrapped.compute(key, remappingFunction);
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.wrapped.merge(key, value, remappingFunction);
    }
}
