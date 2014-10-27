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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.PredicateBuilder;
import test.jamocha.util.SlotAddressMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class PathCollectorTest {

	final Predicate equals = FunctionDictionary.lookupPredicate("=", DOUBLE, DOUBLE);
	final Predicate boolEq = FunctionDictionary.lookupPredicate("=", BOOLEAN, BOOLEAN);
	final Template template = MemoryFactory.getMemoryFactory().newTemplate("", "", Slot.DOUBLE,
			Slot.DOUBLE);
	final SlotAddressMockup s1 = new SlotAddressMockup(0), s2 = new SlotAddressMockup(1);
	final Path p1 = new Path(template), p2 = new Path(template), p3 = new Path(template),
			p4 = new Path(template);
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
				new PathFilter(new PredicateBuilder(equals).addPath(p2, s1).addPath(p2, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p1, s1).addPath(p2, s2)
						.buildPFE());
		// 11 22 21 32
		c =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p2, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p2, s1).addPath(p3, s2)
						.buildPFE());
		// 11 32 31 12
		d =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p3, s1).addPath(p1, s2)
						.buildPFE());
		// 11 12 21 22 31 32 41 42
		e =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p1, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p2, s1).addPath(p2, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p3, s1).addPath(p3, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p4, s1).addPath(p4, s2)
						.buildPFE());
		// 11 32 31 22 41 22 21 12
		f =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p3, s1).addPath(p2, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p4, s1).addPath(p2, s2)
						.buildPFE(), new PredicateBuilder(equals).addPath(p2, s1).addPath(p1, s2)
						.buildPFE());
		// 11 32 32 41 41 22 21 12
		g =
				new PathFilter(new PredicateBuilder(equals).addPath(p1, s1).addPath(p3, s2)
						.buildPFE(), new PredicateBuilder(boolEq)
						.addFunction(
								new PredicateBuilder(equals).addPath(p3, s2).addPath(p4, s1)
										.build())
						.addFunction(
								new PredicateBuilder(equals).addPath(p4, s1).addPath(p2, s2)
										.build()).buildPFE(), new PredicateBuilder(equals)
						.addPath(p2, s1).addPath(p1, s2).buildPFE());
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

	/**
	 * Test method for {@link org.jamocha.filter.PathCollector#newLinkedHashSet()}.
	 */
	@Test
	public void testNewLinkedHashSet() {
		assertArrayEquals(new Path[] { p1 }, PathCollector.newLinkedHashSet().collectAll(a)
				.getPathsArray());
		assertArrayEquals(new Path[] { p2, p1 }, PathCollector.newLinkedHashSet().collectAll(b)
				.getPathsArray());
		assertArrayEquals(new Path[] { p1, p2, p3 }, PathCollector.newLinkedHashSet().collectAll(c)
				.getPathsArray());
		assertArrayEquals(new Path[] { p1, p3 }, PathCollector.newLinkedHashSet().collectAll(d)
				.getPathsArray());
		assertArrayEquals(new Path[] { p1, p2, p3, p4 }, PathCollector.newLinkedHashSet()
				.collectAll(e).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p2, p4 }, PathCollector.newLinkedHashSet()
				.collectAll(f).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p4, p2 }, PathCollector.newLinkedHashSet()
				.collectAll(g).getPathsArray());
	}

	/**
	 * Test method for {@link org.jamocha.filter.PathCollector#newArrayList()}.
	 */
	@Test
	public void testNewArrayList() {
		assertArrayEquals(new Path[] { p1, p1 }, PathCollector.newArrayList().collectAll(a)
				.getPathsArray());
		assertArrayEquals(new Path[] { p2, p2, p1, p2 }, PathCollector.newArrayList().collectAll(b)
				.getPathsArray());
		assertArrayEquals(new Path[] { p1, p2, p2, p3 }, PathCollector.newArrayList().collectAll(c)
				.getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p3, p1 }, PathCollector.newArrayList().collectAll(d)
				.getPathsArray());
		assertArrayEquals(new Path[] { p1, p1, p2, p2, p3, p3, p4, p4 }, PathCollector
				.newArrayList().collectAll(e).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p3, p2, p4, p2, p2, p1 }, PathCollector
				.newArrayList().collectAll(f).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p3, p4, p4, p2, p2, p1 }, PathCollector
				.newArrayList().collectAll(g).getPathsArray());
	}

	/**
	 * Test method for {@link org.jamocha.filter.PathCollector#newLinkedList()}.
	 */
	@Test
	public void testNewLinkedList() {
		assertArrayEquals(new Path[] { p1, p1 }, PathCollector.newLinkedList().collectAll(a)
				.getPathsArray());
		assertArrayEquals(new Path[] { p2, p2, p1, p2 }, PathCollector.newLinkedList()
				.collectAll(b).getPathsArray());
		assertArrayEquals(new Path[] { p1, p2, p2, p3 }, PathCollector.newLinkedList()
				.collectAll(c).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p3, p1 }, PathCollector.newLinkedList()
				.collectAll(d).getPathsArray());
		assertArrayEquals(new Path[] { p1, p1, p2, p2, p3, p3, p4, p4 }, PathCollector
				.newLinkedList().collectAll(e).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p3, p2, p4, p2, p2, p1 }, PathCollector
				.newLinkedList().collectAll(f).getPathsArray());
		assertArrayEquals(new Path[] { p1, p3, p3, p4, p4, p2, p2, p1 }, PathCollector
				.newLinkedList().collectAll(g).getPathsArray());
	}

}
