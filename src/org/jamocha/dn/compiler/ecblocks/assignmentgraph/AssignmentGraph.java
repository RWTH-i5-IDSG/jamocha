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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

import java.util.*;
import java.util.function.BiFunction;

import static org.jamocha.util.Lambdas.newIdentityHashSet;

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


	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class Edge {
		final ECOccurrenceNode source;
		final BindingNode target;
	}

	public class UnrestrictedGraph {
		final IdentityHashMap<ECOccurrenceNode, Set<Edge>> outgoingEdges = new IdentityHashMap<>();
		final IdentityHashMap<BindingNode, Set<Edge>> incomingEdges = new IdentityHashMap<>();
		final Set<Edge> edgeSet = Sets.newIdentityHashSet();

		public boolean addEdge(final ECOccurrenceNode source, final BindingNode target) {
			final Set<Edge> outEdges = this.outgoingEdges.computeIfAbsent(source, newIdentityHashSet());
			if (outEdges.stream().anyMatch(e -> e.getTarget() == target)) {
				return false;
			}
			final Edge edge = new Edge(source, target);
			outEdges.add(edge);
			this.incomingEdges.computeIfAbsent(target, newIdentityHashSet()).add(edge);
			this.edgeSet.add(edge);
			return true;
		}

		public boolean containsEdge(final Edge edge) {
			return this.edgeSet.contains(edge);
		}

		public boolean containsEdge(final ECOccurrenceNode source, final BindingNode target) {
			return null != getEdge(source, target);
		}

		public Edge getEdge(final ECOccurrenceNode source, final BindingNode target) {
			return this.outgoingEdges.get(source).stream().filter(e -> e.getTarget() == target).findAny().orElse(null);
		}


		private Set<Edge> getIncomingEdges(final BindingNode target) {
			final Set<Edge> edges = this.incomingEdges.get(target);
			return null != edges ? edges : ImmutableSet.of();
		}

		public Set<Edge> incomingEdgesOf(final BindingNode target) {
			return ImmutableSet.copyOf(getIncomingEdges(target));
		}

		public int inDegreeOf(final BindingNode target) {
			return getIncomingEdges(target).size();
		}

		private Set<Edge> getOutgoingEdges(final ECOccurrenceNode source) {
			final Set<Edge> edges = this.outgoingEdges.get(source);
			return null != edges ? edges : ImmutableSet.of();
		}

		public Set<Edge> outgoingEdgesOf(final ECOccurrenceNode source) {
			return ImmutableSet.copyOf(getOutgoingEdges(source));
		}

		public int outDegreeOf(final ECOccurrenceNode source) {
			return getOutgoingEdges(source).size();
		}

		public SubGraph newSubGraph() {
			return new SubGraph();
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class SubGraph {
			final IdentityHashMap<ECOccurrenceNode, Set<Edge>> outgoingEdges = new IdentityHashMap<>();
			final IdentityHashMap<BindingNode, Set<Edge>> incomingEdges = new IdentityHashMap<>();
			final Set<Edge> edgeSet = Sets.newIdentityHashSet();

			public SubGraph(final SubGraph other) {
				this.outgoingEdges.putAll(other.outgoingEdges);
				this.incomingEdges.putAll(other.incomingEdges);
				this.edgeSet.addAll(other.edgeSet);
			}

			public boolean addEdge(final Edge edge) {
				assert UnrestrictedGraph.this.edgeSet.contains(edge);
				final boolean added = this.edgeSet.add(edge);
				if (!added) return false;
				this.outgoingEdges.computeIfAbsent(edge.getSource(), newIdentityHashSet()).add(edge);
				this.incomingEdges.computeIfAbsent(edge.getTarget(), newIdentityHashSet()).add(edge);
				return true;
			}

			public boolean containsEdge(final Edge edge) {
				return this.edgeSet.contains(edge);
			}

			public boolean containsEdge(final ECOccurrenceNode source, final BindingNode target) {
				return null != getEdge(source, target);
			}

			public Edge getEdge(final ECOccurrenceNode source, final BindingNode target) {
				return this.outgoingEdges.get(source).stream().filter(e -> e.getTarget() == target).findAny()
						.orElse(null);
			}

			public Set<Edge> edgeSet() {
				return ImmutableSet.copyOf(this.edgeSet);
			}

			public boolean removeEdge(final Edge edge) {
				assert UnrestrictedGraph.this.edgeSet.contains(edge);
				final boolean removed = this.edgeSet.remove(edge);
				if (!removed) return false;
				final BiFunction<AssignmentGraphNode, Set<Edge>, Set<Edge>> edgeRemover = (k, set) -> {
					set.remove(edge);
					return set.isEmpty() ? null : set;
				};
				this.outgoingEdges.compute(edge.getSource(), edgeRemover);
				this.incomingEdges.compute(edge.getTarget(), edgeRemover);
				return true;
			}

			private Set<Edge> getIncomingEdges(final BindingNode target) {
				final Set<Edge> edges = this.incomingEdges.get(target);
				return null != edges ? edges : ImmutableSet.of();
			}

			public Set<Edge> incomingEdgesOf(final BindingNode target) {
				return ImmutableSet.copyOf(getIncomingEdges(target));
			}

			public int inDegreeOf(final BindingNode target) {
				return getIncomingEdges(target).size();
			}

			private Set<Edge> getOutgoingEdges(final ECOccurrenceNode source) {
				final Set<Edge> edges = this.outgoingEdges.get(source);
				return null != edges ? edges : ImmutableSet.of();
			}

			public Set<Edge> outgoingEdgesOf(final ECOccurrenceNode source) {
				return ImmutableSet.copyOf(getOutgoingEdges(source));
			}

			public int outDegreeOf(final ECOccurrenceNode source) {
				return getOutgoingEdges(source).size();
			}

			public Set<ECOccurrenceNode> occurrenceNodeSet() {
				return ImmutableSet.copyOf(this.outgoingEdges.keySet());
			}

			public Set<BindingNode> bindingNodeSet() {
				return ImmutableSet.copyOf(this.incomingEdges.keySet());
			}
		}
	}


	public void addECs(final Iterable<EquivalenceClass> ecs) {
		// for every equivalence class
		for (final EquivalenceClass ec : ecs) {
			// gather binding nodes of the EC
			final List<BindingNode> bindingNodes = new ArrayList<>();
			// add to lookup map
			this.ecToElements.put(ec, bindingNodes);
			// create fact binding nodes
			final LinkedList<SingleFactVariable> factVariables = ec.getFactVariables();
			for (final SingleFactVariable factVariable : factVariables) {
				final TemplateSlotLeaf templateSlotLeaf = new TemplateSlotLeaf(factVariable.getTemplate(), null);
				final FactBindingNode factBindingNode = new FactBindingNode(ec, templateSlotLeaf, factVariable);
				bindingNodes.add(factBindingNode);
				// add to template lookup maps
				this.templateToInstances.computeIfAbsent(factVariable.getTemplate(), newIdentityHashSet())
						.add(factVariable);
				this.templateInstanceToBindingNodes.computeIfAbsent(factVariable, newIdentityHashSet())
						.add(factBindingNode);
				// add to binding lookup map
				this.directBindingNodes.computeIfAbsent(templateSlotLeaf, newIdentityHashSet()).add(factBindingNode);
			}
			final LinkedList<SingleFactVariable.SingleSlotVariable> slotVariables = ec.getSlotVariables();
			for (final SingleFactVariable.SingleSlotVariable slotVariable : slotVariables) {
				final SingleFactVariable factVariable = slotVariable.getFactVariable();
				final TemplateSlotLeaf templateSlotLeaf =
						new TemplateSlotLeaf(factVariable.getTemplate(), slotVariable.getSlot());
				final SlotBindingNode slotBindingNode = new SlotBindingNode(ec, templateSlotLeaf, slotVariable);
				bindingNodes.add(slotBindingNode);
				// add to template lookup map
				this.templateToInstances.computeIfAbsent(factVariable.getTemplate(), newIdentityHashSet())
						.add(factVariable);
				this.templateInstanceToBindingNodes.computeIfAbsent(factVariable, newIdentityHashSet())
						.add(slotBindingNode);
				// add to binding lookup map
				this.directBindingNodes.computeIfAbsent(templateSlotLeaf, newIdentityHashSet()).add(slotBindingNode);
			}
			final LinkedList<FunctionWithArguments<ECLeaf>> constantExpressions = ec.getConstantExpressions();
			for (final FunctionWithArguments<ECLeaf> constantExpression : constantExpressions) {
				final ConstantBindingNode constantBindingNode =
						new ConstantBindingNode(ec, new ConstantLeaf<>(constantExpression));
				bindingNodes.add(constantBindingNode);
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
				bindingNodes.add(functionalExpressionBindingNode);
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
			final List<ImplicitOccurrenceNode> implicitOccurrenceNodes = new ArrayList<>(bindingNodes.size());
			for (final BindingNode bindingNode : bindingNodes) {
				final ImplicitOccurrenceNode implicitOccurrenceNode =
						new ImplicitOccurrenceNode(new ECOccurrence(ec), bindingNode);
				implicitOccurrenceNodes.add(implicitOccurrenceNode);
				// add the binding node and the corresponding implicit occurrence node to the graph
				this.graph.addEdge(implicitOccurrenceNode, bindingNode);
				this.bindingNodeToImplicitOccurrence.put(bindingNode, implicitOccurrenceNode);
			}
			// create edges between all implicit occurrence and binding nodes of the EC
			for (final ImplicitOccurrenceNode implicitOccurrenceNode : implicitOccurrenceNodes) {
				for (final BindingNode bindingNode : bindingNodes) {
					this.graph.addEdge(implicitOccurrenceNode, bindingNode);
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

	// FIXME to be implemented: fact variables occurring within existential parts that do not occur in any actual
	// filters have to be used in dummy filters to make sure they are represented correctly in the graph

	public void addFilter(final ECFilter filter, final ExistentialInfo existentialInfo) {
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
}
