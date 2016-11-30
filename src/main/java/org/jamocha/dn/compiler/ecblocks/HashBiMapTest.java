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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class HashBiMapTest {
    static final int UPPER_BOUND = 2;

    public static void main(final String[] args) {
        testRegular();
        testInverseWithCopy();
        testInverse();
    }

    private static void testInverse() {
        System.out.println("HashBiMapTest.testInverse");
        final HashBiMap<Integer, Integer> b2a = HashBiMap.create();
        IntStream.range(0, UPPER_BOUND).forEach(i -> b2a.put(i, 100 + i));
        System.out.println("b2a" + b2a);
        final HashBiMap<Integer, Integer> b2c = HashBiMap.create();
        IntStream.range(0, UPPER_BOUND).forEach(i -> b2c.put(i, 200 + i));
        System.out.println("b2c" + b2c);
        final BiMap<Integer, Integer> a2b = b2a.inverse();
        System.out.println("a2b" + a2b);
        System.out.println("a2c" + Maps.asMap(a2b.keySet(), k -> b2c.get(a2b.get(k))));
        a2b.replaceAll((a, b) -> b2c.get(b));
        System.out.println("a2b'" + a2b);
        System.out.println();
    }

    private static void testInverseWithCopy() {
        System.out.println("HashBiMapTest.testInverseWithCopy");
        final HashBiMap<Integer, Integer> b2a = HashBiMap.create();
        IntStream.range(0, UPPER_BOUND).forEach(i -> b2a.put(i, 100 + i));
        System.out.println("b2a" + b2a);
        final HashBiMap<Integer, Integer> b2c = HashBiMap.create();
        IntStream.range(0, UPPER_BOUND).forEach(i -> b2c.put(i, 200 + i));
        System.out.println("b2c" + b2c);
        final BiMap<Integer, Integer> a2b = b2a.inverse();
        System.out.println("a2b" + a2b);
        final Map<Integer, Integer> a2c = Maps.newHashMap(Maps.asMap(a2b.keySet(), k -> b2c.get(a2b.get(k))));
        System.out.println("a2c" + a2c);
        a2b.putAll(a2c);
        System.out.println("a2b'" + a2b);
        System.out.println();
    }

    private static void testRegular() {
        System.out.println("HashBiMapTest.testRegular");
        final HashBiMap<Integer, Integer> a2b = HashBiMap.create();
        IntStream.range(0, UPPER_BOUND).forEach(i -> a2b.put(100 + i, i));
        System.out.println("a2b" + a2b);
        final HashBiMap<Integer, Integer> b2c = HashBiMap.create();
        IntStream.range(0, UPPER_BOUND).forEach(i -> b2c.put(i, 200 + i));
        System.out.println("b2c" + b2c);
        System.out.println("a2c" + Maps.asMap(a2b.keySet(), k -> b2c.get(a2b.get(k))));
        a2b.replaceAll((a, b) -> b2c.get(b));
        System.out.println("a2b'" + a2b);
        System.out.println();
    }
}
