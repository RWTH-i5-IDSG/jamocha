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

import java.util.Set;

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 */
public class ObjectTypeNodeTest {

	private static MemoryFactory memoryFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		memoryFactory = (MemoryFactory) MemoryFactory.getMemoryFactory();
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
	 * {@link org.jamocha.dn.nodes.ObjectTypeNode#ObjectTypeNode(org.jamocha.dn.memory.MemoryFactory, org.jamocha.dn.memory.Template, org.jamocha.filter.Path[])}
	 * .
	 */
	@Test
	public void testObjectTypeNode() {
		new ObjectTypeNode(memoryFactory, Template.STRING);
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.ObjectTypeNode#getTemplate()}
	 * .
	 */
	@Test
	public void testGetTemplate() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		assertEquals(Template.STRING, otn.getTemplate());
		otn = new ObjectTypeNode(memoryFactory, Template.DOUBLE, p1);
		assertEquals(Template.DOUBLE, otn.getTemplate());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.ObjectTypeNode#assertFact(org.jamocha.dn.memory.Fact)}
	 * .
	 */
	@Test
	public void testAssertFact() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		otn.assertFact(new Fact(Template.STRING, "TestValue 1"));
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.ObjectTypeNode#retractFact(org.jamocha.dn.memory.Fact)}
	 * .
	 */
	@Test
	public void testRetractFact() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		// TODO implement when retractfact is done
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getChildren()}.
	 */
	@Test
	public void testGetChildren() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		final Set<Edge> children = otn.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
	 */
	@Test
	public void testGetMemory() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		assertEquals(0, otn.getMemory().size());
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#numChildren()}.
	 */
	@Test
	public void testNumChildren() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		assertEquals(0, otn.numChildren());
	}

	/**
	 * Test method for
	 * {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)}
	 * .
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testDelocalizeAddress() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		otn.delocalizeAddress(PathTransformation
				.getFactAddressInCurrentlyLowestNode(p1));
	}

	/**
	 * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testGetIncomingEdges() {
		Path p1 = new Path(Template.STRING);
		ObjectTypeNode otn = new ObjectTypeNode(memoryFactory, Template.STRING,
				p1);
		otn.getIncomingEdges();
	}

}
