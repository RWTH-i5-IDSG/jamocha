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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.HidingIterator;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SetReducer<T, S extends ImmutableMinimalSet<T>> implements ImmutableMinimalSet<T> {
    final S wrapped;
    final T reductionElement;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() - 1;

    public static <T> ImmutableMinimalSet<T> without(final ImmutableMinimalSet<T> toWrap, final T reductionElement) {
        if (!toWrap.contains(reductionElement)) return toWrap;
        return new SetReducer<>(toWrap, reductionElement);
    }

    protected static class IndexedSetReducer<T> extends SetReducer<T, IndexedImmutableSet<T>>
            implements IndexedImmutableSet<T> {
        protected IndexedSetReducer(final IndexedImmutableSet<T> wrapped, final T reductionElement) {
            super(wrapped, reductionElement);
        }

        @Override
        public T get(final int index) {
            if (index == this.wrapped.size()) return this.reductionElement;
            return this.wrapped.get(index);
        }
    }

    public static <T> IndexedImmutableSet<T> without(final IndexedImmutableSet<T> toWrap, final T reductionElement) {
        if (!toWrap.contains(reductionElement)) return toWrap;
        return new IndexedSetReducer<>(toWrap, reductionElement);
    }

    public ImmutableMinimalSet<T> without(final T reductionElement) {
        return without(this, reductionElement);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean contains(final Object o) {
        return !Objects.equals(o, this.reductionElement) && this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new HidingIterator<>(this.wrapped, o -> Objects.equals(o, this.reductionElement));
    }
}
