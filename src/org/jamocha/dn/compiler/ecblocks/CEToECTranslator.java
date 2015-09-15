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
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.compiler.DeepFactVariableCollector;
import org.jamocha.dn.compiler.ShallowFactVariableCollector;
import org.jamocha.dn.compiler.Specificity;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECCollector;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.filter.FWACollector;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.filter.SymbolInSymbolLeafsCollector;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.fwatransformer.FWASymbolToECTranslator;
import org.jamocha.function.impls.predicates.And;
import org.jamocha.function.impls.predicates.DummyPredicate;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.errors.VariableNotDeclaredError;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Collect all ECFilters inside all children of an OrFunctionConditionalElement, returning a List of
 * Lists. Each inner List contains the ECFilters of one child.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
public class CEToECTranslator implements DefaultConditionalElementsVisitor {

	private final Template initialFactTemplate;
	private final Defrule rule;
	@Getter
	private List<ECSetRule> translateds = Collections.emptyList();

	public List<ECSetRule> translate() {
		assert rule.getCondition().getConditionalElements().size() == 1;
		return rule.getCondition().getConditionalElements().get(0).accept(this).translateds;
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		// If there is no OrFunctionConditionalElement just proceed with the CE as it were
		// the only child of an OrFunctionConditionalElement.
		final Map<VariableSymbol, EquivalenceClass> symbolToEC =
				new SymbolCollector(rule.getCondition()).getSymbols().stream()
						.collect(toMap(Function.identity(), VariableSymbol::getEqual));
		translateds = Collections.singletonList(NoORsTranslator.consolidate(initialFactTemplate, rule, ce, symbolToEC));
	}

