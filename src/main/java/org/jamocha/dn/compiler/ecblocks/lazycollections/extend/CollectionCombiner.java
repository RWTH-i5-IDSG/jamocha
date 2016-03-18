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

import com.google.common.collect.Iterators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionCombiner<T> extends ExtendedCollection<T> {
    final Collection<T> wrapped;
    final Collection<T> additionalElements;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + this.additionalElements.size();

    public static <T> CollectionCombiner<T> with(final Collection<T> toWrap,
            final Collection<T> additionalElements) {
        if (!Collections.disjoint(toWrap, additionalElements)) {
            throw new IllegalArgumentException("Collections not disjoint!");
        }
        return new CollectionCombiner<>(toWrap, additionalElements);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean contains(final Object o) {
        return this.additionalElements.contains(o) || this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(this.additionalElements.iterator(), this.wrapped.iterator());
    }
}
