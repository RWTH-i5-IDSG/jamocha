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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.jamocha.dn.memory.SlotType.BOOLEAN;
import static org.jamocha.dn.memory.SlotType.DOUBLE;
import static org.junit.Assert.assertThat;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.PredicateBuilder;
import test.jamocha.util.SlotAddressMockup;
import test.jamocha.util.Slots;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class PathCollectorTest {

	final Predicate equals = FunctionDictionary.lookupPredicate("=", DOUBLE, DOUBLE);
	final Predicate boolEq = FunctionDictionary.lookupPredicate("=", BOOLEAN, BOOLEAN);
	final Template template = MemoryFactory.getMemoryFactory().newTemplate("", "", Slots.newDouble("s1"),
			Slots.newDouble("s2"));
	final SlotAddressMockup s1 = new SlotAddressMockup(0), s2 = new SlotAddressMockup(1);
	final Path p1 = new Path(template), p2 = new Path(template), p3 = new Path(template), p4 = new Path(template);
	PathNodeFilterSet a, b, c, d, e, f, g;

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
		a =
				PathNodeFilterSet.newRegularPathNodeFilterSet(new PredicateBuilder(equals).addPath(p1, s1)
						.addPath(p1, s2).buildFilter());
		// 21 22 11 22
		b =
				PathNodeFilterSet.newRegularPathNodeFilterSet(
						new PredicateBuilder(equals).addPath(p2, s1).addPath(p2, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p2, s2).buildFilter());
		// 11 22 21 32
		c =
				PathNodeFilterSet.newRegularPathNodeFilterSet(
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p2, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p2, s1).addPath(p3, s2).buildFilter());
		// 11 32 31 12
		d =
				PathNodeFilterSet.newRegularPathNodeFilterSet(
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p3, s1).addPath(p1, s2).buildFilter());
		// 11 12 21 22 31 32 41 42
		e =
				PathNodeFilterSet.newRegularPathNodeFilterSet(
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p1, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p2, s1).addPath(p2, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p3, s1).addPath(p3, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p4, s1).addPath(p4, s2).buildFilter());
		// 11 32 31 22 41 22 21 12
		f =
				PathNodeFilterSet.newRegularPathNodeFilterSet(
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p3, s1).addPath(p2, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p4, s1).addPath(p2, s2).buildFilter(),
						new PredicateBuilder(equals).addPath(p2, s1).addPath(p1, s2).buildFilter());
		// 11 32 32 41 41 22 21 12
		g =
				PathNodeFilterSet.newRegularPathNodeFilterSet(
						new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2).buildFilter(),
						new PredicateBuilder(boolEq)
								.addFunction(new PredicateBuilder(equals).addPath(p3, s2).addPath(p4, s1).build())
								.addFunction(new PredicateBuilder(equals).addPath(p4, s1).addPath(p2, s2).build())
								.buildFilter(), new PredicateBuilder(equals).addPath(p2, s1).addPath(p1, s2)
								.buildFilter());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.jamocha.filter.PathCollector#newHashSet()}.
	 */
	@Test
	public void testNewHashSet() {
		assertThat(PathCollector.newHashSet().collectAll(a).getPaths(), hasItems(p1));
		assertThat(PathCollector.newHashSet().collectAll(b).getPaths(), hasItems(p1, p2));
		assertThat(PathCollector.newHashSet().collectAll(c).getPaths(), hasItems(p1, p2, p3));
		assertThat(PathCollector.newHashSet().collectAll(d).getPaths(), hasItems(p1, p3));
		assertThat(PathCollector.newHashSet().collectAll(e).getPaths(), hasItems(p1, p2, p3, p4));
		assertThat(PathCollector.newHashSet().collectAll(f).getPaths(), hasItems(p1, p2, p3, p4));
		assertThat(PathCollector.newHashSet().collectAll(g).getPaths(), hasItems(p1, p2, p3, p4));
	}
}
