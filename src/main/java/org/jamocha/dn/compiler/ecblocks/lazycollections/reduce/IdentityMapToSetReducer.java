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

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.LazyMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.ReplacingCollection;
import org.jamocha.dn.compiler.ecblocks.lazycollections.ReplacingSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityMapToSetReducer<K, V> extends LazyMap<K, Set<V>> {
    private final Map<K, Set<V>> wrapped;
    private final K reductionKey;
    private final V reductionValue;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Entry<K, Set<V>> entry = Pair.of(this.reductionKey, ImmutableSet.of(this.reductionValue));
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<K> keySet = IdentitySetReducer.without(this.wrapped.keySet(), this.reductionKey);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Collection<Set<V>> values = determineValues();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Set<Entry<K, Set<V>>> entrySet = determineEntrySet();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    private Set<Entry<K, Set<V>>> determineEntrySet() {
        final Set<V> toReduce = this.wrapped.get(this.reductionKey);
        if (1 == toReduce.size()) {
            // reduced set would be empty, hide the entire entry
            // can't use identity hash set since getting the original Entry is too cumbersome
            return SetReducer.without(this.wrapped.entrySet(), Pair.of(this.reductionKey, toReduce));
        }
        final Set<V> reduced = IdentitySetReducer.without(toReduce, this.reductionValue);
        return new ReplacingSet<>(this.wrapped.entrySet(), getEntry(), Pair.of(this.reductionKey, reduced),
                (a, b) -> a == b);
    }

    private Collection<Set<V>> determineValues() {
        final Set<V> toReduce = this.wrapped.get(this.reductionKey);
        if (1 == toReduce.size()) {
            // reduced set would be empty, hide the entire value
            return IdentitySetReducer.without(this.wrapped.values(), toReduce);
        }
        final Set<V> reduced = IdentitySetReducer.without(toReduce, this.reductionValue);
        return new ReplacingCollection<>(this.wrapped.values(), toReduce, reduced, (a, b) -> a == b);
    }

    public static <K, V> IdentityMapToSetReducer<K, V> without(final Map<K, Set<V>> toWrap, final K reductionKey,
            final V reductionValue) {
        final Set<V> vs = toWrap.get(reductionKey);
        if (null == vs) {
            throw new IllegalArgumentException("Wrapped map doesn't contain reduction key!");
        }
        if (!vs.contains(reductionValue)) {
            throw new IllegalArgumentException("Wrapped map entry doesn't contain reduction value!");
        }
        return new IdentityMapToSetReducer<>(toWrap, reductionKey, reductionValue);
    }

    @Override
    public boolean isEmpty() {
        return 0 == getSize();
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
        return this.reductionKey == key || this.wrapped.containsKey(key);
    }

    @Override
    public Set<V> get(final Object key) {
        final boolean here = this.reductionKey == key;
        final Set<V> there = this.wrapped.get(key);
        if (!here) return there;
        if (null != there) {
            return IdentitySetReducer.without(there, this.reductionValue);
        }
        return null;
    }
}
