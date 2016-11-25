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

package org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed;

import javax.annotation.Nonnull;
import java.util.IdentityHashMap;
import java.util.stream.Collector;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IndexedIdentityHashSet<E> extends IndexedSet<E, IdentityHashMap<E, Integer>> {
    public IndexedIdentityHashSet() {
        super(new IdentityHashMap<>());
    }

    public IndexedIdentityHashSet(@Nonnull final Iterable<E> iterable) {
        super(new IdentityHashMap<>(), iterable);
    }

    public static <E> Collector<E, IndexedIdentityHashSet<E>, IndexedIdentityHashSet<E>> toIndexedIdentityHashSet() {
        return Collector.of(IndexedIdentityHashSet::new, IndexedSet::add, (set1, set2) -> {
            set2.forEach(set1::add);
            return set1;
        });
    }
}
