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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractMapExtender<K, V, S extends ImmutableMinimalSet<K>, N extends ImmutableMinimalSet<Map.Entry<K, V>>>
        implements ImmutableMinimalMap<K, V, S, N> {
    private final ImmutableMinimalMap<K, V, S, N> wrapped;
    private final K additionalKey;
    private final V additionalValue;
    private final BiPredicate<K, Object> equalityCheck;

    @Getter(value = AccessLevel.PRIVATE)
    private final S keySet;
    @Getter(value = AccessLevel.PRIVATE)
    private final N entrySet;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    public HashMap<K, V> toHashMap() {
        return entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
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
    public S keySet() {
        return getKeySet();
    }

    @Override
    public N entrySet() {
        return getEntrySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.equalityCheck.test(this.additionalKey, key) || this.wrapped.containsKey(key);
    }

    @Override
    public V get(final Object key) {
        if (this.equalityCheck.test(this.additionalKey, key)) return this.additionalValue;
        return this.wrapped.get(key);
    }
}
