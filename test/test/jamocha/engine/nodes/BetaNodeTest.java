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
import org.jamocha.filter.PathTransformation;
import org.jamocha.filter.PathTransformation.PathInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.engine.filter.FilterMockup;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class BetaNodeTest {

	@SuppressWarnings("unused")
	private static class FactAddressMockup implements FactAddress {
	}

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
	 * {@link org.jamocha.dn.nodes.BetaNode#BetaNode(org.jamocha.dn.memory.MemoryFactory, org.jamocha.filter.Filter)}
	 * .
	 */
	@Test
	public void testBetaNode() {
		@SuppressWarnings("unused")
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
				FilterMockup.alwaysTrue());
	}

	/**
	 * 
	 */
	@Test
	public void testSelfJoin() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		Path p3 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2,
				p3);
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(
				true, p1, p2));
		Edge[] incomingEdges = beta.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(otn, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
		assertNotSame(incomingEdges[0], incomingEdges[1]);
		assertSame(beta, PathTransformation.getCurrentlyLowestNode(p1));
		assertSame(beta, PathTransformation.getCurrentlyLowestNode(p2));
		assertSame(otn, PathTransformation.getCurrentlyLowestNode(p3));
		BetaNode beta2 = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(
				true, p1, p3));
		incomingEdges = beta2.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(beta, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
		assertSame(beta2, PathTransformation.getCurrentlyLowestNode(p1));
		assertSame(beta2, PathTransformation.getCurrentlyLowestNode(p2));
		assertSame(beta2, PathTransformation.getCurrentlyLowestNode(p3));
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getChildren()}.
	 */
	@Test
	public void testGetChildren() {
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
				FilterMockup.alwaysTrue());
		final Collection<Edge> outgoingEdges = beta.getOutgoingEdges();
		assertNotNull(outgoingEdges);
		assertEquals(0, outgoingEdges.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
				FilterMockup.alwaysTrue());
		final MemoryHandlerMain memory = beta.getMemory();
		assertNotNull(memory);
		assertEquals(0, memory.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#numChildren()}.
	 */
	@Test
	public void testNumChildren() {
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
				FilterMockup.alwaysTrue());
		assertEquals(0, beta.numChildren());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)}
	 * .
	 */
	@Test
	public void testDelocalizeAddress() {
		final Path p1 = new Path(Template.STRING);
		final Path p2 = new Path(Template.STRING);
		@SuppressWarnings("unused")
		final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK,
				p1, p2);
		final FactAddress fa1 = PathTransformation
				.getFactAddressInCurrentlyLowestNode(p1);
		final FactAddress fa2 = PathTransformation
				.getFactAddressInCurrentlyLowestNode(p2);
		final BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
				new FilterMockup(true, p1, p2));
		assertEquals(
				fa1,
				beta.delocalizeAddress(
						PathTransformation
								.getFactAddressInCurrentlyLowestNode(p1))
						.getAddress());
		assertEquals(
				fa2,
				beta.delocalizeAddress(
						PathTransformation
								.getFactAddressInCurrentlyLowestNode(p2))
						.getAddress());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK,
				Template.STRING);
		Set<Path> joinedWith = new HashSet<>();
		joinedWith.add(p1);
		PathTransformation.setPathInfo(p1, new PathInfo(otn, null, joinedWith));
		joinedWith = new HashSet<>();
		joinedWith.add(p2);
		PathTransformation.setPathInfo(p2, new PathInfo(otn, null, joinedWith));
		BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(
				true, p1, p2));
		final Edge[] incomingEdges = beta.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(beta, incomingEdges[0].getTargetNode());
		assertEquals(beta, incomingEdges[1].getTargetNode());
		assertEquals(otn, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
	}

}
