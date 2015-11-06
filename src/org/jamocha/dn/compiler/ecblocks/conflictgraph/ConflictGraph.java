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
package org.jamocha.dn.compiler.ecblocks.conflictgraph;

import static java.util.stream.Collectors.toList;
import static org.jamocha.util.Lambdas.newHashSet;
import static org.jamocha.util.Lambdas.newIdentityHashMap;
import static org.jamocha.util.Lambdas.newTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import lombok.Getter;

import org.jamocha.dn.compiler.ecblocks.ECLeafToECOccurrenceLeafTranslator;
import org.jamocha.dn.compiler.ecblocks.ECOccurrence;
import org.jamocha.dn.compiler.ecblocks.ECOccurrenceLeaf;
import org.jamocha.dn.compiler.ecblocks.ECOccurrenceLeafCollector;
import org.jamocha.dn.compiler.ecblocks.ExistentialProxy;
import org.jamocha.dn.compiler.ecblocks.Rule;
import org.jamocha.dn.compiler.ecblocks.element.ConstantExpression;
import org.jamocha.dn.compiler.ecblocks.element.FactBinding;
import org.jamocha.dn.compiler.ecblocks.element.SlotBinding;
import org.jamocha.dn.compiler.ecblocks.element.VariableExpression;
import org.jamocha.filter.ECFilter;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.function.fwa.TypeLeaf;
import org.jamocha.function.fwatransformer.FWAECLeafToTypeLeafTranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleGraph;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class ConflictGraph {
	public static class OccurrenceToBindingEdge {
	};

	static EdgeFactory<ConflictGraphNode, OccurrenceToBindingEdge> assertingEdgeFactory = (
			final ConflictGraphNode source, final ConflictGraphNode target) -> {
		if (!(source instanceof ECOccurrenceNode && target instanceof BindingNode)) {
			throw new IllegalArgumentException("Edges always go from occurrence to binding!");
		}
		return new OccurrenceToBindingEdge();
	};

	final SimpleGraph<ConflictGraphNode, OccurrenceToBindingEdge> graph = new SimpleGraph<>(assertingEdgeFactory);
	final IdentityHashMap<EquivalenceClass, List<BindingNode>> ecToElements = new IdentityHashMap<>();

	final HashMap<FunctionWithArguments<TypeLeaf>, IdentityHashMap<Either<Rule, ExistentialProxy>, Set<ECFilter>>> typedFilterToInstances =
			new HashMap<>();
	final IdentityHashMap<ECFilter, TreeMap<Integer, ExplicitFINode>> filterNodeGroups = new IdentityHashMap<>();

	final HashMap<FunctionWithArguments<TypeLeaf>, IdentityHashMap<Either<Rule, ExistentialProxy>, Set<IndirectBindingNode>>> typedVEToInstances =
			new HashMap<>();
	final IdentityHashMap<IndirectBindingNode, TreeMap<Integer, VariableExpressionNode>> variableExpressionNodeGroups =
			new IdentityHashMap<>();

	final HashMap<FunctionWithArguments<TemplateSlotLeaf>, IdentityHashMap<Either<Rule, ExistentialProxy>, Set<DirectBindingNode>>> directBindingNodes =
			new HashMap<>();

	public void addECs(final Either<Rule, ExistentialProxy> ruleOrProxy, final Iterable<EquivalenceClass> ecs) {
		final IdentityHashMap<EquivalenceClass, List<IndirectBindingNode>> ecToVarExprs = new IdentityHashMap<>();
		// for every equivalence class
		for (final EquivalenceClass ec : ecs) {
			// gather direct bindings and constants
			final List<BindingNode> elements = new ArrayList<>();
			Stream.concat(
					Stream.concat(ec.getFactVariables().stream().map(FactBinding::new).map(DirectBindingNode::new), ec
							.getSlotVariables().stream().map(SlotBinding::new).map(DirectBindingNode::new)),
					ec.getConstantExpressions().stream().map(c -> new ConstantExpression(c, ec))
							.map(DirectBindingNode::new))
					.forEach(node -> {
						// already add the node to the graph
							graph.addVertex(node);
							// store the node in the lookup map
							final FunctionWithArguments<TemplateSlotLeaf> templateSlotLeaf =
									node.binding.getTemplateSlotLeaf();
							directBindingNodes.computeIfAbsent(templateSlotLeaf, newIdentityHashMap())
									.computeIfAbsent(ruleOrProxy, newHashSet()).add(node);
							// add it to the elements
							elements.add(node);
						});
			// for every element, create an implicit node and connect it to all EC element nodes
			for (@SuppressWarnings("unused")
			final BindingNode x : elements) {
				final ImplicitFINode implicitFINode = new ImplicitFINode(new ECOccurrence(ec));
				graph.addVertex(implicitFINode);
				for (final BindingNode bindingNode : elements) {
					graph.addEdge(implicitFINode, bindingNode);
				}
			}
			// for every variable expression, create an indirect binding node and store it
			// separately in addition to adding it to the graph and the list of elements
			final List<IndirectBindingNode> varExprNodes =
					ec.getVariableExpressions()
							.stream()
							.map(varExpr -> {
								final FunctionWithArguments<ECOccurrenceLeaf> varExprECLeaf =
										ECLeafToECOccurrenceLeafTranslator.translateUsingNewOccurrences(varExpr);
								final VariableExpression variableExpression = new VariableExpression(varExprECLeaf, ec);
								final IndirectBindingNode indirectBindingNode =
										new IndirectBindingNode(variableExpression);
								// add the indirect binding node to the graph
								graph.addVertex(indirectBindingNode);
								final FunctionWithArguments<TypeLeaf> varExprTypeLeaf =
										FWAECLeafToTypeLeafTranslator.translate(varExpr);
								// store it in the lookup map
								typedVEToInstances.computeIfAbsent(varExprTypeLeaf, newIdentityHashMap())
										.computeIfAbsent(ruleOrProxy, newHashSet()).add(indirectBindingNode);
								return indirectBindingNode;
							}).collect(toList());
			// save the element and variable expressions lists
			ecToElements.put(ec, elements);
			if (!varExprNodes.isEmpty()) {
				ecToVarExprs.put(ec, varExprNodes);
			}
		}
		// for every variable expression, we now additionally have to create a new variable
		// expression node for every EC used within in and create the corresponding edges
		// note: this can't be done beforehand, since the possible bindings have to be determined
		// first. here, only direct bindings are connected => the variable expression nodes are
		// added to the elements map afterwards
		for (final List<IndirectBindingNode> varExprNodes : ecToVarExprs.values()) {
			for (final IndirectBindingNode varExprNode : varExprNodes) {
				// for every indirect binding node, create a group of variable expression nodes
				createEdges(varExprNode.getCorrespondingVE().getVariableExpression(), (index, occ) -> {
					final VariableExpressionNode node = new VariableExpressionNode(occ, varExprNode);
					// store the group node at the correct position in the lookup map
						variableExpressionNodeGroups.computeIfAbsent(varExprNode, newTreeMap()).put(index, node);
						return node;
					});
			}
		}
		// add the indirect bindings to the elements map so they will be connected to the explicit
		// filter instances, but were not used for the occurrences within variable expressions
		for (final Entry<EquivalenceClass, List<IndirectBindingNode>> entry : ecToVarExprs.entrySet()) {
			ecToElements.get(entry.getKey()).addAll(entry.getValue());
		}
	}

	private void createEdges(final FunctionWithArguments<ECOccurrenceLeaf> fwa,
			final BiFunction<Integer, ECOccurrence, ECOccurrenceNode> ctor) {
		// collect the EC occurrences (i.e. the leaves)
		final ArrayList<ECOccurrenceLeaf> occurrenceLeaves = ECOccurrenceLeafCollector.collect(fwa);
		for (int i = 0; i < occurrenceLeaves.size(); i++) {
			final ECOccurrenceLeaf occurrenceLeaf = occurrenceLeaves.get(i);
			final ECOccurrence occurrence = occurrenceLeaf.getEcOccurrence();
			// create a corresponding node
			final ECOccurrenceNode node = ctor.apply(i, occurrence);
			// add it to the graph
			graph.addVertex(node);
			// and add the edges to the corresponding bindings
			for (final BindingNode bindingNode : ecToElements.get(occurrence.getEc())) {
				graph.addEdge(node, bindingNode);
			}
		}
	}

	public void addFilter(final Either<Rule, ExistentialProxy> ruleOrProxy, final ECFilter filter) {
		final PredicateWithArguments<TypeLeaf> translated =
				FWAECLeafToTypeLeafTranslator.translate(filter.getFunction());
		// store the grouping filter in the lookup map
		typedFilterToInstances.computeIfAbsent(translated, newIdentityHashMap())
				.computeIfAbsent(ruleOrProxy, newHashSet()).add(filter);
		createEdges(ECLeafToECOccurrenceLeafTranslator.translateUsingNewOccurrences(filter.getFunction()),
				(index, occ) -> {
					final ExplicitFINode node = new ExplicitFINode(occ, filter);
					// store the group node at the correct position in the lookup map
				filterNodeGroups.computeIfAbsent(filter, newTreeMap()).put(index, node);
				return node;
			});
	}

	public ECOccurrenceNode getOccurrence(final OccurrenceToBindingEdge edge) {
		return (ECOccurrenceNode) graph.getEdgeSource(edge);
	}

	public BindingNode getBinding(final OccurrenceToBindingEdge edge) {
		return (BindingNode) graph.getEdgeTarget(edge);
	}
}
