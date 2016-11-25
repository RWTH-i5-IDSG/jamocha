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

import org.jamocha.dn.compiler.ecblocks.lazycollections.HidingIterator;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;

import java.util.Iterator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IdentitySetReducer<T, S extends ImmutableMinimalSet<T>> extends SetReducer<T, S> {
    private IdentitySetReducer(final S toWrap, final T reductionElement) {
        super(toWrap, reductionElement);
    }

    public static <T> ImmutableMinimalSet<T> without(final ImmutableMinimalSet<T> toWrap, final T reductionElement) {
        if (!toWrap.contains(reductionElement)) return toWrap;
        return new IdentitySetReducer<>(toWrap, reductionElement);
    }

    protected static class IndexedIdentitySetReducer<T> extends IdentitySetReducer<T, IndexedImmutableSet<T>>
            implements IndexedImmutableSet<T> {
        protected IndexedIdentitySetReducer(final IndexedImmutableSet<T> wrapped, final T reductionElement) {
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
        return new IndexedIdentitySetReducer<>(toWrap, reductionElement);
    }

    @Override
    public boolean contains(final Object o) {
        return o != this.reductionElement && this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new HidingIterator<>(this.wrapped, o -> o == this.reductionElement);
    }
}
