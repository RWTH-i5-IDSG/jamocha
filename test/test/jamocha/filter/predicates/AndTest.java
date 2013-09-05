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
import org.jamocha.filter.Function;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.jamocha.filter.impls.predicates.And;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestCase for the {@link And} class using Theories.
 * 
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 */
public class AndTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	@Test
	public void testAnd() {
		Function and = TODODatenkrakeFunktionen.lookup("AND", SlotType.BOOLEAN, SlotType.BOOLEAN);
		assertTrue((Boolean) (and.evaluate(true, true)));
		assertFalse((Boolean) (and.evaluate(true, false)));
		assertFalse((Boolean) (and.evaluate(false, true)));
		assertFalse((Boolean) (and.evaluate(false, false)));
	}

}