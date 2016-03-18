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

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.LazyMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.ReplacingCollection;
import org.jamocha.dn.compiler.ecblocks.lazycollections.ReplacingSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityMapToSetExtender<K, V> extends LazyMap<K, Set<V>> {
    private final Map<K, Set<V>> wrapped;
    private final K additionalKey;
    private final V additionalValue;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<V> valueAsSet = ImmutableSet.of(this.additionalValue);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Map.Entry<K, Set<V>> entry = Pair.of(this.additionalKey, getValueAsSet());
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<K> keySet = IdentitySetExtender.with(this.wrapped.keySet(), this.additionalKey);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Collection<Set<V>> values = determineValues();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<Entry<K, Set<V>>> entrySet = determineEntrySet();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    private Set<Entry<K, Set<V>>> determineEntrySet() {
        final Set<V> toExtend = this.wrapped.get(this.additionalKey);
        if (null == toExtend) {
            return IdentitySetExtender.with(this.wrapped.entrySet(), getEntry());
        }
        final Set<V> extended = IdentitySetExtender.with(toExtend, this.additionalValue);
        return new ReplacingSet<>(this.wrapped.entrySet(), getEntry(), Pair.of(this.additionalKey, extended),
                (a, b) -> a == b);
    }

    private Collection<Set<V>> determineValues() {
        final Set<V> toExtend = this.wrapped.get(this.additionalKey);
        if (null == toExtend) {
            return CollectionExtender.with(this.wrapped.values(), getValueAsSet());
        }
        final Set<V> extended = IdentitySetExtender.with(toExtend, this.additionalValue);
        return new ReplacingCollection<>(this.wrapped.values(), toExtend, extended, (a, b) -> a == b);
    }

    public static <K, V> IdentityMapToSetExtender<K, V> with(final Map<K, Set<V>> toWrap, final K additionalKey,
            final V additionalValue) {
        return new IdentityMapToSetExtender<>(toWrap, additionalKey, additionalValue);
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
    public Collection<Set<V>> values() {
        return getValues();
    }

    @Override
    public Set<Entry<K, Set<V>>> entrySet() {
        return getEntrySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.additionalKey == key || this.wrapped.containsKey(key);
    }

    @Override
    public Set<V> get(final Object key) {
        final boolean here = this.additionalKey == key;
        final Set<V> there = this.wrapped.get(key);
        if (!here) return there;
        if (null != there) {
            return IdentitySetExtender.with(there, this.additionalValue);
        }
        return getValueAsSet();
    }
}
