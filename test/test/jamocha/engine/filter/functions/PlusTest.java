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
package test.jamocha.engine.filter.functions;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import lombok.RequiredArgsConstructor;

import org.jamocha.engine.memory.SlotType;
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
public class PlusTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	private final Long expectedL, leftL, rightL;
	private final Double expectedD, leftD, rightD;
	private Function plusL, plusD;
	
	@Parameterized.Parameters
	public static Collection testCases() {
		return Arrays
				.asList(new Number[][] { 
						{0L, -20L, 20L, 0., -0.001, 0.001 },
						{15L, 10L, 5L, 17.003, 17., 0.003},
						{217L, -3L, 220L, 3520.3, -.7, 3521.}
				});
	}
	
	@Before
	public void setUp() {
		plusL = TODODatenkrakeFunktionen.lookup("+", SlotType.LONG, SlotType.LONG);
		plusD = TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
	}
	
	@Test
	public void test() {
		assertEquals(expectedL, (Long)(plusL.evaluate(leftL, rightL)));
		assertEquals(expectedD, (Double)(plusD.evaluate(leftD, rightD)));
	}

}
