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
package test.jamocha.dn.nodes;

import static org.junit.Assert.assertEquals;
import static test.jamocha.util.AssertsAndRetracts.countAssertsAndRetractsInConflictSet;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.dn.ConflictSet;
import org.jamocha.dn.Network;
import org.jamocha.dn.PlainScheduler;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.filter.FilterMockup;
import test.jamocha.util.AssertsAndRetracts;
import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class TokenProcessingTest {
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FunctionDictionary.load();
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTokenProcessingBetaExistential() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				new Template(SlotType.STRING /* Name */, SlotType.LONG /* Semester */,
						SlotType.STRING /* Studiengang */, SlotType.STRING /* Hobby */);
		final Template prof =
				new Template(SlotType.STRING /* Name */, SlotType.STRING /* Studiengang */);
		final Set<Path> existentialYoungStudent = new HashSet<>();
		final Set<Path> negatedExistentialMatchingProf = new HashSet<>();
		final Path oldStudent = new Path(student), youngStudent = new Path(student), matchingProf =
				new Path(prof);
		existentialYoungStudent.add(youngStudent);
		negatedExistentialMatchingProf.add(matchingProf);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2), studentHobby =
				new SlotAddress(3), profSG = new SlotAddress(1);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
		final Predicate and =
				FunctionDictionary.lookupPredicate("AND", SlotType.BOOLEAN, SlotType.BOOLEAN);

		// get the (old) students with hobby "Coding", for which there are younger (semester-wise)
		// students in the same course of study, but who lack a professor for their course of study
		final PathFilter[] filter =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(existentialYoungStudent, new HashSet<>(),
								new PredicateBuilder(and)
										.addFunction(
												new PredicateBuilder(lessLongLong)
														.addPath(youngStudent, studentSem)
														.addPath(oldStudent, studentSem).build())
										.addFunction(
												new PredicateBuilder(eqStrStr)
														.addPath(youngStudent, studentSG)
														.addPath(oldStudent, studentSG).build())
										.buildPFE()),
						new PathFilter(new HashSet<>(), negatedExistentialMatchingProf,
								new PredicateBuilder(eqStrStr).addPath(oldStudent, studentSG)
										.addPath(matchingProf, profSG).buildPFE()) };
		network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();

		final ConflictSet conflictSet = network.getConflictSet();

		rootNode.assertFact(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFact(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFact(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFact(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Ashcroft", "Geschichte"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Santana", "Biologie"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Kappa", "Informatik"));

		scheduler.run();
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 0,
					assertsAndRetracts.getAsserts());
		}

		rootNode.retractFact(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		rootNode.retractFact(prof.newFact("Prof. Dr. Santana", "Biologie"));
		rootNode.retractFact(prof.newFact("Prof. Dr. Kappa", "Informatik"));

		scheduler.run();
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 2,
					assertsAndRetracts.getAsserts());
		}

		rootNode.retractFact(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.retractFact(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.retractFact(student.newFact("Rachel", 4L, "Informatik", "Coding"));

		scheduler.run();
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 0,
					assertsAndRetracts.getAsserts());
		}
	}

	@Test
	public void testTokenProcessingSimpleSelfJoin() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				new Template(SlotType.STRING /* Name */, SlotType.LONG /* Semester */,
						SlotType.STRING /* Studiengang */, SlotType.STRING /* Hobby */);
		final Path oldStudent = new Path(student), youngStudent = new Path(student);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2), studentHobby =
				new SlotAddress(3), profSG = new SlotAddress(1);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

		final PathFilter[] filter =
				new PathFilter[] { new PathFilter(new PredicateBuilder(lessLongLong)
						.addPath(youngStudent, studentSem).addPath(oldStudent, studentSem)
						.buildPFE(), new PredicateBuilder(eqStrStr)
						.addPath(youngStudent, studentSG).addPath(oldStudent, studentSG).buildPFE()) };
		network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();
		final ConflictSet conflictSet = network.getConflictSet();

		rootNode.assertFact(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFact(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFact(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFact(student.newFact("Erik", 2L, "Informatik", "Rätsel"));

		// Erik - Simon
		// Erik - Rachel
		// Erik - Mike
		// Erik - Samuel
		// Simon - Rachel
		// Simon - Mike
		// Simon - Samuel
		// Rachel - Mike
		// Rachel - Samuel
		// Mike - Samuel

		scheduler.run();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 10,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 10,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
	}

	@Test
	public void testTokenProcessingBeta() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				new Template(SlotType.STRING /* Name */, SlotType.LONG /* Semester */,
						SlotType.STRING /* Studiengang */, SlotType.STRING /* Hobby */);
		final Template prof =
				new Template(SlotType.STRING /* Name */, SlotType.STRING /* Studiengang */);
		final Path oldStudent = new Path(student), youngStudent = new Path(student), matchingProf =
				new Path(prof);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2), studentHobby =
				new SlotAddress(3), profSG = new SlotAddress(1);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

		final PathFilter[] filter =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(new PredicateBuilder(lessLongLong)
								.addPath(youngStudent, studentSem).addPath(oldStudent, studentSem)
								.buildPFE(), new PredicateBuilder(eqStrStr)
								.addPath(youngStudent, studentSG).addPath(oldStudent, studentSG)
								.buildPFE()),
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(youngStudent, studentSG).addPath(matchingProf, profSG)
								.buildPFE()) };
		network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();
		final ConflictSet conflictSet = network.getConflictSet();

		rootNode.assertFact(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFact(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFact(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFact(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Ashcroft", "Geschichte"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Santana", "Biologie"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Kappa", "Informatik"));

		scheduler.run();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 10,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 10,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}

		rootNode.retractFact(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		rootNode.retractFact(prof.newFact("Prof. Dr. Santana", "Biologie"));

		scheduler.run();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 10,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 5,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 5,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
	}

	@Test
	public void testTokenProcessingBetaOneRun() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				new Template(SlotType.STRING /* Name */, SlotType.LONG /* Semester */,
						SlotType.STRING /* Studiengang */, SlotType.STRING /* Hobby */);
		final Template prof =
				new Template(SlotType.STRING /* Name */, SlotType.STRING /* Studiengang */);
		final Path oldStudent = new Path(student), youngStudent = new Path(student), matchingProf =
				new Path(prof);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2), studentHobby =
				new SlotAddress(3), profSG = new SlotAddress(1);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

		final PathFilter[] filter =
				new PathFilter[] {
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(oldStudent, studentHobby)
								.addConstant("Coding", SlotType.STRING).buildPFE()),
						new PathFilter(new PredicateBuilder(lessLongLong)
								.addPath(youngStudent, studentSem).addPath(oldStudent, studentSem)
								.buildPFE(), new PredicateBuilder(eqStrStr)
								.addPath(youngStudent, studentSG).addPath(oldStudent, studentSG)
								.buildPFE()),
						new PathFilter(new PredicateBuilder(eqStrStr)
								.addPath(youngStudent, studentSG).addPath(matchingProf, profSG)
								.buildPFE()) };
		network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();
		final ConflictSet conflictSet = network.getConflictSet();

		rootNode.assertFact(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFact(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFact(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFact(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFact(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Ashcroft", "Geschichte"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Santana", "Biologie"));
		rootNode.assertFact(prof.newFact("Prof. Dr. Kappa", "Informatik"));

		rootNode.retractFact(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		rootNode.retractFact(prof.newFact("Prof. Dr. Santana", "Biologie"));

		// as the retractions kill Dr. Timmes before he leaves the OTN, we don't even get retracts
		// in our conflict set

		scheduler.run();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 5,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 5,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
	}

	@Test
	public void testTokenProcessingSimpleBeta() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 = new Template(SlotType.LONG, SlotType.STRING);
		final Template t2 = new Template(SlotType.DOUBLE, SlotType.STRING);
		final Path p1 = new Path(t1);
		final Path p2 = new Path(t2);
		final SlotAddress slotStr = new SlotAddress(1);

		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

		final PathFilter filter =
				new PathFilter(new PredicateBuilder(eqStrStr).addPath(p1, slotStr)
						.addPath(p2, slotStr).buildPFE());
		network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();

		rootNode.assertFact(t1.newFact(12L, "Micky"));
		rootNode.assertFact(t1.newFact(-2L, "Micky"));
		rootNode.assertFact(t2.newFact(2.1, "Micky"));
		rootNode.assertFact(t2.newFact(-3., "Micky"));
		rootNode.assertFact(t1.newFact(12L, "Sunny"));
		rootNode.assertFact(t1.newFact(-2L, "Becky"));
		rootNode.assertFact(t2.newFact(2.1, "Nelly"));
		rootNode.assertFact(t2.newFact(-3., "Harry"));

		scheduler.run();
		final ConflictSet conflictSet = network.getConflictSet();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 4,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 4,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
	}

	/**
	 * 
	 */
	@Test
	public void testTokenProcessing() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 = new Template(SlotType.LONG, SlotType.STRING, SlotType.BOOLEAN);
		final Path p1 = new Path(t1);
		final SlotAddress slotLong = new SlotAddress(0), slotBool = new SlotAddress(2);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqBoolBool =
				FunctionDictionary.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);

		final PathFilter filter =
				new PathFilter(new PredicateBuilder(eqBoolBool)
						.addPath(p1, slotBool)
						.addFunction(
								new FunctionBuilder(lessLongLong).addPath(p1, slotLong)
										.addConstant(3L, SlotType.LONG).build()).buildPFE());

		network.buildRule(filter);

		// false == 5 < 3
		network.getRootNode().assertFact(new Fact(t1, 5L, "5L&FALSE", false));
		// true != 5 < 3
		network.getRootNode().assertFact(new Fact(t1, 5L, "5L&TRUE", true));
		// true == 2 < 3
		network.getRootNode().assertFact(new Fact(t1, 2L, "2L&TRUE", true));
		// false != 2 < 3
		network.getRootNode().assertFact(new Fact(t1, 2L, "2L&FALSE", false));
		// true == -80 < 3
		network.getRootNode().assertFact(new Fact(t1, -80L, "-80L&TRUE", true));
		// false != -80 < 3
		network.getRootNode().assertFact(new Fact(t1, -80L, "-80L&FALSE", false));
		// false != 0 < 3
		network.getRootNode().assertFact(new Fact(t1, 0L, "0L&FALSE", false));

		scheduler.run();

		final AssertsAndRetracts assertsAndRetracts =
				countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 3,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 0,
				assertsAndRetracts.getRetracts());

	}

	/**
	 * 
	 */
	@Test
	public void testTokenProcessingDummyFilter() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 = new Template(SlotType.LONG, SlotType.STRING, SlotType.BOOLEAN);
		final Path p1 = new Path(t1);

		final FilterMockup filter = FilterMockup.alwaysTrue(p1);

		final RootNode rootNode = network.getRootNode();
		// create OTN
		final ObjectTypeNode otn = new ObjectTypeNode(network, p1);
		// append to root node
		rootNode.putOTN(otn);
		// create & append alpha
		final AlphaNode alphaNode = new AlphaNode(network, filter);
		// create & append terminal
		@SuppressWarnings("unused")
		final TerminalNode terminalNode = new TerminalNode(network, alphaNode);
		AssertsAndRetracts assertsAndRetracts;

		rootNode.assertFact(new Fact(t1, 5L, "5L&FALSE", false));
		rootNode.assertFact(new Fact(t1, 5L, "5L&TRUE", true));
		rootNode.assertFact(new Fact(t1, 2L, "2L&TRUE", true));
		rootNode.assertFact(new Fact(t1, 2L, "2L&FALSE", false));
		rootNode.assertFact(new Fact(t1, -80L, "-80L&TRUE", true));
		rootNode.assertFact(new Fact(t1, -80L, "-80L&FALSE", false));
		rootNode.assertFact(new Fact(t1, 0L, "0L&FALSE", false));
		scheduler.run();

		assertEquals("Amount of facts in otn does not match expected count!", 7, otn.getMemory()
				.size());
		assertEquals("Amount of facts in alpha does not match expected count!", 7, alphaNode
				.getMemory().size());
		assertsAndRetracts = countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 7,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 0,
				assertsAndRetracts.getRetracts());

		rootNode.retractFact(new Fact(t1, 0L, "0L&FALSE", false));
		scheduler.run();

		assertEquals("Amount of facts in otn does not match expected count!", 6, otn.getMemory()
				.size());
		assertEquals("Amount of facts in alpha does not match expected count!", 6, alphaNode
				.getMemory().size());
		assertsAndRetracts = countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 7,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 1,
				assertsAndRetracts.getRetracts());

		rootNode.assertFact(new Fact(t1, 0L, "0L&FALSE", false));
		scheduler.run();

		assertEquals("Amount of facts in otn does not match expected count!", 7, otn.getMemory()
				.size());
		assertEquals("Amount of facts in alpha does not match expected count!", 7, alphaNode
				.getMemory().size());
		assertsAndRetracts = countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 8,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 1,
				assertsAndRetracts.getRetracts());

		rootNode.retractFact(new Fact(t1, 0L, "0L&FALSE", false));
		scheduler.run();

		assertEquals("Amount of facts in otn does not match expected count!", 6, otn.getMemory()
				.size());
		assertEquals("Amount of facts in alpha does not match expected count!", 6, alphaNode
				.getMemory().size());
		assertsAndRetracts = countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 8,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 2,
				assertsAndRetracts.getRetracts());

		rootNode.retractFact(new Fact(t1, 7L, "7L&TRUE", true));
		scheduler.run();

		assertEquals("Amount of facts in otn does not match expected count!", 6, otn.getMemory()
				.size());
		assertEquals("Amount of facts in alpha does not match expected count!", 6, alphaNode
				.getMemory().size());
		assertsAndRetracts = countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 8,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 2,
				assertsAndRetracts.getRetracts());

	}

	@Test
	public void testTokenProcessingDummyFilterOneRun() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 = new Template(SlotType.LONG, SlotType.STRING, SlotType.BOOLEAN);
		final Path p1 = new Path(t1);

		final FilterMockup filter = FilterMockup.alwaysTrue(p1);

		final RootNode rootNode = network.getRootNode();
		// create OTN
		final ObjectTypeNode otn = new ObjectTypeNode(network, p1);
		// append to root node
		rootNode.putOTN(otn);
		// create & append alpha
		final AlphaNode alphaNode = new AlphaNode(network, filter);
		// create & append terminal
		@SuppressWarnings("unused")
		final TerminalNode terminalNode = new TerminalNode(network, alphaNode);

		rootNode.assertFact(new Fact(t1, 5L, "5L&FALSE", false));
		rootNode.assertFact(new Fact(t1, 5L, "5L&TRUE", true));
		rootNode.assertFact(new Fact(t1, 2L, "2L&TRUE", true));
		rootNode.assertFact(new Fact(t1, 2L, "2L&FALSE", false));
		rootNode.assertFact(new Fact(t1, -80L, "-80L&TRUE", true));
		rootNode.assertFact(new Fact(t1, -80L, "-80L&FALSE", false));
		rootNode.assertFact(new Fact(t1, 0L, "0L&FALSE", false));
		rootNode.retractFact(new Fact(t1, 0L, "0L&FALSE", false));
		rootNode.assertFact(new Fact(t1, 0L, "0L&FALSE", false));
		rootNode.retractFact(new Fact(t1, 0L, "0L&FALSE", false));
		rootNode.retractFact(new Fact(t1, 7L, "7L&TRUE", true));
		scheduler.run();

		assertEquals("Amount of facts in otn does not match expected count!", 6, otn.getMemory()
				.size());
		assertEquals("Amount of facts in alpha does not match expected count!", 6, alphaNode
				.getMemory().size());
		final AssertsAndRetracts assertsAndRetracts =
				countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 8,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 2,
				assertsAndRetracts.getRetracts());

	}

	@Test
	public void testTokenProcessingOneRun() throws Exception {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 = new Template(SlotType.LONG, SlotType.STRING, SlotType.BOOLEAN);
		final Path p1 = new Path(t1);
		final SlotAddress slotLong = new SlotAddress(0), slotBool = new SlotAddress(2);

		final Predicate lessLongLong =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqBoolBool =
				FunctionDictionary.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);

		final PathFilter filter =
				new PathFilter(new PredicateBuilder(eqBoolBool)
						.addPath(p1, slotBool)
						.addFunction(
								new FunctionBuilder(lessLongLong).addPath(p1, slotLong)
										.addConstant(3L, SlotType.LONG).build()).buildPFE());
		network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();

		// false == 5 < 3
		rootNode.assertFact(new Fact(t1, 5L, "5L&FALSE", false));
		// true != 5 < 3
		rootNode.assertFact(new Fact(t1, 5L, "5L&TRUE", true));
		// true == 2 < 3
		rootNode.assertFact(new Fact(t1, 2L, "2L&TRUE", true));
		// false != 2 < 3
		rootNode.assertFact(new Fact(t1, 2L, "2L&FALSE", false));
		// true == -80 < 3
		rootNode.assertFact(new Fact(t1, -80L, "-80L&TRUE", true));
		// false != -80 < 3
		rootNode.assertFact(new Fact(t1, -80L, "-80L&FALSE", false));
		// false != 0 < 3
		rootNode.assertFact(new Fact(t1, 0L, "0L&FALSE", false));

		// remove and re-add valid fact
		// false == 5 < 3
		rootNode.assertFact(new Fact(t1, 5L, "5L&FALSE", false));
		// false == 5 < 3
		rootNode.retractFact(new Fact(t1, 5L, "5L&FALSE", false));

		// remove valid fact
		// true == 2 < 3
		rootNode.retractFact(new Fact(t1, 2L, "2L&TRUE", true));

		// remove, re-add and remove valid fact
		// true == -80 < 3
		rootNode.retractFact(new Fact(t1, -80L, "-80L&TRUE", true));
		// true == -80 < 3
		rootNode.assertFact(new Fact(t1, -80L, "-80L&TRUE", true));
		// true == -80 < 3
		rootNode.retractFact(new Fact(t1, -80L, "-80L&TRUE", true));

		// remove invalid fact
		// false != -80 < 3
		rootNode.retractFact(new Fact(t1, -80L, "-80L&FALSE", false));

		scheduler.run();
		final ConflictSet conflictSet = network.getConflictSet();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 5,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 4,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 1,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
	}
}
