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

package org.jamocha.dn.compiler.ecblocks.lazycollections;

import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface LazyMap<K, V> extends Map<K, V> {
    @Override
    default boolean containsValue(final Object value) {
        throw new UnsupportedOperationException("Lazy Maps don't support containsValue!");
    }

    @Override
    default V put(final K key, final V value) {
        throw new UnsupportedOperationException("Lazy Maps are immutable!");
    }

    @Override
    default V remove(final Object key) {
        throw new UnsupportedOperationException("Lazy Maps are immutable!");
    }

    @Override
    default void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Lazy Maps are immutable!");
    }

    @Override
    default void clear() {
        throw new UnsupportedOperationException("Lazy Maps are immutable!");
    }
}
