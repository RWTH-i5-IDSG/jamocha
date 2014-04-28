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

import static org.junit.Assert.assertTrue;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.FilterTranslator;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.UniformFunctionTranslator;
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
	public void testTranslateBinaryMinus() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateUnaryMinus() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateDividedBy() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateTimesInverse() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryPlusLeftExpand() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryPlusRightExpand() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryPlusDoubleExpand() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryTimesDistributePlus() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusLeftArg() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusRightArg() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.UniformFunctionTranslator#translate(org.jamocha.filter.fwa.PredicateWithArguments)}
	 * .
	 */
	@Test
	public void testTranslateBinaryTimesShiftUnaryMinusBothArgs() {
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
		assertTrue(FilterFunctionCompare.equals(compare, FilterTranslator.translate(original,
				CounterColumnMatcherMockup.counterColumnMatcherMockup)));
	}

	// >(a,b) -> <(b,a)
	// <=(a,b) -> !(<(b,a))
	// >=(a,b) -> !(<(a,b))
}
