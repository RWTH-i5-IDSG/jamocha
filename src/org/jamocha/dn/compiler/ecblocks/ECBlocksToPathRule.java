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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.jamocha.util.Lambdas.toArrayList;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.jamocha.util.Lambdas.toSingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.jamocha.filter.ECFilterList.ECSharedListWrapper;
import org.jamocha.filter.ECFilterList.ECSharedListWrapper.ECSharedList;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.util.ToArray;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.atlassian.fugue.Either;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECBlocksToPathRule {
	static class Mergeable<T> {
		final IdentityHashMap<T, Set<T>> tToJoinedWith = new IdentityHashMap<>();

		public Set<T> getSet(final T instance) {
			assert null != instance;
			return tToJoinedWith.computeIfAbsent(instance, toSingleton());
		}

		public Set<T> mergeII(final Collection<Set<T>> toMerge) {
			return merge(toMerge.stream().map(s -> s.iterator().next()).collect(toIdentityHashSet()));
		}

		public Set<T> mergeII(final Set<T> a, final Set<T> b) {
			final Set<T> merged = Sets.newIdentityHashSet();
			merged.addAll(tToJoinedWith.remove(a.iterator().next()));
			merged.addAll(tToJoinedWith.remove(b.iterator().next()));
			merged.forEach(m -> tToJoinedWith.put(m, merged));
			return merged;
		}

		public Set<T> merge(final Collection<T> toMerge) {
			final Set<T> merged =
					toMerge.stream().flatMap(s -> tToJoinedWith.remove(s).stream()).collect(toIdentityHashSet());
			merged.forEach(m -> tToJoinedWith.put(m, merged));
			return merged;
		}

		public Set<T> merge(final T a, final T b) {
			final Set<T> merged = Sets.newIdentityHashSet();
			merged.addAll(tToJoinedWith.remove(a));
			merged.addAll(tToJoinedWith.remove(b));
			merged.forEach(m -> tToJoinedWith.put(m, merged));
			return merged;
		}

		public boolean isMerged(final Collection<T> check) {
			final Iterator<T> iterator = check.iterator();
			final Set<T> first = getSet(iterator.next());
			while (iterator.hasNext()) {
				if (first != getSet(iterator.next()))
					return false;
			}
			return true;
		}

		public boolean isMerged(final T a, final T b) {
			return getSet(a) == getSet(b);
		}
	}

	static class MergeableMapper<T, V> extends Mergeable<T> {
		final IdentityHashMap<Set<T>, V> setToTarget = new IdentityHashMap<>();

		public void mergeII(final Collection<Set<T>> toMerge, final Function<Set<V>, V> toNewTarget) {
			final Set<T> merged = mergeII(toMerge);
			final V newTarget =
					toNewTarget.apply(toMerge.stream().map(s -> setToTarget.remove(s)).collect(toIdentityHashSet()));
			setToTarget.put(merged, newTarget);
		}

		public void merge(final Collection<T> toMerge, final Function<Set<V>, V> toNewTarget) {
			final Set<T> merged = merge(toMerge);
			final V newTarget =
					toNewTarget.apply(toMerge.stream().map(s -> setToTarget.remove(s)).collect(toIdentityHashSet()));
			setToTarget.put(merged, newTarget);
		}

		public V getTarget(final T instance) {
			return setToTarget.get(getSet(instance));
		}

		public V getTarget(final Set<T> merged) {
			return setToTarget.get(merged);
		}

		public void setTarget(final T instance, final V value) {
			setToTarget.put(getSet(instance), value);
		}
	}

	static class RuleInfo {
		// stores the current equivalence class restriction applied
		final Mergeable<Element> elements = new Mergeable<>();
		// stores the mapping of an already constructed set of filter instances to the
		// corresponding ECFilter-thingy
		final MergeableMapper<FilterInstance, PathFilterList> joinedWithToComponent = new MergeableMapper<>();
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
				for (final SingleFactVariable factVariable : existentialProxy.existential.getExistentialFactVariables()) {
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
		final List<Set<SingleFactVariable>> listOfExampleFVsToChooseFrom;

		@Override
		public void visit(final Filter filter) {
			for (final Set<SingleFactVariable> exampleFVsToChooseFrom : listOfExampleFVsToChooseFrom) {
				final FilterInstanceSubSet filterInstanceSubSet =
						block.filterInstancePartition.lookup(exampleFilterInstance);
				final Collection<FactVariableSubSet> factVariableSubSets =
				// Collections2.transform(exampleFVsToChooseFrom,
				// block.factVariablePartition::lookup);
						exampleFVsToChooseFrom.stream().map(block.factVariablePartition::lookup).collect(toList());
				for (final Either<Rule, ExistentialProxy> rule : block.getRulesOrProxies()) {
					final Set<SingleFactVariable> debugFVs = rule.left().get().original.getFactVariables();
					if (!(debugFVs.containsAll(exampleFVsToChooseFrom) || Collections.disjoint(debugFVs, exampleFVsToChooseFrom))) {
						System.out.println("man!");
					}
					
					final ExplicitFilterInstance filterInstance =
							(ExplicitFilterInstance) filterInstanceSubSet.get(rule);
					final RuleInfo ruleInfo = ruleToInfo.get(rule);
					final Set<SingleFactVariable> fvsToChooseFrom =
							factVariableSubSets.stream().map(fvss -> fvss.get(rule)).collect(toIdentityHashSet());
					final List<FunctionWithArguments<PathLeaf>> pathParameters =
							filterInstance
									.getParameters()
									.stream()
									.map(param -> getMatchingElement(param, block, blockEC2Constant, ruleInfo,
											fvsToChooseFrom)).collect(toList());
					assert !pathParameters.stream().anyMatch(Objects::isNull);
					final PredicateWithArguments<PathLeaf> pwa =
							PredicateWithArgumentsComposite.newPredicateInstance(Equals.inClips, ToArray
									.<FunctionWithArguments<PathLeaf>> toArray(pathParameters,
											FunctionWithArguments[]::new));
					ruleInfo.joinedWithToComponent.setTarget(filterInstance,
							PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(pwa)));
				}
			}
		}

		@Override
		public void visit(final FilterProxy filter) {
			// TODO Auto-generated method stub
			// assert filterInstance.getRuleOrProxy().isLeft() : "Nested Existentials Unsupported!";
			// final Rule rule = filterInstance.getRuleOrProxy().left().get();
			// final ExistentialProxy existentialProxy =
			// rule.getExistentialProxies().get(filterInstance);
			// final ECExistentialSet existential = existentialProxy.getExistential();
			// result =
			// new ECFilterList.ECExistentialList(existential.isPositive(),
			// existential.getInitialFactVariable(),
			// existential.getExistentialFactVariables(), ECFilterList.toSimpleList(Collections
			// .emptyList()), new ECNodeFilterSet(instance.getEcFilter()));
		}
	}

	static List<PathRule> compile(final List<Either<Rule, ExistentialProxy>> rules,
			final TreeMap<Integer, CursorableLinkedList<Block>> blockMap) {
		// rule infos
		final IdentityHashMap<Either<Rule, ExistentialProxy>, RuleInfo> ruleToInfo = new IdentityHashMap<>();
		// the set of all FIs already constructed
		final Set<FilterInstance> representedFIs = new HashSet<>();

		// at this point, the network can be constructed
		for (final CursorableLinkedList<Block> blockList : blockMap.values()) {
			for (final Block block : blockList) {
				final ImmutableList<Either<Rule, ExistentialProxy>> blockRules =
						ImmutableList.copyOf(block.getRulesOrProxies());
				// since we are considering blocks, it is either the case that all filter
				// instances of the column have been constructed or none of them have
				final ECSharedListWrapper sharedListWrapper = new ECSharedListWrapper(blockRules.size());
				final Map<Either<Rule, ExistentialProxy>, ECSharedList> ruleToSharedList =
						IntStream.range(0, blockRules.size()).boxed()
								.collect(toMap(blockRules::get, sharedListWrapper.getSharedSiblings()::get));

				final Either<Rule, ExistentialProxy> chosenRule = blockRules.get(0);
				final RuleInfo chosenRuleInfo =
						ruleToInfo.computeIfAbsent(chosenRule, x -> new RuleInfo(x, ruleToInfo));

				final FilterInstanceTypePartitioner chosenTypePartition =
						FilterInstanceTypePartitioner.partition(block.getFlatFilterInstances().stream()
								.filter(fi -> chosenRule == fi.getRuleOrProxy()).collect(toList()));
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
											.filter(e -> ((VariableExpression) e).getEcsInTranslated().stream()
													.allMatch(eec -> blockEC2Constant.containsKey(eec))).findAny();
							if (optElement.isPresent()) {
								changed = true;
								blockEC2Constant.put(ec, Pair.of(optElement.get(), FWAEvaluatorForConstantECs.evaluate(
										Maps.transformValues(blockEC2Constant, Pair::getRight),
										((VariableExpression) optElement.get()).translated)));
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

					for (final Iterator<ImplicitElementFilterInstance> iefisToConstructIter =
							chosenIEFIsByEC.remove(ec).iterator(); iefisToConstructIter.hasNext();) {
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
				do {
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
					for (final Iterator<java.util.Map.Entry<EquivalenceClass, ArrayList<ImplicitECFilterInstance>>> entryIterator =
							chosenIVFIsByEC.entrySet().iterator(); entryIterator.hasNext();) {
						final Entry<EquivalenceClass, ArrayList<ImplicitECFilterInstance>> entry = entryIterator.next();
						final ArrayList<ImplicitECFilterInstance> list = entry.getValue();
						for (final Iterator<ImplicitECFilterInstance> filterInstanceIterator = list.iterator(); filterInstanceIterator
								.hasNext();) {
							final ImplicitECFilterInstance filterInstance = filterInstanceIterator.next();
							final List<Set<SingleFactVariable>> fvSets =
									getECIntersections(blockEC2Constant, block.theta, chosenRuleInfo, filterInstance);
							for (final Set<SingleFactVariable> set : fvSets) {
								// construct and choose stuff that belongs to the current set as
								// representatives for the ECs that don't contain constants
								createImplicitECFilterInstance(block, ruleToInfo, blockEC2Constant, filterInstance, set);
							}
							// remove filter instance from maps and lists
							filterInstanceIterator.remove();
						}
						if (list.isEmpty()) {
							entryIterator.remove();
						}
					}
					for (final Iterator<ExplicitFilterInstance> filterInstanceIterator = chosenEFIs.iterator(); filterInstanceIterator
							.hasNext();) {
						final ExplicitFilterInstance filterInstance = filterInstanceIterator.next();
						final List<Set<SingleFactVariable>> fvSets =
								getECIntersections(blockEC2Constant, block.theta, chosenRuleInfo, filterInstance);
						if (!fvSets.isEmpty()) {
							// construct and choose stuff that belongs to the current set as
							// representatives for the ECs that don't contain constants
							filterInstance.getFilter().accept(
									new ExplicitFilterCreator(block, ruleToInfo, blockEC2Constant, filterInstance,
											fvSets));
							// remove filter instance from maps and lists
							filterInstanceIterator.remove();
						}
					}

					// if all filter instances have been removed, break the loop
					if (chosenIEFIsByEC.isEmpty() && chosenEFIs.isEmpty() && chosenIVFIsByEC.isEmpty()) {
						break;
					}

					// second step: plot a graph with FVs as vertices and edges as join conditions
					// (if any)
					// goal: choose a pair of FVs to join

					// since its complicated to get only those FVs relevant to the block, we just
					// put all of them into the graph and afterwards remove the ones without edges.
					// since a block always results in one "joined component" this works

					// final Set<Set<SingleFactVariable>> allFVSets =
					// block.getFactVariablePartition().getElements().stream()
					// .map(ss -> chosenRuleInfo.factVariables.getSet(ss.get(chosenRule)))
					// .collect(toSet());
					final SimpleWeightedGraph<Set<SingleFactVariable>, DefaultWeightedEdge> graph =
							new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
					// allFVSets.forEach(set -> graph.addVertex(set));

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
					} else {
						// no more implicit edges suggesting anything
						// can be improved arbitrarily
						final Collection<Set<SingleFactVariable>> values =
								chosenRuleInfo.factVariables.tToJoinedWith.values();
						final Iterator<Set<SingleFactVariable>> iterator = values.iterator();
						final Set<SingleFactVariable> source = iterator.next();
						boolean joined = false;
						while (iterator.hasNext()) {
							final Set<SingleFactVariable> target = iterator.next();
							if (source == target) {
								continue;
							}
							chosenRuleInfo.factVariables.mergeII(source, target);
							joined = true;
							break;
						}
						if (!joined)
							throw new IllegalStateException();
					}
				} while (true);

				// create the shared list stuff

				// if (!columnsAlreadyConstructed.isEmpty()) {
				// final Map<PathSharedList, LinkedHashSet<PathFilterList>> sharedPart = new
				// HashMap<>();
				// for (final List<FilterInstance> column : columnsAlreadyConstructed) {
				// for (final FilterInstance fi : column) {
				// sharedPart.computeIfAbsent(ruleToSharedList.get(fi.getRuleOrProxy()),
				// newLinkedHashSet())
				// .add(joinedWithToComponent.get(ruleToJoinedWith.get(fi.getRuleOrProxy()).get(fi)));
				// }
				// }
				// sharedListWrapper.addSharedColumns(sharedPart);
				// }
				//
				// for (final List<FilterInstance> column : columnsToConstruct) {
				// sharedListWrapper.addSharedColumn(column.stream().collect(
				// toMap(fi -> ruleToSharedList.get(fi.getRuleOrProxy()),
				// FilterInstance::convert)));
				// }
				// constructedFIs.addAll(block.getFlatFilterInstances());
				// for (final Entry<Either<Rule, ExistentialProxy>, Map<Filter,
				// FilterInstancesSideBySide>> entry : block
				// .getRuleToFilterToRow().entrySet()) {
				// final Either<Rule, ExistentialProxy> rule = entry.getKey();
				// final Set<FilterInstance> joined =
				// entry.getValue().values().stream().flatMap(sbs -> sbs.getInstances().stream())
				// .collect(toSet());
				// final Map<FilterInstance, Set<FilterInstance>> joinedWithMapForThisRule =
				// ruleToJoinedWith.computeIfAbsent(rule, newHashMap());
				// joined.forEach(fi -> joinedWithMapForThisRule.put(fi, joined));
				// joinedWithToComponent.put(joined, ruleToSharedList.get(rule));
				// }
			}
		}
		final List<PathRule> pathRules = new ArrayList<>();
		for (final Either<Rule, ExistentialProxy> either : rules) {
			if (either.isRight()) {
				continue;
			}
			final Rule rule = either.left().get();
			// final List<PathFilterList> ecFilterLists =
			// Stream.concat(rule.existentialProxies.values().stream().map(ExistentialProxy::getEither),
			// Stream.of(either))
			// .flatMap(
			// e -> Optional.ofNullable(ruleToInfo.get(e))
			// .map(ri -> ri.joinedWithToComponent.setToTarget.values())
			// .orElse(Collections.emptyList()).stream()).collect(toList());
			final ECSetRule ecSetRule = rule.getOriginal();
			// final ECListRule ecListRule =
			// ecSetRule.toECListRule(
			// PathFilterList.toSimpleList(ecFilterLists),
			// ecFilterLists.size() > 1 ? InitialFactVariablesFinder.gather(ecFilterLists) :
			// Collections
			// .emptySet());
			final PathRule pathRule =
					ecSetRule.toPathRule((PathFilterList) /* convertedCondition */null, (Set<Path>) /* resultPaths */null,
							(Map<EquivalenceClass, PathLeaf>) /* equivalenceClassToPathLeaf */null);
			pathRules.add(pathRule);
		}
		throw new UnsupportedOperationException("WIP");
		// return pathRules;
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
			ruleInfo.joinedWithToComponent.setTarget(filterInstance,
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
			ruleInfo.joinedWithToComponent.setTarget(filterInstance, pathNodeFilterSet);
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
			ruleInfo.joinedWithToComponent.setTarget(filterInstance, pathNodeFilterSet);
			ruleInfo.elements.merge(filterInstance.left, filterInstance.right);
		}
	}
}
