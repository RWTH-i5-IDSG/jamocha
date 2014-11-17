/*
 * Copyright 2002-2013 The Jamocha Team
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
package test.jamocha.filter.functions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.fwa.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.function.impls.functions.Minus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.jamocha.util.TestData.LotsOfRandomDoubles;
import test.jamocha.util.TestData.LotsOfRandomLongs;

/**
 * TestCase for the {@link Minus} class using Theories.
 * 
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Theories.class)
public class MinusTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FunctionDictionary.load();
	}

	private Function<? extends Number> minusL, minusD;

	@Before
	public void setUp() {
		minusL = FunctionDictionary.<Long> lookup("-", SlotType.LONG, SlotType.LONG);
		minusD = FunctionDictionary.<Long> lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
	}

	@Theory
	public void testLong(@LotsOfRandomLongs final Long left, @LotsOfRandomLongs final Long right) {
		assertThat(left - right, is((Long) (minusL.evaluate(new LazyObject<>(left), new LazyObject<>(right)))));
	}

	@Theory
	public void testDouble(@LotsOfRandomDoubles final Double left, @LotsOfRandomDoubles final Double right) {
		assertThat(left - right, is((Double) (minusD.evaluate(new LazyObject<>(left), new LazyObject<>(right)))));
	}
}
