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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.LazyMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityMapExtender<K, V> extends LazyMap<K, V> {
    private final Map<K, V> wrapped;
    private final Entry<K, V> additionalEntry;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<K> keySet = IdentitySetExtender.with(this.wrapped.keySet(), this.additionalEntry.getKey());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Collection<V> values = CollectionExtender.with(this.wrapped.values(), this.additionalEntry.getValue());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<Entry<K, V>> entrySet = IdentitySetExtender.with(this.wrapped.entrySet(), this.additionalEntry);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    public static <K, V> IdentityMapExtender<K, V> with(final Map<K, V> toWrap, final Entry<K, V> additionalEntry) {
        if (toWrap.containsKey(additionalEntry.getKey())) {
            throw new UnsupportedOperationException(
                    "Hiding keys of the wrapped map is not supported, since it is too error-prone!");
        }
        return new IdentityMapExtender<>(toWrap, additionalEntry);
    }

    public static <K, V> IdentityMapExtender<K, V> with(final Map<K, V> toWrap, final K additionalKey,
            final V additionalValue) {
        return with(toWrap, Pair.of(additionalKey, additionalValue));
    }

    public IdentityMapExtender<K, V> with(final Entry<K, V> additionalEntry) {
        return with(this, additionalEntry);
    }

    public IdentityMapExtender<K, V> with(final K additionalKey, final V additionalValue) {
        return with(this, additionalKey, additionalValue);
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
        return this.additionalEntry.getKey() == key || this.wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return Objects.equals(this.additionalEntry.getValue(), value) || this.wrapped.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        if (this.additionalEntry.getKey() == key) return this.additionalEntry.getValue();
        return this.wrapped.get(key);
    }
}
