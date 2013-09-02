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
import static org.junit.Assert.fail;

import org.jamocha.engine.memory.Fact;
import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.memory.javaimpl.MemoryHandlerMain;
import org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp;
import org.jamocha.engine.memory.javaimpl.SlotAddress;
import org.jamocha.engine.nodes.AddressPredecessor;
import org.jamocha.engine.nodes.Node;
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
	private static MemoryHandlerMain memoryHandlerMain;
	private static Node node;
	private static org.jamocha.engine.memory.javaimpl.FactAddress factAddress;
	private static SlotAddress slotAddress;

	private class NodeMockup extends Node {

		int numChildern;

		public NodeMockup(int numChildren) {
			super();
			this.numChildern = numChildren;
		}

		@Override
		public int numChildren() {
			return numChildern;
		};

		@Override
		protected EdgeImpl newEdge(Node source) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AddressPredecessor delocalizeAddress(
				FactAddress localNetworkFactAddress) {
			// TODO Auto-generated method stub
			return null;
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
		memoryHandlerMain = new MemoryHandlerMain();
		node = new NodeMockup(1);
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
	 */
	@Test
	public void testNewBetaTemp() {
		fail("Not yet implemented"); // TODO
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
		memoryTempHandler = factory.processTokenInAlpha(
				memoryHandlerMain, token, node, FilterMockup.alwaysFalse());
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
		memoryHandlerMain = new MemoryHandlerMain();
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
