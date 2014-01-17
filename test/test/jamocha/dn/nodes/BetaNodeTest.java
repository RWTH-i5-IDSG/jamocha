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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.filter.FilterMockup;

/**
 * Test class for {@link BetaNode}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class BetaNodeTest {

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
	 * {@link org.jamocha.dn.nodes.BetaNode#BetaNode(Network, org.jamocha.filter.Filter)} .
	 */
	@Test
	public void testBetaNode() {
		@SuppressWarnings("unused")
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, FilterMockup.alwaysTrue());
	}

	/**
	 * 
	 */
	@Test
	public void testSelfJoin() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		Path p3 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2, p3);
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p2));
		Edge[] incomingEdges = beta.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(otn, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
		assertNotSame(incomingEdges[0], incomingEdges[1]);
		assertSame(beta, p1.getCurrentlyLowestNode());
		assertSame(beta, p2.getCurrentlyLowestNode());
		assertSame(otn, p3.getCurrentlyLowestNode());
		BetaNode beta2 = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p3));
		incomingEdges = beta2.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(beta, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
		assertSame(beta2, p1.getCurrentlyLowestNode());
		assertSame(beta2, p2.getCurrentlyLowestNode());
		assertSame(beta2, p3.getCurrentlyLowestNode());
	}

	/**
	 * 
	 */
	@Test
	public void testNodeSharing() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		Path p3 = new Path(Template.STRING);
		Path p4 = new Path(Template.STRING);
		Path p5 = new Path(Template.STRING);
		Path p6 = new Path(Template.STRING);
		Path p7 = new Path(Template.STRING);
		Path p8 = new Path(Template.STRING);
		ObjectTypeNode otn =
				new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2, p3, p4, p5, p6, p7, p8);
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p2));
		assertSame(otn, p3.getCurrentlyLowestNode());
		assertSame(otn, p4.getCurrentlyLowestNode());
		beta.shareNode(p3, p4);
		assertSame(beta, p1.getCurrentlyLowestNode());
		assertSame(beta, p2.getCurrentlyLowestNode());
		assertSame(beta, p3.getCurrentlyLowestNode());
		assertSame(beta, p4.getCurrentlyLowestNode());
		assertSame(p1.getFactAddressInCurrentlyLowestNode(),
				p3.getFactAddressInCurrentlyLowestNode());
		assertSame(p2.getFactAddressInCurrentlyLowestNode(),
				p4.getFactAddressInCurrentlyLowestNode());
		BetaNode betaB = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p3));
		assertSame(betaB, p1.getCurrentlyLowestNode());
		assertSame(betaB, p2.getCurrentlyLowestNode());
		assertSame(betaB, p3.getCurrentlyLowestNode());
		assertSame(betaB, p4.getCurrentlyLowestNode());
		assertNotSame(p1.getFactAddressInCurrentlyLowestNode(),
				p3.getFactAddressInCurrentlyLowestNode());
		assertNotSame(p2.getFactAddressInCurrentlyLowestNode(),
				p4.getFactAddressInCurrentlyLowestNode());
		beta.shareNode(p5, p6);
		beta.shareNode(p7, p8);
		betaB.shareNode(p5, p7);
		assertSame(betaB, p5.getCurrentlyLowestNode());
		assertSame(betaB, p6.getCurrentlyLowestNode());
		assertSame(betaB, p7.getCurrentlyLowestNode());
		assertSame(betaB, p8.getCurrentlyLowestNode());
		assertNotSame(p5.getFactAddressInCurrentlyLowestNode(),
				p7.getFactAddressInCurrentlyLowestNode());
		assertNotSame(p6.getFactAddressInCurrentlyLowestNode(),
				p8.getFactAddressInCurrentlyLowestNode());
		assertSame(p1.getFactAddressInCurrentlyLowestNode(),
				p5.getFactAddressInCurrentlyLowestNode());
		assertSame(p2.getFactAddressInCurrentlyLowestNode(),
				p6.getFactAddressInCurrentlyLowestNode());
		assertSame(p3.getFactAddressInCurrentlyLowestNode(),
				p7.getFactAddressInCurrentlyLowestNode());
		assertSame(p4.getFactAddressInCurrentlyLowestNode(),
				p8.getFactAddressInCurrentlyLowestNode());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getOutgoingEdges()}.
	 */
	@Test
	public void testGetOutgoingEdges() {
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, FilterMockup.alwaysTrue());
		final Collection<? extends Edge> outgoingEdges = beta.getOutgoingPositiveEdges();
		assertNotNull(outgoingEdges);
		assertEquals(0, outgoingEdges.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, FilterMockup.alwaysTrue());
		final MemoryHandlerMain memory = beta.getMemory();
		assertNotNull(memory);
		assertEquals(0, memory.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getNumberOfOutgoingEdges()}.
	 */
	@Test
	public void testNumChildren() {
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, FilterMockup.alwaysTrue());
		assertEquals(0, beta.getNumberOfOutgoingEdges());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)} .
	 */
	@Test
	public void testDelocalizeAddress() {
		final Path p1 = new Path(Template.STRING);
		final Path p2 = new Path(Template.STRING);
		@SuppressWarnings("unused")
		final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2);
		final FactAddress fa1 = p1.getFactAddressInCurrentlyLowestNode();
		final FactAddress fa2 = p2.getFactAddressInCurrentlyLowestNode();
		final BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p2));
		assertEquals(fa1, beta.delocalizeAddress(p1.getFactAddressInCurrentlyLowestNode())
				.getAddress());
		assertEquals(fa2, beta.delocalizeAddress(p2.getFactAddressInCurrentlyLowestNode())
				.getAddress());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {
		Path p = new Path(Template.STRING);
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p);
		Set<Path> joinedWith = new HashSet<>();
		joinedWith.add(p1);
		p1.setCurrentlyLowestNode(otn);
		p1.setFactAddressInCurrentlyLowestNode(null);
		p1.setJoinedWith(joinedWith);
		joinedWith = new HashSet<>();
		joinedWith.add(p2);
		p2.setCurrentlyLowestNode(otn);
		p2.setFactAddressInCurrentlyLowestNode(null);
		p2.setJoinedWith(joinedWith);
		// next line will cause assertion failure when trying to localize
		// FactAddressInCurrentlyLowestNode downwards for p1 and p2
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, p1, p2));
		final Edge[] incomingEdges = beta.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(beta, incomingEdges[0].getTargetNode());
		assertEquals(beta, incomingEdges[1].getTargetNode());
		assertEquals(otn, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
	}

}
