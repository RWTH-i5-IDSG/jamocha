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
import org.jamocha.filter.*;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.*;
import org.junit.*;
import test.jamocha.util.CounterColumnMatcherMockup;
import test.jamocha.util.builder.fwa.PathFunctionBuilder;
import test.jamocha.util.builder.fwa.PathPredicateBuilder;

import static org.junit.Assert.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FunctionNormaliserTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(5L)
                .addFunction(new PathFunctionBuilder(minusLL).addLong(6L).addLong(1L).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(new PathFunctionBuilder(plusLL).addLong(6)
                        .addFunction(new PathFunctionBuilder(minusL).addLong(1).build()).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(5.)
                .addFunction(new PathFunctionBuilder(minusDD).addDouble(6.).addDouble(1.).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addFunction(
                        new PathFunctionBuilder(plusDD).addDouble(6.)
                                .addFunction(new PathFunctionBuilder(minusD).addDouble(1.).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    @Test
    public void testTranslateUnaryMinusLong() {
        // -(-(a)) -> a
        final Function<Long> minusL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS, SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(5L)
                .addFunction(
                        new PathFunctionBuilder(minusL).addFunction(new PathFunctionBuilder(minusL).addLong(5L).build())
                                .build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathPredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    @Test
    public void testTranslateUnaryMinusDouble() {
        // -(-(a)) -> a
        final Function<Double> minusD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.UnaryMinus.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(5.)
                .addFunction(new PathFunctionBuilder(minusD)
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addDouble(5.).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(5L)
                .addFunction(new PathFunctionBuilder(divLL).addLong(7).addLong(5).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(new PathFunctionBuilder(timesLL).addLong(7)
                        .addFunction(new PathFunctionBuilder(divL).addLong(5).build()).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(5.)
                .addFunction(new PathFunctionBuilder(divDD).addDouble(7.).addDouble(5.).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addFunction(
                        new PathFunctionBuilder(timesDD).addDouble(7.)
                                .addFunction(new PathFunctionBuilder(divD).addDouble(5.).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    @Test(expected = java.lang.ArithmeticException.class)
    public void testTranslateTimesInverseLong() {
        // 1/(1/a) -> a
        final Function<Long> divL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                new PathPredicateBuilder(equalsLL).addLong(5L).addFunction(
                        new PathFunctionBuilder(divL).addFunction(new PathFunctionBuilder(divL).addLong(5L).build())
                                .build()).build()));
        final PathNodeFilterSet compare = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathPredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildFilter());
        assertNotSame(evalFirstFE(original.normalise()), evalFirstFE(compare.normalise()));

    }

    @Test
    public void testTranslateTimesInverseDouble() {
        // 1/(1/a) -> a
        final Function<Double> divD =
                FunctionDictionary.<Double>lookup(org.jamocha.function.impls.functions.TimesInverse.IN_CLIPS,
                        SlotType.DOUBLE);
        final Predicate equalsDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(5.)
                .addFunction(
                        new PathFunctionBuilder(divD).addFunction(new PathFunctionBuilder(divD).addDouble(5.).build())
                                .build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(5.).addDouble(5.).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(12L)
                .addFunction(new PathFunctionBuilder(plusLL)
                        .addFunction(new PathFunctionBuilder(plusLL).addLong(5L).addLong(4L).build()).addLong(3L)
                        .build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(12L)
                        .addFunction(new PathFunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(12.)
                .addFunction(new PathFunctionBuilder(plusDD)
                        .addFunction(new PathFunctionBuilder(plusDD).addDouble(5.).addDouble(4.).build()).addDouble(3.)
                        .build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(12.)
                        .addFunction(new PathFunctionBuilder(plusDDD).addDouble(5.).addDouble(4.).addDouble(3.).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(12L)
                .addFunction(new PathFunctionBuilder(plusLL).addLong(5L)
                        .addFunction(new PathFunctionBuilder(plusLL).addLong(4L).addLong(3L).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(12L)
                        .addFunction(new PathFunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(12.)
                .addFunction(new PathFunctionBuilder(plusDD).addDouble(5.)
                        .addFunction(new PathFunctionBuilder(plusDD).addDouble(4.).addDouble(3.).build()).build())
                .build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(12.)
                        .addFunction(new PathFunctionBuilder(plusDDD).addDouble(5.).addDouble(4.).addDouble(3.).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(10L)
                .addFunction(new PathFunctionBuilder(plusLL)
                        .addFunction(new PathFunctionBuilder(plusLL).addLong(1L).addLong(2L).build())
                        .addFunction(new PathFunctionBuilder(plusLL).addLong(3L).addLong(4L).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(10L).addFunction(
                        new PathFunctionBuilder(plusLLL).addLong(1L).addLong(2L).addLong(3L).addLong(4L).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(10.)
                .addFunction(new PathFunctionBuilder(plusDD)
                        .addFunction(new PathFunctionBuilder(plusDD).addDouble(1.).addDouble(2.).build())
                        .addFunction(new PathFunctionBuilder(plusDD).addDouble(3.).addDouble(4.).build()).build())
                .build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(10.).addFunction(
                        new PathFunctionBuilder(plusDDD).addDouble(1.).addDouble(2.).addDouble(3.).addDouble(4.)
                                .build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(35L)
                .addFunction(new PathFunctionBuilder(timesLL)
                        .addFunction(new PathFunctionBuilder(plusLL).addLong(3L).addLong(4L).build()).addLong(5L)
                        .build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(35L).addFunction(new PathFunctionBuilder(plusLL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(3L).addLong(5L).build())
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(4L).addLong(5L).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(35.)
                .addFunction(new PathFunctionBuilder(timesDD)
                        .addFunction(new PathFunctionBuilder(plusDD).addDouble(3.).addDouble(4.).build()).addDouble(5.)
                        .build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(35.).addFunction(new PathFunctionBuilder(plusDD)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(3.).addDouble(5.).build())
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(4.).addDouble(5.).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(-10L)
                .addFunction(new PathFunctionBuilder(timesLL)
                        .addFunction(new PathFunctionBuilder(minusL).addLong(2L).build()).addLong(5L).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(-10L).addFunction(new PathFunctionBuilder(minusL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(2L).addLong(5L).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(-10.)
                .addFunction(new PathFunctionBuilder(timesDD)
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(2.).build()).addDouble(5.).build())
                .build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(-10.).addFunction(new PathFunctionBuilder(minusD)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(-10L)
                .addFunction(new PathFunctionBuilder(timesLL).addLong(2L)
                        .addFunction(new PathFunctionBuilder(minusL).addLong(5L).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(-10L).addFunction(new PathFunctionBuilder(minusL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(2L).addLong(5L).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(-10.)
                .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.)
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(-10.).addFunction(new PathFunctionBuilder(minusD)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsLL).addLong(10L)
                .addFunction(new PathFunctionBuilder(timesLL)
                        .addFunction(new PathFunctionBuilder(minusL).addLong(2L).build())
                        .addFunction(new PathFunctionBuilder(minusL).addLong(5L).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(10L)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(2L).addLong(5L).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(10.)
                .addFunction(new PathFunctionBuilder(timesDD)
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(2.).build())
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(10.)
                        .addFunction(new PathFunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    @Test
    public void testTranslateGreaterToLessLong() {
        // >(a,b) -> <(b,a)
        final Predicate greaterLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Greater.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate lessLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PredicateWithArguments<PathLeaf> originalPredicate =
                new PathPredicateBuilder(greaterLL).addLong(20L).addLong(10L).build();
        final PathNodeFilterSet compare = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathPredicateBuilder(lessLL).addLong(10L).addLong(20L).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    @Test
    public void testTranslateGreaterToLessDouble() {
        // >(a,b) -> <(b,a)
        final Predicate greaterDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Greater.IN_CLIPS, SlotType.DOUBLE,
                        SlotType.DOUBLE);
        final Predicate lessDD = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Less.IN_CLIPS, SlotType.DOUBLE, SlotType.DOUBLE);
        final PredicateWithArguments<PathLeaf> originalPredicate =
                new PathPredicateBuilder(greaterDD).addDouble(20.).addDouble(10.).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(lessDD).addDouble(10.).addDouble(20.).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate =
                new PathPredicateBuilder(leqLL).addLong(10L).addLong(20L).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate =
                new PathPredicateBuilder(leqDD).addDouble(10.).addDouble(20.).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate =
                new PathPredicateBuilder(geqLL).addLong(20L).addLong(10L).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

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
        final PredicateWithArguments<PathLeaf> originalPredicate =
                new PathPredicateBuilder(geqDD).addDouble(20.).addDouble(10.).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathPredicateBuilder(notB)
                .addFunction(new PathPredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build()).buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    @Test
    public void testNormalise() {
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
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(12.)
                .addFunction(new PathFunctionBuilder(timesDD).addFunction(new PathFunctionBuilder(plusDD)
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(5.).build()).addDouble(7.).build())
                        .addFunction(new PathFunctionBuilder(minusDD).addDouble(9).addDouble(3).build()).build())
                .build();
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
        testEqualResult(originalPredicate, compare);
    }

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
        final Double a = 7., b = 4., c = 8., d = 3., e = 5., f = 5., g = 7., h = 2., i = 7., j = 1., k = 9.;
        final Double l = (-a + b) * (c - d) * (e + f + g + h * i * j) * k;
        final PredicateWithArguments<PathLeaf> originalPredicate = new PathPredicateBuilder(equalsDD).addDouble(l)
                .addFunction(new PathFunctionBuilder(timesDDDD).addFunction(new PathFunctionBuilder(plusDD)
                        .addFunction(new PathFunctionBuilder(minusD).addDouble(a).build()).addDouble(b).build())
                        .addFunction(new PathFunctionBuilder(minusDD).addDouble(c).addDouble(d).build()).addFunction(
                                new PathFunctionBuilder(plusDDDD).addDouble(e).addDouble(f).addDouble(g).addFunction(
                                        new PathFunctionBuilder(timesDDD).addDouble(h).addDouble(i).addDouble(j)
                                                .build()).build()).addDouble(k).build()).build();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsDD).addDouble(l).addFunction(new PathFunctionBuilder(plusD16)
                        .addFunction(new PathFunctionBuilder(minusD).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(a).addDouble(c).addDouble(e).addDouble(k)
                                        .build()).build()).addFunction(new PathFunctionBuilder(minusD).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(a).addDouble(c).addDouble(f).addDouble(k)
                                        .build()).build()).addFunction(new PathFunctionBuilder(minusD).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(a).addDouble(c).addDouble(g).addDouble(k)
                                        .build()).build()).addFunction(new PathFunctionBuilder(minusD).addFunction(
                                new PathFunctionBuilder(timesDDDDDD).addDouble(a).addDouble(c).addDouble(h).addDouble(i)
                                        .addDouble(j).addDouble(k).build()).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(a).addDouble(d).addDouble(e).addDouble(k)
                                        .build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(a).addDouble(d).addDouble(f).addDouble(k)
                                        .build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(a).addDouble(d).addDouble(g).addDouble(k)
                                        .build()).addFunction(
                                new PathFunctionBuilder(timesDDDDDD).addDouble(a).addDouble(d).addDouble(h).addDouble(i)
                                        .addDouble(j).addDouble(k).build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(b).addDouble(c).addDouble(e).addDouble(k)
                                        .build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(b).addDouble(c).addDouble(f).addDouble(k)
                                        .build()).addFunction(
                                new PathFunctionBuilder(timesDDDD).addDouble(b).addDouble(c).addDouble(g).addDouble(k)
                                        .build()).addFunction(
                                new PathFunctionBuilder(timesDDDDDD).addDouble(b).addDouble(c).addDouble(h).addDouble(i)
                                        .addDouble(j).addDouble(k).build()).addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(timesDDDD).addDouble(b).addDouble(d).addDouble(e)
                                        .addDouble(k).build()).build()).addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(timesDDDD).addDouble(b).addDouble(d).addDouble(f)
                                        .addDouble(k).build()).build()).addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(timesDDDD).addDouble(b).addDouble(d).addDouble(g)
                                        .addDouble(k).build()).build()).addFunction(new PathFunctionBuilder(minusD)
                                .addFunction(new PathFunctionBuilder(timesDDDDDD).addDouble(b).addDouble(d).addDouble(h)
                                        .addDouble(i).addDouble(j).addDouble(k).build()).build()).build())
                        .buildFilter());
        testEqualResult(originalPredicate, compare);
    }

    private static void testEqualResult(final PredicateWithArguments<PathLeaf> originalPredicate,
            final PathNodeFilterSet compare) {
        final PathNodeFilterSet original =
                PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(originalPredicate));
        final PathNodeFilterSet originalTranslated = PathNodeFilterSet
                .newRegularPathNodeFilterSet(new PathFilter(UniformFunctionTranslator.translate(originalPredicate)));
        final AddressNodeFilterSet addrOrignal = PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(original, CounterColumnMatcherMockup.counterColumnMatcherMockup);
        final AddressNodeFilterSet addrOrignalTranslated = PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(originalTranslated, CounterColumnMatcherMockup.counterColumnMatcherMockup);
        final AddressNodeFilterSet addrCompare = PathNodeFilterSetToAddressNodeFilterSetTranslator
                .translate(compare, CounterColumnMatcherMockup.counterColumnMatcherMockup);
        assertSame(evalFirstFE(original), evalFirstFE(original.normalise()));
        assertSame(evalFirstFE(original), evalFirstFE(addrOrignal));
        assertSame(evalFirstFE(original), evalFirstFE(addrOrignal.getNormalisedVersion()));
        assertSame(evalFirstFE(original), evalFirstFE(originalTranslated));
        assertSame(evalFirstFE(original), evalFirstFE(originalTranslated.normalise()));
        assertSame(evalFirstFE(original), evalFirstFE(addrOrignalTranslated));
        assertSame(evalFirstFE(original), evalFirstFE(addrOrignalTranslated.getNormalisedVersion()));
        assertSame(evalFirstFE(original), evalFirstFE(compare));
        assertSame(evalFirstFE(original), evalFirstFE(compare.normalise()));
        assertSame(evalFirstFE(original), evalFirstFE(addrCompare));
        assertSame(evalFirstFE(original), evalFirstFE(addrCompare.getNormalisedVersion()));
    }

    private static <L extends ExchangeableLeaf<L>, T extends Filter<L>> Boolean evalFirstFE(
            final NodeFilterSet<L, T> filter) {
        return filter.getFilters().iterator().next().getFunction().evaluate().booleanValue();
    }

    @Test
    public void testHashCode() {
        final Function<Long> plusLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(7L)
                        .addFunction(new PathFunctionBuilder(plusLL).addLong(6L).addLong(1L).build()).buildFilter())
                .normalise();
        @SuppressWarnings("unchecked")
        final FunctionWithArguments<PathLeaf>[] args =
                ((GenericWithArgumentsComposite<?, ?, PathLeaf>) original.getFilters().iterator().next().getFunction())
                        .getArgs();
        assertEquals(2, args.length);
        final int gwacIndex = args[0] instanceof GenericWithArgumentsComposite<?, ?, ?> ? 0 : 1;
        @SuppressWarnings("unchecked")
        final GenericWithArgumentsComposite<?, ?, PathLeaf> gwac =
                (GenericWithArgumentsComposite<?, ?, PathLeaf>) args[gwacIndex];
        final FunctionWithArguments<PathLeaf>[] gwacArgs = gwac.getArgs();
        assertEquals(2, gwacArgs.length);
        assertEquals(1L, ((ConstantLeaf<PathLeaf>) gwacArgs[0]).getValue());
        assertEquals(6L, ((ConstantLeaf<PathLeaf>) gwacArgs[1]).getValue());
    }

    @Test
    public void testHashCodeCompare() {
        final Function<Long> plusLLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Plus.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG, SlotType.LONG);
        final Function<Long> timesLL =
                FunctionDictionary.<Long>lookup(org.jamocha.function.impls.functions.Times.IN_CLIPS, SlotType.LONG,
                        SlotType.LONG);
        final Predicate equalsLL = FunctionDictionary
                .lookupPredicate(org.jamocha.function.impls.predicates.Equals.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final PathNodeFilterSet original = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(44L).addFunction(new PathFunctionBuilder(plusLLL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(3L).addLong(4L).build())
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(5L).addLong(6L).build())
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(1L).addLong(2L).build()).build())
                        .buildFilter()).normalise();
        final PathNodeFilterSet compare = PathNodeFilterSet.newRegularPathNodeFilterSet(
                new PathPredicateBuilder(equalsLL).addLong(44L).addFunction(new PathFunctionBuilder(plusLLL)
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(1L).addLong(2L).build())
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(3L).addLong(4L).build())
                        .addFunction(new PathFunctionBuilder(timesLL).addLong(5L).addLong(6L).build()).build())
                        .buildFilter()).normalise();
        assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(compare, CounterColumnMatcherMockup.counterColumnMatcherMockup),
                PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(original, CounterColumnMatcherMockup.counterColumnMatcherMockup)));
    }
}
