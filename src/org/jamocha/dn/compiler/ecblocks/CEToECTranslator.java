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

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.jamocha.function.impls.predicates.Not;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.RuleConditionProcessor.ShallowSymbolCollector;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.errors.VariableNotDeclaredError;
import org.jamocha.util.ToArray;

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
		assert this.rule.getCondition().getConditionalElements().size() == 1;
		return this.rule.getCondition().getConditionalElements().get(0).accept(this).translateds;
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		// If there is no OrFunctionConditionalElement just proceed with the CE as it were
		// the only child of an OrFunctionConditionalElement.
		final Map<VariableSymbol, EquivalenceClass> symbolToEC =
				new SymbolCollector(this.rule.getCondition()).getSymbols().stream()
						.collect(toMap(Function.identity(), VariableSymbol::getEqual));
		this.translateds =
				Collections.singletonList(NoORsTranslator.consolidate(this.initialFactTemplate, this.rule, ce,
						symbolToEC));
	}

	@Override
	public void visit(final OrFunctionConditionalElement ce) {
		final Map<VariableSymbol, EquivalenceClass> symbolToEC =
				new SymbolCollector(this.rule.getCondition()).getSymbols().stream()
						.collect(toMap(Function.identity(), VariableSymbol::getEqual));
		// For each child of the OrCE ...
		this.translateds =
				ce.getChildren().stream().map(child ->
				// ... collect all PathFilters in the child
						NoORsTranslator.consolidate(this.initialFactTemplate, this.rule, child, symbolToEC))
						.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	static class NoORsTranslator implements DefaultConditionalElementsVisitor {

		private final Template initialFactTemplate;
		private final SingleFactVariable initialFactVariable;
		private final Set<VariableSymbol> variableSymbols;
		private final BiMap<SingleFactVariable, SingleFactVariable> oldToNewFactVariables;

		@Getter
		private final Set<ECFilterSet> filters;

		private NoORsTranslator(final Template initialFactTemplate, final SingleFactVariable initialFactVariable,
				final Set<VariableSymbol> variableSymbols, final Set<PredicateWithArguments<ECLeaf>> shallowTests,
				final BiMap<SingleFactVariable, SingleFactVariable> oldToNewFactVariables) {
			this.initialFactTemplate = initialFactTemplate;
			this.initialFactVariable = initialFactVariable;
			this.variableSymbols = variableSymbols;
			this.oldToNewFactVariables = oldToNewFactVariables;
			this.filters = shallowTests.stream().map(ECFilter::new).collect(toSet());
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

			replaceEC(symbols, oldToNewEC);
			replaceFVs(oldToNewEC.values(), oldToNewFV);

			final HashSet<FunctionWithArguments<SymbolLeaf>> occurringFWAs =
					FWACollector.newHashSet().collect(ce).getFwas();
			final Set<SingleFactVariable> occurringFactVariables = oldToNewFV.values();
			/*
			 * inspect the equivalence class hierarchy for sections not contained in this rule part
			 */
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

			// FIXME needs fixing - always returns empty-handed
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
					ce.accept(new ShallowCEEquivalenceClassBuilder(scope, symbols, Sets
							.newHashSet(ShallowFactVariableCollector.collect(ce)), false));
			final Set<EquivalenceClass> equivalenceClasses =
					new HashSet<>(equivalenceClassBuilder.equivalenceClasses.values());

			final Set<PredicateWithArguments<ECLeaf>> shallowTests =
					equivalenceClassBuilder.shallowTests.stream().map(FWASymbolToECTranslator::translate)
							.collect(toSet());
			shallowTests.stream().map(ECCollector::collect).forEach(equivalenceClasses::addAll);

			final SingleFactVariable initialFactVariable =
					oldToNewFV.get(ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce).getLeft());

			final ECSetRule result =
					consolidateOnCopiedEquivalenceClasses(initialFactTemplate, initialFactVariable, rule, ce,
							shallowTests, equivalenceClasses, oldToNewEC.inverse(), oldToNewFV,
							Specificity.calculate(ce));

			// reset the symbol - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.setEqual(ec));
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
			final Set<SingleFactVariable> shallowFactVariables;
			final Map<FunctionWithArguments<SymbolLeaf>, EquivalenceClass> equivalenceClasses;
			final FWAEquivalenceClassBuilder fwaBuilder = new FWAEquivalenceClassBuilder();
			final Set<PredicateWithArguments<SymbolLeaf>> shallowTests = new HashSet<>();
			boolean negated = false;

			public ShallowCEEquivalenceClassBuilder(final Scope scope, final Set<VariableSymbol> occurringSymbols,
					final Set<SingleFactVariable> shallowFactVariables, final boolean negated) {
				this.scope = scope;
				this.negated = negated;
				this.occurringSymbols = occurringSymbols;
				this.shallowFactVariables = shallowFactVariables;
				this.equivalenceClasses =
						occurringSymbols.stream().collect(toMap(SymbolLeaf::new, this::getNewECForExistentials));
			}

			private EquivalenceClass getNewECForExistentials(final VariableSymbol vs) {
				final EquivalenceClass oldEc = vs.getEqual();
				if (this.scope == oldEc.getMaximalScope()) {
					// same scope, nothing to do
					return oldEc;
				}
				if (this.scope.isParentOf(oldEc.getMaximalScope())) {
					// can not be accessed in the current scope!
					return oldEc;
				}
				// oldEc scope is parent scope of this scope => modify when it reappears in current
				// scope
				final EquivalenceClass newEc = EquivalenceClass.newECFromType(this.scope, oldEc.getType());
				for (final FunctionWithArguments<ECLeaf> constant : oldEc.getConstantExpressions()) {
					newEc.add(constant);
				}
				for (final Iterator<SingleFactVariable> iterator = oldEc.getFactVariables().iterator(); iterator
						.hasNext();) {
					final SingleFactVariable factVariable = iterator.next();
					if (this.shallowFactVariables.contains(factVariable)) {
						newEc.add(factVariable);
						iterator.remove();
					}
				}
				for (final Iterator<SingleSlotVariable> iterator = oldEc.getSlotVariables().iterator(); iterator
						.hasNext();) {
					final SingleSlotVariable slotVariable = iterator.next();
					if (this.shallowFactVariables.contains(slotVariable.getFactVariable())) {
						newEc.add(slotVariable);
						iterator.remove();
					}
				}
				EquivalenceClass.addEqualParentEquivalenceClassRelation(newEc, oldEc);
				return newEc;
			}

			private void addToShallowTests(final PredicateWithArguments<SymbolLeaf> shallowTest) {
				this.shallowTests.add(this.negated ? GenericWithArgumentsComposite.newPredicateInstance(Not.inClips,
						shallowTest) : shallowTest);
			}

			@RequiredArgsConstructor
			class FWAEquivalenceClassBuilder implements DefaultFunctionWithArgumentsVisitor<SymbolLeaf> {
				// assumption: all expressions are constant in the sense that subsequent calls to
				// evaluate yield the same result

				private EquivalenceClass getEC(final FunctionWithArguments<SymbolLeaf> fwa) {
					return ShallowCEEquivalenceClassBuilder.this.equivalenceClasses.computeIfAbsent(fwa, f -> {
						final Set<VariableSymbol> symbols = SymbolInSymbolLeafsCollector.collect(f);
						if (symbols.isEmpty()) {
							return EquivalenceClass.newECFromConstantExpression(
									ShallowCEEquivalenceClassBuilder.this.scope, new ConstantLeaf<ECLeaf>(f.evaluate(),
											f.getReturnType()));
						}
						Scope max = ShallowCEEquivalenceClassBuilder.this.scope;
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
					if (ShallowCEEquivalenceClassBuilder.this.negated
							|| !fwa.getFunction().inClips().equals(Equals.inClips)) {
						addToShallowTests(fwa);
						return;
					}
					final LinkedList<FunctionWithArguments<SymbolLeaf>> remainingArguments = new LinkedList<>();
					final FunctionWithArguments<SymbolLeaf>[] args =
							FunctionNormaliser.normalise(FWADeepCopy.copy(fwa)).getArgs();
					final EquivalenceClass left = getEC(args[0]);
					final boolean leftInCS = left.getMaximalScope() == ShallowCEEquivalenceClassBuilder.this.scope;
					for (int i = 1; i < args.length; i++) {
						final FunctionWithArguments<SymbolLeaf> arg = args[i];
						final EquivalenceClass right = getEC(arg);
						final boolean rightInCS =
								right.getMaximalScope() == ShallowCEEquivalenceClassBuilder.this.scope;
						if (leftInCS && rightInCS) {
							// merge right into left
							left.merge(right);
							// replace right with left everywhere
							ShallowCEEquivalenceClassBuilder.this.equivalenceClasses.put(arg, left);
							// replace all constants in right to point to left
							if (!right.getConstantExpressions().isEmpty() || !right.getVariableExpressions().isEmpty()) {
								for (final Entry<FunctionWithArguments<SymbolLeaf>, EquivalenceClass> entry : ShallowCEEquivalenceClassBuilder.this.equivalenceClasses
										.entrySet()) {
									if (entry.getValue() == right) {
										ShallowCEEquivalenceClassBuilder.this.equivalenceClasses.put(entry.getKey(),
												left);
									}
								}
							}
							replaceEC(ShallowCEEquivalenceClassBuilder.this.occurringSymbols,
									Collections.singletonMap(right, left));
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
							addToShallowTests(fwa);
						} else {
							// test only partially preserved
							remainingArguments.addFirst(args[0]);
							ShallowCEEquivalenceClassBuilder.this.shallowTests.add(GenericWithArgumentsComposite
									.<SymbolLeaf> newPredicateInstance(!ShallowCEEquivalenceClassBuilder.this.negated,
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
						ShallowCEEquivalenceClassBuilder.this.shallowTests
								.add((PredicateWithArguments<SymbolLeaf>) function);
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
				ce.getPredicateWithArguments().accept(this.fwaBuilder);
			}

			@Override
			public void visit(final NotFunctionConditionalElement ce) {
				this.negated = !this.negated;
				defaultAction(ce);
				this.negated = !this.negated;
			}
		}

		private static ECSetRule consolidateOnCopiedEquivalenceClasses(final Template initialFactTemplate,
				final SingleFactVariable initialFactVariable, final Defrule rule, final ConditionalElement ce,
				final Set<PredicateWithArguments<ECLeaf>> shallowTests, final Set<EquivalenceClass> equivalenceClasses,
				final BiMap<EquivalenceClass, EquivalenceClass> newToOldECs,
				final BiMap<SingleFactVariable, SingleFactVariable> oldToNewFactVariables, final int specificity) {
			final Set<ECFilterSet> filters =
					new NoORsTranslator(initialFactTemplate, initialFactVariable, rule.getCondition()
							.getVariableSymbols(), shallowTests, oldToNewFactVariables).collect(ce).getFilters();
			final Pair<SingleFactVariable, Set<SingleFactVariable>> initialFactAndVariables =
					ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce);
			final Set<SingleFactVariable> factVariables =
					initialFactAndVariables.getRight().stream().map(oldToNewFactVariables::get)
							.collect(toIdentityHashSet());

			final Set<EquivalenceClass> usedECs = ECCollector.collect(filters);
			if (usedECs.contains(initialFactVariable.getEqual())) {
				factVariables.add(initialFactVariable);
			} else {
				equivalenceClasses.remove(initialFactVariable.getEqual());
			}
			return rule.newECSetRule(filters, factVariables, equivalenceClasses, newToOldECs, specificity);
		}

		private NoORsTranslator collect(final ConditionalElement ce) {
			return ce.accept(this);
		}

		private static boolean relevant(final Set<VariableSymbol> symbolsInTests,
				final Set<SingleFactVariable> shallowExistentialFVs, final VariableSymbol s) {
			final boolean contains = symbolsInTests.contains(s);
			if (contains)
				return true;
			final Set<SingleFactVariable> directlyDependentFactVariables =
					s.getEqual().getDirectlyDependentFactVariables();
			final boolean disjoint = Collections.disjoint(shallowExistentialFVs, directlyDependentFactVariables);
			return !disjoint;
		}

		static ECExistentialSet processExistentialCondition(final Template initialFactTemplate,
				final SingleFactVariable initialFactVariable, final Set<VariableSymbol> variableSymbols,
				final BiMap<SingleFactVariable, SingleFactVariable> oldToNewFactVariables, final ConditionalElement ce,
				final Scope scope, final boolean isPositive) {
			// Collect the existential FactVariables in a shallow manner (not including FVs in
			// nested existential elements)
			final Pair<SingleFactVariable, Set<SingleFactVariable>> initialFactAndOtherFVs =
					ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce);
			final Set<SingleFactVariable> shallowExistentialFVs =
					initialFactAndOtherFVs.getRight().stream().map(oldToNewFactVariables::get).collect(toSet());
			final Set<EquivalenceClass> shallowExistentialECs =
					shallowExistentialFVs.stream().map(SingleFactVariable::getEqual).collect(toSet());

			/*
			 * merge equals test conditional elements arguments and get the equivalence classes and
			 * tests
			 */

			final Set<VariableSymbol> symbolsInTests = ce.accept(new ShallowSymbolCollector()).getSymbols();

			final Set<VariableSymbol> symbolsAtThisLevel =
					Sets.newHashSet(Sets.filter(variableSymbols,
							s -> relevant(symbolsInTests, shallowExistentialFVs, s)));

			final ShallowCEEquivalenceClassBuilder equivalenceClassBuilder =
					ce.accept(new ShallowCEEquivalenceClassBuilder(scope, symbolsAtThisLevel, shallowExistentialFVs,
							false));
			final Set<EquivalenceClass> equivalenceClasses =
					new HashSet<>(equivalenceClassBuilder.equivalenceClasses.values());
			final Set<PredicateWithArguments<ECLeaf>> shallowTests =
					equivalenceClassBuilder.shallowTests.stream().map(FWASymbolToECTranslator::translate)
							.collect(toSet());
			// shallowTests.stream().map(ECCollector::collect).forEach(equivalenceClasses::addAll);

			// Generate ECFilters from CE (recurse)
			final Set<ECFilterSet> filters =
					new NoORsTranslator(initialFactTemplate, initialFactVariable, variableSymbols, shallowTests,
							oldToNewFactVariables).collect(ce).getFilters();

			// Collect all used Equivalence Classes for every Filter
			final Map<ECFilterSet, Set<SingleFactVariable>> filter2FVs =
					filters.stream().collect(
							Collectors.toMap(
									Function.identity(),
									filter -> ECCollector.collect(filter).stream()
											.map(EquivalenceClass::getDirectlyDependentFactVariables)
											.flatMap(Set::stream).collect(toIdentityHashSet())));

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
											final Set<SingleFactVariable> fvs = filter2FVs.get(filter);
											return shallowExistentialFVs.containsAll(fvs)
													|| Collections.disjoint(shallowExistentialFVs, fvs);
										}, toSet()));
				pureFilters = tmp.get(Boolean.TRUE);
				mixedFilters = tmp.get(Boolean.FALSE);
			}

			// FIXME is this creation of the existential closure correct?
			final List<FunctionWithArguments<ECLeaf>> predicates = new ArrayList<>();
			// // identify the equivalence classes that contain local bindings and an equal parent
			// for (final EquivalenceClass ec : equivalenceClasses) {
			// final Set<EquivalenceClass> equalParents = ec.getEqualParentEquivalenceClasses();
			// if (equalParents.isEmpty())
			// continue;
			// if (ec.getFactVariables().isEmpty() && ec.getSlotVariables().isEmpty()
			// && ec.getConstantExpressions().isEmpty() && ec.getVariableExpressions().isEmpty())
			// continue;
			// for (final EquivalenceClass equalParent : equalParents) {
			// predicates.add(PredicateWithArgumentsComposite.newPredicateInstance(Equals.inClips,
			// new ECLeaf(ec),
			// new ECLeaf(equalParent)));
			// }
			// }

			if (mixedFilters.isEmpty()) {
				predicates.add(new PredicateWithArgumentsComposite<ECLeaf>(DummyPredicate.instance, toArray(
						Stream.concat(shallowExistentialECs.stream(), Stream.of(initialFactVariable.getEqual())).map(
								ECLeaf::new), ECLeaf[]::new)));
			} else if (1 == mixedFilters.size()) {
				final ECFilter mixed = (ECFilter) mixedFilters.iterator().next();
				predicates.add(mixed.getFunction());
				final Set<EquivalenceClass> ecsInMixed = ECCollector.collect(mixed);
				if (!ecsInMixed.containsAll(shallowExistentialECs)) {
					final SetView<EquivalenceClass> missingECs = Sets.difference(shallowExistentialECs, ecsInMixed);
					predicates.add(GenericWithArgumentsComposite.newPredicateInstance(
							And.inClips,
							mixed.getFunction(),
							new PredicateWithArgumentsComposite<ECLeaf>(DummyPredicate.instance, toArray(missingECs
									.stream().map(ECLeaf::new), ECLeaf[]::new))));
				}
			} else {
				mixedFilters.stream().map(f -> ((ECFilter) f).getFunction()).forEach(predicates::add);
				final Set<SingleFactVariable> fvsPulledInByMixedFilters =
						mixedFilters.stream().map(filter2FVs::get).flatMap(Set::stream).collect(toIdentityHashSet());
				if (!fvsPulledInByMixedFilters.containsAll(shallowExistentialFVs)) {
					final SetView<SingleFactVariable> missingFVs =
							Sets.difference(shallowExistentialFVs, fvsPulledInByMixedFilters);
					predicates.add(new PredicateWithArgumentsComposite<ECLeaf>(DummyPredicate.instance, toArray(
							missingFVs.stream().map(SingleFactVariable::getEqual).map(ECLeaf::new), ECLeaf[]::new)));
				}
			}
			final ECFilter existentialClosure =
					new ECFilter(predicates.size() == 1 ? (PredicateWithArguments<ECLeaf>) predicates.get(0)
							: PredicateWithArgumentsComposite.newPredicateInstance(Equals.inClips, ToArray
									.<FunctionWithArguments<ECLeaf>> toArray(predicates, FunctionWithArguments[]::new)));
			return new ECFilterSet.ECExistentialSet(isPositive, initialFactVariable, shallowExistentialFVs,
					equivalenceClasses, pureFilters, existentialClosure);
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
			this.filters.add(processExistentialCondition(this.initialFactTemplate, this.initialFactVariable,
					this.variableSymbols, this.oldToNewFactVariables, ce.getChildren().get(0), ce.getScope(), true));
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.filters.add(processExistentialCondition(this.initialFactTemplate, this.initialFactVariable,
					this.variableSymbols, this.oldToNewFactVariables, ce.getChildren().get(0), ce.getScope(), false));
		}
	}
}