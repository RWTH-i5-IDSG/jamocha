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
		final Function<Long> minusL = FunctionDictionary.<Long> lookup("-", SlotType.LONG);
		final Function<Long> minusLL =
				FunctionDictionary.<Long> lookup("-", SlotType.LONG, SlotType.LONG);
		final Function<Long> plusLL =
				FunctionDictionary.<Long> lookup("+", SlotType.LONG, SlotType.LONG);
		final Predicate equalsLL =
				FunctionDictionary.lookupPredicate("=", SlotType.LONG, SlotType.LONG);
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
		// fail("Not yet implemented");
	}

	// -(-(a)) -> a
	// /(a,b) -> *(a,1/b)
	// 1/(1/a) -> a
	// +(+(a,b),c) -> +(a,b,c)
	// +(a,+(b,c)) -> +(a,b,c)
	// *(+(a,b),c) -> +(*(a,c),*(b,c))
	// *(-(a),b) -> -(*(a,b))
	// >(a,b) -> <(b,a)
	// <=(a,b) -> !(<(b,a))
	// >=(a,b) -> !(<(a,b))
}
