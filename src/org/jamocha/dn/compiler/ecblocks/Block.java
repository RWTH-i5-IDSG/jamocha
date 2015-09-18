package org.jamocha.dn.compiler.ecblocks;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;

import org.jamocha.dn.compiler.ecblocks.ECBlocks.ConflictEdge;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Theta;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
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
		this.theta = new Theta.Reducer();
		this.variableExpressionTheta = new Theta.Reducer();
		this.edgeFactory = ConflictEdge.newFactory(this.theta);
		this.graph = new SimpleGraph<>(this.edgeFactory);
		this.rulesOrProxies = Sets.newHashSet(rules);
		this.factVariablePartition = factVariablePartition;
		this.filterInstancePartition = new FilterInstancePartition();
		this.elementPartition = new ElementPartition();
	}

	public Block(final Block block) {
		this.theta = block.theta.copy();
		this.variableExpressionTheta = block.variableExpressionTheta.copy();
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
		return this.filterInstancePartition.elements.size();
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
							rule -> ECBlocks.getFilters(rule).stream()
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
				other.filterInstancePartition.getElements();
		final Set<FilterInstancePartition.FilterInstanceSubSet> thisFISubSets =
				this.filterInstancePartition.getElements();
		if (otherFISubSets.size() < thisFISubSets.size()) {
			return false;
		}
		if (!other.getFlatFilterInstances().containsAll(this.getFlatFilterInstances())) {
			return false;
		}
		return true;
	}
}