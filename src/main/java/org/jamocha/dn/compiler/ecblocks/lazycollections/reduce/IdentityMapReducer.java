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

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;

import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IdentityMapReducer<K, V>
        extends AbstractMapReducer<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>>
        implements SimpleImmutableMinimalMap<K, V> {
    protected IdentityMapReducer(
            final ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>> wrapped,
            final K reductionKey, final V reductionValue) {
        super(wrapped, reductionKey, (a, b) -> a == b, IdentitySetReducer.without(wrapped.keySet(), reductionKey),
                SetReducer.without(wrapped.entrySet(), Pair.of(reductionKey, reductionValue)));
    }

    public static <K, V> IdentityMapReducer<K, V> without(
            final ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>> toWrap,
            final K reductionKey) {
        final V reductionValue = toWrap.get(reductionKey);
        if (null == reductionValue) {
            throw new IllegalArgumentException("Wrapped map doesn't contain reduction key!");
        }
        return new IdentityMapReducer<>(toWrap, reductionKey, reductionValue);
    }

    public static <K, V> IdentityMapReducer<K, V> without(final SimpleImmutableMinimalMap<K, V> toWrap,
            final K reductionKey) {
        return without((ImmutableMinimalMap<K, V, ImmutableMinimalSet<K>, ImmutableMinimalSet<Map.Entry<K, V>>>) toWrap,
                reductionKey);
    }
}