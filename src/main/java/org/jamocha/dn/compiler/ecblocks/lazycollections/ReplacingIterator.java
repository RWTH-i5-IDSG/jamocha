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

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class ReplacingIterator<T> implements Iterator<T> {
    final Iterator<T> wrapped;
    final T toReplace;
    final T replacer;
    final BiPredicate<T, T> equals;

    public ReplacingIterator(final Iterable<T> wrapped, final T toReplace, final T replacer,
            final BiPredicate<T, T> equals) {
        this(wrapped.iterator(), toReplace, replacer, equals);
    }

    public static <T> ReplacingIterator<T> usingReferentialEquality(final Iterator<T> wrapped, final T toReplace,
            final T replacer) {
        return new ReplacingIterator<>(wrapped, toReplace, replacer, (a, b) -> a == b);
    }

    public static <T> ReplacingIterator<T> usingEquals(final Iterator<T> wrapped, final T toReplace, final T replacer) {
        return new ReplacingIterator<>(wrapped, toReplace, replacer, Objects::equals);
    }

    @Override
    public boolean hasNext() {
        return this.wrapped.hasNext();
    }

    @Override
    public T next() {
        final T next = this.wrapped.next();
        if (this.equals.test(next, this.toReplace)) {
            return this.replacer;
        }
        return next;
    }
}
