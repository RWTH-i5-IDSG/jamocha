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

package test.jamocha.dn.compiler.ecblocks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jamocha.dn.ConstructCache;
import org.jamocha.dn.Network;
import org.jamocha.dn.compiler.ecblocks.ExistentialInfo;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FilterOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ImplicitOccurrenceNode;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.*;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.function.impls.predicates.Less;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.junit.Test;
import test.jamocha.util.Slots;
import test.jamocha.util.builder.rule.ECSetRuleBuilder;

import java.util.*;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.*;
import static org.jamocha.util.Lambdas.newIdentityHashSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.junit.Assert.*;
import static test.jamocha.util.Constants.of;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class AssignmentGraphTest {
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testSimpleExistential() throws Exception {
        final Network network = new Network();
        final ECSetRuleBuilder ruleBuilder = new ECSetRuleBuilder(network.getInitialFactTemplate(), "r1");

        final Template.Slot t1s1 = Slots.newLong("s1");
        final Template t1 = network.defTemplate("t1", "", t1s1);
        final Predicate lessLL = FunctionDictionary.lookupPredicate(Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);

        final SingleFactVariable fv1 = ruleBuilder.newFactVariable(t1);
        final SingleSlotVariable sv1 = ruleBuilder.newSlotVariable(fv1, t1s1);

        final EquivalenceClass ec1 = sv1.getEqual();
        ec1.add(7);

        final SingleFactVariable fv2 = ruleBuilder.newFactVariable(t1);
        final SingleSlotVariable sv2 = ruleBuilder.newSlotVariable(fv2, t1s1);
        final EquivalenceClass ec2 = sv2.getEqual();

        final SingleFactVariable ex1fv1;
        final SingleSlotVariable ex1sv1;
        final EquivalenceClass ex1ec1, ex1twoEC;
        try (final ECSetRuleBuilder.ExistentialConditionProxy ex1 = ruleBuilder.newExistentialScope(false)) {
            ex1fv1 = ex1.newFactVariable(t1);
            ex1sv1 = ex1.newSlotVariable(ex1fv1, t1s1);
            ex1ec1 = ex1sv1.getEqual();
            ex1ec1.addEqualParentEquivalenceClass(ec2);
            ex1.newPredicateBuilder(lessLL).addLong(2).addEC(ex1ec1).build();
            ex1twoEC = ex1.getECFromLong(2);
        }

        ruleBuilder.newPredicateBuilder(lessLL).addEC(ec1).addLong(9).build();
        final EquivalenceClass nineEC = ruleBuilder.getECFromLong(9);

        final ConstructCache.Defrule.ECSetRule rule = ruleBuilder.build();

        final Set<ECFilterSet> condition = rule.getCondition();
        assertThat(condition, hasSize(2));
        final ECFilter regularFilter, closureFilter, pureFilter;
        final ExistentialInfo existentialInfo;
        {
            final Iterator<ECFilterSet> iterator = condition.iterator();
            final ECFilterSet filterSet0 = iterator.next();
            final ECFilterSet filterSet1 = iterator.next();
            final ECFilterSet.ECExistentialSet ecExistentialSet;
            if (filterSet0 instanceof ECFilter) {
                assertThat(filterSet1, instanceOf(ECFilterSet.ECExistentialSet.class));
                regularFilter = (ECFilter) filterSet0;
                ecExistentialSet = (ECFilterSet.ECExistentialSet) filterSet1;
            } else if (filterSet1 instanceof ECFilter) {
                assertThat(filterSet0, instanceOf(ECFilterSet.ECExistentialSet.class));
                regularFilter = (ECFilter) filterSet1;
                ecExistentialSet = (ECFilterSet.ECExistentialSet) filterSet0;
            } else {
                fail();
                regularFilter = null;
                ecExistentialSet = null;
            }
            existentialInfo = ExistentialInfo.get(ecExistentialSet);
            closureFilter = ecExistentialSet.getExistentialClosure();
            final Set<ECFilterSet> purePart = ecExistentialSet.getPurePart();
            assertThat(purePart, hasSize(1));
            final ECFilterSet firstPure = purePart.iterator().next();
            assertThat(firstPure, instanceOf(ECFilter.class));
            pureFilter = ((ECFilter) firstPure);
        }

        final AssignmentGraph assignmentGraph = new AssignmentGraph();
        assignmentGraph.addRule(rule);

        // SETUP COMPLETE, START ACTUAL TESTING

        final IdentityHashMap<EquivalenceClass, List<BindingNode>> ecToElements = assignmentGraph.getEcToElements();

        final Set<EquivalenceClass> usedECs = newIdentityHashSet(ImmutableSet.of(ec1, ec2, ex1ec1, ex1twoEC, nineEC));
        final ConstantBindingNode sevenBN, twoBN, nineBN;
        final SlotBindingNode sv1bn, sv2bn, ex1sv1bn;
        assertThat(ecToElements.keySet(), equalTo(usedECs));
        {
            final List<BindingNode> ec1bindingNodes = ecToElements.get(ec1);
            assertThat(ec1bindingNodes, hasSize(2));
            final Map<BindingType, List<BindingNode>> partition =
                    ec1bindingNodes.stream().collect(groupingBy(BindingNode::getNodeType));
            final List<BindingNode> constantBindings = partition.get(BindingType.CONSTANT_EXPRESSION);
            assertNotNull(constantBindings);
            assertThat(constantBindings, hasSize(1));
            final BindingNode constantBinding = constantBindings.get(0);
            assertThat(constantBinding, instanceOf(ConstantBindingNode.class));
            sevenBN = ((ConstantBindingNode) constantBinding);
            final ConstantLeaf<TemplateSlotLeaf> constantLeaf = sevenBN.getConstant();
            assertThat(constantLeaf, equalTo(of(7)));
            final List<BindingNode> slotBindings = partition.get(BindingType.SLOT_OR_FACT_BINDING);
            assertNotNull(slotBindings);
            assertThat(slotBindings, hasSize(1));
            final BindingNode slotBinding = slotBindings.get(0);
            assertThat(slotBinding, instanceOf(SlotBindingNode.class));
            final SlotBindingNode slot = (SlotBindingNode) slotBinding;
            assertThat(slot.getGroupingFactVariable(), equalTo(fv1));
            assertThat(slot.getSlotInGroupingFactVariable(), equalTo(sv1));
            sv1bn = slot;
        }
        {
            final List<BindingNode> ec2bindingNodes = ecToElements.get(ec2);
            assertThat(ec2bindingNodes, hasSize(1));
            final Map<BindingType, List<BindingNode>> partition =
                    ec2bindingNodes.stream().collect(groupingBy(BindingNode::getNodeType));
            final List<BindingNode> slotBindings = partition.get(BindingType.SLOT_OR_FACT_BINDING);
            assertNotNull(slotBindings);
            assertThat(slotBindings, hasSize(1));
            final BindingNode slotBinding = slotBindings.get(0);
            assertThat(slotBinding, instanceOf(SlotBindingNode.class));
            final SlotBindingNode slot = (SlotBindingNode) slotBinding;
            assertThat(slot.getGroupingFactVariable(), equalTo(fv2));
            assertThat(slot.getSlotInGroupingFactVariable(), equalTo(sv2));
            sv2bn = slot;
        }
        {
            final List<BindingNode> ex1ec1bindingNodes = ecToElements.get(ex1ec1);
            assertThat(ex1ec1bindingNodes, hasSize(1));
            final Map<BindingType, List<BindingNode>> partition =
                    ex1ec1bindingNodes.stream().collect(groupingBy(BindingNode::getNodeType));
            final List<BindingNode> slotBindings = partition.get(BindingType.SLOT_OR_FACT_BINDING);
            assertNotNull(slotBindings);
            assertThat(slotBindings, hasSize(1));
            final BindingNode slotBinding = slotBindings.get(0);
            assertThat(slotBinding, instanceOf(SlotBindingNode.class));
            final SlotBindingNode slot = (SlotBindingNode) slotBinding;
            assertThat(slot.getGroupingFactVariable(), equalTo(ex1fv1));
            assertThat(slot.getSlotInGroupingFactVariable(), equalTo(ex1sv1));
            ex1sv1bn = slot;
        }
        {
            final List<BindingNode> ex1twoECBindingNodes = ecToElements.get(ex1twoEC);
            assertThat(ex1twoECBindingNodes, hasSize(1));
            final Map<BindingType, List<BindingNode>> partition =
                    ex1twoECBindingNodes.stream().collect(groupingBy(BindingNode::getNodeType));
            final List<BindingNode> constantBindings = partition.get(BindingType.CONSTANT_EXPRESSION);
            assertNotNull(constantBindings);
            assertThat(constantBindings, hasSize(1));
            final BindingNode constantBinding = constantBindings.get(0);
            assertThat(constantBinding, instanceOf(ConstantBindingNode.class));
            twoBN = ((ConstantBindingNode) constantBinding);
            final ConstantLeaf<TemplateSlotLeaf> constantLeaf = twoBN.getConstant();
            assertThat(constantLeaf, equalTo(of(2)));
        }
        {
            final List<BindingNode> nineECBindingNodes = ecToElements.get(nineEC);
            assertThat(nineECBindingNodes, hasSize(1));
            final Map<BindingType, List<BindingNode>> partition =
                    nineECBindingNodes.stream().collect(groupingBy(BindingNode::getNodeType));
            final List<BindingNode> constantBindings = partition.get(BindingType.CONSTANT_EXPRESSION);
            assertNotNull(constantBindings);
            assertThat(constantBindings, hasSize(1));
            final BindingNode constantBinding = constantBindings.get(0);
            assertThat(constantBinding, instanceOf(ConstantBindingNode.class));
            nineBN = ((ConstantBindingNode) constantBinding);
            final ConstantLeaf<TemplateSlotLeaf> constantLeaf = nineBN.getConstant();
            assertThat(constantLeaf, equalTo(of(9)));
        }
        assertNotNull(sv1bn);
        assertNotNull(sv2bn);
        assertNotNull(ex1sv1bn);
        assertNotNull(sevenBN);
        assertNotNull(twoBN);

        final HashMap<FunctionWithArguments<TemplateSlotLeaf>, Set<BindingNode>> directBindingNodes =
                assignmentGraph.getDirectBindingNodes();
        assertThat(directBindingNodes.keySet(), hasSize(4));
        assertThat(directBindingNodes.get(new TemplateSlotLeaf(t1, t1.getSlotAddress(t1s1.getName()))),
                equalTo(ImmutableSet.of(sv1bn, sv2bn, ex1sv1bn)));
        assertThat(directBindingNodes.get(of(7)), equalTo(ImmutableSet.of(sevenBN)));
        assertThat(directBindingNodes.get(of(2)), equalTo(ImmutableSet.of(twoBN)));
        assertThat(directBindingNodes.get(of(9)), equalTo(ImmutableSet.of(nineBN)));

        assertThat(assignmentGraph.getFunctionalExpressionToBindings().keySet(), empty());
        assertThat(assignmentGraph.getFunctionalExpressionBindingToOccurrenceNodes().keySet(), empty());

        final IdentityHashMap<SingleFactVariable, Set<SlotOrFactBindingNode>> templateInstanceToBindingNodes =
                assignmentGraph.getTemplateInstanceToBindingNodes();
        assertThat(templateInstanceToBindingNodes.keySet(), hasSize(3));
        {
            final Set<SlotOrFactBindingNode> fv1nodes = templateInstanceToBindingNodes.get(fv1);
            assertThat(fv1nodes, hasSize(1));
            assertThat(fv1nodes, contains(sv1bn));
        }
        {
            final Set<SlotOrFactBindingNode> fv2nodes = templateInstanceToBindingNodes.get(fv2);
            assertThat(fv2nodes, hasSize(1));
            assertThat(fv2nodes, contains(sv2bn));
        }
        {
            final Set<SlotOrFactBindingNode> ex1fv1nodes = templateInstanceToBindingNodes.get(ex1fv1);
            assertThat(ex1fv1nodes, hasSize(1));
            assertThat(ex1fv1nodes, contains(ex1sv1bn));
        }

        final HashMap<ExistentialInfo.FunctionWithExistentialInfo, Set<ECFilter>> predicateToFilters =
                assignmentGraph.getPredicateToFilters();
        assertThat(predicateToFilters.keySet(), hasSize(2));
        final IdentityHashMap<ECFilter, TreeMap<Integer, FilterOccurrenceNode>> filterToOccurrenceNodes =
                assignmentGraph.getFilterToOccurrenceNodes();
        final Set<FilterOccurrenceNode> myFilterOccurrenceNodes = Sets.newIdentityHashSet();
        assertThat(filterToOccurrenceNodes.keySet(), hasSize(3));
        {
            final ExistentialInfo.FunctionWithExistentialInfo predicate =
                    new ExistentialInfo.FunctionWithExistentialInfo(FunctionWithArgumentsComposite
                            .newPredicateInstance(Less.IN_CLIPS, new TypeLeaf(SlotType.LONG),
                                    new TypeLeaf(SlotType.LONG)), ExistentialInfo.REGULAR);
            final Set<ECFilter> ecFilters = predicateToFilters.get(predicate);
            assertThat(ecFilters, equalTo(ImmutableSet.of(regularFilter, pureFilter)));
            for (final ECFilter ecFilter : ecFilters) {
                final TreeMap<Integer, FilterOccurrenceNode> arguments = filterToOccurrenceNodes.get(ecFilter);
                assertThat(arguments.keySet(), hasSize(2));
                assertThat(arguments, hasKey(0));
                assertThat(arguments, hasKey(1));
                myFilterOccurrenceNodes.addAll(arguments.values());
            }
        }
        {
            final PredicateWithArguments<TypeLeaf> function = FunctionWithArgumentsComposite
                    .newPredicateInstance(Equals.IN_CLIPS, new TypeLeaf(SlotType.LONG), new TypeLeaf(SlotType.LONG));
            final ExistentialInfo.NegatedExistentialInfo existentialInfo0 =
                    new ExistentialInfo.NegatedExistentialInfo(new int[]{0});
            final ExistentialInfo.NegatedExistentialInfo existentialInfo1 =
                    new ExistentialInfo.NegatedExistentialInfo(new int[]{1});
            assertThat(existentialInfo, isOneOf(existentialInfo0, existentialInfo1));
            final Set<ECFilter> ecFilters =
                    predicateToFilters.get(new ExistentialInfo.FunctionWithExistentialInfo(function, existentialInfo));
            assertThat(ecFilters, hasSize(1));
            final ECFilter ecFilter = ecFilters.iterator().next();
            assertThat(ecFilter, is(closureFilter));
            final TreeMap<Integer, FilterOccurrenceNode> arguments = filterToOccurrenceNodes.get(ecFilter);
            assertThat(arguments.keySet(), hasSize(2));
            assertThat(arguments, hasKey(0));
            assertThat(arguments, hasKey(1));
            myFilterOccurrenceNodes.addAll(arguments.values());
        }

        final IdentityHashMap<BindingNode, ImplicitOccurrenceNode> bindingNodeToImplicitOccurrence =
                assignmentGraph.getBindingNodeToImplicitOccurrence();
        final ImmutableSet<BindingNode> myBindingNodes =
                ImmutableSet.of(sv1bn, sv2bn, ex1sv1bn, twoBN, sevenBN, nineBN);
        final Set<ImplicitOccurrenceNode> myImplicitOccurrenceNodes = Sets.newIdentityHashSet();
        assertThat(bindingNodeToImplicitOccurrence.keySet(), equalTo(myBindingNodes));
        for (final BindingNode myBindingNode : myBindingNodes) {
            final ImplicitOccurrenceNode implicitOccurrenceNode = bindingNodeToImplicitOccurrence.get(myBindingNode);
            assertThat(myBindingNode, sameInstance(implicitOccurrenceNode.getCorrespondingBindingNode()));
            myImplicitOccurrenceNodes.add(implicitOccurrenceNode);
        }

        final AssignmentGraph.UnrestrictedGraph graph = assignmentGraph.getGraph();

        final Set<ECOccurrenceNode> occurrenceNodes = graph.getECOccurrenceNodes();
        final Set<BindingNode> bindingNodes = graph.getBindingNodes();
        final Set<ECOccurrenceNode> myOccurrenceNodes = Sets.union(myFilterOccurrenceNodes, myImplicitOccurrenceNodes);
        assertThat(bindingNodes, equalTo(myBindingNodes));
        assertThat(graph.edgeSet().stream().map(AssignmentGraph.Edge::getTarget).collect(toIdentityHashSet()),
                equalTo(myBindingNodes));
        assertThat(occurrenceNodes, equalTo(myOccurrenceNodes));
        assertThat(graph.edgeSet().stream().map(AssignmentGraph.Edge::getSource).collect(toIdentityHashSet()),
                equalTo(myOccurrenceNodes));
        final Set<AssignmentGraph.Edge<ECOccurrenceNode, BindingNode>> myEdges = new HashSet<>();
        final BiConsumer<ECOccurrenceNode, BindingNode> edgeCheck = (e, b) -> {
            final AssignmentGraph.Edge<ECOccurrenceNode, BindingNode> edge = graph.getEdge(e, b);
            assertNotNull(edge);
            myEdges.add(edge);
        };
        {
            for (final BindingNode myBindingNode : myBindingNodes) {
                edgeCheck.accept(bindingNodeToImplicitOccurrence.get(myBindingNode), myBindingNode);
            }
            {
                final TreeMap<Integer, FilterOccurrenceNode> arguments = filterToOccurrenceNodes.get(regularFilter);
                final FilterOccurrenceNode sv1AndSeven = arguments.get(0);
                edgeCheck.accept(sv1AndSeven, sv1bn);
                edgeCheck.accept(sv1AndSeven, sevenBN);
                final FilterOccurrenceNode nine = arguments.get(1);
                edgeCheck.accept(nine, nineBN);
            }
            {
                final TreeMap<Integer, FilterOccurrenceNode> arguments = filterToOccurrenceNodes.get(pureFilter);
                final FilterOccurrenceNode two = arguments.get(0);
                edgeCheck.accept(two, twoBN);
                final FilterOccurrenceNode eSv1 = arguments.get(1);
                edgeCheck.accept(eSv1, ex1sv1bn);
            }
            {
                final TreeMap<Integer, FilterOccurrenceNode> arguments = filterToOccurrenceNodes.get(closureFilter);
                final int[] existentialArguments = existentialInfo.getExistentialArguments();
                final FilterOccurrenceNode eSv1 = arguments.get(existentialArguments[0]);
                edgeCheck.accept(eSv1, ex1sv1bn);
                final FilterOccurrenceNode rSv2 = arguments.get(1 - existentialArguments[0]);
                edgeCheck.accept(rSv2, sv2bn);
            }
            edgeCheck.accept(bindingNodeToImplicitOccurrence.get(sv1bn), sevenBN);
            edgeCheck.accept(bindingNodeToImplicitOccurrence.get(sevenBN), sv1bn);
        }
        assertThat(graph.edgeSet(), equalTo(myEdges));
        // AssignmentGraphToDot.toDot(assignmentGraph, "assignmentGraphTest1.gv");
    }
}
