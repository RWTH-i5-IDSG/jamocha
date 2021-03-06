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
package test.jamocha.dn.memory.javaimpl;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.*;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerMain;
import org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.*;
import org.jamocha.filter.AddressNodeFilterSet;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.filter.AddressNodeFilterSet.AddressMatchingConfiguration;
import org.jamocha.filter.AddressNodeFilterSet.NormalAddressNodeFilterSet;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.filter.PathNodeFilterSetToAddressNodeFilterSetTranslator;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.junit.*;
import test.jamocha.filter.FilterMockup;
import test.jamocha.util.Slots;

import java.util.*;

import static org.junit.Assert.*;
import static test.jamocha.util.CounterColumnMatcherMockup.COUNTER_COLUMN_MATCHER_MOCKUP;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class MemoryHandlerTempTest {

    private static MemoryHandlerMain memoryHandlerMain;
    private static NodeMockup node, nodeLeft, nodeRight;
    private static org.jamocha.dn.memory.javaimpl.FactAddress factAddress;
    private static SlotAddress slotAddress;
    private static Edge originInput;

    static final int FA_SIZE = 10;
    static final FactAddress[] FA = new FactAddress[FA_SIZE];

    static {
        for (int i = 0; i < 10; i++) {
            FA[i] = new org.jamocha.dn.memory.javaimpl.FactAddress(i);
        }
    }

    private static class NodeMockup extends Node {

        private class EdgeMockup extends EdgeImpl implements Edge {

            final int offset;
            final LinkedList<org.jamocha.dn.memory.MemoryHandlerPlusTemp> plusTemps = new LinkedList<>();

            EdgeMockup(final Node sourceNode, final Node targetNode, final int offset) {
                super(sourceNode, targetNode, AddressNodeFilterSet.EMPTY);
                this.offset = offset;
                activateTokenQueue();
            }

            @Override
            public void processPlusToken(final org.jamocha.dn.memory.MemoryHandlerTemp memory) {
            }

            @Override
            public void processMinusToken(final org.jamocha.dn.memory.MemoryHandlerTemp memory) {
            }

            @Override
            public FactAddress localizeAddress(final FactAddress addressInParent) {
                return FA[((org.jamocha.dn.memory.javaimpl.FactAddress) addressInParent).getIndex() + offset];
            }

            @Override
            public LinkedList<org.jamocha.dn.memory.MemoryHandlerPlusTemp> getTempMemories() {
                return plusTemps;
            }

            @Override
            public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map) {
            }

            @Override
            public void enqueueMemory(final MemoryHandlerMinusTemp mem) {
            }

            @Override
            public void enqueueMemory(final org.jamocha.dn.memory.MemoryHandlerPlusTemp mem) {
            }

            @Override
            public boolean targetsBeta() {
                return true;
            }
        }

        int numChildren;
        int currentOffset = 0;

        @SuppressWarnings("deprecation")
        NodeMockup(final Network network, final int numChildren, final Node... parents) {
            super(network, parents);
            this.numChildren = numChildren;
        }

        NodeMockup(final Network network, final int numChildren, final Template template) {
            super(network, template);
            this.numChildren = numChildren;
        }

        @Override
        public int getNumberOfOutgoingEdges() {
            return numChildren;
        }

        @Override
        protected Edge newEdge(final Node source) {
            final Edge edge = new EdgeMockup(source, this, currentOffset);
            currentOffset += source.getMemory().getTemplate().length;
            return edge;
        }

        @Override
        public AddressPredecessor delocalizeAddress(final FactAddress localNetworkFactAddress) {
            final org.jamocha.dn.memory.javaimpl.FactAddress factAddress =
                    (org.jamocha.dn.memory.javaimpl.FactAddress) localNetworkFactAddress;
            int pos = 0;
            Edge originEdge = null;
            for (final Edge edge : incomingEdges) {
                if (pos > factAddress.getIndex()) break;
                if (pos + edge.getSourceNode().getMemory().getTemplate().length > factAddress.getIndex()) {
                    originEdge = edge;
                    break;
                }
                pos += edge.getSourceNode().getMemory().getTemplate().length;
            }
            assert originEdge != null;
            return new AddressPredecessor(originEdge, FA[factAddress.getIndex() - pos]);
        }

        @Override
        public void shareNode(final PathNodeFilterSet filter, final Map<Path, FactAddress> map, final Path... paths) {
        }

        @Override
        public <V extends NodeVisitor> V accept(final V visitor) {
            return visitor;
        }

    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        factAddress = new org.jamocha.dn.memory.javaimpl.FactAddress(0);
        slotAddress = new SlotAddress(0);
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
        nodeLeft = new NodeMockup(Network.DEFAULTNETWORK, 1, Slots.STRING);
        nodeRight = new NodeMockup(Network.DEFAULTNETWORK, 1, Slots.STRING);
        node = new NodeMockup(Network.DEFAULTNETWORK, 1, nodeLeft, nodeRight);
        originInput = node.getIncomingEdges()[0];
        memoryHandlerMain =
                (MemoryHandlerMain) Network.DEFAULTNETWORK.getMemoryFactory().newMemoryHandlerMain(Slots.STRING);
        assert node.getIncomingEdges().length == 2;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#newBetaTemp(MemoryHandlerMain, Edge,
     * AddressNodeFilterSet)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testNewBetaTempFullJoin() throws CouldNotAcquireLockException {
        Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> token = nodeRight.getMemory()
                .newPlusToken(nodeRight, Slots.STRING.newFact("Fakt3"), Slots.STRING.newFact("Fakt4"));
        token.getLeft().releaseLock();
        token = nodeLeft.getMemory()
                .newPlusToken(nodeLeft, Slots.STRING.newFact("Fakt1"), Slots.STRING.newFact("Fakt2"));
        final MemoryHandlerPlusTemp token1 = (MemoryHandlerPlusTemp) node.getMemory()
                .processTokenInBeta(token.getLeft(), originInput, PathNodeFilterSetToAddressNodeFilterSetTranslator
                        .translate(FilterMockup.alwaysTrue(), COUNTER_COLUMN_MATCHER_MOCKUP));
        assertEquals(4, token1.size());
        assertEquals(2, token1.getTemplate().length);
        String s = (String) token1.getValue(FA[0], slotAddress, 0);
        assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
        s = (String) token1.getValue(FA[0], slotAddress, 1);
        assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
        s = (String) token1.getValue(FA[0], slotAddress, 2);
        assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
        s = (String) token1.getValue(FA[0], slotAddress, 3);
        assertTrue(s.equals("Fakt1") || s.equals("Fakt2"));
        s = (String) token1.getValue(FA[1], slotAddress, 0);
        assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
        s = (String) token1.getValue(FA[1], slotAddress, 1);
        assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
        s = (String) token1.getValue(FA[1], slotAddress, 2);
        assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
        s = (String) token1.getValue(FA[1], slotAddress, 3);
        assertTrue(s.equals("Fakt3") || s.equals("Fakt4"));
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#newBetaTemp(MemoryHandlerMain, Edge,
     * AddressNodeFilterSet)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testNewBetaTempSelectiveJoin() throws CouldNotAcquireLockException {
        FunctionDictionary.load();
        final FunctionWithArguments<ParameterLeaf> pl1 = new ParameterLeaf(SlotType.STRING, 0);
        final FunctionWithArguments<ParameterLeaf> pl2 = new ParameterLeaf(SlotType.STRING, 1);
        final Predicate eq = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
        final PredicateWithArguments<ParameterLeaf> faw = new PredicateWithArgumentsComposite<>(eq, pl1, pl2);
        final AddressFilter filter = new AddressFilter(faw, new SlotInFactAddress[]{
                new SlotInFactAddress(new org.jamocha.dn.memory.javaimpl.FactAddress(0), new SlotAddress(0)),
                new SlotInFactAddress(new org.jamocha.dn.memory.javaimpl.FactAddress(1), new SlotAddress(0))});
        final AddressNodeFilterSet filterSet =
                new AddressNodeFilterSet(new HashSet<FactAddress>(), new HashSet<FactAddress>(),
                        new HashSet<AddressFilter>(Arrays.asList(filter)), (NormalAddressNodeFilterSet) null,
                        Collections.<AddressMatchingConfiguration>emptyList());
        Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> token = nodeRight.getMemory()
                .newPlusToken(nodeRight, Slots.STRING.newFact("Fakt1"), Slots.STRING.newFact("Fakt3"));
        token.getLeft().releaseLock();
        token = nodeLeft.getMemory()
                .newPlusToken(nodeLeft, Slots.STRING.newFact("Fakt1"), Slots.STRING.newFact("Fakt2"));
        final MemoryHandlerPlusTemp token1 =
                (MemoryHandlerPlusTemp) node.getMemory().processTokenInBeta(token.getLeft(), originInput, filterSet);
        assertEquals(1, token1.size());
        assertEquals(2, token1.getTemplate().length);
        assertEquals("Fakt1", token1.getValue(FA[0], slotAddress, 0));
        assertEquals("Fakt1", token1.getValue(FA[1], slotAddress, 0));
    }

    /**
     * Test method for {@link MemoryHandlerMain#processTokenInAlpha(org.jamocha.dn.memory.MemoryHandlerTemp, Edge,
     * AddressNodeFilterSet)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testNewAlphaTemp() throws CouldNotAcquireLockException {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> token =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        MemoryHandlerPlusTemp memoryTempHandler = (MemoryHandlerPlusTemp) memoryHandlerMain
                .processTokenInAlpha(token.getLeft(), node.getIncomingEdges()[0],
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(FilterMockup.alwaysTrue(), COUNTER_COLUMN_MATCHER_MOCKUP));
        assertEquals(1, memoryTempHandler.size());
        memoryTempHandler = (MemoryHandlerPlusTemp) memoryHandlerMain
                .processTokenInAlpha(token.getLeft(), node.getIncomingEdges()[0],
                        PathNodeFilterSetToAddressNodeFilterSetTranslator
                                .translate(FilterMockup.alwaysFalse(), COUNTER_COLUMN_MATCHER_MOCKUP));
        assertEquals(0, memoryTempHandler.size());
    }

    /**
     * Test method for {@link MemoryHandlerMain#newPlusToken(Node, Fact...)}.
     */
    @Test
    public void testNewToken() {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertNotNull(memoryHandlerTemp);
        assertNotNull(memoryHandlerTemp.getLeft());
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#size()}.
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testSize() throws InterruptedException {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertNotNull(memoryHandlerTemp);
        assertNotNull(memoryHandlerTemp.getLeft());
        assertEquals(1, memoryHandlerTemp.getLeft().size());
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#releaseLock()} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testReleaseLock() throws InterruptedException {
        Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertEquals(1, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(1, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(1, memoryHandlerMain.size());
        final Node node4 = new NodeMockup(Network.DEFAULTNETWORK, 4, Slots.STRING);
        new NodeMockup(Network.DEFAULTNETWORK, 999, node4);
        memoryHandlerTemp = memoryHandlerMain.newPlusToken(node4, Slots.STRING.newFact("Test2"));
        assertEquals(1, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(1, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(1, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(1, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(2, memoryHandlerMain.size());
        memoryHandlerTemp.getLeft().releaseLock();
        assertEquals(2, memoryHandlerMain.size());
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#getValue(org.jamocha.dn.memory
     * .FactAddress, org.jamocha.dn.memory.SlotAddress, int)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testGetValue() throws InterruptedException {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertNotNull(memoryHandlerTemp);
        assertNotNull(memoryHandlerTemp.getLeft());
        assertEquals("Test", memoryHandlerTemp.getLeft().getValue(factAddress, slotAddress, 0));
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#getValue(org.jamocha.dn.memory
     * .FactAddress, org.jamocha.dn.memory.SlotAddress, int)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test
    public void testGetValueRowOutOfBounds() throws InterruptedException {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertNotNull(memoryHandlerTemp);
        assertNotNull(memoryHandlerTemp.getLeft());
        try {
            memoryHandlerTemp.getLeft().getValue(factAddress, slotAddress, 1);
            fail();
        } catch (final IndexOutOfBoundsException | AssertionError e) {
            // ok
        }
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#getValue(org.jamocha.dn.memory
     * .FactAddress, org.jamocha.dn.memory.SlotAddress, int)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetValueSlotOutOfBounds() throws InterruptedException {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertNotNull(memoryHandlerTemp);
        assertNotNull(memoryHandlerTemp.getLeft());
        final FactAddress factAddress1 = new org.jamocha.dn.memory.javaimpl.FactAddress(1);
        memoryHandlerTemp.getLeft().getValue(factAddress1, slotAddress, 0);
    }

    /**
     * Test method for {@link org.jamocha.dn.memory.javaimpl.MemoryHandlerPlusTemp#getValue(org.jamocha.dn.memory
     * .FactAddress, org.jamocha.dn.memory.SlotAddress, int)} .
     *
     * @throws CouldNotAcquireLockException
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetValueFactOutOfBounds() throws InterruptedException {
        final Pair<? extends org.jamocha.dn.memory.MemoryHandlerPlusTemp, MemoryFact[]> memoryHandlerTemp =
                memoryHandlerMain.newPlusToken(node, Slots.STRING.newFact("Test"));
        assertNotNull(memoryHandlerTemp);
        assertNotNull(memoryHandlerTemp.getLeft());
        final SlotAddress slotAddress1 = new SlotAddress(1);
        memoryHandlerTemp.getLeft().getValue(factAddress, slotAddress1, 0);
    }
}
