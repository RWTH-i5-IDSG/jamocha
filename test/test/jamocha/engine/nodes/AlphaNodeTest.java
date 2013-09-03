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
package test.jamocha.engine.nodes;

import static org.junit.Assert.*;

import java.util.Set;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
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
public class AlphaNodeTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.AlphaNode#AlphaNode(org.jamocha.dn.memory.MemoryFactory, org.jamocha.filter.Filter)}
	 * .
	 */
	@Test
	public void testAlphaNode() {
		new AlphaNode(Network.DEFAULTNETWORK, FilterMockup.alwaysFalse());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getChildren()}.
	 */
	@Test
	public void testGetChildren() {
		Path p1 = new Path(Template.BOOLEAN);
		Path p2 = new Path(Template.BOOLEAN);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Template.BOOLEAN, p1, p2);
		AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p2));
		Set<Edge> children = alpha.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());
		AlphaNode alphaB1 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		children = alpha.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		assertTrue(children.contains(alphaB1));
		AlphaNode alphaB2 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p2));
		children = alpha.getChildren();
		assertNotNull(children);
		assertEquals(2, children.size());
		assertTrue(children.contains(alphaB1));
		assertTrue(children.contains(alphaB2));
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		fail("Not yet implemented"); // TODO Test
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#numChildren()}.
	 */
	@Test
	public void testNumChildren() {
		fail("Not yet implemented"); // TODO Test
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)}
	 * .
	 */
	@Test
	public void testDelocalizeAddress() {
		fail("Not yet implemented"); // TODO Test
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {
		fail("Not yet implemented"); // TODO Test
	}

}
