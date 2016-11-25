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
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class ReplacingSet<T> implements ImmutableMinimalSet<T> {
    final ImmutableMinimalSet<T> wrapped;
    final T toReplace;
    final T replacer;
    final BiPredicate<T, T> equals;

    @Override
    public int size() {
        return this.wrapped.size();
    }

    @Override
    public boolean contains(final Object o) {
        if (Objects.equals(this.replacer, o)) return true;
        if (Objects.equals(this.toReplace, o)) return false;
        return this.wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new ReplacingIterator<>(this.wrapped, this.toReplace, this.replacer, this.equals);
    }
}
