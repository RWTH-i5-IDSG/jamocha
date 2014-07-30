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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;

import org.jamocha.dn.Network;
import org.jamocha.dn.PlainScheduler;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
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

	private static boolean tryToShareNode(final Network network, final PathFilter filter)
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return network.tryToShareNode(filter);
		// final Method tryToShareNode =
		// Network.class.getDeclaredMethod("tryToShareNode", PathFilter.class);
		// tryToShareNode.setAccessible(true);
		// return (Boolean) tryToShareNode.invoke((Object) network, (Object) filter);
	}

	@Test
	public void testTryToShareNodeSimpleAlphaCase() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template template =
				MemoryFactory.getMemoryFactory().newTemplate("", "", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, ""), new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		final Path pathOne = new Path(template), pathTwo = new Path(template), pathThree =
				new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOne, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathOne, slotLongOne)
								.addPath(pathOne, slotLongTwo).buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwo, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathTwo, slotLongOne)
								.addPath(pathTwo, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathTwo, slotStringOne)
								.addPath(pathTwo, slotStringTwo).buildPFE()) }, filterThree =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathThree, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathThree, slotLongTwo)
								.addPath(pathThree, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathThree, slotStringTwo)
								.addPath(pathThree, slotStringOne).buildPFE()) };

		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (PathFilter filter : filterTwo) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
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
			for (PathFilter filter : filterThree) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			rootNode.addPaths(network, pathArray);
		}

		assertFalse(tryToShareNode(network, filterThree[0]));
		assertFalse(tryToShareNode(network, filterThree[1]));
		assertFalse(tryToShareNode(network, filterThree[2]));

		assertNotEquals(pathOne.getCurrentlyLowestNode(), pathThree.getCurrentlyLowestNode());
		assertNotEquals(pathOne.getFactAddressInCurrentlyLowestNode(),
				pathThree.getFactAddressInCurrentlyLowestNode());
	}

	@Test
	public void testTryToShareNodeSimpleBetaCase() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template template =
				MemoryFactory.getMemoryFactory().newTemplate("", "", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, ""), new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		final Path pathOneA = new Path(template), pathOneB = new Path(template), pathTwoA =
				new Path(template), pathTwoB = new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOneA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathOneA, slotLongOne)
								.addPath(pathOneB, slotLongTwo).buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwoA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathTwoA, slotLongOne)
								.addPath(pathTwoB, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathTwoA, slotStringOne)
								.addPath(pathTwoB, slotStringTwo).buildPFE()) };

		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (PathFilter filter : filterTwo) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
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
		final Network network = new Network(Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template template =
				MemoryFactory.getMemoryFactory().newTemplate("", "", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, ""), new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		final Path pathOneA = new Path(template), pathOneB = new Path(template), pathTwoA =
				new Path(template), pathTwoB = new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOneA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathOneA, slotLongOne)
								.addPath(pathOneB, slotLongTwo).buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwoA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathTwoA, slotLongOne)
								.addPath(pathTwoB, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathTwoA, slotStringOne)
								.addPath(pathTwoB, slotStringTwo).buildPFE()) };

		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (PathFilter filter : filterTwo) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
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
	public void testTryToShareNodeBetaCase2() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);
		final RootNode rootNode = network.getRootNode();

		final Template student =
				MemoryFactory.getMemoryFactory().newTemplate("Student", "Student",
						new Slot(SlotType.STRING, "Name"), new Slot(SlotType.LONG, "Semester"),
						new Slot(SlotType.STRING, "Studiengang"),
						new Slot(SlotType.STRING, "Hobby "));
		final Template prof =
				MemoryFactory.getMemoryFactory()
						.newTemplate("Prof", "Prof", new Slot(SlotType.STRING, "Name"),
								new Slot(SlotType.STRING, "Studiengang"));
		final Path oldStudent1 = new Path(student), youngStudent1 = new Path(student), matchingProf1 =
				new Path(prof), oldStudent2 = new Path(student), youngStudent2 = new Path(student), matchingProf2 =
				new Path(prof);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2), studentHobby =
				new SlotAddress(3), profSG = new SlotAddress(1);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent1, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(new PredicateBuilder(lessLongLong)
								.addPath(youngStudent1, studentSem)
								.addPath(oldStudent1, studentSem).buildPFE(), new PredicateBuilder(
								eqStrStr).addPath(youngStudent1, studentSG)
								.addPath(oldStudent1, studentSG).buildPFE()),
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(youngStudent1, studentSG).addPath(matchingProf1, profSG)
								.buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent2, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(new PredicateBuilder(lessLongLong)
								.addPath(youngStudent2, studentSem)
								.addPath(oldStudent2, studentSem).buildPFE(), new PredicateBuilder(
								eqStrStr).addPath(youngStudent2, studentSG)
								.addPath(oldStudent2, studentSG).buildPFE()),
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(youngStudent2, studentSG).addPath(matchingProf2, profSG)
								.buildPFE()) };
		network.buildRule(filterOne);

		{
			final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
			for (PathFilter filter : filterTwo) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = allPaths.toArray(new Path[allPaths.size()]);
			rootNode.addPaths(network, pathArray);
		}

		assertTrue(tryToShareNode(network, filterTwo[0]));
		assertTrue(tryToShareNode(network, filterTwo[1]));
		assertTrue(tryToShareNode(network, filterTwo[2]));

		assertEquals(oldStudent1.getCurrentlyLowestNode(), oldStudent2.getCurrentlyLowestNode());
		assertEquals(oldStudent1.getFactAddressInCurrentlyLowestNode(),
				oldStudent2.getFactAddressInCurrentlyLowestNode());

		assertEquals(youngStudent1.getCurrentlyLowestNode(), youngStudent2.getCurrentlyLowestNode());
		assertEquals(youngStudent1.getFactAddressInCurrentlyLowestNode(),
				youngStudent2.getFactAddressInCurrentlyLowestNode());

		assertEquals(matchingProf1.getCurrentlyLowestNode(), matchingProf2.getCurrentlyLowestNode());
		assertEquals(matchingProf1.getFactAddressInCurrentlyLowestNode(),
				matchingProf2.getFactAddressInCurrentlyLowestNode());
	}

	@Test
	public void testBuildRuleSimpleAlphaCase() {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);

		final Template template =
				MemoryFactory.getMemoryFactory().newTemplate("", "", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, ""), new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		final Path pathOne = new Path(template), pathTwo = new Path(template), pathThree =
				new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOne, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathOne, slotLongOne)
								.addPath(pathOne, slotLongTwo).buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwo, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathTwo, slotLongOne)
								.addPath(pathTwo, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathTwo, slotStringOne)
								.addPath(pathTwo, slotStringTwo).buildPFE()) }, filterThree =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathThree, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathThree, slotLongTwo)
								.addPath(pathThree, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathThree, slotStringTwo)
								.addPath(pathThree, slotStringOne).buildPFE()) };

		network.buildRule(filterOne);
		network.buildRule(filterTwo);
		network.buildRule(filterThree);

		assertEquals(pathOne.getCurrentlyLowestNode(), pathTwo.getCurrentlyLowestNode()
				.getIncomingEdges()[0].getSourceNode());
		assertEquals(
				pathOne.getFactAddressInCurrentlyLowestNode(),
				pathTwo.getCurrentlyLowestNode()
						.delocalizeAddress(pathTwo.getFactAddressInCurrentlyLowestNode())
						.getAddress());

		assertNotEquals(pathOne.getCurrentlyLowestNode(), pathThree.getCurrentlyLowestNode()
				.getIncomingEdges()[0].getSourceNode());
		assertNotEquals(
				pathOne.getFactAddressInCurrentlyLowestNode(),
				pathThree.getCurrentlyLowestNode()
						.delocalizeAddress(pathThree.getFactAddressInCurrentlyLowestNode())
						.getAddress());
	}

	@Test
	public void testBuildRuleSimpleBetaCase() {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);

		final Template template =
				MemoryFactory.getMemoryFactory().newTemplate("", "", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, ""), new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		;
		final Path pathOneA = new Path(template), pathOneB = new Path(template), pathTwoA =
				new Path(template), pathTwoB = new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOneA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathOneA, slotLongOne)
								.addPath(pathOneB, slotLongTwo).buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwoA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathTwoA, slotLongOne)
								.addPath(pathTwoB, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathTwoA, slotStringOne)
								.addPath(pathTwoB, slotStringTwo).buildPFE()) };

		network.buildRule(filterOne);
		network.buildRule(filterTwo);

		assertEquals(pathOneA.getCurrentlyLowestNode(), pathTwoA.getCurrentlyLowestNode()
				.getIncomingEdges()[0].getSourceNode());
		assertEquals(
				pathOneA.getFactAddressInCurrentlyLowestNode(),
				pathTwoA.getCurrentlyLowestNode()
						.delocalizeAddress(pathTwoA.getFactAddressInCurrentlyLowestNode())
						.getAddress());

		assertEquals(pathOneB.getCurrentlyLowestNode(), pathTwoB.getCurrentlyLowestNode()
				.getIncomingEdges()[0].getSourceNode());
		assertEquals(
				pathOneB.getFactAddressInCurrentlyLowestNode(),
				pathTwoB.getCurrentlyLowestNode()
						.delocalizeAddress(pathTwoB.getFactAddressInCurrentlyLowestNode())
						.getAddress());
	}

	@Test
	public void testBuildRuleBetaCase() {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);

		final Template template =
				MemoryFactory.getMemoryFactory().newTemplate("", "", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, ""), new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		;
		final Path pathOneA = new Path(template), pathOneB = new Path(template), pathTwoA =
				new Path(template), pathTwoB = new Path(template);

		final SlotAddress slotStringOne = new SlotAddress(0), slotStringTwo = new SlotAddress(3), slotLongOne =
				new SlotAddress(1), slotLongTwo = new SlotAddress(2);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathOneA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathOneA, slotLongOne)
								.addPath(pathOneB, slotLongTwo).buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(lessL).addConstant(3L, SlotType.LONG)
								.addPath(pathTwoA, slotLongOne).buildPFE()),
						new PathFilter(new PredicateBuilder(lessL).addPath(pathTwoA, slotLongOne)
								.addPath(pathTwoB, slotLongTwo).buildPFE()),
						new PathFilter(new PredicateBuilder(eqS).addPath(pathTwoA, slotStringOne)
								.addPath(pathTwoB, slotStringTwo).buildPFE()) };

		network.buildRule(filterOne);
		network.buildRule(filterTwo);

		assertEquals(pathOneA.getCurrentlyLowestNode(), pathTwoA.getCurrentlyLowestNode()
				.getIncomingEdges()[0].getSourceNode());
		assertEquals(
				pathOneA.getFactAddressInCurrentlyLowestNode(),
				pathTwoA.getCurrentlyLowestNode()
						.delocalizeAddress(pathTwoA.getFactAddressInCurrentlyLowestNode())
						.getAddress());

		assertEquals(pathOneB.getCurrentlyLowestNode(), pathTwoB.getCurrentlyLowestNode()
				.getIncomingEdges()[0].getSourceNode());
		assertEquals(
				pathOneB.getFactAddressInCurrentlyLowestNode(),
				pathTwoB.getCurrentlyLowestNode()
						.delocalizeAddress(pathTwoB.getFactAddressInCurrentlyLowestNode())
						.getAddress());
	}

	@Test
	public void testBuildRuleBetaCase2() {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network = new Network(Integer.MAX_VALUE, scheduler);

		final Template student =
				MemoryFactory.getMemoryFactory().newTemplate("Student", "Student",
						new Slot(SlotType.STRING, "Name"), new Slot(SlotType.LONG, "Semester"),
						new Slot(SlotType.STRING, "Studiengang"),
						new Slot(SlotType.STRING, "Hobby"));
		final Template prof =
				MemoryFactory.getMemoryFactory()
						.newTemplate("Prof", "Prof", new Slot(SlotType.STRING, "Name"),
								new Slot(SlotType.STRING, "Studiengang"));
		final Path oldStudent1 = new Path(student), youngStudent1 = new Path(student), matchingProf1 =
				new Path(prof), oldStudent2 = new Path(student), youngStudent2 = new Path(student), matchingProf2 =
				new Path(prof);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2), studentHobby =
				new SlotAddress(3), profSG = new SlotAddress(1);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

		final PathFilter[] filterOne =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent1, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(new PredicateBuilder(lessLongLong)
								.addPath(youngStudent1, studentSem)
								.addPath(oldStudent1, studentSem).buildPFE(), new PredicateBuilder(
								eqStrStr).addPath(youngStudent1, studentSG)
								.addPath(oldStudent1, studentSG).buildPFE()),
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(youngStudent1, studentSG).addPath(matchingProf1, profSG)
								.buildPFE()) }, filterTwo =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent2, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(new PredicateBuilder(lessLongLong)
								.addPath(youngStudent2, studentSem)
								.addPath(oldStudent2, studentSem).buildPFE(), new PredicateBuilder(
								eqStrStr).addPath(youngStudent2, studentSG)
								.addPath(oldStudent2, studentSG).buildPFE()),
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(youngStudent2, studentSG).addPath(matchingProf2, profSG)
								.buildPFE()) };
		network.buildRule(filterOne);
		network.buildRule(filterTwo);

		assertEquals(oldStudent1.getCurrentlyLowestNode(), oldStudent2.getCurrentlyLowestNode());
		assertEquals(oldStudent1.getFactAddressInCurrentlyLowestNode(),
				oldStudent2.getFactAddressInCurrentlyLowestNode());

		assertEquals(youngStudent1.getCurrentlyLowestNode(), youngStudent2.getCurrentlyLowestNode());
		assertEquals(youngStudent1.getFactAddressInCurrentlyLowestNode(),
				youngStudent2.getFactAddressInCurrentlyLowestNode());

		assertEquals(matchingProf1.getCurrentlyLowestNode(), matchingProf2.getCurrentlyLowestNode());
		assertEquals(matchingProf1.getFactAddressInCurrentlyLowestNode(),
				matchingProf2.getFactAddressInCurrentlyLowestNode());
	}
}
