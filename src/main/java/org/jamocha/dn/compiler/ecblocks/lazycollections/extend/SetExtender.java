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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SetExtender<T, S extends ImmutableMinimalSet<T>> implements ImmutableMinimalSet<T> {
    final S wrapped;
    final T additionalElement;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    public static <T> ImmutableMinimalSet<T> with(final ImmutableMinimalSet<T> toWrap, final T additionalElement) {
        if (toWrap.contains(additionalElement)) return toWrap;
        return new SetExtender<>(toWrap, additionalElement);
    }

    protected static class IndexedSetExtender<T> extends SetExtender<T, IndexedImmutableSet<T>>
            implements IndexedImmutableSet<T> {
        protected IndexedSetExtender(final IndexedImmutableSet<T> wrapped, final T additionalElement) {
            super(wrapped, additionalElement);
        }

        @Override
        public T get(final int index) {
            if (index == this.wrapped.size()) return this.additionalElement;
            return this.wrapped.get(index);
        }
    }

    public static <T> IndexedImmutableSet<T> with(final IndexedImmutableSet<T> toWrap, final T additionalElement) {
        if (toWrap.contains(additionalElement)) return toWrap;
        return new IndexedSetExtender<>(toWrap, additionalElement);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean contains(final Object o) {
        return Objects.equals(o, this.additionalElement) || this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(Iterators.singletonIterator(this.additionalElement), this.wrapped.iterator());
    }
}
