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
package test.jamocha.dn.memory.javaimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jamocha.dn.memory.javaimpl.Counter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class CounterTest {

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
	public void testCounter() {
		final Counter counter = new Counter(true, false);
		assertEquals(0, counter.size());
		counter.addEmptyRow();
		assertEquals(1, counter.size());
		counter.addEmptyRow();
		assertEquals(2, counter.size());
		counter.addEmptyRow();
		assertEquals(3, counter.size());
		counter.increment(0, 0);
		assertEquals(1, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(2, 0));
		counter.increment(1, 1, 3);
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(3, counter.getCounter(1, 1));
		assertEquals(0, counter.getCounter(2, 1));
	}

	@Test
	public void testValidRow() {
		final boolean negated[] = new boolean[] { false, true, false, false, true };
		final Counter counter = new Counter(negated);
		assertEquals(0, counter.size());
		counter.addEmptyRow();
		assertEquals(1, counter.size());
		counter.addEmptyRow();
		assertEquals(2, counter.size());
		final int value = 3;
		for (int i = 0; i < negated.length; ++i) {
			if (!negated[i]) {
				counter.increment(0, i, value);
				counter.increment(1, i, value);
			}
		}
		assertTrue(counter.validRow(0));
		for (int i = 0; i < negated.length; ++i) {
			if (negated[i])
				counter.increment(0, i, value);
		}
		assertFalse(counter.validRow(0));
		assertTrue(counter.validRow(1));
		for (int i = 0; i < negated.length; ++i) {
			if (negated[i])
				counter.increment(1, i);
		}
		assertFalse(counter.validRow(1));
	}

	@Test
	public void testIncrementIntIntInt() {
		final Counter counter = new Counter(false, true);
		assertEquals(0, counter.size());
		counter.addEmptyRow();
		assertEquals(1, counter.size());
		counter.addEmptyRow();
		assertEquals(2, counter.size());
		assertEquals(0, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		final int value = 9;
		counter.increment(0, 0, value);
		assertEquals(value, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		counter.increment(1, 1, value);
		assertEquals(value, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(value, counter.getCounter(1, 1));
	}

	@Test
	public void testIncrementIntInt() {
		final Counter counter = new Counter(false, true);
		assertEquals(0, counter.size());
		counter.addEmptyRow();
		assertEquals(1, counter.size());
		counter.addEmptyRow();
		assertEquals(2, counter.size());
		assertEquals(0, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		counter.increment(0, 0);
		assertEquals(1, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		counter.increment(1, 1);
		assertEquals(1, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(1, counter.getCounter(1, 1));
	}

	@Test
	public void testDecrementIntInt() {
		final Counter counter = new Counter(false, true);
		assertEquals(0, counter.size());
		counter.addEmptyRow();
		assertEquals(1, counter.size());
		counter.addEmptyRow();
		assertEquals(2, counter.size());
		assertEquals(0, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		counter.decrement(0, 0);
		assertEquals(-1, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		counter.decrement(1, 1);
		assertEquals(-1, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(-1, counter.getCounter(1, 1));
	}

	@Test
	public void testDecrementIntIntInt() {
		final Counter counter = new Counter(true, false);
		assertEquals(0, counter.size());
		counter.addEmptyRow();
		assertEquals(1, counter.size());
		counter.addEmptyRow();
		assertEquals(2, counter.size());
		assertEquals(0, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		final int value = 9;
		counter.decrement(0, 0, value);
		assertEquals(-value, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(0, counter.getCounter(1, 1));
		counter.decrement(1, 1, value);
		assertEquals(-value, counter.getCounter(0, 0));
		assertEquals(0, counter.getCounter(0, 1));
		assertEquals(0, counter.getCounter(1, 0));
		assertEquals(-value, counter.getCounter(1, 1));
	}

}
