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

package org.jamocha.dn.compiler.ecblocks.extendedcollections;

import com.google.common.collect.Iterators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SetWrapper<T> implements Set<T> {
    final Set<T> wrapped;
    final T additionalElement;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.wrapped.size() + 1;

    public static <T> SetWrapper<T> with(final Set<T> toWrap, final T additionalElement) {
        return new SetWrapper<>(toWrap, additionalElement);
    }

    public SetWrapper<T> with(final T additionalElement) {
        return with(this, additionalElement);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(final Object o) {
        return Objects.equals(o, this.additionalElement) || this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(Iterators.singletonIterator(this.additionalElement), this.wrapped.iterator());
    }

    @Override
    public Object[] toArray() {
        return new ArrayList<>(this).toArray();
    }

    @Override
    public <X> X[] toArray(final X[] a) {
        return new ArrayList<>(this).toArray(a);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean add(final T t) {
        throw new UnsupportedOperationException("SetWrapper is immutable!");
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("SetWrapper is immutable!");
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        throw new UnsupportedOperationException("SetWrapper is immutable!");
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException("SetWrapper is immutable!");
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("SetWrapper is immutable!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("SetWrapper is immutable!");
    }
}
