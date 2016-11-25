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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;

import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class AbstractMapToSetExtender<K, V, VALUESET extends ImmutableMinimalSet<V>, S extends
        ImmutableMinimalSet<K>, N extends ImmutableMinimalSet<Map.Entry<K, VALUESET>>>
        implements ImmutableMinimalMap<K, VALUESET, S, N> {
    protected final ImmutableMinimalMap<K, VALUESET, S, N> wrapped;
    protected final K additionalKey;
    protected final V additionalValue;
    protected final BiPredicate<Object, Object> keyEquals;
    protected final CtorStrategy<K, V, VALUESET, S, N> strategy;

    protected AbstractMapToSetExtender(final ImmutableMinimalMap<K, VALUESET, S, N> wrapped, final K additionalKey,
            final V additionalValue, final BiPredicate<Object, Object> keyEquals,
            final CtorStrategy<K, V, VALUESET, S, N> strategy) {
        this.wrapped = wrapped;
        this.additionalKey = additionalKey;
        this.additionalValue = additionalValue;
        this.keyEquals = keyEquals;
        this.strategy = strategy;
    }

    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final VALUESET valueAsSet = strategy.getValueAsSet(additionalValue);
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final Map.Entry<K, VALUESET> entry = strategy.getEntry(additionalKey, getValueAsSet());
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final S keySet = strategy.getKeySet(wrapped, additionalKey);
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final N entrySet = strategy.getEntrySet(wrapped, additionalKey, additionalValue, getEntry());
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final int size = wrapped.size() + 1;

    interface CtorStrategy<K, V, VALUESET extends ImmutableMinimalSet<V>, S extends ImmutableMinimalSet<K>, N extends
            ImmutableMinimalSet<Map.Entry<K, VALUESET>>> {
        VALUESET getValueAsSet(final V additionalValue);

        Map.Entry<K, VALUESET> getEntry(final K additionalKey, final VALUESET valueAsSet);

        S getKeySet(final ImmutableMinimalMap<K, VALUESET, S, N> wrapped, final K additionalKey);

        N getEntrySet(final ImmutableMinimalMap<K, VALUESET, S, N> wrapped, final K additionalKey,
                final V additionalValue, final Map.Entry<K, VALUESET> entry);
    }
    //
    //    private ImmutableMinimalSet<K> determineKeySet() {
    //        return IdentitySetExtender.with(this.wrapped.keySet(), this.additionalKey);
    //    }
    //
    //    private ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>> determineEntrySet() {
    //        final ImmutableMinimalSet<V> toExtend = this.wrapped.get(this.additionalKey);
    //        final ImmutableMinimalSet<Map.Entry<K, ImmutableMinimalSet<V>>> wrappedEntries = this.wrapped.entrySet();
    //        final Map.Entry<K, ImmutableMinimalSet<V>> entry = getEntry();
    //        if (null == toExtend) {
    //            return IdentitySetExtender.with(wrappedEntries, entry);
    //        }
    //        final ImmutableMinimalSet<V> extended = IdentitySetExtender.with(toExtend, this.additionalValue);
    //        final Map.Entry<K, ImmutableMinimalSet<V>> replacer = Pair.of(this.additionalKey, extended);
    //        return new ReplacingSet<>(wrappedEntries, entry, replacer, Objects::equal);
    //    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public S keySet() {
        return getKeySet();
    }

    @Override
    public N entrySet() {
        return getEntrySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.additionalKey == key || this.wrapped.containsKey(key);
    }

    @Override
    public VALUESET get(final Object key) {
        final boolean here = this.keyEquals.test(this.additionalKey, key);
        final VALUESET there = this.wrapped.get(key);
        if (!here) return there;
        if (null != there) {
            return extendViaIdentitySetExtender(there, this.additionalValue);
        }
        return getValueAsSet();
    }

    protected abstract VALUESET extendViaIdentitySetExtender(final VALUESET toExtend, final V additionalValue);
}
