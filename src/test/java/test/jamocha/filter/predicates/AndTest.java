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
import org.jamocha.function.impls.predicates.And;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestCase for the {@link And} class using Theories.
 *
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class AndTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FunctionDictionary.load();
    }

    @Test
    public void testAnd() {
        final Predicate and = FunctionDictionary.lookupPredicate(And.IN_CLIPS, SlotType.BOOLEAN, SlotType.BOOLEAN);
        assertTrue(and.evaluate(new LazyObject<>(true), new LazyObject<>(true)));
        assertFalse(and.evaluate(new LazyObject<>(true), new LazyObject<>(false)));
        assertFalse(and.evaluate(new LazyObject<>(false), new LazyObject<>(true)));
        assertFalse(and.evaluate(new LazyObject<>(false), new LazyObject<>(false)));
    }

}
