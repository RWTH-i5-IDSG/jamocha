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
package org.jamocha.dn.compiler.ecblocks.assignmentgraph;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jamocha.dn.ConstructCache;
import org.jamocha.dn.compiler.ecblocks.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FilterOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ImplicitOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.IdentitySetExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.extend.IndexedIdentityMapToSetExtender;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.ImmutableMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.MapAsMinimalMap;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.SetAsMinimalSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IdentityHashMapWithIndexedIdentityKeySet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedIdentityHashSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.indexed.IndexedImmutableSet;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalMapWrapper;
import org.jamocha.dn.compiler.ecblocks.lazycollections.minimal.normal.MinimalSetWrapper;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.IdentitySetReducer;
import org.jamocha.dn.compiler.ecblocks.lazycollections.reduce.IndexedIdentityMapToSetReducer;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.ECFilterSetVisitor;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWAECLeafToTypeLeafTranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.SingleFactVariable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

import static org.jamocha.util.Lambdas.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class AssignmentGraph {
    // the actual graph
    final UnrestrictedGraph graph = new UnrestrictedGraph();
    // cached bindings for the corresponding equivalence classes
    final IdentityHashMap<EquivalenceClass, List<BindingNode>> ecToElements = new IdentityHashMap<>();

    // needed for block consistency checks:
    // lookup map from filter/functional expression to corresponding occurrence nodes (existential stuff marked)
    // lookup map from fact variable (template INSTANCE) to corresponding fact/slot binding nodes

    // explicit filter INSTANCE node groups:
    // lookup map from abstract typed FWA to rule to the set of matching filters
    final HashMap<ExistentialInfo.FunctionWithExistentialInfo, Set<ECFilter>> predicateToFilters = new HashMap<>();
    final IdentityHashMap<ECFilter, TreeMap<Integer, FilterOccurrenceNode>> filterToOccurrenceNodes =
            new IdentityHashMap<>();

    // lookup from template to template instances to the corresponding binding nodes
    final IdentityHashMap<Template, Set<SingleFactVariable>> templateToInstances = new IdentityHashMap<>();
    final IdentityHashMap<SingleFactVariable, IdentityHashMap<SlotAddress, SlotOrFactBindingNode>>
            templateInstanceToBindingNodes = new IdentityHashMap<>();

    // direct bindings (can't use Leaf directly since constants are contained, too)
    // lookup map from abstract 'templated' FWA to set of matching binding nodes
    final HashMap<FunctionWithArguments<TemplateSlotLeaf>, Set<BindingNode>> directBindingNodes = new HashMap<>();

    final HashMap<FunctionWithArguments<TypeLeaf>, Set<FunctionalExpressionBindingNode>>
            functionalExpressionToBindings = new HashMap<>();
    final IdentityHashMap<FunctionalExpressionBindingNode, TreeMap<Integer, FunctionalExpressionOccurrenceNode>>
            functionalExpressionBindingToOccurrenceNodes = new IdentityHashMap<>();


    // lookup map to get the implicit occurrence node for a binding node (other direction is stored within implicit
    // occurrence node)
    final IdentityHashMap<BindingNode, ImplicitOccurrenceNode> bindingNodeToImplicitOccurrence =
            new IdentityHashMap<>();


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Value
    public static class Edge<O extends ECOccurrenceNode, B extends BindingNode> {
        @Nonnull
        final O source;
        @Nonnull
        final B target;
    }

    @RequiredArgsConstructor
    public abstract class Graph<EDGESET extends ImmutableMinimalSet<Edge<ECOccurrenceNode, BindingNode>>, VALUESET
            extends ImmutableMinimalSet<Edge<ECOccurrenceNode, BindingNode>>, KEYSETOFOUTGOING extends
            ImmutableMinimalSet<ECOccurrenceNode>, ENTRYSETOFOUTGOING extends ImmutableMinimalSet<Map
            .Entry<ECOccurrenceNode, VALUESET>>, OUTGOING extends ImmutableMinimalMap<ECOccurrenceNode, VALUESET,
            KEYSETOFOUTGOING, ENTRYSETOFOUTGOING>, KEYSETOFINCOMING extends ImmutableMinimalSet<BindingNode>,
            ENTRYSETOFINCOMING extends ImmutableMinimalSet<Map.Entry<BindingNode, VALUESET>>, INCOMING extends
            ImmutableMinimalMap<BindingNode, VALUESET, KEYSETOFINCOMING, ENTRYSETOFINCOMING>> {
        final OUTGOING outgoingEdges;
        final INCOMING incomingEdges;
        final EDGESET edgeSet;
        final Supplier<VALUESET> valueSetSupplier;

        public EDGESET edgeSet() {
            return this.edgeSet;
        }

        public boolean containsEdge(final Edge<ECOccurrenceNode, BindingNode> edge) {
            return this.edgeSet.contains(edge);
        }

        public boolean containsEdge(final ECOccurrenceNode source, final BindingNode target) {
            return null != getEdge(source, target);
        }

        public Edge<ECOccurrenceNode, BindingNode> getEdge(final ECOccurrenceNode source, final BindingNode target) {
            return this.outgoingEdges.get(source).stream().filter(e -> e.getTarget() == target).findAny().orElse(null);
        }

        protected VALUESET getIncomingEdges(final BindingNode target) {
            final VALUESET edges = this.incomingEdges.get(target);
            return null != edges ? edges : this.valueSetSupplier.get();
        }

        public VALUESET incomingEdgesOf(final BindingNode target) {
            return getIncomingEdges(target);
        }

        public int inDegreeOf(final BindingNode target) {
            return getIncomingEdges(target).size();
        }

        protected VALUESET getOutgoingEdges(final ECOccurrenceNode source) {
            final VALUESET edges = this.outgoingEdges.get(source);
            return null != edges ? edges : this.valueSetSupplier.get();
        }

        public VALUESET outgoingEdgesOf(final ECOccurrenceNode source) {
            return getOutgoingEdges(source);
        }

        public int outDegreeOf(final ECOccurrenceNode source) {
            return getOutgoingEdges(source).size();
        }

        public KEYSETOFOUTGOING getECOccurrenceNodes() {
            return this.outgoingEdges.keySet();
        }

        public KEYSETOFINCOMING getBindingNodes() {
            return this.incomingEdges.keySet();
        }
    }

    public class UnrestrictedGraph extends
            Graph<SetAsMinimalSet<Edge<ECOccurrenceNode, BindingNode>>, SetAsMinimalSet<Edge<ECOccurrenceNode,
                    BindingNode>>, SetAsMinimalSet<ECOccurrenceNode>, SetAsMinimalSet<Map.Entry<ECOccurrenceNode,
                    SetAsMinimalSet<Edge<ECOccurrenceNode, BindingNode>>>>, MapAsMinimalMap<ECOccurrenceNode,
                    SetAsMinimalSet<Edge<ECOccurrenceNode, BindingNode>>>, SetAsMinimalSet<BindingNode>,
                    SetAsMinimalSet<Map.Entry<BindingNode, SetAsMinimalSet<Edge<ECOccurrenceNode, BindingNode>>>>,
                    MapAsMinimalMap<BindingNode, SetAsMinimalSet<Edge<ECOccurrenceNode, BindingNode>>>> {
        public UnrestrictedGraph() {
            super(new MinimalMapWrapper<>(new IdentityHashMap<>()), new MinimalMapWrapper<>(new IdentityHashMap<>()),
                    new MinimalSetWrapper<>(Sets.newIdentityHashSet()),
                    () -> new MinimalSetWrapper<>(Sets.newIdentityHashSet()));
        }

        public boolean addEdge(final ECOccurrenceNode source, final BindingNode target) {
            final Set<Edge<ECOccurrenceNode, BindingNode>> outEdges = this.outgoingEdges
                    .computeIfAbsent(source, (x) -> new MinimalSetWrapper<>(Sets.newIdentityHashSet()));
            if (outEdges.stream().anyMatch(e -> e.getTarget() == target)) {
                return false;
            }
            final Edge<ECOccurrenceNode, BindingNode> edge = new Edge<>(source, target);
            outEdges.add(edge);
            this.incomingEdges.computeIfAbsent(target, (x) -> new MinimalSetWrapper<>(Sets.newIdentityHashSet()))
                    .add(edge);
            this.edgeSet.add(edge);
            return true;
        }

        public SubGraph newSubGraph(final Edge<ECOccurrenceNode, BindingNode> edge) {
            return new SubGraph(edge);
        }

        public class SubGraph extends
                Graph<IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>,
                        IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>,
                        IndexedImmutableSet<ECOccurrenceNode>, ImmutableMinimalSet<Map.Entry<ECOccurrenceNode,
                        IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>>>,
                        ImmutableMinimalMap<ECOccurrenceNode, IndexedImmutableSet<Edge<ECOccurrenceNode,
                                BindingNode>>, IndexedImmutableSet<ECOccurrenceNode>, ImmutableMinimalSet<Map
                                .Entry<ECOccurrenceNode, IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>>>>,
                        IndexedImmutableSet<BindingNode>, ImmutableMinimalSet<Map.Entry<BindingNode,
                        IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>>>, ImmutableMinimalMap<BindingNode,
                        IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>, IndexedImmutableSet<BindingNode>,
                        ImmutableMinimalSet<Map.Entry<BindingNode, IndexedImmutableSet<Edge<ECOccurrenceNode,
                                BindingNode>>>>>> {
            public SubGraph(
                    final ImmutableMinimalMap<ECOccurrenceNode, IndexedImmutableSet<Edge<ECOccurrenceNode,
                            BindingNode>>, IndexedImmutableSet<ECOccurrenceNode>, ImmutableMinimalSet<Map
                            .Entry<ECOccurrenceNode, IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>>>>
                            outgoingEdges,
                    final ImmutableMinimalMap<BindingNode, IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>,
                            IndexedImmutableSet<BindingNode>, ImmutableMinimalSet<Map.Entry<BindingNode,
                            IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>>>>> incomingEdges,
                    final IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>> edgeSet) {
                super(outgoingEdges, incomingEdges, edgeSet, IndexedIdentityHashSet::new);
            }

            public SubGraph(final Edge<ECOccurrenceNode, BindingNode> edge) {
                this(edge.getSource(), edge.getTarget(), new IndexedIdentityHashSet<>(ImmutableSet.of(edge)));
            }

            public SubGraph(final SubGraph other) {
                this(new IdentityHashMapWithIndexedIdentityKeySet<>(other.outgoingEdges),
                        new IdentityHashMapWithIndexedIdentityKeySet<>(other.incomingEdges),
                        new IndexedIdentityHashSet<>(other.edgeSet));
            }

            private SubGraph(final ECOccurrenceNode source, final BindingNode target,
                    final IndexedImmutableSet<Edge<ECOccurrenceNode, BindingNode>> singleton) {
                this(new IdentityHashMapWithIndexedIdentityKeySet<>(ImmutableMap.of(source, singleton)),
                        new IdentityHashMapWithIndexedIdentityKeySet<>(ImmutableMap.of(target, singleton)), singleton);
                assert 1 == singleton.size();
            }

            public SubGraph addEdge(final Edge<ECOccurrenceNode, BindingNode> edge) {
                assert UnrestrictedGraph.this.edgeSet.contains(edge);
                final boolean contained = this.edgeSet.contains(edge);
                if (contained) throw new IllegalArgumentException("Edge already contained!");
                return new SubGraph(IndexedIdentityMapToSetExtender.with(this.outgoingEdges, edge.getSource(), edge),
                        IndexedIdentityMapToSetExtender.with(this.incomingEdges, edge.getTarget(), edge),
                        IdentitySetExtender.with(this.edgeSet, edge));
            }

            public SubGraph removeEdge(final Edge<ECOccurrenceNode, BindingNode> edge) {
                assert UnrestrictedGraph.this.edgeSet.contains(edge);
                final boolean contained = this.edgeSet.contains(edge);
                if (!contained) throw new IllegalArgumentException("Edge not contained!");
                return new SubGraph(IndexedIdentityMapToSetReducer.without(this.outgoingEdges, edge.getSource(), edge),
                        IndexedIdentityMapToSetReducer.without(this.incomingEdges, edge.getTarget(), edge),
                        IdentitySetReducer.without(this.edgeSet, edge));
            }

            public IndexedImmutableSet<ECOccurrenceNode> occurrenceNodeSet() {
                return this.outgoingEdges.keySet();
            }

            public IndexedImmutableSet<BindingNode> bindingNodeSet() {
                return this.incomingEdges.keySet();
            }
        }
    }

    private void addFVs(final Iterable<SingleFactVariable> factVariables) {
        final ScopeStack scopeStack = new ScopeStack();
        for (final SingleFactVariable factVariable : factVariables) {
            final Template template = factVariable.getTemplate();
            final TemplateSlotLeaf fvTemplateSlotLeaf = new TemplateSlotLeaf(template, null);
            final EquivalenceClass fvEC = factVariable.getEqual();
            final FactBindingNode factBindingNode = new FactBindingNode(fvEC, fvTemplateSlotLeaf, factVariable);
            this.ecToElements.computeIfAbsent(fvEC, newArrayList()).add(factBindingNode);
            // add to template lookup maps
            this.templateToInstances.computeIfAbsent(template, newIdentityHashSet()).add(factVariable);
            this.templateInstanceToBindingNodes.computeIfAbsent(factVariable, newIdentityHashMap())
                    .put(null, factBindingNode);
            // add to binding lookup map
            this.directBindingNodes.computeIfAbsent(fvTemplateSlotLeaf, newIdentityHashSet()).add(factBindingNode);

            final ImplicitOccurrenceNode fvImplicitOccurrenceNode =
                    new ImplicitOccurrenceNode(new ECOccurrence(fvEC), factBindingNode);
            // add the binding node and the corresponding implicit occurrence node to the graph
            this.graph.addEdge(fvImplicitOccurrenceNode, factBindingNode);
            this.bindingNodeToImplicitOccurrence.put(factBindingNode, fvImplicitOccurrenceNode);

            final Collection<Template.Slot> slots = template.getSlots();
            final HashMap<SlotAddress, SingleFactVariable.SingleSlotVariable> slotVariableMap = factVariable.getSlots();
            for (final Template.Slot slot : slots) {
                final SlotAddress slotAddress = template.getSlotAddress(slot.getName());
                if (!slotVariableMap.containsKey(slotAddress)) {
                    scopeStack.createDummySlotVariable(factVariable, slotAddress, null,
                            ssv -> slotVariableMap.put(slotAddress, ssv));
                }
                final SingleFactVariable.SingleSlotVariable slotVariable = slotVariableMap.get(slotAddress);

                final TemplateSlotLeaf svTemplateSlotLeaf =
                        new TemplateSlotLeaf(factVariable.getTemplate(), slotVariable.getSlot());
                final EquivalenceClass svEC = slotVariable.getEqual();
                final SlotBindingNode slotBindingNode = new SlotBindingNode(svEC, svTemplateSlotLeaf, slotVariable);
                this.ecToElements.computeIfAbsent(svEC, newArrayList()).add(slotBindingNode);
                // add to template lookup map
                this.templateToInstances.computeIfAbsent(factVariable.getTemplate(), newIdentityHashSet())
                        .add(factVariable);
                this.templateInstanceToBindingNodes.computeIfAbsent(factVariable, newIdentityHashMap())
                        .put(slotAddress, slotBindingNode);
                // add to binding lookup map
                this.directBindingNodes.computeIfAbsent(svTemplateSlotLeaf, newIdentityHashSet()).add(slotBindingNode);

                final ImplicitOccurrenceNode svImplicitOccurrenceNode =
                        new ImplicitOccurrenceNode(new ECOccurrence(svEC), slotBindingNode);
                // add the binding node and the corresponding implicit occurrence node to the graph
                this.graph.addEdge(svImplicitOccurrenceNode, slotBindingNode);
                this.bindingNodeToImplicitOccurrence.put(slotBindingNode, svImplicitOccurrenceNode);
            }
        }
    }

    private void addECs(final Iterable<EquivalenceClass> ecs) {
        // FIXME for every FV contained in the result, add a FactBindingNode and for every slot of the template of
        // every FV add a SlotBindingNode (also stuff not contained in the rule)

        // for every equivalence class
        for (final EquivalenceClass ec : ecs) {
            // gather binding nodes of the EC
            final List<BindingNode> additionalBindingNodes = new ArrayList<>();

            final LinkedList<FunctionWithArguments<ECLeaf>> constantExpressions = ec.getConstantExpressions();
            for (final FunctionWithArguments<ECLeaf> constantExpression : constantExpressions) {
                final ConstantBindingNode constantBindingNode =
                        new ConstantBindingNode(ec, new ConstantLeaf<>(constantExpression));
                additionalBindingNodes.add(constantBindingNode);
                // add to binding lookup map
                this.directBindingNodes.computeIfAbsent(constantBindingNode.getConstant(), newIdentityHashSet())
                        .add(constantBindingNode);
            }
            final LinkedList<FunctionWithArguments<ECLeaf>> functionalExpressions = ec.getFunctionalExpressions();
            for (final FunctionWithArguments<ECLeaf> functionalExpression : functionalExpressions) {
                final FunctionWithArguments<ECOccurrenceLeaf> occurrenceBasedFunctionalExpression =
                        ECLeafToECOccurrenceLeafTranslator.translateUsingNewOccurrences(functionalExpression);
                final FunctionWithArguments<TypeLeaf> typeLeafBasedFunctionalExpression =
                        FWAECLeafToTypeLeafTranslator.translate(functionalExpression);
                final FunctionalExpressionBindingNode functionalExpressionBindingNode =
                        new FunctionalExpressionBindingNode(ec, typeLeafBasedFunctionalExpression,
                                occurrenceBasedFunctionalExpression);
                additionalBindingNodes.add(functionalExpressionBindingNode);
                // add to template lookup map and create occurrences
                this.functionalExpressionToBindings
                        .computeIfAbsent(typeLeafBasedFunctionalExpression, newIdentityHashSet())
                        .add(functionalExpressionBindingNode);
                final ArrayList<ECOccurrenceLeaf> occurrences =
                        ECOccurrenceLeafCollector.collect(occurrenceBasedFunctionalExpression);
                final TreeMap<Integer, FunctionalExpressionOccurrenceNode> arguments = new TreeMap<>();
                for (int i = 0; i < occurrences.size(); i++) {
                    arguments.put(i, new FunctionalExpressionOccurrenceNode(occurrences.get(i).getEcOccurrence(),
                            functionalExpressionBindingNode, i));
                }
                this.functionalExpressionBindingToOccurrenceNodes.put(functionalExpressionBindingNode, arguments);
            }

            // create implicit occurrence nodes for the bindings
            for (final BindingNode bindingNode : additionalBindingNodes) {
                final ImplicitOccurrenceNode implicitOccurrenceNode =
                        new ImplicitOccurrenceNode(new ECOccurrence(ec), bindingNode);
                // add the binding node and the corresponding implicit occurrence node to the graph
                this.graph.addEdge(implicitOccurrenceNode, bindingNode);
                this.bindingNodeToImplicitOccurrence.put(bindingNode, implicitOccurrenceNode);
            }


            final List<BindingNode> bindingNodes = this.ecToElements.computeIfAbsent(ec, newArrayList());
            bindingNodes.addAll(additionalBindingNodes);

            // create edges between all implicit occurrence and binding nodes of the EC
            for (final BindingNode target : bindingNodes) {
                for (final BindingNode bindingNode : bindingNodes) {
                    if (bindingNode == target) {
                        continue;
                    }
                    final ImplicitOccurrenceNode source = this.bindingNodeToImplicitOccurrence.get(bindingNode);
                    this.graph.addEdge(source, target);
                }
            }
        }

        // now that all direct bindings are created for all ECs, add the edges from the occurrences in functional
        // expressions to their bindings (and the occurrence nodes themselves) to the graph
        for (final TreeMap<Integer, FunctionalExpressionOccurrenceNode> occurrenceNodes : this
                .functionalExpressionBindingToOccurrenceNodes
                .values()) {
            for (final FunctionalExpressionOccurrenceNode functionalExpressionOccurrenceNode : occurrenceNodes
                    .values()) {
                final EquivalenceClass equivalenceClass = functionalExpressionOccurrenceNode.getOccurrence().getEc();
                final List<BindingNode> bindingNodes = this.ecToElements.get(equivalenceClass);
                for (final BindingNode bindingNode : bindingNodes) {
                    this.graph.addEdge(functionalExpressionOccurrenceNode, bindingNode);
                }
            }
        }
    }

    private void addFilter(final ECFilter filter, final ExistentialInfo existentialInfo) {
        final PredicateWithArguments<TypeLeaf> typeLeafBasedPredicate =
                FWAECLeafToTypeLeafTranslator.translate(filter.getFunction());
        // store the grouping filter in the lookup map
        final ExistentialInfo.FunctionWithExistentialInfo functionWithExistentialInfo =
                new ExistentialInfo.FunctionWithExistentialInfo(typeLeafBasedPredicate, existentialInfo);
        this.predicateToFilters.computeIfAbsent(functionWithExistentialInfo, newIdentityHashSet()).add(filter);
        final TreeMap<Integer, FilterOccurrenceNode> parameters = new TreeMap<>();
        final FunctionWithArguments<ECOccurrenceLeaf> occurrenceBasedPredicate =
                ECLeafToECOccurrenceLeafTranslator.translateUsingNewOccurrences(filter.getFunction());
        final ArrayList<ECOccurrenceLeaf> occurrences = ECOccurrenceLeafCollector.collect(occurrenceBasedPredicate);
        for (int i = 0; i < occurrences.size(); i++) {
            final ECOccurrence occurrence = occurrences.get(i).getEcOccurrence();
            final FilterOccurrenceNode filterOccurrenceNode =
                    new FilterOccurrenceNode(occurrence, functionWithExistentialInfo, filter, i);
            parameters.put(i, filterOccurrenceNode);
            final List<BindingNode> bindingNodes = this.ecToElements.get(occurrence.getEc());
            for (final BindingNode bindingNode : bindingNodes) {
                this.graph.addEdge(filterOccurrenceNode, bindingNode);
            }
        }
        this.filterToOccurrenceNodes.put(filter, parameters);
    }

    class ECFilterSetAdder implements ECFilterSetVisitor {
        public void add(final Iterable<ECFilterSet> ecFilterSets) {
            for (final ECFilterSet ecFilterSet : ecFilterSets) {
                ecFilterSet.accept(this);
            }
        }

        @Override
        public void visit(final ECFilterSet.ECExistentialSet set) {
            addFVs(set.getExistentialFactVariables());
            addECs(set.getEquivalenceClasses());
            add(set.getPurePart());
            addFilter(set.getExistentialClosure(), ExistentialInfo.get(set));
        }

        @Override
        public void visit(final ECFilter filter) {
            addFilter(filter, ExistentialInfo.REGULAR);
        }
    }

    public void addRule(final ConstructCache.Defrule.ECSetRule rule) {
        addFVs(rule.getShallowFactVariables());
        final Set<ECFilterSet> filters = rule.getCondition();
        addECs(rule.getEquivalenceClasses());
        new ECFilterSetAdder().add(filters);
    }
}
