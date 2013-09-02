/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package test.jamocha.engine.memory.javaimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedList;

import org.jamocha.engine.memory.Fact;
import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.MemoryHandler;
import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.memory.javaimpl.MemoryHandlerMain;
import org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp;
import org.jamocha.engine.memory.javaimpl.SlotAddress;
import org.jamocha.engine.nodes.AddressPredecessor;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.Node.Edge;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.engine.filter.FilterMockup;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class MemoryHandlerTempTest {

	private static MemoryFactory factory;
	private static MemoryHandlerMain memoryHandlerMain, memoryHandlerMainLeft,
			memoryHandlerMainRight;
	private static NodeMockup node, nodeLeft, nodeRight;
	private static org.jamocha.engine.memory.javaimpl.FactAddress factAddress;
	private static SlotAddress slotAddress;
	private static Edge originInput;

	static final int faSize = 10;
	static final FactAddress[] fa = new FactAddress[faSize];

	static {
		for (int i = 0; i < 10; i++) {
			fa[i] = new org.jamocha.engine.memory.javaimpl.FactAddress(i);
		}
	}

	private static class NodeMockup extends Node {

		private class EdgeMockup extends EdgeImpl {

			final int offset;

			public EdgeMockup(Node sourceNode, Node targetNode, int offset) {
				super(sourceNode, targetNode);
				this.offset = offset;
			}

			@Override
			public void processPlusToken(MemoryHandler memory) {
			}

			@Override
			public void processMinusToken(MemoryHandler memory) {
			}

			@Override
			public FactAddress localizeAddress(FactAddress addressInParent) {
				return fa[((org.jamocha.engine.memory.javaimpl.FactAddress) addressInParent)
						.getIndex() + offset];
			}

			@Override
			public LinkedList<org.jamocha.engine.memory.MemoryHandlerTemp> getTempMemories() {
				return new LinkedList<>();
			}

		}

		int numChildern;
		int currentOffset = 0;

		public NodeMockup(int numChildren,
				org.jamocha.engine.memory.MemoryHandlerMain memoryHandlerMain) {
			super(memoryHandlerMain);
			this.numChildern = numChildren;
			this.inputs = new Edge[0];
		}

		public NodeMockup(int numChildren) {
			super(null);
			this.numChildern = numChildren;
			this.inputs = new Edge[0];
		}

		@Override
		public int numChildren() {
			return numChildern;
		};

		@Override
		protected EdgeImpl newEdge(Node source) {
			EdgeImpl edge = new EdgeMockup(source, this, currentOffset);
			currentOffset += source.getMemory().getTemplate().length;
			return edge;
		}

		@Override
		public Edge connectParent(final Node parent) {
			Edge edge = super.connectParent(parent);
			inputs = Arrays.copyOf(inputs, inputs.length + 1);
			inputs[inputs.length - 1] = edge;
			return edge;
		}

		@Override
		public AddressPredecessor delocalizeAddress(
				FactAddress localNetworkFactAddress) {
			org.jamocha.engine.memory.javaimpl.FactAddress factAddress = (org.jamocha.engine.memory.javaimpl.FactAddress) localNetworkFactAddress;
			int pos = 0;
			Edge originEdge = null;
			for (Edge edge : inputs) {
				if (pos > factAddress.getIndex())
					break;
				if (pos + edge.getSourceNode().getMemory().getTemplate().length > factAddress
						.getIndex()) {
					originEdge = edge;
					break;
				}
			}
			return new AddressPredecessor(originEdge, fa[factAddress.getIndex()
					- pos]);
		}

	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = org.jamocha.engine.memory.javaimpl.MemoryFactory
				.getMemoryFactory();
		factAddress = new org.jamocha.engine.memory.javaimpl.FactAddress(0);
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
		memoryHandlerMainRight = new MemoryHandlerMain(new Template(
				SlotType.STRING));
		memoryHandlerMainLeft = new MemoryHandlerMain(new Template(
				SlotType.STRING));
		memoryHandlerMain = new MemoryHandlerMain(
				new Template(SlotType.STRING), new Template(SlotType.STRING));
		node = new NodeMockup(1, memoryHandlerMain);
		nodeLeft = new NodeMockup(1, memoryHandlerMainLeft);
		nodeRight = new NodeMockup(1, memoryHandlerMainRight);
		originInput = node.connectParent(nodeLeft);
		node.connectParent(nodeRight);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		memoryHandlerMain = null;
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#newBetaTemp(org.jamocha.engine.memory.javaimpl.MemoryHandlerMain, org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp, org.jamocha.engine.nodes.Node.Edge, org.jamocha.filter.Filter)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testNewBetaTemp() throws InterruptedException {
		MemoryHandlerTemp token = factory.newToken(memoryHandlerMainRight,
				nodeLeft, new Fact(new Template(SlotType.STRING), "Fakt1"),
				new Fact(new Template(SlotType.STRING), "Fakt2"));
		token.releaseLock();
		token = factory.newToken(memoryHandlerMainLeft, nodeRight, new Fact(
				new Template(SlotType.STRING), "Fakt3"), new Fact(new Template(
				SlotType.STRING), "Fakt4"));
		MemoryHandlerTemp token1 = factory.processTokenInBeta(
				memoryHandlerMain, token, originInput,
				FilterMockup.alwaysTrue());
		assertEquals(4, token1.size());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#newAlphaTemp(org.jamocha.engine.memory.javaimpl.MemoryHandlerMain, org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp, org.jamocha.engine.nodes.Node, org.jamocha.filter.Filter)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testNewAlphaTemp() throws InterruptedException {
		MemoryHandlerTemp token = factory.newToken(memoryHandlerMain, node,
				new Fact(new Template(SlotType.STRING), "Test"));
		MemoryHandlerTemp memoryTempHandler = factory.processTokenInAlpha(
				memoryHandlerMain, token, node, FilterMockup.alwaysTrue());
		assertEquals(1, memoryTempHandler.size());
		memoryTempHandler = factory.processTokenInAlpha(memoryHandlerMain,
				token, node, FilterMockup.alwaysFalse());
		assertEquals(0, memoryTempHandler.size());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#newRootTemp(org.jamocha.engine.memory.javaimpl.MemoryHandlerMain, org.jamocha.engine.memory.Fact, org.jamocha.engine.nodes.Node)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testNewToken() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#size()}.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testSize() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		assertEquals(1, memoryHandlerTemp.size());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#releaseLock()}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testReleaseLock() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertEquals(0, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		memoryHandlerTemp.releaseLock();
		assertEquals(1, memoryHandlerMain.size());
		Node node5 = new NodeMockup(5);
		memoryHandlerTemp = factory.newToken(memoryHandlerMain, node5,
				new Fact(new Template(SlotType.STRING), "Test"));
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
		memoryHandlerMain = new MemoryHandlerMain(new Template[0]);
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.engine.memory.FactAddress, org.jamocha.engine.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testGetValue() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		assertEquals("Test",
				memoryHandlerTemp.getValue(factAddress, slotAddress, 0));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.engine.memory.FactAddress, org.jamocha.engine.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueRowOutOfBounds() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		memoryHandlerTemp.getValue(factAddress, slotAddress, 1);
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.engine.memory.FactAddress, org.jamocha.engine.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueSlotOutOfBounds() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		FactAddress factAddress1 = new org.jamocha.engine.memory.javaimpl.FactAddress(
				1);
		memoryHandlerTemp.getValue(factAddress1, slotAddress, 0);
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.engine.memory.FactAddress, org.jamocha.engine.memory.SlotAddress, int)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueFactOutOfBounds() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(
				memoryHandlerMain, node, new Fact(
						new Template(SlotType.STRING), "Test"));
		assertNotNull(memoryHandlerTemp);
		SlotAddress slotAddress1 = new SlotAddress(1);
		memoryHandlerTemp.getValue(factAddress, slotAddress1, 0);
	}

}
