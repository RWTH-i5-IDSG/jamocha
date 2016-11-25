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

package org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.MinimalSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class IndexedSet<E, S extends Map<E, Integer>> implements IndexedImmutableSet<E>, MinimalSet<E>, RandomAccess {
    private final ArrayList<E> list;
    private final S map;

    public IndexedSet(@Nonnull final S map) {
        this.map = map;
        this.list = new ArrayList<>();
    }

    public IndexedSet(@Nonnull final S map, @Nonnull final Iterable<E> iterable) {
        this.map = map;
        this.list = Lists.newArrayList(iterable);
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
    public boolean contains(@Nullable final Object o) {
        return this.map.keySet().contains(o);
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

    public boolean addAll(final Iterable<E> es) {
        boolean changed = false;
        for (final E e : es) {
            changed |= add(e);
        }
        return changed;
    }

    public boolean remove(@Nullable final E e) {
        if (!this.map.containsKey(e)) {
            return false;
        }
        final E replacer = this.list.remove(this.list.size() - 1);
        final Integer indexToReplace = this.map.remove(e);
        this.map.put(replacer, indexToReplace);
        this.list.set(indexToReplace, replacer);
        return true;
    }

    public boolean removeAll(final Iterable<E> es) {
        boolean changed = false;
        for (final E e : es) {
            changed |= remove(e);
        }
        return changed;
    }

    @Override
    @Nonnull
    public Iterator<E> iterator() {
        return Iterators.unmodifiableIterator(this.list.iterator());
    }

    @Override
    public E get(final int index) {
        return this.list.get(index);
    }

    Integer getInternalPosition(final Object element) {
        return this.map.get(element);
    }
}
