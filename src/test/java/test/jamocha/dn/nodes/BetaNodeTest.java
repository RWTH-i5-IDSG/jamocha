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

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.junit.*;
import test.jamocha.filter.FilterMockup;
import test.jamocha.filter.FilterMockup.PathAndSlotAddress;
import test.jamocha.util.Slots;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static test.jamocha.util.ShareNode.shareOTN;

/**
 * Test class for {@link BetaNode}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class BetaNodeTest {

    static final SlotAddress s1 = new SlotAddress(0);

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

    public BetaNode createDummyBetaNode() {
        final Path p1 = new Path(Slots.STRING);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.STRING);
        shareOTN(otn, Collections.emptyMap(), p1);
        return new BetaNode(Network.DEFAULTNETWORK, new FilterMockup(true, new PathAndSlotAddress(p1, s1)));
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.BetaNode#BetaNode(Network, org.jamocha.filter.PathFilter)} .
     */
    @Test
    public void testBetaNode() {
        createDummyBetaNode();
    }

    /**
     *
     */
    @Test
    public void testSelfJoin() {
        final Path p1 = new Path(Slots.STRING);
        final Path p2 = new Path(Slots.STRING);
        final Path p3 = new Path(Slots.STRING);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.STRING);
        shareOTN(otn, Collections.emptyMap(), p1, p2, p3);
        final BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
                new FilterMockup(true, new PathAndSlotAddress(p1, s1), new PathAndSlotAddress(p2, s1)));
        Edge[] incomingEdges = beta.getIncomingEdges();
        assertEquals(2, incomingEdges.length);
        assertEquals(otn, incomingEdges[0].getSourceNode());
        assertEquals(otn, incomingEdges[1].getSourceNode());
        assertNotSame(incomingEdges[0], incomingEdges[1]);
        assertSame(beta, p1.getCurrentlyLowestNode());
        assertSame(beta, p2.getCurrentlyLowestNode());
        assertSame(otn, p3.getCurrentlyLowestNode());
        final BetaNode beta2 = new BetaNode(Network.DEFAULTNETWORK,
                new FilterMockup(true, new PathAndSlotAddress(p1, s1), new PathAndSlotAddress(p3, s1)));
        incomingEdges = beta2.getIncomingEdges();
        assertEquals(2, incomingEdges.length);
        assertThat(Arrays.stream(incomingEdges).map(Edge::getSourceNode).collect(toList()),
                containsInAnyOrder(beta, otn));
        assertSame(beta2, p1.getCurrentlyLowestNode());
        assertSame(beta2, p2.getCurrentlyLowestNode());
        assertSame(beta2, p3.getCurrentlyLowestNode());
    }

    /**
     *
     */
    @Test
    public void testNodeSharing() {
        final Path p1 = new Path(Slots.STRING);
        final Path p2 = new Path(Slots.STRING);
        final Path p3 = new Path(Slots.STRING);
        final Path p4 = new Path(Slots.STRING);
        final Path p5 = new Path(Slots.STRING);
        final Path p6 = new Path(Slots.STRING);
        final Path p7 = new Path(Slots.STRING);
        final Path p8 = new Path(Slots.STRING);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.STRING);
        shareOTN(otn, Collections.emptyMap(), p1, p2, p3, p4, p5, p6, p7, p8);
        final FilterMockup betaFilter =
                new FilterMockup(true, new PathAndSlotAddress(p1, s1), new PathAndSlotAddress(p2, s1));
        final BetaNode beta = new BetaNode(Network.DEFAULTNETWORK, betaFilter);
        assertSame(otn, p3.getCurrentlyLowestNode());
        assertSame(otn, p4.getCurrentlyLowestNode());
        final FactAddress factAddressOne = p1.getFactAddressInCurrentlyLowestNode();
        final FactAddress factAddressTwo = p2.getFactAddressInCurrentlyLowestNode();
        {
            final HashMap<Path, FactAddress> map = new HashMap<Path, FactAddress>();
            map.put(p3, factAddressOne);
            map.put(p4, factAddressTwo);
            beta.shareNode(betaFilter, map, p3, p4);
        }
        assertSame(beta, p1.getCurrentlyLowestNode());
        assertSame(beta, p2.getCurrentlyLowestNode());
        assertSame(beta, p3.getCurrentlyLowestNode());
        assertSame(beta, p4.getCurrentlyLowestNode());
        assertSame(p1.getFactAddressInCurrentlyLowestNode(), p3.getFactAddressInCurrentlyLowestNode());
        assertSame(p2.getFactAddressInCurrentlyLowestNode(), p4.getFactAddressInCurrentlyLowestNode());
        final BetaNode betaB = new BetaNode(Network.DEFAULTNETWORK,
                new FilterMockup(true, new PathAndSlotAddress(p1, s1), new PathAndSlotAddress(p3, s1)));
        assertSame(betaB, p1.getCurrentlyLowestNode());
        assertSame(betaB, p2.getCurrentlyLowestNode());
        assertSame(betaB, p3.getCurrentlyLowestNode());
        assertSame(betaB, p4.getCurrentlyLowestNode());
        assertNotSame(p1.getFactAddressInCurrentlyLowestNode(), p3.getFactAddressInCurrentlyLowestNode());
        assertNotSame(p2.getFactAddressInCurrentlyLowestNode(), p4.getFactAddressInCurrentlyLowestNode());
        {
            final HashMap<Path, FactAddress> map = new HashMap<Path, FactAddress>();
            map.put(p5, factAddressOne);
            map.put(p6, factAddressTwo);
            beta.shareNode(betaFilter, map, p5, p6);
        }
        {
            final HashMap<Path, FactAddress> map = new HashMap<Path, FactAddress>();
            map.put(p7, factAddressOne);
            map.put(p8, factAddressTwo);
            beta.shareNode(betaFilter, map, p7, p8);
        }
        {
            final HashMap<Path, FactAddress> map = new HashMap<Path, FactAddress>();
            map.put(p5, p1.getFactAddressInCurrentlyLowestNode());
            map.put(p6, p2.getFactAddressInCurrentlyLowestNode());
            map.put(p7, p3.getFactAddressInCurrentlyLowestNode());
            map.put(p8, p4.getFactAddressInCurrentlyLowestNode());
            betaB.shareNode(betaFilter, map, p5, p7);
        }
        assertSame(betaB, p5.getCurrentlyLowestNode());
        assertSame(betaB, p6.getCurrentlyLowestNode());
        assertSame(betaB, p7.getCurrentlyLowestNode());
        assertSame(betaB, p8.getCurrentlyLowestNode());
        assertNotSame(p5.getFactAddressInCurrentlyLowestNode(), p7.getFactAddressInCurrentlyLowestNode());
        assertNotSame(p6.getFactAddressInCurrentlyLowestNode(), p8.getFactAddressInCurrentlyLowestNode());
        assertSame(p1.getFactAddressInCurrentlyLowestNode(), p5.getFactAddressInCurrentlyLowestNode());
        assertSame(p2.getFactAddressInCurrentlyLowestNode(), p6.getFactAddressInCurrentlyLowestNode());
        assertSame(p3.getFactAddressInCurrentlyLowestNode(), p7.getFactAddressInCurrentlyLowestNode());
        assertSame(p4.getFactAddressInCurrentlyLowestNode(), p8.getFactAddressInCurrentlyLowestNode());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getOutgoingEdges()}.
     */
    @Test
    public void testGetOutgoingEdges() {
        final BetaNode beta = createDummyBetaNode();
        final Collection<? extends Edge> outgoingEdges = beta.getOutgoingEdges();
        assertNotNull(outgoingEdges);
        assertEquals(0, outgoingEdges.size());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getMemory()}.
     */
    @Test
    public void testGetMemory() {
        final BetaNode beta = createDummyBetaNode();
        final MemoryHandlerMain memory = beta.getMemory();
        assertNotNull(memory);
        assertEquals(0, memory.size());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getNumberOfOutgoingEdges()}.
     */
    @Test
    public void testNumChildren() {
        final BetaNode beta = createDummyBetaNode();
        assertEquals(0, beta.getNumberOfOutgoingEdges());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#delocalizeAddress(org.jamocha.dn.memory.FactAddress)} .
     */
    @Test
    public void testDelocalizeAddress() {
        final Path p1 = new Path(Slots.STRING);
        final Path p2 = new Path(Slots.STRING);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.STRING);
        shareOTN(otn, Collections.emptyMap(), p1, p2);
        final FactAddress fa1 = p1.getFactAddressInCurrentlyLowestNode();
        final FactAddress fa2 = p2.getFactAddressInCurrentlyLowestNode();
        final BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
                new FilterMockup(true, new PathAndSlotAddress(p1, s1), new PathAndSlotAddress(p2, s1)));
        assertEquals(fa1, beta.delocalizeAddress(p1.getFactAddressInCurrentlyLowestNode()).getAddress());
        assertEquals(fa2, beta.delocalizeAddress(p2.getFactAddressInCurrentlyLowestNode()).getAddress());
    }

    /**
     * Test method for {@link org.jamocha.dn.nodes.Node#getIncomingEdges()}.
     */
    @Test
    public void testGetIncomingEdges() {
        final Path p = new Path(Slots.STRING);
        final Path p1 = new Path(Slots.STRING);
        final Path p2 = new Path(Slots.STRING);
        final ObjectTypeNode otn = new ObjectTypeNode(Network.DEFAULTNETWORK, Slots.STRING);
        shareOTN(otn, Collections.emptyMap(), p);
        Set<Path> joinedWith = new HashSet<>();
        joinedWith.add(p1);
        p1.setCurrentlyLowestNode(otn);
        p1.setFactAddressInCurrentlyLowestNode(new org.jamocha.dn.memory.javaimpl.FactAddress(0));
        p1.setJoinedWith(joinedWith);
        joinedWith = new HashSet<>();
        joinedWith.add(p2);
        p2.setCurrentlyLowestNode(otn);
        p2.setFactAddressInCurrentlyLowestNode(new org.jamocha.dn.memory.javaimpl.FactAddress(0));
        p2.setJoinedWith(joinedWith);
        shareOTN(otn, Collections.emptyMap(), p1, p2);
        final BetaNode beta = new BetaNode(Network.DEFAULTNETWORK,
                new FilterMockup(true, new PathAndSlotAddress(p1, s1), new PathAndSlotAddress(p2, s1)));
        final Edge[] incomingEdges = beta.getIncomingEdges();
        assertEquals(2, incomingEdges.length);
        assertEquals(beta, incomingEdges[0].getTargetNode());
        assertEquals(beta, incomingEdges[1].getTargetNode());
        assertEquals(otn, incomingEdges[0].getSourceNode());
        assertEquals(otn, incomingEdges[1].getSourceNode());
    }
}
