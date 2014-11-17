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
package test.jamocha.filter;

import static org.jamocha.dn.memory.SlotType.BOOLEAN;
import static org.jamocha.dn.memory.SlotType.DOUBLE;
import static org.junit.Assert.assertArrayEquals;
import static test.jamocha.util.CounterColumnMatcherMockup.counterColumnMatcherMockup;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.PathFilterToAddressFilterTranslator;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.SlotInFactAddressCollector;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FactAddressMockup;
import test.jamocha.util.PredicateBuilder;
import test.jamocha.util.SlotAddressMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class FilterTranslatorTest {
	final Predicate equals = FunctionDictionary.lookupPredicate("=", DOUBLE, DOUBLE);
	final Predicate boolEq = FunctionDictionary.lookupPredicate("=", BOOLEAN, BOOLEAN);
	final Template template = MemoryFactory.getMemoryFactory().newTemplate("", "", Slot.DOUBLE, Slot.DOUBLE);
	final SlotAddressMockup s1 = new SlotAddressMockup(0), s2 = new SlotAddressMockup(1);
	final FactAddressMockup f1 = new FactAddressMockup(0), f2 = new FactAddressMockup(1),
			f3 = new FactAddressMockup(2), f4 = new FactAddressMockup(3);
	final Path p1 = new Path(template, null, f1), p2 = new Path(template, null, f2), p3 = new Path(template, null, f3),
			p4 = new Path(template, null, f4);
	PathFilter a, b, c, d, e, f, g;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// 11 12
		a = new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p1, s2).buildPFE());
		// 21 22 11 22
		b =
				new PathFilter(new PredicateBuilder(equals).addPath(p2, s1).addPath(p2, s2).buildPFE(),
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p2, s2).buildPFE());
		// 11 22 21 32
		c =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p2, s2).buildPFE(),
						new PredicateBuilder(equals).addPath(p2, s1).addPath(p3, s2).buildPFE());
		// 11 32 31 12
		d =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2).buildPFE(),
						new PredicateBuilder(equals).addPath(p3, s1).addPath(p1, s2).buildPFE());
		// 11 12 21 22 31 32 41 42
		e =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p1, s2).buildPFE(),
						new PredicateBuilder(equals).addPath(p2, s1).addPath(p2, s2).buildPFE(), new PredicateBuilder(
								equals).addPath(p3, s1).addPath(p3, s2).buildPFE(), new PredicateBuilder(equals)
								.addPath(p4, s1).addPath(p4, s2).buildPFE());
		// 11 32 31 22 41 22 21 12
		f =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2).buildPFE(),
						new PredicateBuilder(equals).addPath(p3, s1).addPath(p2, s2).buildPFE(), new PredicateBuilder(
								equals).addPath(p4, s1).addPath(p2, s2).buildPFE(), new PredicateBuilder(equals)
								.addPath(p2, s1).addPath(p1, s2).buildPFE());
		// 11 32 32 41 41 22 21 12
		g =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2).buildPFE(),
						new PredicateBuilder(boolEq)
								.addFunction(new PredicateBuilder(equals).addPath(p3, s2).addPath(p4, s1).build())
								.addFunction(new PredicateBuilder(equals).addPath(p4, s1).addPath(p2, s2).build())
								.buildPFE(), new PredicateBuilder(equals).addPath(p2, s1).addPath(p1, s2).buildPFE());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.jamocha.filter.PathFilterToAddressFilterTranslator#translate(PathFilter, org.jamocha.dn.memory.CounterColumnMatcher)}
	 * .
	 */
	@Test
	public void testTranslate() {
		assertArrayEquals(
				new SlotInFactAddress[] { new SlotInFactAddress(f1, s1), new SlotInFactAddress(f1, s2) },
				SlotInFactAddressCollector.newArrayList()
						.collect(PathFilterToAddressFilterTranslator.translate(a, counterColumnMatcherMockup))
						.getAddressesArray());
		assertArrayEquals(new SlotInFactAddress[] { new SlotInFactAddress(f2, s1), new SlotInFactAddress(f2, s2),
				new SlotInFactAddress(f1, s1), new SlotInFactAddress(f2, s2) }, SlotInFactAddressCollector
				.newArrayList().collect(PathFilterToAddressFilterTranslator.translate(b, counterColumnMatcherMockup))
				.getAddressesArray());
		assertArrayEquals(new SlotInFactAddress[] { new SlotInFactAddress(f1, s1), new SlotInFactAddress(f2, s2),
				new SlotInFactAddress(f2, s1), new SlotInFactAddress(f3, s2) }, SlotInFactAddressCollector
				.newArrayList().collect(PathFilterToAddressFilterTranslator.translate(c, counterColumnMatcherMockup))
				.getAddressesArray());
		assertArrayEquals(new SlotInFactAddress[] { new SlotInFactAddress(f1, s1), new SlotInFactAddress(f3, s2),
				new SlotInFactAddress(f3, s1), new SlotInFactAddress(f1, s2) }, SlotInFactAddressCollector
				.newArrayList().collect(PathFilterToAddressFilterTranslator.translate(d, counterColumnMatcherMockup))
				.getAddressesArray());
		assertArrayEquals(
				new SlotInFactAddress[] { new SlotInFactAddress(f1, s1), new SlotInFactAddress(f1, s2),
						new SlotInFactAddress(f2, s1), new SlotInFactAddress(f2, s2), new SlotInFactAddress(f3, s1),
						new SlotInFactAddress(f3, s2), new SlotInFactAddress(f4, s1), new SlotInFactAddress(f4, s2) },
				SlotInFactAddressCollector.newArrayList()
						.collect(PathFilterToAddressFilterTranslator.translate(e, counterColumnMatcherMockup))
						.getAddressesArray());
		assertArrayEquals(
				new SlotInFactAddress[] { new SlotInFactAddress(f1, s1), new SlotInFactAddress(f3, s2),
						new SlotInFactAddress(f3, s1), new SlotInFactAddress(f2, s2), new SlotInFactAddress(f4, s1),
						new SlotInFactAddress(f2, s2), new SlotInFactAddress(f2, s1), new SlotInFactAddress(f1, s2) },
				SlotInFactAddressCollector.newArrayList()
						.collect(PathFilterToAddressFilterTranslator.translate(f, counterColumnMatcherMockup))
						.getAddressesArray());
		assertArrayEquals(
				new SlotInFactAddress[] { new SlotInFactAddress(f1, s1), new SlotInFactAddress(f3, s2),
						new SlotInFactAddress(f3, s2), new SlotInFactAddress(f4, s1), new SlotInFactAddress(f4, s1),
						new SlotInFactAddress(f2, s2), new SlotInFactAddress(f2, s1), new SlotInFactAddress(f1, s2) },
				SlotInFactAddressCollector.newArrayList()
						.collect(PathFilterToAddressFilterTranslator.translate(g, counterColumnMatcherMockup))
						.getAddressesArray());
	}
}
