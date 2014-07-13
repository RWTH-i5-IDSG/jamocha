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
import static org.junit.Assert.assertThat;
import static test.jamocha.util.AssertsAndRetracts.countAssertsAndRetractsInConflictSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.jamocha.dn.ConflictSet;
import org.jamocha.dn.Network;
import org.jamocha.dn.PlainScheduler;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.impls.predicates.And;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.filter.FilterMockup;
import test.jamocha.filter.FilterMockup.PathAndSlotAddress;
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
	public void testTokenProcessingBetaExistential() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				MemoryFactory.getMemoryFactory().newTemplate("Student",
						new Slot(SlotType.STRING, "Name"), new Slot(SlotType.LONG, "Semester"),
						new Slot(SlotType.STRING, "Studiengang"),
						new Slot(SlotType.STRING, "Hobby"));
		final Template prof =
				MemoryFactory.getMemoryFactory()
						.newTemplate("Prof", new Slot(SlotType.STRING, "Name"),
								new Slot(SlotType.STRING, "Studiengang"));
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
				FunctionDictionary.lookupPredicate(And.inClips, SlotType.BOOLEAN, SlotType.BOOLEAN);

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
		final TerminalNode terminalNode = network.buildRule(filter);

		final RootNode rootNode = network.getRootNode();

		final ConflictSet conflictSet = network.getConflictSet();

		final Edge edgeToTerminal = terminalNode.getEdge();
		final Node betaNodeProf = edgeToTerminal.getSourceNode();
		assertThat(betaNodeProf, Matchers.instanceOf(BetaNode.class));

		final Edge[] profIncomingEdges = betaNodeProf.getIncomingEdges();
		final int exIndex =
				1 - profIncomingEdges[0].getSourceNode().getOutgoingExistentialEdges().size();
		final Node profOTN = profIncomingEdges[exIndex].getSourceNode();
		assertThat(profOTN, Matchers.instanceOf(ObjectTypeNode.class));
		final Node betaNodeStudent = profIncomingEdges[1 - exIndex].getSourceNode();
		assertThat(betaNodeStudent, Matchers.instanceOf(BetaNode.class));
		assertEquals(1, betaNodeStudent.getOutgoingEdges().size());
		assertEquals(0, betaNodeStudent.getOutgoingExistentialEdges().size());
		final Edge[] studentIncomingEdges = betaNodeStudent.getIncomingEdges();
		assertEquals(2, studentIncomingEdges.length);
		final int alphaIndex = studentIncomingEdges[0].getSourceNode() instanceof AlphaNode ? 0 : 1;
		final Node alphaNode = studentIncomingEdges[1 - alphaIndex].getSourceNode();
		assertThat(alphaNode, Matchers.instanceOf(AlphaNode.class));
		final Node studentOTN = studentIncomingEdges[0].getSourceNode();
		assertThat(studentOTN, Matchers.instanceOf(ObjectTypeNode.class));
		assertEquals(1, studentOTN.getOutgoingExistentialEdges().size());
		assertEquals(2, studentOTN.getOutgoingEdges().size());

		final FactIdentifier[] simon =
				rootNode.assertFacts(student.newFact("Simon", 3L, "Informatik", "Schach"));
		final FactIdentifier[] rachel =
				rootNode.assertFacts(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFacts(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		final FactIdentifier[] erik =
				rootNode.assertFacts(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.assertFacts(prof.newFact("Prof. Dr. Ashcroft", "Geschichte"));
		final FactIdentifier[] timmes =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		final FactIdentifier[] santana =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Santana", "Biologie"));
		final FactIdentifier[] kappa =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Kappa", "Informatik"));

		scheduler.run();
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 0,
					assertsAndRetracts.getAsserts());
		}

		rootNode.retractFacts(timmes);
		rootNode.retractFacts(santana);
		rootNode.retractFacts(kappa);

		scheduler.run();
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 2,
					assertsAndRetracts.getAsserts());
		}

		rootNode.retractFacts(simon);
		rootNode.retractFacts(erik);
		rootNode.retractFacts(rachel);

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
	public void testTokenProcessingSimpleExistential() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, "")), t2 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.BOOLEAN, ""));
		final Path p1 = new Path(t1), p2 = new Path(t2);
		final SlotAddress s1 = new SlotAddress(0), s2 = new SlotAddress(1);

		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
		final Predicate eqBoolBool =
				FunctionDictionary.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);
		final Predicate and =
				FunctionDictionary.lookupPredicate(And.inClips, SlotType.BOOLEAN, SlotType.BOOLEAN);

		final PathFilter[] filter =
				new PathFilter[] { new PathFilter(new HashSet<Path>(Arrays.asList(p2)),
						new HashSet<Path>(), new PredicateBuilder(and)
								.addFunction(
										new PredicateBuilder(eqStrStr).addPath(p1, s1)
												.addPath(p2, s1).build())
								.addFunction(
										new PredicateBuilder(eqBoolBool).addBoolean(false)
												.addPath(p2, s2).build()).buildPFE()) };
		final TerminalNode terminalNode = network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();
		final ConflictSet conflictSet = network.getConflictSet();

		final Edge edgeToTerminal = terminalNode.getEdge();
		final Node betaNode = edgeToTerminal.getSourceNode();
		assertThat(betaNode, org.hamcrest.Matchers.instanceOf(BetaNode.class));
		final Edge[] incomingEdges = betaNode.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		final Node otn1 = incomingEdges[0].getSourceNode();
		final Node otn2 = incomingEdges[1].getSourceNode();
		assertThat(otn1, org.hamcrest.Matchers.instanceOf(ObjectTypeNode.class));
		assertThat(otn2, org.hamcrest.Matchers.instanceOf(ObjectTypeNode.class));
		assertEquals(1, otn1.getOutgoingExistentialEdges().size()
				+ otn2.getOutgoingExistentialEdges().size());

		// \forall t1 \exists t2 : t1.1 == t2.1 \wedge t2.2 == false
		rootNode.assertFacts(t1.newFact("a", 1L));
		rootNode.assertFacts(t1.newFact("b", 2L));
		rootNode.assertFacts(t1.newFact("c", 3L));
		rootNode.assertFacts(t1.newFact("a", 4L));
		rootNode.assertFacts(t1.newFact("b", 5L));
		rootNode.assertFacts(t1.newFact("c", 6L));

		rootNode.assertFacts(t2.newFact("a", true));
		rootNode.assertFacts(t2.newFact("a", true));
		rootNode.assertFacts(t2.newFact("b", true));
		rootNode.assertFacts(t2.newFact("b", false));

		// b 2 - b false
		// b 4 - b false

		scheduler.run();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 2,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
		conflictSet.deleteRevokedEntries();
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 2,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 0,
					assertsAndRetracts.getRetracts());
		}
	}

	@Test
	public void testTokenProcessingSimpleNegatedExistential() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.LONG, "")), t2 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.STRING, ""),
						new Slot(SlotType.BOOLEAN, ""));
		final Path p1 = new Path(t1), p2 = new Path(t2);
		final SlotAddress s1 = new SlotAddress(0), s2 = new SlotAddress(1);

		final Predicate eqStrStr =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
		final Predicate eqBoolBool =
				FunctionDictionary.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);
		final Predicate and =
				FunctionDictionary.lookupPredicate(And.inClips, SlotType.BOOLEAN, SlotType.BOOLEAN);

		final PathFilter[] filter =
				new PathFilter[] { new PathFilter(new HashSet<Path>(), new HashSet<Path>(
						Arrays.asList(p2)), new PredicateBuilder(and)
						.addFunction(
								new PredicateBuilder(eqStrStr).addPath(p1, s1).addPath(p2, s1)
										.build())
						.addFunction(
								new PredicateBuilder(eqBoolBool).addBoolean(false).addPath(p2, s2)
										.build()).buildPFE()) };
		final TerminalNode terminalNode = network.buildRule(filter);
		final RootNode rootNode = network.getRootNode();
		final ConflictSet conflictSet = network.getConflictSet();

		final Edge edgeToTerminal = terminalNode.getEdge();
		final Node betaNode = edgeToTerminal.getSourceNode();
		assertThat(betaNode, org.hamcrest.Matchers.instanceOf(BetaNode.class));
		final Edge[] incomingEdges = betaNode.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		final Node otn1 = incomingEdges[0].getSourceNode();
		final Node otn2 = incomingEdges[1].getSourceNode();
		assertThat(otn1, org.hamcrest.Matchers.instanceOf(ObjectTypeNode.class));
		assertThat(otn2, org.hamcrest.Matchers.instanceOf(ObjectTypeNode.class));
		assertEquals(1, otn1.getOutgoingExistentialEdges().size()
				+ otn2.getOutgoingExistentialEdges().size());

		// \forall t1 \not\exists t2 : t1.1 == t2.1 \wedge t2.2 == false
		rootNode.assertFacts(t1.newFact("a", 1L));
		rootNode.assertFacts(t1.newFact("b", 2L));
		rootNode.assertFacts(t1.newFact("c", 3L));
		rootNode.assertFacts(t1.newFact("a", 4L));
		rootNode.assertFacts(t1.newFact("b", 5L));
		rootNode.assertFacts(t1.newFact("c", 6L));

		rootNode.assertFacts(t2.newFact("a", true));
		rootNode.assertFacts(t2.newFact("a", true));
		rootNode.assertFacts(t2.newFact("b", true));
		rootNode.assertFacts(t2.newFact("b", false));

		// a 1
		// c 3
		// a 4
		// c 6

		// b 2 - b false
		// b 4 - b false

		scheduler.run();
		assertEquals(4, betaNode.getMemory().size());
		{
			final AssertsAndRetracts assertsAndRetracts =
					countAssertsAndRetractsInConflictSet(conflictSet);
			assertEquals("Amount of asserts does not match expected count!", 6,
					assertsAndRetracts.getAsserts());
			assertEquals("Amount of retracts does not match expected count!", 2,
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

	@Test
	public void testTokenProcessingSimpleSelfJoin() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				MemoryFactory.getMemoryFactory().newTemplate("Student",
						new Slot(SlotType.STRING, "Name"), new Slot(SlotType.LONG, "Semester"),
						new Slot(SlotType.STRING, "Studiengang"),
						new Slot(SlotType.STRING, "Hobby"));
		final Path oldStudent = new Path(student), youngStudent = new Path(student);
		final SlotAddress studentSem = new SlotAddress(1), studentSG = new SlotAddress(2);

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

		rootNode.assertFacts(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFacts(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFacts(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFacts(student.newFact("Erik", 2L, "Informatik", "Rätsel"));

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
	public void testTokenProcessingBeta() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				MemoryFactory.getMemoryFactory().newTemplate("Student",
						new Slot(SlotType.STRING, "Name"), new Slot(SlotType.LONG, "Semester"),
						new Slot(SlotType.STRING, "Studiengang"),
						new Slot(SlotType.STRING, "Hobby"));
		final Template prof =
				MemoryFactory.getMemoryFactory()
						.newTemplate("Prof", new Slot(SlotType.STRING, "Name"),
								new Slot(SlotType.STRING, "Studiengang"));
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

		rootNode.assertFacts(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFacts(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFacts(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFacts(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.assertFacts(prof.newFact("Prof. Dr. Ashcroft", "Geschichte"));
		final FactIdentifier[] timmes =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		final FactIdentifier[] santana =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Santana", "Biologie"));
		rootNode.assertFacts(prof.newFact("Prof. Dr. Kappa", "Informatik"));

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

		rootNode.retractFacts(timmes);
		rootNode.retractFacts(santana);

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
	public void testTokenProcessingBetaOneRun() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template student =
				MemoryFactory.getMemoryFactory().newTemplate("Student",
						new Slot(SlotType.STRING, "Name"), new Slot(SlotType.LONG, "Semester"),
						new Slot(SlotType.STRING, "Studiengang"),
						new Slot(SlotType.STRING, "Hobby"));
		final Template prof =
				MemoryFactory.getMemoryFactory()
						.newTemplate("Prof", new Slot(SlotType.STRING, "Name"),
								new Slot(SlotType.STRING, "Studiengang"));
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

		rootNode.assertFacts(student.newFact("Simon", 3L, "Informatik", "Schach"));
		rootNode.assertFacts(student.newFact("Rachel", 4L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Mike", 5L, "Informatik", "Coding"));
		rootNode.assertFacts(student.newFact("Samuel", 7L, "Informatik", "Schwimmen"));
		rootNode.assertFacts(student.newFact("Lydia", 4L, "Anglizistik", "Musik"));
		rootNode.assertFacts(student.newFact("Erik", 2L, "Informatik", "Rätsel"));
		rootNode.assertFacts(prof.newFact("Prof. Dr. Ashcroft", "Geschichte"));
		final FactIdentifier[] timmes =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Timmes", "Informatik"));
		final FactIdentifier[] santana =
				rootNode.assertFacts(prof.newFact("Prof. Dr. Santana", "Biologie"));
		rootNode.assertFacts(prof.newFact("Prof. Dr. Kappa", "Informatik"));

		rootNode.retractFacts(timmes);
		rootNode.retractFacts(santana);

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
	public void testTokenProcessingSimpleBeta() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""));
		final Template t2 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.DOUBLE, ""),
						new Slot(SlotType.STRING, ""));
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

		rootNode.assertFacts(t1.newFact(12L, "Micky"));
		rootNode.assertFacts(t1.newFact(-2L, "Micky"));
		rootNode.assertFacts(t2.newFact(2.1, "Micky"));
		rootNode.assertFacts(t2.newFact(-3., "Micky"));
		rootNode.assertFacts(t1.newFact(12L, "Sunny"));
		rootNode.assertFacts(t1.newFact(-2L, "Becky"));
		rootNode.assertFacts(t2.newFact(2.1, "Nelly"));
		rootNode.assertFacts(t2.newFact(-3., "Harry"));

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

	@Test
	public void testTokenProcessing() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""), new Slot(SlotType.BOOLEAN, ""));
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
		network.getRootNode().assertFacts(t1.newFact(5L, "5L&FALSE", false));
		// true != 5 < 3
		network.getRootNode().assertFacts(t1.newFact(5L, "5L&TRUE", true));
		// true == 2 < 3
		network.getRootNode().assertFacts(t1.newFact(2L, "2L&TRUE", true));
		// false != 2 < 3
		network.getRootNode().assertFacts(t1.newFact(2L, "2L&FALSE", false));
		// true == -80 < 3
		network.getRootNode().assertFacts(t1.newFact(-80L, "-80L&TRUE", true));
		// false != -80 < 3
		network.getRootNode().assertFacts(t1.newFact(-80L, "-80L&FALSE", false));
		// false != 0 < 3
		network.getRootNode().assertFacts(t1.newFact(0L, "0L&FALSE", false));

		scheduler.run();

		final AssertsAndRetracts assertsAndRetracts =
				countAssertsAndRetractsInConflictSet(network.getConflictSet());
		assertEquals("Amount of asserts does not match expected count!", 3,
				assertsAndRetracts.getAsserts());
		assertEquals("Amount of retracts does not match expected count!", 0,
				assertsAndRetracts.getRetracts());

	}

	@Test
	public void testTokenProcessingDummyFilter() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""), new Slot(SlotType.BOOLEAN, ""));
		final Path p1 = new Path(t1);

		final FilterMockup filter =
				FilterMockup.alwaysTrue(new PathAndSlotAddress(p1, new SlotAddress(2)));

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

		rootNode.assertFacts(t1.newFact(5L, "5L&FALSE", false));
		rootNode.assertFacts(t1.newFact(5L, "5L&TRUE", true));
		rootNode.assertFacts(t1.newFact(2L, "2L&TRUE", true));
		rootNode.assertFacts(t1.newFact(2L, "2L&FALSE", false));
		rootNode.assertFacts(t1.newFact(-80L, "-80L&TRUE", true));
		rootNode.assertFacts(t1.newFact(-80L, "-80L&FALSE", false));
		final FactIdentifier[] t10lfalse = rootNode.assertFacts(t1.newFact(0L, "0L&FALSE", false));
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

		rootNode.retractFacts(t10lfalse);
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

		final FactIdentifier[] t10lfalse_2 =
				rootNode.assertFacts(t1.newFact(0L, "0L&FALSE", false));
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

		rootNode.retractFacts(t10lfalse_2);
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
	public void testTokenProcessingDummyFilterOneRun() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""), new Slot(SlotType.BOOLEAN, ""));
		final Path p1 = new Path(t1);

		final FilterMockup filter =
				FilterMockup.alwaysTrue(new PathAndSlotAddress(p1, new SlotAddress(2)));

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

		rootNode.assertFacts(t1.newFact(5L, "5L&FALSE", false));
		rootNode.assertFacts(t1.newFact(5L, "5L&TRUE", true));
		rootNode.assertFacts(t1.newFact(2L, "2L&TRUE", true));
		rootNode.assertFacts(t1.newFact(2L, "2L&FALSE", false));
		rootNode.assertFacts(t1.newFact(-80L, "-80L&TRUE", true));
		rootNode.assertFacts(t1.newFact(-80L, "-80L&FALSE", false));
		final FactIdentifier[] f1 = rootNode.assertFacts(t1.newFact(0L, "0L&FALSE", false));
		rootNode.retractFacts(f1);
		final FactIdentifier[] f2 = rootNode.assertFacts(t1.newFact(0L, "0L&FALSE", false));
		rootNode.retractFacts(f2);
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
	public void testTokenProcessingOneRun() throws InterruptedException {
		final PlainScheduler scheduler = new PlainScheduler();
		final Network network =
				new Network(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
						Integer.MAX_VALUE, scheduler);
		final Template t1 =
				MemoryFactory.getMemoryFactory().newTemplate("", new Slot(SlotType.LONG, ""),
						new Slot(SlotType.STRING, ""), new Slot(SlotType.BOOLEAN, ""));
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
		rootNode.assertFacts(t1.newFact(5L, "5L&FALSE", false));
		// true != 5 < 3
		rootNode.assertFacts(t1.newFact(5L, "5L&TRUE", true));
		// true == 2 < 3
		final FactIdentifier[] f2 = rootNode.assertFacts(t1.newFact(2L, "2L&TRUE", true));
		// false != 2 < 3
		rootNode.assertFacts(t1.newFact(2L, "2L&FALSE", false));
		// true == -80 < 3
		final FactIdentifier[] f3 = rootNode.assertFacts(t1.newFact(-80L, "-80L&TRUE", true));
		// false != -80 < 3
		rootNode.assertFacts(t1.newFact(-80L, "-80L&FALSE", false));
		// false != 0 < 3
		rootNode.assertFacts(t1.newFact(0L, "0L&FALSE", false));

		// remove and re-add valid fact
		// false == 5 < 3
		final FactIdentifier[] f1 = rootNode.assertFacts(t1.newFact(5L, "5L&FALSE", false));
		// false == 5 < 3
		rootNode.retractFacts(f1);

		// remove valid fact
		// true == 2 < 3
		rootNode.retractFacts(f2);

		// remove, re-add and remove valid fact
		// true == -80 < 3
		rootNode.retractFacts(f3);
		// true == -80 < 3
		final FactIdentifier[] f4 = rootNode.assertFacts(t1.newFact(-80L, "-80L&TRUE", true));
		// true == -80 < 3
		rootNode.retractFacts(f4);

		// remove invalid fact
		// false != -80 < 3
		rootNode.retractFacts(f4);

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
