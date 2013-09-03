/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package test.jamocha.engine.filter.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 */
@RunWith(value = Parameterized.class)
@RequiredArgsConstructor
public class EqualsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	private final Long testLong;
	private final Long expectedLong;
	private final Double testDouble;
	private final Double expectedDouble;
	private final Boolean testBool;
	private final Boolean expectedBool;
	private final String testString;
	private final String expectedString;

	private Function eqL, eqD, eqB, eqS;

	@Parameterized.Parameters
	public static Collection<Object[]> testCases() {
		Double i = 5.;
		return Arrays.asList(new Object[][] {
				{ 1L, 1L, 0.3535, 0.3535, true, true, "asdf", "asdf" },
				{ 192853692L, 192853692L, 17.3, 17.3, false, 5. != i, "OMG",
						"OMG" },
				{ 9223372036854775807L, 9223372036854775807L, Double.MAX_VALUE,
						new Double(Double.MAX_VALUE), true, 5. == i, "foobar",
						"foobar" } });
	}

	@Before
	public void setup() {
		eqL = TODODatenkrakeFunktionen
				.lookup("=", SlotType.LONG, SlotType.LONG);
		eqD = TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
				SlotType.DOUBLE);
		eqB = TODODatenkrakeFunktionen.lookup("=", SlotType.BOOLEAN,
				SlotType.BOOLEAN);
		eqS = TODODatenkrakeFunktionen.lookup("=", SlotType.STRING,
				SlotType.STRING);
	}

	@Test
	public void test() {
		assertTrue((Boolean) eqL.evaluate(expectedLong, testLong));
		assertTrue((Boolean) eqD.evaluate(expectedDouble, testDouble));
		assertTrue((Boolean) eqB.evaluate(expectedBool, testBool));
		assertTrue((Boolean) eqS.evaluate(expectedString, testString));
		assertFalse((Boolean) eqL.evaluate(5162013L, testLong));
		assertFalse((Boolean) eqD.evaluate(21732.1409325, testDouble));
		assertFalse((Boolean) eqS.evaluate("OMGWTFBBQ!", testString));
	}

}
