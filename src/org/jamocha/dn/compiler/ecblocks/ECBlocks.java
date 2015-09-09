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
import static org.jamocha.util.Lambdas.newLinkedHashSet;
import static org.jamocha.util.Lambdas.newTreeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
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

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.collections4.list.CursorableLinkedList;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule.ECListRule;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ConflictEdge.ConflictEdgeFactory;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ExplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.FilterInstance.Conflict;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.FilterInstanceVisitor;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ImplicitECFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ImplicitElementFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ImplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Partition.SubSet;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterList;
import org.jamocha.filter.ECFilterList.ECExistentialList;
import org.jamocha.filter.ECFilterList.ECNodeFilterSet;
import org.jamocha.filter.ECFilterList.ECSharedListWrapper;
import org.jamocha.filter.ECFilterList.ECSharedListWrapper.ECSharedList;
import org.jamocha.filter.ECFilterListVisitor;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.filter.ECFilterSetVisitor;
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.function.fwa.TypeLeaf;
import org.jamocha.function.fwatransformer.FWAPathLeafToTypeLeafTranslator;
import org.jamocha.function.fwatransformer.FWASymbolToECTranslator;
import org.jamocha.function.impls.predicates.Equals;
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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
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
	static class FactBinding implements Element {
		final SingleFactVariable fact;

		@Override
		public EquivalenceClass getEquivalenceClass() {
			return fact.getEqual();
		}

		@Override
		public SingleFactVariable getFactVariable() {
			return fact;
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
	static class SlotBinding implements Element {
		final SingleSlotVariable slot;

		@Override
		public EquivalenceClass getEquivalenceClass() {
			return slot.getEqual();
		}

		@Override
		public SingleFactVariable getFactVariable() {
			return slot.getFactVariable();
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
	static class ConstantExpression implements Element {
		final FunctionWithArguments<SymbolLeaf> constant;
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
	@EqualsAndHashCode(of = { "variable" })
	static class VariableExpression implements Element {
		final FunctionWithArguments<SymbolLeaf> variable;
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
	static class ElementToTemplateSlotLeafTranslator implements ElementVisitor {
		FunctionWithArguments<TemplateSlotLeaf> arg;

		@Override
		public void visit(final FactBinding element) {
			arg = new TemplateSlotLeaf(element.getFact().getTemplate(), null);
		}

		@Override
		public void visit(final SlotBinding element) {
			arg = new TemplateSlotLeaf(element.getFactVariable().getTemplate(), element.getSlot().getSlot());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void visit(final ConstantExpression element) {
			arg = new ConstantLeaf<TemplateSlotLeaf>(element.constant.evaluate(), element.constant.getReturnType());
		}

		@Override
		public void visit(final VariableExpression element) {
			throw new UnsupportedOperationException(
					"VariableElements can not to be transformed into TemplateSlotLeaf, but only into TypeLeaf!");
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode(of = { "predicate" })
	@ToString(of = { "predicate" })
	static class Filter {
		final FunctionWithArguments<?> predicate;
		final Map<Either<Rule, ExistentialProxy>, Set<FilterInstance>> ruleToAllInstances = new HashMap<>();
		final Map<Either<Rule, ExistentialProxy>, Set<ExplicitFilterInstance>> ruleToExplicitInstances =
				new HashMap<>();
		final Map<Either<Rule, ExistentialProxy>, Set<ImplicitElementFilterInstance>> ruleToImplicitElementInstances =
				new HashMap<>();
		final Map<Either<Rule, ExistentialProxy>, Set<ImplicitECFilterInstance>> ruleToImplicitECInstances =
				new HashMap<>();

		static final Map<Filter, Filter> cache = new HashMap<>();

		static Filter newFilter(final FunctionWithArguments<?> predicate) {
			return cache.computeIfAbsent(new Filter(predicate), Function.identity());
		}

		static Filter newEqualityFilter(final Element left, final Element right) {
			return newFilter(GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips,
					left.accept(new ElementToTemplateSlotLeafTranslator()).arg,
					right.accept(new ElementToTemplateSlotLeafTranslator()).arg));
		}

		static Filter newEqualityFilter(final FunctionWithArguments<ECLeaf> left,
				final FunctionWithArguments<ECLeaf> right) {
			return newFilter(GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips, left, right));
		}

		public ExplicitFilterInstance addExplicitInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
				final ECFilter ecFilter) {
			final ArrayList<EquivalenceClass> parameterECs = OrderedECCollector.collect(ecFilter.getFunction());
			final ExplicitFilterInstance instance = new ExplicitFilterInstance(ruleOrProxy, ecFilter, parameterECs);
			ruleToExplicitInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			return instance;
		}

		public static ImplicitElementFilterInstance newImplicitElementInstance(
				final Either<Rule, ExistentialProxy> ruleOrProxy, final Element left, final Element right) {
			return newEqualityFilter(left, right).addImplicitElementInstance(ruleOrProxy, left, right);
		}

		public ImplicitElementFilterInstance addImplicitElementInstance(
				final Either<Rule, ExistentialProxy> ruleOrProxy, final Element left, final Element right) {
			final ImplicitElementFilterInstance instance = new ImplicitElementFilterInstance(ruleOrProxy, left, right);
			ruleToImplicitElementInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			return instance;
		}

		public static ImplicitECFilterInstance newImplicitECInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
				final FunctionWithArguments<ECLeaf> left, final FunctionWithArguments<ECLeaf> right) {
			return newEqualityFilter(left, right).addImplicitECInstance(ruleOrProxy, left, right);
		}

		public ImplicitECFilterInstance addImplicitECInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
				final FunctionWithArguments<ECLeaf> left, final FunctionWithArguments<ECLeaf> right) {
			final ImplicitECFilterInstance instance =
					new ImplicitECFilterInstance(ruleOrProxy,
							ImmutableList.<EquivalenceClass> builder().addAll(OrderedECCollector.collect(left))
									.addAll(OrderedECCollector.collect(right)).build(), left, right);
			ruleToImplicitECInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
			return instance;
		}

		public Set<FilterInstance> getAllInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
			return ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet());
		}

		public Set<ExplicitFilterInstance> getExplicitInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
			return ruleToExplicitInstances.computeIfAbsent(ruleOrProxy, newHashSet());
		}

		public Set<ImplicitElementFilterInstance> getImplicitElementInstances(
				final Either<Rule, ExistentialProxy> ruleOrProxy) {
			return ruleToImplicitElementInstances.computeIfAbsent(ruleOrProxy, newHashSet());
		}

		public Set<ImplicitECFilterInstance> getImplicitECInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
			return ruleToImplicitECInstances.computeIfAbsent(ruleOrProxy, newHashSet());
		}

		public ECFilterList convert(final ExplicitFilterInstance instance) {
			return new ECNodeFilterSet(instance.ecFilter);
		}

		static interface FilterInstanceVisitor extends Visitor {
			public void visit(final ExplicitFilterInstance filterInstance);

			public void visit(final ImplicitElementFilterInstance filterInstance);

			public void visit(final ImplicitECFilterInstance filterInstance);
		}

		/**
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		@Getter
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		abstract class FilterInstance implements Visitable<FilterInstanceVisitor> {
			final Either<Rule, ExistentialProxy> ruleOrProxy;
			final Map<FilterInstance, Conflict> conflicts = new HashMap<>();

			/**
			 * A conflict represents the fact that two filter instances are using the same data
			 * (possibly on different parameter positions). The parameters of the enclosing instance
			 * are compared to the parameters of the given target instance.
			 * <p>
			 * It holds for every pair c in {@code samePathsIndices} that parameter {@code c.left}
			 * of the enclosing filter instance uses the same {@link Path} as parameter
			 * {@code c.right} of the target filter instance.
			 *
			 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
			 */
			@RequiredArgsConstructor
			@Getter
			class Conflict {
				// left refers to the source, right to the target of the conflicts
				final Set<Pair<Integer, Integer>> intersectingECsIndices;
				final FilterInstance target;

				public boolean hasEqualConflicts(final Conflict other) {
					if (null == other)
						return false;
					if (this == other)
						return true;
					return this.intersectingECsIndices == other.intersectingECsIndices
							|| (this.intersectingECsIndices != null && this.intersectingECsIndices
									.equals(other.intersectingECsIndices));
				}

				public FilterInstance getSource() {
					return FilterInstance.this;
				}
			}

			protected Conflict newConflict(final ImplicitElementFilterInstance source,
					final ImplicitElementFilterInstance target, final Theta sourceTheta, final Theta targetTheta) {
				final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
				final SingleFactVariable s0 = source.left.getFactVariable();
				final SingleFactVariable s1 = source.right.getFactVariable();
				final SingleFactVariable t0 = target.left.getFactVariable();
				final SingleFactVariable t1 = target.right.getFactVariable();
				final boolean tlRelevant = targetTheta.isRelevant(target.left);
				final boolean trRelevant = targetTheta.isRelevant(target.right);
				if (null != s0 && sourceTheta.isRelevant(source.left)) {
					if (s0 == t0 && tlRelevant) {
						intersectingECsIndices.add(Pair.of(0, 0));
					}
					if (s0 == t1 && trRelevant) {
						intersectingECsIndices.add(Pair.of(0, 1));
					}
				}
				if (null != s1 && sourceTheta.isRelevant(source.right)) {
					if (s1 == t0 && tlRelevant) {
						intersectingECsIndices.add(Pair.of(1, 0));
					}
					if (s1 == t1 && trRelevant) {
						intersectingECsIndices.add(Pair.of(1, 1));
					}
				}
				return new Conflict(intersectingECsIndices, target);
			}

			protected Conflict newConflict(final ImplicitElementFilterInstance source, final ECFilterInstance target,
					final Theta sourceTheta, final Theta targetTheta) {
				return newConflict(target, source, targetTheta, sourceTheta, true);
			}

			protected Conflict newConflict(final ECFilterInstance source, final ImplicitElementFilterInstance target,
					final Theta sourceTheta, final Theta targetTheta) {
				return newConflict(source, target, sourceTheta, targetTheta, false);
			}

			protected Conflict newConflict(final ECFilterInstance source, final ImplicitElementFilterInstance target,
					final Theta sourceTheta, final Theta targetTheta, final boolean reverse) {
				final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
				final List<EquivalenceClass> sourceParameters = source.parameters;
				final SingleFactVariable left = target.left.getFactVariable();
				final SingleFactVariable right = target.right.getFactVariable();
				final boolean leftRelevant = targetTheta.isRelevant(target.left);
				final boolean rightRelevant = targetTheta.isRelevant(target.right);
				if (leftRelevant || rightRelevant) {
					final int size = sourceParameters.size();
					for (int i = 0; i < size; ++i) {
						final Set<SingleFactVariable> sourceFVs =
								sourceTheta.getDependentFactVariables(sourceParameters.get(i));
						if (leftRelevant && sourceFVs.contains(left)) {
							intersectingECsIndices.add(reverse ? Pair.of(0, i) : Pair.of(i, 0));
						}
						if (rightRelevant && sourceFVs.contains(right)) {
							intersectingECsIndices.add(reverse ? Pair.of(1, i) : Pair.of(i, 1));
						}
					}
				}
				return new Conflict(intersectingECsIndices, target);
			}

			protected Conflict newConflict(final ECFilterInstance source, final ECFilterInstance target,
					final Theta sourceTheta, final Theta targetTheta) {
				final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
				final List<EquivalenceClass> sourceParameters = source.parameters;
				final List<EquivalenceClass> targetParameters = target.parameters;
				final int size = sourceParameters.size();
				final List<Set<SingleFactVariable>> targetFVsList =
						targetParameters.stream().map(targetTheta::getDependentFactVariables).collect(toList());
				for (int i = 0; i < size; ++i) {
					final Set<SingleFactVariable> sourceFVs =
							sourceTheta.getDependentFactVariables(sourceParameters.get(i));
					for (int j = 0; j < size; ++j) {
						final Set<SingleFactVariable> targetFVs = targetFVsList.get(j);
						if (Collections.disjoint(sourceFVs, targetFVs))
							continue;
						intersectingECsIndices.add(Pair.of(i, j));
					}
				}
				return new Conflict(intersectingECsIndices, target);
			}

			protected abstract Conflict newConflict(final FilterInstance targetFilterInstance, final Theta sourceTheta,
					final Theta targetTheta);

			protected abstract Conflict forSource(final ImplicitElementFilterInstance source, final Theta sourceTheta,
					final Theta targetTheta);

			protected abstract Conflict forSource(final ECFilterInstance source, final Theta sourceTheta,
					final Theta targetTheta);

			public Conflict getConflict(final FilterInstance targetFilterInstance, final Theta sourceTheta,
					final Theta targetTheta) {
				final Conflict conflict = newConflict(targetFilterInstance, sourceTheta, targetTheta);
				if (conflict.intersectingECsIndices.isEmpty()) {
					return null;
				}
				return conflict;
			}

			public Filter getFilter() {
				return Filter.this;
			}

			/**
			 * Returns the filter instances of the same filter within the same rule (result contains
			 * the filter instance this method is called upon).
			 *
			 * @return the filter instances of the same filter within the same rule
			 */
			public abstract Set<? extends FilterInstance> getSiblings();

			public abstract List<SingleFactVariable> getDirectlyContainedFactVariables();

			public abstract List<EquivalenceClass> getDirectlyContainedEquivalenceClasses();
		}

		static interface ImplicitFilterInstance {
			public FilterInstance getDual();
		}

		/**
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		@Getter
		// no EqualsAndHashCode
		class ImplicitElementFilterInstance extends FilterInstance implements ImplicitFilterInstance {
			final Element left, right;

			private ImplicitElementFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy, final Element left,
					final Element right) {
				super(ruleOrProxy);
				this.left = left;
				this.right = right;
			}

			@Override
			public String toString() {
				return "[= " + Objects.toString(left) + " " + Objects.toString(right) + "]";
			}

			@Override
			public FilterInstance getDual() {
				final Set<ImplicitElementFilterInstance> implicitElementInstances =
						Filter.newEqualityFilter(right, left).getImplicitElementInstances(ruleOrProxy);
				assert implicitElementInstances.size() == 1;
				return implicitElementInstances.iterator().next();
			}

			@Override
			public <V extends FilterInstanceVisitor> V accept(final V visitor) {
				visitor.visit(this);
				return visitor;
			}

			@Override
			protected FilterInstance.Conflict newConflict(final FilterInstance targetFilterInstance,
					final Theta sourceTheta, final Theta targetTheta) {
				return targetFilterInstance.forSource(this, sourceTheta, targetTheta);
			}

			@Override
			protected FilterInstance.Conflict forSource(final ImplicitElementFilterInstance source,
					final Theta sourceTheta, final Theta targetTheta) {
				return newConflict(source, this, sourceTheta, targetTheta);
			}

			@Override
			protected FilterInstance.Conflict forSource(final ECFilterInstance source, final Theta sourceTheta,
					final Theta targetTheta) {
				return newConflict(source, this, sourceTheta, targetTheta);
			}

			@Override
			public Set<ImplicitElementFilterInstance> getSiblings() {
				return getImplicitElementInstances(ruleOrProxy);
			}

			@Override
			public List<SingleFactVariable> getDirectlyContainedFactVariables() {
				final Builder<SingleFactVariable> builder = ImmutableList.builder();
				{
					final SingleFactVariable fv = left.getFactVariable();
					if (null != fv)
						builder.add(fv);
				}
				{
					final SingleFactVariable fv = right.getFactVariable();
					if (null != fv)
						builder.add(fv);
				}
				return builder.build();
			}

			@Override
			public List<EquivalenceClass> getDirectlyContainedEquivalenceClasses() {
				return ImmutableList.of(left.getEquivalenceClass(), right.getEquivalenceClass());
			}
		}

		/**
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		private abstract class ECFilterInstance extends FilterInstance {
			final List<EquivalenceClass> parameters;

			private ECFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
					final List<EquivalenceClass> parameters) {
				super(ruleOrProxy);
				this.parameters = parameters;
			}

			@Override
			protected FilterInstance.Conflict newConflict(final FilterInstance targetFilterInstance,
					final Theta sourceTheta, final Theta targetTheta) {
				return targetFilterInstance.forSource(this, sourceTheta, targetTheta);
			}

			@Override
			protected FilterInstance.Conflict forSource(final ImplicitElementFilterInstance source,
					final Theta sourceTheta, final Theta targetTheta) {
				return newConflict(source, this, sourceTheta, targetTheta);
			}

			@Override
			protected FilterInstance.Conflict forSource(final ECFilterInstance source, final Theta sourceTheta,
					final Theta targetTheta) {
				return newConflict(source, this, sourceTheta, targetTheta);
			}

			@Override
			public List<SingleFactVariable> getDirectlyContainedFactVariables() {
				return parameters.stream().flatMap(ec -> ec.getDirectlyDependentFactVariables().stream())
						.collect(toList());
			}

			@Override
			public List<EquivalenceClass> getDirectlyContainedEquivalenceClasses() {
				return parameters;
			}
		}

		/**
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		@Getter
		// no EqualsAndHashCode
		class ImplicitECFilterInstance extends ECFilterInstance implements ImplicitFilterInstance {
			final FunctionWithArguments<ECLeaf> left, right;

			private ImplicitECFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
					final List<EquivalenceClass> parameters, final FunctionWithArguments<ECLeaf> left,
					final FunctionWithArguments<ECLeaf> right) {
				super(ruleOrProxy, parameters);
				this.left = left;
				this.right = right;
			}

			@Override
			public String toString() {
				return "[= " + Objects.toString(left) + " " + Objects.toString(right) + "]";
			}

			@Override
			public FilterInstance getDual() {
				final Set<ImplicitECFilterInstance> implicitElementInstances =
						Filter.newEqualityFilter(right, left).getImplicitECInstances(ruleOrProxy);
				assert implicitElementInstances.size() == 1;
				return implicitElementInstances.iterator().next();
			}

			@Override
			public <V extends FilterInstanceVisitor> V accept(final V visitor) {
				visitor.visit(this);
				return visitor;
			}

			@Override
			public Set<ImplicitECFilterInstance> getSiblings() {
				return getImplicitECInstances(ruleOrProxy);
			}
		}

		/**
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		@Getter
		// no EqualsAndHashCode
		class ExplicitFilterInstance extends ECFilterInstance {
			final ECFilter ecFilter;

			private ExplicitFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy, final ECFilter ecFilter,
					final List<EquivalenceClass> parameters) {
				super(ruleOrProxy, parameters);
				this.ecFilter = ecFilter;
			}

			@Override
			public String toString() {
				return Objects.toString(ecFilter);
			}

			@Override
			public <V extends FilterInstanceVisitor> V accept(final V visitor) {
				visitor.visit(this);
				return visitor;
			}

			public ECFilterList convert() {
				return getFilter().convert(this);
			}

			@Override
			public Set<ExplicitFilterInstance> getSiblings() {
				return getExplicitInstances(ruleOrProxy);
			}
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	static class FilterProxy extends Filter {
		final Set<ExistentialProxy> proxies;

		private FilterProxy(final FunctionWithArguments<TypeLeaf> predicate, final ExistentialProxy proxy) {
			super(predicate);
			this.proxies = Sets.newHashSet(proxy);
		}

		static final Map<FilterProxy, FilterProxy> cache = new HashMap<>();

		static FilterProxy newFilterProxy(final FunctionWithArguments<TypeLeaf> predicate, final ExistentialProxy proxy) {
			return cache.computeIfAbsent(new FilterProxy(predicate, proxy), Function.identity());
		}

		static Set<FilterProxy> getFilterProxies() {
			return cache.keySet();
		}

		@Override
		public ECFilterList convert(final ExplicitFilterInstance instance) {
			assert instance.getRuleOrProxy().isLeft() : "Nested Existentials Unsupported!";
			final Rule rule = instance.getRuleOrProxy().left().get();
			final ExistentialProxy existentialProxy = rule.getExistentialProxies().get(instance);
			final ECExistentialSet existential = existentialProxy.getExistential();
			return new ECFilterList.ECExistentialList(existential.isPositive(), existential.getInitialFactVariable(),
					existential.getExistentialFactVariables(), ECFilterList.toSimpleList(Collections.emptyList()),
					new ECNodeFilterSet(instance.getEcFilter()));
		}

		@Override
		protected boolean canEqual(final Object other) {
			return other instanceof FilterProxy;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this)
				return true;
			if (!(o instanceof FilterProxy))
				return false;
			final FilterProxy other = (FilterProxy) o;
			if (!other.canEqual((Object) this))
				return false;
			if (!super.equals(other))
				return false;
			if (!equalProxies(this, other))
				return false;
			return true;
		}

		private static boolean equalProxies(final FilterProxy aFilterProxy, final FilterProxy bFilterProxy) {
			final ExistentialProxy aProxy = aFilterProxy.proxies.iterator().next();
			final ExistentialProxy bProxy = bFilterProxy.proxies.iterator().next();
			if (aProxy.existential.getExistentialFactVariables().size() != bProxy.existential
					.getExistentialFactVariables().size())
				return false;
			if (aProxy.existential.isPositive() != bProxy.existential.isPositive())
				return false;
			final Set<Filter> aFilters = aProxy.getFilters();
			final Set<Filter> bFilters = bProxy.getFilters();
			if (aFilters.size() != bFilters.size())
				return false;
			if (aFilters.size() == 0) {
				return true;
			}

			final List<Set<FilterInstance>> aFilterInstanceSets =
					aFilters.stream().map(f -> f.getAllInstances(Either.right(aProxy)))
							.collect(toCollection(ArrayList::new));
			aFilterInstanceSets.add(Collections.singleton(aProxy.getExistentialClosure()));
			final List<Set<FilterInstance>> bFilterInstanceSets =
					bFilters.stream().map(f -> f.getAllInstances(Either.right(bProxy)))
							.collect(toCollection(ArrayList::new));
			bFilterInstanceSets.add(Collections.singleton(bProxy.getExistentialClosure()));

			final List<FilterInstance> aFlatFilterInstances =
					aFilterInstanceSets.stream().flatMap(Set::stream).collect(toList());

			final Set<List<List<FilterInstance>>> cartesianProduct =
					Sets.cartesianProduct(bFilterInstanceSets.stream()
							.map(set -> Sets.newHashSet(new PermutationIterator<FilterInstance>(set)))
							.collect(toList()));

			final HashMap<FilterInstance, Pair<Integer, Integer>> aFI2IndexPair = new HashMap<>();
			{
				int i = 0;
				for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
					int j = 0;
					for (final FilterInstance filterInstance : aCell) {
						aFI2IndexPair.put(filterInstance, Pair.of(i, j));
						++j;
					}
					++i;
				}
			}
			bijectionLoop: for (final List<List<FilterInstance>> bijection : cartesianProduct) {
				int i = 0;
				for (final Set<FilterInstance> aCell : aFilterInstanceSets) {
					final List<FilterInstance> bCell = bijection.get(i);
					int j = 0;
					for (final FilterInstance aSource : aCell) {
						final FilterInstance bSource = bCell.get(j);
						for (final FilterInstance aTarget : aFlatFilterInstances) {
							final Pair<Integer, Integer> indexPair = aFI2IndexPair.get(aTarget);
							final FilterInstance bTarget = bijection.get(indexPair.getLeft()).get(indexPair.getRight());
							final Set<Pair<Integer, Integer>> aConflict = getECIndexSet(aSource, aTarget);
							final Set<Pair<Integer, Integer>> bConflict = getECIndexSet(bSource, bTarget);
							if (!Objects.equals(aConflict, bConflict)) {
								continue bijectionLoop;
							}
						}
						++j;
					}
					++i;
				}
				return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			final int PRIME = 59;
			int result = 1;
			result = (result * PRIME) + super.hashCode();
			result =
					(result * PRIME)
							+ (this.proxies == null ? 0 : (this.proxies.iterator().next().filters == null ? 0
									: this.proxies.iterator().next().filters.hashCode()));
			return result;
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
	static class FilterInstanceTypePartitioner implements FilterInstanceVisitor {
		final List<ExplicitFilterInstance> explicitFilterInstances = new ArrayList<>();
		final List<ImplicitElementFilterInstance> implicitElementFilterInstances = new ArrayList<>();
		final List<ImplicitECFilterInstance> implicitECFilterInstances = new ArrayList<>();

		static FilterInstanceTypePartitioner partition(final Iterable<FilterInstance> filterInstances) {
			final FilterInstanceTypePartitioner partitioner = new FilterInstanceTypePartitioner();
			for (final FilterInstance filterInstance : filterInstances) {
				filterInstance.accept(partitioner);
			}
			return partitioner;
		}

		@Override
		public void visit(final ExplicitFilterInstance filterInstance) {
			explicitFilterInstances.add(filterInstance);
		}

		@Override
		public void visit(final ImplicitECFilterInstance filterInstance) {
			implicitECFilterInstances.add(filterInstance);
		}

		@Override
		public void visit(final ImplicitElementFilterInstance filterInstance) {
			implicitElementFilterInstances.add(filterInstance);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	static class Rule {
		final ECSetRule original;
		final Set<Filter> filters = new HashSet<>();
		final BiMap<FilterInstance, ExistentialProxy> existentialProxies = HashBiMap.create();

		@Override
		public String toString() {
			return original.getParent().getName();
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	@ToString(of = { "rule", "filters" })
	static class ExistentialProxy {
		final Rule rule;
		final ECExistentialSet existential;
		final Set<Filter> filters = new HashSet<>();

		public FilterInstance getExistentialClosure() {
			return this.rule.getExistentialProxies().inverse().get(this);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	static class Partition<T, S extends Partition.SubSet<T>> {
		@RequiredArgsConstructor
		@Getter
		static class SubSet<T> {
			final Map<Either<Rule, ExistentialProxy>, T> elements;
		}

		final Set<S> elements = new HashSet<>();
		final Map<T, S> lookup = new HashMap<>();

		public Partition(final Partition<T, S> copy) {
			elements.addAll(copy.elements);
			lookup.putAll(copy.lookup);
		}

		public void add(final S newSubSet) {
			elements.add(newSubSet);
			for (final T newElement : newSubSet.elements.values()) {
				lookup.put(newElement, newSubSet);
			}
		}

		public void extend(final Either<Rule, ExistentialProxy> rule, final Map<S, T> extension) {
			for (final S subset : elements) {
				subset.elements.put(rule, extension.get(subset));
			}
		}

		public S lookup(final T element) {
			return lookup.get(element);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	static class FilterInstancePartition extends
			Partition<FilterInstance, FilterInstancePartition.FilterInstanceSubSet> {
		@Getter
		static class FilterInstanceSubSet extends Partition.SubSet<FilterInstance> {
			final Filter filter;

			public FilterInstanceSubSet(final Map<Either<Rule, ExistentialProxy>, FilterInstance> elements) {
				super(elements);
				this.filter = elements.values().iterator().next().getFilter();
			}

			public FilterInstanceSubSet(final Either<Rule, ExistentialProxy> rule, final FilterInstance filterInstance) {
				this(Maps.newHashMap(Collections.singletonMap(rule, filterInstance)));
			}
		}

		final Map<Filter, Set<FilterInstanceSubSet>> filterLookup = new HashMap<>();

		public FilterInstancePartition(final FilterInstancePartition copy) {
			super(copy);
			filterLookup.putAll(copy.filterLookup);
		}

		@Override
		public void add(final FilterInstanceSubSet newSubSet) {
			super.add(newSubSet);
			filterLookup.computeIfAbsent(newSubSet.filter, newHashSet()).add(newSubSet);
		}

		public Set<FilterInstanceSubSet> lookupByFilter(final Filter filter) {
			return filterLookup.get(filter);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	static class FactVariablePartition extends Partition<SingleFactVariable, FactVariablePartition.FactVariableSubSet> {
		@Getter
		static class FactVariableSubSet extends Partition.SubSet<SingleFactVariable> {
			final Template template;

			public FactVariableSubSet(final Map<Either<Rule, ExistentialProxy>, SingleFactVariable> elements) {
				super(elements);
				this.template = elements.values().iterator().next().getTemplate();
			}

			public FactVariableSubSet(final Either<Rule, ExistentialProxy> rule, final SingleFactVariable filterInstance) {
				this(Maps.newHashMap(Collections.singletonMap(rule, filterInstance)));
			}
		}

		final Map<Template, Set<FactVariableSubSet>> templateLookup = new HashMap<>();

		public FactVariablePartition(final FactVariablePartition copy) {
			super(copy);
			templateLookup.putAll(copy.templateLookup);
		}

		@Override
		public void add(final FactVariableSubSet newSubSet) {
			super.add(newSubSet);
			templateLookup.computeIfAbsent(newSubSet.template, newHashSet()).add(newSubSet);
		}

		public Set<FactVariableSubSet> lookupByTemplate(final Template template) {
			return templateLookup.get(template);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	static class ElementPartition extends Partition<Element, Partition.SubSet<Element>> {
		public ElementPartition(final Partition<Element, Partition.SubSet<Element>> copy) {
			super(copy);
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
				return equivalenceClassToReduced.get(element.getEquivalenceClass()).isRelevant(element);
			}

			@Override
			public Set<SingleFactVariable> getDependentFactVariables(final EquivalenceClass equivalenceClass) {
				return equivalenceClassToReduced.get(equivalenceClass).getDependentFactVariables();
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
		}

		public void add(final Element element);

		public boolean isRelevant(final Element element);

		public Set<SingleFactVariable> getDependentFactVariables(final EquivalenceClass equivalenceClass);

		public Theta copy();

		public Set<EquivalenceClass> getEquivalenceClasses();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	public static class Block {
		// conflict graph
		UndirectedGraph<FilterInstance, ConflictEdge> graph;
		// rules of the block
		final Set<Either<Rule, ExistentialProxy>> rulesOrProxies;
		// abstract filters of the block
		final Set<Filter> filters = new HashSet<>();
		// contains the filterInstances without the correct arrangement, just to avoid having to
		// flat map the filterInstances every time
		final Set<FilterInstance> flatFilterInstances = new HashSet<>();

		// theta : map the arguments of the filter instances used instead of modifying them
		// in-place to be able to have the same instance within different blocks
		final Theta theta;
		final ConflictEdgeFactory edgeFactory;
		final FilterInstancePartition filterInstancePartition;
		final FactVariablePartition factVariablePartition;
		final ElementPartition elementPartition;

		public Block(final Set<Either<Rule, ExistentialProxy>> rules, final FactVariablePartition factVariablePartition) {
			this.theta = new Theta.Reducer();
			this.edgeFactory = ConflictEdge.newFactory(theta);
			this.graph = new SimpleGraph<>(edgeFactory);
			this.rulesOrProxies = Sets.newHashSet(rules);
			this.factVariablePartition = factVariablePartition;
			this.filterInstancePartition = new FilterInstancePartition();
			this.elementPartition = new ElementPartition();
		}

		public Block(final Block block) {
			theta = block.theta.copy();
			edgeFactory = ConflictEdge.newFactory(theta);
			graph = block.graph;
			rulesOrProxies = new HashSet<>(block.rulesOrProxies);
			filters.addAll(block.filters);
			flatFilterInstances.addAll(block.flatFilterInstances);
			filterInstancePartition = new FilterInstancePartition(block.filterInstancePartition);
			factVariablePartition = new FactVariablePartition(block.factVariablePartition);
			elementPartition = new ElementPartition(block.elementPartition);
		}

		@Override
		public String toString() {
			return "Block: " + Objects.toString(filterInstancePartition);
		}

		public int getNumberOfRows() {
			return rulesOrProxies.size();
		}

		public int getNumberOfColumns() {
			return filterInstancePartition.elements.size();
		}

		public void addElementSet(final Map<Either<Rule, ExistentialProxy>, Element> elements) {
			addElementSet(new SubSet<>(elements));
		}

		public void addElementSet(final SubSet<ECBlocks.Element> newSubSet) {
			assert rulesOrProxies.stream().allMatch(newSubSet.elements.keySet()::contains);
			for (final Element element : newSubSet.elements.values()) {
				theta.add(element);
			}
			elementPartition.add(newSubSet);
		}

		public boolean addExplicitColumn(final Map<Either<Rule, ExistentialProxy>, FilterInstance> filterInstances) {
			return addExplicitColumn(new FilterInstanceSubSet(filterInstances));
		}

		public boolean addExplicitColumn(final FilterInstanceSubSet newSubSet) {
			assert rulesOrProxies.stream().allMatch(newSubSet.elements.keySet()::contains);
			filterInstancePartition.add(newSubSet);
			filters.add(newSubSet.getFilter());
			final Collection<FilterInstance> filterInstances = newSubSet.elements.values();
			flatFilterInstances.addAll(filterInstances);

			// FIXME adjust element partition, return false if none applicable
			final Map<Integer, Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> ecPartition = new HashMap<>();
			for (final FilterInstance filterInstance : filterInstances) {
				final ImmutableList<EquivalenceClass> newECs =
						ImmutableList.copyOf(Sets.difference(
								Sets.newLinkedHashSet(filterInstance.getDirectlyContainedEquivalenceClasses()),
								theta.getEquivalenceClasses()));
				final Either<Rule, ExistentialProxy> ruleOrProxy = filterInstance.getRuleOrProxy();
				for (int i = 0; i < newECs.size(); i++) {
					ecPartition.computeIfAbsent(i, newHashMap()).put(ruleOrProxy, newECs.get(i));
				}
			}

			final Collection<Map<Either<Rule, ExistentialProxy>, EquivalenceClass>> values = ecPartition.values();

			return true;
		}

		public Set<FilterInstance> getConflictNeighbours() {
			final SetView<FilterInstance> outside = Sets.difference(graph.vertexSet(), flatFilterInstances);
			final Set<FilterInstance> neighbours =
					outside.stream()
							.filter(nFI -> flatFilterInstances.stream().anyMatch(bFI -> graph.containsEdge(bFI, nFI)))
							.collect(toSet());
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
			final Set<FilterInstanceSubSet> otherFISubSets = other.filterInstancePartition.getElements();
			final Set<FilterInstanceSubSet> thisFISubSets = this.filterInstancePartition.getElements();
			if (otherFISubSets.size() < thisFISubSets.size()) {
				return false;
			}
			SubSetLoop: for (final FilterInstanceSubSet thisFISubSet : thisFISubSets) {
				final Set<FilterInstanceSubSet> otherFISubSetsOfSameFilter =
						other.filterInstancePartition.lookupByFilter(thisFISubSet.getFilter());
				for (final FilterInstanceSubSet otherFISubSet : otherFISubSetsOfSameFilter) {
					if (otherFISubSet.getElements().values().containsAll(thisFISubSet.getElements().values())) {
						continue SubSetLoop;
					}
				}
				return false;
			}
			return true;
		}
	}

	protected static boolean determineEquivalenceClassIntersection(
			final Map<Either<Rule, ExistentialProxy>, EquivalenceClass> ecs, final Block block) {
		final Map<FactVariableSubSet, Map<Either<Rule, ExistentialProxy>, FactBinding>> fvMapping =
				new IdentityHashMap<>();
		final Map<FactVariableSubSet, Map<SlotAddress, Map<Either<Rule, ExistentialProxy>, SlotBinding>>> svMapping =
				new IdentityHashMap<>();
		final Map<Object, Map<Either<Rule, ExistentialProxy>, ConstantExpression>> constantMapping = new HashMap<>();
		for (final Entry<Either<Rule, ExistentialProxy>, EquivalenceClass> entry : ecs.entrySet()) {
			final Either<Rule, ExistentialProxy> rule = entry.getKey();
			final EquivalenceClass ec = entry.getValue();
			for (final SingleFactVariable fv : ec.getFactVariables()) {
				final FactVariableSubSet subSet = block.factVariablePartition.lookup(fv);
				fvMapping.computeIfAbsent(subSet, x -> new IdentityHashMap<>()).put(rule, new FactBinding(fv));
			}
			for (final SingleSlotVariable sv : ec.getSlotVariables()) {
				final FactVariableSubSet subSet = block.factVariablePartition.lookup(sv.getFactVariable());
				svMapping.computeIfAbsent(subSet, x -> new IdentityHashMap<>())
						.computeIfAbsent(sv.getSlot(), x -> new IdentityHashMap<>()).put(rule, new SlotBinding(sv));
			}
			for (final FunctionWithArguments<SymbolLeaf> constant : ec.getConstantExpressions()) {
				final Object value = constant.evaluate();
				constantMapping.computeIfAbsent(value, x -> new HashMap<>()).put(rule,
						new ConstantExpression(constant, ec));
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
		for (final Map<Either<Rule, ExistentialProxy>, ? extends Element> subset : intersection) {
			block.addElementSet(new SubSet<>(new HashMap<>(subset)));
		}
		return !intersection.isEmpty();
	}

	@Getter
	static class BlockSet {
		final HashSet<Block> blocks = new HashSet<>();
		final TreeMap<Integer, HashSet<Block>> ruleCountToBlocks = new TreeMap<>();
		final TreeMap<Integer, HashSet<Block>> filterCountToBlocks = new TreeMap<>();
		final HashMap<Either<Rule, ExistentialProxy>, HashSet<Block>> ruleInstanceToBlocks = new HashMap<>();
		final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> ruleCountToFilterCountToBlocks = new TreeMap<>();
		final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> filterCountToRuleCountToBlocks = new TreeMap<>();

		private static int getRuleCount(final Block block) {
			return block.getRulesOrProxies().size();
		}

		private static int getFilterCount(final Block block) {
			return block.getNumberOfColumns();
		}

		private boolean addDuringHorizontalRecursion(final Block block) {
			final Integer ruleCount = getRuleCount(block);
			final Integer filterCount = getFilterCount(block);
			// first check if there is a block of the same height with more filter instances
			{
				final NavigableMap<Integer, HashSet<Block>> fixedRuleCountFilters =
						ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap()).tailMap(filterCount,
								true);
				for (final Set<Block> fixedFilterCountRule : fixedRuleCountFilters.values()) {
					for (final Block candidate : fixedFilterCountRule) {
						if (block.containedIn(candidate)) {
							return false;
						}
					}
				}
			}
			// then check if there is a block of the same width with more rules
			{
				final NavigableMap<Integer, HashSet<Block>> fixedFilterCountRules =
						filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap()).tailMap(ruleCount,
								true);
				for (final Set<Block> fixedFilterCountRule : fixedFilterCountRules.values()) {
					for (final Block candidate : fixedFilterCountRule) {
						if (block.containedIn(candidate)) {
							return false;
						}
					}
				}
			}
			// finally check if there is a block larger in both dimensions
			{
				for (final TreeMap<Integer, HashSet<Block>> fixedFilterCountMap : filterCountToRuleCountToBlocks
						.tailMap(filterCount, false).values()) {
					for (final HashSet<Block> candidates : fixedFilterCountMap.tailMap(ruleCount, false).values()) {
						for (final Block candidate : candidates) {
							if (block.containedIn(candidate)) {
								return false;
							}
						}
					}
				}
			}
			actuallyInsertBlockIntoAllCaches(block);
			return true;
		}

		private void removeContainedBlocks(final Block block) {
			final Integer ruleCount = getRuleCount(block);
			final Integer filterCount = getFilterCount(block);

			final List<Block> toRemove = new ArrayList<>();

			final Collection<TreeMap<Integer, HashSet<Block>>> filterCountToBlocksRuleCountHead =
					ruleCountToFilterCountToBlocks.headMap(ruleCount, true).values();
			for (final TreeMap<Integer, HashSet<Block>> filterCountToBlocksFixedRuleCount : filterCountToBlocksRuleCountHead) {
				final Collection<HashSet<Block>> blocksFixedRuleCountFilterCountHead =
						filterCountToBlocksFixedRuleCount.headMap(filterCount, true).values();
				for (final HashSet<Block> blocksFixedRuleCountFixedFilterCount : blocksFixedRuleCountFilterCountHead) {
					for (final Block candidate : blocksFixedRuleCountFixedFilterCount) {
						if (candidate != block && candidate.containedIn(block)) {
							// can't remove right now since we are iterating over a collection that
							// would be changed
							toRemove.add(candidate);
						}
					}
				}
			}
			for (final Block remove : toRemove) {
				remove(remove);
			}
		}

		private void actuallyInsertBlockIntoAllCaches(final Block block) {
			blocks.add(block);
			final Integer ruleCount = getRuleCount(block);
			final Integer filterCount = getFilterCount(block);
			ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).add(block);
			filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).add(block);
			ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
					.computeIfAbsent(filterCount, newHashSet()).add(block);
			filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
					.computeIfAbsent(ruleCount, newHashSet()).add(block);
			block.getRulesOrProxies().forEach(r -> ruleInstanceToBlocks.computeIfAbsent(r, newHashSet()).add(block));
			removeContainedBlocks(block);
		}

		public boolean isContained(final Block block) {
			final Integer ruleCount = getRuleCount(block);
			final Integer filterCount = getFilterCount(block);
			for (final TreeMap<Integer, HashSet<Block>> treeMap : filterCountToRuleCountToBlocks.tailMap(filterCount)
					.values()) {
				for (final HashSet<Block> blocks : treeMap.tailMap(ruleCount).values()) {
					if (blocks.stream().anyMatch(block::containedIn)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean addDuringConflictResolution(final Block block) {
			if (isContained(block)) {
				return false;
			}
			actuallyInsertBlockIntoAllCaches(block);
			return true;
		}

		public boolean remove(final Block block) {
			if (!blocks.remove(block))
				return false;
			final Integer ruleCount = getRuleCount(block);
			final Integer filterCount = getFilterCount(block);
			ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).remove(block);
			filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).remove(block);
			ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
					.computeIfAbsent(filterCount, newHashSet()).remove(block);
			filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
					.computeIfAbsent(ruleCount, newHashSet()).remove(block);
			block.getRulesOrProxies().forEach(r -> ruleInstanceToBlocks.computeIfAbsent(r, newHashSet()).remove(block));
			return true;
		}
	}

	protected static Set<Filter> getFilters(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFilters, ExistentialProxy::getFilters);
	}

	protected static void addRule(final ECSetRule ecFilterSetCondition, final List<Either<Rule, ExistentialProxy>> rules) {
		final Rule rule = new Rule(ecFilterSetCondition);
		// create all filter instances
		final Set<ECFilterSet> condition = ecFilterSetCondition.getCondition();
		final Either<Rule, ExistentialProxy> ruleEither = Either.left(rule);
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
			assert element.getEquivalenceClass() == original;
			this.elements.add(element);
			return this;
		}

		public ReducedEquivalenceClass remove(final Element element) {
			assert element.getEquivalenceClass() == original;
			this.elements.remove(element);
			return this;
		}

		public boolean isRelevant(final Element element) {
			return elements.contains(element);
		}

		public Set<SingleFactVariable> getDependentFactVariables() {
			return elements.stream().map(Element::getFactVariable).collect(toSet());
		}
	}

	@AllArgsConstructor
	static class RuleConverter implements ECFilterSetVisitor {
		final List<Either<Rule, ExistentialProxy>> rules;
		final Either<Rule, ExistentialProxy> ruleOrProxy;

		public static void convert(final List<Either<Rule, ExistentialProxy>> rules,
				final Either<Rule, ExistentialProxy> ruleOrProxy, final Collection<ECFilterSet> filters) {
			final RuleConverter ruleConverter = new RuleConverter(rules, ruleOrProxy);
			// FIXME !explicitly add the tests currently implicit within the equivalence classes!
			final Rule rule = ruleOrProxy.left().get();
			final Set<EquivalenceClass> equivalenceClasses = rule.getOriginal().getEquivalenceClasses();
			for (final EquivalenceClass equivalenceClass : equivalenceClasses) {
				final List<Element> elements = new ArrayList<>();
				equivalenceClass.getFactVariables().stream().map(FactBinding::new).forEach(elements::add);
				equivalenceClass.getSlotVariables().stream().map(SlotBinding::new).forEach(elements::add);
				equivalenceClass.getConstantExpressions().stream()
						.map(c -> new ConstantExpression(c, equivalenceClass)).forEach(elements::add);
				for (final Element left : elements) {
					for (final Element right : elements) {
						if (left == right)
							continue;
						Filter.newImplicitElementInstance(ruleOrProxy, left, right);
					}
				}
				final List<FunctionWithArguments<ECLeaf>> converted =
						equivalenceClass.getVariableExpressions().stream().map(FWASymbolToECTranslator::translate)
								.collect(toList());
				for (final FunctionWithArguments<ECLeaf> left : converted) {
					for (final FunctionWithArguments<ECLeaf> right : converted) {
						if (left == right)
							continue;
						Filter.newImplicitECInstance(ruleOrProxy, left, right);
					}
				}
				if (!elements.isEmpty()) {
					final ECLeaf ecLeaf = new ECLeaf(equivalenceClass);
					for (final FunctionWithArguments<ECLeaf> fwa : converted) {
						Filter.newImplicitECInstance(ruleOrProxy, ecLeaf, fwa);
						Filter.newImplicitECInstance(ruleOrProxy, fwa, ecLeaf);
					}
				}

				// ignore here, perform additional actions in visit(ECExistentialSet)
				// 1 : just ignore and check within FilterProxy ::new and ::convert
				// 2 : add explicitly to FilterProxy
				// 3 : add explicitly to existentialClosure
				final Set<EquivalenceClass> equalParentEquivalenceClasses =
						equivalenceClass.getEqualParentEquivalenceClasses();
				// FWASymbolToECTranslator
				final ReducedEquivalenceClass reducedEquivalenceClass = new ReducedEquivalenceClass(equivalenceClass);
			}
			for (final ECFilterSet filter : filters) {
				filter.accept(ruleConverter);
			}
		}

		@Override
		public void visit(final ECFilter ecFilter) {
			final Filter filter = convertFilter(ecFilter, Filter::newFilter);
			filter.addExplicitInstance(ruleOrProxy, ecFilter);
			getFilters(ruleOrProxy).add(filter);
		}

		protected static <T extends Filter> T convertFilter(final ECFilter ecFilter,
				final Function<PredicateWithArguments<TypeLeaf>, T> ctor) {
			final PredicateWithArguments<ECLeaf> predicate = ecFilter.getFunction();
			return ctor.apply(FWAPathLeafToTypeLeafTranslator.getArguments(predicate));
		}

		@Override
		public void visit(final ECExistentialSet existentialSet) {
			final Rule rule =
					ruleOrProxy.left().getOrThrow(() -> new UnsupportedOperationException("Nested Existentials!"));
			// we may be able to share the existential closure part
			// existential closure filter instances are put into the same column if and only if they
			// have the same conflicts to their pure part and the pure parts have the same inner
			// conflicts

			final ECFilter existentialClosure = existentialSet.getExistentialClosure();
			final Set<ECFilterSet> purePart = existentialSet.getPurePart();

			final ExistentialProxy proxy = new ExistentialProxy(rule, existentialSet);
			final Either<Rule, ExistentialProxy> proxyEither = Either.right(proxy);
			final RuleConverter visitor = new RuleConverter(rules, proxyEither);

			// insert all pure filters into the proxy
			for (final ECFilterSet pathFilterSet : purePart) {
				pathFilterSet.accept(visitor);
			}
			// create own row for the pure part
			rules.add(proxyEither);

			final FilterProxy convertedExCl =
					convertFilter(existentialClosure, pred -> FilterProxy.newFilterProxy(pred, proxy));
			getFilters(ruleOrProxy).add(convertedExCl);
			final FilterInstance filterInstance = convertedExCl.addExplicitInstance(ruleOrProxy, existentialClosure);
			rule.existentialProxies.put(filterInstance, proxy);
		}
	}

	public static List<PathRule> transform(final List<ECSetRule> rules) {
		final List<Either<Rule, ExistentialProxy>> translatedRules = new ArrayList<>();
		for (final ECSetRule rule : rules) {
			addRule(rule, translatedRules);
		}
		// find all maximal blocks
		final BlockSet resultBlockSet = new BlockSet();
		findAllMaximalBlocks(translatedRules, resultBlockSet);
		// solve the conflicts
		determineAndSolveConflicts(resultBlockSet);
		// transform into PathFilterList
		final List<PathRule> output = createOutput(translatedRules, resultBlockSet);
		Filter.cache.clear();
		FilterProxy.cache.clear();
		return output;
	}

	protected static List<PathRule> createOutput(final List<Either<Rule, ExistentialProxy>> rules,
			final BlockSet resultBlockSet) {
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
							.computeIfAbsent(Either.right(existentialProxies.iterator().next()), newHashSet()).stream()
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
		final Set<FilterInstance> constructedFIs = new HashSet<>();
		final Map<Either<Rule, ExistentialProxy>, Map<FilterInstance, Set<FilterInstance>>> ruleToJoinedWith =
				new HashMap<>();
		final Map<Set<FilterInstance>, ECFilterList> joinedWithToComponent = new HashMap<>();
		// at this point, the network can be constructed
		for (final CursorableLinkedList<Block> blockList : blockMap.values()) {
			for (final Block block : blockList) {
				final List<Either<Rule, ExistentialProxy>> blockRules = Lists.newArrayList(block.getRulesOrProxies());
				final Set<FilterInstanceSubSet> filterInstanceColumns =
						block.getFilterInstancePartition().getElements();
				// since we are considering blocks, it is either the case that all filter
				// instances of the column have been constructed or none of them have
				final ECSharedListWrapper sharedListWrapper = new ECSharedListWrapper(blockRules.size());
				final Map<Either<Rule, ExistentialProxy>, ECSharedList> ruleToSharedList =
						IntStream.range(0, blockRules.size()).boxed()
								.collect(toMap(blockRules::get, sharedListWrapper.getSharedSiblings()::get));
				final List<List<FilterInstance>> columnsToConstruct, columnsAlreadyConstructed;
				{
					final Map<Boolean, List<List<FilterInstance>>> partition =
							filterInstanceColumns.stream().map(FilterInstanceSubSet::getElements)
									.collect(partitioningBy(column -> Collections.disjoint(column, constructedFIs)));
					columnsAlreadyConstructed = partition.get(Boolean.FALSE);
					columnsToConstruct = partition.get(Boolean.TRUE);
				}

				block.getFlatFilterInstances().stream().filter(negate(constructedFIs::contains))
						.map(fi -> ruleToJoinedWith.get(fi.getRuleOrProxy()).get(fi)).distinct();
				if (!columnsAlreadyConstructed.isEmpty()) {
					final Map<ECSharedList, LinkedHashSet<ECFilterList>> sharedPart = new HashMap<>();
					for (final List<FilterInstance> column : columnsAlreadyConstructed) {
						for (final FilterInstance fi : column) {
							sharedPart.computeIfAbsent(ruleToSharedList.get(fi.getRuleOrProxy()), newLinkedHashSet())
									.add(joinedWithToComponent.get(ruleToJoinedWith.get(fi.getRuleOrProxy()).get(fi)));
						}
					}
					sharedListWrapper.addSharedColumns(sharedPart);
				}

				for (final List<FilterInstance> column : columnsToConstruct) {
					// FIXME !explicit filter instances!
					sharedListWrapper.addSharedColumn(((List<ExplicitFilterInstance>) (List<?>) column).stream()
							.collect(
									toMap(fi -> ruleToSharedList.get(fi.getRuleOrProxy()),
											ExplicitFilterInstance::convert)));
				}
				constructedFIs.addAll(block.getFlatFilterInstances());
				for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter, FilterInstancesSideBySide>> entry : block
						.getRuleToFilterToRow().entrySet()) {
					final Either<Rule, ExistentialProxy> rule = entry.getKey();
					final Set<FilterInstance> joined =
							entry.getValue().values().stream().flatMap(sbs -> sbs.getInstances().stream())
									.collect(toSet());
					final Map<FilterInstance, Set<FilterInstance>> joinedWithMapForThisRule =
							ruleToJoinedWith.computeIfAbsent(rule, newHashMap());
					joined.forEach(fi -> joinedWithMapForThisRule.put(fi, joined));
					joinedWithToComponent.put(joined, ruleToSharedList.get(rule));
				}
			}
		}
		final List<PathRule> ecRules = new ArrayList<>();
		for (final Either<Rule, ExistentialProxy> either : rules) {
			if (either.isRight()) {
				continue;
			}
			final List<ECFilterList> ecFilterLists =
					Stream.concat(either.left().get().existentialProxies.values().stream().map(p -> Either.right(p)),
							Stream.of(either))
							.flatMap(
									e -> ruleToJoinedWith.getOrDefault(e, Collections.emptyMap()).values().stream()
											.distinct()).map(joinedWithToComponent::get).collect(toList());
			final ECSetRule ecSetRule = either.left().get().getOriginal();
			final ECListRule ecListRule =
					ecSetRule.toECListRule(
							ECFilterList.toSimpleList(ecFilterLists),
							ecFilterLists.size() > 1 ? InitialFactVariablesFinder.gather(ecFilterLists) : Collections
									.emptySet());
			final PathRule pathRule = ECFilterOrderOptimizer.optimize(ecListRule);
			ecRules.add(pathRule);
		}
		return ecRules;
	}

	static class InitialFactVariablesFinder implements ECFilterListVisitor {
		final Set<SingleFactVariable> initialFactVariables = Sets.newHashSet();

		static Set<SingleFactVariable> gather(final Iterable<ECFilterList> filters) {
			final InitialFactVariablesFinder instance = new InitialFactVariablesFinder();
			for (final ECFilterList filter : filters) {
				filter.accept(instance);
			}
			return instance.initialFactVariables;
		}

		@Override
		public void visit(final ECSharedList filter) {
			final ImmutableList<ECFilterList> elements = filter.getUnmodifiableFilterListCopy();
			if (1 != elements.size()) {
				return;
			}
			elements.get(0).accept(new InitialFactVariablesFinderHelper());
		}

		class InitialFactVariablesFinderHelper implements ECFilterListVisitor {
			@Override
			public void visit(final ECExistentialList filter) {
				initialFactVariables.add(filter.getInitialFactVariable());
			}

			@Override
			public void visit(final ECNodeFilterSet filter) {
			}

			@Override
			public void visit(final ECSharedList filter) {
			}
		}

		@Override
		public void visit(final ECNodeFilterSet filter) {
		}

		@Override
		public void visit(final ECExistentialList filter) {
		}
	}

	public static boolean hasEqualConflicts(final Conflict a, final Conflict b) {
		return (a == b) || (a != null && a.hasEqualConflicts(b));
	}

	protected static void findAllMaximalBlocks(final List<Either<Rule, ExistentialProxy>> rules,
			final BlockSet resultBlocks) {
		final Set<Filter> filters = rules.stream().flatMap(rule -> getFilters(rule).stream()).collect(toSet());
		for (final Filter filter : filters) {
			vertical(rules.stream().map(r -> filter.getAllInstances(r)).filter(negate(Set::isEmpty)).collect(toSet()),
					resultBlocks);
		}
	}

	protected static <T, K, D> Collector<T, ?, Set<D>> groupingIntoSets(
			final Function<? super T, ? extends K> classifier, final Collector<? super T, ?, D> downstream) {
		final Collector<T, ?, Map<K, D>> groupingBy = groupingBy(classifier, downstream);
		return Collectors.collectingAndThen(groupingBy, map -> new HashSet<D>(map.values()));
	}

	protected static BlockSet findAllMaximalBlocksInReducedScope(final Set<FilterInstance> filterInstances,
			final BlockSet resultBlocks) {
		final Set<Set<Set<FilterInstance>>> filterInstancesGroupedByFilterAndByRule =
				filterInstances.stream().collect(
						groupingIntoSets(FilterInstance::getFilter,
								groupingIntoSets(FilterInstance::getRuleOrProxy, toSet())));
		for (final Set<Set<FilterInstance>> filterInstancesOfOneFilterGroupedByRule : filterInstancesGroupedByFilterAndByRule) {
			vertical(filterInstancesOfOneFilterGroupedByRule, resultBlocks);
		}
		return resultBlocks;
	}

	protected static void determineAndSolveConflicts(final BlockSet resultBlocks) {
		// determine conflicts
		final BlockSet deletedBlocks = new BlockSet();
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
			final BlockSet resultBlocks, final Block x) {
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
			final Map<Either<Rule, ExistentialProxy>, List<FilterInstance>> yFIsByRule =
					conflictingBlock.getFlatFilterInstances().stream()
							.collect(groupingBy(FilterInstance::getRuleOrProxy));
			final Set<FilterInstance> cfi =
					replaceBlock
							.getFlatFilterInstances()
							.stream()
							.filter(xFI -> yFIsByRule
									.getOrDefault(xFI.getRuleOrProxy(), Collections.emptyList())
									.stream()
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
			if (rnoc == cnoc) {
				// containment of columns impossible (only if one block is fully contained within
				// the other, which we won't consider here)
				return new BlockConflict(replaceBlock, conflictingBlock, cfi);
			}
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
			// only consider the rules of x (the wider block)
			final List<Either<Rule, ExistentialProxy>> rules = ImmutableList.copyOf(x.getRulesOrProxies());
			// only consider the filters of y (the taller block)
			final Set<Filter> filters = y.getFilters();
			// the result will be the columns within the intersection
			final Set<List<FilterInstance>> xColumns =
					Block.getFilterInstanceColumns(filters, x.getRuleToFilterToRow(), rules);
			final Set<List<FilterInstance>> yColumns =
					Block.getFilterInstanceColumns(filters, y.getRuleToFilterToRow(), rules);
			// if one of the (shrinked) columns of the taller block is not present in the
			// intersection part of the wider block, the blocks are in conflict
			for (final List<FilterInstance> yColumn : yColumns) {
				if (!xColumns.contains(yColumn))
					return new BlockConflict(replaceBlock, conflictingBlock, cfi);
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
			assert a.getReplaceBlock() == replaceBlock || b.getReplaceBlock() == replaceBlock;
			return a.getReplaceBlock() == replaceBlock ? a : b;
		}

		public BlockConflict getForConflictingBlock(final Block conflictingBlock) {
			assert a.getConflictingBlock() == conflictingBlock || b.getConflictingBlock() == conflictingBlock;
			return a.getConflictingBlock() == conflictingBlock ? a : b;
		}

		public static BlockConflictEdge of(final Block x, final Block y) {
			final BlockConflict xy = BlockConflict.of(x, y);
			if (null == xy)
				return null;
			return new BlockConflictEdge(xy, BlockConflict.of(y, x));
		}
	}

	protected static void solveConflict(final BlockConflict blockConflict,
			final DirectedGraph<Block, BlockConflict> blockConflictGraph, final BlockSet resultBlocks,
			final BlockSet deletedBlocks) {
		final Block replaceBlock = blockConflict.getReplaceBlock();
		final Set<FilterInstance> xWOcfi =
				replaceBlock.getFlatFilterInstances().stream().filter(negate(blockConflict.getCfi()::contains))
						.collect(toSet());
		resultBlocks.remove(replaceBlock);
		// remove replaceBlock and update qualities
		removeArc(blockConflictGraph, blockConflict);
		// find the horizontally maximal blocks within xWOcfi
		final BlockSet newBlocks = findAllMaximalBlocksInReducedScope(xWOcfi, new BlockSet());
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

	protected static List<FactVariablePartition> enumerateFactVariablePartitions(
			final Set<Either<Rule, ExistentialProxy>> rules) {
		final IdentityHashMap<Template, Set<List<FactVariableSubSet>>> subsets = new IdentityHashMap<>();
		final IdentityHashMap<Template, Map<Either<Rule, ExistentialProxy>, List<SingleFactVariable>>> partitionMap =
				new IdentityHashMap<>();
		final IdentityHashMap<SingleFactVariable, Either<Rule, ExistentialProxy>> fvToRule = new IdentityHashMap<>();
		for (final Either<Rule, ExistentialProxy> rule : rules) {
			final Map<Template, List<SingleFactVariable>> template2FVs =
					getFilters(rule).stream().flatMap(f -> f.getAllInstances(rule).stream())
							.flatMap(fi -> fi.getDirectlyContainedFactVariables().stream())
							.collect(groupingBy(SingleFactVariable::getTemplate));
			for (final Entry<Template, List<SingleFactVariable>> entry : template2FVs.entrySet()) {
				final Template template = entry.getKey();
				final List<SingleFactVariable> fvs = entry.getValue();
				partitionMap.computeIfAbsent(template, newHashMap()).put(rule, fvs);
				fvs.forEach(fv -> fvToRule.put(fv, rule));
			}
		}
		for (final Entry<Template, Map<Either<Rule, ExistentialProxy>, List<SingleFactVariable>>> templateToMap : partitionMap
				.entrySet()) {
			final Template template = templateToMap.getKey();
			final IntSummaryStatistics summary =
					templateToMap.getValue().values().stream().mapToInt(List::size).summaryStatistics();
			final int min = summary.getMin();
			final int max = summary.getMax();
			if (0 == min) {
				// no FV for the current template in at least one rule
				continue;
			}
			if (1 == max) {
				// every rule contains exactly one fv for the current template
				subsets.computeIfAbsent(template, x -> Sets.newIdentityHashSet()).add(
						Collections.singletonList(new FactVariableSubSet(Maps.transformValues(templateToMap.getValue(),
								l -> l.get(0)))));
				continue;
			}
			final List<Set<ICombinatoricsVector<SingleFactVariable>>> generators = new ArrayList<>();
			for (final Entry<Either<Rule, ExistentialProxy>, List<SingleFactVariable>> ruleToFVs : templateToMap
					.getValue().entrySet()) {
				final List<SingleFactVariable> fvs = ruleToFVs.getValue();
				final Set<ICombinatoricsVector<SingleFactVariable>> set = Sets.newIdentityHashSet();
				set.addAll(Factory.createSimpleCombinationGenerator(Factory.createVector(fvs), min)
						.generateAllObjects());
				generators.add(set);
			}
			final Set<List<Map<Either<Rule, ExistentialProxy>, SingleFactVariable>>> listOfMaps =
					Sets.newIdentityHashSet();
			for (final List<ICombinatoricsVector<SingleFactVariable>> list : Sets.cartesianProduct(generators)) {
				final List<Map<Either<Rule, ExistentialProxy>, SingleFactVariable>> currentList = new ArrayList<>();
				// every vector contains $min$ fvs corresponding to the same rule
				for (final ICombinatoricsVector<SingleFactVariable> vector : list) {
					for (int i = 0; i < min; ++i) {
						final SingleFactVariable value = vector.getValue(i);
						final Either<Rule, ExistentialProxy> rule = fvToRule.get(value);
						if (currentList.size() > min) {
							currentList.get(i).put(rule, value);
						} else {
							final IdentityHashMap<Either<Rule, ExistentialProxy>, SingleFactVariable> newMap =
									new IdentityHashMap<>();
							newMap.put(rule, value);
							currentList.add(newMap);
						}
					}
				}
				listOfMaps.add(currentList);
			}
			final Set<List<FactVariableSubSet>> targetSubSets =
					subsets.computeIfAbsent(template, x -> Sets.newIdentityHashSet());
			for (final List<Map<Either<Rule, ExistentialProxy>, SingleFactVariable>> maps : listOfMaps) {
				targetSubSets.add(maps.stream().map(FactVariableSubSet::new).collect(toList()));
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

	protected static void vertical(final Set<Set<FilterInstance>> filterInstancesGroupedByRule,
			final BlockSet resultBlocks) {
		final Set<Set<Set<FilterInstance>>> filterInstancesPowerSet = Sets.powerSet(filterInstancesGroupedByRule);
		final Iterator<Set<Set<FilterInstance>>> iterator = filterInstancesPowerSet.iterator();
		// skip empty set
		iterator.next();
		while (iterator.hasNext()) {
			final ImmutableList<Set<FilterInstance>> powerSetElement = ImmutableList.copyOf(iterator.next());
			final Set<Either<Rule, ExistentialProxy>> rules =
					powerSetElement.stream().map(set -> set.iterator().next().getRuleOrProxy()).collect(toSet());
			final List<FactVariablePartition> partitions = enumerateFactVariablePartitions(rules);
			final Set<List<FilterInstance>> cartesianProduct = Sets.cartesianProduct(powerSetElement);
			for (final List<FilterInstance> filterInstances : cartesianProduct) {
				final FilterInstanceSubSet subSet =
						new FilterInstanceSubSet(Maps.newHashMap(Maps.uniqueIndex(filterInstances,
								FilterInstance::getRuleOrProxy)));
				final FilterInstanceSubSet dualSubSet;
				if (filterInstances.get(0) instanceof ImplicitFilterInstance) {
					assert filterInstances.stream().allMatch(fi -> fi instanceof ImplicitFilterInstance);
					dualSubSet =
							new FilterInstanceSubSet(Maps.newHashMap(filterInstances.stream()
									.collect(
											toMap(FilterInstance::getRuleOrProxy,
													fi -> ((ImplicitFilterInstance) fi).getDual()))));
				} else {
					dualSubSet = null;
				}
				for (final FactVariablePartition partition : partitions) {
					final Block newBlock = new Block(rules, partition);
					if (!newBlock.addColumn(subSet))
						continue;
					if (null != dualSubSet && !newBlock.addColumn(dualSubSet)) {
						continue;
					}
					horizontalRecursion(newBlock, new Stack<>(), resultBlocks);
				}
			}
		}
	}

	protected static void horizontalRecursion(final Block block, final Stack<Set<FilterInstance>> exclusionStack,
			final BlockSet resultBlocks) {
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
		final Map<Filter, List<ImplicitElementFilterInstance>> nRelevantFilterToImplicitElementInstances =
				nTypePartition.getImplicitElementFilterInstances().stream()
						.collect(groupingBy(FilterInstance::getFilter));
		final Map<Filter, List<ImplicitECFilterInstance>> nRelevantFilterToImplicitECInstances =
				nTypePartition.getImplicitECFilterInstances().stream().collect(groupingBy(FilterInstance::getFilter));

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
		final List<FilterInstanceSubSet> matchingFilters = new ArrayList<>();
		final List<Filter> incompatibleFilters = new ArrayList<>();

		// prefer singleCellFilters
		findMatchingAndIncompatibleFilters(nFilterToInstances, bRules, nSingleCellFilters,
				block.filterInstancePartition, block.theta, matchingFilters, incompatibleFilters);
		// if none matched, try multiCellFilters, otherwise defer them
		if (matchingFilters.isEmpty()) {
			findMatchingAndIncompatibleFilters(nFilterToInstances, bRules, nMultiCellFilters,
					block.filterInstancePartition, block.theta, matchingFilters, incompatibleFilters);
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
		for (final Filter incompatibleFilter : incompatibleFilters) {
			furtherExcludes.addAll(nFilterToInstances.get(incompatibleFilter));
		}
		// for every matching filter instance set, create a new block
		for (final FilterInstanceSubSet neighbourSubSet : matchingFilters) {
			final Block newBlock = new Block(block);
			newBlock.addColumn(neighbourSubSet);
			// recurse for that block
			horizontalRecursion(newBlock, exclusionStack, resultBlocks);
			// after the recursion, exclude all filter instances just used
			for (final FilterInstance filterInstance : neighbourSubSet.getElements().values()) {
				furtherExcludes.add(filterInstance);
			}
		}
		// eliminate top layer of the exclusion stack
		exclusionStack.pop();
	}

	protected static void findMatchingAndIncompatibleFilters(
			final Map<Filter, List<FilterInstance>> nFilterToInstances,
			final Set<Either<Rule, ExistentialProxy>> bRules, final List<Filter> nFilters,
			final FilterInstancePartition bFIPartition, final Theta bTheta,
			final List<FilterInstanceSubSet> matchingFilters, final List<Filter> incompatibleFilters) {
		// iterate over every single-/multi-cell filter and check that its instances have the same
		// conflicts in every rule
		for (final Filter nFilter : nFilters) {
			boolean matchingConstellationFound = false;

			// iterate over the possible mappings: (filter,rule) -> filter instance
			final List<Set<FilterInstance>> nListOfRelevantFilterInstancesGroupedByRule =
					new ArrayList<>(nFilterToInstances.get(nFilter).stream()
							.collect(groupingBy(FilterInstance::getRuleOrProxy, toSet())).values());

			// create the cartesian product
			final Set<List<FilterInstance>> nRelevantFilterInstanceCombinations =
					Sets.cartesianProduct(nListOfRelevantFilterInstancesGroupedByRule);
			// iterate over the possible filter instance combinations
			cartesianProductLoop: for (final List<FilterInstance> nCurrentOutsideFilterInstances : nRelevantFilterInstanceCombinations) {
				// create a map for faster lookup: rule -> filter instance (outside)
				final Map<Either<Rule, ExistentialProxy>, FilterInstance> nRuleToCurrentOutsideFilterInstance =
						Maps.uniqueIndex(nCurrentOutsideFilterInstances, FilterInstance::getRuleOrProxy);
				// if only one rule, every filter matches
				if (bRules.size() > 1) {
					// iterate over the block columns
					for (final FilterInstanceSubSet bFilterInstanceSubSet : bFIPartition.getElements()) {
						Conflict firstConflict = null;
						boolean first = true;
						// iterate over the rows aka the rules
						for (final Entry<Either<Rule, ExistentialProxy>, FilterInstance> bRuleToFI : bFilterInstanceSubSet
								.getElements().entrySet()) {
							final Either<Rule, ExistentialProxy> rule = bRuleToFI.getKey();
							final FilterInstance nSource = nRuleToCurrentOutsideFilterInstance.get(rule);
							final FilterInstance bTarget = bRuleToFI.getValue();
							// determine conflict between inside instance and outside instance
							final Conflict conflict = nSource.getConflict(bTarget, bTheta, bTheta);
							// if this is the first loop iteration, just remember the conflict to be
							// compared later on
							if (first) {
								first = false;
								firstConflict = conflict;
							}
							// if the conflicts don't match, continue with next filter
							else if (!hasEqualConflicts(firstConflict, conflict)) {
								continue cartesianProductLoop;
							}
						}
					}
				}
				// conflict identical for all rules
				matchingFilters.add(new FilterInstanceSubSet(Maps.newHashMap(Maps.asMap(bRules,
						rule -> nRuleToCurrentOutsideFilterInstance.get(rule)))));
				matchingConstellationFound = true;
			}
			if (!matchingConstellationFound) {
				incompatibleFilters.add(nFilter);
			}
		}
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
			assert a.getSource() == sourceFilterInstance || b.getSource() == sourceFilterInstance;
			return a.getSource() == sourceFilterInstance ? a : b;
		}

		public Conflict getForTarget(final FilterInstance targetFilterInstance) {
			assert a.getTarget() == targetFilterInstance || b.getTarget() == targetFilterInstance;
			return a.getTarget() == targetFilterInstance ? a : b;
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
				return ConflictEdge.of(sourceVertex, targetVertex, blockTheta, blockTheta);
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
