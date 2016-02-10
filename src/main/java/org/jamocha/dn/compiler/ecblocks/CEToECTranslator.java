/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.compiler.ecblocks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.compiler.ShallowFactVariableCollector;
import org.jamocha.dn.compiler.Specificity;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.ECCollector;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.filter.UniformFunctionTranslator;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.impls.predicates.And;
import org.jamocha.function.impls.predicates.DummyPredicate;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.function.impls.predicates.Not;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.*;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.newIdentityHashSet;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.jamocha.util.ToArray.toArray;

/**
 * Collect all ECFilters inside all children of an OrFunctionConditionalElement, returning a List of Lists. Each inner
 * List contains the ECFilters of one child.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
public class CEToECTranslator implements DefaultConditionalElementsVisitor<ECLeaf> {

    private final Template initialFactTemplate;
    private final Defrule.ECBasedCERule rule;
    @Getter
    private List<ECSetRule> translateds = Collections.emptyList();

    public List<ECSetRule> translate() {
        return this.rule.getCondition().accept(this).translateds;
    }

    @Override
    public void defaultAction(final ConditionalElement<ECLeaf> ce) {
        // If there is no OrFunctionConditionalElement just proceed with the CE as it were
        // the only child of an OrFunctionConditionalElement.
        this.translateds =
                Collections.singletonList(NoORsTranslator.consolidate(this.initialFactTemplate, this.rule, ce));
    }

    @Override
    public void visit(final OrFunctionConditionalElement<ECLeaf> ce) {
        // For each child of the OrCE ...
        this.translateds = ce.getChildren().stream().map(child ->
                // ... collect all PathFilters in the child
                NoORsTranslator.consolidate(this.initialFactTemplate, this.rule, child))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
     */
    public static final class NoORsTranslator implements DefaultConditionalElementsVisitor<ECLeaf> {
        private final Template initialFactTemplate;
        private final SingleFactVariable initialFactVariable;
        private final Set<EquivalenceClass> equivalenceClasses;

        @Getter
        private final Set<ECFilterSet> filters;

        private NoORsTranslator(final Template initialFactTemplate, final SingleFactVariable initialFactVariable,
                final Set<EquivalenceClass> equivalenceClasses,
                final Set<PredicateWithArguments<ECLeaf>> shallowTests) {
            this.initialFactTemplate = initialFactTemplate;
            this.initialFactVariable = initialFactVariable;
            this.equivalenceClasses = equivalenceClasses;
            this.filters = shallowTests.stream().map(ECFilter::new).collect(toSet());
        }

        public static ECSetRule consolidate(final Template initialFactTemplate, final Defrule.ECBasedCERule rule,
                final ConditionalElement<ECLeaf> ce) {
            final Scope scope = rule.getParent().getCondition().getScope();
            final Set<EquivalenceClass> equivalenceClasses = newIdentityHashSet(rule.getEquivalenceClasses());

            /* merge (bind)s and overlapping SlotVariables */
            for (final EquivalenceClass ec : equivalenceClasses) {
                for (final SingleSlotVariable sv : ec.getSlotVariables()) {
                    if (sv.getEqualSet().size() <= 1) continue;
                    final Iterator<EquivalenceClass> ecIter = sv.getEqualSet().iterator();
                    final EquivalenceClass first = ecIter.next();
                    while (ecIter.hasNext()) {
                        final EquivalenceClass other = ecIter.next();
                        first.merge(other);
                        replaceEC(other, first);
                    }
                }
            }

            /* merge fact variables within equivalence classes */
            equivalenceClasses.forEach(EquivalenceClass::mergeEquivalenceClassesOfFactVariables);

            /*
             * merge equals test conditional elements arguments and get the equivalence classes and
             * tests
             */
            final ShallowCEEquivalenceClassBuilder equivalenceClassBuilder =
                    ce.accept(new ShallowCEEquivalenceClassBuilder(equivalenceClasses, scope, false));
            equivalenceClasses.addAll(equivalenceClassBuilder.equivalenceClasses.values());
            // remove equivalence classes that were merged with other equivalence classes
            for (final Iterator<EquivalenceClass> ecIter = equivalenceClasses.iterator(); ecIter.hasNext(); ) {
                final EquivalenceClass equivalenceClass = ecIter.next();
                final EquivalenceClass replacement =
                        equivalenceClassBuilder.equivalenceClasses.get(new ECLeaf(equivalenceClass));
                if (null == replacement) continue;
                if (replacement == equivalenceClass) continue;
                ecIter.remove();
            }

            final Set<PredicateWithArguments<ECLeaf>> shallowTests =
                    equivalenceClassBuilder.getTranslatedShallowTests();

            final SingleFactVariable initialFactVariable =
                    ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce).getLeft();

            return consolidateOnCopiedEquivalenceClasses(initialFactTemplate, initialFactVariable, rule, ce,
                    shallowTests, equivalenceClasses, Specificity.calculate(ce));
        }

        // there are references to EquivalenceClass in:
        // VariableSymbol.equal
        // SingleFactVariable.equal
        // SingleSlotVariable.equal (Set)
        private static void replaceEC(final EquivalenceClass oldEC, final EquivalenceClass newEC) {
            // SingleFactVariable.equal
            oldEC.getFactVariables().forEach(fv -> fv.setEqual(newEC));
            // SingleSlotVariable.equal (Set)
            for (final SingleSlotVariable sv : oldEC.getSlotVariables()) {
                final Set<EquivalenceClass> equalSet = sv.getEqualSet();
                if (equalSet.remove(oldEC)) equalSet.add(newEC);
            }
        }

        static class ShallowCEEquivalenceClassBuilder implements DefaultConditionalElementsVisitor<ECLeaf> {
            final Scope scope;
            final Map<FunctionWithArguments<ECLeaf>, EquivalenceClass> equivalenceClasses;
            final FWAEquivalenceClassBuilder fwaBuilder = new FWAEquivalenceClassBuilder();
            final Set<PredicateWithArguments<ECLeaf>> shallowTests = new HashSet<>();
            boolean negated = false;

            public Set<PredicateWithArguments<ECLeaf>> getTranslatedShallowTests() {
                return this.shallowTests.stream().map(pwa -> ((PredicateWithArguments<ECLeaf>) pwa
                        .accept(new RuleConditionProcessor.FWAECReplacer(
                                ec -> this.equivalenceClasses.getOrDefault(new ECLeaf(ec), ec)))
                        .getFunctionWithArguments())).collect(toSet());
            }

            ShallowCEEquivalenceClassBuilder(final Set<EquivalenceClass> allECs, final Scope scope,
                    final boolean negated) {
                this.scope = scope;
                this.negated = negated;
                this.equivalenceClasses = Maps.newHashMap(
                        Maps.uniqueIndex(allECs.stream().filter(ec -> ec.getMaximalScope() == scope)::iterator,
                                ECLeaf::new));
            }

            private void addToShallowTests(final PredicateWithArguments<ECLeaf> shallowTest) {
                this.shallowTests.add(this.negated ? GenericWithArgumentsComposite
                        .newPredicateInstance(Not.IN_CLIPS, shallowTest) : shallowTest);
            }

            @RequiredArgsConstructor
            class FWAEquivalenceClassBuilder implements DefaultFunctionWithArgumentsVisitor<ECLeaf> {
                // assumption: all expressions are constant in the sense that subsequent calls to
                // evaluate yield the same result

                private EquivalenceClass getEC(final FunctionWithArguments<ECLeaf> fwa) {
                    return ShallowCEEquivalenceClassBuilder.this.equivalenceClasses.computeIfAbsent(fwa, f -> {
                        final Set<EquivalenceClass> ecs = ECCollector.collect(f);
                        if (ecs.isEmpty()) {
                            return EquivalenceClass
                                    .newECFromConstantExpression(ShallowCEEquivalenceClassBuilder.this.scope,
                                            new ConstantLeaf<>(f.evaluate(), f.getReturnType()));
                        }
                        Scope max = ShallowCEEquivalenceClassBuilder.this.scope;
                        for (final EquivalenceClass ec : ecs) {
                            final Scope maximalScope = ec.getMaximalScope();
                            if (maximalScope.isParentOf(max)) {
                                max = maximalScope;
                            }
                        }
                        return EquivalenceClass.newECFromFunctionalExpression(max, f);
                    });
                }

                @Override
                public void visit(final PredicateWithArgumentsComposite<ECLeaf> fwa) {
                    if (ShallowCEEquivalenceClassBuilder.this.negated || !fwa.getFunction().inClips()
                            .equals(Equals.IN_CLIPS)) {
                        addToShallowTests(UniformFunctionTranslator.translate(fwa));
                        return;
                    }
                    final LinkedList<FunctionWithArguments<ECLeaf>> remainingArguments = new LinkedList<>();
                    final FunctionWithArguments<ECLeaf>[] args =
                            FunctionNormaliser.normalise(FWADeepCopy.copy(fwa)).getArgs();
                    final EquivalenceClass left = getEC(args[0]);
                    final boolean leftInCS = left.getMaximalScope() == ShallowCEEquivalenceClassBuilder.this.scope;
                    for (int i = 1; i < args.length; i++) {
                        final FunctionWithArguments<ECLeaf> arg = args[i];
                        final EquivalenceClass right = getEC(arg);
                        final boolean rightInCS =
                                right.getMaximalScope() == ShallowCEEquivalenceClassBuilder.this.scope;
                        if (leftInCS && rightInCS) {
                            // merge right into left
                            left.merge(right);
                            // replace right with left everywhere
                            ShallowCEEquivalenceClassBuilder.this.equivalenceClasses
                                    .replaceAll((ecLeaf, ec) -> ec == right ? left : ec);
                            ShallowCEEquivalenceClassBuilder.this.equivalenceClasses.put(arg, left);
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
                                    .newPredicateInstance(!ShallowCEEquivalenceClassBuilder.this.negated,
                                            Equals.IN_CLIPS, remainingArguments));
                        }
                    }
                }

                @Override
                public void defaultAction(final FunctionWithArguments<ECLeaf> function) {
                    // should never be called, yet, if someone adds other classes implementing the
                    // PredicateWithArguments interface other than PredicateWithArgumentsComposite,
                    // we would lose those tests if not preserved here
                    if (function instanceof PredicateWithArguments) {
                        log.warn("A class implementing PredicateWithArguments other than "
                                + "PredicateWithArgumentsComposite seems to have been added. Consider modifying "
                                + "the FWAEquivalenceClassBuilder to exploit equivalence classes within those "
                                + "new classes. Tests are simply preserved for now.");
                        ShallowCEEquivalenceClassBuilder.this.shallowTests
                                .add((PredicateWithArguments<ECLeaf>) function);
                    }
                }
            }

            @Override
            public void defaultAction(final ConditionalElement<ECLeaf> ce) {
                ce.getChildren().forEach(c -> c.accept(this));
            }

            @Override
            public void visit(final ExistentialConditionalElement<ECLeaf> ce) {
                // stop
            }

            @Override
            public void visit(final NegatedExistentialConditionalElement<ECLeaf> ce) {
                // stop
            }

            @Override
            public void visit(final TestConditionalElement<ECLeaf> ce) {
                ce.getPredicateWithArguments().accept(this.fwaBuilder);
            }

            @Override
            public void visit(final NotFunctionConditionalElement<ECLeaf> ce) {
                this.negated = !this.negated;
                defaultAction(ce);
                this.negated = !this.negated;
            }
        }

        private static ECSetRule consolidateOnCopiedEquivalenceClasses(final Template initialFactTemplate,
                final SingleFactVariable initialFactVariable, final Defrule.ECBasedCERule rule,
                final ConditionalElement<ECLeaf> ce, final Set<PredicateWithArguments<ECLeaf>> shallowTests,
                final Set<EquivalenceClass> equivalenceClasses, final int specificity) {
            final Set<ECFilterSet> filters =
                    new NoORsTranslator(initialFactTemplate, initialFactVariable, equivalenceClasses, shallowTests)
                            .collect(ce).getFilters();
            final Pair<SingleFactVariable, Set<SingleFactVariable>> initialFactAndVariables =
                    ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce);
            final Set<SingleFactVariable> shallowFVs = initialFactAndVariables.getRight();

            final Set<EquivalenceClass> usedECs = ECCollector.collect(filters);
            // final Set<SingleFactVariable> usedFVs =
            // usedECs.stream().map(EquivalenceClass::getDirectlyDependentFactVariables).flatMap
            // (Set::stream)
            // .collect(toIdentityHashSet());
            if (usedECs.contains(initialFactVariable.getEqual())) {
                // usedFVs.add(initialFactVariable);
                shallowFVs.add(initialFactVariable);
            } else {
                equivalenceClasses.remove(initialFactVariable.getEqual());
            }
            final Scope scope = rule.getParent().getCondition().getScope();
            final Set<EquivalenceClass> shallowECs =
                    equivalenceClasses.stream().filter(ec -> scope == ec.getMaximalScope()).collect(Collectors.toSet());
            return rule.newECSetRule(filters, shallowFVs, shallowECs, specificity);
        }

        private NoORsTranslator collect(final ConditionalElement<ECLeaf> ce) {
            return ce.accept(this);
        }

        static ECExistentialSet processExistentialCondition(final Template initialFactTemplate,
                final SingleFactVariable initialFactVariable, final ConditionalElement<ECLeaf> ce,
                final Set<EquivalenceClass> equivalenceClasses, final Scope scope, final boolean isPositive) {
            // Collect the existential FactVariables in a shallow manner (not including FVs in
            // nested existential elements)
            final Pair<SingleFactVariable, Set<SingleFactVariable>> initialFactAndOtherFVs =
                    ShallowFactVariableCollector.collectVariables(initialFactTemplate, ce);
            final Set<SingleFactVariable> shallowExistentialFVs = initialFactAndOtherFVs.getRight();
            final Set<EquivalenceClass> shallowExistentialECs =
                    equivalenceClasses.stream().filter(ec -> ec.getMaximalScope() == scope).collect(Collectors.toSet());

            /*
             * merge equals test conditional elements arguments and get the equivalence classes and
             * tests
             */

            final ShallowCEEquivalenceClassBuilder equivalenceClassBuilder =
                    ce.accept(new ShallowCEEquivalenceClassBuilder(equivalenceClasses, scope, false));
            equivalenceClasses.addAll(equivalenceClassBuilder.equivalenceClasses.values());

            final Set<PredicateWithArguments<ECLeaf>> shallowTests =
                    equivalenceClassBuilder.getTranslatedShallowTests();

            // Generate ECFilters from CE (recurse)
            final Set<ECFilterSet> filters =
                    new NoORsTranslator(initialFactTemplate, initialFactVariable, equivalenceClasses, shallowTests)
                            .collect(ce).getFilters();
            return toEcExistentialSet(initialFactVariable, isPositive, shallowExistentialFVs, shallowExistentialECs,
                    filters);
        }

        public static ECExistentialSet toEcExistentialSet(final SingleFactVariable initialFactVariable,
                final boolean isPositive, final Set<SingleFactVariable> shallowExistentialFVs,
                final Set<EquivalenceClass> shallowExistentialECs, final Set<ECFilterSet> filters) {
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
                        filters.stream().collect(Collectors.partitioningBy(filter -> {
                            final Set<EquivalenceClass> ecs = filter2ECs.get(filter);
                            return shallowExistentialECs.containsAll(ecs) || Collections
                                    .disjoint(shallowExistentialECs, ecs);
                        }, toSet()));
                pureFilters = tmp.get(Boolean.TRUE);
                mixedFilters = tmp.get(Boolean.FALSE);
            }

            final Set<EquivalenceClass> usedECs = Sets.newIdentityHashSet();
            final Set<SingleFactVariable> usedFVs = Sets.newIdentityHashSet();
            final Consumer<EquivalenceClass> markAsUsed = ec -> {
                usedECs.add(ec);
                usedFVs.addAll(ec.getDirectlyDependentFactVariables());
            };

            final List<FunctionWithArguments<ECLeaf>> predicates = new ArrayList<>();
            // identify the equivalence classes that contain local bindings and an equal parent
            for (final EquivalenceClass ec : shallowExistentialECs) {
                final Set<EquivalenceClass> equalParents = ec.getEqualParentEquivalenceClasses();
                if (equalParents.isEmpty()) continue;
                if (ec.getFactVariables().isEmpty() && ec.getSlotVariables().isEmpty() && ec.getConstantExpressions()
                        .isEmpty() && ec.getFunctionalExpressions().isEmpty()) continue;
                markAsUsed.accept(ec);
                for (final EquivalenceClass equalParent : equalParents) {
                    predicates.add(PredicateWithArgumentsComposite
                            .newPredicateInstance(Equals.IN_CLIPS, new ECLeaf(ec), new ECLeaf(equalParent)));
                }
            }

            {
                // ECExistentialSet is pure, thus we can cast the filters in mixedFilters to ECFilter
                mixedFilters.stream().map(f -> ((ECFilter) f).getFunction()).forEach(predicates::add);
                mixedFilters.stream().map(filter2ECs::get).flatMap(Set::stream).collect(toIdentityHashSet())
                        .forEach(markAsUsed);
            }

            // if any of the local ECs are not used as a parameter of a filter and do not have a parent equality
            // relationship, join them using a dummy predicate. If they have no connection to the regular fact
            // variables at all, use the initial fact in the join.
            // only those ECs have to be added specifically that
            // - contain 2 or more elements
            // - contain a fact binding not added any other way
            if (!usedECs.containsAll(shallowExistentialECs)) {
                final Set<EquivalenceClass> missingECs = Sets.newIdentityHashSet();
                // there is a fact binding => if there is one other element, we have an implicit test
                Sets.difference(shallowExistentialECs, usedECs).stream().filter(ec ->
                        (ec.getFactVariables().isEmpty() ? 0 : 1) + ec.getSlotVariables().size() + ec
                                .getConstantExpressions().size() + ec.getFunctionalExpressions().size() >= 2)
                        .forEach(missingECs::add);
                missingECs.forEach(markAsUsed);
                // if there is a fact binding not yet used, use the EC to join it together
                Sets.difference(shallowExistentialECs, usedECs).stream()
                        .filter(ec -> !usedFVs.containsAll(ec.getFactVariables())).forEach(missingECs::add);
                if (predicates.isEmpty()) missingECs.add(initialFactVariable.getEqual());
                if (!missingECs.isEmpty()) {
                    predicates.add(new PredicateWithArgumentsComposite<>(DummyPredicate.INSTANCE,
                            toArray(missingECs.stream().map(ECLeaf::new), ECLeaf[]::new)));
                    missingECs.forEach(markAsUsed);
                }
            }

            final ECFilter existentialClosure = new ECFilter(
                    predicates.size() == 1 ? (PredicateWithArguments<ECLeaf>) predicates.get(0)
                            : PredicateWithArgumentsComposite.newPredicateInstance(And.IN_CLIPS, predicates));
            return new ECExistentialSet(isPositive, initialFactVariable, shallowExistentialFVs, shallowExistentialECs,
                    pureFilters, existentialClosure);
        }

        @Override
        public void defaultAction(final ConditionalElement<ECLeaf> ce) {
            // just traverse the tree to find the existential CEs
            ce.getChildren().forEach(c -> c.accept(this));
        }

        @Override
        public void visit(final OrFunctionConditionalElement<ECLeaf> ce) {
            throw new Error("There should not be any OrFunctionCEs at this level.");
        }

        @Override
        public void visit(final ExistentialConditionalElement<ECLeaf> ce) {
            assert ce.getChildren().size() == 1;
            this.filters.add(processExistentialCondition(this.initialFactTemplate, this.initialFactVariable,
                    ce.getChildren().get(0), this.equivalenceClasses, ce.getScope(), true));
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<ECLeaf> ce) {
            assert ce.getChildren().size() == 1;
            this.filters.add(processExistentialCondition(this.initialFactTemplate, this.initialFactVariable,
                    ce.getChildren().get(0), this.equivalenceClasses, ce.getScope(), false));
        }
    }
}
