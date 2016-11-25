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

package org.jamocha.util;

import com.google.common.collect.Iterators;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IndexedHashSet<E> implements Set<E>, RandomAccess {

    private final ArrayList<E> list;
    private final HashMap<E, Integer> map = new HashMap<>();

    public IndexedHashSet(@Nonnull final Collection<E> coll) {
        this.list = new ArrayList<>(coll);
        for (int i = 0; i < this.list.size(); i++) {
            final E indexedElement = this.list.get(i);
            this.map.put(indexedElement, i);
        }
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(@Nullable final Object o) {
        return this.map.keySet().contains(o);
    }

    @Override
    @Nonnull
    public Iterator<E> iterator() {
        return Iterators.unmodifiableIterator(this.list.iterator());
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    @Nonnull
    public <T> T[] toArray(@Nonnull final T[] a) {
        return this.list.toArray(a);
    }

    @Override
    public boolean add(@Nullable final E e) {
        if (this.map.containsKey(e)) {
            return false;
        }
        this.map.put(e, this.list.size());
        this.list.add(e);
        return true;
    }

    public E removeAt(final int index) {
        final int lastValidIndex = this.list.size() - 1;
        if (index > lastValidIndex) {
            return null;
        }
        final E remove = this.list.get(index);
        this.map.remove(remove);
        final E replacer = this.list.remove(lastValidIndex);
        if (index < lastValidIndex) {
            this.map.put(replacer, index);
            this.list.set(index, replacer);
        }
        return remove;
    }

    @Override
    public boolean remove(@Nullable final Object o) {
        final Integer index = this.map.get(o);
        if (null == index) {
            return false;
        }
        removeAt(index);
        return true;
    }

    @Override
    public boolean containsAll(@Nonnull final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(@Nonnull final Collection<? extends E> c) {
        boolean changed = false;
        for (final E e : c) {
            changed |= add(e);
        }
        return changed;
    }

    @Override
    public boolean retainAll(@Nonnull final Collection<?> c) {
        boolean changed = false;
        final Set<?> toRetain = c instanceof Set ? (Set<?>) c : new HashSet<>(c);
        for (int i = size() - 1; i >= 0; --i) {
            final E e = this.list.get(i);
            if (!toRetain.contains(e)) {
                removeAt(i);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(@Nonnull final Collection<?> c) {
        boolean changed = false;
        for (final Object e : c) {
            changed |= remove(e);
        }
        return changed;
    }

    @Override
    public void clear() {
        this.map.clear();
        this.list.clear();
    }

    public E get(@Nonnull final Random random) {
        return this.list.get(random.nextInt(size()));
    }
}
