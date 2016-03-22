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

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MapToSetReducer<K, V> extends AbstractMapToSetReducer<K, V> {
    public MapToSetReducer(final Map<K, Set<V>> wrapped, final K reductionKey, final V reductionValue) {
        super(wrapped, reductionKey, reductionValue, Objects::equals);
    }

    public static <K, V> MapToSetReducer<K, V> without(final Map<K, Set<V>> toWrap, final K reductionKey,
            final V reductionValue) {
        final Set<V> vs = toWrap.get(reductionKey);
        if (null == vs) {
            throw new IllegalArgumentException("Wrapped map doesn't contain reduction key!");
        }
        if (!vs.contains(reductionValue)) {
            throw new IllegalArgumentException("Wrapped map entry doesn't contain reduction value!");
        }
        return new MapToSetReducer<>(toWrap, reductionKey, reductionValue);
    }
}