	@Override
	public void visit(final OrFunctionConditionalElement ce) {
		final Map<VariableSymbol, EquivalenceClass> symbolToEC =
				new SymbolCollector(rule.getCondition()).getSymbols().stream()
						.collect(toMap(Function.identity(), VariableSymbol::getEqual));
		// For each child of the OrCE ...
		this.translateds =
				ce.getChildren().stream().map(child ->
				// ... collect all PathFilters in the child
						NoORsTranslator.consolidate(initialFactTemplate, rule, child, symbolToEC))
						.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	static class NoORsTranslator implements DefaultConditionalElementsVisitor {

		private final Template initialFactTemplate;
		private final Optional<SingleFactVariable> initialFactVariable;
		private final Set<VariableSymbol> variableSymbols;

		@Getter
		private final Set<ECFilterSet> filters;

		private NoORsTranslator(final Template initialFactTemplate,
				final Optional<SingleFactVariable> initialFactVariable, final Set<VariableSymbol> variableSymbols,
				final Set<PredicateWithArguments<SymbolLeaf>> shallowTests) {
			this.initialFactTemplate = initialFactTemplate;
			this.initialFactVariable = initialFactVariable;
			this.variableSymbols = variableSymbols;
			this.filters =
					shallowTests.stream().map(pwa -> new ECFilter(FWASymbolToECTranslator.translate(pwa)))
							.collect(toSet());
		}

		public static ECSetRule consolidate(final Template initialFactTemplate, final Defrule rule,
				final ConditionalElement ce, final Map<VariableSymbol, EquivalenceClass> symbolToECbackup) {
			final Scope scope = rule.getCondition().getScope();
			final Set<VariableSymbol> symbols = symbolToECbackup.keySet();

			final Set<SingleFactVariable> deepFactVariables = new HashSet<>(DeepFactVariableCollector.collect(ce));

			// copy the equivalence classes
			final BiMap<EquivalenceClass, EquivalenceClass> oldToNewEC =
					HashBiMap
							.create(symbolToECbackup
									.values()
									.stream()
									.collect(
											toMap(Function.identity(),
													(final EquivalenceClass ec) -> new EquivalenceClass(ec))));
			final BiMap<SingleFactVariable, SingleFactVariable> oldToNewFV =
					HashBiMap.create(Maps.asMap(deepFactVariables, fv -> new SingleFactVariable(fv, oldToNewEC)));

			System.out.println(symbols.stream().map(VariableSymbol::getEqual).collect(toList()));
			replaceEC(symbols, oldToNewEC);
			System.out.println(symbols.stream().map(VariableSymbol::getEqual).collect(toList()));
			replaceFVs(oldToNewEC.values(), oldToNewFV);
			System.out.println(symbols.stream().map(VariableSymbol::getEqual).collect(toList()));

			final HashSet<FunctionWithArguments<SymbolLeaf>> occurringFWAs =
					FWACollector.newHashSet().collect(ce).getFwas();
			final Set<SingleFactVariable> occurringFactVariables = oldToNewFV.values();
			/*
			 * inspect the equivalence class hierarchy for sections not contained in this rule part
			 */
			// FIXME may need fixing - after everything is correctly copied, this might fail the way
			// it was implemented in the old days
			// for every symbol in the CE
			for (final VariableSymbol vs : symbols) {
				// and thus for every equivalence class
				final EquivalenceClass ec = vs.getEqual();
				// check whether the fact variables are bound via a TPCE
				for (final Iterator<SingleFactVariable> iterator = ec.getFactVariables().iterator(); iterator.hasNext();) {
					final SingleFactVariable fv = iterator.next();
					if (!occurringFactVariables.contains(fv)) {
						// the fact variable is not contained in the CE, remove it
						iterator.remove();
					}
				}
				// for every slot variable, check whether the corresponding fact variable is
				// contained in the CE
				for (final Iterator<SingleSlotVariable> iterator = ec.getSlotVariables().iterator(); iterator.hasNext();) {
					final SingleSlotVariable sv = iterator.next();
					if (!occurringFactVariables.contains(sv.getFactVariable())) {
						// not contained, remove the SV and its reference to this EC
						iterator.remove();
						sv.getEqualSet().remove(ec);
					}
				}
				// for every FWA, check whether the FWA occurs in the CE
				ec.getConstantExpressions().removeIf(fwa -> !occurringFWAs.contains(fwa));
				ec.getVariableExpressions().removeIf(fwa -> !occurringFWAs.contains(fwa));
			}

			/* merge (bind)s and overlapping SlotVariables */
			for (final VariableSymbol vs : symbols) {
				final EquivalenceClass ec = vs.getEqual();
				for (final SingleSlotVariable sv : ec.getSlotVariables()) {
					if (sv.getEqualSet().size() <= 1)
						continue;
					final Iterator<EquivalenceClass> ecIter = sv.getEqualSet().iterator();
					final EquivalenceClass first = ecIter.next();
					while (ecIter.hasNext()) {
						final EquivalenceClass other = ecIter.next();
						first.merge(other);
						replaceEC(symbols, Collections.singletonMap(other, first));
					}
				}
			}

			/* merge fact variables within equivalence classes */
			symbols.forEach(vs -> vs.getEqual().mergeEquivalenceClassesOfFactVariables());

			/* check that all variables are bound */
			final Set<VariableSymbol> symbolsInLeafs = SymbolInSymbolLeafsCollector.collect(ce);
			for (final VariableSymbol vs : symbols) {
				final EquivalenceClass ec = vs.getEqual();
				if (ec.getFactVariables().isEmpty() && ec.getSlotVariables().isEmpty() && symbolsInLeafs.contains(vs)) {
					// vs is not bound
					throw new VariableNotDeclaredError(vs.getImage());
				}
			}

			/*
			 * merge equals test conditional elements arguments and get the equivalence classes and
			 * tests
			 */
			final ShallowCEEquivalenceClassBuilder equivalenceClassBuilder =
					ce.accept(new ShallowCEEquivalenceClassBuilder(scope, symbols, false));
			final Set<EquivalenceClass> equivalenceClasses =
					new HashSet<>(equivalenceClassBuilder.equivalenceClasses.values());
			final Set<PredicateWithArguments<SymbolLeaf>> shallowTests = equivalenceClassBuilder.shallowTests;

			final ECSetRule result =
					consolidateOnCopiedEquivalenceClasses(initialFactTemplate, rule, ce, shallowTests,
							equivalenceClasses, new IdentityHashMap<>(oldToNewEC.inverse()), new IdentityHashMap<>(
									oldToNewFV), Specificity.calculate(ce));

			// reset the symbol - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.setEqual(ec));
			// replaceEC(symbols, oldToNewEC.inverse());
			// restore the SlotVariable - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.getEqual().getSlotVariables().forEach(sv -> {
				sv.getEqualSet().clear();
				sv.getEqualSet().add(ec);
			}));
			return result;
		}

		private static void replaceFVs(final Set<EquivalenceClass> ecs,
				final BiMap<SingleFactVariable, SingleFactVariable> oldToNewFV) {
			for (final EquivalenceClass ec : ecs) {
				ec.getFactVariables().replaceAll(oldToNewFV::get);
				ec.getSlotVariables().replaceAll(
						sv -> oldToNewFV.get(sv.getFactVariable()).getSlots().get(sv.getSlot()));
			}
		}

		// there are references to EquivalenceClass in:
		// VariableSymbol.equal
		// SingleFactVariable.equal
		// SingleSlotVariable.equal (Set)
		private static void replaceEC(final Set<VariableSymbol> symbols,
				final Map<EquivalenceClass, EquivalenceClass> map) {
			// VariableSymbol.equal
			for (final VariableSymbol symbol : symbols) {
				symbol.setEqual(map.getOrDefault(symbol.getEqual(), symbol.getEqual()));
			}
			// symbols.forEach(sym -> sym.setEqual(map.getOrDefault(sym.getEqual(),
			// sym.getEqual())));
			for (final Map.Entry<EquivalenceClass, EquivalenceClass> entry : map.entrySet()) {
				final EquivalenceClass oldEC = entry.getKey();
				final EquivalenceClass newEC = entry.getValue();
				// SingleFactVariable.equal
				oldEC.getFactVariables().forEach(fv -> fv.setEqual(newEC));
				// SingleSlotVariable.equal (Set)
				for (final SingleSlotVariable sv : oldEC.getSlotVariables()) {
					final Set<EquivalenceClass> equalSet = sv.getEqualSet();
					for (final Map.Entry<EquivalenceClass, EquivalenceClass> innerEntry : map.entrySet()) {
						if (equalSet.remove(innerEntry.getKey()))
							equalSet.add(innerEntry.getValue());
					}
				}
			}
		}

		static class ShallowCEEquivalenceClassBuilder implements DefaultConditionalElementsVisitor {
			final Scope scope;
			final Set<VariableSymbol> occurringSymbols;
			final Map<FunctionWithArguments<SymbolLeaf>, EquivalenceClass> equivalenceClasses;
			final FWAEquivalenceClassBuilder fwaBuilder = new FWAEquivalenceClassBuilder();
			final Set<PredicateWithArguments<SymbolLeaf>> shallowTests = new HashSet<>();
			boolean negated = false;

			public ShallowCEEquivalenceClassBuilder(final Scope scope, final Set<VariableSymbol> occurringSymbols,
					final boolean negated) {
				this.scope = scope;
				this.negated = negated;
				this.occurringSymbols = occurringSymbols;
				this.equivalenceClasses =
						occurringSymbols.stream().collect(toMap(SymbolLeaf::new, this::getNewECForExistentials));
			}

			private EquivalenceClass getNewECForExistentials(final VariableSymbol vs) {
				final EquivalenceClass oldEc = vs.getEqual();
				if (this.scope == oldEc.getMaximalScope()) {
					return oldEc;
				}
				final EquivalenceClass newEc = EquivalenceClass.newECFromType(this.scope, oldEc.getType());
				for (final FunctionWithArguments<ECLeaf> constant : oldEc.getConstantExpressions()) {
					newEc.add(constant);
				}
				EquivalenceClass.addEqualParentEquivalenceClassRelation(newEc, oldEc);
				return newEc;
			}

			@RequiredArgsConstructor
			class FWAEquivalenceClassBuilder implements DefaultFunctionWithArgumentsVisitor<SymbolLeaf> {
				// assumption: all expressions are constant in the sense that subsequent calls to
				// evaluate yield the same result

				private EquivalenceClass getEC(final FunctionWithArguments<SymbolLeaf> fwa) {
					return equivalenceClasses.computeIfAbsent(fwa, f -> {
						final Set<VariableSymbol> symbols = SymbolInSymbolLeafsCollector.collect(f);
						if (symbols.isEmpty()) {
							return EquivalenceClass.newECFromConstantExpression(scope,
									new ConstantLeaf<ECLeaf>(f.evaluate(), f.getReturnType()));
						}
						Scope max = scope;
						for (final VariableSymbol symbol : symbols) {
							final Scope maximalScope = symbol.getEqual().getMaximalScope();
							if (maximalScope.isParentOf(max)) {
								max = maximalScope;
							}
						}
						return EquivalenceClass.newECFromVariableExpression(max, FWASymbolToECTranslator.translate(f));
					});
				}

				@Override
				public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> fwa) {
					if (negated || !fwa.getFunction().inClips().equals(Equals.inClips)) {
						shallowTests.add(fwa);
						return;
					}
					final LinkedList<FunctionWithArguments<SymbolLeaf>> remainingArguments = new LinkedList<>();
					final FunctionWithArguments<SymbolLeaf>[] args =
							FunctionNormaliser.normalise(FWADeepCopy.copy(fwa)).getArgs();
					final EquivalenceClass left = getEC(args[0]);
					final boolean leftInCS = left.getMaximalScope() == scope;
					for (int i = 1; i < args.length; i++) {
						final FunctionWithArguments<SymbolLeaf> arg = args[i];
						final EquivalenceClass right = getEC(arg);
						final boolean rightInCS = right.getMaximalScope() == scope;
						if (leftInCS && rightInCS) {
							// merge right into left
							left.merge(right);
							// replace right with left everywhere
							equivalenceClasses.put(arg, left);
							replaceEC(occurringSymbols, Collections.singletonMap(right, left));
						} else if (leftInCS || rightInCS) {
							// equal parent scope relation
							EquivalenceClass.addEqualParentEquivalenceClassRelation(left, right);
						} else {
							// test stays
							remainingArguments.add(arg);
						}
					}
					if (!remainingArguments.isEmpty()) {
						if (remainingArguments.size() == args.length - 1) {
							// test completely preserved
							shallowTests.add(fwa);
						} else {
							// test only partially preserved
							remainingArguments.addFirst(args[0]);
							shallowTests.add(GenericWithArgumentsComposite.<SymbolLeaf> newPredicateInstance(!negated,
									Equals.inClips, toArray(remainingArguments, FunctionWithArguments[]::new)));
						}
					}
				}

				@Override
				public void defaultAction(final FunctionWithArguments<SymbolLeaf> function) {
					// should never be called, yet, if someone adds other classes implementing the
					// PredicateWithArguments interface other than PredicateWithArgumentsComposite,
					// we would lose those tests if not preserved here
					if (function instanceof PredicateWithArguments) {
						log.warn("A class implementing PredicateWithArguments other than PredicateWithArgumentsComposite seems to have been added. Consider modifying the FWAEquivalenceClassBuilder to exploit equivalence classes within those new classes. Tests are simply preserved for now.");
						shallowTests.add((PredicateWithArguments<SymbolLeaf>) function);
					}
				}
			}

			@Override
			public void defaultAction(final ConditionalElement ce) {
				ce.getChildren().forEach(c -> c.accept(this));
			}

			@Override
			public void visit(final ExistentialConditionalElement ce) {
				// stop
			}

			@Override
			public void visit(final NegatedExistentialConditionalElement ce) {
				// stop
			}

			@Override
			public void visit(final TestConditionalElement ce) {
				ce.getPredicateWithArguments().accept(fwaBuilder);
			}

			@Override
			public void visit(final NotFunctionConditionalElement ce) {
				negated = !negated;
				defaultAction(ce);
				negated = !negated;
			}
		}

		private static ECSetRule consolidateOnCopiedEquivalenceClasses(final Template initialFactTemplate,
				final Defrule rule, final ConditionalElement ce,
				final Set<PredicateWithArguments<SymbolLeaf>> shallowTests,
				final Set<EquivalenceClass> equivalenceClasses,
				final IdentityHashMap<EquivalenceClass, EquivalenceClass> conditionECsToLocalECs,
				final IdentityHashMap<SingleFactVariable, SingleFactVariable> oldToNewFactVariables,
				final int specificity) {
			final Pair<Optional<SingleFactVariable>, Set<SingleFactVariable>> initialFactAndVariables =
					ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce);
			final Set<SingleFactVariable> factVariables =
					initialFactAndVariables.getRight().stream().map(oldToNewFactVariables::get)
							.collect(toIdentityHashSet());

			return rule.newECFilterSetCondition(
					new NoORsTranslator(initialFactTemplate, initialFactAndVariables.getLeft(), rule.getCondition()
							.getVariableSymbols(), shallowTests).collect(ce).getFilters(), factVariables,
					equivalenceClasses, conditionECsToLocalECs, specificity);
		}

		private NoORsTranslator collect(final ConditionalElement ce) {
			return ce.accept(this);
		}

		static ECExistentialSet processExistentialCondition(final Template initialFactTemplate,
				final Optional<SingleFactVariable> initialFactVariable, final Set<VariableSymbol> variableSymbols,
				final ConditionalElement ce, final Scope scope, final boolean isPositive) {
			// Collect the existential FactVariables in a shallow manner (not including FVs in
			// nested existential elements)
			final Pair<Optional<SingleFactVariable>, Set<SingleFactVariable>> initialFactAndOtherFVs =
					ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce);
			final Set<SingleFactVariable> shallowExistentialFVs = initialFactAndOtherFVs.getRight();
			final Set<EquivalenceClass> shallowExistentialECs =
					shallowExistentialFVs.stream().map(SingleFactVariable::getEqual).collect(toSet());

			/*
			 * merge equals test conditional elements arguments and get the equivalence classes and
			 * tests
			 */
			final ShallowCEEquivalenceClassBuilder equivalenceClassBuilder =
					ce.accept(new ShallowCEEquivalenceClassBuilder(scope, variableSymbols, false));
			final Set<EquivalenceClass> equivalenceClasses =
					new HashSet<>(equivalenceClassBuilder.equivalenceClasses.values());
			final Set<PredicateWithArguments<SymbolLeaf>> shallowTests = equivalenceClassBuilder.shallowTests;

			// Generate ECFilters from CE (recurse)
			final Set<ECFilterSet> filters =
					new NoORsTranslator(initialFactTemplate, initialFactAndOtherFVs.getLeft(), variableSymbols,
							shallowTests).collect(ce).getFilters();

			// Collect all used Equivalence Classes for every Filter
			final Map<ECFilterSet, Set<EquivalenceClass>> filter2ECs =
					filters.stream().collect(Collectors.toMap(Function.identity(), ECCollector::collect));

			// Filter categories:
			// A filter is pure if it either contains only local existential fact variables (ie ECs)
			// (this excludes nested existentials) or none of them at all
			// A filter is mixed if it contains both

			// TBD nested existentials may contain local fact variables and fact variables bound
			// in parents (and fact variables bound within the nested existential) and thus be mixed

			// Partition filters according to their category
			final Set<ECFilterSet> pureFilters, mixedFilters;
			{
				final Map<Boolean, Set<ECFilterSet>> tmp =
						filters.stream().collect(
								Collectors.partitioningBy(
										filter -> {
											final Set<EquivalenceClass> ec = filter2ECs.get(filter);
											return shallowExistentialECs.containsAll(ec)
													|| Collections.disjoint(shallowExistentialECs, ec);
										}, toSet()));
				pureFilters = tmp.get(Boolean.TRUE);
				mixedFilters = tmp.get(Boolean.FALSE);
			}
			// FIXME is this creation of the existential closure correct?
			final ECFilter existentialClosure;
			if (mixedFilters.isEmpty()) {
				existentialClosure =
						new ECFilter(new PredicateWithArgumentsComposite<ECLeaf>(DummyPredicate.instance, toArray(
								Stream.concat(shallowExistentialECs.stream(),
										Stream.of(initialFactVariable.get().getEqual())).map(ECLeaf::new),
								ECLeaf[]::new)));
			} else if (1 == mixedFilters.size()) {
				final ECFilter mixed = (ECFilter) mixedFilters.iterator().next();
				final Set<EquivalenceClass> ecsInMixed = ECCollector.collect(mixed);
				if (ecsInMixed.containsAll(shallowExistentialECs)) {
					existentialClosure = mixed;
				} else {
					final SetView<EquivalenceClass> missingECs = Sets.difference(shallowExistentialECs, ecsInMixed);
					existentialClosure =
							new ECFilter(PredicateWithArgumentsComposite.newPredicateInstance(
									And.inClips,
									mixed.getFunction(),
									new PredicateWithArgumentsComposite<ECLeaf>(DummyPredicate.instance, toArray(
											missingECs.stream().map(ECLeaf::new), ECLeaf[]::new))));
				}
			} else {
				Stream<FunctionWithArguments<ECLeaf>> argStream =
						mixedFilters.stream().map(f -> ((ECFilter) f).getFunction());
				final ECCollector ecCollector = new ECCollector();
				mixedFilters.forEach(f -> f.accept(ecCollector));
				final Set<EquivalenceClass> ecsInMixed = ecCollector.getEquivalenceClasses();
				if (!ecsInMixed.containsAll(shallowExistentialECs)) {
					final SetView<EquivalenceClass> missingECs = Sets.difference(shallowExistentialECs, ecsInMixed);
					argStream =
							Stream.concat(argStream, Stream.of(new PredicateWithArgumentsComposite<ECLeaf>(
									DummyPredicate.instance, toArray(missingECs.stream().map(ECLeaf::new),
											ECLeaf[]::new))));
				}
				existentialClosure =
						new ECFilter(GenericWithArgumentsComposite.newPredicateInstance(And.inClips,
								(FunctionWithArguments<ECLeaf>[]) toArray(argStream, FunctionWithArguments[]::new)));
			}
			return new ECFilterSet.ECExistentialSet(isPositive, initialFactVariable.orElse(null),
					shallowExistentialFVs, equivalenceClasses, pureFilters, existentialClosure);
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			// just traverse the tree to find the existential CEs
			ce.getChildren().forEach(c -> c.accept(this));
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			throw new Error("There should not be any OrFunctionCEs at this level.");
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.filters.add(processExistentialCondition(initialFactTemplate, initialFactVariable, variableSymbols, ce
					.getChildren().get(0), ce.getScope(), true));
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.filters.add(processExistentialCondition(initialFactTemplate, initialFactVariable, variableSymbols, ce
					.getChildren().get(0), ce.getScope(), false));
		}
	}
}