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

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SetExtender<T> extends ExtendedCollection<T> implements Set<T> {
    final Set<T> wrapped;
    final T additionalElement;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    public static <T> Set<T> with(final Set<T> toWrap, final T additionalElement) {
        if (toWrap.contains(additionalElement)) return toWrap;
        return new SetExtender<>(toWrap, additionalElement);
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
