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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hamcrest.Matchers;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.filter.FilterMockup;

/**
 * Test class for {@link AlphaNode}.
 * 
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
	 * {@link org.jamocha.dn.nodes.AlphaNode#AlphaNode(Network, org.jamocha.filter.Filter)} .
	 */
	@Test
	public void testAlphaNode() {
		new AlphaNode(Network.DEFAULTNETWORK, FilterMockup.alwaysFalse());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getOutgoingPositiveEdges()}.
	 */
	@Test
	public void testGetOutgoingEdges() {
		Path p1 = new Path(Template.BOOLEAN);
		Path p2 = new Path(Template.BOOLEAN);
		@SuppressWarnings("unused")
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2);
		AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		alpha.shareNode(p2);
		Collection<? extends Edge> children = alpha.getOutgoingPositiveEdges();
		assertNotNull(children);
		assertEquals(0, children.size());
		AlphaNode alphaB1 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		children = alpha.getOutgoingPositiveEdges();
		assertNotNull(children);
		assertEquals(1, children.size());
		assertTrue(children.contains(alphaB1.getIncomingEdges()[0]));
		AlphaNode alphaB2 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p2));
		children = alpha.getOutgoingPositiveEdges();
		assertNotNull(children);
		assertEquals(2, children.size());
		assertTrue(children.contains(alphaB1.getIncomingEdges()[0]));
		assertTrue(children.contains(alphaB2.getIncomingEdges()[0]));
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		Path p1 = new Path(Template.BOOLEAN);
		@SuppressWarnings("unused")
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		final MemoryHandlerMain memory = alpha.getMemory();
		assertNotNull(memory);
		assertEquals(0, memory.size());
		assertEquals(1, memory.getTemplate().length);
		assertEquals(Template.BOOLEAN, memory.getTemplate()[0]);
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getNumberOfOutgoingEdges()}.
	 */
	@Test
	public void testNumberOfOutgoingEdges() {
		Path p1 = new Path(Template.BOOLEAN);
		Path p2 = new Path(Template.BOOLEAN);
		@SuppressWarnings("unused")
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2);
		AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		assertEquals(0, alpha.getNumberOfOutgoingEdges());
		alpha.shareNode(p2);
		assertEquals(0, alpha.getNumberOfOutgoingEdges());
		@SuppressWarnings("unused")
		AlphaNode alphaB1 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		assertEquals(1, alpha.getNumberOfOutgoingEdges());
		@SuppressWarnings("unused")
		AlphaNode alphaB2 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p2));
		assertEquals(2, alpha.getNumberOfOutgoingEdges());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)} .
	 */
	@Test
	public void testDelocalizeAddress() {
		Path p1 = new Path(Template.BOOLEAN);
		Path p2 = new Path(Template.BOOLEAN);
		@SuppressWarnings("unused")
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2);
		FactAddress fa1 = p1.getFactAddressInCurrentlyLowestNode();
		AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		assertNotSame(fa1, p1.getFactAddressInCurrentlyLowestNode());
		alpha.delocalizeAddress(p1.getFactAddressInCurrentlyLowestNode());
		alpha.shareNode(p2);
		assertNotSame(fa1, p2.getFactAddressInCurrentlyLowestNode());
		assertSame(p1.getFactAddressInCurrentlyLowestNode(),
				p2.getFactAddressInCurrentlyLowestNode());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetIncomingEdges() {
		Path p1 = new Path(Template.BOOLEAN);
		Path p2 = new Path(Template.BOOLEAN);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2);
		AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		Edge[] incomingEdges = alpha.getIncomingEdges();
		assertEquals(1, incomingEdges.length);
		assertEquals(otn.getOutgoingPositiveEdges().iterator().next(), incomingEdges[0]);
		alpha.shareNode(p2);
		incomingEdges = alpha.getIncomingEdges();
		assertEquals(1, incomingEdges.length);
		assertEquals(otn.getOutgoingPositiveEdges().iterator().next(), incomingEdges[0]);
		AlphaNode alphaB1 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1));
		incomingEdges = alpha.getIncomingEdges();
		assertEquals(1, incomingEdges.length);
		assertEquals(otn.getOutgoingPositiveEdges().iterator().next(), incomingEdges[0]);
		incomingEdges = alphaB1.getIncomingEdges();
		assertEquals(1, incomingEdges.length);
		assertEquals(alpha.getOutgoingPositiveEdges().iterator().next(), incomingEdges[0]);
		@SuppressWarnings("unused")
		AlphaNode alphaB2 = new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p2));
		incomingEdges = alphaB1.getIncomingEdges();
		assertEquals(1, incomingEdges.length);
		assertEquals(alpha.getOutgoingPositiveEdges().iterator().next(), incomingEdges[0]);
		incomingEdges = alphaB1.getIncomingEdges();
		assertEquals(1, incomingEdges.length);
		assertThat((Collection<Edge>) (Collection<?>) alpha.getOutgoingPositiveEdges(),
				Matchers.hasItem(incomingEdges[0]));
	}

}
