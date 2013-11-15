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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.dn.ConflictSet.NodeAndToken;
import org.jamocha.dn.Network;
import org.jamocha.dn.PlainScheduler;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetractVisitor;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Path;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.filter.FilterMockup;
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
		TODODatenkrakeFunktionen.load();
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

	public static PredicateWithArguments combine() {
		return null;
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
				TODODatenkrakeFunktionen.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqBoolBool =
				TODODatenkrakeFunktionen.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);

		final Filter filter =
				new Filter(new PredicateBuilder(eqBoolBool)
						.addPath(p1, slotBool)
						.addFunction(
								new FunctionBuilder(lessLongLong).addPath(p1, slotLong)
										.addConstant(3L, SlotType.LONG).build()).build());

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

		final List<Assert> asserts = new ArrayList<>();
		final List<Retract> retracts = new ArrayList<>();

		for (final NodeAndToken nat : network.getConflictSet()) {
			final AssertOrRetract<?> assertOrRetract = nat.getToken();
			final TerminalNode terminalNode = nat.getTerminal();
			assertOrRetract.accept(terminalNode, new AssertOrRetractVisitor() {

				@Override
				public void visit(TerminalNode node, Retract mem) {
					retracts.add(mem);
				}

				@Override
				public void visit(TerminalNode node, Assert mem) {
					asserts.add(mem);
				}
			});
		}
		assertEquals("Amount of asserts does not match expected count!", 3, asserts.size());
		assertEquals("Amount of retracts does not match expected count!", 0, retracts.size());

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

		final Filter filter = FilterMockup.alwaysTrue(p1);

		final RootNode rootNode = network.getRootNode();
		// create OTN
		final ObjectTypeNode otn = new ObjectTypeNode(network, p1);
		// append to root node
		rootNode.putOTN(otn);
		// create & append alpha
		final AlphaNode alphaNode = new AlphaNode(network, filter);
		// create & append terminal
		final TerminalNode terminalNode = new TerminalNode(network, alphaNode);

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

		assertEquals("Amount of facts in terminal does not match expected count!", 7, terminalNode
				.getMemory().size());

		final List<Assert> asserts = new ArrayList<>();
		final List<Retract> retracts = new ArrayList<>();

		for (final NodeAndToken nat : network.getConflictSet()) {
			final AssertOrRetract<?> assertOrRetract = nat.getToken();
			final TerminalNode terminal = nat.getTerminal();
			assertOrRetract.accept(terminal, new AssertOrRetractVisitor() {
				@Override
				public void visit(TerminalNode node, Retract mem) {
					retracts.add(mem);
				}

				@Override
				public void visit(TerminalNode node, Assert mem) {
					asserts.add(mem);
				}
			});
		}
		assertEquals("Amount of asserts does not match expected count!", 7, asserts.size());
		assertEquals("Amount of retracts does not match expected count!", 0, retracts.size());

	}
}
