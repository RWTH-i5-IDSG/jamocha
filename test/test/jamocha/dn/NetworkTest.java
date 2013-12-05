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
package test.jamocha.dn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;

import org.jamocha.dn.Network;
import org.jamocha.dn.PlainScheduler;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Path;
import org.jamocha.filter.Predicate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.PredicateBuilder;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 * @see Network
 */
public class NetworkTest {

	final Function<Double> plusD = FunctionDictionary.<Double> lookup("+", SlotType.DOUBLE,
			SlotType.DOUBLE);
	final Function<Double> minusD = FunctionDictionary.<Double> lookup("-", SlotType.DOUBLE,
			SlotType.DOUBLE);
	final Predicate eqD = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
	final Function<Long> plusL = FunctionDictionary
			.<Long> lookup("+", SlotType.LONG, SlotType.LONG);
	final Function<Long> minusL = FunctionDictionary.<Long> lookup("-", SlotType.LONG,
			SlotType.LONG);
	final Predicate lessL = FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
	final Predicate eqL = FunctionDictionary.lookupPredicate("=", SlotType.LONG, SlotType.LONG);
	final Predicate eqS = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FunctionDictionary.load();
	}

	@Before
	public void init() {
	}

	private static boolean tryToShareNode(final Network network, final Filter filter)
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Method tryToShareNode =
				Network.class.getDeclaredMethod("tryToShareNode", Filter.class);
		tryToShareNode.setAccessible(true);
		return (Boolean) tryToShareNode.invoke((Object) network, (Object) filter);
	}

	@Test
	public void testTryToShareNodeSimpleAlphaCase() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template template =
				new Template(SlotType.STRING, SlotType.LONG, SlotType.LONG, SlotType.STRING);
		final Path pathOne = new Path(template), pathTwo = new Path(template), pathThree =
				new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final Filter[] filterOne =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOne, slotLongOne).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathOne, slotLongOne)
								.addPath(pathOne, slotLongTwo).build()) }, filterTwo =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwo, slotLongOne).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathTwo, slotLongOne)
								.addPath(pathTwo, slotLongTwo).build()),
						new Filter(new PredicateBuilder(eqS).addPath(pathTwo, slotStringOne)
								.addPath(pathTwo, slotStringTwo).build()) }, filterThree =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathThree, slotLongTwo).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathThree, slotLongTwo)
								.addPath(pathThree, slotLongOne).build()),
						new Filter(new PredicateBuilder(eqS).addPath(pathThree, slotStringTwo)
								.addPath(pathThree, slotStringOne).build()) };

		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (Filter filter : filterTwo) {
				final LinkedHashSet<Path> paths = filter.gatherPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			rootNode.addPaths(network, pathArray);
		}

		assertTrue(tryToShareNode(network, filterTwo[0]));
		assertTrue(tryToShareNode(network, filterTwo[1]));
		assertFalse(tryToShareNode(network, filterTwo[2]));

		assertEquals(pathOne.getCurrentlyLowestNode(), pathTwo.getCurrentlyLowestNode());
		assertEquals(pathOne.getFactAddressInCurrentlyLowestNode(),
				pathTwo.getFactAddressInCurrentlyLowestNode());

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (Filter filter : filterThree) {
				final LinkedHashSet<Path> paths = filter.gatherPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			rootNode.addPaths(network, pathArray);
		}

		assertFalse(tryToShareNode(network, filterThree[0]));
		assertFalse(tryToShareNode(network, filterThree[1]));
		assertFalse(tryToShareNode(network, filterThree[2]));
	}

	@Test
	public void testTryToShareNodeSimpleBetaCase() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template template =
				new Template(SlotType.STRING, SlotType.LONG, SlotType.LONG, SlotType.STRING);
		final Path pathOneA = new Path(template), pathOneB = new Path(template), pathTwoA =
				new Path(template), pathTwoB = new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final Filter[] filterOne =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOneA, slotLongOne).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathOneA, slotLongOne)
								.addPath(pathOneB, slotLongTwo).build()) }, filterTwo =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwoA, slotLongOne).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathTwoA, slotLongOne)
								.addPath(pathTwoB, slotLongTwo).build()),
						new Filter(new PredicateBuilder(eqS).addPath(pathTwoA, slotStringOne)
								.addPath(pathTwoB, slotStringTwo).build()) };

		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (Filter filter : filterTwo) {
				final LinkedHashSet<Path> paths = filter.gatherPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			rootNode.addPaths(network, pathArray);
		}

		assertTrue(tryToShareNode(network, filterTwo[0]));
		assertTrue(tryToShareNode(network, filterTwo[1]));
		assertFalse(tryToShareNode(network, filterTwo[2]));

		assertEquals(pathOneA.getCurrentlyLowestNode(), pathTwoA.getCurrentlyLowestNode());
		assertEquals(pathOneA.getFactAddressInCurrentlyLowestNode(),
				pathTwoA.getFactAddressInCurrentlyLowestNode());

		assertEquals(pathOneB.getCurrentlyLowestNode(), pathTwoB.getCurrentlyLowestNode());
		assertEquals(pathOneB.getFactAddressInCurrentlyLowestNode(),
				pathTwoB.getFactAddressInCurrentlyLowestNode());
	}

	@Test
	public void testTryToShareNodeBetaCase() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template template =
				new Template(SlotType.STRING, SlotType.LONG, SlotType.LONG, SlotType.STRING);
		final Path pathOneA = new Path(template), pathOneB = new Path(template), pathTwoA =
				new Path(template), pathTwoB = new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final Filter[] filterOne =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOneA, slotLongOne).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathOneA, slotLongOne)
								.addPath(pathOneB, slotLongTwo).build()) }, filterTwo =
				new Filter[] {
						new Filter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwoA, slotLongOne).build()),
						new Filter(new PredicateBuilder(lessL).addPath(pathTwoA, slotLongOne)
								.addPath(pathTwoB, slotLongTwo).build()),
						new Filter(new PredicateBuilder(eqS).addPath(pathTwoA, slotStringOne)
								.addPath(pathTwoB, slotStringTwo).build()) };

		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (Filter filter : filterTwo) {
				final LinkedHashSet<Path> paths = filter.gatherPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			rootNode.addPaths(network, pathArray);
		}

		assertTrue(tryToShareNode(network, filterTwo[0]));
		assertTrue(tryToShareNode(network, filterTwo[1]));
		assertFalse(tryToShareNode(network, filterTwo[2]));

		assertEquals(pathOneA.getCurrentlyLowestNode(), pathTwoA.getCurrentlyLowestNode());
		assertEquals(pathOneA.getFactAddressInCurrentlyLowestNode(),
				pathTwoA.getFactAddressInCurrentlyLowestNode());

		assertEquals(pathOneB.getCurrentlyLowestNode(), pathTwoB.getCurrentlyLowestNode());
		assertEquals(pathOneB.getFactAddressInCurrentlyLowestNode(),
				pathTwoB.getFactAddressInCurrentlyLowestNode());
	}

	@Test
	public void testBuildRule() {
		// build some filters with the given predicates and build them with buildRule
		// Afterwards iterate the network and check if everything is in order.
		fail("Not yet implemented");
	}

}
