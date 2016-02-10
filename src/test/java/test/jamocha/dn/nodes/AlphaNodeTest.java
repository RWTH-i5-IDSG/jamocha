/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package test.jamocha.dn.nodes;

import org.hamcrest.Matchers;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.junit.*;
import test.jamocha.filter.FilterMockup;
import test.jamocha.filter.FilterMockup.PathAndSlotAddress;
import test.jamocha.util.Slots;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static test.jamocha.util.ShareNode.shareOTN;

/**
 * Test class for {@link AlphaNode}.
 *
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class AlphaNodeTest {

    static final SlotAddress S_1 = new SlotAddress(0);

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
     * Test method for {@link org.jamocha.dn.nodes.AlphaNode#AlphaNode(Network, org.jamocha.filter.PathFilter)} .
     */
    @Test
    public void testAlphaNode() {
        final Path p1 = new Path(Slots.STRING);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.STRING);
        shareOTN(otn, Collections.emptyMap(), p1);
        new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(false, new PathAndSlotAddress(p1, S_1)));
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getOutgoingEdges()}.
     */
    @Test
    public void testGetOutgoingEdges() {
        final Path p1 = new Path(Slots.BOOLEAN);
        final Path p2 = new Path(Slots.BOOLEAN);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.BOOLEAN);
        shareOTN(otn, Collections.emptyMap(), p1, p2);
        final FilterMockup filter = new FilterMockup(true, new PathAndSlotAddress(p1, S_1));
        final AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, filter);
        alpha.shareNode(filter, Collections.singletonMap(p2, p1.getFactAddressInCurrentlyLowestNode()), p2);
        Collection<? extends Edge> children = alpha.getOutgoingEdges();
        assertNotNull(children);
        assertEquals(0, children.size());
        final AlphaNode alphaB1 =
                new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p1, S_1)));
        children = alpha.getOutgoingEdges();
        assertNotNull(children);
        assertEquals(1, children.size());
        assertTrue(children.contains(alphaB1.getIncomingEdges()[0]));
        final AlphaNode alphaB2 =
                new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p2, S_1)));
        children = alpha.getOutgoingEdges();
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
        final Path p1 = new Path(Slots.BOOLEAN);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.BOOLEAN);
        shareOTN(otn, Collections.emptyMap(), p1);
        final AlphaNode alpha =
                new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p1, S_1)));
        final MemoryHandlerMain memory = alpha.getMemory();
        assertNotNull(memory);
        assertEquals(0, memory.size());
        assertEquals(1, memory.getTemplate().length);
        assertEquals(Slots.BOOLEAN, memory.getTemplate()[0]);
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getNumberOfOutgoingEdges()}.
     */
    @Test
    public void testNumberOfOutgoingEdges() {
        final Path p1 = new Path(Slots.BOOLEAN);
        final Path p2 = new Path(Slots.BOOLEAN);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.BOOLEAN);
        shareOTN(otn, Collections.emptyMap(), p1, p2);
        final FilterMockup filter = new FilterMockup(true, new PathAndSlotAddress(p1, S_1));
        final AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, filter);
        assertEquals(0, alpha.getNumberOfOutgoingEdges());
        alpha.shareNode(filter, Collections.singletonMap(p2, p1.getFactAddressInCurrentlyLowestNode()), p2);
        assertEquals(0, alpha.getNumberOfOutgoingEdges());
        // final AlphaNode alphaB1 =
        new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p1, S_1)));
        assertEquals(1, alpha.getNumberOfOutgoingEdges());
        // final AlphaNode alphaB2 =
        new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p2, S_1)));
        assertEquals(2, alpha.getNumberOfOutgoingEdges());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)} .
     */
    @Test
    public void testDelocalizeAddress() {
        final Path p1 = new Path(Slots.BOOLEAN);
        final Path p2 = new Path(Slots.BOOLEAN);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.BOOLEAN);
        shareOTN(otn, Collections.emptyMap(), p1, p2);
        final FactAddress fa1 = p1.getFactAddressInCurrentlyLowestNode();
        final FilterMockup filter = new FilterMockup(true, new PathAndSlotAddress(p1, S_1));
        final AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, filter);
        assertNotSame(fa1, p1.getFactAddressInCurrentlyLowestNode());
        alpha.delocalizeAddress(p1.getFactAddressInCurrentlyLowestNode());
        alpha.shareNode(filter, Collections.singletonMap(p2, p1.getFactAddressInCurrentlyLowestNode()), p2);
        assertNotSame(fa1, p2.getFactAddressInCurrentlyLowestNode());
        assertSame(p1.getFactAddressInCurrentlyLowestNode(), p2.getFactAddressInCurrentlyLowestNode());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetIncomingEdges() {
        final Path p1 = new Path(Slots.BOOLEAN);
        final Path p2 = new Path(Slots.BOOLEAN);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.BOOLEAN);
        shareOTN(otn, Collections.emptyMap(), p1, p2);
        final FilterMockup filter = new FilterMockup(true, new PathAndSlotAddress(p1, S_1));
        final AlphaNode alpha = new AlphaNode(Network.DEFAULTNETWORK, filter);
        Edge[] incomingEdges = alpha.getIncomingEdges();
        assertEquals(1, incomingEdges.length);
        assertEquals(otn.getOutgoingEdges().iterator().next(), incomingEdges[0]);
        alpha.shareNode(filter, Collections.singletonMap(p2, p1.getFactAddressInCurrentlyLowestNode()), p2);
        incomingEdges = alpha.getIncomingEdges();
        assertEquals(1, incomingEdges.length);
        assertEquals(otn.getOutgoingEdges().iterator().next(), incomingEdges[0]);
        final AlphaNode alphaB1 =
                new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p1, S_1)));
        incomingEdges = alpha.getIncomingEdges();
        assertEquals(1, incomingEdges.length);
        assertEquals(otn.getOutgoingEdges().iterator().next(), incomingEdges[0]);
        incomingEdges = alphaB1.getIncomingEdges();
        assertEquals(1, incomingEdges.length);
        assertEquals(alpha.getOutgoingEdges().iterator().next(), incomingEdges[0]);
        // final AlphaNode alphaB2 =
        new AlphaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p2, S_1)));
        incomingEdges = alphaB1.getIncomingEdges();
        assertEquals(1, incomingEdges.length);
        assertEquals(alpha.getOutgoingEdges().iterator().next(), incomingEdges[0]);
        incomingEdges = alphaB1.getIncomingEdges();
        assertEquals(1, incomingEdges.length);
        assertThat((Collection<Edge>) alpha.getOutgoingEdges(), Matchers.hasItem(incomingEdges[0]));
    }
}
