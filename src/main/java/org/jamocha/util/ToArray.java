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

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
public class ToArray {
    public static <T> T[] toArray(final Collection<T> list, final IntFunction<T[]> gen) {
        return list.toArray(gen.apply(list.size()));
    }

    public static <T> T[] toArray(final Stream<T> stream, final IntFunction<T[]> gen) {
        return stream.toArray(gen);
    }
}
