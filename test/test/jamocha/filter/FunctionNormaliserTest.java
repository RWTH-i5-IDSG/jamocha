/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.filter;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.Filter;
import org.jamocha.filter.FilterTranslator;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.UniformFunctionTranslator;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.CounterColumnMatcherMockup;
import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;

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
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Function<Long> minusLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Minus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL).addLong(5L)
						.addFunction(new FunctionBuilder(minusLL).addLong(6L).addLong(1L).build())
						.build();
		final PathFilter compare =
				new PathFilter(
						new PredicateBuilder(equalsLL)
								.addLong(5L)
								.addFunction(
										new FunctionBuilder(plusLL)
												.addLong(6)
												.addFunction(
														new FunctionBuilder(minusL).addLong(1)
																.build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryMinusDouble() {
		// -(a,b) -> +(a,(-b))
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Function<Double> minusDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Minus.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> plusDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(minusDD).addDouble(6.).addDouble(1.).build())
						.build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(plusDD)
										.addDouble(6.)
										.addFunction(
												new FunctionBuilder(minusD).addDouble(1.).build())
										.build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateUnaryMinusLong() {
		// -(-(a)) -> a
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(5L)
						.addFunction(
								new FunctionBuilder(minusL).addFunction(
										new FunctionBuilder(minusL).addLong(5L).build()).build())
						.build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateUnaryMinusDouble() {
		// -(-(a)) -> a
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(minusD).addFunction(
										new FunctionBuilder(minusD).addDouble(5.).build()).build())
						.build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD).addDouble(5.).addDouble(5.)
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateDividedByLong() {
		// /(a,b) -> *(a,1/b)
		final Function<Long> divLL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.DividedBy.inClips, SlotType.LONG,
						SlotType.LONG);
		final Function<Long> divL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.TimesInverse.inClips, SlotType.LONG);
		final Function<Long> timesLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL).addLong(5L)
						.addFunction(new FunctionBuilder(divLL).addLong(7).addLong(5).build())
						.build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(5L)
						.addFunction(
								new FunctionBuilder(timesLL).addLong(7)
										.addFunction(new FunctionBuilder(divL).addLong(5).build())
										.build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateDividedByDouble() {
		// /(a,b) -> *(a,1/b)
		final Function<Double> divDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.DividedBy.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> divD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.TimesInverse.inClips, SlotType.DOUBLE);
		final Function<Double> timesDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(new FunctionBuilder(divDD).addDouble(7.).addDouble(5.).build())
						.build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addDouble(7.)
										.addFunction(
												new FunctionBuilder(divD).addDouble(5.).build())
										.build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test(expected = java.lang.ArithmeticException.class)
	public void testTranslateTimesInverseLong() {
		// 1/(1/a) -> a
		final Function<Long> divL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.TimesInverse.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(new PredicateBuilder(equalsLL)
						.addLong(5L)
						.addFunction(
								new FunctionBuilder(divL).addFunction(
										new FunctionBuilder(divL).addLong(5L).build()).build())
						.build()));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildPFE());
		assertNotSame(evalFirstFE(original.normalise()), evalFirstFE(compare.normalise()));

	}

	@Test
	public void testTranslateTimesInverseDouble() {
		// 1/(1/a) -> a
		final Function<Double> divD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.TimesInverse.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(divD).addFunction(
										new FunctionBuilder(divD).addDouble(5.).build()).build())
						.build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD).addDouble(5.).addDouble(5.)
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryPlusLeftExpandLong() {
		// +(+(a,b),c) -> +(a,b,c)
		final Function<Long> plusLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(12L)
						.addFunction(
								new FunctionBuilder(plusLL)
										.addFunction(
												new FunctionBuilder(plusLL).addLong(5L).addLong(4L)
														.build()).addLong(3L).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(12L)
						.addFunction(
								new FunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L)
										.build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryPlusLeftExpandDouble() {
		// +(+(a,b),c) -> +(a,b,c)
		final Function<Double> plusDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDD)
										.addFunction(
												new FunctionBuilder(plusDD).addDouble(5.)
														.addDouble(4.).build()).addDouble(3.)
										.build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDDD).addDouble(5.).addDouble(4.)
										.addDouble(3.).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryPlusRightExpandLong() {
		// +(a,+(b,c)) -> +(a,b,c)
		final Function<Long> plusLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(12L)
						.addFunction(
								new FunctionBuilder(plusLL)
										.addLong(5L)
										.addFunction(
												new FunctionBuilder(plusLL).addLong(4L).addLong(3L)
														.build()).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(12L)
						.addFunction(
								new FunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L)
										.build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryPlusRightExpandDouble() {
		// +(a,+(b,c)) -> +(a,b,c)
		final Function<Double> plusDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDD)
										.addDouble(5.)
										.addFunction(
												new FunctionBuilder(plusDD).addDouble(4.)
														.addDouble(3.).build()).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDDD).addDouble(5.).addDouble(4.)
										.addDouble(3.).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryPlusDoubleExpandLong() {
		// +(+(a,b),+(c,d)) -> +(a,b,c,d)
		final Function<Long> plusLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG, SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(10L)
						.addFunction(
								new FunctionBuilder(plusLL)
										.addFunction(
												new FunctionBuilder(plusLL).addLong(1L).addLong(2L)
														.build())
										.addFunction(
												new FunctionBuilder(plusLL).addLong(3L).addLong(4L)
														.build()).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(10L)
						.addFunction(
								new FunctionBuilder(plusLLL).addLong(1L).addLong(2L).addLong(3L)
										.addLong(4L).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryPlusDoubleExpandDouble() {
		// +(+(a,b),+(c,d)) -> +(a,b,c,d)
		final Function<Double> plusDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(10.)
						.addFunction(
								new FunctionBuilder(plusDD)
										.addFunction(
												new FunctionBuilder(plusDD).addDouble(1.)
														.addDouble(2.).build())
										.addFunction(
												new FunctionBuilder(plusDD).addDouble(3.)
														.addDouble(4.).build()).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(10.)
						.addFunction(
								new FunctionBuilder(plusDDD).addDouble(1.).addDouble(2.)
										.addDouble(3.).addDouble(4.).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesDistributePlusLong() {
		// *(+(a,b),c) -> +(*(a,c),*(b,c))
		final Function<Long> plusLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> timesLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(35L)
						.addFunction(
								new FunctionBuilder(timesLL)
										.addFunction(
												new FunctionBuilder(plusLL).addLong(3L).addLong(4L)
														.build()).addLong(5L).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(35L)
						.addFunction(
								new FunctionBuilder(plusLL)
										.addFunction(
												new FunctionBuilder(timesLL).addLong(3L)
														.addLong(5L).build())
										.addFunction(
												new FunctionBuilder(timesLL).addLong(4L)
														.addLong(5L).build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesDistributePlusDouble() {
		// *(+(a,b),c) -> +(*(a,c),*(b,c))
		final Function<Double> plusDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> timesDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(35.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addFunction(
												new FunctionBuilder(plusDD).addDouble(3.)
														.addDouble(4.).build()).addDouble(5.)
										.build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(35.)
						.addFunction(
								new FunctionBuilder(plusDD)
										.addFunction(
												new FunctionBuilder(timesDD).addDouble(3.)
														.addDouble(5.).build())
										.addFunction(
												new FunctionBuilder(timesDD).addDouble(4.)
														.addDouble(5.).build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusLeftArgLong() {
		// *(-(a),b) -> -(*(a,b))
		final Function<Long> timesLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(-10L)
						.addFunction(
								new FunctionBuilder(timesLL)
										.addFunction(
												new FunctionBuilder(minusL).addLong(2L).build())
										.addLong(5L).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(-10L)
						.addFunction(
								new FunctionBuilder(minusL).addFunction(
										new FunctionBuilder(timesLL).addLong(2L).addLong(5L)
												.build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusLeftArgDouble() {
		// *(-(a),b) -> -(*(a,b))
		final Function<Double> timesDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(-10.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addFunction(
												new FunctionBuilder(minusD).addDouble(2.).build())
										.addDouble(5.).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(-10.)
						.addFunction(
								new FunctionBuilder(minusD).addFunction(
										new FunctionBuilder(timesDD).addDouble(2.).addDouble(5.)
												.build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusRightArgLong() {
		// *(a,-(b)) -> -(*(a,b))
		final Function<Long> timesLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(-10L)
						.addFunction(
								new FunctionBuilder(timesLL)
										.addLong(2L)
										.addFunction(
												new FunctionBuilder(minusL).addLong(5L).build())
										.build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(-10L)
						.addFunction(
								new FunctionBuilder(minusL).addFunction(
										new FunctionBuilder(timesLL).addLong(2L).addLong(5L)
												.build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusRightArgDouble() {
		// *(a,-(b)) -> -(*(a,b))
		final Function<Double> timesDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(-10.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addDouble(2.)
										.addFunction(
												new FunctionBuilder(minusD).addDouble(5.).build())
										.build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(-10.)
						.addFunction(
								new FunctionBuilder(minusD).addFunction(
										new FunctionBuilder(timesDD).addDouble(2.).addDouble(5.)
												.build()).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusBothArgsLong() {
		// *(-(a),(-b)) -> *(a,b)
		final Function<Long> timesLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsLL)
						.addLong(10L)
						.addFunction(
								new FunctionBuilder(timesLL)
										.addFunction(
												new FunctionBuilder(minusL).addLong(2L).build())
										.addFunction(
												new FunctionBuilder(minusL).addLong(5L).build())
										.build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL).addLong(10L)
						.addFunction(new FunctionBuilder(timesLL).addLong(2L).addLong(5L).build())
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusBothArgsDouble() {
		// *(-(a),(-b)) -> *(a,b)
		final Function<Double> timesDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(10.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addFunction(
												new FunctionBuilder(minusD).addDouble(2.).build())
										.addFunction(
												new FunctionBuilder(minusD).addDouble(5.).build())
										.build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(10.)
						.addFunction(
								new FunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build())
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateGreaterToLessLong() {
		// >(a,b) -> <(b,a)
		final Predicate greaterLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Greater.inClips, SlotType.LONG,
						SlotType.LONG);
		final Predicate lessLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Less.inClips, SlotType.LONG,
						SlotType.LONG);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(greaterLL).addLong(20L).addLong(10L).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(lessLL).addLong(10L).addLong(20L).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateGreaterToLessDouble() {
		// >(a,b) -> <(b,a)
		final Predicate greaterDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Greater.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate lessDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Less.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(greaterDD).addDouble(20.).addDouble(10.).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(lessDD).addDouble(10.).addDouble(20.)
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateLeqToNLessLong() {
		// <=(a,b) -> !(<(b,a))
		final Predicate leqLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.LessOrEqual.inClips, SlotType.LONG,
						SlotType.LONG);
		final Predicate lessLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Less.inClips, SlotType.LONG,
						SlotType.LONG);
		final Predicate notB =
				FunctionDictionary.lookupPredicate(org.jamocha.filter.impls.predicates.Not.inClips,
						SlotType.BOOLEAN);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(leqLL).addLong(10L).addLong(20L).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateLeqToNLessDouble() {
		// <=(a,b) -> !(<(b,a))
		final Predicate leqDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.LessOrEqual.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate lessDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Less.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate notB =
				FunctionDictionary.lookupPredicate(org.jamocha.filter.impls.predicates.Not.inClips,
						SlotType.BOOLEAN);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(leqDD).addDouble(10.).addDouble(20.).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build())
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateGeqToNLessLong() {
		// >=(a,b) -> !(<(a,b))
		final Predicate geqLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.GreaterOrEqual.inClips, SlotType.LONG,
						SlotType.LONG);
		final Predicate lessLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Less.inClips, SlotType.LONG,
						SlotType.LONG);
		final Predicate notB =
				FunctionDictionary.lookupPredicate(org.jamocha.filter.impls.predicates.Not.inClips,
						SlotType.BOOLEAN);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(geqLL).addLong(20L).addLong(10L).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testTranslateGeqToNLessDouble() {
		// >=(a,b) -> !(<(a,b))
		final Predicate geqDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.GreaterOrEqual.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate lessDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Less.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate notB =
				FunctionDictionary.lookupPredicate(org.jamocha.filter.impls.predicates.Not.inClips,
						SlotType.BOOLEAN);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(geqDD).addDouble(20.).addDouble(10.).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build())
						.buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	@Test
	public void testNormalise() {
		// *(+(-(a),b),-(c,d))
		// -> +( *(-a,c), *(a,d), *(b,c), *(b,-d))
		final Function<Double> plusDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> minusDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Minus.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Function<Double> timesDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PredicateWithArguments originalPredicate =
				new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addFunction(
												new FunctionBuilder(plusDD)
														.addFunction(
																new FunctionBuilder(minusD)
																		.addDouble(5.).build())
														.addDouble(7.).build())
										.addFunction(
												new FunctionBuilder(minusDD).addDouble(9)
														.addDouble(3).build()).build()).build();
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDDDD)
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDD).addDouble(5.)
																.addDouble(9.).build()).build())
										.addFunction(
												new FunctionBuilder(timesDD).addDouble(5.)
														.addDouble(3.).build())
										.addFunction(
												new FunctionBuilder(timesDD).addDouble(7.)
														.addDouble(9.).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDD).addDouble(7.)
																.addDouble(3.).build()).build())
										.build()).buildPFE());
		testEqualResult(originalPredicate, compare);
	}

	private static void testEqualResult(final PredicateWithArguments originalPredicate,
			final PathFilter compare) {
		final PathFilter original = new PathFilter(new PathFilterElement(originalPredicate));
		final PathFilter originalTranslated =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(originalPredicate)));
		final AddressFilter addrOrignal =
				FilterTranslator.translate(original,
						CounterColumnMatcherMockup.counterColumnMatcherMockup);
		final AddressFilter addrOrignalTranslated =
				FilterTranslator.translate(originalTranslated,
						CounterColumnMatcherMockup.counterColumnMatcherMockup);
		final AddressFilter addrCompare =
				FilterTranslator.translate(compare,
						CounterColumnMatcherMockup.counterColumnMatcherMockup);
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

	private static <T extends Filter.FilterElement> Boolean evalFirstFE(final Filter<T> filter) {
		return filter.getFilterElements()[0].getFunction().evaluate().booleanValue();
	}
}
