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

import com.google.common.collect.Iterators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.HidingIterator;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;

import java.util.Iterator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SetSubtractor<T, S1 extends ImmutableMinimalSet<T>, S2 extends ImmutableMinimalSet<T>>
        implements ImmutableMinimalSet<T> {
    final S1 wrapped;
    final S2 reductionElements;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = Iterators.size(iterator());

    public static <T> ImmutableMinimalSet<T> without(final ImmutableMinimalSet<T> toWrap,
            final ImmutableMinimalSet<T> reductionElements) {
        return new SetSubtractor<>(toWrap, reductionElements);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean isEmpty() {
        for (final T t : this.wrapped) {
            if (!this.reductionElements.contains(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(final Object o) {
        return !this.reductionElements.contains(o) && this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new HidingIterator<>(this.wrapped, this.reductionElements::contains);
    }
}
