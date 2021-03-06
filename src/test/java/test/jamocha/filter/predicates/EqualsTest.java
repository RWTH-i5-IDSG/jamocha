/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package test.jamocha.filter.predicates;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.GenericWithArgumentsComposite.LazyObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import test.jamocha.util.TestData.ListOfBooleans;
import test.jamocha.util.TestData.ListOfDoubles;
import test.jamocha.util.TestData.ListOfLongs;
import test.jamocha.util.TestData.ListOfStrings;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

/**
 * TestCase for the {@link org.jamocha.function.impls.predicates.Equals} class using Theories.
 *
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Theories.class)
public class EqualsTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FunctionDictionary.load();
    }

    private Predicate eqL, eqD, eqB, eqS;

    @Before
    public void setup() {
        eqL = FunctionDictionary.lookupPredicate("=", SlotType.LONG, SlotType.LONG);
        eqD = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
        eqB = FunctionDictionary.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);
        eqS = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
    }

    @Theory
    public void testLongPos(@ListOfLongs final Long left, @ListOfLongs final Long right) {
        assumeThat(left, is(equalTo(right)));
        assertTrue(eqL.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testLongNeg(@ListOfLongs final Long left, @ListOfLongs final Long right) {
        assumeThat(left, is(not(equalTo(right))));
        assertFalse(eqL.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testDoublePos(@ListOfDoubles final Double left, @ListOfDoubles final Double right) {
        assumeThat(left, is(equalTo(right)));
        assertTrue(eqD.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testDoubleNeg(@ListOfDoubles final Double left, @ListOfDoubles final Double right) {
        assumeThat(left, is(not(equalTo(right))));
        assertFalse(eqD.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testStringPos(@ListOfStrings final String left, @ListOfStrings final String right) {
        assumeThat(left, is(equalTo(right)));
        assertTrue(eqS.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testStringNeg(@ListOfStrings final String left, @ListOfStrings final String right) {
        assumeThat(left, is(not(equalTo(right))));
        assertFalse(eqS.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testBooleanPos(@ListOfBooleans final Boolean left, @ListOfBooleans final Boolean right) {
        assumeThat(left, is(equalTo(right)));
        assertTrue(eqB.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testBooleanNeg(@ListOfBooleans final Boolean left, @ListOfBooleans final Boolean right) {
        assumeThat(left, is(not(equalTo(right))));
        assertFalse(eqB.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }
}
