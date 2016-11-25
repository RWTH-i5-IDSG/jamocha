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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.SetSubtractor;

import java.util.Iterator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SetCombiner<T, S1 extends ImmutableMinimalSet<T>, S2 extends ImmutableMinimalSet<T>>
        implements ImmutableMinimalSet<T> {
    final S1 set1;
    final S2 set2;

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final ImmutableMinimalSet<T> set2minus1 = SetSubtractor.without(this.set1, this.set2);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final int size = this.set1.size() + getSet2minus1().size();

    public static <T> ImmutableMinimalSet<T> with(final ImmutableMinimalSet<T> toWrap,
            final ImmutableMinimalSet<T> additionalElements) {
        return new SetCombiner<>(toWrap, additionalElements);
    }

    @Override
    public boolean isEmpty() {
        return this.set1.isEmpty() && this.set2.isEmpty();
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean contains(final Object o) {
        return this.set1.contains(o) || this.set2.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(this.set1.iterator(), getSet2minus1().iterator());
    }
}
