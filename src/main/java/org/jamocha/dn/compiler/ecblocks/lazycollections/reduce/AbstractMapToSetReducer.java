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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;

import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class AbstractMapToSetReducer<K, V, VALUESET extends ImmutableMinimalSet<V>, S extends
        ImmutableMinimalSet<K>, N extends ImmutableMinimalSet<Map.Entry<K, VALUESET>>>
        implements ImmutableMinimalMap<K, VALUESET, S, N> {
    protected final ImmutableMinimalMap<K, VALUESET, S, N> wrapped;
    protected final K reductionKey;
    protected final V reductionValue;
    protected final BiPredicate<Object, Object> keyEquals;
    protected final CtorStrategy<K, V, VALUESET, S, N> strategy;

    protected AbstractMapToSetReducer(final ImmutableMinimalMap<K, VALUESET, S, N> wrapped, final K reductionKey,
            final V reductionValue, final BiPredicate<Object, Object> keyEquals,
            final CtorStrategy<K, V, VALUESET, S, N> strategy) {
        this.wrapped = wrapped;
        this.reductionKey = reductionKey;
        this.reductionValue = reductionValue;
        this.keyEquals = keyEquals;
        this.strategy = strategy;
    }

    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final VALUESET valueAsSet = strategy.getValueAsSet(reductionValue);
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final Map.Entry<K, VALUESET> entry = strategy.getEntry(reductionKey, getValueAsSet());
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final S keySet = strategy.getKeySet(wrapped, reductionKey);
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final N entrySet = strategy.getEntrySet(wrapped, reductionKey, reductionValue, getEntry());
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final int size = wrapped.size() - 1;

    interface CtorStrategy<K, V, VALUESET extends ImmutableMinimalSet<V>, S extends ImmutableMinimalSet<K>, N extends
            ImmutableMinimalSet<Map.Entry<K, VALUESET>>> {
        VALUESET getValueAsSet(final V reductionValue);

        Map.Entry<K, VALUESET> getEntry(final K reductionKey, final VALUESET valueAsSet);

        S getKeySet(final ImmutableMinimalMap<K, VALUESET, S, N> wrapped, final K reductionKey);

        N getEntrySet(final ImmutableMinimalMap<K, VALUESET, S, N> wrapped, final K reductionKey,
                final V reductionValue, final Map.Entry<K, VALUESET> entry);
    }

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
        return null != get(key);
    }

    @Override
    public VALUESET get(final Object key) {
        final boolean here = this.keyEquals.test(this.reductionKey, key);
        final VALUESET there = this.wrapped.get(key);
        if (!here) return there;
        if (null != there) {
            if (1 == there.size()) {
                // reduced set would be empty, just return null
                return null;
            }
            return reduceViaIdentitySetReducer(there, this.reductionValue);
        }
        return null;
    }

    protected abstract VALUESET reduceViaIdentitySetReducer(final VALUESET toRecude, final V reductionValue);
}
