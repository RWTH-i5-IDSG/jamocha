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
package test.jamocha.dn.memory.javaimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Map;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerMain;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.AddressPredecessor;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.FunctionWithArgumentsComposite;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathLeaf;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.filter.FilterMockup;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MemoryHandlerTempTest {

	private static MemoryHandlerMain memoryHandlerMain;
	private static NodeMockup node, nodeLeft, nodeRight;
	private static org.jamocha.dn.memory.javaimpl.FactAddress factAddress;
	private static SlotAddress slotAddress;
	private static Edge originInput;

	static final int faSize = 10;
	static final FactAddress[] fa = new FactAddress[faSize];

	static {
		for (int i = 0; i < 10; i++) {
			fa[i] = new org.jamocha.dn.memory.javaimpl.FactAddress(i);
		}
	}

	private static class NodeMockup extends Node {

		private class EdgeMockup extends EdgeImpl {

			final int offset;

			public EdgeMockup(Network network, Node sourceNode, Node targetNode, int offset) {
				super(network, sourceNode, targetNode);
				this.offset = offset;
			}

			@Override
			public void processPlusToken(org.jamocha.dn.memory.MemoryHandlerTemp memory) {
			}

			@Override
			public void processMinusToken(org.jamocha.dn.memory.MemoryHandlerTemp memory) {
			}

			@Override
			public FactAddress localizeAddress(FactAddress addressInParent) {
				return fa[((org.jamocha.dn.memory.javaimpl.FactAddress) addressInParent).getIndex()
						+ offset];
			}

			@Override
			public LinkedList<org.jamocha.dn.memory.MemoryHandlerTemp> getTempMemories() {
				return new LinkedList<>();
			}

			@Override
			public void setAddressMap(Map<? extends FactAddress, ? extends FactAddress> map) {
			}

		}

		int numChildern;
		int currentOffset = 0;

		@SuppressWarnings("deprecation")
		public NodeMockup(Network network, int numChildren, Node... parents) {
			super(network, parents);
			this.numChildern = numChildren;
		}

		@SuppressWarnings("deprecation")
		public NodeMockup(Network network, int numChildren) {
			super(network);
			this.numChildern = numChildren;
		}

		public NodeMockup(Network network, int numChildren, Template template) {
			super(network, template);
			this.numChildern = numChildren;
		}

		@Override
		public int getNumberOfOutgoingEdges() {
			return numChildern;
		};

		@Override
		protected EdgeImpl newEdge(Node source) {
			EdgeImpl edge = new EdgeMockup(Network.DEFAULTNETWORK, source, this, currentOffset);
			currentOffset += source.getMemory().getTemplate().length;
			return edge;
		}

		@Override
		public AddressPredecessor delocalizeAddress(FactAddress localNetworkFactAddress) {
			org.jamocha.dn.memory.javaimpl.FactAddress factAddress =
					(org.jamocha.dn.memory.javaimpl.FactAddress) localNetworkFactAddress;
			int pos = 0;
			Edge originEdge = null;
			for (Edge edge : incomingEdges) {
				if (pos > factAddress.getIndex())
					break;
				if (pos + edge.getSourceNode().getMemory().getTemplate().length > factAddress
						.getIndex()) {
					originEdge = edge;
					break;
				}
				pos += edge.getSourceNode().getMemory().getTemplate().length;
			}
			assert originEdge != null;
			return new AddressPredecessor(originEdge, fa[factAddress.getIndex() - pos]);
		}

		@Override
		public void shareNode(Path... paths) {
		}

	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factAddress = new org.jamocha.dn.memory.javaimpl.FactAddress(0);
		slotAddress = new SlotAddress(0);
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
		nodeLeft = new NodeMockup(Network.DEFAULTNETWORK, 1, new Template(SlotType.STRING));
		nodeRight = new NodeMockup(Network.DEFAULTNETWORK, 1, new Template(SlotType.STRING));
		node = new NodeMockup(Network.DEFAULTNETWORK, 1, nodeLeft, nodeRight);
		originInput = node.getIncomingEdges()[0];
		memoryHandlerMain =
				(MemoryHandlerMain) Network.DEFAULTNETWORK.getMemoryFactory().newMemoryHandlerMain(
						new Template(SlotType.STRING));
		assert node.getIncomingEdges().length == 2;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#newBetaTemp(org.jamocha.dn.memory.javaimpl.MemoryHandlerMain, org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp, org.jamocha.dn.nodes.Node.Edge, org.jamocha.filter.Filter)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testNewBetaTempFullJoin() throws CouldNotAcquireLockException {
		MemoryHandlerTemp token =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						nodeRight.getMemory(), nodeRight,
						new Fact(new Template(SlotType.STRING), "Fakt3"),
						new Fact(new Template(SlotType.STRING), "Fakt4"));
		token.releaseLock();
		token =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						nodeLeft.getMemory(), nodeLeft,
						new Fact(new Template(SlotType.STRING), "Fakt1"),
						new Fact(new Template(SlotType.STRING), "Fakt2"));
		MemoryHandlerTemp token1 =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().processTokenInBeta(
						node.getMemory(), token, originInput, FilterMockup.alwaysTrue());
		assertEquals(4, token1.size());
		assertEquals(2, token1.getTemplate().length);
		String s = (String) token1.getValue(fa[0], slotAddress, 0);
		assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
		s = (String) token1.getValue(fa[0], slotAddress, 1);
		assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
		s = (String) token1.getValue(fa[0], slotAddress, 2);
		assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
		s = (String) token1.getValue(fa[0], slotAddress, 3);
		assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
		s = (String) token1.getValue(fa[1], slotAddress, 0);
		assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
		s = (String) token1.getValue(fa[1], slotAddress, 1);
		assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
		s = (String) token1.getValue(fa[1], slotAddress, 2);
		assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
		s = (String) token1.getValue(fa[1], slotAddress, 3);
		assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#newBetaTemp(org.jamocha.dn.memory.javaimpl.MemoryHandlerMain, org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp, org.jamocha.dn.nodes.Node.Edge, org.jamocha.filter.Filter)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNewBetaTempSelectiveJoin() throws CouldNotAcquireLockException {
		TODODatenkrakeFunktionen.load();
		FunctionWithArguments pl1 = new PathLeaf.ParameterLeaf(SlotType.STRING);
		FunctionWithArguments pl2 = new PathLeaf.ParameterLeaf(SlotType.STRING);
		FunctionWithArguments faw =
				new FunctionWithArgumentsComposite(TODODatenkrakeFunktionen.lookup("=",
						SlotType.STRING, SlotType.STRING), pl1, pl2);
		FilterElement fe =
				new FilterElement(faw, new SlotInFactAddress(
						new org.jamocha.dn.memory.javaimpl.FactAddress(0), new SlotAddress(0)),
						new SlotInFactAddress(new org.jamocha.dn.memory.javaimpl.FactAddress(1),
								new SlotAddress(0)));
		Filter filter = new Filter(new FilterElement[] { fe });
		MemoryHandlerTemp token =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						nodeRight.getMemory(), nodeRight,
						new Fact(new Template(SlotType.STRING), "Fakt1"),
						new Fact(new Template(SlotType.STRING), "Fakt3"));
		token.releaseLock();
		token =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						nodeLeft.getMemory(), nodeLeft,
						new Fact(new Template(SlotType.STRING), "Fakt1"),
						new Fact(new Template(SlotType.STRING), "Fakt2"));
		MemoryHandlerTemp token1 =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().processTokenInBeta(
						node.getMemory(), token, originInput, filter);
		assertEquals(1, token1.size());
		assertEquals(2, token1.getTemplate().length);
		assertEquals("Fakt1", (String) token1.getValue(fa[0], slotAddress, 0));
		assertEquals("Fakt1", (String) token1.getValue(fa[1], slotAddress, 0));
	}

	/**
	 * Test method for
	 * {@link MemoryFactory#processTokenInAlpha(org.jamocha.dn.memory.MemoryHandlerMain, org.jamocha.dn.memory.MemoryHandlerTemp, Edge, Filter)}
	 * .
	 * 
	 * @throws CouldNotAcquireLockException
	 */
	@Test
	public void testNewAlphaTemp() throws CouldNotAcquireLockException {
		MemoryHandlerTemp token =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		MemoryHandlerTemp memoryTempHandler =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().processTokenInAlpha(
						memoryHandlerMain, token, node.getIncomingEdges()[0],
						FilterMockup.alwaysTrue());
		assertEquals(1, memoryTempHandler.size());
		memoryTempHandler =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().processTokenInAlpha(
						memoryHandlerMain, token, node.getIncomingEdges()[0],
						FilterMockup.alwaysFalse());
		assertEquals(0, memoryTempHandler.size());
	}

	/**
	 * Test method for
	 * {@link MemoryFactory#newToken(org.jamocha.dn.memory.MemoryHandlerMain, Node, Fact...)}
	 * .
	 */
	@Test
	public void testNewToken() {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
	}

	/**
	 * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#size()}.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testSize() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		assertEquals(1, memoryHandlerTemp.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#releaseLock()} .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testReleaseLock() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertEquals(0, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		Node node5 = new NodeMockup(Network.DEFAULTNETWORK, 5);
		memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node5, new Fact(new Template(SlotType.STRING), "Test"));
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(2, memoryHandlerMain.size());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.dn.memory.FactAddress, org.jamocha.dn.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testGetValue() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		assertEquals("Test", memoryHandlerTemp.getValue(factAddress, slotAddress, 0));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.dn.memory.FactAddress, org.jamocha.dn.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueRowOutOfBounds() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		memoryHandlerTemp.getValue(factAddress, slotAddress, 1);
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.dn.memory.FactAddress, org.jamocha.dn.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueSlotOutOfBounds() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		FactAddress factAddress1 = new org.jamocha.dn.memory.javaimpl.FactAddress(1);
		memoryHandlerTemp.getValue(factAddress1, slotAddress, 0);
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.dn.memory.FactAddress, org.jamocha.dn.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueFactOutOfBounds() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp =
				(MemoryHandlerTemp) Network.DEFAULTNETWORK.getMemoryFactory().newToken(
						memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		SlotAddress slotAddress1 = new SlotAddress(1);
		memoryHandlerTemp.getValue(factAddress, slotAddress1, 0);
	}

}
