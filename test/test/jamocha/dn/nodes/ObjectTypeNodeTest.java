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
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link ObjectTypeNode}.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class ObjectTypeNodeTest {

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
	 * Test method for {@link org.jamocha.dn.nodes.ObjectTypeNode#ObjectTypeNode(Network, Path...)}
	 * .
	 */
	@Test
	public void testObjectTypeNode() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1, p2);
		assertEquals(otn, p1.getCurrentlyLowestNode());
		Set<Path> joinedWith = p1.getJoinedWith();
		assertEquals(1, joinedWith.size());
		assertTrue(joinedWith.contains(p1));
		joinedWith = p2.getJoinedWith();
		assertEquals(1, joinedWith.size());
		assertTrue(joinedWith.contains(p2));
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.ObjectTypeNode#getTemplate()} .
	 */
	@Test
	public void testGetTemplate() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		assertEquals(Template.STRING, otn.getTemplate());
		p1 = new Path(Template.DOUBLE);
		otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		assertEquals(Template.DOUBLE, otn.getTemplate());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.ObjectTypeNode#assertFact(org.jamocha.dn.memory.Fact)} .
	 */
	@Test
	public void testAssertFact() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		otn.assertFact(new Fact(Template.STRING, "TestValue 1"));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.ObjectTypeNode#retractFact(org.jamocha.dn.memory.Fact)} .
	 */
	@Test
	public void testRetractFact() {
		Path p1 = new Path(Template.STRING);
		@SuppressWarnings("unused")
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		// TODO implement when retractfact is done
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getOutgoingEdges()}.
	 */
	@Test
	public void testGetOutgoingEdges() {
		final Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		final Collection<? extends Edge> outgoingEdges = otn.getOutgoingPositiveEdges();
		assertNotNull(outgoingEdges);
		assertEquals(0, outgoingEdges.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		assertEquals(0, otn.getMemory().size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getNumberOfOutgoingEdges()}.
	 */
	@Test
	public void testGetNumberOfOutgoingEdges() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		assertEquals(0, otn.getNumberOfOutgoingEdges());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)} .
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testDelocalizeAddress() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		otn.delocalizeAddress(p1.getFactAddressInCurrentlyLowestNode());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testGetIncomingEdges() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, p1);
		otn.getIncomingEdges();
	}

}
