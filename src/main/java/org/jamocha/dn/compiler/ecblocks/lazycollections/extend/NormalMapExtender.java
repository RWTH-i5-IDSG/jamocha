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

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;

import java.util.Map;
import java.util.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class NormalMapExtender<K, V>
        extends AbstractMapExtender<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>>
        implements SimpleImmutableMinimalMap<K, V> {
    protected NormalMapExtender(
            final ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>> wrapped,
            final K additionalKey, final V additionalValue) {
        super(wrapped, additionalKey, additionalValue, Objects::equals,
                SetExtender.with(wrapped.keySet(), additionalKey),
                SetExtender.with(wrapped.entrySet(), Pair.of(additionalKey, additionalValue)));
    }

    public static <K, V> NormalMapExtender<K, V> with(
            final ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>> toWrap,
            final K additionalKey, final V additionalValue) {
        if (toWrap.containsKey(additionalKey)) {
            throw new UnsupportedOperationException(
                    "Hiding keys of the wrapped map is not supported, since it is too error-prone!");
        }
        return new NormalMapExtender<>(toWrap, additionalKey, additionalValue);
    }
}
