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
package org.jamocha.dn.compiler.ecblocks;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;

import org.jamocha.dn.compiler.ecblocks.ECBlocks.ConflictEdge;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterInstanceTypePartitioner;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Theta;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.VariableExpression;
import org.jamocha.dn.compiler.ecblocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitECFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitElementFilterInstance;
import org.jamocha.dn.compiler.ecblocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.Partition.SubSet;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import com.atlassian.fugue.Either;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class Block {
	// conflict graph
	UndirectedGraph<Filter.FilterInstance, ConflictEdge> graph;
	int graphModCount = 0;
	int blockModCount = 0;
	// rules of the block
	final Set<Either<Rule, ExistentialProxy>> rulesOrProxies;
	// abstract filters of the block
	final Set<Filter> filters = new HashSet<>();
	// contains the filterInstances without the correct arrangement, just to avoid having to
	// flat map the filterInstances every time
	final Set<Filter.FilterInstance> flatFilterInstances = new HashSet<>();

	// theta : map the arguments of the filter instances used instead of modifying them
	// in-place to be able to have the same instance within different blocks
	final Theta theta;
	final Theta variableExpressionTheta;
	final ConflictEdge.ConflictEdgeFactory edgeFactory;
	final FilterInstancePartition filterInstancePartition;
	final FactVariablePartition factVariablePartition;
	final ElementPartition elementPartition;

	public Block(final Set<Either<Rule, ExistentialProxy>> rules, final FactVariablePartition factVariablePartition) {
		this.theta = new Theta();
		this.variableExpressionTheta = new Theta();
		this.edgeFactory = ConflictEdge.newFactory(this.theta);
		this.graph = new SimpleGraph<>(this.edgeFactory);
		this.rulesOrProxies = Sets.newHashSet(rules);
		this.factVariablePartition = factVariablePartition;
		this.filterInstancePartition = new FilterInstancePartition();
		this.elementPartition = new ElementPartition();
	}

	public Block(final Block block) {
		this.theta = new Theta(block.theta);
		this.variableExpressionTheta = new Theta(block.variableExpressionTheta);
		this.edgeFactory = ConflictEdge.newFactory(this.theta);
		this.graph = block.graph;
		this.blockModCount = block.blockModCount;
		this.graphModCount = block.graphModCount;
		this.rulesOrProxies = new HashSet<>(block.rulesOrProxies);
		this.filters.addAll(block.filters);
		this.flatFilterInstances.addAll(block.flatFilterInstances);
		this.filterInstancePartition = new FilterInstancePartition(block.filterInstancePartition);
		this.factVariablePartition = new FactVariablePartition(block.factVariablePartition);
		this.elementPartition = new ElementPartition(block.elementPartition);
	}

	@Override
	public String toString() {
		return "Block(" + this.getNumberOfColumns() + "x" + this.getNumberOfRows() + "): "
				+ Objects.toString(this.filterInstancePartition);
	}

	public int getNumberOfRows() {
		return this.rulesOrProxies.size();
	}

	public int getNumberOfColumns() {
		return this.filterInstancePartition.subSets.size();
	}

	public void addElementSubSet(final Partition.SubSet<ECBlocks.Element> newSubSet) {
		assert this.rulesOrProxies.stream().allMatch(newSubSet.elements.keySet()::contains);
		for (final Element element : newSubSet.elements.values()) {
			this.theta.add(element);
		}
		this.elementPartition.add(newSubSet);
		++this.blockModCount;
	}

	public void addVariableExpressionSubSet(final Partition.SubSet<ECBlocks.Element> newSubSet) {
		assert this.rulesOrProxies.stream().allMatch(newSubSet.elements.keySet()::contains);
		for (final Element element : newSubSet.elements.values()) {
			this.variableExpressionTheta.add(element);
		}
		this.elementPartition.add(newSubSet);
		++this.blockModCount;
	}

	public void addFilterInstanceSubSet(final FilterInstancePartition.FilterInstanceSubSet newSubSet) {
		assert this.rulesOrProxies.stream().allMatch(newSubSet.elements.keySet()::contains);
		this.filterInstancePartition.add(newSubSet);
		this.filters.add(newSubSet.getFilter());
		final Collection<Filter.FilterInstance> filterInstances = newSubSet.elements.values();
		this.flatFilterInstances.addAll(filterInstances);
		++this.blockModCount;
	}

	public Set<Filter.FilterInstance> getConflictNeighbours() {
		if (this.blockModCount != this.graphModCount) {
			final Set<List<Filter.FilterInstance>> filterInstancesGroupedByRule =
					this.rulesOrProxies
							.stream()
							.<Filter.FilterInstance> flatMap(
									rule -> Util.getFilters(rule).stream()
											.flatMap(f -> f.getAllInstances(rule).stream()))
							.collect(ECBlocks.groupingIntoSets(FilterInstance::getRuleOrProxy, toList()));
			this.graph = ECBlocks.determineConflictGraph(this.theta, filterInstancesGroupedByRule);
			this.graphModCount = this.blockModCount;
		}
		final SetView<Filter.FilterInstance> outside =
				Sets.difference(this.graph.vertexSet(), this.flatFilterInstances);
		final Set<Filter.FilterInstance> neighbours =
				outside.stream()
						.filter(nFI -> this.flatFilterInstances.stream().anyMatch(
								bFI -> this.graph.containsEdge(bFI, nFI))).collect(toSet());
		return neighbours;
	}

	public boolean containedIn(final Block other) {
		if (other.rulesOrProxies.size() < this.rulesOrProxies.size()
				|| !other.rulesOrProxies.containsAll(this.rulesOrProxies)) {
			return false;
		}
		if (other.filters.size() < this.filters.size() || !other.filters.containsAll(this.filters)) {
			return false;
		}
		final Set<FilterInstancePartition.FilterInstanceSubSet> otherFISubSets =
				other.filterInstancePartition.getSubSets();
		final Set<FilterInstancePartition.FilterInstanceSubSet> thisFISubSets =
				this.filterInstancePartition.getSubSets();
		if (otherFISubSets.size() < thisFISubSets.size()) {
			return false;
		}
		if (!other.getFlatFilterInstances().containsAll(this.getFlatFilterInstances())) {
			return false;
		}
		return true;
	}

	public boolean remove(final Either<Rule, ExistentialProxy> rule) {
		if (!rulesOrProxies.remove(rule)) {
			return false;
		}
		// remove all ECs of the rule from theta by looking at the elements and using their pointer
		for (final SubSet<Element> subSet : elementPartition.getSubSets()) {
			final Element element = subSet.get(rule);
			final EquivalenceClass ec = element.getEquivalenceClass();
			theta.equivalenceClassToReduced.remove(ec);
			variableExpressionTheta.equivalenceClassToReduced.remove(ec);
		}
		elementPartition.remove(rule);
		factVariablePartition.remove(rule);
		filterInstancePartition.remove(rule);
		flatFilterInstances.removeIf(fi -> fi.getRuleOrProxy() == rule);
		++blockModCount;
		return true;
	}

	public boolean containsColumn(final FilterInstanceSubSet column) {
		return this.filterInstancePartition.lookupByFilter(column.filter).stream()
				.anyMatch(ss -> ss.elements.values().equals(column.elements.values()));
	}

	/**
	 * Removes the column. If the column is an implicit filter instance column, the right argument
	 * is removed from the equivalence class of the block.
	 * 
	 * @param column
	 *            column to be removed
	 * @return true iff the block was changed
	 */
	public boolean remove(final FilterInstanceSubSet column) {
		if (!removeFilterInstanceSubSet(column))
			return false;
		final FilterInstanceTypePartitioner partition =
				FilterInstanceTypePartitioner.partition(column.elements.values());
		if (!partition.getExplicitFilterInstances().isEmpty()) {
			// nothing to do, FIs removed, no fallout
		} else if (!partition.implicitECFilterInstances.isEmpty()) {
			final List<ImplicitECFilterInstance> fis = partition.implicitECFilterInstances;
			final ImplicitECFilterInstance someFI = fis.get(0);
			removeFilterInstanceSubSet(filterInstancePartition.lookup(someFI.getDual()));
			// consider element stuff
			final VariableExpression someElement = someFI.getRight();
			final SubSet<Element> elementSubSet = elementPartition.lookup(someElement);
			// remove from element partition
			elementPartition.remove(elementSubSet);
			boolean fisremoved = false;
			for (final Entry<Either<Rule, ExistentialProxy>, Element> entry : elementSubSet.elements.entrySet()) {
				final Either<Rule, ExistentialProxy> rule = entry.getKey();
				final VariableExpression element = (VariableExpression) entry.getValue();
				final EquivalenceClass ec = element.getEquivalenceClass();
				@SuppressWarnings("unchecked")
				final Set<VariableExpression> reduced =
						(Set<VariableExpression>) (Set<?>) variableExpressionTheta.reduce(ec);
				reduced.remove(element);
				if (reduced.isEmpty()) {
					variableExpressionTheta.equivalenceClassToReduced.remove(ec);
				} else if (!fisremoved) {
					fisremoved = true;
					for (final VariableExpression other : reduced) {
						final Filter filter = Filter.newEqualityFilter(element, other);
						final ImplicitECFilterInstance next =
								filter.getImplicitECInstances(rule)
										.stream()
										.filter(fi -> (fi.left == element && fi.right == other)
												|| (fi.left == other && fi.right == element)).iterator().next();
						removeFilterInstanceSubSet(filterInstancePartition.lookup(next));
						removeFilterInstanceSubSet(filterInstancePartition.lookup(next.getDual()));
					}
				}
			}
		} else if (!partition.implicitElementFilterInstances.isEmpty()) {
			final List<ImplicitElementFilterInstance> fis = partition.implicitElementFilterInstances;
			final ImplicitElementFilterInstance someFI = fis.get(0);
			removeFilterInstanceSubSet(filterInstancePartition.lookup(someFI.getDual()));
			// consider element stuff
			final Element someElement = someFI.getRight();
			final SubSet<Element> elementSubSet = elementPartition.lookup(someElement);
			// remove from element partition
			elementPartition.remove(elementSubSet);
			boolean fisremoved = false;
			for (final Entry<Either<Rule, ExistentialProxy>, Element> entry : elementSubSet.elements.entrySet()) {
				final Either<Rule, ExistentialProxy> rule = entry.getKey();
				final Element element = entry.getValue();
				final EquivalenceClass ec = element.getEquivalenceClass();
				final Set<Element> reduced = theta.reduce(ec);
				reduced.remove(element);
				if (reduced.isEmpty()) {
					theta.equivalenceClassToReduced.remove(ec);
				} else if (!fisremoved) {
					fisremoved = true;
					for (final Element other : reduced) {
						final Filter filter = Filter.newEqualityFilter(element, other);
						final ImplicitElementFilterInstance next =
								filter.getImplicitElementInstances(rule)
										.stream()
										.filter(fi -> (fi.left == element && fi.right == other)
												|| (fi.left == other && fi.right == element)).iterator().next();
						removeFilterInstanceSubSet(filterInstancePartition.lookup(next));
						removeFilterInstanceSubSet(filterInstancePartition.lookup(next.getDual()));
					}
				}
			}
		}
		// fact variable partition unchanged
		++blockModCount;
		return true;
	}

	private boolean removeFilterInstanceSubSet(final FilterInstanceSubSet subset) {
		final boolean removed = filterInstancePartition.remove(subset);
		if (!removed) {
			return false;
		}
		flatFilterInstances.removeAll(subset.elements.values());
		if (filterInstancePartition.lookupByFilter(subset.filter).isEmpty()) {
			filters.remove(subset.filter);
		}
		return true;
	}

	public void addRow(final Either<Rule, ExistentialProxy> extendRule,
			final IdentityHashMap<FactVariableSubSet, SingleFactVariable> fvExtension,
			final IdentityHashMap<FilterInstanceSubSet, FilterInstance> fiExtension,
			final IdentityHashMap<SubSet<Element>, Element> elExtension) {
		// TODO variableExpressionTheta
		elExtension.values().forEach(theta::add);
		elementPartition.extend(extendRule, elExtension);
		factVariablePartition.extend(extendRule, fvExtension);
		filterInstancePartition.extend(extendRule, fiExtension);
		flatFilterInstances.addAll(fiExtension.values());
		rulesOrProxies.add(extendRule);
		++blockModCount;
	}
}