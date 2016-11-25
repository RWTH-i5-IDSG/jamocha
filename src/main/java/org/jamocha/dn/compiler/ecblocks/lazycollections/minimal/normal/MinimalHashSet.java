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

import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SetAsMinimalSet;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MinimalHashSet<E> extends HashSet<E> implements SetAsMinimalSet<E> {
    public MinimalHashSet() {
        super();
    }

    public MinimalHashSet(final Collection<? extends E> c) {
        super(c);
    }

    public MinimalHashSet(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MinimalHashSet(final int initialCapacity) {
        super(initialCapacity);
    }
}
