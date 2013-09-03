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
public class LessTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	private final Boolean longEq;
	private final Long leftLong;
	private final Long rightLong;

	private final Boolean doubleEq;
	private final Double leftDouble;
	private final Double rightDouble;

	private Function lessL, lessD;

	@Parameterized.Parameters
	public static Collection<Object[]> testCases() {
		return Arrays
				.asList(new Object[][] {
						{ true, (Long) 5L, (Long) 6L, false, (Double) 4.1,
								(Double) 4. },
						{ true, (Long) (-20L), (Long) 500L, true,
								(Double) (-20.), (Double) 20. },
						{ false, (Long) 1L, (Long) 0L, true, (Double) 4.0001,
								(Double) 4.001 },
						{ false, (Long) 5L, (Long) 5L, false, (Double) 217.,
								(Double) 217. } });
	}

	@Before
	public void setup() {
		lessL = TODODatenkrakeFunktionen.lookup("<", SlotType.LONG,
				SlotType.LONG);
		lessD = TODODatenkrakeFunktionen.lookup("<", SlotType.DOUBLE,
				SlotType.DOUBLE);
	}

	@Test
	public void test() {
		if (longEq) {
			assertTrue((Boolean) lessL.evaluate(leftLong, rightLong));
		} else {
			assertFalse((Boolean) lessL.evaluate(leftLong, rightLong));
		}
		if (doubleEq) {
			assertTrue((Boolean) lessD.evaluate(leftDouble, rightDouble));
		} else {
			assertFalse((Boolean) lessD.evaluate(leftDouble, rightDouble));
		}
	}

}
