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

package org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal;

import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SetAsMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SimpleImmutableMinimalMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SimpleMinimalHashMap<K, V> extends HashMap<K, V> implements SimpleImmutableMinimalMap<K, V> {
    public SimpleMinimalHashMap() {
    }

    public SimpleMinimalHashMap(final int expectedMaxSize) {
        super(expectedMaxSize);
    }

    public SimpleMinimalHashMap(final Map<? extends K, ? extends V> m) {
        super(m);
    }

    @Nonnull
    @Override
    public SetAsMinimalSet<K> keySet() {
        return new MinimalSetWrapper<>(super.keySet());
    }

    @Override
    public SetAsMinimalSet<Entry<K, V>> entrySet() {
        return new MinimalSetWrapper<>(super.entrySet());
    }
}
