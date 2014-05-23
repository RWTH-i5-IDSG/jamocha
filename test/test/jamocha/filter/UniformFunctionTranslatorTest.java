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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static test.jamocha.util.CounterColumnMatcherMockup.counterColumnMatcherMockup;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.memory.javaimpl.Template;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.FilterTranslator;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.UniformFunctionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;

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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(5L)
								.addFunction(
										new FunctionBuilder(minusLL).addLong(6L).addLong(1L)
												.build()).build())));
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
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(5.)
								.addFunction(
										new FunctionBuilder(minusDD).addDouble(6.).addDouble(1.)
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(plusDD)
										.addDouble(6.)
										.addFunction(
												new FunctionBuilder(minusD).addDouble(1.).build())
										.build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(5L)
								.addFunction(
										new FunctionBuilder(minusL).addFunction(
												new FunctionBuilder(minusL).addLong(5L).build())
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(5.)
								.addFunction(
										new FunctionBuilder(minusD).addFunction(
												new FunctionBuilder(minusD).addDouble(5.).build())
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD).addDouble(5.).addDouble(5.)
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(5L)
								.addFunction(
										new FunctionBuilder(divLL).addLong(7).addLong(5).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(5L)
						.addFunction(
								new FunctionBuilder(timesLL).addLong(7)
										.addFunction(new FunctionBuilder(divL).addLong(5).build())
										.build()).buildPFE());
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(5.)
								.addFunction(
										new FunctionBuilder(divDD).addDouble(7.).addDouble(5.)
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(5.)
						.addFunction(
								new FunctionBuilder(timesDD)
										.addDouble(7.)
										.addFunction(
												new FunctionBuilder(divD).addDouble(5.).build())
										.build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.TimesInverse.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(5L)
								.addFunction(
										new FunctionBuilder(divL).addFunction(
												new FunctionBuilder(divL).addLong(5L).build())
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL).addLong(5L).addLong(5L).buildPFE());
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.TimesInverse.inClips, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(5.)
								.addFunction(
										new FunctionBuilder(divD).addFunction(
												new FunctionBuilder(divD).addDouble(5.).build())
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD).addDouble(5.).addDouble(5.)
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(12L)
								.addFunction(
										new FunctionBuilder(plusLL)
												.addFunction(
														new FunctionBuilder(plusLL).addLong(5L)
																.addLong(4L).build()).addLong(3L)
												.build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(12L)
						.addFunction(
								new FunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L)
										.build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(12.)
								.addFunction(
										new FunctionBuilder(plusDD)
												.addFunction(
														new FunctionBuilder(plusDD).addDouble(5.)
																.addDouble(4.).build())
												.addDouble(3.).build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDDD).addDouble(5.).addDouble(4.)
										.addDouble(3.).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(12L)
								.addFunction(
										new FunctionBuilder(plusLL)
												.addLong(5L)
												.addFunction(
														new FunctionBuilder(plusLL).addLong(4L)
																.addLong(3L).build()).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(12L)
						.addFunction(
								new FunctionBuilder(plusLLL).addLong(5L).addLong(4L).addLong(3L)
										.build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(12.)
								.addFunction(
										new FunctionBuilder(plusDD)
												.addDouble(5.)
												.addFunction(
														new FunctionBuilder(plusDD).addDouble(4.)
																.addDouble(3.).build()).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(12.)
						.addFunction(
								new FunctionBuilder(plusDDD).addDouble(5.).addDouble(4.)
										.addDouble(3.).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG, SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(10L)
								.addFunction(
										new FunctionBuilder(plusLL)
												.addFunction(
														new FunctionBuilder(plusLL).addLong(1L)
																.addLong(2L).build())
												.addFunction(
														new FunctionBuilder(plusLL).addLong(3L)
																.addLong(4L).build()).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(10L)
						.addFunction(
								new FunctionBuilder(plusLLL).addLong(1L).addLong(2L).addLong(3L)
										.addLong(4L).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(10.)
								.addFunction(
										new FunctionBuilder(plusDD)
												.addFunction(
														new FunctionBuilder(plusDD).addDouble(1.)
																.addDouble(2.).build())
												.addFunction(
														new FunctionBuilder(plusDD).addDouble(3.)
																.addDouble(4.).build()).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(10.)
						.addFunction(
								new FunctionBuilder(plusDDD).addDouble(1.).addDouble(2.)
										.addDouble(3.).addDouble(4.).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> timesLL =
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(35L)
								.addFunction(
										new FunctionBuilder(timesLL)
												.addFunction(
														new FunctionBuilder(plusLL).addLong(3L)
																.addLong(4L).build()).addLong(5L)
												.build()).build())));
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
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(35.)
								.addFunction(
										new FunctionBuilder(timesDD)
												.addFunction(
														new FunctionBuilder(plusDD).addDouble(3.)
																.addDouble(4.).build())
												.addDouble(5.).build()).build())));
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
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(-10L)
								.addFunction(
										new FunctionBuilder(timesLL)
												.addFunction(
														new FunctionBuilder(minusL).addLong(2L)
																.build()).addLong(5L).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(-10L)
						.addFunction(
								new FunctionBuilder(minusL).addFunction(
										new FunctionBuilder(timesLL).addLong(2L).addLong(5L)
												.build()).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(-10.)
								.addFunction(
										new FunctionBuilder(timesDD)
												.addFunction(
														new FunctionBuilder(minusD).addDouble(2.)
																.build()).addDouble(5.).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(-10.)
						.addFunction(
								new FunctionBuilder(minusD).addFunction(
										new FunctionBuilder(timesDD).addDouble(2.).addDouble(5.)
												.build()).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(-10L)
								.addFunction(
										new FunctionBuilder(timesLL)
												.addLong(2L)
												.addFunction(
														new FunctionBuilder(minusL).addLong(5L)
																.build()).build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL)
						.addLong(-10L)
						.addFunction(
								new FunctionBuilder(minusL).addFunction(
										new FunctionBuilder(timesLL).addLong(2L).addLong(5L)
												.build()).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(-10.)
								.addFunction(
										new FunctionBuilder(timesDD)
												.addDouble(2.)
												.addFunction(
														new FunctionBuilder(minusD).addDouble(5.)
																.build()).build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(-10.)
						.addFunction(
								new FunctionBuilder(minusD).addFunction(
										new FunctionBuilder(timesDD).addDouble(2.).addDouble(5.)
												.build()).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
				FunctionDictionary.<Long> lookup(org.jamocha.filter.impls.functions.Times.inClips,
						SlotType.LONG, SlotType.LONG);
		final Function<Long> minusL =
				FunctionDictionary.<Long> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.LONG,
						SlotType.LONG);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsLL)
								.addLong(10L)
								.addFunction(
										new FunctionBuilder(timesLL)
												.addFunction(
														new FunctionBuilder(minusL).addLong(2L)
																.build())
												.addFunction(
														new FunctionBuilder(minusL).addLong(5L)
																.build()).build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsLL).addLong(10L)
						.addFunction(new FunctionBuilder(timesLL).addLong(2L).addLong(5L).build())
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(10.)
								.addFunction(
										new FunctionBuilder(timesDD)
												.addFunction(
														new FunctionBuilder(minusD).addDouble(2.)
																.build())
												.addFunction(
														new FunctionBuilder(minusD).addDouble(5.)
																.build()).build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addDouble(10.)
						.addFunction(
								new FunctionBuilder(timesDD).addDouble(2.).addDouble(5.).build())
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(greaterLL)
								.addLong(20L).addLong(10L).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(lessLL).addLong(10L).addLong(20L).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(greaterDD)
								.addDouble(20.).addDouble(10.).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(lessDD).addDouble(10.).addDouble(20.)
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(leqLL)
								.addLong(10L).addLong(20L).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(leqDD)
								.addDouble(10.).addDouble(20.).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build())
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(geqLL)
								.addLong(20L).addLong(10L).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessLL).addLong(20L).addLong(10L).build()).buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(geqDD)
								.addDouble(20.).addDouble(10.).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(notB).addFunction(
						new PredicateBuilder(lessDD).addDouble(20.).addDouble(10.).build())
						.buildPFE());
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup),
				FilterTranslator.translate(original, counterColumnMatcherMockup)));
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
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addDouble(12.)
								.addFunction(
										new FunctionBuilder(timesDD)
												.addFunction(
														new FunctionBuilder(plusDD)
																.addFunction(
																		new FunctionBuilder(minusD)
																				.addDouble(5.)
																				.build())
																.addDouble(7.).build())
												.addFunction(
														new FunctionBuilder(minusDD).addDouble(9)
																.addDouble(3).build()).build())
								.build())));
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
		final AddressFilter translate =
				FilterTranslator.translate(original, counterColumnMatcherMockup);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup), translate));
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
		final Template template = Template.DOUBLE;
		final Path a = new Path(template), b = new Path(template), c = new Path(template), d =
				new Path(template), l = new Path(template);
		final SlotAddress s = new SlotAddress(0);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addPath(l, s)
								.addFunction(
										new FunctionBuilder(timesDD)
												.addFunction(
														new FunctionBuilder(plusDD)
																.addFunction(
																		new FunctionBuilder(minusD)
																				.addPath(a, s)
																				.build())
																.addPath(b, s).build())
												.addFunction(
														new FunctionBuilder(minusDD).addPath(c, s)
																.addPath(d, s).build()).build())
								.build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addPath(l, s)
						.addFunction(
								new FunctionBuilder(plusDDDD)
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDD).addPath(a, s)
																.addPath(c, s).build()).build())
										.addFunction(
												new FunctionBuilder(timesDD).addPath(a, s)
														.addPath(d, s).build())
										.addFunction(
												new FunctionBuilder(timesDD).addPath(b, s)
														.addPath(c, s).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDD).addPath(b, s)
																.addPath(d, s).build()).build())
										.build()).buildPFE());
		final AddressFilter translate =
				FilterTranslator.translate(original, counterColumnMatcherMockup);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup), translate));
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
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusDDDD =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> plusD16 =
				FunctionDictionary.<Double> lookup(org.jamocha.filter.impls.functions.Plus.inClips,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> minusDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Minus.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Function<Double> minusD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.UnaryMinus.inClips, SlotType.DOUBLE);
		final Function<Double> timesDDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> timesDDDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<Double> timesDDDDDD =
				FunctionDictionary.<Double> lookup(
						org.jamocha.filter.impls.functions.Times.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Predicate equalsDD =
				FunctionDictionary.lookupPredicate(
						org.jamocha.filter.impls.predicates.Equals.inClips, SlotType.DOUBLE,
						SlotType.DOUBLE);
		final Template template = Template.DOUBLE;
		final Path a = new Path(template), b = new Path(template), c = new Path(template), d =
				new Path(template), e = new Path(template), f = new Path(template), g =
				new Path(template), h = new Path(template), i = new Path(template), j =
				new Path(template), k = new Path(template), l = new Path(template);
		final SlotAddress s = new SlotAddress(0);
		final PathFilter original =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(new PredicateBuilder(equalsDD)
								.addPath(l, s)
								.addFunction(
										new FunctionBuilder(timesDDDD)
												.addFunction(
														new FunctionBuilder(plusDD)
																.addFunction(
																		new FunctionBuilder(minusD)
																				.addPath(a, s)
																				.build())
																.addPath(b, s).build())
												.addFunction(
														new FunctionBuilder(minusDD).addPath(c, s)
																.addPath(d, s).build())
												.addFunction(
														new FunctionBuilder(plusDDDD)
																.addPath(e, s)
																.addPath(f, s)
																.addPath(g, s)
																.addFunction(
																		new FunctionBuilder(
																				timesDDD)
																				.addPath(h, s)
																				.addPath(i, s)
																				.addPath(j, s)
																				.build()).build())
												.addPath(k, s).build()).build())));
		final PathFilter compare =
				new PathFilter(new PredicateBuilder(equalsDD)
						.addPath(l, s)
						.addFunction(
								new FunctionBuilder(plusD16)
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDD)
																.addPath(a, s).addPath(c, s)
																.addPath(e, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDD)
																.addPath(a, s).addPath(c, s)
																.addPath(f, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDD)
																.addPath(a, s).addPath(c, s)
																.addPath(g, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDDDD)
																.addPath(a, s).addPath(c, s)
																.addPath(h, s).addPath(i, s)
																.addPath(j, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(timesDDDD).addPath(a, s)
														.addPath(d, s).addPath(e, s).addPath(k, s)
														.build())
										.addFunction(
												new FunctionBuilder(timesDDDD).addPath(a, s)
														.addPath(d, s).addPath(f, s).addPath(k, s)
														.build())
										.addFunction(
												new FunctionBuilder(timesDDDD).addPath(a, s)
														.addPath(d, s).addPath(g, s).addPath(k, s)
														.build())
										.addFunction(
												new FunctionBuilder(timesDDDDDD).addPath(a, s)
														.addPath(d, s).addPath(h, s).addPath(i, s)
														.addPath(j, s).addPath(k, s).build())
										.addFunction(
												new FunctionBuilder(timesDDDD).addPath(b, s)
														.addPath(c, s).addPath(e, s).addPath(k, s)
														.build())
										.addFunction(
												new FunctionBuilder(timesDDDD).addPath(b, s)
														.addPath(c, s).addPath(f, s).addPath(k, s)
														.build())
										.addFunction(
												new FunctionBuilder(timesDDDD).addPath(b, s)
														.addPath(c, s).addPath(g, s).addPath(k, s)
														.build())
										.addFunction(
												new FunctionBuilder(timesDDDDDD).addPath(b, s)
														.addPath(c, s).addPath(h, s).addPath(i, s)
														.addPath(j, s).addPath(k, s).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDD)
																.addPath(b, s).addPath(d, s)
																.addPath(e, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDD)
																.addPath(b, s).addPath(d, s)
																.addPath(f, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDD)
																.addPath(b, s).addPath(d, s)
																.addPath(g, s).addPath(k, s)
																.build()).build())
										.addFunction(
												new FunctionBuilder(minusD).addFunction(
														new FunctionBuilder(timesDDDDDD)
																.addPath(b, s).addPath(d, s)
																.addPath(h, s).addPath(i, s)
																.addPath(j, s).addPath(k, s)
																.build()).build()).build())
						.buildPFE());
		final AddressFilter translate =
				FilterTranslator.translate(original, counterColumnMatcherMockup);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(compare, counterColumnMatcherMockup), translate));
	}
}
