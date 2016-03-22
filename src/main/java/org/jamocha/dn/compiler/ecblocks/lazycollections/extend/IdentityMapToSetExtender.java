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

import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public final class IdentityMapToSetExtender<K, V> extends AbstractMapToSetExtender<K, V> {
    private IdentityMapToSetExtender(final Map<K, Set<V>> wrapped, final K additionalKey, final V additionalValue) {
        super(wrapped, additionalKey, additionalValue, (a, b) -> a == b);
    }

    public static <K, V> IdentityMapToSetExtender<K, V> with(final Map<K, Set<V>> toWrap, final K additionalKey,
            final V additionalValue) {
        return new IdentityMapToSetExtender<>(toWrap, additionalKey, additionalValue);
    }
}
