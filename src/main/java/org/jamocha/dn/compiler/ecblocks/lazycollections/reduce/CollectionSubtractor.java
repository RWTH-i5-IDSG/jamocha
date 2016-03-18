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

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionSubtractor<T> extends ReducedCollection<T> {
    final Collection<T> wrapped;
    final Collection<T> reductionElements;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() - this.reductionElements.size();

    public static <T> CollectionSubtractor<T> without(final Collection<T> toWrap,
            final Collection<T> reductionElements) {
        if (!toWrap.containsAll(reductionElements)) {
            throw new IllegalArgumentException(
                    "Not all of the elements that are to be hidden are actually in the wrapped collection!");
        }
        return new CollectionSubtractor<>(toWrap, reductionElements);
    }

    @Override
    public int size() {
        return getSize();
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
