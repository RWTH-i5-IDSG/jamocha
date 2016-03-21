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
package org.jamocha.dn.compiler.ecblocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString(of = {"subSets"})
public class Partition<T, S extends Partition.SubSet<T>> {
    @RequiredArgsConstructor
    @Getter
    @ToString
    static class SubSet<T> {
        protected final IdentityHashMap<RowIdentifier, T> elements;

        SubSet(final SubSet<T> copy) {
            this(new IdentityHashMap<>(copy.elements));
        }

        SubSet(final Map<RowIdentifier, ? extends T> elements) {
            this(new IdentityHashMap<>(elements));
        }

        public T get(final RowIdentifier rule) {
            return this.elements.get(rule);
        }
    }

    protected final Set<S> subSets = new HashSet<>();
    protected final HashMap<T, S> lookup = new HashMap<>();

    public Partition(final Partition<T, S> copy, final Function<S, S> copyCtor) {
        copy.subSets.stream().map(copyCtor).forEach(this.subSets::add);
        for (final S s : this.subSets) {
            for (final T t : s.elements.values()) {
                this.lookup.put(t, s);
            }
        }
    }

    public void add(final S newSubSet) {
        assert !newSubSet.elements.values().contains(null);
        assert this.subSets.stream().allMatch(ss -> ss.getElements().keySet().equals(newSubSet.elements.keySet()));
        assert this.subSets.stream()
                .allMatch(ss -> Collections.disjoint(ss.getElements().values(), newSubSet.elements.values()));
        this.subSets.add(newSubSet);
        for (final T newElement : newSubSet.elements.values()) {
            this.lookup.put(newElement, newSubSet);
        }
    }

    public void extend(final RowIdentifier row, final IdentityHashMap<S, T> extension) {
        assert !extension.containsValue(null);
        for (final S subset : this.subSets) {
            final T newElement = extension.get(subset);
            assert null != newElement;
            subset.elements.put(row, newElement);
        }
    }

    public S lookup(final T element) {
        return this.lookup.get(element);
    }

    public void remove(final RowIdentifier row) {
        for (final S s : this.subSets) {
            s.elements.remove(row);
        }
    }

    public boolean remove(final S s) {
        s.elements.keySet().forEach(this.lookup::remove);
        return this.subSets.remove(s);
    }
}
