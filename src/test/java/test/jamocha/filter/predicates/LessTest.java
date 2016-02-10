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
import test.jamocha.util.TestData.LotsOfRandomDoubles;
import test.jamocha.util.TestData.LotsOfRandomLongs;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

/**
 * TestCase for the {@link org.jamocha.function.impls.predicates.Less} class using Theories.
 *
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Theories.class)
public class LessTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FunctionDictionary.load();
    }

    private Predicate lessL, lessD;

    @Before
    public void setup() {
        lessL = FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
        lessD = FunctionDictionary.lookupPredicate("<", SlotType.DOUBLE, SlotType.DOUBLE);
    }

    @Theory
    public void testLongPos(@LotsOfRandomLongs final Long left, @LotsOfRandomLongs final Long right) {
        assumeThat(left, is(lessThan(right)));
        assertTrue(lessL.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testLongNeg(@LotsOfRandomLongs final Long left, @LotsOfRandomLongs final Long right) {
        assumeThat(left, is(not(lessThan(right))));
        assertFalse(lessL.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testDoublePos(@LotsOfRandomDoubles final Double left, @LotsOfRandomDoubles final Double right) {
        assumeThat(left, is(lessThan(right)));
        assertTrue(lessD.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

    @Theory
    public void testDoubleNeg(@LotsOfRandomDoubles final Double left, @LotsOfRandomDoubles final Double right) {
        assumeThat(left, is(not(lessThan(right))));
        assertFalse(lessD.evaluate(new LazyObject<>(left), new LazyObject<>(right)));
    }

}
