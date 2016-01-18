/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.dn.compiler.ecblocks.assignmentgraph;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jamocha.dn.compiler.ecblocks.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ECOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FilterOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.FunctionalExpressionOccurrenceNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.ImplicitOccurrenceNode;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWAECLeafToTypeLeafTranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.*;

import static org.jamocha.util.Lambdas.newIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class AssignmentGraph {
	public static class OccurrenceToBindingEdge {
		public ECOccurrenceNode getOccurrence(final AssignmentGraph graph) {
			return getOccurrence(graph.graph);
		}

		public ECOccurrenceNode getOccurrence(final DirectedGraph<AssignmentGraphNode, OccurrenceToBindingEdge>
				graph) {
			return (ECOccurrenceNode) graph.getEdgeSource(this);
		}

		public BindingNode getBinding(final AssignmentGraph graph) {
			return getBinding(graph.graph);
		}

		public BindingNode getBinding(final DirectedGraph<AssignmentGraphNode, OccurrenceToBindingEdge> graph) {
			return (BindingNode) graph.getEdgeTarget(this);
		}
	}

	private static EdgeFactory<AssignmentGraphNode, OccurrenceToBindingEdge> assertingEdgeFactory =
			(final AssignmentGraphNode source, final AssignmentGraphNode target) -> {
				if (!(source instanceof ECOccurrenceNode && target instanceof BindingNode)) {
					throw new IllegalArgumentException("Edges always go from occurrence to binding!");
				}
				return new OccurrenceToBindingEdge();
			};

	private void addEdge(final ECOccurrenceNode source, final BindingNode target) {
		graph.addEdge(source, target);
	}

	// the actual graph
	final SimpleDirectedGraph<AssignmentGraphNode, OccurrenceToBindingEdge> graph =
			new SimpleDirectedGraph<>(assertingEdgeFactory);
	// cached bindings for the corresponding equivalence classes
	final IdentityHashMap<EquivalenceClass, List<BindingNode>> ecToElements = new IdentityHashMap<>();

	// needed for block consistency checks:
	// lookup map from filter/functional expression to corresponding occurrence nodes (existential stuff marked)
	// lookup map from fact variable (template instance) to corresponding fact/slot binding nodes

	// explicit filter instance node groups:
	// lookup map from abstract typed FWA to rule to the set of matching filters
	final HashMap<ExistentialInfo.FunctionWithExistentialInfo, Set<ECFilter>> predicateToFilters = new HashMap<>();
	final IdentityHashMap<ECFilter, TreeMap<Integer, FilterOccurrenceNode>> filterToOccurrenceNodes =
			new IdentityHashMap<>();

	// lookup from template to template instances to the corresponding binding nodes
	final IdentityHashMap<Template, Set<SingleFactVariable>> templateToInstances = new IdentityHashMap<>();
	final IdentityHashMap<SingleFactVariable, Set<SlotOrFactBindingNode>> templateInstanceToBindingNodes =
			new IdentityHashMap<>();

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


	public void addECs(final Rule rule, final Iterable<EquivalenceClass> ecs) {
		final IdentityHashMap<EquivalenceClass, List<FunctionalExpressionBindingNode>> ecToFunctionalExprs =
				new IdentityHashMap<>();
		// for every equivalence class
		for (final EquivalenceClass ec : ecs) {
			// gather binding nodes of the EC
			final List<BindingNode> bindingNodes = new ArrayList<>();
			// add to lookup map
			ecToElements.put(ec, bindingNodes);
			// create fact binding nodes
			final LinkedList<SingleFactVariable> factVariables = ec.getFactVariables();
			for (final SingleFactVariable factVariable : factVariables) {
				final FactBindingNode factBindingNode = new FactBindingNode(ec, factVariable);
				bindingNodes.add(factBindingNode);
				// add to template lookup maps
				templateToInstances.computeIfAbsent(factVariable.getTemplate(), newIdentityHashSet()).add
						(factVariable);
				templateInstanceToBindingNodes.computeIfAbsent(factVariable, newIdentityHashSet()).add
						(factBindingNode);
				// add to binding lookup map
				directBindingNodes
						.computeIfAbsent(new TemplateSlotLeaf(factVariable.getTemplate(), null), newIdentityHashSet())
						.add(factBindingNode);
			}
			final LinkedList<SingleFactVariable.SingleSlotVariable> slotVariables = ec.getSlotVariables();
			for (final SingleFactVariable.SingleSlotVariable slotVariable : slotVariables) {
				final SlotBindingNode slotBindingNode = new SlotBindingNode(ec, slotVariable);
				bindingNodes.add(slotBindingNode);
				// add to template lookup map
				final SingleFactVariable factVariable = slotVariable.getFactVariable();
				templateToInstances.computeIfAbsent(factVariable.getTemplate(), newIdentityHashSet()).add
						(factVariable);
				templateInstanceToBindingNodes.computeIfAbsent(factVariable, newIdentityHashSet()).add
						(slotBindingNode);
				// add to binding lookup map
				directBindingNodes
						.computeIfAbsent(new TemplateSlotLeaf(factVariable.getTemplate(), slotVariable.getSlot()),
								newIdentityHashSet()).add(slotBindingNode);
			}
			final LinkedList<FunctionWithArguments<ECLeaf>> constantExpressions = ec.getConstantExpressions();
			for (final FunctionWithArguments<ECLeaf> constantExpression : constantExpressions) {
				final ConstantBindingNode constantBindingNode =
						new ConstantBindingNode(ec, new ConstantLeaf<TemplateSlotLeaf>(constantExpression));
				bindingNodes.add(constantBindingNode);
				// add to binding lookup map
				directBindingNodes.computeIfAbsent(constantBindingNode.getConstant(), newIdentityHashSet())
						.add(constantBindingNode);
			}
			final LinkedList<FunctionWithArguments<ECLeaf>> functionalExpressions = ec.getFunctionalExpressions();
			for (final FunctionWithArguments<ECLeaf> functionalExpression : functionalExpressions) {
				final FunctionWithArguments<ECOccurrenceLeaf> occurrenceBasedFunctionalExpression =
						ECLeafToECOccurrenceLeafTranslator.translateUsingNewOccurrences(functionalExpression);
				final FunctionalExpressionBindingNode functionalExpressionBindingNode =
						new FunctionalExpressionBindingNode(ec, occurrenceBasedFunctionalExpression);
				bindingNodes.add(functionalExpressionBindingNode);
				// add to template lookup map and create occurrences
				functionalExpressionToBindings
						.computeIfAbsent(FWAECLeafToTypeLeafTranslator.translate(functionalExpression),
								newIdentityHashSet()).add(functionalExpressionBindingNode);
				final ArrayList<ECOccurrenceLeaf> occurrences =
						ECOccurrenceLeafCollector.collect(occurrenceBasedFunctionalExpression);
				final TreeMap<Integer, FunctionalExpressionOccurrenceNode> arguments = new TreeMap<>();
				for (int i = 0; i < occurrences.size(); i++) {
					arguments.put(i, new FunctionalExpressionOccurrenceNode(occurrences.get(i).getEcOccurrence(),
							functionalExpressionBindingNode));
				}
				functionalExpressionBindingToOccurrenceNodes.put(functionalExpressionBindingNode, arguments);
			}

			// create implicit occurrence nodes for the bindings
			final List<ImplicitOccurrenceNode> implicitOccurrenceNodes = new ArrayList<>(bindingNodes.size());
			for (final BindingNode bindingNode : bindingNodes) {
				final ImplicitOccurrenceNode implicitOccurrenceNode =
						new ImplicitOccurrenceNode(new ECOccurrence(ec), bindingNode);
				implicitOccurrenceNodes.add(implicitOccurrenceNode);
				// add the binding node and the corresponding implicit occurrence node to the graph
				graph.addVertex(bindingNode);
				graph.addVertex(implicitOccurrenceNode);
				bindingNodeToImplicitOccurrence.put(bindingNode, implicitOccurrenceNode);
			}
			// create edges between all implicit occurrence and binding nodes of the EC
			for (final ImplicitOccurrenceNode implicitOccurrenceNode : implicitOccurrenceNodes) {
				for (final BindingNode bindingNode : bindingNodes) {
					addEdge(implicitOccurrenceNode, bindingNode);
				}
			}
		}

		// now that all direct bindings are created for all ECs, add the edges from the occurrences in functional
		// expressions to their bindings (and the occurrence nodes themselves) to the graph
		for (final TreeMap<Integer, FunctionalExpressionOccurrenceNode> occurrenceNodes :
				functionalExpressionBindingToOccurrenceNodes
				.values()) {
			for (final FunctionalExpressionOccurrenceNode functionalExpressionOccurrenceNode : occurrenceNodes
					.values()) {
				final EquivalenceClass equivalenceClass = functionalExpressionOccurrenceNode.getOccurrence().getEc();
				final List<BindingNode> bindingNodes = ecToElements.get(equivalenceClass);
				graph.addVertex(functionalExpressionOccurrenceNode);
				for (final BindingNode bindingNode : bindingNodes) {
					addEdge(functionalExpressionOccurrenceNode, bindingNode);
				}
			}
		}
	}

	// FIXME to be implemented: fact variables occurring within existential parts that do not occur in any actual
	// filters have to be used in dummy filters to make sure they are represented correctly in the graph

	public void addFilter(final Rule rule, final ECFilter filter, final ExistentialInfo existentialInfo) {
		final PredicateWithArguments<TypeLeaf> typeLeafBasedPredicate =
				FWAECLeafToTypeLeafTranslator.translate(filter.getFunction());
		// store the grouping filter in the lookup map
		final ExistentialInfo.FunctionWithExistentialInfo functionWithExistentialInfo =
				new ExistentialInfo.FunctionWithExistentialInfo(typeLeafBasedPredicate, existentialInfo);
		predicateToFilters.computeIfAbsent(functionWithExistentialInfo, newIdentityHashSet()).add(filter);
		final TreeMap<Integer, FilterOccurrenceNode> parameters = new TreeMap<>();
		final FunctionWithArguments<ECOccurrenceLeaf> occurrenceBasedPredicate =
				ECLeafToECOccurrenceLeafTranslator.translateUsingNewOccurrences(filter.getFunction());
		final ArrayList<ECOccurrenceLeaf> occurrences = ECOccurrenceLeafCollector.collect(occurrenceBasedPredicate);
		for (int i = 0; i < occurrences.size(); i++) {
			final ECOccurrence occurrence = occurrences.get(i).getEcOccurrence();
			final FilterOccurrenceNode filterOccurrenceNode =
					new FilterOccurrenceNode(occurrence, functionWithExistentialInfo, filter);
			parameters.put(i, filterOccurrenceNode);
			graph.addVertex(filterOccurrenceNode);
			final List<BindingNode> bindingNodes = ecToElements.get(occurrence.getEc());
			for (final BindingNode bindingNode : bindingNodes) {
				addEdge(filterOccurrenceNode, bindingNode);
			}
		}
		filterToOccurrenceNodes.put(filter, parameters);
	}
}
