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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.composeToInt;
import static org.jamocha.util.Lambdas.iterable;
import static org.jamocha.util.Lambdas.negate;
import static org.jamocha.util.Lambdas.newHashMap;
import static org.jamocha.util.Lambdas.newHashSet;
import static org.jamocha.util.Lambdas.newIdentityHashMap;
import static org.jamocha.util.Lambdas.newIdentityHashSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.collections4.list.CursorableLinkedList;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.dn.compiler.ecblocks.Filter.ExplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance.Conflict;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstanceVisitor;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitECFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitElementFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.ImplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.Partition.SubSet;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.filter.ECFilterSetVisitor;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.function.fwatransformer.FWAECLeafToTypeLeafTranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.visitor.Visitable;
import org.jamocha.visitor.Visitor;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.VertexCovers;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.atlassian.fugue.Either;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECBlocks {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static interface ElementVisitor extends Visitor {
		public void visit(final FactBinding element);

		public void visit(final SlotBinding element);

		public void visit(final ConstantExpression element);

		public void visit(final VariableExpression element);
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static interface Element extends Visitable<ElementVisitor> {
		public EquivalenceClass getEquivalenceClass();

		public SingleFactVariable getFactVariable();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode
	@ToString
	static class FactBinding implements Element {
		final SingleFactVariable fact;

		@Override
		public EquivalenceClass getEquivalenceClass() {
			return this.fact.getEqual();
		}

		@Override
		public SingleFactVariable getFactVariable() {
			return this.fact;
		}

		@Override
		public <V extends ElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode
	@ToString
	static class SlotBinding implements Element {
		final SingleSlotVariable slot;

		@Override
		public EquivalenceClass getEquivalenceClass() {
			return this.slot.getEqual();
		}

		@Override
		public SingleFactVariable getFactVariable() {
			return this.slot.getFactVariable();
		}

		@Override
		public <V extends ElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode(of = { "constant" })
	@ToString(of = { "constant" })
	static class ConstantExpression implements Element {
		final FunctionWithArguments<ECLeaf> constant;
		@Getter(onMethod = @__({ @Override }))
		final EquivalenceClass equivalenceClass;

		@Override
		public SingleFactVariable getFactVariable() {
			return null;
		}

		@Override
		public <V extends ElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode(of = { "variableExpression" })
	@ToString(of = "variableExpression")
	static class VariableExpression implements Element {
		final FunctionWithArguments<ECLeaf> variableExpression;
		final EquivalenceClass originEquivalenceClass;
		final List<EquivalenceClass> ecsInVE;

		public VariableExpression(final FunctionWithArguments<ECLeaf> variableExpression,
				final EquivalenceClass originEquivalenceClass) {
			this.variableExpression = variableExpression;
			this.originEquivalenceClass = originEquivalenceClass;
			this.ecsInVE = OrderedECCollector.collect(variableExpression);
		}

		@Override
		public EquivalenceClass getEquivalenceClass() {
			return this.originEquivalenceClass;
		}

		@Override
		public <V extends ElementVisitor> V accept(final V visitor) {
			throw new UnsupportedOperationException();
		}

		@Override
		public SingleFactVariable getFactVariable() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static class ElementToTemplateSlotLeafTranslator implements ElementVisitor {
		FunctionWithArguments<TemplateSlotLeaf> arg;

		@Override
		public void visit(final FactBinding element) {
			this.arg = new TemplateSlotLeaf(element.getFact().getTemplate(), null);
		}

		@Override
		public void visit(final SlotBinding element) {
			this.arg = new TemplateSlotLeaf(element.getFactVariable().getTemplate(), element.getSlot().getSlot());
		}

		@Override
		public void visit(final ConstantExpression element) {
			this.arg =
					new ConstantLeaf<TemplateSlotLeaf>(element.constant.evaluate(), element.constant.getReturnType());
		}

		@Override
		public void visit(final VariableExpression element) {
			throw new UnsupportedOperationException();
		}
	}

	static class ConstantExpressionCollector implements ElementVisitor {
		Optional<ConstantExpression> constant = Optional.empty();

		static Optional<ConstantExpression> findFirst(final Collection<Element> elements) {
			final ConstantExpressionCollector instance = new ConstantExpressionCollector();
			for (final Element element : elements) {
				element.accept(instance);
				if (instance.constant.isPresent())
					return instance.constant;
			}
			return instance.constant;
		}

		@Override
		public void visit(final FactBinding element) {
		}

		@Override
		public void visit(final SlotBinding element) {
		}

		@Override
		public void visit(final ConstantExpression element) {
			this.constant = Optional.of(element);
		}

		@Override
		public void visit(final VariableExpression element) {
		}
	}

	protected static Set<Pair<Integer, Integer>> getECIndexSet(final FilterInstance source, final FilterInstance target) {
		final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
		final List<EquivalenceClass> sourceParameters = source.getDirectlyContainedEquivalenceClasses();
		final List<EquivalenceClass> targetParameters = target.getDirectlyContainedEquivalenceClasses();
		final Map<EquivalenceClass, List<Integer>> targetECIndices =
				IntStream.range(0, targetParameters.size()).boxed().collect(groupingBy(targetParameters::get));
		final int size = sourceParameters.size();
		for (int i = 0; i < size; ++i) {
			final Integer oi = Integer.valueOf(i);
			for (final Integer ji : targetECIndices.getOrDefault(sourceParameters.get(oi), Collections.emptyList())) {
				intersectingECsIndices.add(Pair.of(oi, ji));
			}
		}
		return intersectingECsIndices;
	}

	@Getter
	public static class FilterInstanceTypePartitioner implements FilterInstanceVisitor {
		final List<ExplicitFilterInstance> explicitFilterInstances = new ArrayList<>();
		final List<ImplicitElementFilterInstance> implicitElementFilterInstances = new ArrayList<>();
		final List<ImplicitECFilterInstance> implicitECFilterInstances = new ArrayList<>();

		public static FilterInstanceTypePartitioner partition(final Iterable<FilterInstance> filterInstances) {
			final FilterInstanceTypePartitioner partitioner = new FilterInstanceTypePartitioner();
			for (final FilterInstance filterInstance : filterInstances) {
				filterInstance.accept(partitioner);
			}
			return partitioner;
		}

		@Override
		public void visit(final ExplicitFilterInstance filterInstance) {
			this.explicitFilterInstances.add(filterInstance);
		}

		@Override
		public void visit(final ImplicitECFilterInstance filterInstance) {
			this.implicitECFilterInstances.add(filterInstance);
		}

		@Override
		public void visit(final ImplicitElementFilterInstance filterInstance) {
			this.implicitElementFilterInstances.add(filterInstance);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	static interface Theta {
		@RequiredArgsConstructor
		static class Reducer implements Theta {
			final Map<EquivalenceClass, ReducedEquivalenceClass> equivalenceClassToReduced = new IdentityHashMap<>();

			@Override
			public boolean isRelevant(final Element element) {
				final ReducedEquivalenceClass reducedEquivalenceClass =
						equivalenceClassToReduced.get(element.getEquivalenceClass());
				return null != reducedEquivalenceClass && reducedEquivalenceClass.isRelevant(element);
			}

			@Override
			public boolean isRelevant(final EquivalenceClass equivalenceClass) {
				return equivalenceClassToReduced.keySet().contains(equivalenceClass);
			}

			@Override
			public Set<SingleFactVariable> getDependentFactVariables(final EquivalenceClass equivalenceClass) {
				final ReducedEquivalenceClass reducedEquivalenceClass = equivalenceClassToReduced.get(equivalenceClass);
				if (null == reducedEquivalenceClass)
					return Collections.emptySet();
				return reducedEquivalenceClass.getDependentFactVariables();
			}

			@Override
			public Theta copy() {
				final Reducer reducer = new Reducer();
				for (final Entry<EquivalenceClass, ReducedEquivalenceClass> entry : equivalenceClassToReduced
						.entrySet()) {
					reducer.equivalenceClassToReduced
					.put(entry.getKey(), new ReducedEquivalenceClass(entry.getValue()));
				}
				return reducer;
			}

			@Override
			public void add(final Element element) {
				equivalenceClassToReduced.computeIfAbsent(element.getEquivalenceClass(), ReducedEquivalenceClass::new)
				.add(element);
			}

			@Override
			public Set<EquivalenceClass> getEquivalenceClasses() {
				return equivalenceClassToReduced.keySet();
			}

			@Override
			public Set<Element> reduce(final EquivalenceClass ec) {
				final ReducedEquivalenceClass reducedEquivalenceClass = equivalenceClassToReduced.get(ec);
				if (null == reducedEquivalenceClass)
					return Collections.emptySet();
				return reducedEquivalenceClass.elements;
			}
		}

		static class Identity implements Theta {
			final Set<EquivalenceClass> equivalenceClasses = Sets.newIdentityHashSet();

			public Identity(final Set<EquivalenceClass> equivalenceClasses) {
				this.equivalenceClasses.addAll(equivalenceClasses);
			}

			@Override
			public boolean isRelevant(final Element element) {
				return equivalenceClasses.contains(element.getEquivalenceClass());
			}

			@Override
			public boolean isRelevant(final EquivalenceClass equivalenceClass) {
				return equivalenceClasses.contains(equivalenceClass);
			}

			@Override
			public Set<SingleFactVariable> getDependentFactVariables(final EquivalenceClass equivalenceClass) {
				return equivalenceClass.getDependentFactVariables();
			}

			@Override
			public Theta copy() {
				return new Identity(equivalenceClasses);
			}

			@Override
			public void add(final Element element) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Set<EquivalenceClass> getEquivalenceClasses() {
				return equivalenceClasses;
			}

			@Override
			public Set<Element> reduce(final EquivalenceClass ec) {
				throw new UnsupportedOperationException();
			}
		}

		public Set<Element> reduce(final EquivalenceClass ec);

		public void add(final Element element);

		public boolean isRelevant(final Element element);

		public boolean isRelevant(final EquivalenceClass equivalenceClass);

		public Set<SingleFactVariable> getDependentFactVariables(final EquivalenceClass equivalenceClass);

		public Theta copy();

		public Set<EquivalenceClass> getEquivalenceClasses();
	}

	protected static List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> determineEquivalenceClassIntersection(
			final Map<Either<Rule, ExistentialProxy>, EquivalenceClass> ecs,
			final FactVariablePartition factVariablePartition) {
		final Map<FactVariableSubSet, Map<Either<Rule, ExistentialProxy>, FactBinding>> fvMapping =
				new IdentityHashMap<>();
		final Map<FactVariableSubSet, Map<SlotAddress, Map<Either<Rule, ExistentialProxy>, SlotBinding>>> svMapping =
				new IdentityHashMap<>();
		final Map<Object, Map<Either<Rule, ExistentialProxy>, ConstantExpression>> constantMapping = new HashMap<>();
		for (final Entry<Either<Rule, ExistentialProxy>, EquivalenceClass> entry : ecs.entrySet()) {
			final Either<Rule, ExistentialProxy> rule = entry.getKey();
			final EquivalenceClass ec = entry.getValue();
			for (final SingleFactVariable fv : ec.getFactVariables()) {
				final FactVariableSubSet subSet = factVariablePartition.lookup(fv);
				fvMapping.computeIfAbsent(subSet, newIdentityHashMap()).put(rule, new FactBinding(fv));
			}
			for (final SingleSlotVariable sv : ec.getSlotVariables()) {
				final FactVariableSubSet subSet = factVariablePartition.lookup(sv.getFactVariable());
				svMapping.computeIfAbsent(subSet, newIdentityHashMap())
				.computeIfAbsent(sv.getSlot(), newIdentityHashMap()).put(rule, new SlotBinding(sv));
			}
			for (final FunctionWithArguments<ECLeaf> constant : ec.getConstantExpressions()) {
				final Object value = constant.evaluate();
				constantMapping.computeIfAbsent(value, newHashMap()).put(rule, new ConstantExpression(constant, ec));
			}
		}
		final int ruleCount = ecs.size();
		final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> intersection =
				Stream.concat(
						fvMapping.values().stream().filter(map -> map.size() == ruleCount),
						Stream.concat(
								svMapping.values().stream().flatMap(map -> map.values().stream())
								.filter(map -> map.size() == ruleCount), constantMapping.values().stream()
								.filter(map -> map.size() == ruleCount))).collect(toList());
		assert intersection.stream().allMatch(map -> map.size() == ruleCount);
		return intersection;
	}

	protected static Set<Filter> getFilters(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFilters, ExistentialProxy::getFilters);
	}

	protected static Set<SingleFactVariable> getFactVariables(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFactvariables, ExistentialProxy::getFactvariables);
	}

	protected static void addRule(final ECSetRule ecFilterSetCondition, final List<Either<Rule, ExistentialProxy>> rules) {
		final Rule rule = new Rule(ecFilterSetCondition);
		// create all filter instances
		final Set<ECFilterSet> condition = ecFilterSetCondition.getCondition();
		final Either<Rule, ExistentialProxy> ruleEither = rule.either;
		RuleConverter.convert(rules, ruleEither, condition);
		// from this point on, the rule won't change any more (aka the filters and the existential
		// proxies have been added) => it can be used as a key in a HashMap

		// add rule to rule list
		rules.add(ruleEither);
	}

	protected static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraphForRules(
			final Theta blockTheta, final List<Either<Rule, ExistentialProxy>> ruleOrProxies) {
		return determineConflictGraph(
				blockTheta,
				ruleOrProxies
				.stream()
				.map(ruleOrProxy -> getFilters(ruleOrProxy).stream()
						.flatMap(f -> f.getAllInstances(ruleOrProxy).stream()).collect(toList()))
						.collect(toList()));
	}

	protected static UndirectedGraph<FilterInstance, ConflictEdge> determineConflictGraph(final Theta blockTheta,
			final Iterable<? extends List<FilterInstance>> filterInstancesGroupedByRule) {
		final UndirectedGraph<FilterInstance, ConflictEdge> graph =
				new SimpleGraph<>(ConflictEdge.newFactory(blockTheta));
		for (final List<FilterInstance> instances : filterInstancesGroupedByRule) {
			for (final FilterInstance filterInstance : instances) {
				assert null != filterInstance;
				graph.addVertex(filterInstance);
			}
			// instances.forEach(graph::addVertex);
			final int numInstances = instances.size();
			for (int i = 0; i < numInstances; i++) {
				final FilterInstance fi1 = instances.get(i);
				for (int j = i + 1; j < numInstances; j++) {
					final FilterInstance fi2 = instances.get(j);
					final ConflictEdge edge = ConflictEdge.of(fi1, fi2, blockTheta, blockTheta);
					if (null == edge)
						continue;
					graph.addEdge(fi1, fi2, edge);
				}
			}
		}
		return graph;
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static class ReducedEquivalenceClass {
		final EquivalenceClass original;
		final Set<Element> elements;

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("REC: ");
			sb.append(Objects.toString(this.elements));
			return sb.toString();
		}

		public ReducedEquivalenceClass(final EquivalenceClass original) {
			this(original, Sets.newHashSet());
		}

		public ReducedEquivalenceClass(final EquivalenceClass original, final Element firstElement) {
			this(original, Sets.newHashSet(firstElement));
		}

		public ReducedEquivalenceClass(final ReducedEquivalenceClass copy) {
			this(copy.original, Sets.newHashSet(copy.elements));
		}

		public ReducedEquivalenceClass add(final Element element) {
			assert element.getEquivalenceClass() == this.original;
			this.elements.add(element);
			return this;
		}

		public ReducedEquivalenceClass remove(final Element element) {
			assert element.getEquivalenceClass() == this.original;
			this.elements.remove(element);
			return this;
		}

		public boolean isRelevant(final Element element) {
			return this.elements.contains(element);
		}

		public Set<SingleFactVariable> getDependentFactVariables() {
			return this.elements.stream().map(Element::getFactVariable).collect(toSet());
		}
	}

	@AllArgsConstructor
	static class RuleConverter implements ECFilterSetVisitor {
		final List<Either<Rule, ExistentialProxy>> rules;
		final Either<Rule, ExistentialProxy> ruleOrProxy;

		public static void convert(final List<Either<Rule, ExistentialProxy>> rules,
				final Either<Rule, ExistentialProxy> ruleOrProxy, final Collection<ECFilterSet> filters) {
			final Rule rule = ruleOrProxy.left().get();
			final Set<EquivalenceClass> equivalenceClasses = rule.getOriginal().getEquivalenceClasses();
			for (final EquivalenceClass equivalenceClass : equivalenceClasses) {
				final List<Element> elements = new ArrayList<>();
				equivalenceClass.getFactVariables().stream().map(FactBinding::new).forEach(elements::add);
				equivalenceClass.getSlotVariables().stream().map(SlotBinding::new).forEach(elements::add);
				equivalenceClass.getConstantExpressions().stream()
				.map(c -> new ConstantExpression(c, equivalenceClass)).forEach(elements::add);
				for (int i = 0; i < elements.size(); i++) {
					final Element left = elements.get(i);
					for (int j = i + 1; j < elements.size(); j++) {
						final Element right = elements.get(j);
						Filter.newImplicitElementInstance(ruleOrProxy, left, right);
					}
				}

				final LinkedList<FunctionWithArguments<ECLeaf>> variableExpressions =
						equivalenceClass.getVariableExpressions();
				if (!variableExpressions.isEmpty()) {
					final List<VariableExpression> converted =
							variableExpressions.stream().map(v -> new VariableExpression(v, equivalenceClass))
							.collect(toList());
					for (int i = 0; i < converted.size(); i++) {
						final VariableExpression left = converted.get(i);
						for (int j = i + 1; j < converted.size(); j++) {
							final VariableExpression right = converted.get(j);
							Filter.newImplicitECInstance(ruleOrProxy, left, right);
						}
					}
					if (!converted.isEmpty() && !elements.isEmpty()) {
						final VariableExpression ecLeaf =
								new VariableExpression(new ECLeaf(equivalenceClass), equivalenceClass);
						for (final VariableExpression fwa : converted) {
							Filter.newImplicitECInstance(ruleOrProxy, ecLeaf, fwa);
						}
					}
				}
			}
			final RuleConverter ruleConverter = new RuleConverter(rules, ruleOrProxy);
			for (final ECFilterSet filter : filters) {
				filter.accept(ruleConverter);
			}
		}

		@Override
		public void visit(final ECFilter ecFilter) {
			Filter.newFilter(FWAECLeafToTypeLeafTranslator.translate(ecFilter.getFunction())).addExplicitInstance(
					this.ruleOrProxy, ecFilter);
		}

		@Override
		public void visit(final ECExistentialSet existentialSet) {
			final Rule rule =
					this.ruleOrProxy.left().getOrThrow(() -> new UnsupportedOperationException("Nested Existentials!"));
			// we may be able to share the existential closure part
			// existential closure filter instances are put into the same column if and only if they
			// have the same conflicts to their pure part and the pure parts have the same inner
			// conflicts

			final ECFilter existentialClosure = existentialSet.getExistentialClosure();
			final Set<ECFilterSet> purePart = existentialSet.getPurePart();

			final ExistentialProxy proxy = new ExistentialProxy(rule, existentialSet);
			final Either<Rule, ExistentialProxy> proxyEither = proxy.either;
			final RuleConverter visitor = new RuleConverter(this.rules, proxyEither);

			// insert all pure filters into the proxy
			for (final ECFilterSet pathFilterSet : purePart) {
				pathFilterSet.accept(visitor);
			}
			// create own row for the pure part
			this.rules.add(proxyEither);

			final FilterInstance filterInstance =
					FilterProxy.newFilterProxy(
							FWAECLeafToTypeLeafTranslator.translate(existentialClosure.getFunction()), proxy)
							.addExplicitInstance(this.ruleOrProxy, existentialClosure);
			rule.existentialProxies.put(filterInstance, proxy);
		}
	}

	public static List<PathRule> transform(final List<ECSetRule> rules) {
		final Pair<List<Either<Rule, ExistentialProxy>>, ECBlockSet> compile = compile(rules);
		return compile(compile.getLeft(), compile.getRight());
	}

	public static Pair<List<Either<Rule, ExistentialProxy>>, ECBlockSet> compile(final List<ECSetRule> rules) {
		final List<Either<Rule, ExistentialProxy>> translatedRules = new ArrayList<>();
		for (final ECSetRule rule : rules) {
			addRule(rule, translatedRules);
		}
		// find all maximal blocks
		final ECBlockSet resultBlockSet = new ECBlockSet();
		findAllMaximalBlocks(translatedRules, resultBlockSet);
		// solve the conflicts
		determineAndSolveConflicts(resultBlockSet);
		return Pair.of(translatedRules, resultBlockSet);
	}

	public static List<PathRule> compile(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet resultBlockSet) {
		final Function<? super Block, ? extends Integer> characteristicNumber =
				block -> block.getFlatFilterInstances().size() / block.getRulesOrProxies().size();
				final TreeMap<Integer, CursorableLinkedList<Block>> blockMap =
						resultBlockSet
						.getBlocks()
						.stream()
						.collect(
								groupingBy(characteristicNumber, TreeMap::new, toCollection(CursorableLinkedList::new)));

				// iterate over all the filter proxies ever used
				for (final FilterProxy filterProxy : FilterProxy.getFilterProxies()) {
					final Set<ExistentialProxy> existentialProxies = filterProxy.getProxies();
					// determine the largest characteristic number of the blocks containing filter instances
					// of one of the existential proxies (choice is arbitrary, since the filters and the
					// conflicts are identical if they belong to the same filter).
					final OptionalInt optMax =
							resultBlockSet.getRuleInstanceToBlocks()
							.computeIfAbsent(existentialProxies.iterator().next().either, newHashSet()).stream()
							.mapToInt(composeToInt(characteristicNumber, Integer::intValue)).max();
					if (!optMax.isPresent())
						continue;
					final int eCN = optMax.getAsInt();
					// get the list to append the blocks using the existential closure filter instance to
					final CursorableLinkedList<Block> targetList = blockMap.get(eCN);
					// for every existential part
					for (final ExistentialProxy existentialProxy : existentialProxies) {
						final FilterInstance exClosure = existentialProxy.getExistentialClosure();
						// create a list storing the blocks to move
						final List<Block> toMove = new ArrayList<>();
						// scan all lists up to characteristic number eCN
						for (final CursorableLinkedList<Block> blockList : blockMap.headMap(eCN, true).values()) {
							// iterate over the blocks in the current list
							for (final ListIterator<Block> iterator = blockList.listIterator(); iterator.hasNext();) {
								final Block current = iterator.next();
								// if the current block uses the current existential closure filter
								// instance, it has to be moved
								if (current.getFlatFilterInstances().contains(exClosure)) {
									iterator.remove();
									toMove.add(current);
								}
							}
						}
						// append the blocks to be moved (they were only removed so far)
						targetList.addAll(toMove);
					}
				}

				return ECBlocksToPathRule.compile(rules, blockMap);
	}

	protected static Optional<Element> getVEinECwithConstantsAsArgs(final Block block, final EquivalenceClass ec) {
		return block.variableExpressionTheta
				.reduce(ec)
				.stream()
				.filter(e -> ((VariableExpression) e).getEcsInVE().stream()
						.allMatch(eec -> getConstantInEC(block, eec).isPresent())).findAny();
	}

	protected static Optional<Element> getConstantInEC(final Block block, final EquivalenceClass ec) {
		return block.theta.reduce(ec).stream().filter(e -> null == e.getFactVariable()).findAny();
	}

	public static boolean hasEqualConflicts(final Conflict a, final Conflict b) {
		return (a == b) || (a != null && a.hasEqualConflicts(b));
	}

	protected static void determineAndSolveConflicts(final ECBlockSet resultBlocks) {
		// determine conflicts
		final ECBlockSet deletedBlocks = new ECBlockSet();
		final DirectedGraph<Block, BlockConflict> blockConflictGraph = new SimpleDirectedGraph<>(BlockConflict::of);
		for (final Block block : resultBlocks.getBlocks()) {
			blockConflictGraph.addVertex(block);
		}
		for (final Block x : blockConflictGraph.vertexSet()) {
			createArcs(blockConflictGraph, resultBlocks, x);
		}
		// solve conflicts
		while (true) {
			final Optional<BlockConflict> mostUsefulConflictsFirst =
					blockConflictGraph.edgeSet().stream().max(Comparator.comparingInt(BlockConflict::getQuality));
			if (!mostUsefulConflictsFirst.isPresent()) {
				break;
			}
			final BlockConflict blockConflict = mostUsefulConflictsFirst.get();
			solveConflict(blockConflict, blockConflictGraph, resultBlocks, deletedBlocks);
		}
	}

	protected static void createArcs(final DirectedGraph<Block, BlockConflict> blockConflictGraph,
			final ECBlockSet resultBlocks, final Block x) {
		for (final Either<Rule, ExistentialProxy> rule : x.getRulesOrProxies()) {
			for (final Block y : resultBlocks.getRuleInstanceToBlocks().get(rule)) {
				if (x == y || blockConflictGraph.containsEdge(x, y)) {
					continue;
				}
				addArc(blockConflictGraph, x, y);
				addArc(blockConflictGraph, y, x);
			}
		}
	}

	protected static void addArc(final DirectedGraph<Block, BlockConflict> blockConflictGraph, final Block x,
			final Block y) {
		// arc is a conflict between X and Y
		// conf will always be a conflict between X and B
		final BlockConflict arc = BlockConflict.of(x, y);
		if (null == arc)
			return;
		final Set<BlockConflict> oldArcs = blockConflictGraph.outgoingEdgesOf(x);
		// determine arc's quality
		final Set<FilterInstance> xFIs = x.getFlatFilterInstances();
		final Set<FilterInstance> yFIs = y.getFlatFilterInstances();
		arc.quality =
				oldArcs.stream()
				.mapToInt(
						conf -> usefulness(arc, xFIs, conf, conf.getConflictingBlock().getFlatFilterInstances())
						- (Sets.difference(arc.cfi, yFIs).size() + (Sets.intersection(xFIs, yFIs)
								.isEmpty() ? 0 : 1))).sum();
		// update quality of all arcs affected
		for (final BlockConflict conf : oldArcs) {
			final Set<FilterInstance> bFIs = conf.getConflictingBlock().getFlatFilterInstances();
			conf.quality += usefulness(arc, xFIs, conf, bFIs);
		}
		// add the new edge
		blockConflictGraph.addEdge(x, y, arc);
	}

	protected static int usefulness(final BlockConflict xyArc, final Set<FilterInstance> xFIs,
			final BlockConflict xbArc, final Set<FilterInstance> bFIs) {
		return Sets.intersection(Sets.difference(xbArc.cfi, bFIs), xyArc.cfi).size()
				+ (Sets.difference(Sets.intersection(xFIs, bFIs), xyArc.cfi).isEmpty() ? 0 : 1);
	}

	protected static void removeArc(final DirectedGraph<Block, BlockConflict> blockConflictGraph,
			final BlockConflict arc) {
		final Block b = arc.getReplaceBlock();
		final Set<FilterInstance> bFIs = b.getFlatFilterInstances();
		// about updating the quality of all affected arcs:
		// the outgoing arcs of b only influence each other, but all get deleted
		// the incoming arcs of b influence arcs not getting deleted
		for (final BlockConflict xbArc : blockConflictGraph.incomingEdgesOf(b)) {
			// for every neighbor x of b
			final Block x = xbArc.getReplaceBlock();
			final Set<FilterInstance> xFIs = x.getFlatFilterInstances();
			for (final BlockConflict xyArc : blockConflictGraph.outgoingEdgesOf(x)) {
				// for every conflict of that neighbor
				if (xbArc == xyArc)
					// excluding the arc getting deleted
					continue;
				// decrement the quality of the block conflict
				// remove the influence of the xb arc regarding the xy arc
				xyArc.quality -= usefulness(xyArc, xFIs, xbArc, bFIs);
			}
		}
		// remove the block to be replaced
		final boolean vertexRemoved = blockConflictGraph.removeVertex(b);
		assert vertexRemoved;
	}

	@RequiredArgsConstructor
	@Getter
	@Setter
	static class BlockConflict {
		final Block replaceBlock, conflictingBlock;
		final Set<FilterInstance> cfi;
		int quality;

		public static BlockConflict of(final Block replaceBlock, final Block conflictingBlock) {
			// determine if there is a pair of FIs in conflict
			final Set<FilterInstance> cfi =
					replaceBlock
					.getFlatFilterInstances()
					.stream()
					.filter(xFI -> conflictingBlock
							.getFilterInstancePartition()
							.getElements()
							.stream()
							.map(ySS -> ySS.get(xFI.getRuleOrProxy()))
							.filter(Objects::nonNull)
							.anyMatch(
									yFI -> null != yFI.getConflict(xFI, conflictingBlock.theta,
											replaceBlock.theta))).collect(toSet());
			if (cfi.isEmpty())
				return null;
			// if non-overlapping
			if (Collections.disjoint(replaceBlock.getFlatFilterInstances(), conflictingBlock.getFlatFilterInstances())) {
				return new BlockConflict(replaceBlock, conflictingBlock, cfi);
			}
			// else overlapping
			final int rnoc = replaceBlock.getNumberOfColumns();
			final int cnoc = conflictingBlock.getNumberOfColumns();
			// x will be wider, y will be taller
			final Block x, y;
			if (rnoc >= cnoc) {
				x = replaceBlock;
				y = conflictingBlock;
			} else {
				x = conflictingBlock;
				y = replaceBlock;
			}
			// will only work if y.getFilters() subseteq x.getFilters()
			if (!x.getFilters().containsAll(y.getFilters())) {
				return new BlockConflict(replaceBlock, conflictingBlock, cfi);
			}
			// will only work if x.getRules subseteq y.getRules
			if (!y.getRulesOrProxies().containsAll(x.getRulesOrProxies())) {
				return new BlockConflict(replaceBlock, conflictingBlock, cfi);
			}
			// only consider the filters of y (the taller block)
			final Set<FilterInstanceSubSet> columns = y.getFilterInstancePartition().getElements();

			// if one of the columns of the taller block is not present in the wider block, the
			// blocks are in conflict
			for (final FilterInstanceSubSet column : columns) {
				if (!x.getFilterInstancePartition().lookupByFilter(column.getFilter()).stream()
						.anyMatch(xSubSet -> column.contains(xSubSet))) {
					return new BlockConflict(replaceBlock, conflictingBlock, cfi);
				}
			}

			// if one of the intersecting equivalence classes is not reduced further by the theta of
			// the taller block than by the theta of the wider block, the blocks are in conflict
			final Set<EquivalenceClass> m =
					x.flatFilterInstances.stream().map(FilterInstance::getDirectlyContainedEquivalenceClasses)
					.flatMap(List::stream).collect(toCollection(Sets::newIdentityHashSet));
			m.retainAll(y.flatFilterInstances.stream().map(FilterInstance::getDirectlyContainedEquivalenceClasses)
					.flatMap(List::stream).collect(toCollection(Sets::newIdentityHashSet)));
			for (final EquivalenceClass equivalenceClass : m) {
				if (!x.theta.reduce(equivalenceClass).containsAll(y.theta.reduce(equivalenceClass))) {
					return new BlockConflict(replaceBlock, conflictingBlock, cfi);
				}
			}

			return null;
		}
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	// no @EqualsAndHashCode so equals will not prevent new edges
	static class BlockConflictEdge {
		final BlockConflict a, b;

		public BlockConflict getForReplaceBlock(final Block replaceBlock) {
			assert this.a.getReplaceBlock() == replaceBlock || this.b.getReplaceBlock() == replaceBlock;
			return this.a.getReplaceBlock() == replaceBlock ? this.a : this.b;
		}

		public BlockConflict getForConflictingBlock(final Block conflictingBlock) {
			assert this.a.getConflictingBlock() == conflictingBlock || this.b.getConflictingBlock() == conflictingBlock;
			return this.a.getConflictingBlock() == conflictingBlock ? this.a : this.b;
		}

		public static BlockConflictEdge of(final Block x, final Block y) {
			final BlockConflict xy = BlockConflict.of(x, y);
			if (null == xy)
				return null;
			return new BlockConflictEdge(xy, BlockConflict.of(y, x));
		}
	}

	protected static void solveConflict(final BlockConflict blockConflict,
			final DirectedGraph<Block, BlockConflict> blockConflictGraph, final ECBlockSet resultBlocks,
			final ECBlockSet deletedBlocks) {
		final Block replaceBlock = blockConflict.getReplaceBlock();
		final Set<FilterInstance> xWOcfi =
				replaceBlock.getFlatFilterInstances().stream().filter(negate(blockConflict.getCfi()::contains))
				.collect(toSet());
		resultBlocks.remove(replaceBlock);
		// remove replaceBlock and update qualities
		removeArc(blockConflictGraph, blockConflict);
		// find the horizontally maximal blocks within xWOcfi
		final ECBlockSet newBlocks = findAllMaximalBlocksInReducedScope(xWOcfi, new ECBlockSet());
		// for every such block,
		for (final Block block : newBlocks.getBlocks()) {
			if (!deletedBlocks.isContained(block)) {
				if (resultBlocks.addDuringConflictResolution(block)) {
					blockConflictGraph.addVertex(block);
					createArcs(blockConflictGraph, resultBlocks, block);
				}
			}
		}
		deletedBlocks.addDuringConflictResolution(replaceBlock);
	}

	protected static void findAllMaximalBlocks(final List<Either<Rule, ExistentialProxy>> rules,
			final ECBlockSet resultBlocks) {
		final Set<Filter> filters = rules.stream().flatMap(rule -> getFilters(rule).stream()).collect(toSet());
		for (final Filter filter : filters) {
			vertical(rules.stream().map(r -> filter.getImplicitElementInstances(r)).filter(negate(Set::isEmpty))
					.collect(toSet()), resultBlocks, ECBlocks::implicitElementVerticalInner);
			for (final Set<Set<ExplicitFilterInstance>> sameECPatternFIsGroupedByRule : rules
					.stream()
					.flatMap(r -> filter.getExplicitInstances(r).stream())
					.collect(
							groupingIntoSets(ECBlocks::computeECPattern,
									groupingIntoSets(ExplicitFilterInstance::getRuleOrProxy, toSet())))) {
				vertical(sameECPatternFIsGroupedByRule, resultBlocks, ECBlocks::explicitVerticalInner);
			}
			for (final Set<Set<ImplicitECFilterInstance>> sameECPatternFIsGroupedByRule : rules
					.stream()
					.flatMap(r -> filter.getImplicitECInstances(r).stream())
					.collect(
							groupingIntoSets(ECBlocks::computeECPattern,
									groupingIntoSets(ImplicitECFilterInstance::getRuleOrProxy, toSet())))) {
				vertical(sameECPatternFIsGroupedByRule, resultBlocks, ECBlocks::implicitECVerticalInner);
			}
		}
	}

	protected static <T, K, D> Collector<T, ?, Set<D>> groupingIntoSets(
			final Function<? super T, ? extends K> classifier, final Collector<? super T, ?, D> downstream) {
		final Collector<T, ?, Map<K, D>> groupingBy = groupingBy(classifier, downstream);
		return Collectors.collectingAndThen(groupingBy, map -> new HashSet<D>(map.values()));
	}

	protected static ECBlockSet findAllMaximalBlocksInReducedScope(final Set<FilterInstance> filterInstances,
			final ECBlockSet resultBlocks) {
		final FilterInstanceTypePartitioner typePartition = FilterInstanceTypePartitioner.partition(filterInstances);
		for (final Set<Set<ImplicitElementFilterInstance>> filterInstancesOfOneFilterGroupedByRule : typePartition.implicitElementFilterInstances
				.stream().collect(
						groupingIntoSets(FilterInstance::getFilter,
								groupingIntoSets(FilterInstance::getRuleOrProxy, toSet())))) {
			vertical(filterInstancesOfOneFilterGroupedByRule, resultBlocks, ECBlocks::implicitElementVerticalInner);
		}
		for (final Set<Set<Set<ExplicitFilterInstance>>> filterInstancesOfOneFilterGroupedByECPatternAndRule : typePartition.explicitFilterInstances
				.stream().collect(
						groupingIntoSets(
								FilterInstance::getFilter,
								groupingIntoSets(ECBlocks::computeECPattern,
										groupingIntoSets(ExplicitFilterInstance::getRuleOrProxy, toSet()))))) {
			for (final Set<Set<ExplicitFilterInstance>> filterInstancesOfOneFilterAndECPatternGroupedByRule : filterInstancesOfOneFilterGroupedByECPatternAndRule) {
				vertical(filterInstancesOfOneFilterAndECPatternGroupedByRule, resultBlocks,
						ECBlocks::explicitVerticalInner);
			}
		}
		for (final Set<Set<Set<ImplicitECFilterInstance>>> filterInstancesOfOneFilterGroupedByECPatternAndRule : typePartition.implicitECFilterInstances
				.stream().collect(
						groupingIntoSets(
								FilterInstance::getFilter,
								groupingIntoSets(ECBlocks::computeECPattern,
										groupingIntoSets(ImplicitECFilterInstance::getRuleOrProxy, toSet()))))) {
			for (final Set<Set<ImplicitECFilterInstance>> filterInstancesOfOneFilterAndECPatternGroupedByRule : filterInstancesOfOneFilterGroupedByECPatternAndRule) {
				vertical(filterInstancesOfOneFilterAndECPatternGroupedByRule, resultBlocks,
						ECBlocks::implicitECVerticalInner);
			}
		}
		return resultBlocks;
	}

	protected static List<FactVariablePartition> enumerateFactVariablePartitions(
			final Set<Either<Rule, ExistentialProxy>> rules) {
		if (1 == rules.size()) {
			final Either<Rule, ExistentialProxy> rule = rules.iterator().next();
			final FactVariablePartition partition = new FactVariablePartition();
			getFactVariables(rule).forEach(
					fv -> partition.add(new FactVariableSubSet(Collections.singletonMap(rule, fv))));
			return Collections.singletonList(partition);
		}
		final IdentityHashMap<Template, Set<List<FactVariableSubSet>>> subsets = new IdentityHashMap<>();
		final IdentityHashMap<Template, Map<Either<Rule, ExistentialProxy>, Set<SingleFactVariable>>> partitionMap =
				new IdentityHashMap<>();
		final IdentityHashMap<SingleFactVariable, Either<Rule, ExistentialProxy>> fvToRule = new IdentityHashMap<>();
		for (final Either<Rule, ExistentialProxy> rule : rules) {
			final Map<Template, Set<SingleFactVariable>> template2FVs =
					getFactVariables(rule).stream().collect(
							groupingBy(SingleFactVariable::getTemplate, toIdentityHashSet()));
			for (final Entry<Template, Set<SingleFactVariable>> entry : template2FVs.entrySet()) {
				final Template template = entry.getKey();
				final Set<SingleFactVariable> fvs = entry.getValue();
				partitionMap.computeIfAbsent(template, newHashMap()).put(rule, fvs);
				fvs.forEach(fv -> fvToRule.put(fv, rule));
			}
		}
		for (final Entry<Template, Map<Either<Rule, ExistentialProxy>, Set<SingleFactVariable>>> templateToMap : partitionMap
				.entrySet()) {
			final Template template = templateToMap.getKey();
			final Map<Either<Rule, ExistentialProxy>, Set<SingleFactVariable>> map = templateToMap.getValue();
			final IntSummaryStatistics summary =
					rules.stream().map(rule -> map.getOrDefault(rule, Collections.emptySet())).mapToInt(Set::size)
					.summaryStatistics();
			final int min = summary.getMin();
			final int max = summary.getMax();
			if (0 == min) {
				// no FV for the current template in at least one rule
				continue;
			}
			if (1 == max) {
				// every rule contains exactly one fv for the current template
				subsets.computeIfAbsent(template, newIdentityHashSet()).add(
						Collections.singletonList(new FactVariableSubSet(Maps.transformValues(templateToMap.getValue(),
								l -> l.iterator().next()))));
				continue;
			}
			final List<Set<List<SingleFactVariable>>> generators =
					templateToMap
					.getValue()
					.values()
					.stream()
					.map(fvs -> {
						final List<ICombinatoricsVector<SingleFactVariable>> allChosen =
								Factory.createSimpleCombinationGenerator(Factory.createVector(fvs), min)
								.generateAllObjects();
						final Set<List<SingleFactVariable>> converted =
								allChosen.stream().map(ImmutableList::copyOf)
								.flatMap(list -> Collections2.permutations(list).stream())
								.collect(toIdentityHashSet());
						return converted;
					}).collect(toList());

			final Set<Map<Integer, Map<Either<Rule, ExistentialProxy>, SingleFactVariable>>> listOfMaps =
					Sets.newIdentityHashSet();
			for (final List<List<SingleFactVariable>> list : Sets.cartesianProduct(generators)) {
				final Map<Integer, Map<Either<Rule, ExistentialProxy>, SingleFactVariable>> currentMaps =
						new TreeMap<>();
				// every vector contains $min$ fvs corresponding to the same rule
				for (final List<SingleFactVariable> vector : list) {
					for (int i = 0; i < min; ++i) {
						final SingleFactVariable value = vector.get(i);
						final Either<Rule, ExistentialProxy> rule = fvToRule.get(value);
						currentMaps.computeIfAbsent(i, newIdentityHashMap()).put(rule, value);
					}
				}
				listOfMaps.add(currentMaps);
			}
			final Set<List<FactVariableSubSet>> targetSubSets = subsets.computeIfAbsent(template, newIdentityHashSet());
			for (final Map<Integer, Map<Either<Rule, ExistentialProxy>, SingleFactVariable>> maps : listOfMaps) {
				targetSubSets.add(maps.values().stream().map(FactVariableSubSet::new).collect(toList()));
			}
		}
		final List<FactVariablePartition> partitions = new ArrayList<>();
		// list of possible combinations
		for (final List<List<FactVariableSubSet>> factVariablePartitions : Sets.cartesianProduct(ImmutableList
				.copyOf(subsets.values()))) {
			final FactVariablePartition partition = new FactVariablePartition();
			// list of subsets per template
			for (final List<FactVariableSubSet> list : factVariablePartitions) {
				// list of subsets of the same template
				for (final FactVariableSubSet factVariableSubSet : list) {
					partition.add(factVariableSubSet);
				}
			}
			partitions.add(partition);
		}
		return partitions;
	}

	protected static interface VerticalInner<T extends FilterInstance> {
		public void apply(final Set<Either<Rule, ExistentialProxy>> rules, final List<T> filterInstances,
				final List<FactVariablePartition> partitions, final ECBlockSet resultBlocks);
	}

	protected static void implicitElementVerticalInner(final Set<Either<Rule, ExistentialProxy>> rules,
			final List<ImplicitElementFilterInstance> filterInstances, final List<FactVariablePartition> partitions,
			final ECBlockSet resultBlocks) {
		final SubSet<Element> leftESS =
				new SubSet<>(filterInstances.stream().collect(
						toMap(FilterInstance::getRuleOrProxy, ImplicitElementFilterInstance::getLeft)));
		final SubSet<Element> rightESS =
				new SubSet<>(filterInstances.stream().collect(
						toMap(FilterInstance::getRuleOrProxy, ImplicitElementFilterInstance::getRight)));
		final ImmutableMap<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> map =
				Maps.uniqueIndex(filterInstances, ImplicitFilterInstance::getRuleOrProxy);
		final FilterInstanceSubSet fiSS = new FilterInstanceSubSet(map);
		final FilterInstanceSubSet fiSSDual =
				new FilterInstanceSubSet(Maps.transformValues(map, ImplicitFilterInstance::getDual));
		for (final FactVariablePartition partition : partitions) {
			if (leftESS.getElements().values().stream().map(Element::getFactVariable)
					.map(fv -> fv == null ? null : partition.lookup(fv)).distinct().count() != 1) {
				continue;
			}
			if (rightESS.getElements().values().stream().map(Element::getFactVariable)
					.map(fv -> fv == null ? null : partition.lookup(fv)).distinct().count() != 1) {
				continue;
			}
			final Block newBlock = new Block(rules, partition);
			newBlock.addElementSubSet(leftESS);
			newBlock.addElementSubSet(rightESS);
			newBlock.addFilterInstanceSubSet(fiSS);
			newBlock.addFilterInstanceSubSet(fiSSDual);
			horizontalRecursion(newBlock, new Stack<>(), resultBlocks);
		}
	}

	protected static void implicitECVerticalInner(final Set<Either<Rule, ExistentialProxy>> rules,
			final List<ImplicitECFilterInstance> filterInstances, final List<FactVariablePartition> partitions,
			final ECBlockSet resultBlocks) {
		final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> ecColumns =
				determineECColumns(filterInstances);
		if (ecColumns.isEmpty())
			return;
		final SubSet<Element> leftESS =
				new SubSet<>(filterInstances.stream().collect(
						toMap(FilterInstance::getRuleOrProxy, ImplicitECFilterInstance::getLeft)));
		final SubSet<Element> rightESS =
				new SubSet<>(filterInstances.stream().collect(
						toMap(FilterInstance::getRuleOrProxy, ImplicitECFilterInstance::getRight)));
		final ImmutableMap<Either<Rule, ExistentialProxy>, ImplicitECFilterInstance> map =
				Maps.uniqueIndex(filterInstances, ImplicitFilterInstance::getRuleOrProxy);
		final FilterInstanceSubSet fiSS = new FilterInstanceSubSet(map);
		final FilterInstanceSubSet fiSSDual =
				new FilterInstanceSubSet(Maps.transformValues(map, ImplicitFilterInstance::getDual));
		for (final FactVariablePartition partition : partitions) {
			final List<List<Map<Either<Rule, ExistentialProxy>, ? extends Element>>> intersections =
					ecColumns.values().stream().map(ecMap -> determineEquivalenceClassIntersection(ecMap, partition))
					.collect(toList());
			if (intersections.stream().anyMatch(List::isEmpty)) {
				continue;
			}
			final Block newBlock = new Block(rules, partition);
			for (final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> intersection : intersections) {
				for (final Map<Either<Rule, ExistentialProxy>, ? extends Element> ess : intersection) {
					newBlock.addElementSubSet(new SubSet<>(ess));
				}
			}
			newBlock.addVariableExpressionSubSet(leftESS);
			newBlock.addVariableExpressionSubSet(rightESS);
			newBlock.addFilterInstanceSubSet(fiSS);
			newBlock.addFilterInstanceSubSet(fiSSDual);
			horizontalRecursion(newBlock, new Stack<>(), resultBlocks);
		}
	}

	protected static void explicitVerticalInner(final Set<Either<Rule, ExistentialProxy>> rules,
			final List<ExplicitFilterInstance> filterInstances, final List<FactVariablePartition> partitions,
			final ECBlockSet resultBlocks) {
		final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> ecColumns =
				determineECColumns(filterInstances);
		if (ecColumns.isEmpty())
			return;
		final FilterInstanceSubSet fiSS =
				new FilterInstanceSubSet(Maps.uniqueIndex(filterInstances, FilterInstance::getRuleOrProxy));
		for (final FactVariablePartition partition : partitions) {
			final List<List<Map<Either<Rule, ExistentialProxy>, ? extends Element>>> intersections =
					ecColumns.values().stream().map(ecMap -> determineEquivalenceClassIntersection(ecMap, partition))
					.collect(toList());
			if (intersections.stream().anyMatch(List::isEmpty)) {
				continue;
			}
			final Block newBlock = new Block(rules, partition);
			for (final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> intersection : intersections) {
				if (intersection.size() > 1) {
					// determine the implicit filters for each intersection
					addImplicitFiltersForIntersections(newBlock, intersection);
				}
				for (final Map<Either<Rule, ExistentialProxy>, ? extends Element> ess : intersection) {
					newBlock.addElementSubSet(new SubSet<>(ess));
				}
			}
			newBlock.addFilterInstanceSubSet(fiSS);
			horizontalRecursion(newBlock, new Stack<>(), resultBlocks);
		}
	}

	private static void addImplicitFiltersForIntersections(final Block block,
			final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> intersection) {
		try {
			for (int i = 0; i < intersection.size(); ++i) {
				final Map<Either<Rule, ExistentialProxy>, ? extends Element> left = intersection.get(i);
				for (int j = i + 1; j < intersection.size(); ++j) {
					final Map<Either<Rule, ExistentialProxy>, ? extends Element> right = intersection.get(j);
					block.addFilterInstanceSubSet(new FilterInstanceSubSet(Maps.asMap(block.getRulesOrProxies(),
							rule -> Filter.newEqualityFilter(left.get(rule), right.get(rule))
							.getImplicitElementInstances(rule).iterator().next())));
					block.addFilterInstanceSubSet(new FilterInstanceSubSet(Maps.asMap(block.getRulesOrProxies(),
							rule -> Filter.newEqualityFilter(right.get(rule), left.get(rule))
							.getImplicitElementInstances(rule).iterator().next())));
				}
			}
		} catch (final NoSuchElementException ex) {
			throw new IllegalStateException(
					"The intersections promised elements that were not found in the implicit filters!");
		}
	}

	protected static Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> determineECColumns(
			final List<? extends FilterInstance> filterInstances) {
		final Set<List<Integer>> ecPatterns = filterInstances.stream().map(ECBlocks::computeECPattern).collect(toSet());
		if (ecPatterns.size() > 1) {
			// different patterns
			return Collections.emptyMap();
		}
		final HashSet<Integer> differentECs = Sets.newHashSet(ecPatterns.iterator().next());
		if (differentECs.isEmpty()) {
			// no ECs at all
			return Collections.emptyMap();
		}

		// create the EC columns
		final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> ecColumns = new HashMap<>();
		for (final FilterInstance filterInstance : filterInstances) {
			final Either<Rule, ExistentialProxy> rule = filterInstance.getRuleOrProxy();
			final List<EquivalenceClass> ecs = filterInstance.getDirectlyContainedEquivalenceClasses();
			for (final Integer i : differentECs) {
				ecColumns.computeIfAbsent(i, newIdentityHashMap()).put(rule, ecs.get(i));
			}
		}
		return ecColumns;
	}

	protected static <T extends FilterInstance> void vertical(final Set<Set<T>> filterInstancesGroupedByRule,
			final ECBlockSet resultBlocks, final VerticalInner<T> verticalInner) {
		final Set<Set<Set<T>>> filterInstancesPowerSet = Sets.powerSet(filterInstancesGroupedByRule);
		final Iterator<Set<Set<T>>> iterator = filterInstancesPowerSet.iterator();
		// skip empty set
		iterator.next();
		while (iterator.hasNext()) {
			final ImmutableList<Set<T>> powerSetElement = ImmutableList.copyOf(iterator.next());
			final Set<Either<Rule, ExistentialProxy>> rules =
					powerSetElement.stream().map(set -> set.iterator().next().getRuleOrProxy()).collect(toSet());
			final List<FactVariablePartition> partitions = enumerateFactVariablePartitions(rules);
			final Set<List<T>> cartesianProduct = Sets.cartesianProduct(powerSetElement);
			for (final List<T> filterInstances : cartesianProduct) {
				verticalInner.apply(rules, filterInstances, partitions, resultBlocks);
			}
		}
	}

	protected static void horizontalRecursion(final Block block, final Stack<Set<FilterInstance>> exclusionStack,
			final ECBlockSet resultBlocks) {
		// needed: the filters that are contained in every rule of the block, where for every
		// filter it is the case that: every rule contains at least one instance not already
		// excluded by the exclusion stack
		// thus: get the non-excluded filter instances
		final Set<FilterInstance> neighbours =
				block.getConflictNeighbours().stream()
				.filter(fi -> !exclusionStack.stream().anyMatch(as -> as.contains(fi))).collect(toSet());
		if (neighbours.isEmpty()) {
			resultBlocks.addDuringHorizontalRecursion(block);
			return;
		}
		// group them by their filter
		final Map<Filter, List<FilterInstance>> nFilterToInstances =
				neighbours.stream().collect(groupingBy(FilterInstance::getFilter));
		// get all the rules in the block
		final Set<Either<Rule, ExistentialProxy>> bRules = block.getRulesOrProxies();
		// get a map from filter to all rules containing instances of that filter
		final Map<Filter, Set<Either<Rule, ExistentialProxy>>> nFilterToRulesContainingIt =
				nFilterToInstances
				.entrySet()
				.stream()
				.collect(
						toMap(Entry::getKey, e -> e.getValue().stream().map(FilterInstance::getRuleOrProxy)
								.collect(toSet())));
		// get the filters that are contained in every rule
		final Set<Filter> nRelevantFilters =
				nFilterToInstances.keySet().stream().filter(f -> nFilterToRulesContainingIt.get(f).containsAll(bRules))
				.collect(toSet());
		// if no filters are left to add, the block is horizontally maximized, add it
		if (nRelevantFilters.isEmpty()) {
			resultBlocks.addDuringHorizontalRecursion(block);
			return;
		}

		final FilterInstanceTypePartitioner nTypePartition =
				FilterInstanceTypePartitioner.partition(iterable(nRelevantFilters.stream().flatMap(
						f -> nFilterToInstances.get(f).stream())));
		final Map<Filter, List<ExplicitFilterInstance>> nRelevantFilterToExplicitInstances =
				nTypePartition.getExplicitFilterInstances().stream().collect(groupingBy(FilterInstance::getFilter));
		final List<ImplicitElementFilterInstance> nRelevantImplicitElementFilterInstances =
				nTypePartition.getImplicitElementFilterInstances();
		final List<ImplicitECFilterInstance> nRelevantImplicitECFilterInstances =
				nTypePartition.getImplicitECFilterInstances();

		// divide into filters without multiple instances and filters with multiple instances
		final List<Filter> nSingleCellFilters, nMultiCellFilters;
		{
			final Map<Boolean, List<Filter>> partition =
					nRelevantFilterToExplicitInstances
					.entrySet()
					.stream()
					.collect(
							partitioningBy(e -> e.getValue().size() > bRules.size(),
									mapping(Entry::getKey, toList())));
			nSingleCellFilters = partition.get(Boolean.FALSE);
			nMultiCellFilters = partition.get(Boolean.TRUE);
		}
		// list of rule-filter-matchings that may be added
		final List<Pair<Block, List<FilterInstance>>> matchingFilters = new ArrayList<>();
		final List<FilterInstance> incompatibleFilters = new ArrayList<>();

		if (!nRelevantImplicitElementFilterInstances.isEmpty()) {
			findMatchingImplicitElementFilters(nRelevantImplicitElementFilterInstances, block, matchingFilters);
		}
		// prefer singleCellFilters
		if (!nSingleCellFilters.isEmpty()) {
			findMatchingAndIncompatibleExplicitFilters(nRelevantFilterToExplicitInstances, nSingleCellFilters, block,
					matchingFilters, incompatibleFilters);
		}
		if (!nRelevantImplicitECFilterInstances.isEmpty()) {
			findMatchingAndIncompatibleImplicitECFilters(nRelevantImplicitECFilterInstances, block, matchingFilters,
					incompatibleFilters);
		}
		// if none matched, try multiCellFilters, otherwise defer them
		if (matchingFilters.isEmpty()) {
			if (!nMultiCellFilters.isEmpty()) {
				findMatchingAndIncompatibleExplicitFilters(nRelevantFilterToExplicitInstances, nMultiCellFilters,
						block, matchingFilters, incompatibleFilters);
			}
			// if still none matched, the block is maximal, add it to the result blocks
			if (matchingFilters.isEmpty()) {
				resultBlocks.addDuringHorizontalRecursion(block);
				return;
			}
		}

		// create the next exclusion layer
		final Set<FilterInstance> furtherExcludes = new HashSet<>();
		// add it to the stack
		exclusionStack.push(furtherExcludes);
		// add all incompatible filter instances to the exclusion stack
		furtherExcludes.addAll(incompatibleFilters);
		// for every matching filter instance set, create a new block
		for (final Pair<Block, List<FilterInstance>> match : matchingFilters) {
			final Block newBlock = match.getLeft();
			// recurse for that block
			horizontalRecursion(newBlock, exclusionStack, resultBlocks);
			// after the recursion, exclude all filter instances just used
			furtherExcludes.addAll(match.getRight());
		}
		// eliminate top layer of the exclusion stack
		exclusionStack.pop();
	}

	protected static void findMatchingAndIncompatibleExplicitFilters(
			final Map<Filter, List<ExplicitFilterInstance>> nFilterToInstances, final List<Filter> nFilters,
			final Block block, final List<Pair<Block, List<FilterInstance>>> matchingFilters,
			final List<FilterInstance> incompatibleFilters) {
		final FilterInstancePartition bFIPartition = block.filterInstancePartition;
		final FactVariablePartition bFactVariablePartition = block.factVariablePartition;
		// iterate over every single-/multi-cell filter and check that its instances have the same
		// conflicts in every rule
		for (final Filter nFilter : nFilters) {
			boolean matchingConstellationFound = false;

			// iterate over the possible mappings: (filter,rule) -> filter instance
			final List<Set<ExplicitFilterInstance>> nListOfRelevantFilterInstancesGroupedByRule =
					new ArrayList<>(nFilterToInstances.get(nFilter).stream()
							.collect(groupingBy(FilterInstance::getRuleOrProxy, toSet())).values());

			// create the cartesian product
			final Set<List<FilterInstance>> nRelevantFilterInstanceCombinations =
					Sets.cartesianProduct(nListOfRelevantFilterInstancesGroupedByRule);
			// iterate over the possible filter instance combinations
			cartesianProductLoop: for (final List<FilterInstance> nCurrentOutsideColumn : nRelevantFilterInstanceCombinations) {
				// create a map for faster lookup: rule -> filter instance (outside)
				final Map<Either<Rule, ExistentialProxy>, FilterInstance> nRuleToCurrentOutsideColumn =
						Maps.uniqueIndex(nCurrentOutsideColumn, FilterInstance::getRuleOrProxy);

				// create the EC index pattern
				final HashSet<List<Integer>> ecPatterns =
						nCurrentOutsideColumn.stream().map(ECBlocks::computeECPattern)
						.collect(toCollection(HashSet::new));
				// if different patterns, disregard combination
				if (ecPatterns.size() > 1) {
					continue cartesianProductLoop;
				}
				final HashSet<Integer> differentECs = Sets.newHashSet(ecPatterns.iterator().next());

				// create the EC columns
				final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> ecColumns = new HashMap<>();
				for (final FilterInstance filterInstance : nCurrentOutsideColumn) {
					final Either<Rule, ExistentialProxy> rule = filterInstance.getRuleOrProxy();
					final List<EquivalenceClass> ecs = filterInstance.getDirectlyContainedEquivalenceClasses();
					for (final Integer i : differentECs) {
						final EquivalenceClass ec = ecs.get(i);
						// if old don't consider EC
						if (block.theta.getEquivalenceClasses().contains(ec)) {
							continue;
						}
						ecColumns.computeIfAbsent(i, newIdentityHashMap()).put(rule, ec);
					}
				}
				if (ecColumns.values().stream()
						.anyMatch(map -> map.keySet().size() != block.getRulesOrProxies().size())) {
					// the EC pattern may have been identical, but they differ in being new and old
					continue cartesianProductLoop;
				}
				final List<List<Map<Either<Rule, ExistentialProxy>, ? extends Element>>> intersections =
						ecColumns.values().stream()
						.map(ecMap -> determineEquivalenceClassIntersection(ecMap, bFactVariablePartition))
						.collect(toList());
				// if any intersection is empty, disregard combination
				if (intersections.stream().anyMatch(List::isEmpty)) {
					continue cartesianProductLoop;
				}
				// introduce the intersections into the block
				// create a new block first
				final Block newBlock = new Block(block);
				// add everything to the element partition and the theta
				for (final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> intersection : intersections) {
					for (final Map<Either<Rule, ExistentialProxy>, ? extends Element> subset : intersection) {
						newBlock.addElementSubSet(new SubSet<>(subset));
					}
				}

				if (!checkForConflictEquivalence(bFIPartition, nRuleToCurrentOutsideColumn, newBlock)) {
					continue cartesianProductLoop;
				}

				// conflict identical for all rules
				newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(Maps.newHashMap(nRuleToCurrentOutsideColumn)));
				matchingFilters.add(Pair.of(newBlock, nCurrentOutsideColumn));
				matchingConstellationFound = true;
			}
			if (!matchingConstellationFound) {
				incompatibleFilters.addAll(nFilterToInstances.get(nFilter));
			}
		}
	}

	protected static boolean checkForConflictEquivalence(final FilterInstancePartition bFIPartition,
			final Map<Either<Rule, ExistentialProxy>, ? extends FilterInstance> nFISubSet, final Block newBlock) {
		// iterate over the block columns
		for (final FilterInstanceSubSet bFilterInstanceSubSet : bFIPartition.getElements()) {
			Conflict firstConflict = null;
			boolean first = true;
			// iterate over the rows aka the rules
			for (final Entry<Either<Rule, ExistentialProxy>, FilterInstance> bRuleToFI : bFilterInstanceSubSet
					.getElements().entrySet()) {
				final Either<Rule, ExistentialProxy> rule = bRuleToFI.getKey();
				final FilterInstance nSource = nFISubSet.get(rule);
				final FilterInstance bTarget = bRuleToFI.getValue();
				// determine conflict between inside instance and outside instance
				final Conflict conflict = nSource.getConflict(bTarget, newBlock.theta, newBlock.theta);
				// if this is the first loop iteration, just remember the conflict to be
				// compared later on
				if (first) {
					first = false;
					firstConflict = conflict;
				}
				// if the conflicts don't match, continue with next filter
				else if (!hasEqualConflicts(firstConflict, conflict)) {
					return false;
				}
			}
		}
		return true;
	}

	protected static void findMatchingImplicitElementFilters(
			final List<ImplicitElementFilterInstance> nImplicitElementFilterInstances, final Block block,
			final List<Pair<Block, List<FilterInstance>>> matchingFilters) {
		final FactVariablePartition factVariablePartition = block.factVariablePartition;
		final ElementPartition bElementPartition = block.elementPartition;
		final Set<Either<Rule, ExistentialProxy>> rules = block.rulesOrProxies;

		final Set<ImplicitElementFilterInstance> workspace = Sets.newIdentityHashSet();
		workspace.addAll(nImplicitElementFilterInstances);
		final Map<Either<Rule, ExistentialProxy>, Map<Filter, Set<ImplicitElementFilterInstance>>> workspaceByRule =
				workspace.stream().collect(
						groupingBy(FilterInstance::getRuleOrProxy,
								groupingBy(FilterInstance::getFilter, toCollection(() -> Sets.newIdentityHashSet()))));

		final Consumer<ImplicitElementFilterInstance> remove = (fi) -> {
			if (workspace.remove(fi)) {
				workspaceByRule.get(fi.getRuleOrProxy()).get(fi.getFilter()).remove(fi);
			}
		};

		workspaceLoop: while (!workspace.isEmpty()) {
			final ImplicitElementFilterInstance nCurrentFI = workspace.iterator().next();
			final Filter nCurrentFilter = nCurrentFI.getFilter();
			remove.accept(nCurrentFI);
			final Function<ImplicitElementFilterInstance, Element> getBElement, getNElement;
			final BiFunction<Element, Element, Filter> bnToFilter;
			{
				final Element left = nCurrentFI.getLeft();
				final Element right = nCurrentFI.getRight();
				final boolean leftElementInBlock = block.theta.isRelevant(left);
				final boolean rightElementInBlock = block.theta.isRelevant(right);
				assert left.getEquivalenceClass() == right.getEquivalenceClass();
				final boolean ecInBlock = block.theta.isRelevant(left.getEquivalenceClass());

				if (leftElementInBlock && rightElementInBlock) {
					throw new IllegalStateException(
							"A test comparing two elements of an equivalence class is to be considered, but both elements already are in the effective equivalence class of the block!");
				}
				if (!ecInBlock) {
					// add left = right and right = left for every rule => done

					// create a new block
					final Block newBlock = new Block(block);
					final List<FilterInstance> filterInstances = new ArrayList<>();
					if (!getFirstColumnAndDualsForTwoNewElements(factVariablePartition, rules, workspaceByRule, remove,
							nCurrentFI, newBlock, filterInstances)) {
						continue;
					}

					// add to matching filters list and continue
					matchingFilters.add(Pair.of(newBlock, filterInstances));
					continue;
				}
				// ec already in block

				if (leftElementInBlock) {
					// => right is new
					getBElement = ImplicitElementFilterInstance::getLeft;
					getNElement = ImplicitElementFilterInstance::getRight;
					bnToFilter = (b, n) -> Filter.newEqualityFilter(b, n);
				} else if (rightElementInBlock) {
					// => left is new
					getBElement = ImplicitElementFilterInstance::getRight;
					getNElement = ImplicitElementFilterInstance::getLeft;
					bnToFilter = (b, n) -> Filter.newEqualityFilter(n, b);
				} else {
					// ec already in the block but none of the arguments is in the element partition
					// => add all tests comparing old = left and old = right and left = old and
					// right = old for every old and every rule

					// create a new block
					final Block newBlock = new Block(block);
					final List<FilterInstance> filterInstances = new ArrayList<>();
					if (!getFirstColumnAndDualsForTwoNewElements(factVariablePartition, rules, workspaceByRule, remove,
							nCurrentFI, newBlock, filterInstances)) {
						continue workspaceLoop;
					}

					final Map<Either<Rule, ExistentialProxy>, Element> leftColumn, rightColumn;
					{
						final IdentityHashMap<Either<Rule, ExistentialProxy>, FilterInstance> column =
								newBlock.filterInstancePartition.lookup(nCurrentFI).getElements();
						leftColumn = Maps.transformValues(column, fi -> ((ImplicitElementFilterInstance) fi).getLeft());
						rightColumn =
								Maps.transformValues(column, fi -> ((ImplicitElementFilterInstance) fi).getRight());
					}

					// find the tests for all the other elements in theta(bElement.getEC())
					final Set<Element> representativesOfTheOld = block.theta.reduce(left.getEquivalenceClass());
					for (final Element representative : representativesOfTheOld) {
						final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> columnForLeft =
								new IdentityHashMap<>();
						final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> columnForRight =
								new IdentityHashMap<>();
						final SubSet<Element> representativeSubSet = bElementPartition.lookup(representative);
						for (final Either<Rule, ExistentialProxy> rule : rules) {
							final Element currentOElement = representativeSubSet.get(rule);
							{
								final Element currentLElement = leftColumn.get(rule);
								final Filter leftFilter = Filter.newEqualityFilter(currentOElement, currentLElement);
								final ImplicitElementFilterInstance matchingLeftInstance =
										getMatchingFilterInstanceForRule(factVariablePartition, bElementPartition,
												workspaceByRule, leftFilter, ImplicitElementFilterInstance::getLeft,
												ImplicitElementFilterInstance::getRight, currentOElement,
												currentLElement, rule);
								if (null == matchingLeftInstance) {
									throw new IllegalStateException("Inconsistent!");
								}
								columnForLeft.put(rule, matchingLeftInstance);
								remove.accept(matchingLeftInstance);
							}
							{
								final Element currentRElement = rightColumn.get(rule);
								final Filter rightFilter = Filter.newEqualityFilter(currentOElement, currentRElement);
								final ImplicitElementFilterInstance matchingRightInstance =
										getMatchingFilterInstanceForRule(factVariablePartition, bElementPartition,
												workspaceByRule, rightFilter, ImplicitElementFilterInstance::getLeft,
												ImplicitElementFilterInstance::getRight, currentOElement,
												currentRElement, rule);
								if (null == matchingRightInstance) {
									throw new IllegalStateException("Inconsistent!");
								}
								columnForRight.put(rule, matchingRightInstance);
								remove.accept(matchingRightInstance);
							}
						}
						// add the newly found filter instances
						newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnForLeft));
						newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnForRight));
						filterInstances.addAll(columnForLeft.values());
						filterInstances.addAll(columnForRight.values());
						// get the corresponding dual filter instances ...
						final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> columnForLeftDuals =
								Maps.transformValues(columnForLeft, ImplicitElementFilterInstance::getDual);
						final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> columnForRightDuals =
								Maps.transformValues(columnForRight, ImplicitElementFilterInstance::getDual);
						// ... add them to the new block ...
						newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnForLeftDuals));
						newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnForRightDuals));
						filterInstances.addAll(columnForLeftDuals.values());
						filterInstances.addAll(columnForRightDuals.values());
						// ... and remove them from the workspace
						columnForLeftDuals.values().forEach(remove);
						columnForRightDuals.values().forEach(remove);
					}

					// add to matching filters list and continue
					matchingFilters.add(Pair.of(newBlock, filterInstances));
					continue;
				}
			}

			final Element firstNElement = getNElement.apply(nCurrentFI);
			final Element firstBElement = getBElement.apply(nCurrentFI);
			final Either<Rule, ExistentialProxy> currentRule = nCurrentFI.getRuleOrProxy();
			final SetView<Either<Rule, ExistentialProxy>> otherRules =
					Sets.difference(rules, Collections.singleton(currentRule));
			final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> firstFIColumn =
					new IdentityHashMap<>();
			firstFIColumn.put(currentRule, nCurrentFI);
			for (final Either<Rule, ExistentialProxy> rule : otherRules) {
				final ImplicitElementFilterInstance matchingInstance =
						getMatchingFilterInstanceForRule(factVariablePartition, bElementPartition, workspaceByRule,
								nCurrentFilter, getBElement, getNElement, firstBElement, firstNElement, rule);
				if (null == matchingInstance) {
					continue workspaceLoop;
				}
				firstFIColumn.put(rule, matchingInstance);
				remove.accept(matchingInstance);
			}
			// now we know there is an equivalent filter instance for every rule
			final Map<Either<Rule, ExistentialProxy>, Element> ruleToNElement =
					Maps.transformValues(firstFIColumn, getNElement::apply);

			// create a new block
			final Block newBlock = new Block(block);
			final List<FilterInstance> filterInstances = new ArrayList<>();
			// extend the element partition and the theta
			newBlock.addElementSubSet(new SubSet<>(ruleToNElement));
			// add the first column we already found ...
			newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(firstFIColumn));
			// (also to the list of filter instances)
			filterInstances.addAll(firstFIColumn.values());
			// ... and their dual filter instances ...
			final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> firstFIColumnDuals =
					Maps.transformValues(firstFIColumn, ImplicitElementFilterInstance::getDual);
			newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(firstFIColumnDuals));
			// (same as above)
			filterInstances.addAll(firstFIColumnDuals.values());
			// ... which also have to be removed from the workspace
			firstFIColumnDuals.values().forEach(remove);

			// since the list of ElementSubSets is the same for all rules, we can iterate over them
			// and inside over the rules to get a list of FilterInstanceSubSets

			// find the tests for all the other elements in theta(bElement.getEC())
			final Set<Element> representativesOfTheRest =
					Sets.difference(block.theta.reduce(firstBElement.getEquivalenceClass()),
							Collections.singleton(firstBElement));
			for (final Element representative : representativesOfTheRest) {
				final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> column =
						new IdentityHashMap<>();
				final SubSet<Element> representativeSubSet = bElementPartition.lookup(representative);
				for (final Either<Rule, ExistentialProxy> rule : rules) {
					final Element currentBElement = representativeSubSet.get(rule);
					final Element currentNElement = ruleToNElement.get(rule);
					final Filter filter = bnToFilter.apply(currentBElement, currentNElement);
					final ImplicitElementFilterInstance matchingInstance =
							getMatchingFilterInstanceForRule(factVariablePartition, bElementPartition, workspaceByRule,
									filter, getBElement, getNElement, currentBElement, currentNElement, rule);
					if (null == matchingInstance) {
						throw new IllegalStateException("Inconsistent!");
					}
					column.put(rule, matchingInstance);
					remove.accept(matchingInstance);
				}
				// add the newly found filter instances
				newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(column));
				filterInstances.addAll(column.values());
				// get the corresponding dual filter instances ...
				final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> columnDuals =
						Maps.transformValues(column, ImplicitElementFilterInstance::getDual);
				// ... add them to the new block ...
				newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnDuals));
				filterInstances.addAll(columnDuals.values());
				// ... and remove them from the workspace
				columnDuals.values().forEach(remove);
			}
			matchingFilters.add(Pair.of(newBlock, filterInstances));
		}
	}

	private static boolean getFirstColumnAndDualsForTwoNewElements(final FactVariablePartition factVariablePartition,
			final Set<Either<Rule, ExistentialProxy>> rules,
			final Map<Either<Rule, ExistentialProxy>, Map<Filter, Set<ImplicitElementFilterInstance>>> workspaceByRule,
			final Consumer<ImplicitElementFilterInstance> remove, final ImplicitElementFilterInstance nCurrentFI,
			final Block newBlock, final List<FilterInstance> filterInstances) {
		final Either<Rule, ExistentialProxy> currentRule = nCurrentFI.getRuleOrProxy();
		final SetView<Either<Rule, ExistentialProxy>> otherRules =
				Sets.difference(rules, Collections.singleton(currentRule));
		final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> column = new IdentityHashMap<>();
		column.put(currentRule, nCurrentFI);
		for (final Either<Rule, ExistentialProxy> rule : otherRules) {
			final ImplicitElementFilterInstance matchingInstance =
					getMatchingFilterInstanceForRuleBothArgsNew(factVariablePartition, workspaceByRule, nCurrentFI,
							rule);
			if (null == matchingInstance) {
				return false;
			}
			column.put(rule, matchingInstance);
			remove.accept(matchingInstance);
		}
		// extend the element partition and the theta
		newBlock.addElementSubSet(new SubSet<>(Maps.transformValues(column, ImplicitElementFilterInstance::getLeft)));
		newBlock.addElementSubSet(new SubSet<>(Maps.transformValues(column, ImplicitElementFilterInstance::getRight)));
		// add the first column we already found ...
		newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(column));
		// (also to the list of filter instances)
		filterInstances.addAll(column.values());
		// ... and their dual filter instances ...
		final Map<Either<Rule, ExistentialProxy>, ImplicitElementFilterInstance> columnDuals =
				Maps.transformValues(column, ImplicitElementFilterInstance::getDual);
		newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnDuals));
		// (same as above)
		filterInstances.addAll(columnDuals.values());
		// ... which also have to be removed from the workspace
		columnDuals.values().forEach(remove);
		return true;
	}

	protected static ImplicitElementFilterInstance getMatchingFilterInstanceForRule(
			final FactVariablePartition factVariablePartition, final ElementPartition bElementPartition,
			final Map<Either<Rule, ExistentialProxy>, Map<Filter, Set<ImplicitElementFilterInstance>>> workspaceByRule,
			final Filter nCurrentFilter, final Function<ImplicitElementFilterInstance, Element> getBElement,
			final Function<ImplicitElementFilterInstance, Element> getNElement, final Element bElement,
			final Element nElement, final Either<Rule, ExistentialProxy> rule) {
		for (final ImplicitElementFilterInstance candidate : workspaceByRule.getOrDefault(rule, Collections.emptyMap())
				.getOrDefault(nCurrentFilter, Collections.emptySet())) {
			if (bElementPartition.lookup(bElement) != bElementPartition.lookup(getBElement.apply(candidate))) {
				continue;
			}
			if (!checkSameFactBinding(factVariablePartition, getNElement, nElement, candidate)) {
				continue;
			}
			return candidate;
		}
		for (final ImplicitElementFilterInstance candidate : nCurrentFilter.getImplicitElementInstances(rule)) {
			if (bElementPartition.lookup(bElement) != bElementPartition.lookup(getBElement.apply(candidate))) {
				continue;
			}
			if (!checkSameFactBinding(factVariablePartition, getNElement, nElement, candidate)) {
				continue;
			}
			return candidate;
		}
		return null;
	}

	protected static ImplicitElementFilterInstance getMatchingFilterInstanceForRuleBothArgsNew(
			final FactVariablePartition factVariablePartition,
			final Map<Either<Rule, ExistentialProxy>, Map<Filter, Set<ImplicitElementFilterInstance>>> workspaceByRule,
			final ImplicitElementFilterInstance nCurrentFI, final Either<Rule, ExistentialProxy> rule) {
		final Set<ImplicitElementFilterInstance> candidates =
				workspaceByRule.getOrDefault(rule, Collections.emptyMap()).getOrDefault(nCurrentFI.getFilter(),
						Collections.emptySet());
		for (final ImplicitElementFilterInstance candidate : candidates) {
			if (!checkSameFactBinding(factVariablePartition, ImplicitElementFilterInstance::getLeft,
					nCurrentFI.getLeft(), candidate)) {
				continue;
			}
			if (!checkSameFactBinding(factVariablePartition, ImplicitElementFilterInstance::getRight,
					nCurrentFI.getRight(), candidate)) {
				continue;
			}
			return candidate;
		}
		return null;
	}

	protected static boolean checkSameFactBinding(final FactVariablePartition factVariablePartition,
			final Function<ImplicitElementFilterInstance, Element> getXElement, final Element xElement,
			final ImplicitElementFilterInstance candidate) {
		final Element candidateXElement = getXElement.apply(candidate);
		final SingleFactVariable candidateXFactVariable = candidateXElement.getFactVariable();
		if (null == candidateXFactVariable) {
			// constant binding
			assert null == xElement.getFactVariable();
			// equality of constant has been checked by Filter
			return true;
		}
		// both are a slot binding to same slot (same TemplateSlotLeaf) or fact
		// binding to a fact variable of identical template, checked by Filter
		assert null != xElement.getFactVariable();
		if (factVariablePartition.lookup(candidateXFactVariable) != factVariablePartition.lookup(xElement
				.getFactVariable())) {
			// fact variable partition not the same for the arguments
			return false;
		}
		return true;
	}

	protected static void findMatchingAndIncompatibleImplicitECFilters(
			final List<ImplicitECFilterInstance> nImplicitECFilterInstances, final Block block,
			final List<Pair<Block, List<FilterInstance>>> matchingFilters,
			final List<FilterInstance> incompatibleFilters) {
		final FactVariablePartition factVariablePartition = block.factVariablePartition;
		final ElementPartition bElementPartition = block.elementPartition;
		final Set<Either<Rule, ExistentialProxy>> rules = block.rulesOrProxies;
		final FilterInstancePartition bFIPartition = block.filterInstancePartition;

		final Set<ImplicitECFilterInstance> workspace = Sets.newIdentityHashSet();
		workspace.addAll(nImplicitECFilterInstances);
		final Map<Either<Rule, ExistentialProxy>, Map<Filter, Set<ImplicitECFilterInstance>>> workspaceByRule =
				workspace.stream().collect(
						groupingBy(FilterInstance::getRuleOrProxy,
								groupingBy(FilterInstance::getFilter, toCollection(() -> Sets.newIdentityHashSet()))));

		final Consumer<ImplicitECFilterInstance> remove = (fi) -> {
			workspace.remove(fi);
			workspaceByRule.get(fi.getRuleOrProxy()).get(fi.getFilter()).remove(fi);
		};

		workspaceLoop: while (!workspace.isEmpty()) {
			final List<Integer> nECPattern;
			final List<Set<ImplicitECFilterInstance>> filterInstancesWithIdenticalPattern;
			{
				final ImplicitECFilterInstance nFirstFI = workspace.iterator().next();
				final Filter nCurrentFilter = nFirstFI.getFilter();
				remove.accept(nFirstFI);

				nECPattern = computeECPattern(nFirstFI);
				filterInstancesWithIdenticalPattern =
						rules.stream()
						.map(rule -> workspaceByRule.getOrDefault(rule, Collections.emptyMap())
								.getOrDefault(nCurrentFilter, Collections.emptySet()).stream()
								.filter(fi -> Objects.equals(nECPattern, computeECPattern(fi)))
								.collect(toSet())).collect(toList());
			}
			final Set<List<ImplicitECFilterInstance>> cartesianProduct =
					Sets.cartesianProduct(filterInstancesWithIdenticalPattern);
			if (cartesianProduct.isEmpty()) {
				// at least one of the sets was empty, completely incompatible
				filterInstancesWithIdenticalPattern.forEach(set -> {
					incompatibleFilters.addAll(set);
					set.forEach(remove);
				});
				continue workspaceLoop;
			}
			final Set<Integer> nECIndices = Sets.newHashSet(nECPattern);
			boolean matchingCombinationFound = false;
			combinationLoop: for (final List<ImplicitECFilterInstance> nFICombination : cartesianProduct) {
				final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> indexToECSubSet =
						new TreeMap<>();
				for (final ImplicitECFilterInstance nFI : nFICombination) {
					final Either<Rule, ExistentialProxy> nFIRule = nFI.getRuleOrProxy();
					for (final Integer index : nECIndices) {
						final EquivalenceClass equivalenceClass = nFI.parameters.get(index);
						indexToECSubSet.computeIfAbsent(index, newIdentityHashMap()).put(nFIRule, equivalenceClass);
					}
				}
				final Partition<EquivalenceClass, SubSet<EquivalenceClass>> ecPartition = new Partition<>();
				final Block newBlock = new Block(block);
				for (final Map<Either<Rule, ExistentialProxy>, EquivalenceClass> ecss : indexToECSubSet.values()) {
					ecPartition.add(new SubSet<>(ecss));
					if (newBlock.theta.getEquivalenceClasses().contains(ecss.values().iterator().next()))
						continue;
					final List<Map<Either<Rule, ExistentialProxy>, ? extends Element>> intersections =
							determineEquivalenceClassIntersection(ecss, factVariablePartition);
					if (intersections.isEmpty()) {
						continue combinationLoop;
					}
					intersections.forEach(ess -> newBlock.addElementSubSet(new SubSet<>(ess)));
				}

				final ImmutableMap<Either<Rule, ExistentialProxy>, ImplicitECFilterInstance> nFirstColumn =
						Maps.uniqueIndex(nFICombination, FilterInstance::getRuleOrProxy);
				// check for conflict equivalence between block and nFirstColumn
				if (!checkForConflictEquivalence(bFIPartition, nFirstColumn, newBlock)) {
					continue combinationLoop;
				}

				// add first column to block
				newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(nFirstColumn));

				// start collecting new filter instances
				final List<FilterInstance> filterInstances = new ArrayList<>();
				filterInstances.addAll(nFICombination);
				nFICombination.forEach(remove);

				// gather the duals of the first column
				final Map<Either<Rule, ExistentialProxy>, ImplicitECFilterInstance> nFirstColumnDuals =
						Maps.transformValues(nFirstColumn, ImplicitECFilterInstance::getDual);
				// add them to the block
				newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(nFirstColumnDuals));
				// and to the list
				filterInstances.addAll(nFirstColumnDuals.values());
				nFirstColumnDuals.values().forEach(remove);

				final ImplicitECFilterInstance nChosenFI = nFICombination.get(0);

				final Function<ImplicitECFilterInstance, VariableExpression> getBElement, getNElement;
				final BiFunction<VariableExpression, VariableExpression, Filter> bnToFilter;
				{
					final VariableExpression left = nChosenFI.getLeft();
					final VariableExpression right = nChosenFI.getRight();
					final boolean leftInBlock = block.variableExpressionTheta.isRelevant(left);
					final boolean rightInBlock = block.variableExpressionTheta.isRelevant(right);
					if (leftInBlock && rightInBlock) {
						throw new IllegalStateException(
								"A test comparing two elements of an equivalence class is to be considered, but both elements already are in the effective equivalence class of the block!");
					}
					if (leftInBlock) {
						// => right is new
						getBElement = ImplicitECFilterInstance::getLeft;
						getNElement = ImplicitECFilterInstance::getRight;
						bnToFilter = (b, n) -> Filter.newEqualityFilter(b, n);
					} else if (rightInBlock) {
						// => left is new
						getBElement = ImplicitECFilterInstance::getRight;
						getNElement = ImplicitECFilterInstance::getLeft;
						bnToFilter = (b, n) -> Filter.newEqualityFilter(n, b);
					} else {
						// both are new
						newBlock.addVariableExpressionSubSet(new SubSet<>(Maps.transformValues(nFirstColumn,
								ImplicitECFilterInstance::getLeft)));
						newBlock.addVariableExpressionSubSet(new SubSet<>(Maps.transformValues(nFirstColumn,
								ImplicitECFilterInstance::getRight)));
						matchingFilters.add(Pair.of(newBlock, filterInstances));
						matchingCombinationFound = true;
						continue combinationLoop;
					}
				}

				// since the list of ElementSubSets is the same for all rules, we can iterate over
				// them and inside over the rules to get a list of FilterInstanceSubSets

				// find the tests for all the other elements in theta(bElement.getEC())
				final Map<Either<Rule, ExistentialProxy>, VariableExpression> ruleToNElement =
						Maps.transformValues(nFirstColumn, getNElement::apply);
				final VariableExpression chosenBElement = getBElement.apply(nChosenFI);
				final Set<Element> representativesOfTheRest =
						Sets.difference(block.variableExpressionTheta.reduce(chosenBElement.getEquivalenceClass()),
								Collections.singleton(chosenBElement));
				for (final Element representative : representativesOfTheRest) {
					final Map<Either<Rule, ExistentialProxy>, ImplicitECFilterInstance> column =
							new IdentityHashMap<>();
					final SubSet<Element> representativeSubSet = bElementPartition.lookup(representative);
					for (final Either<Rule, ExistentialProxy> rule : rules) {
						final VariableExpression currentBElement = (VariableExpression) representativeSubSet.get(rule);
						final VariableExpression currentNElement = ruleToNElement.get(rule);
						final Filter filter = bnToFilter.apply(currentBElement, currentNElement);
						final ImplicitECFilterInstance matchingInstance =
								findMatchingFilterInstanceForVariableExpressions(workspaceByRule, getBElement,
										currentBElement, getNElement, currentNElement, rule, filter);
						if (null == matchingInstance) {
							throw new IllegalStateException("Inconsistent!");
						}
						column.put(rule, matchingInstance);
						remove.accept(matchingInstance);
					}
					// add the newly found filter instances
					newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(column));
					filterInstances.addAll(column.values());
					// get the corresponding dual filter instances ...
					final Map<Either<Rule, ExistentialProxy>, ImplicitECFilterInstance> columnDuals =
							Maps.transformValues(column, ImplicitECFilterInstance::getDual);
					// ... add them to the new block ...
					newBlock.addFilterInstanceSubSet(new FilterInstanceSubSet(columnDuals));
					filterInstances.addAll(columnDuals.values());
					// ... and remove them from the workspace
					columnDuals.values().forEach(remove);
				}
				newBlock.addVariableExpressionSubSet(new SubSet<>(ruleToNElement));
				matchingFilters.add(Pair.of(newBlock, filterInstances));
				matchingCombinationFound = true;
			}
			if (!matchingCombinationFound) {
				filterInstancesWithIdenticalPattern.forEach(set -> {
					incompatibleFilters.addAll(set);
					set.forEach(remove);
				});
				continue workspaceLoop;
			}
		}
	}

	protected static ImplicitECFilterInstance findMatchingFilterInstanceForVariableExpressions(
			final Map<Either<Rule, ExistentialProxy>, Map<Filter, Set<ImplicitECFilterInstance>>> workspaceByRule,
			final Function<ImplicitECFilterInstance, VariableExpression> getBElement,
			final VariableExpression currentBElement,
			final Function<ImplicitECFilterInstance, VariableExpression> getNElement,
			final VariableExpression currentNElement, final Either<Rule, ExistentialProxy> rule, final Filter filter) {
		final Set<ImplicitECFilterInstance> candidates =
				workspaceByRule.getOrDefault(rule, Collections.emptyMap()).getOrDefault(filter, Collections.emptySet());
		for (final ImplicitECFilterInstance candidate : candidates) {
			if (getBElement.apply(candidate) != currentBElement)
				continue;
			if (getNElement.apply(candidate) != currentNElement)
				continue;
			return candidate;
		}
		return null;
	}

	static List<Integer> computeECPattern(final FilterInstance instance) {
		final List<EquivalenceClass> ecs = instance.getDirectlyContainedEquivalenceClasses();
		final List<Integer> result = new ArrayList<>();
		final Map<EquivalenceClass, Integer> firstSighting = new IdentityHashMap<>();
		for (int i = 0; i < ecs.size(); i++) {
			final EquivalenceClass equivalenceClass = ecs.get(i);
			final Integer current = Integer.valueOf(i);
			final Integer first = firstSighting.computeIfAbsent(equivalenceClass, x -> current);
			result.add(first);
		}
		return result;
	}

	@Getter
	@Setter
	// no @EqualsAndHashCode so equals will not prevent new edges
	static class ConflictEdge {
		final Conflict a, b;

		public ConflictEdge(final Conflict a, final Conflict b) {
			this.a = a;
			this.b = b;
		}

		public Conflict getForSource(final FilterInstance sourceFilterInstance) {
			assert this.a.getSource() == sourceFilterInstance || this.b.getSource() == sourceFilterInstance;
			return this.a.getSource() == sourceFilterInstance ? this.a : this.b;
		}

		public Conflict getForTarget(final FilterInstance targetFilterInstance) {
			assert this.a.getTarget() == targetFilterInstance || this.b.getTarget() == targetFilterInstance;
			return this.a.getTarget() == targetFilterInstance ? this.a : this.b;
		}

		public static ConflictEdge of(final FilterInstance x, final FilterInstance y, final Theta xTheta,
				final Theta yTheta) {
			final Conflict a = x.getConflict(y, xTheta, yTheta);
			if (null == a)
				return null;
			return new ConflictEdge(a, y.getConflict(x, yTheta, xTheta));
		}

		@RequiredArgsConstructor
		static class ConflictEdgeFactory implements EdgeFactory<FilterInstance, ConflictEdge> {
			final Theta blockTheta;

			@Override
			public ConflictEdge createEdge(final FilterInstance sourceVertex, final FilterInstance targetVertex) {
				return ConflictEdge.of(sourceVertex, targetVertex, this.blockTheta, this.blockTheta);
			}
		}

		public static ConflictEdgeFactory newFactory(final Theta blockTheta) {
			return new ConflictEdgeFactory(blockTheta);
		}
	}

	protected static SetView<FilterInstance> getStableSet(final UndirectedGraph<FilterInstance, ConflictEdge> graph) {
		// could be improved by DOI 10.1137/0206036
		return Sets.difference(graph.vertexSet(), VertexCovers.find2ApproximationCover(graph));
	}
}
