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

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.newIdentityHashSet;
import static org.jamocha.util.Lambdas.toArrayList;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.jamocha.util.Lambdas.toSingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.list.CursorableLinkedList;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Block;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ConstantExpression;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ConstantExpressionCollector;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ElementVisitor;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ExistentialProxy;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FactBinding;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ECFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ExplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ImplicitECFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Filter.ImplicitElementFilterInstance;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterInstancePartition.FilterInstanceSubSet;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterInstanceTypePartitioner;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterProxy;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FilterVisitor;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Partition.SubSet;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Rule;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.SlotBinding;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Theta;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.VariableExpression;
import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwatransformer.FWAECLeafToPathTranslator;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.util.ToArray;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.atlassian.fugue.Either;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECBlocksToPathRule {
	static class Mergeable<T> {
		final IdentityHashMap<T, Set<T>> tToJoinedWith = new IdentityHashMap<>();

		Set<T> getSet(final T instance) {
			assert null != instance;
			return tToJoinedWith.computeIfAbsent(instance, toSingleton());
		}

		Set<T> mergeII(final Collection<Set<T>> toMerge) {
			return merge(toMerge.stream().map(s -> s.iterator().next()).collect(toIdentityHashSet()));
		}

		Set<T> mergeII(final Set<T> a, final Set<T> b) {
			final Set<T> merged = Sets.newIdentityHashSet();
			merged.addAll(tToJoinedWith.remove(a.iterator().next()));
			merged.addAll(tToJoinedWith.remove(b.iterator().next()));
			merged.forEach(m -> tToJoinedWith.put(m, merged));
			return merged;
		}

		Set<T> merge(final Collection<T> toMerge) {
			final Set<T> merged =
					toMerge.stream().flatMap(s -> tToJoinedWith.remove(s).stream()).collect(toIdentityHashSet());
			merged.forEach(m -> tToJoinedWith.put(m, merged));
			return merged;
		}

		Set<T> merge(final T a, final T b) {
			final Set<T> merged = Sets.newIdentityHashSet();
			merged.addAll(tToJoinedWith.remove(a));
			merged.addAll(tToJoinedWith.remove(b));
			merged.forEach(m -> tToJoinedWith.put(m, merged));
			return merged;
		}

		boolean isMerged(final Collection<T> check) {
			final Iterator<T> iterator = check.iterator();
			final Set<T> first = getSet(iterator.next());
			while (iterator.hasNext()) {
				if (first != getSet(iterator.next()))
					return false;
			}
			return true;
		}

		boolean isMerged(final T a, final T b) {
			return getSet(a) == getSet(b);
		}
	}

	static class MergeableMapper<T, V> {
		final IdentityHashMap<Set<T>, V> setToTarget = new IdentityHashMap<>();
		final Mergeable<T> mergeable = new Mergeable<>();

		void mergeII(final Collection<Set<T>> toMerge, final V newTarget) {
			final Set<T> merged = mergeable.mergeII(toMerge);
			for (final Set<T> set : toMerge) {
				setToTarget.remove(set);
			}
			setToTarget.put(merged, newTarget);
		}

		void merge(final Collection<T> toMerge, final V newTarget) {
			final Set<T> merged = mergeable.merge(toMerge);
			for (final T t : toMerge) {
				setToTarget.remove(mergeable.getSet(t));
			}
			setToTarget.put(merged, newTarget);
		}

		V getTarget(final T instance) {
			return setToTarget.get(mergeable.getSet(instance));
		}

		V getTarget(final Set<T> merged) {
			return setToTarget.get(merged);
		}

		void setTarget(final T instance, final V value) {
			setToTarget.put(mergeable.getSet(instance), value);
		}
	}

	static class RuleInfo {
		// stores the current equivalence class restriction applied
		final Mergeable<Element> elements = new Mergeable<>();
		// stores the mapping of an already constructed set of filter instances to the
		// corresponding ECFilter-thingy
		final IdentityHashMap<FilterInstance, List<PathFilterList>> joinedWithToComponent = new IdentityHashMap<>();
		// stores which fact variables have been joined together
		final Mergeable<SingleFactVariable> factVariables = new Mergeable<>();
		// fv to path mapping - determined once
		final IdentityHashMap<SingleFactVariable, Path> fvToPath;

		public RuleInfo(final Either<Rule, ExistentialProxy> rule,
				final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo) {
			if (rule.isLeft()) {
				fvToPath =
						new IdentityHashMap<>(Maps.asMap(rule.left().get().original.getFactVariables(), fv -> new Path(
								fv.getTemplate())));
			} else {
				final ExistentialProxy existentialProxy = rule.right().get();
				fvToPath =
						new IdentityHashMap<>(ruleToInfo.computeIfAbsent(existentialProxy.rule.either,
								r -> new RuleInfo(r, ruleToInfo)).fvToPath);
				for (final SingleFactVariable factVariable :
				// Sets.union(
				existentialProxy.existential.getExistentialFactVariables()
				// ,Collections.singleton(existentialProxy.existential.getInitialFactVariable()) )
				) {
					fvToPath.put(factVariable, new Path(factVariable.getTemplate()));
				}
			}
		}
	}

	@RequiredArgsConstructor
	static class ElementToPathLeafTranslator implements ElementVisitor {
		final RuleInfo ruleInfo;
		FunctionWithArguments<PathLeaf> fwa;

		static PredicateWithArguments<PathLeaf> translate(final ImplicitElementFilterInstance toTranslate,
				final RuleInfo ruleInfo) {
			return PredicateWithArgumentsComposite.newPredicateInstance(Equals.inClips,
					translate(toTranslate.getLeft(), ruleInfo), translate(toTranslate.getRight(), ruleInfo));
		}

		static FunctionWithArguments<PathLeaf> translate(final Element toTranslate, final RuleInfo ruleInfo) {
			return toTranslate.accept(new ElementToPathLeafTranslator(ruleInfo)).fwa;
		}

		@Override
		public void visit(final FactBinding element) {
			fwa = new PathLeaf(ruleInfo.fvToPath.get(element.getFactVariable()), null);
		}

		@Override
		public void visit(final SlotBinding element) {
			fwa = new PathLeaf(ruleInfo.fvToPath.get(element.getFactVariable()), element.getSlot().getSlot());
		}

		@Override
		public void visit(final ConstantExpression element) {
			fwa = new ConstantLeaf<PathLeaf>(element.constant);
		}

		@Override
		public void visit(final VariableExpression element) {
		}
	}

	@RequiredArgsConstructor
	static class ExplicitFilterCreator implements FilterVisitor {
		final Block block;
		final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo;
		final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant;
		final ExplicitFilterInstance exampleFilterInstance;

		boolean created = false;

		public static boolean create(final Block block,
				final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo,
				final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant,
				final ExplicitFilterInstance exampleFilterInstance) {
			return exampleFilterInstance.getFilter().accept(
					new ExplicitFilterCreator(block, ruleToInfo, blockEC2Constant, exampleFilterInstance)).created;
		}

		@Override
		public void visit(final Filter filter) {
			final List<Set<SingleFactVariable>> listOfExampleFVsToChooseFrom =
					getECIntersections(blockEC2Constant, block.theta,
							ruleToInfo.get(exampleFilterInstance.getRuleOrProxy()), exampleFilterInstance);
			if (listOfExampleFVsToChooseFrom.isEmpty()) {
				return;
			}
			for (final Set<SingleFactVariable> exampleFVsToChooseFrom : listOfExampleFVsToChooseFrom) {
				final FilterInstanceSubSet filterInstanceSubSet =
						block.filterInstancePartition.lookup(exampleFilterInstance);
				final Collection<FactVariableSubSet> factVariableSubSets =
						Collections2.transform(exampleFVsToChooseFrom, block.factVariablePartition::lookup);
				for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
					final ExplicitFilterInstance filterInstance =
							(ExplicitFilterInstance) filterInstanceSubSet.get(rule);
					final RuleInfo ruleInfo = ruleToInfo.get(rule);
					final Set<SingleFactVariable> fvsToChooseFrom =
							factVariableSubSets.stream().map(fvss -> fvss.get(rule)).collect(toIdentityHashSet());
					final ImmutableMap<EquivalenceClass, FunctionWithArguments<PathLeaf>> map =
							Maps.toMap(
									Sets.newHashSet(filterInstance.getParameters()),
									param -> getMatchingElement(param, block, blockEC2Constant, ruleInfo,
											fvsToChooseFrom));
					final PredicateWithArguments<PathLeaf> pwa =
							FWAECLeafToPathTranslator.translate(filterInstance.ecFilter.getFunction(), map);
					ruleInfo.joinedWithToComponent.get(filterInstance).add(
							PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(pwa)));
				}
			}
			created = true;
		}

		@Override
		public void visit(final FilterProxy filter) {
			assert exampleFilterInstance.getRuleOrProxy().isLeft() : "Nested Existentials Unsupported!";
			final Set<SingleFactVariable> regularSet;
			{
				final Either<Rule, ExistentialProxy> exampleRule = exampleFilterInstance.getRuleOrProxy();
				final ExistentialProxy existentialProxy =
						exampleRule.left().get().getExistentialProxies().get(exampleFilterInstance);
				final ECExistentialSet existential = existentialProxy.getExistential();
				final RuleInfo ruleInfo = ruleToInfo.get(exampleRule);

				final Set<EquivalenceClass> existentialECs = existential.getEquivalenceClasses();
				final List<EquivalenceClass> regularParams;
				{
					final Set<EquivalenceClass> parameters = Sets.newHashSet(exampleFilterInstance.getParameters());
					parameters.removeIf(blockEC2Constant::containsKey);

					final Map<Boolean, List<EquivalenceClass>> tmp =
							parameters.stream().collect(partitioningBy(existentialECs::contains));
					regularParams = tmp.get(Boolean.FALSE);
				}
				{
					final Map<Set<SingleFactVariable>, Long> regularFVCounter =
							regularParams
									.stream()
									.flatMap(
											ec -> block.theta.reduce(ec).stream().map(Element::getFactVariable)
													.filter(Objects::nonNull).map(ruleInfo.factVariables::getSet)
													.distinct()).collect(groupingBy(Function.identity(), counting()));
					final Optional<Set<SingleFactVariable>> findAny =
							regularFVCounter.entrySet().stream()
									.filter(entry -> entry.getValue().intValue() == regularParams.size())
									.map(Entry::getKey).findAny();
					if (!findAny.isPresent()) {
						return;
					}
					regularSet = findAny.get();
				}
			}

			final FilterInstanceSubSet filterInstanceSubSet =
					block.filterInstancePartition.lookup(exampleFilterInstance);
			final Collection<FactVariableSubSet> regularFVSubSets =
					Collections2.transform(regularSet, block.factVariablePartition::lookup);

			for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
				final ExplicitFilterInstance filterInstance = (ExplicitFilterInstance) filterInstanceSubSet.get(rule);
				final ExistentialProxy existentialProxy = rule.left().get().getExistentialProxies().get(filterInstance);
				final ECExistentialSet existential = existentialProxy.getExistential();
				final RuleInfo proxyInfo = ruleToInfo.get(existentialProxy.either);
				final RuleInfo ruleInfo = ruleToInfo.get(rule);

				final Set<SingleFactVariable> regularFVsToChooseFrom =
						regularFVSubSets.stream().map(fvss -> fvss.get(rule)).collect(toIdentityHashSet());

				final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> fixedArgs = new IdentityHashMap<>();
				final Set<EquivalenceClass> existentialECs = existential.getEquivalenceClasses();
				{
					final Set<EquivalenceClass> parameters = Sets.newHashSet(exampleFilterInstance.getParameters());
					parameters.removeIf(blockEC2Constant::containsKey);
					for (final EquivalenceClass parameter : parameters) {
						if (existentialECs.contains(parameter)) {
							fixedArgs.put(parameter, createBinding(parameter, proxyInfo.fvToPath));
						} else {
							fixedArgs.put(
									parameter,
									getMatchingElement(parameter, block, blockEC2Constant, ruleInfo,
											regularFVsToChooseFrom));
						}
					}
				}

				final PredicateWithArguments<PathLeaf> pwa =
						FWAECLeafToPathTranslator.translate(filterInstance.ecFilter.getFunction(), fixedArgs);

				final Path initialPath = proxyInfo.fvToPath.get(existential.getInitialFactVariable());
				final Set<Path> existentialPaths =
						existential.getExistentialFactVariables().stream().map(proxyInfo.fvToPath::get)
								.collect(toSet());

				final PathFilterList purePart = PathFilterList.toSimpleList(Collections.emptyList());
				ruleInfo.joinedWithToComponent.get(filterInstance).add(
						new PathExistentialList(initialPath, purePart, PathNodeFilterSet
								.newExistentialPathNodeFilterSet(!existential.isPositive(), existentialPaths,
										new PathFilter(pwa))));
				created = true;
			}
		}
	}

	static List<PathRule> compile(final List<Either<Rule, ExistentialProxy>> rules,
			final TreeMap<Integer, CursorableLinkedList<Block>> blockMap) {
		// rule infos
		final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo = new IdentityHashMap<>();
		// initialize paths
		for (final Either<Rule, ExistentialProxy> rule : rules) {
			ruleToInfo.computeIfAbsent(rule, x -> new RuleInfo(x, ruleToInfo));
		}

		// the set of all FIs already constructed
		final IdentityHashMap<Either<Rule, ExistentialProxy>, Set<FilterInstance>> representedFIsByRule =
				new IdentityHashMap<>();

		// at this point, the network can be constructed
		for (final CursorableLinkedList<Block> blockList : blockMap.values()) {
			for (final Block block : blockList) {
				final ImmutableList<Either<Rule, ExistentialProxy>> blockRules =
						ImmutableList.copyOf(block.getRulesOrProxies());

				// initialize all entries in joinedWithToComponent to point to a single list
				// containing all elements previously created
				for (final Entry<Either<Rule, ExistentialProxy>, List<FilterInstance>> entry : block
						.getFlatFilterInstances().stream().collect(groupingBy(FilterInstance::getRuleOrProxy))
						.entrySet()) {
					final Either<Rule, ExistentialProxy> key = entry.getKey();
					final List<FilterInstance> fis = entry.getValue();
					final IdentityHashMap<FilterInstance, List<PathFilterList>> joinedWithToComponent =
							ruleToInfo.get(key).joinedWithToComponent;
					final ArrayList<PathFilterList> combined =
							fis.stream().map(joinedWithToComponent::get).filter(Objects::nonNull)
									.collect(toIdentityHashSet()).stream().flatMap(List::stream).collect(toArrayList());
					fis.forEach(fi -> joinedWithToComponent.put(fi, combined));
				}

				final Either<Rule, ExistentialProxy> chosenRule = blockRules.get(0);
				final RuleInfo chosenRuleInfo = ruleToInfo.get(chosenRule);

				final FilterInstanceTypePartitioner chosenTypePartition =
						FilterInstanceTypePartitioner.partition(block.getFlatFilterInstances().stream()
								.filter(fi -> chosenRule == fi.getRuleOrProxy()).collect(toList()));
				final Set<FilterInstance> representedFIs =
						representedFIsByRule.computeIfAbsent(chosenRule, newIdentityHashSet());
				final List<ExplicitFilterInstance> chosenEFIs =
						ListUtils.removeAll(chosenTypePartition.getExplicitFilterInstances(), representedFIs);
				final IdentityHashMap<EquivalenceClass, ArrayList<ImplicitElementFilterInstance>> chosenIEFIsByEC =
						ListUtils
								.removeAll(chosenTypePartition.getImplicitElementFilterInstances(), representedFIs)
								.stream()
								.collect(
										groupingBy(fi -> fi.getLeft().getEquivalenceClass(), IdentityHashMap::new,
												toArrayList()));
				final IdentityHashMap<EquivalenceClass, ArrayList<ImplicitECFilterInstance>> chosenIVFIsByEC =
						ListUtils
								.removeAll(chosenTypePartition.getImplicitECFilterInstances(), representedFIs)
								.stream()
								.collect(
										groupingBy(fi -> fi.getLeft().getEquivalenceClass(), IdentityHashMap::new,
												toArrayList()));

				// initialize all fact variables that are to be joined by this block
				{
					final Mergeable<SingleFactVariable> factVariables = chosenRuleInfo.factVariables;
					if (factVariables.tToJoinedWith.values().isEmpty()) {
						for (final FactVariableSubSet factVariableSubSet : block.factVariablePartition.getElements()) {
							factVariables.getSet(factVariableSubSet.get(chosenRule));
						}
					}
				}

				final Set<EquivalenceClass> blockECs =
						Sets.newHashSet(Sets.union(block.variableExpressionTheta.getEquivalenceClasses(),
								block.theta.getEquivalenceClasses()));

				// first step: construct everything using only one FV

				// for explicit and EC FIs this means checking if there is a FV which is used by all
				// ECs not containing a constant in the reduced version
				final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant =
						new IdentityHashMap<>(blockECs
								.stream()
								.map(ec -> ConstantExpressionCollector.findFirst(block.theta.reduce(ec)))
								.filter(Optional::isPresent)
								.map(Optional::get)
								.collect(
										toMap(Element::getEquivalenceClass,
												e -> Pair.of(e,
														new ConstantLeaf<>(((ConstantExpression) e).getConstant())))));
				{
					// try to fill as many ECs with constants as possible
					boolean changed;
					final HashSet<EquivalenceClass> remainingECs = Sets.newHashSet(blockECs);
					remainingECs.removeAll(blockEC2Constant.keySet());
					do {
						changed = false;
						for (final Iterator<EquivalenceClass> iterator = remainingECs.iterator(); iterator.hasNext();) {
							final EquivalenceClass ec = iterator.next();
							final Optional<Element> optElement =
									block.variableExpressionTheta
											.reduce(ec)
											.stream()
											.filter(e -> ((VariableExpression) e).getEcsInVE().stream()
													.allMatch(eec -> blockEC2Constant.containsKey(eec))).findAny();
							if (optElement.isPresent()) {
								changed = true;
								blockEC2Constant.put(ec, Pair.of(optElement.get(), FWAEvaluatorForConstantECs.evaluate(
										Maps.transformValues(blockEC2Constant, Pair::getRight),
										((VariableExpression) optElement.get()).variableExpression)));
								iterator.remove();
							}
						}
					} while (changed);
				}
				// at this point ec2Constant contains all the equivalence classes that contain a
				// constant

				// create all the implicit element filter instances for all the ECs containing a
				// constant
				// they will always be alpha filters and this only has to be done once
				for (final Entry<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> ecAndConstant : blockEC2Constant
						.entrySet()) {
					final EquivalenceClass ec = ecAndConstant.getKey();
					final Pair<Element, ConstantLeaf<PathLeaf>> pair = ecAndConstant.getValue();
					final Element constantElement = pair.getLeft();
					final ConstantLeaf<PathLeaf> constantValue = pair.getRight();

					final ArrayList<ImplicitElementFilterInstance> iefis = chosenIEFIsByEC.remove(ec);
					if (null == iefis)
						continue;
					for (final Iterator<ImplicitElementFilterInstance> iefisToConstructIter = iefis.iterator(); iefisToConstructIter
							.hasNext();) {
						final ImplicitElementFilterInstance filterInstance = iefisToConstructIter.next();
						if (filterInstance.left != constantElement && filterInstance.right != constantElement) {
							continue;
						}
						if (chosenRuleInfo.elements.isMerged(filterInstance.left, filterInstance.right)) {
							continue;
						}
						createConstantImplicitElementFilterInstance(block, ruleToInfo, constantValue, constantElement,
								filterInstance);
					}
				}

				// iteratively join all FVs and apply all the filter instances that don't need a
				// join any more
				JoinLoop: do {
					for (final Iterator<Entry<EquivalenceClass, ArrayList<ImplicitElementFilterInstance>>> entryIterator =
							chosenIEFIsByEC.entrySet().iterator(); entryIterator.hasNext();) {
						final Entry<EquivalenceClass, ArrayList<ImplicitElementFilterInstance>> entry =
								entryIterator.next();
						final ArrayList<ImplicitElementFilterInstance> list = entry.getValue();
						for (final Iterator<ImplicitElementFilterInstance> filterInstanceIterator = list.iterator(); filterInstanceIterator
								.hasNext();) {
							final ImplicitElementFilterInstance filterInstance = filterInstanceIterator.next();
							if (chosenRuleInfo.elements.isMerged(filterInstance.getLeft(), filterInstance.getRight())) {
								// test not necessary anymore
								filterInstanceIterator.remove();
								continue;
							}
							if (chosenRuleInfo.factVariables.isMerged(filterInstance
									.getDirectlyContainedFactVariables())) {
								// same FV in both args or FVs already joined
								// => apply directly
								createImplicitElementFilterInstance(block, ruleToInfo, filterInstance);
								filterInstanceIterator.remove();
								continue;
							}
						}
						if (list.isEmpty()) {
							entryIterator.remove();
						}
					}
					for (final Iterator<Entry<EquivalenceClass, ArrayList<ImplicitECFilterInstance>>> entryIterator =
							chosenIVFIsByEC.entrySet().iterator(); entryIterator.hasNext();) {
						final Entry<EquivalenceClass, ArrayList<ImplicitECFilterInstance>> entry = entryIterator.next();
						final ArrayList<ImplicitECFilterInstance> list = entry.getValue();
						for (final Iterator<ImplicitECFilterInstance> filterInstanceIterator = list.iterator(); filterInstanceIterator
								.hasNext();) {
							final ImplicitECFilterInstance filterInstance = filterInstanceIterator.next();
							if (checkAndCreateImplicitECFilterInstance(ruleToInfo, block, chosenRuleInfo,
									blockEC2Constant, filterInstance)) {
								// remove filter instance from maps and lists
								filterInstanceIterator.remove();
							}
						}
						if (list.isEmpty()) {
							entryIterator.remove();
						}
					}
					for (final Iterator<ExplicitFilterInstance> filterInstanceIterator = chosenEFIs.iterator(); filterInstanceIterator
							.hasNext();) {
						final ExplicitFilterInstance filterInstance = filterInstanceIterator.next();
						if (ExplicitFilterCreator.create(block, ruleToInfo, blockEC2Constant, filterInstance)) {
							filterInstanceIterator.remove();
						}
					}

					// if all filter instances have been removed, break the loop
					if (chosenIEFIsByEC.isEmpty() && chosenEFIs.isEmpty() && chosenIVFIsByEC.isEmpty()) {
						break JoinLoop;
					}

					// second step: plot a graph with FVs as vertices and edges as join conditions
					// (if any)
					// goal: choose a pair of FVs to join

					final SimpleWeightedGraph<Set<SingleFactVariable>, DefaultWeightedEdge> graph =
							new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

					for (final ArrayList<ImplicitElementFilterInstance> list : chosenIEFIsByEC.values()) {
						for (final ImplicitElementFilterInstance filterInstance : list) {
							final SingleFactVariable fvLeft = filterInstance.left.getFactVariable();
							final SingleFactVariable fvRight = filterInstance.right.getFactVariable();
							assert null != fvLeft && null != fvRight;
							final Set<SingleFactVariable> source = chosenRuleInfo.factVariables.getSet(fvLeft);
							final Set<SingleFactVariable> target = chosenRuleInfo.factVariables.getSet(fvRight);
							graph.addVertex(source);
							graph.addVertex(target);
							final DefaultWeightedEdge newEdge = Graphs.addEdge(graph, source, target, 1);
							if (null == newEdge) {
								// edge existed already
								final DefaultWeightedEdge edge = graph.getEdge(source, target);
								graph.setEdgeWeight(edge, 1 + graph.getEdgeWeight(edge));
							}
						}
					}
					final Optional<DefaultWeightedEdge> max =
							graph.edgeSet().stream().max(Comparator.comparingDouble(graph::getEdgeWeight));
					if (max.isPresent()) {
						// good - we still have implicit tests wanting to join stuff
						final DefaultWeightedEdge defaultWeightedEdge = max.get();
						final Set<SingleFactVariable> source = graph.getEdgeSource(defaultWeightedEdge);
						final Set<SingleFactVariable> target = graph.getEdgeTarget(defaultWeightedEdge);
						chosenRuleInfo.factVariables.mergeII(source, target);
						continue JoinLoop;
					}
					// no more implicit edges suggesting anything
					// can be improved arbitrarily
					for (final ExplicitFilterInstance filterInstance : chosenEFIs) {
						final Set<Set<SingleFactVariable>> components =
								filterInstance.getDirectlyContainedFactVariables().stream()
										.map(chosenRuleInfo.factVariables::getSet).collect(toIdentityHashSet());
						if (components.size() > 1) {
							final Iterator<Set<SingleFactVariable>> iterator = components.iterator();
							final Set<SingleFactVariable> source = iterator.next();
							final Set<SingleFactVariable> target = iterator.next();
							chosenRuleInfo.factVariables.mergeII(source, target);
							continue JoinLoop;
						}
					}
					for (final ArrayList<ImplicitECFilterInstance> filterInstances : chosenIVFIsByEC.values()) {
						for (final ImplicitECFilterInstance filterInstance : filterInstances) {
							final Set<Set<SingleFactVariable>> components =
									filterInstance.getDirectlyContainedFactVariables().stream()
											.map(chosenRuleInfo.factVariables::getSet).collect(toIdentityHashSet());
							if (components.size() > 1) {
								final Iterator<Set<SingleFactVariable>> iterator = components.iterator();
								final Set<SingleFactVariable> source = iterator.next();
								final Set<SingleFactVariable> target = iterator.next();
								chosenRuleInfo.factVariables.mergeII(source, target);
								continue JoinLoop;
							}
						}
					}
					throw new IllegalStateException();
				} while (true); // end of join loop

				// mark all FIs as done
				representedFIs.addAll(block.getFlatFilterInstances());

				// create the shared list stuff
				final PathSharedListWrapper sharedListWrapper = new PathSharedListWrapper(blockRules.size());
				final Map<Either<Rule, ExistentialProxy>, PathSharedList> ruleToSharedList =
						IntStream.range(0, blockRules.size()).boxed()
								.collect(toMap(blockRules::get, sharedListWrapper.getSharedSiblings()::get));

				final Map<PathSharedList, List<PathFilterList>> sharedPart = new HashMap<>();
				final FilterInstanceSubSet someColumn =
						block.getFilterInstancePartition().getElements().iterator().next();
				for (final Either<Rule, ExistentialProxy> rule : blockRules) {
					final List<PathFilterList> list =
							ruleToInfo.get(rule).joinedWithToComponent.get(someColumn.getElements().get(rule));
					sharedPart.put(ruleToSharedList.get(rule), list);
				}
				if (!sharedPart.isEmpty()) {
					sharedListWrapper.addSharedColumns(sharedPart);
				}

				for (final Entry<Either<Rule, ExistentialProxy>, List<FilterInstance>> entry : block
						.getFlatFilterInstances().stream().collect(groupingBy(FilterInstance::getRuleOrProxy))
						.entrySet()) {
					final Either<Rule, ExistentialProxy> key = entry.getKey();
					final List<PathFilterList> pathSharedList =
							Lists.newArrayList(ImmutableList.of(ruleToSharedList.get(key)));
					final IdentityHashMap<FilterInstance, List<PathFilterList>> joinedWithToComponent =
							ruleToInfo.get(key).joinedWithToComponent;
					entry.getValue().forEach(fi -> joinedWithToComponent.put(fi, pathSharedList));
				}
			} // end of block loop
		} // end of block list loop
		final List<PathRule> pathRules = new ArrayList<>();
		for (final Either<Rule, ExistentialProxy> either : rules) {
			if (either.isRight()) {
				continue;
			}
			final Rule rule = either.left().get();
			final List<PathFilterList> pathFilterLists =
					Stream.concat(rule.existentialProxies.values().stream().map(ExistentialProxy::getEither),
							Stream.of(either))
							.flatMap(
									e -> Optional
											.ofNullable(ruleToInfo.get(e))
											.map(ri -> newIdentityHashSet(ri.joinedWithToComponent.values()).stream()
													.<PathFilterList> map(list -> list.get(0))).orElse(Stream.empty()))
							.collect(toList());
			final ECSetRule original = rule.getOriginal();
			final RuleInfo ruleInfo = ruleToInfo.get(either);
			final Set<Path> regularPaths = Sets.newHashSet(ruleInfo.fvToPath.values());
			final Set<Path> resultPaths =
			// pathFilterLists.size() > 1 ? Sets.union(regularPaths,
			// InitialFactPathsFinder.gather(pathFilterLists)) :
					regularPaths;
			final PathFilterList convertedCondition = PathFilterList.toSimpleList(pathFilterLists);

			final Set<EquivalenceClass> ecsInSymbols =
					ActionlistSymbolCollector.getUnboundSymbols(original.getActionList()).stream()
							.map(VariableSymbol::getEqual).collect(toIdentityHashSet());

			final BiMap<EquivalenceClass, EquivalenceClass> conditionECsToLocalECs =
					original.getLocalECsToConditionECs();

			final Map<EquivalenceClass, PathLeaf> ecToPathLeaf =
					ecsInSymbols.stream().collect(
							toMap(Function.identity(),
									ec -> createBinding(conditionECsToLocalECs.inverse().get(ec), ruleInfo.fvToPath)));

			final PathRule pathRule = original.toPathRule(
			/* PathFilterList convertedCondition */convertedCondition,
			/* Set<Path> resultPaths */resultPaths,
			/* Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf */ecToPathLeaf);

			pathRules.add(pathRule);
		}
		return pathRules;
	}

	protected static boolean checkAndCreateImplicitECFilterInstance(
			final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo, final Block block,
			final RuleInfo chosenRuleInfo,
			final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant,
			final ImplicitECFilterInstance filterInstance) {
		final List<Set<SingleFactVariable>> fvSets =
				getECIntersections(blockEC2Constant, block.theta, chosenRuleInfo, filterInstance);
		for (final Set<SingleFactVariable> set : fvSets) {
			// construct and choose stuff that belongs to the current set as
			// representatives for the ECs that don't contain constants
			createImplicitECFilterInstance(block, ruleToInfo, blockEC2Constant, filterInstance, set);
		}
		return !fvSets.isEmpty();
	}

	protected static boolean checkAndCreateExplicitFilterInstance(
			final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo, final Block block,
			final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant,
			final ExplicitFilterInstance filterInstance) {
		return ExplicitFilterCreator.create(block, ruleToInfo, blockEC2Constant, filterInstance);
	}

	private static PathLeaf createBinding(final EquivalenceClass ec, final Map<SingleFactVariable, Path> fvToPath) {
		final SingleFactVariable fv = ec.getFactVariables().peek();
		if (null != fv) {
			return fv.toPathLeaf(fvToPath);
		}
		final SingleSlotVariable sv = ec.getSlotVariables().peek();
		if (null != sv) {
			return sv.toPathLeaf(fvToPath);
		}
		return null;
	}

	/**
	 * returns the fact variables / sets of joined fact variables (according to
	 * chosenRuleInfo.factVariables) that the filter instance can be applied to
	 */
	private static List<Set<SingleFactVariable>> getECIntersections(
			final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant,
			final Theta theta, final RuleInfo ruleInfo, final ECFilterInstance filterInstance) {
		final Set<EquivalenceClass> parameters = Sets.newHashSet(filterInstance.getParameters());
		parameters.removeIf(blockEC2Constant::containsKey);
		final Map<Set<SingleFactVariable>, Long> fvCounter =
				parameters
						.stream()
						.flatMap(
								ec -> theta.reduce(ec).stream().map(Element::getFactVariable).filter(Objects::nonNull)
										.map(ruleInfo.factVariables::getSet).distinct())
						.collect(groupingBy(Function.identity(), counting()));
		final List<Set<SingleFactVariable>> fvSets =
				fvCounter.entrySet().stream().filter(e -> e.getValue().intValue() == parameters.size())
						.map(Entry::getKey).collect(toList());
		return fvSets;
	}

	private static void createImplicitECFilterInstance(final Block block,
			final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo,
			final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant,
			final ImplicitECFilterInstance exampleFilterInstance, final Set<SingleFactVariable> exampleFVsToChooseFrom) {
		final FilterInstanceSubSet filterInstanceSubSet = block.filterInstancePartition.lookup(exampleFilterInstance);
		final Collection<FactVariableSubSet> factVariableSubSets =
				Collections2.transform(exampleFVsToChooseFrom, block.factVariablePartition::lookup);
		for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
			final ImplicitECFilterInstance filterInstance = (ImplicitECFilterInstance) filterInstanceSubSet.get(rule);
			final RuleInfo ruleInfo = ruleToInfo.get(rule);
			final Set<SingleFactVariable> fvsToChooseFrom =
					factVariableSubSets.stream().map(fvss -> fvss.get(rule)).collect(toIdentityHashSet());
			final List<FunctionWithArguments<PathLeaf>> pathParameters =
					filterInstance
							.getParameters()
							.stream()
							.map(param -> getMatchingElement(param, block, blockEC2Constant, ruleInfo, fvsToChooseFrom))
							.collect(toList());
			assert !pathParameters.stream().anyMatch(Objects::isNull);
			final PredicateWithArguments<PathLeaf> pwa =
					PredicateWithArgumentsComposite.newPredicateInstance(Equals.inClips, ToArray
							.<FunctionWithArguments<PathLeaf>> toArray(pathParameters, FunctionWithArguments[]::new));
			ruleInfo.joinedWithToComponent.get(filterInstance).add(
					PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(pwa)));
		}
	}

	private static FunctionWithArguments<PathLeaf> getMatchingElement(final EquivalenceClass ec, final Block block,
			final IdentityHashMap<EquivalenceClass, Pair<Element, ConstantLeaf<PathLeaf>>> blockEC2Constant,
			final RuleInfo ruleInfo, final Set<SingleFactVariable> fvsToChooseFrom) {
		final Pair<Element, ConstantLeaf<PathLeaf>> constant = blockEC2Constant.get(ec);
		if (null != constant) {
			return constant.getRight();
		}
		final Set<Element> elements = block.theta.reduce(ec);
		final Optional<Element> optSlotOrFactBinding =
				elements.stream()
						.filter(e -> e.getFactVariable() != null && fvsToChooseFrom.contains(e.getFactVariable()))
						.findAny();
		if (optSlotOrFactBinding.isPresent()) {
			return ElementToPathLeafTranslator.translate(optSlotOrFactBinding.get(), ruleInfo);
		}
		return null;
	}

	private static void createImplicitElementFilterInstance(final Block block,
			final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo,
			final ImplicitElementFilterInstance exampleFilterInstance) {
		final FilterInstanceSubSet filterInstanceSubSet = block.filterInstancePartition.lookup(exampleFilterInstance);
		for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
			final RuleInfo ruleInfo = ruleToInfo.get(rule);
			final ImplicitElementFilterInstance filterInstance =
					(ImplicitElementFilterInstance) filterInstanceSubSet.get(rule);
			final PredicateWithArguments<PathLeaf> translated =
					ElementToPathLeafTranslator.translate(filterInstance, ruleInfo);
			final PathNodeFilterSet pathNodeFilterSet =
					PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(translated));
			ruleInfo.joinedWithToComponent.get(filterInstance).add(pathNodeFilterSet);
			ruleInfo.elements.merge(filterInstance.left, filterInstance.right);
		}
	}

	private static void createConstantImplicitElementFilterInstance(final Block block,
			final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo,
			final ConstantLeaf<PathLeaf> constantValue, final Element exampleConstantElement,
			final ImplicitElementFilterInstance exampleFilterInstance) {
		final SubSet<Element> elementSubSet = block.elementPartition.lookup(exampleConstantElement);
		final FilterInstanceSubSet filterInstanceSubSet = block.filterInstancePartition.lookup(exampleFilterInstance);
		for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
			final RuleInfo ruleInfo = ruleToInfo.get(rule);
			final Element constantElement = elementSubSet.get(rule);
			final ImplicitElementFilterInstance filterInstance =
					(ImplicitElementFilterInstance) filterInstanceSubSet.get(rule);
			final FunctionWithArguments<PathLeaf> translated =
					ElementToPathLeafTranslator.translate(filterInstance.left == constantElement ? filterInstance.right
							: filterInstance.left, ruleInfo);
			final PathNodeFilterSet pathNodeFilterSet =
					PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(PredicateWithArgumentsComposite
							.newPredicateInstance(Equals.inClips, translated, constantValue)));
			ruleInfo.joinedWithToComponent.get(filterInstance).add(pathNodeFilterSet);
			ruleInfo.elements.merge(filterInstance.left, filterInstance.right);
		}
	}
}
