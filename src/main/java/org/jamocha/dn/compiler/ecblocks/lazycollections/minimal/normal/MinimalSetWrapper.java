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

package org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SetAsMinimalSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class MinimalSetWrapper<E> implements SetAsMinimalSet<E> {
    private final Set<E> wrapped;

    public static <E> SetAsMinimalSet<E> newIdentityHashSet() {
        return new MinimalSetWrapper<>(Sets.newIdentityHashSet());
    }

    @Override
    public String toString() {
        return this.wrapped.toString();
    }

    @Override
    public int size() {
        return this.wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return this.wrapped.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return this.wrapped.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.wrapped.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return this.wrapped.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        return this.wrapped.add(e);
    }

    @Override
    public boolean remove(final Object o) {
        return this.wrapped.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return this.wrapped.addAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.wrapped.retainAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.wrapped.removeAll(c);
    }

    @Override
    public void clear() {
        this.wrapped.clear();
    }

    @Override
    public boolean equals(final Object o) {
        return this.wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public Spliterator<E> spliterator() {
        return this.wrapped.spliterator();
    }

    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        return this.wrapped.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return this.wrapped.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return this.wrapped.parallelStream();
    }

    @Override
    public void forEach(final Consumer<? super E> action) {
        this.wrapped.forEach(action);
    }
}
