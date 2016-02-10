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

package test.jamocha.filter;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.*;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.junit.BeforeClass;
import org.junit.Test;
import test.jamocha.util.Slots;
import test.jamocha.util.builder.fwa.PathFunctionBuilder;
import test.jamocha.util.builder.fwa.PathPredicateBuilder;

import static org.junit.Assert.*;
import static test.jamocha.util.CounterColumnMatcherMockup.COUNTER_COLUMN_MATCHER_MOCKUP;

public class FilterEqualsInFunctionTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FunctionDictionary.load();
    }

    @Test
    public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueEquality() {
        final Path p1 = new Path(Slots.DOUBLE);
        final Path p2 = new Path(Slots.DOUBLE);
        final Path p3 = new Path(Slots.DOUBLE);
        final SlotAddress a1 = new SlotAddress(0);
        final SlotAddress a2 = new SlotAddress(0);
        final SlotAddress a3 = new SlotAddress(0);
        final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
        final PathFilter f, g;

        f = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        g = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        final PathNodeFilterSet pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f), pg =
                PathNodeFilterSet.newRegularPathNodeFilterSet(g);

        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP)));
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP)));
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP)));
    }

    @Test
    public void testEqualsInFunctionFalseDifferentSlotAddress() {
        final Path p1 = new Path(
                MemoryFactory.getMemoryFactory().newTemplate("", "", Slots.newDouble("s1"), Slots.newDouble("s2")));
        final Path p2 = new Path(Slots.DOUBLE);
        final Path p3 = new Path(Slots.DOUBLE);
        final SlotAddress a1 = new SlotAddress(0);
        final SlotAddress a2 = new SlotAddress(0);
        final SlotAddress a3 = new SlotAddress(0);
        final SlotAddress a4 = new SlotAddress(1);
        final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
        final PathFilter f, g;

        f = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        g = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a4).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        final PathNodeFilterSet pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f), pg =
                PathNodeFilterSet.newRegularPathNodeFilterSet(g);

        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP)));
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP)));
        assertFalse(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP)));
        assertFalse(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP)));
    }

    @Test
    public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueNormalize() {
        final Path p1 = new Path(Slots.DOUBLE);
        final Path p2 = new Path(Slots.DOUBLE);
        final Path p3 = new Path(Slots.DOUBLE);
        final SlotAddress a1 = new SlotAddress(0);
        final SlotAddress a2 = new SlotAddress(0);
        final SlotAddress a3 = new SlotAddress(0);
        final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
        final PathFilter f, g, h;
        final PathNodeFilterSet pf, pg, ph;

        f = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        g = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build()).buildFilter();
        pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f);
        pg = PathNodeFilterSet.newRegularPathNodeFilterSet(g);
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP)
                                .getNormalisedVersion()));
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP)
                                .getNormalisedVersion()));
        h = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
                .addFunction(new PathFunctionBuilder(plus).addPath(p2, a2).addPath(p1, a1).build()).buildFilter();
        ph = PathNodeFilterSet.newRegularPathNodeFilterSet(h);
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(ph, COUNTER_COLUMN_MATCHER_MOCKUP)
                                .getNormalisedVersion()));
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(ph, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP)
                                .getNormalisedVersion()));
    }

    @Test
    public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueDifferentPath() {
        final Path p1 = new Path(Slots.DOUBLE);
        final Path p2 = new Path(Slots.DOUBLE);
        final Path p3 = new Path(Slots.DOUBLE);
        final Path p4 = new Path(Slots.DOUBLE);
        final SlotAddress a1 = new SlotAddress(0);
        final SlotAddress a2 = new SlotAddress(0);
        final SlotAddress a3 = new SlotAddress(0);
        final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
        final PathFilter f, g;
        final PathNodeFilterSet pf, pg;

        f = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        g = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p4, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f);
        pg = PathNodeFilterSet.newRegularPathNodeFilterSet(g);
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP)));
        assertTrue(FilterFunctionCompare
                .equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, COUNTER_COLUMN_MATCHER_MOCKUP),
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(pf, COUNTER_COLUMN_MATCHER_MOCKUP)));
    }

    @Test
    public void testFilterEqualsInFunction() {
        final Path p1 = new Path(
                MemoryFactory.getMemoryFactory().newTemplate("", "", Slots.newString("s1"), Slots.newLong("s2")));
        final Path p2 = new Path(Slots.LONG);
        final Path p3 = new Path(Slots.LONG);
        final SlotAddress a1 = new SlotAddress(0);
        final SlotAddress a3 = new SlotAddress(0);
        final SlotAddress a4 = new SlotAddress(0);
        final SlotAddress a5 = new SlotAddress(1);
        final PathFilter f, g, h, i, j, k, l;
        final Function<?> plusL = FunctionDictionary.lookup("+", SlotType.LONG, SlotType.LONG);
        final Predicate lessL = FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
        final Predicate eqS = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
        f = new PathPredicateBuilder(eqS).addString("Max Mustermann").addPath(p1, a1).buildFilter();
        g = new PathPredicateBuilder(lessL).addLong(18L).addPath(p1, a5).buildFilter();
        h = new PathPredicateBuilder(lessL).addLong(50000)
                .addFunction(new PathFunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build()).buildFilter();
        i = new PathPredicateBuilder(eqS).addString("Max Mustermann").addPath(p1, a1).buildFilter();
        j = new PathPredicateBuilder(lessL).addLong(18L).addPath(p1, a5).buildFilter();
        k = new PathPredicateBuilder(lessL).addLong(50000)
                .addFunction(new PathFunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build()).buildFilter();
        final PathNodeFilterSet a = PathNodeFilterSet.newRegularPathNodeFilterSet(f, g, h);
        {
            final PathNodeFilterSet b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, g, h);
            assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(a, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, COUNTER_COLUMN_MATCHER_MOCKUP)));
            assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(b, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, COUNTER_COLUMN_MATCHER_MOCKUP)));
        }
        {
            final PathNodeFilterSet b = PathNodeFilterSet.newRegularPathNodeFilterSet(i, j, k);
            assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(a, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, COUNTER_COLUMN_MATCHER_MOCKUP)));
            assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(b, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, COUNTER_COLUMN_MATCHER_MOCKUP)));
        }
        {
            final PathNodeFilterSet b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, j, h);
            assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(a, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, COUNTER_COLUMN_MATCHER_MOCKUP)));
            assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(b, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, COUNTER_COLUMN_MATCHER_MOCKUP)));
        }
        l = new PathPredicateBuilder(lessL).addLong(17L).addPath(p1, a5).buildFilter();
        {
            final PathNodeFilterSet b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, l, h);
            assertFalse(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(a, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, COUNTER_COLUMN_MATCHER_MOCKUP)));
            assertFalse(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(b, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, COUNTER_COLUMN_MATCHER_MOCKUP)));
        }
        {
            final PathNodeFilterSet b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, l, h);
            assertFalse(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(a, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, COUNTER_COLUMN_MATCHER_MOCKUP)));
            assertFalse(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                            .translate(b, COUNTER_COLUMN_MATCHER_MOCKUP),
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, COUNTER_COLUMN_MATCHER_MOCKUP)));
        }
    }
}
