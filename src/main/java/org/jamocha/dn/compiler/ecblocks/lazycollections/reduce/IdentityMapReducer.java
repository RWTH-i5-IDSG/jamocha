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
public class IdentityMapReducer<K, V> implements LazyMap<K, V> {
    private final Map<K, V> wrapped;
    private final K reductionKey;
    private final V reductionValue;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Entry<K, V> entry = Pair.of(this.reductionKey, this.reductionValue);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<K> keySet = IdentitySetReducer.without(this.wrapped.keySet(), this.reductionKey);

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Collection<V> values = CollectionReducer.without(this.wrapped.values(), this.reductionValue);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<Entry<K, V>> entrySet = IdentitySetReducer.without(this.wrapped.entrySet(), getEntry());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() - 1;

    public static <K, V> IdentityMapReducer<K, V> without(final Map<K, V> toWrap, final K reductionKey) {
        final V reductionValue = toWrap.get(reductionKey);
        if (null == reductionValue) {
            throw new IllegalArgumentException("Wrapped map doesn't contain reduction key!");
        }
        return new IdentityMapReducer<>(toWrap, reductionKey, reductionValue);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean isEmpty() {
        return 0 == getSize();
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
        return !Objects.equals(this.reductionKey, key) && this.wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return !Objects.equals(this.reductionValue, value) && this.wrapped.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        if (this.reductionKey == key) return null;
        return this.wrapped.get(key);
    }
}
