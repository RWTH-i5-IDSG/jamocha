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

package org.jamocha.dn.compiler.ecblocks.lazycollections;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public abstract class LazyCollection<T> implements Collection<T> {
    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @Override
    public <X> X[] toArray(final X[] a) {
        final ArrayList<T> array = new ArrayList<>(size());
        for (final T t : this) {
            array.add(t);
        }
        return array.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        throw new UnsupportedOperationException("Lazy Collections are immutable!");
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("Lazy Collections are immutable!");
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        throw new UnsupportedOperationException("Lazy Collections are immutable!");
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Lazy Collections are immutable!");
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Lazy Collections are immutable!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Lazy Collections are immutable!");
    }
}
