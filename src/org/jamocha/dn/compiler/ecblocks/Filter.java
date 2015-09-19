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
import static org.jamocha.util.Lambdas.newHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ElementToTemplateSlotLeafTranslator;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Theta;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.VariableExpression;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwatransformer.FWAECLeafToTypeLeafTranslator;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.visitor.Visitable;
import org.jamocha.visitor.Visitor;

import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = { "predicate" })
@ToString(of = { "predicate" })
class Filter implements Visitable<FilterVisitor> {
	final FunctionWithArguments<?> predicate;
	final Map<Either<Rule, ExistentialProxy>, Set<Filter.FilterInstance>> ruleToAllInstances = new HashMap<>();
	final Map<Either<Rule, ExistentialProxy>, Set<ExplicitFilterInstance>> ruleToExplicitInstances = new HashMap<>();
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

	static Filter newEqualityFilter(final VariableExpression left, final VariableExpression right) {
		return newFilter(FWAECLeafToTypeLeafTranslator.translate(GenericWithArgumentsComposite.newPredicateInstance(
				Equals.inClips, left.variableExpression, right.variableExpression)));
	}

	public ExplicitFilterInstance addExplicitInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
			final ECFilter ecFilter) {
		final ArrayList<EquivalenceClass> parameterECs = OrderedECCollector.collect(ecFilter.getFunction());
		final ExplicitFilterInstance instance = new ExplicitFilterInstance(ruleOrProxy, ecFilter, parameterECs);
		this.ruleToExplicitInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
		this.ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
		Util.getFilters(ruleOrProxy).add(Filter.this);
		return instance;
	}

	public static ImplicitElementFilterInstance newImplicitElementInstance(
			final Either<Rule, ExistentialProxy> ruleOrProxy, final Element left, final Element right) {
		final ImplicitElementFilterInstance lrFI =
				newEqualityFilter(left, right).addImplicitElementInstance(ruleOrProxy, left, right);
		final ImplicitElementFilterInstance rlFI =
				newEqualityFilter(right, left).addImplicitElementInstance(ruleOrProxy, right, left);
		lrFI.setDual(rlFI);
		rlFI.setDual(lrFI);
		return lrFI;
	}

	private ImplicitElementFilterInstance addImplicitElementInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
			final Element left, final Element right) {
		final ImplicitElementFilterInstance instance = new ImplicitElementFilterInstance(ruleOrProxy, left, right);
		this.ruleToImplicitElementInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
		this.ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
		Util.getFilters(ruleOrProxy).add(Filter.this);
		return instance;
	}

	public static ImplicitECFilterInstance newImplicitECInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
			final VariableExpression left, final VariableExpression right) {
		final ImplicitECFilterInstance lrFI =
				newEqualityFilter(left, right).addImplicitECInstance(ruleOrProxy, left, right);
		final ImplicitECFilterInstance rlFI =
				newEqualityFilter(right, left).addImplicitECInstance(ruleOrProxy, right, left);
		lrFI.setDual(rlFI);
		rlFI.setDual(lrFI);
		return lrFI;
	}

	private ImplicitECFilterInstance addImplicitECInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
			final VariableExpression left, final VariableExpression right) {
		final ImplicitECFilterInstance instance =
				new ImplicitECFilterInstance(ruleOrProxy, ImmutableList.<EquivalenceClass> builder()
						.addAll(left.ecsInVE).addAll(right.ecsInVE).build(), left, right);
		this.ruleToImplicitECInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
		this.ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet()).add(instance);
		Util.getFilters(ruleOrProxy).add(Filter.this);
		return instance;
	}

	public Set<Filter.FilterInstance> getAllInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return this.ruleToAllInstances.computeIfAbsent(ruleOrProxy, newHashSet());
	}

	public Set<ExplicitFilterInstance> getExplicitInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return this.ruleToExplicitInstances.computeIfAbsent(ruleOrProxy, newHashSet());
	}

	public Set<ImplicitElementFilterInstance> getImplicitElementInstances(
			final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return this.ruleToImplicitElementInstances.computeIfAbsent(ruleOrProxy, newHashSet());
	}

	public Set<ImplicitECFilterInstance> getImplicitECInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return this.ruleToImplicitECInstances.computeIfAbsent(ruleOrProxy, newHashSet());
	}

	@Override
	public <V extends FilterVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	static interface FilterInstanceVisitor extends Visitor {
		public void visit(final ExplicitFilterInstance filterInstance);

		public void visit(final ImplicitElementFilterInstance filterInstance);

		public void visit(final ImplicitECFilterInstance filterInstance);
	}

	static interface FilterInstance extends Visitable<Filter.FilterInstanceVisitor> {
		public Filter getFilter();

		public Either<Rule, ExistentialProxy> getRuleOrProxy();

		public FilterInstance.Conflict getConflict(final Filter.FilterInstance targetFilterInstance,
				final Theta sourceTheta, final Theta targetTheta);

		/**
		 * Returns the filter instances of the same filter within the same rule (result contains the
		 * filter instance this method is called upon).
		 *
		 * @return the filter instances of the same filter within the same rule
		 */
		public Set<? extends Filter.FilterInstance> getSiblings();

		public List<SingleFactVariable> getDirectlyContainedFactVariables();

		public List<EquivalenceClass> getDirectlyContainedEquivalenceClasses();

		FilterInstance.Conflict private_newConflict(final Filter.FilterInstance targetFilterInstance,
				final Theta sourceTheta, final Theta targetTheta);

		FilterInstance.Conflict private_forSource(final ImplicitElementFilterInstance source, final Theta sourceTheta,
				final Theta targetTheta);

		FilterInstance.Conflict private_forSource(final ECFilterInstance source, final Theta sourceTheta,
				final Theta targetTheta);

		/**
		 * A conflict represents the fact that two filter instances are using the same data
		 * (possibly on different parameter positions). The parameters of the enclosing instance are
		 * compared to the parameters of the given target instance.
		 * <p>
		 * It holds for every pair c in {@code samePathsIndices} that parameter {@code c.left} of
		 * the enclosing filter instance uses the same {@link Path} as parameter {@code c.right} of
		 * the target filter instance.
		 *
		 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
		 */
		@RequiredArgsConstructor
		@Getter
		static class Conflict {
			// left refers to the source, right to the target of the conflicts
			final Set<Pair<Integer, Integer>> intersectingECsIndices;
			final Filter.FilterInstance source, target;

			public boolean hasEqualConflicts(final FilterInstance.Conflict other) {
				if (null == other)
					return false;
				if (this == other)
					return true;
				return this.intersectingECsIndices == other.intersectingECsIndices
						|| (this.intersectingECsIndices != null && this.intersectingECsIndices
								.equals(other.intersectingECsIndices));
			}
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	abstract class AbstractFilterInstance implements Filter.FilterInstance {
		final Either<Rule, ExistentialProxy> ruleOrProxy;
		final Map<Filter.FilterInstance, FilterInstance.Conflict> conflicts = new HashMap<>();

		protected FilterInstance.Conflict newConflict(final ImplicitElementFilterInstance source,
				final ImplicitElementFilterInstance target) {
			final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
			final SingleFactVariable s0 = source.left.getFactVariable();
			final SingleFactVariable s1 = source.right.getFactVariable();
			final SingleFactVariable t0 = target.left.getFactVariable();
			final SingleFactVariable t1 = target.right.getFactVariable();
			if (null != s0) {
				if (s0 == t0) {
					intersectingECsIndices.add(Pair.of(0, 0));
				}
				if (s0 == t1) {
					intersectingECsIndices.add(Pair.of(0, 1));
				}
			}
			if (null != s1) {
				if (s1 == t0) {
					intersectingECsIndices.add(Pair.of(1, 0));
				}
				if (s1 == t1) {
					intersectingECsIndices.add(Pair.of(1, 1));
				}
			}
			return new Conflict(intersectingECsIndices, source, target);
		}

		protected FilterInstance.Conflict newConflict(final ImplicitElementFilterInstance source,
				final ECFilterInstance target, final Theta sourceTheta, final Theta targetTheta) {
			return newConflict(target, source, targetTheta, sourceTheta, true);
		}

		protected FilterInstance.Conflict newConflict(final ECFilterInstance source,
				final ImplicitElementFilterInstance target, final Theta sourceTheta, final Theta targetTheta) {
			return newConflict(source, target, sourceTheta, targetTheta, false);
		}

		protected FilterInstance.Conflict newConflict(final ECFilterInstance source,
				final ImplicitElementFilterInstance target, final Theta sourceTheta,
				@SuppressWarnings("unused") final Theta targetTheta, final boolean reverse) {
			final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
			final List<EquivalenceClass> sourceParameters = source.parameters;
			final SingleFactVariable left = target.left.getFactVariable();
			final SingleFactVariable right = target.right.getFactVariable();
			final int size = sourceParameters.size();
			for (int i = 0; i < size; ++i) {
				final Set<SingleFactVariable> sourceFVs =
						sourceTheta.getDependentFactVariables(sourceParameters.get(i));
				if (sourceFVs.contains(left)) {
					intersectingECsIndices.add(reverse ? Pair.of(0, i) : Pair.of(i, 0));
				}
				if (sourceFVs.contains(right)) {
					intersectingECsIndices.add(reverse ? Pair.of(1, i) : Pair.of(i, 1));
				}
			}
			return new Conflict(intersectingECsIndices, source, target);
		}

		protected FilterInstance.Conflict newConflict(final ECFilterInstance source, final ECFilterInstance target,
				final Theta sourceTheta, final Theta targetTheta) {
			final Set<Pair<Integer, Integer>> intersectingECsIndices = new HashSet<>();
			final List<EquivalenceClass> sourceParameters = source.parameters;
			final List<EquivalenceClass> targetParameters = target.parameters;
			final List<Set<SingleFactVariable>> targetFVsList =
					targetParameters.stream().map(targetTheta::getDependentFactVariables).collect(toList());
			for (int i = 0; i < sourceParameters.size(); ++i) {
				final Set<SingleFactVariable> sourceFVs =
						sourceTheta.getDependentFactVariables(sourceParameters.get(i));
				for (int j = 0; j < targetParameters.size(); ++j) {
					final Set<SingleFactVariable> targetFVs = targetFVsList.get(j);
					if (Collections.disjoint(sourceFVs, targetFVs))
						continue;
					intersectingECsIndices.add(Pair.of(i, j));
				}
			}
			return new Conflict(intersectingECsIndices, source, target);
		}

		@Override
		public FilterInstance.Conflict getConflict(final Filter.FilterInstance targetFilterInstance,
				final Theta sourceTheta, final Theta targetTheta) {
			final FilterInstance.Conflict conflict =
					private_newConflict(targetFilterInstance, sourceTheta, targetTheta);
			if (conflict.intersectingECsIndices.isEmpty()) {
				return null;
			}
			return conflict;
		}

		@Override
		public Filter getFilter() {
			return Filter.this;
		}
	}

	static interface ImplicitFilterInstance extends Filter.FilterInstance {
		public Element getLeft();

		public Element getRight();

		public Filter.ImplicitFilterInstance getDual();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	// no EqualsAndHashCode
	class ImplicitElementFilterInstance extends AbstractFilterInstance implements Filter.ImplicitFilterInstance {
		final Element left, right;
		@Setter(AccessLevel.PRIVATE)
		private ImplicitElementFilterInstance dual;
		@Getter(onMethod = @__({ @Override }))
		private final List<SingleFactVariable> directlyContainedFactVariables;

		private ImplicitElementFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy, final Element left,
				final Element right) {
			super(ruleOrProxy);
			this.left = left;
			this.right = right;
			final Builder<SingleFactVariable> builder = ImmutableList.builder();
			Optional.ofNullable(left.getFactVariable()).ifPresent(builder::add);
			Optional.ofNullable(right.getFactVariable()).ifPresent(builder::add);
			this.directlyContainedFactVariables = builder.build();
		}

		@Override
		public String toString() {
			return "[= " + Objects.toString(this.left) + " " + Objects.toString(this.right) + "]";
		}

		@Override
		public ImplicitElementFilterInstance getDual() {
			return this.dual;
		}

		@Override
		public <V extends Filter.FilterInstanceVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public FilterInstance.Conflict private_newConflict(final Filter.FilterInstance targetFilterInstance,
				final Theta sourceTheta, final Theta targetTheta) {
			return targetFilterInstance.private_forSource(this, sourceTheta, targetTheta);
		}

		@Override
		public FilterInstance.Conflict private_forSource(final ImplicitElementFilterInstance source,
				final Theta sourceTheta, final Theta targetTheta) {
			return newConflict(source, this);
		}

		@Override
		public FilterInstance.Conflict private_forSource(final ECFilterInstance source, final Theta sourceTheta,
				final Theta targetTheta) {
			return newConflict(source, this, sourceTheta, targetTheta);
		}

		@Override
		public Set<ImplicitElementFilterInstance> getSiblings() {
			return getImplicitElementInstances(this.ruleOrProxy);
		}

		@Override
		public List<EquivalenceClass> getDirectlyContainedEquivalenceClasses() {
			return ImmutableList.of(this.left.getEquivalenceClass(), this.right.getEquivalenceClass());
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	protected abstract class ECFilterInstance extends AbstractFilterInstance {
		final List<EquivalenceClass> parameters;

		private ECFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
				final List<EquivalenceClass> parameters) {
			super(ruleOrProxy);
			this.parameters = parameters;
		}

		@Override
		public FilterInstance.Conflict private_newConflict(final Filter.FilterInstance targetFilterInstance,
				final Theta sourceTheta, final Theta targetTheta) {
			return targetFilterInstance.private_forSource(this, sourceTheta, targetTheta);
		}

		@Override
		public FilterInstance.Conflict private_forSource(final ImplicitElementFilterInstance source,
				final Theta sourceTheta, final Theta targetTheta) {
			return newConflict(source, this, sourceTheta, targetTheta);
		}

		@Override
		public FilterInstance.Conflict private_forSource(final ECFilterInstance source, final Theta sourceTheta,
				final Theta targetTheta) {
			return newConflict(source, this, sourceTheta, targetTheta);
		}

		@Override
		public List<SingleFactVariable> getDirectlyContainedFactVariables() {
			return this.parameters.stream().flatMap(ec -> ec.getDirectlyDependentFactVariables().stream())
					.collect(toList());
		}

		@Override
		public List<EquivalenceClass> getDirectlyContainedEquivalenceClasses() {
			return this.parameters;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	// no EqualsAndHashCode
	class ImplicitECFilterInstance extends ECFilterInstance implements Filter.ImplicitFilterInstance {
		final VariableExpression left, right;
		@Setter(AccessLevel.PRIVATE)
		private ImplicitECFilterInstance dual;

		private ImplicitECFilterInstance(final Either<Rule, ExistentialProxy> ruleOrProxy,
				final List<EquivalenceClass> parameters, final VariableExpression left, final VariableExpression right) {
			super(ruleOrProxy, parameters);
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return "[= " + Objects.toString(this.left) + " " + Objects.toString(this.right) + "]";
		}

		@Override
		public ImplicitECFilterInstance getDual() {
			return this.dual;
		}

		@Override
		public <V extends Filter.FilterInstanceVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public Set<ImplicitECFilterInstance> getSiblings() {
			return getImplicitECInstances(this.ruleOrProxy);
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
			return Objects.toString(this.ecFilter);
		}

		@Override
		public <V extends Filter.FilterInstanceVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public Set<ExplicitFilterInstance> getSiblings() {
			return getExplicitInstances(this.ruleOrProxy);
		}
	}
}