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
package test.jamocha.filter.predicates;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.jamocha.filter.impls.predicates.Equals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.jamocha.util.TestData.ListOfBooleans;
import test.jamocha.util.TestData.ListOfDoubles;
import test.jamocha.util.TestData.ListOfLongs;
import test.jamocha.util.TestData.ListOfStrings;

/**
 * TestCase for the {@link Equals} class using Theories.
 * 
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Theories.class)
public class EqualsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	private Predicate eqL, eqD, eqB, eqS;

	@Before
	public void setup() {
		eqL = (Predicate) TODODatenkrakeFunktionen.lookup("=", SlotType.LONG, SlotType.LONG);
		eqD = (Predicate) TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE, SlotType.DOUBLE);
		eqB = (Predicate) TODODatenkrakeFunktionen.lookup("=", SlotType.BOOLEAN, SlotType.BOOLEAN);
		eqS = (Predicate) TODODatenkrakeFunktionen.lookup("=", SlotType.STRING, SlotType.STRING);
	}

	@Theory
	public void testLongPos(@ListOfLongs Long left, @ListOfLongs Long right) {
		assumeThat(left, is(equalTo(right)));
		assertTrue((Boolean) (eqL.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testLongNeg(@ListOfLongs Long left, @ListOfLongs Long right) {
		assumeThat(left, is(not(equalTo(right))));
		assertFalse((Boolean) (eqL.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testDoublePos(@ListOfDoubles Double left, @ListOfDoubles Double right) {
		assumeThat(left, is(equalTo(right)));
		assertTrue((Boolean) (eqD.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testDoubleNeg(@ListOfDoubles Double left, @ListOfDoubles Double right) {
		assumeThat(left, is(not(equalTo(right))));
		assertFalse((Boolean) (eqD.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testStringPos(@ListOfStrings String left, @ListOfStrings String right) {
		assumeThat(left, is(equalTo(right)));
		assertTrue((Boolean) (eqS.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testStringNeg(@ListOfStrings String left, @ListOfStrings String right) {
		assumeThat(left, is(not(equalTo(right))));
		assertFalse((Boolean) (eqS.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testBooleanPos(@ListOfBooleans Boolean left, @ListOfBooleans Boolean right) {
		assumeThat(left, is(equalTo(right)));
		assertTrue((Boolean) (eqB.evaluate(new LazyObject(left), new LazyObject(right))));
	}

	@Theory
	public void testBooleanNeg(@ListOfBooleans Boolean left, @ListOfBooleans Boolean right) {
		assumeThat(left, is(not(equalTo(right))));
		assertFalse((Boolean) (eqB.evaluate(new LazyObject(left), new LazyObject(right))));
	}
}
