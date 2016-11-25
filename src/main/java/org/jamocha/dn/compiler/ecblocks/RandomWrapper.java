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

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedSet;

import java.util.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class RandomWrapper {
    final Random random;

    public boolean decide(final int threshold) {
        assert 0 <= threshold && threshold <= 100;
        return this.random.nextInt(100) < threshold;
    }

    public <T, C extends RandomAccess & List<T>> T choose(final C collection) {
        return collection.get(this.random.nextInt(collection.size()));
    }

    public <T> T choose(final T[] array) {
        return array[this.random.nextInt(array.length)];
    }

    public <T> T choose(final IndexedImmutableSet<T> indexedSet) {
        return indexedSet.get(this.random);
    }

    public <T, C extends RandomAccess & List<T>> C shuffle(final C collection) {
        Collections.shuffle(collection, this.random);
        return collection;
    }

    public <T> T[] shuffle(final T[] array) {
        // Durstenfeld shuffle https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
        for (int i = array.length - 1; i > 0; i--) {
            final int index = this.random.nextInt(i + 1);
            final T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
        return array;
    }

    public <T, C extends Collection<T>> ArrayList<T> shuffledCopy(final C collection) {
        final ArrayList<T> shuffled = new ArrayList<>(collection);
        Collections.shuffle(shuffled, this.random);
        return shuffled;
    }
}
