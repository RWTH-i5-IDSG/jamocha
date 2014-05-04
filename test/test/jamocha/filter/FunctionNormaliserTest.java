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
		final PathFilter original = new PathFilter(new PathFilterElement(originalPredicate));
		final PathFilter originalTranslated =
				new PathFilter(new PathFilterElement(
						UniformFunctionTranslator.translate(originalPredicate)));
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
