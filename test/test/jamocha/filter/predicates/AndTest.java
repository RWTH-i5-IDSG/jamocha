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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.impls.predicates.And;
import org.junit.BeforeClass;
import org.junit.Test;

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
		Predicate and =
				FunctionDictionary.lookupPredicate("AND", SlotType.BOOLEAN, SlotType.BOOLEAN);
		assertTrue(and.evaluate(new LazyObject(true), new LazyObject(true)));
		assertFalse(and.evaluate(new LazyObject(true), new LazyObject(false)));
		assertFalse(and.evaluate(new LazyObject(false), new LazyObject(true)));
		assertFalse(and.evaluate(new LazyObject(false), new LazyObject(false)));
	}

}
