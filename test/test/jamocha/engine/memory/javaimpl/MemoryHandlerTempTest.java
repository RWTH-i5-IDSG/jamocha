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

import static org.junit.Assert.*;

import org.jamocha.engine.memory.Fact;
import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.memory.javaimpl.MemoryHandlerMain;
import org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp;
import org.jamocha.engine.nodes.AddressPredecessor;
import org.jamocha.engine.nodes.Node;
import org.jamocha.filter.Filter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class MemoryHandlerTempTest {
	
	private static MemoryFactory factory;
	private static MemoryHandlerMain memoryHandlerMain;
	private static Node node;
	
	private class NodeMockup extends Node {
		
		int numChildern;

		public NodeMockup(MemoryFactory memoryFactory, Filter filter, int numChildren) {
			super(memoryFactory, filter);
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
		factory = org.jamocha.engine.memory.javaimpl.MemoryFactory.getMemoryFactory();
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
		node = new NodeMockup(memoryHandlerMain, filter, 1);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		memoryHandlerMain = null;
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#newBetaTemp(org.jamocha.engine.memory.javaimpl.MemoryHandlerMain, org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp, org.jamocha.engine.nodes.Node.Edge, org.jamocha.filter.Filter)}.
	 */
	@Test
	public void testNewBetaTemp() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#newAlphaTemp(org.jamocha.engine.memory.javaimpl.MemoryHandlerMain, org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp, org.jamocha.engine.nodes.Node, org.jamocha.filter.Filter)}.
	 */
	@Test
	public void testNewAlphaTemp() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#newRootTemp(org.jamocha.engine.memory.javaimpl.MemoryHandlerMain, org.jamocha.engine.memory.Fact, org.jamocha.engine.nodes.Node)}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testNewToken() throws InterruptedException {
		MemoryHandlerTemp memoryHandlerTemp = factory.newToken(memoryHandlerMain, node, new Fact(new Template(SlotType.STRING), "Test"));
		// TODO assert something
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#size()}.
	 */
	@Test
	public void testSize() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#releaseLock()}.
	 */
	@Test
	public void testReleaseLock() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#getTemplate()}.
	 */
	@Test
	public void testGetTemplate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.jamocha.engine.memory.javaimpl.MemoryHandlerTemp#getValue(org.jamocha.engine.memory.FactAddress, org.jamocha.engine.memory.SlotAddress, int)}.
	 */
	@Test
	public void testGetValue() {
		fail("Not yet implemented"); // TODO
	}

}
