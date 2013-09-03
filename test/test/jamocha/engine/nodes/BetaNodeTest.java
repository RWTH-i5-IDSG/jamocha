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
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.engine.memory.FactAddress;
import org.jamocha.engine.memory.MemoryFactory;
import org.jamocha.engine.memory.MemoryHandlerMain;
import org.jamocha.engine.memory.Template;
import org.jamocha.engine.nodes.BetaNode;
import org.jamocha.engine.nodes.Node.Edge;
import org.jamocha.engine.nodes.ObjectTypeNode;
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
 * 
 */
public class BetaNodeTest {

	private static class FactAddressMockup implements FactAddress {
	}

	private static MemoryFactory memoryFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		memoryFactory = org.jamocha.engine.memory.javaimpl.MemoryFactory
				.getMemoryFactory();
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
	 * {@link org.jamocha.engine.nodes.BetaNode#BetaNode(org.jamocha.engine.memory.MemoryFactory, org.jamocha.filter.Filter)}
	 * .
	 */
	@Test
	public void testBetaNode() {
		BetaNode beta = new BetaNode(memoryFactory, FilterMockup.alwaysTrue());
	}

	/**
	 * Test method for {@link org.jamocha.engine.nodes.Node#getChildren()}.
	 */
	@Test
	public void testGetChildren() {
		BetaNode beta = new BetaNode(memoryFactory, FilterMockup.alwaysTrue());
		final Set<Edge> children = beta.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());
	}

	/**
	 * Test method for {@link org.jamocha.engine.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		BetaNode beta = new BetaNode(memoryFactory, FilterMockup.alwaysTrue());
		final MemoryHandlerMain memory = beta.getMemory();
		assertNotNull(memory);
		assertEquals(0, memory.size());
	}

	/**
	 * Test method for {@link org.jamocha.engine.nodes.Node#numChildren()}.
	 */
	@Test
	public void testNumChildren() {
		BetaNode beta = new BetaNode(memoryFactory, FilterMockup.alwaysTrue());
		assertEquals(0, beta.numChildren());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.engine.nodes.Node#delocalizeAddress(org.jamocha.engine.memory.FactAddress)}
	 * .
	 */
	@Test
	public void testDelocalizeAddress() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		FactAddress fa1 = new FactAddressMockup();
		FactAddress fa2 = new FactAddressMockup();
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING);
		Set<Path> joinedWith = new HashSet<>();
		joinedWith.add(p1);
		PathTransformation.setPathInfo(p1, new PathInfo(otn, fa1, joinedWith));
		joinedWith = new HashSet<>();
		joinedWith.add(p2);
		PathTransformation.setPathInfo(p2, new PathInfo(otn, fa2, joinedWith));
		BetaNode beta = new BetaNode(memoryFactory, new FilterMockup(true, p1,
				p2));
		assertEquals(fa1, beta.delocalizeAddress(PathTransformation
				.getFactAddressInCurrentlyLowestNode(p1)));
		assertEquals(fa2, beta.delocalizeAddress(PathTransformation
				.getFactAddressInCurrentlyLowestNode(p2)));
	}

	/**
	 * Test method for {@link org.jamocha.engine.nodes.Node#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {
		Path p1 = new Path(Template.STRING);
		Path p2 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING);
		Set<Path> joinedWith = new HashSet<>();
		joinedWith.add(p1);
		PathTransformation.setPathInfo(p1, new PathInfo(otn, null, joinedWith));
		joinedWith = new HashSet<>();
		joinedWith.add(p2);
		PathTransformation.setPathInfo(p2, new PathInfo(otn, null, joinedWith));
		BetaNode beta = new BetaNode(memoryFactory, new FilterMockup(true, p1,
				p2));
		final Edge[] incomingEdges = beta.getIncomingEdges();
		assertEquals(2, incomingEdges.length);
		assertEquals(beta, incomingEdges[0].getTargetNode());
		assertEquals(beta, incomingEdges[1].getTargetNode());
		assertEquals(otn, incomingEdges[0].getSourceNode());
		assertEquals(otn, incomingEdges[1].getSourceNode());
	}

}
