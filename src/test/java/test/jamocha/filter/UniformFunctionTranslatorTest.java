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
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.*;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.junit.*;
import test.jamocha.util.Slots;
import test.jamocha.util.builder.fwa.PathFunctionBuilder;
import test.jamocha.util.builder.fwa.PathPredicateBuilder;

import static org.junit.Assert.*;
import static test.jamocha.util.CounterColumnMatcherMockup.counterColumnMatcherMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class UniformFunctionTranslatorTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FunctionDictionary.load();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryMinusLong() {
        // -(a,b) -> +(a,(-b))
        final Function<Long> minusL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS, SlotType.LONG);
        final Function<Long> minusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Minus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> plusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(5L)
                        .addFunction(new PathFunctionBuilder(minusLL).addLong(6L).addLong(1L).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(new PathFunctionBuilder(plusLL).addLong(6)
                        .addFunction(new PathFunctionBuilder(minusL).addLong(1).build()).build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryMinusDouble() {
        // -(a,b) -> +(a,(-b))
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Function<Double> minusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Minus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(5.)
                        .addFunction(new PathFunctionBuilder(minusDD).addDouble(6.).addDouble(1.).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addFunction(
                        new PathFunctionBuilder(plusDD).addDouble(6.)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(1.).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateUnaryMinusLong() {
        // -(-(a)) -> a
        final Function<Long> minusL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(
                        new PathFunctionBuilder(minusL).addFunction(new PathFunctionBuilder(minusL).addLong(5L).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathPredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateUnaryMinusDouble() {
        // -(-(a)) -> a
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(5.).addFunction(
                        new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addDouble(5.).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateDividedByLong() {
        // /(a,b) -> *(a,1/b)
        final Function<Long> divLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.DividedBy.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> divL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                        SlotType.LONG);
        final Function<Long> timesLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(5L)
                        .addFunction(new PathFunctionBuilder(divLL).addLong(7).addLong(5).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(new PathFunctionBuilder(timesLL).addLong(7)
                        .addFunction(new PathFunctionBuilder(divL).addLong(5).build()).build()).buildFilter());
        assertFalse(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateDividedByDouble() {
        // /(a,b) -> *(a,1/b)
        final Function<Double> divDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.DividedBy.IN_CLIPS,
                        SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> divD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                        SlotType.DOUBLE);
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(5.)
                        .addFunction(new PathFunctionBuilder(divDD).addDouble(7.).addDouble(5.).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addFunction(
                        new PathFunctionBuilder(timesDD).addDouble(7.)
                                .addFunction(new PathFunctionBuilder(divD).addDouble(5.).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateTimesInverseLong() {
        // 1/(1/a) -> a
        final Function<Long> divL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(
                        new PathFunctionBuilder(divL).addFunction(new PathFunctionBuilder(divL).addLong(5L).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathPredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildFilter());
        assertFalse(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateTimesInverseDouble() {
        // 1/(1/a) -> a
        final Function<Double> divD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(5.).addFunction(
                        new PathFunctionBuilder(divD).addFunction(new PathFunctionBuilder(divD).addDouble(5.).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addDouble(5.).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryPlusLeftExpandLong() {
        // +(+(a,b),c) -> +(a,b,c)
        final Function<Long> plusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> plusLLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(12L).addFunction(
                        new PathFunctionBuilder(plusLL)
                                .addFunction(new PathFunctionBuilder(plusLL).addLong(5L).addLong(4L).build())
                                .addLong(3L).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(12L)
                        .addFunction(new PathFunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryPlusLeftExpandDouble() {
        // +(+(a,b),c) -> +(a,b,c)
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(12.).addFunction(
                        new PathFunctionBuilder(plusDD)
                                .addFunction(new PathFunctionBuilder(plusDD).addDouble(5.).addDouble(4.).build())
                                .addDouble(3.).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(12.)
                        .addFunction(new PathFunctionBuilder(plusDDD).addDouble(5.).addDouble(4.).addDouble(3.).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryPlusRightExpandLong() {
        // +(a,+(b,c)) -> +(a,b,c)
        final Function<Long> plusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> plusLLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(12L).addFunction(
                        new PathFunctionBuilder(plusLL).addLong(5L)
                                .addFunction(new PathFunctionBuilder(plusLL).addLong(4L).addLong(3L).build()).build())
                        .build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(12L)
                        .addFunction(new PathFunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryPlusRightExpandDouble() {
        // +(a,+(b,c)) -> +(a,b,c)
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(12.).addFunction(
                        new PathFunctionBuilder(plusDD).addDouble(5.)
                                .addFunction(new PathFunctionBuilder(plusDD).addDouble(4.).addDouble(3.).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(12.)
                        .addFunction(new PathFunctionBuilder(plusDDD).addDouble(5.).addDouble(4.).addDouble(3.).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryPlusDoubleExpandLong() {
        // +(+(a,b),+(c,d)) -> +(a,b,c,d)
        final Function<Long> plusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> plusLLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG, SlotType.LONG, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(10L).addFunction(
                        new PathFunctionBuilder(plusLL)
                                .addFunction(new PathFunctionBuilder(plusLL).addLong(1L).addLong(2L).build())
                                .addFunction(new PathFunctionBuilder(plusLL).addLong(3L).addLong(4L).build()).build())
                        .build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(10L).addFunction(
                        new PathFunctionBuilder(plusLLL).addLong(1L).addLong(2L).addLong(3L).addLong(4L).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryPlusDoubleExpandDouble() {
        // +(+(a,b),+(c,d)) -> +(a,b,c,d)
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(10.).addFunction(
                        new PathFunctionBuilder(plusDD)
                                .addFunction(new PathFunctionBuilder(plusDD).addDouble(1.).addDouble(2.).build())
                                .addFunction(new PathFunctionBuilder(plusDD).addDouble(3.).addDouble(4.).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(10.).addFunction(
                        new PathFunctionBuilder(plusDDD).addDouble(1.).addDouble(2.).addDouble(3.).addDouble(4.)
                                .build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesDistributePlusLong() {
        // *(+(a,b),c) -> +(*(a,c),*(b,c))
        final Function<Long> plusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> timesLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(35L).addFunction(
                        new PathFunctionBuilder(timesLL)
                                .addFunction(new PathFunctionBuilder(plusLL).addLong(3L).addLong(4L).build())
                                .addLong(5L).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(35L).addFunction(new PathFunctionBuilder(plusLL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(3L).addLong(5L).build())
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(4L).addLong(5L).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesDistributePlusDouble() {
        // *(+(a,b),c) -> +(*(a,c),*(b,c))
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(35.).addFunction(
                        new PathFunctionBuilder(timesDD)
                                .addFunction(new PathFunctionBuilder(plusDD).addDouble(3.).addDouble(4.).build())
                                .addDouble(5.).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(35.).addFunction(new PathFunctionBuilder(plusDD)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(3.).addDouble(5.).build())
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(4.).addDouble(5.).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesShiftUnaryMinusLeftArgLong() {
        // *(-(a),b) -> -(*(a,b))
        final Function<Long> timesLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> minusL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(-10L).addFunction(
                        new PathFunctionBuilder(timesLL)
                                .addFunction(new PathFunctionBuilder(minusL).addLong(2L).build()).addLong(5L).build())
                        .build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(-10L).addFunction(new PathFunctionBuilder(minusL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(2L).addLong(5L).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesShiftUnaryMinusLeftArgDouble() {
        // *(-(a),b) -> -(*(a,b))
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(-10.).addFunction(
                        new PathFunctionBuilder(timesDD)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(2.).build()).addDouble(5.)
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(-10.).addFunction(new PathFunctionBuilder(minusD)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesShiftUnaryMinusRightArgLong() {
        // *(a,-(b)) -> -(*(a,b))
        final Function<Long> timesLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> minusL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(-10L).addFunction(
                        new PathFunctionBuilder(timesLL).addLong(2L)
                                .addFunction(new PathFunctionBuilder(minusL).addLong(5L).build()).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(-10L).addFunction(new PathFunctionBuilder(minusL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(2L).addLong(5L).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesShiftUnaryMinusRightArgDouble() {
        // *(a,-(b)) -> -(*(a,b))
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(-10.).addFunction(
                        new PathFunctionBuilder(timesDD).addDouble(2.)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(-10.).addFunction(new PathFunctionBuilder(minusD)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build()).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesShiftUnaryMinusBothArgsLong() {
        // *(-(a),(-b)) -> *(a,b)
        final Function<Long> timesLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Function<Long> minusL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsLL).addLong(10L).addFunction(
                        new PathFunctionBuilder(timesLL)
                                .addFunction(new PathFunctionBuilder(minusL).addLong(2L).build())
                                .addFunction(new PathFunctionBuilder(minusL).addLong(5L).build()).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(10L)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(2L).addLong(5L).build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateBinaryTimesShiftUnaryMinusBothArgsDouble() {
        // *(-(a),(-b)) -> *(a,b)
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(10.).addFunction(
                        new PathFunctionBuilder(timesDD)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(2.).build())
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(10.)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build())
                        .buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateGreaterToLessLong() {
        // >(a,b) -> <(b,a)
        final Predicate greaterLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Greater.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate lessLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator
                        .translate(new PathPredicateBuilder(greaterLL).addLong(20L).addLong(10L).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathPredicateBuilder(lessLL).addLong(10L).addLong(20L).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateGreaterToLessDouble() {
        // >(a,b) -> <(b,a)
        final Predicate greaterDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Greater.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate lessDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.DOUBLE, SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator
                        .translate(new PathPredicateBuilder(greaterDD).addDouble(20.).addDouble(10.).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(lessDD).addDouble(10.).addDouble(20.).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateLeqToNLessLong() {
        // <=(a,b) -> !(<(b,a))
        final Predicate leqLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.LessOrEqual.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate lessLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate notB =
                FunctionDictionary.lookupPredicate(org.jamocha.function.impls.predicates.Not.IN_CLIPS, SlotType.BOOLEAN);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator
                        .translate(new PathPredicateBuilder(leqLL).addLong(10L).addLong(20L).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateLeqToNLessDouble() {
        // <=(a,b) -> !(<(b,a))
        final Predicate leqDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.LessOrEqual.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate lessDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate notB =
                FunctionDictionary.lookupPredicate(org.jamocha.function.impls.predicates.Not.IN_CLIPS, SlotType.BOOLEAN);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator
                        .translate(new PathPredicateBuilder(leqDD).addDouble(10.).addDouble(20.).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateGeqToNLessLong() {
        // >=(a,b) -> !(<(a,b))
        final Predicate geqLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.GreaterOrEqual.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate lessLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate notB =
                FunctionDictionary.lookupPredicate(org.jamocha.function.impls.predicates.Not.IN_CLIPS, SlotType.BOOLEAN);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator
                        .translate(new PathPredicateBuilder(geqLL).addLong(20L).addLong(10L).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateGeqToNLessDouble() {
        // >=(a,b) -> !(<(a,b))
        final Predicate geqDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.GreaterOrEqual.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate lessDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate notB =
                FunctionDictionary.lookupPredicate(org.jamocha.function.impls.predicates.Not.IN_CLIPS, SlotType.BOOLEAN);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator
                        .translate(new PathPredicateBuilder(geqDD).addDouble(20.).addDouble(10.).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build()).buildFilter());
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup)));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateCombinationDouble() {
        // *(+(-(a),b),-(c,d))
        // -> +( *(-a,c), *(a,d), *(b,c), *(b,-d))
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> minusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Minus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addDouble(12.).addFunction(
                        new PathFunctionBuilder(timesDD).addFunction(new PathFunctionBuilder(plusDD)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).addDouble(7.)
                                .build())
                                .addFunction(new PathFunctionBuilder(minusDD).addDouble(9).addDouble(3).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(12.).addFunction(new PathFunctionBuilder(plusDDDD)
                        .addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(timesDD).addDouble(5.).addDouble(9.).build())
                                .build())
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(5.).addDouble(3.).build())
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(7.).addDouble(9.).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDD).addDouble(7.).addDouble(3.).build()).build())
                        .build()).buildFilter());
        final AddressNodeFilterSet translate =
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup);
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(compare, counterColumnMatcherMockup), translate));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateCombinationPathsDouble() {
        // *(+(-(a),b),-(c,d))
        // -> +( *(-a,c), *(a,d), *(b,c), *(b,-d))
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> minusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Minus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Function<Double> timesDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Template template = Slots.DOUBLE;
        final Path a = new Path(template), b = new Path(template), c = new Path(template), d = new Path(template), l =
                new Path(template);
        final SlotAddress s = new SlotAddress(0);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addPath(l, s).addFunction(
                        new PathFunctionBuilder(timesDD).addFunction(new PathFunctionBuilder(plusDD)
                                .addFunction(new PathFunctionBuilder(minusD).addPath(a, s).build()).addPath(b, s)
                                .build())
                                .addFunction(new PathFunctionBuilder(minusDD).addPath(c, s).addPath(d, s).build())
                                .build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addPath(l, s).addFunction(new PathFunctionBuilder(plusDDDD)
                        .addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(timesDD).addPath(a, s).addPath(c, s).build())
                                .build())
                        .addFunction(new PathFunctionBuilder(timesDD).addPath(a, s).addPath(d, s).build())
                        .addFunction(new PathFunctionBuilder(timesDD).addPath(b, s).addPath(c, s).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDD).addPath(b, s).addPath(d, s).build()).build())
                        .build()).buildFilter());
        final AddressNodeFilterSet translate =
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup);
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(compare, counterColumnMatcherMockup), translate));
    }

    /**
     * Test method for
     * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
     * .
     */
    @Test
    public void testTranslateFancyDouble() {
        // *(+(-(a),b),-(c,d),+(e,f,g,*(h,i,j)),k)
        // -> +( *(e,-a,c,k),*(f,-a,c,k),*(g,-a,c,k),*(*(h,i,j),-a,c,k),
        // *(e,a,d,k),*(f,a,d,k),*(g,a,d,k),*(*(h,i,j),a,d,k),
        // *(e,b,c,k),*(f,b,c,k),*(g,b,c,k),*(*(h,i,j),b,c,k),
        // *(e,b,-d,k),*(f,b,-d,k),*(g,b,-d,k),*(*(h,i,j),b,-d,k))
        final Function<Double> plusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> plusDDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> plusD16 =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> minusDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Minus.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Function<Double> timesDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> timesDDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<Double> timesDDDDDD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Template template = Slots.DOUBLE;
        final Path a = new Path(template), b = new Path(template), c = new Path(template), d = new Path(template), e =
                new Path(template), f = new Path(template), g = new Path(template), h = new Path(template), i =
                new Path(template), j = new Path(template), k = new Path(template), l = new Path(template);
        final SlotAddress s = new SlotAddress(0);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                UniformFunctionTranslator.translate(new PathPredicateBuilder(equalsDD).addPath(l, s).addFunction(
                        new PathFunctionBuilder(timesDDDD).addFunction(new PathFunctionBuilder(plusDD)
                                .addFunction(new PathFunctionBuilder(minusD).addPath(a, s).build()).addPath(b, s)
                                .build())
                                .addFunction(new PathFunctionBuilder(minusDD).addPath(c, s).addPath(d, s).build())
                                .addFunction(new PathFunctionBuilder(plusDDDD).addPath(e, s).addPath(f, s).addPath(g, s)
                                        .addFunction(new PathFunctionBuilder(timesDDD).addPath(h, s).addPath(i, s)
                                                .addPath(j, s).build()).build()).addPath(k, s).build()).build())));
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addPath(l, s).addFunction(new PathFunctionBuilder(plusD16)
                        .addFunction(new PathFunctionBuilder(minusD).addFunction(
                                new PathFunctionBuilder(timesDDDD).addPath(a, s).addPath(c, s).addPath(e, s)
                                        .addPath(k, s).build()).build()).addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(
                                        new PathFunctionBuilder(timesDDDD).addPath(a, s).addPath(c, s).addPath(f, s)
                                                .addPath(k, s).build()).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDDDD).addPath(a, s).addPath(c, s).addPath(g, s)
                                                .addPath(k, s).build()).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDDDDDD).addPath(a, s).addPath(c, s).addPath(h, s)
                                                .addPath(i, s).addPath(j, s).addPath(k, s).build()).build())
                        .addFunction(new PathFunctionBuilder(timesDDDD).addPath(a, s).addPath(d, s).addPath(e, s)
                                .addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addPath(a, s).addPath(d, s).addPath(f, s)
                                        .addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addPath(a, s).addPath(d, s).addPath(g, s)
                                        .addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDDDD).addPath(a, s).addPath(d, s).addPath(h, s)
                                        .addPath(i, s).addPath(j, s).addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addPath(b, s).addPath(c, s).addPath(e, s)
                                        .addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addPath(b, s).addPath(c, s).addPath(f, s)
                                        .addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addPath(b, s).addPath(c, s).addPath(g, s)
                                        .addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(timesDDDDDD).addPath(b, s).addPath(c, s).addPath(h, s)
                                        .addPath(i, s).addPath(j, s).addPath(k, s).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDDDD).addPath(b, s).addPath(d, s).addPath(e, s)
                                                .addPath(k, s).build()).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDDDD).addPath(b, s).addPath(d, s).addPath(f, s)
                                                .addPath(k, s).build()).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDDDD).addPath(b, s).addPath(d, s).addPath(g, s)
                                                .addPath(k, s).build()).build()).addFunction(
                                new PathFunctionBuilder(minusD).addFunction(
                                        new PathFunctionBuilder(timesDDDDDD).addPath(b, s).addPath(d, s).addPath(h, s)
                                                .addPath(i, s).addPath(j, s).addPath(k, s).build()).build()).build())
                        .buildFilter());
        final AddressNodeFilterSet translate =
                PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(original, counterColumnMatcherMockup);
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(compare, counterColumnMatcherMockup), translate));
    }
}
