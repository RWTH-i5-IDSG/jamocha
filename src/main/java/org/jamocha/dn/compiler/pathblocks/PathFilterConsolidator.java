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
package org.jamocha.dn.compiler.pathblocks;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.PathSetRule;
import org.jamocha.dn.compiler.DeepFactVariableCollector;
import org.jamocha.dn.compiler.ShallowFactVariableCollector;
import org.jamocha.dn.compiler.Specificity;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.*;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWASymbolToPathTranslator;
import org.jamocha.function.impls.predicates.And;
import org.jamocha.function.impls.predicates.DummyPredicate;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.function.impls.predicates.Not;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.*;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.errors.VariableNotDeclaredError;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.ToArray.toArray;

/**
 * Collect all PathFilters inside all children of an OrFunctionConditionalElement, returning a List of Lists. Each inner
 * List contains the PathFilters of one child.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
public class PathFilterConsolidator implements DefaultConditionalElementsVisitor<SymbolLeaf> {

    private final Template initialFactTemplate;
    private final Defrule rule;
    @Getter
    private List<Defrule.PathSetRule> translateds = null;

    public List<Defrule.PathSetRule> consolidate() {
        assert this.rule.getCondition().getConditionalElements().size() == 1 : this.rule.getCondition()
                .getConditionalElements();
        return this.rule.getCondition().getConditionalElements().get(0).accept(this).translateds;
    }

    @Override
    public void defaultAction(final ConditionalElement<SymbolLeaf> ce) {
        // If there is no OrFunctionConditionalElement just proceed with the CE as it were
        // the only child of an OrFunctionConditionalElement.
        final Map<VariableSymbol, EquivalenceClass> symbolToEC = this.rule.getCondition().getVariableSymbols().stream()
                .collect(toMap(Function.identity(), VariableSymbol::getEqual));
        this.translateds =
                Collections.singletonList(NoORsPFC.consolidate(this.initialFactTemplate, this.rule, ce, symbolToEC));
    }

    @Override
    public void visit(final OrFunctionConditionalElement<SymbolLeaf> ce) {
        final Map<VariableSymbol, EquivalenceClass> symbolToEC = this.rule.getCondition().getVariableSymbols().stream()
                .collect(toMap(Function.identity(), VariableSymbol::getEqual));
        // For each child of the OrCE ...
        this.translateds = ce.getChildren().stream().map(child ->
                // ... collect all PathFilters in the child
                NoORsPFC.consolidate(this.initialFactTemplate, this.rule, child, symbolToEC))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static final class NoORsPFC implements DefaultConditionalElementsVisitor<SymbolLeaf> {

        private final Template initialFactTemplate;
        private final Path initialFactPath;
        private final Map<EquivalenceClass, Path> ec2Path;
        private final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf;
        private final boolean negated;

        @Getter
        private final Set<PathFilterSet> filters;

        private NoORsPFC(final Template initialFactTemplate, final Path initialFactPath,
                final Map<EquivalenceClass, Path> ec2Path,
                final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf,
                final Set<PredicateWithArguments<SymbolLeaf>> shallowTests, final boolean negated) {
            this.initialFactTemplate = initialFactTemplate;
            this.initialFactPath = initialFactPath;
            this.ec2Path = ec2Path;
            this.equivalenceClassToPathLeaf = equivalenceClassToPathLeaf;
            this.filters = shallowTests.stream()
                    .map(pwa -> new PathFilter(FWASymbolToPathTranslator.translate(pwa, equivalenceClassToPathLeaf)))
                    .collect(toSet());
            this.negated = negated;
        }

        public static PathSetRule consolidate(final Template initialFactTemplate, final Defrule rule,
                final ConditionalElement<SymbolLeaf> ce, final Map<VariableSymbol, EquivalenceClass> symbolToECbackup) {
            final Set<VariableSymbol> symbols = symbolToECbackup.keySet();
            // copy the equivalence classes
            final BiMap<EquivalenceClass, EquivalenceClass> oldToNew = HashBiMap
                    .create(symbolToECbackup.values().stream().collect(
                            toMap(Function.identity(), (final EquivalenceClass ec) -> new EquivalenceClass(ec))));
            replaceEC(symbols, oldToNew);

            final HashSet<SingleFactVariable> occurringFactVariables =
                    new HashSet<>(DeepFactVariableCollector.collect(ce));
            /*
             * inspect the equivalence class hierarchy for sections not contained in this rule part
             */
            // for every symbol in the CE
            for (final VariableSymbol vs : symbols) {
                // and thus for every equivalence class
                final EquivalenceClass ec = vs.getEqual();
                // check whether the fact variables are bound via a TPCE
                for (final Iterator<SingleFactVariable> iterator = ec.getFactVariables().iterator();
                        iterator.hasNext(); ) {
                    final SingleFactVariable fv = iterator.next();
                    if (!occurringFactVariables.contains(fv)) {
                        // the fact variable is not contained in the CE, remove it
                        iterator.remove();
                    }
                }
                // for every slot variable, check whether the corresponding fact variable is
                // contained in the CE
                for (final Iterator<SingleSlotVariable> iterator = ec.getSlotVariables().iterator();
                        iterator.hasNext(); ) {
                    final SingleSlotVariable sv = iterator.next();
                    if (!occurringFactVariables.contains(sv.getFactVariable())) {
                        // not contained, remove the SV and its reference to this EC
                        iterator.remove();
                        sv.getEqualSet().remove(ec);
                    }
                }
                // there should not be any constant or variable expressions within the equivalence
                // classes -- all tests are left explicitly
                assert ec.getConstantExpressions().isEmpty();
                assert ec.getFunctionalExpressions().isEmpty();
            }

            /* merge (bind)s and overlapping SlotVariables */
            for (final VariableSymbol vs : symbols) {
                final EquivalenceClass ec = vs.getEqual();
                for (final SingleSlotVariable sv : ec.getSlotVariables()) {
                    if (sv.getEqualSet().size() <= 1) continue;
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
                if (ec.getFactVariables().isEmpty() && ec.getSlotVariables().isEmpty()) {
                    if (symbolsInLeafs.contains(vs)) {
                        // vs is not bound
                        throw new VariableNotDeclaredError(vs.getImage());
                    }
                }
            }

            final PathSetRule result = consolidateOnCopiedEquivalenceClasses(initialFactTemplate, rule, ce,
                    symbols.stream().map(VariableSymbol::getEqual).collect(toSet()), Specificity.calculate(ce),
                    oldToNew);

            // reset the symbol - equivalence class mapping
            symbolToECbackup.forEach((vs, ec) -> vs.setEqual(ec));
            replaceEC(symbols, oldToNew.inverse());
            // restore the SlotVariable - equivalence class mapping
            symbolToECbackup.forEach((vs, ec) -> vs.getEqual().getSlotVariables().forEach(sv -> {
                sv.getEqualSet().clear();
                sv.getEqualSet().add(ec);
            }));
            return result;
        }

        // there are references to EquivalenceClass in:
        // VariableSymbol.equal
        // SingleFactVariable.equal
        // SingleSlotVariable.equal (Set)
        private static void replaceEC(final Set<VariableSymbol> symbols,
                final Map<EquivalenceClass, EquivalenceClass> map) {
            // VariableSymbol.equal
            symbols.forEach(sym -> sym.setEqual(map.getOrDefault(sym.getEqual(), sym.getEqual())));
            for (final Map.Entry<EquivalenceClass, EquivalenceClass> entry : map.entrySet()) {
                final EquivalenceClass oldEC = entry.getKey();
                final EquivalenceClass newEC = entry.getValue();
                // SingleFactVariable.equal
                oldEC.getFactVariables().forEach(fv -> fv.setEqual(newEC));
                // SingleSlotVariable.equal (Set)
                for (final SingleSlotVariable sv : oldEC.getSlotVariables()) {
                    final Set<EquivalenceClass> equalSet = sv.getEqualSet();
                    for (final Map.Entry<EquivalenceClass, EquivalenceClass> innerEntry : map.entrySet()) {
                        if (equalSet.remove(innerEntry.getKey())) equalSet.add(innerEntry.getValue());
                    }
                }
            }
        }

        @RequiredArgsConstructor
        static class ECShallowTestsCollector implements DefaultConditionalElementsVisitor<SymbolLeaf> {
            final FWAShallowTestsCollector fwaBuilder = new FWAShallowTestsCollector();
            final Set<PredicateWithArguments<SymbolLeaf>> shallowTests = new HashSet<>();
            boolean negated = false;

            @RequiredArgsConstructor
            class FWAShallowTestsCollector implements DefaultFunctionWithArgumentsVisitor<SymbolLeaf> {
                // assumption: all expressions are constant in the sense that subsequent calls to
                // evaluate yield the same result

                @Override
                public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> fwa) {
                    if (ECShallowTestsCollector.this.negated) {
                        ECShallowTestsCollector.this.shallowTests.add(new PredicateWithArgumentsComposite<>(NOT, fwa));
                    } else {
                        ECShallowTestsCollector.this.shallowTests.add(fwa);
                    }
                }

                @Override
                public void defaultAction(final FunctionWithArguments<SymbolLeaf> function) {
                    // should never be called, yet, if someone adds other classes implementing the
                    // PredicateWithArguments interface other than PredicateWithArgumentsComposite,
                    // we would lose those tests if not preserved here
                    if (function instanceof PredicateWithArguments) {
                        log.warn("A class implementing PredicateWithArguments other than "
                                + "PredicateWithArgumentsComposite seems to have been added. Consider "
                                + "modifying the FWAEquivalenceClassBuilder to exploit equivalence classes "
                                + "within those new classes. Tests are simply preserved for now.");
                        ECShallowTestsCollector.this.shallowTests.add((PredicateWithArguments<SymbolLeaf>) function);
                    }
                }
            }

            @Override
            public void defaultAction(final ConditionalElement<SymbolLeaf> ce) {
                ce.getChildren().forEach(c -> c.accept(this));
            }

            @Override
            public void visit(final ExistentialConditionalElement<SymbolLeaf> ce) {
                // stop
            }

            @Override
            public void visit(final NegatedExistentialConditionalElement<SymbolLeaf> ce) {
                // stop
            }

            @Override
            public void visit(final TestConditionalElement<SymbolLeaf> ce) {
                ce.getPredicateWithArguments().accept(this.fwaBuilder);
            }

            @Override
            public void visit(final NotFunctionConditionalElement<SymbolLeaf> ce) {
                this.negated = !this.negated;
                defaultAction(ce);
                this.negated = !this.negated;
            }
        }

        private static PathSetRule consolidateOnCopiedEquivalenceClasses(final Template initialFactTemplate,
                final Defrule rule, final ConditionalElement<SymbolLeaf> ce,
                final Set<EquivalenceClass> equivalenceClasses, final int specificity,
                final Map<EquivalenceClass, EquivalenceClass> oldToNew) {
            // get the tests on this level (stopping at existentials)
            final Set<PredicateWithArguments<SymbolLeaf>> shallowTests =
                    ce.accept(new ECShallowTestsCollector()).shallowTests;

            final Pair<Path, Map<EquivalenceClass, Path>> initialFactAndEc2Path =
                    ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
            final Path initialFactPath = initialFactAndEc2Path.getLeft();
            final Map<EquivalenceClass, Path> ec2Path = initialFactAndEc2Path.getRight();
            final Set<Path> allShallowPaths = new HashSet<>(ec2Path.values());

            final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf = new HashMap<>();
            for (final EquivalenceClass equiv : equivalenceClasses) {
                equivalenceClassToPathLeaf.put(equiv, equiv.getPathLeaf(ec2Path));
            }

            // recursion: get the filters nested within existentials
            final NoORsPFC instance =
                    new NoORsPFC(initialFactTemplate, initialFactPath, ec2Path, equivalenceClassToPathLeaf,
                            shallowTests, false).collect(ce);
            final Set<PathFilterSet> filters = instance.getFilters();

            // add the tests for the equivalence classes of this scope
            createEquivalenceClassTests(equivalenceClasses, ec2Path, equivalenceClassToPathLeaf, filters);

            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> originalEC2PathLeaf = new HashMap<>();
            oldToNew.forEach((k, v) -> originalEC2PathLeaf.put(k, equivalenceClassToPathLeaf.get(v)));

            // remove initial fact path (that is always added, if it was not used
            final HashSet<Path> paths = PathCollector.newHashSet().collectAllInSets(filters).getPaths();
            if (!paths.contains(initialFactPath)) {
                allShallowPaths.remove(initialFactPath);
            }

            return rule.new PathSetRule(filters, allShallowPaths, originalEC2PathLeaf, specificity);
        }

        private static void createEquivalenceClassTests(final Set<EquivalenceClass> equivalenceClasses,
                final Map<EquivalenceClass, Path> ec2Path,
                final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final Set<PathFilterSet> filters) {
            final Set<EquivalenceClass> neqAlreadyDone = new HashSet<>();
            for (final EquivalenceClass equiv : equivalenceClasses) {
                createEquivalenceClassTests(equiv, ec2Path, equivalenceClassToPathLeaf, filters, neqAlreadyDone);
            }
        }

        private static final Predicate NOT = FunctionDictionary.lookupPredicate(Not.IN_CLIPS, SlotType.BOOLEAN);

        private static void createEquivalenceClassTests(final EquivalenceClass equiv,
                final Map<EquivalenceClass, Path> ec2Path,
                final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final Set<PathFilterSet> filters,
                final Set<EquivalenceClass> neqAlreadyDone) {
            if (!neqAlreadyDone.add(equiv)) return;
            if (!equiv.hasMoreThanOneElementOrAParent()) return;
            final FunctionWithArguments<PathLeaf> element = equivalenceClassToPathLeaf.get(equiv);
            Objects.requireNonNull(element, "EquivalenceClass could not be mapped to PathLeaf!");
            // constant and variable expressions are empty - no need to create tests for them
            if (!equiv.getSlotVariables().isEmpty()) {
                createEqualSlotsAndFactsTests(equiv, filters, ec2Path, (x) -> true, (x) -> element);
            }
            for (final EquivalenceClass parent : equiv.getEqualParentEquivalenceClasses()) {
                filters.add(new PathFilter(GenericWithArgumentsComposite
                        .newPredicateInstance(Equals.IN_CLIPS, element, equivalenceClassToPathLeaf.get(parent))));
            }
        }

        private static void createEqualSlotsAndFactsTests(final EquivalenceClass equiv,
                final Set<PathFilterSet> filters, final Map<EquivalenceClass, Path> ec2Path,
                final java.util.function.Predicate<? super SingleFactVariable> pred,
                final Function<? super Set<FunctionWithArguments<PathLeaf>>, ? extends
                        FunctionWithArguments<PathLeaf>> elementChooser) {
            final Set<FunctionWithArguments<PathLeaf>> equalPathLeafs =
                    equiv.getSlotVariables().stream().filter(sv -> pred.test(sv.getFactVariable()))
                            .map(sv -> sv.getPathLeaf(ec2Path)).collect(toSet());
            equiv.getFactVariables().stream().filter(pred).map(fv -> fv.getPathLeaf(ec2Path))
                    .forEach(equalPathLeafs::add);
            if (equalPathLeafs.size() <= 1) {
                return;
            }
            final FunctionWithArguments<PathLeaf> element = elementChooser.apply(equalPathLeafs);
            equalPathLeafs.remove(element);
            for (final Iterator<FunctionWithArguments<PathLeaf>> iterator = equalPathLeafs.iterator();
                    iterator.hasNext(); ) {
                final FunctionWithArguments<PathLeaf> other = iterator.next();
                filters.add(new PathFilter(
                        GenericWithArgumentsComposite.newPredicateInstance(Equals.IN_CLIPS, element, other)));
            }
            // remove all used single slot variables and the fact variable except the for the
            // _element_ chosen (which is not part of equalPathLeafs)
            equiv.getSlotVariables().removeIf(sv -> equalPathLeafs.contains(sv.getPathLeaf(ec2Path)));
            equiv.getFactVariables().removeIf(fv -> equalPathLeafs.contains(fv.getPathLeaf(ec2Path)));
        }

        private <T extends ConditionalElement<SymbolLeaf>> NoORsPFC collect(final T ce) {
            return ce.accept(this);
        }

        private static Map<EquivalenceClass, PathLeaf> getMatchingFVsAndPathLeafs(final SingleFactVariable fv,
                final Set<EquivalenceClass> targets, final Map<EquivalenceClass, Path> ec2Path) {
            final Map<EquivalenceClass, PathLeaf> ecToTarget = new HashMap<>();
            for (final SingleSlotVariable sv : fv.getSlotVariables()) {
                final EquivalenceClass ec = sv.getEqual();
                if (targets.contains(ec)) {
                    ecToTarget.put(ec, sv.getPathLeaf(ec2Path));
                }
            }
            if (targets.contains(fv.getEqual())) {
                ecToTarget.put(fv.getEqual(), fv.getPathLeaf(ec2Path));
            }
            return ecToTarget;
        }

        // merge the fact variables given and apply all tests between them
        private static void mergeFVs(final Set<SingleFactVariable> shallowExistentialFVs,
                final Map<EquivalenceClass, Path> ec2Path, final Set<PathFilterSet> filters) {
            if (shallowExistentialFVs.size() == 1) return;
            final Set<EquivalenceClass> done = new HashSet<>();
            for (final SingleFactVariable factVariable : shallowExistentialFVs) {
                for (final SingleSlotVariable slotVariable : factVariable.getSlotVariables()) {
                    final EquivalenceClass equiv = slotVariable.getEqual();
                    if (!done.add(equiv)) continue;
                    createEqualSlotsAndFactsTests(equiv, filters, ec2Path, shallowExistentialFVs::contains,
                            set -> set.iterator().next());
                }
            }
        }

        @SuppressWarnings("checkstyle:methodlength")
        static Set<PathFilterSet> processExistentialCondition(final Template initialFactTemplate,
                final Path initialFactPath, final ConditionalElement<SymbolLeaf> ce,
                final Map<EquivalenceClass, Path> ec2Path,
                final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final boolean isPositive) {
            // Collect the existential FactVariables and corresponding paths from the existentialCE
            final Pair<Path, Map<EquivalenceClass, Path>> initialFactAndPathMap =
                    ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
            final Map<EquivalenceClass, Path> shallowExistentialEc2Path = initialFactAndPathMap.getRight();

            // combine existential FactVariables and Paths with non existential ones for PathFilter
            // generation
            ec2Path.putAll(shallowExistentialEc2Path);

            final Set<EquivalenceClass> equivalenceClasses = equivalenceClassToPathLeaf.keySet();
            for (final EquivalenceClass equiv : equivalenceClasses) {
                equivalenceClassToPathLeaf.computeIfAbsent(equiv, e -> e.getPathLeaf(ec2Path));
            }

            // Only existential Paths
            final Set<Path> shallowExistentialPaths = new HashSet<>(shallowExistentialEc2Path.values());

            final Set<PredicateWithArguments<SymbolLeaf>> shallowTests =
                    ce.accept(new ECShallowTestsCollector()).shallowTests;

            // Generate PathFilters from CE (recurse)
            final Set<PathFilterSet> filters =
                    new NoORsPFC(initialFactTemplate, initialFactPath, ec2Path, equivalenceClassToPathLeaf,
                            shallowTests, false).collect(ce).getFilters();

            {
                final Set<SingleFactVariable> shallowExistentialFVs =
                        new HashSet<>(ShallowFactVariableCollector.collect(ce));

                // if we have a fact variable that is bound here and elsewhere, throw
                final Set<EquivalenceClass> shallowExistentialECs = shallowExistentialEc2Path.keySet();
                shallowExistentialECs.stream().forEach(ec -> {
                    if (!shallowExistentialFVs.containsAll(ec.getMerged())) throw new RuntimeException(
                            "Shared Fact Variable binding inside and outside of Existential CE found, unsupported!");
                });

                // if there is more than just one TPCE (i.e. there is more than one FactVariable),
                // we have to join those in an own node first (thus they all come in via the same
                // edge) to be able to access all relevant information in the test of the
                // "existential CE"
                // equal slot variables within the existential-CE always have to be included in the
                // test, so add them too
                mergeFVs(shallowExistentialFVs, ec2Path, filters);
                // after the merge, the mapping between Slot Variables of the existential FVs and
                // their equivalence classes should be 1:1 if you ignore the SlotVariables that are
                // no longer contained in their equivalence class
                final Map<EquivalenceClass, SingleSlotVariable> ecToSVs =
                        shallowExistentialFVs.stream().flatMap(exFV -> exFV.getSlotVariables().stream()).collect(
                                toMap(sv -> sv.getEqual(), Function.identity(),
                                        (a, b) -> (a.getEqual().getSlotVariables().contains(a) ? a : b)));
                final Set<EquivalenceClass> ecsOccurringInSlotsOfExFVs = ecToSVs.keySet();

                /*
                 * if there are no symbol occurrences outside (which means we have at most one equal
                 * slot variable left), work off the CE now
                 */
                final Map<Boolean, Set<EquivalenceClass>> partitionedEquivalenceClasses =
                        ecsOccurringInSlotsOfExFVs.stream().collect(partitioningBy(
                                ec -> ec.getSlotVariables().size() <= 1 && !Optional
                                        .ofNullable(ec.getFactVariables().peekFirst())
                                        .filter(fv -> !shallowExistentialFVs.contains(fv)).isPresent(), toSet()));
                shallowExistentialECs.stream()
                        .filter(ec -> !ecsOccurringInSlotsOfExFVs.contains(ec) && !ec.getSlotVariables().isEmpty())
                        .forEach(ec -> partitionedEquivalenceClasses.get(!ec.getSlotVariables().stream()
                                .filter(sv -> shallowExistentialECs.contains(sv.getEqual())).findAny().isPresent())
                                .add(ec));

                final Set<EquivalenceClass> localEquivalenceClasses = partitionedEquivalenceClasses.get(Boolean.TRUE);
                for (final EquivalenceClass localEquivalenceClass : localEquivalenceClasses) {
                    final FunctionWithArguments<PathLeaf> element =
                            equivalenceClassToPathLeaf.get(localEquivalenceClass);
                    createEqualSlotsAndFactsTests(localEquivalenceClass, filters, ec2Path, (x) -> true, (x) -> element);
                }

                final Set<EquivalenceClass> nonLocalEquivalenceClasses =
                        partitionedEquivalenceClasses.get(Boolean.FALSE);
                if (!nonLocalEquivalenceClasses.isEmpty()) {
                    /*
                     * now, the only thing left to do should be to find non-existential fact
                     * variables or constants which we can use to create a final test establishing
                     * the ties to the non-existential parts of the non-local equivalence classes
                     * after which all existential parts are converted into a number in its counter
                     * column
                     */
                    // no constants found for the following ones, join non-existential fact
                    // variables
                    while (!nonLocalEquivalenceClasses.isEmpty()) {
                        final Optional<Map<EquivalenceClass, PathLeaf>> optBestCandidate =
                                nonLocalEquivalenceClasses.stream().flatMap(
                                        ec -> ec.getSlotVariables().stream().map(SingleSlotVariable::getFactVariable))
                                        .distinct().filter(fv -> !shallowExistentialFVs.contains(fv) && ec2Path
                                        .containsKey(fv.getEqual()))
                                        .map(fv -> getMatchingFVsAndPathLeafs(fv, nonLocalEquivalenceClasses, ec2Path))
                                        .max((a, b) -> Integer.compare(a.size(), b.size()));
                        if (optBestCandidate.isPresent()) {
                            final Map<EquivalenceClass, PathLeaf> merged = optBestCandidate.get();
                            for (final Map.Entry<EquivalenceClass, PathLeaf> entry : merged.entrySet()) {
                                final PathLeaf target = entry.getValue();
                                final EquivalenceClass ec = entry.getKey();
                                final SingleSlotVariable svDone = ecToSVs.get(ec);
                                final PathLeaf source;
                                if (svDone != null) {
                                    // remove the SV
                                    ec.getSlotVariables().remove(svDone);
                                    source = svDone.getPathLeaf(ec2Path);
                                } else {
                                    final SingleFactVariable exFV = ec.getFactVariables().pollFirst();
                                    assert shallowExistentialFVs.contains(exFV);
                                    source = exFV.getPathLeaf(ec2Path);
                                }
                                filters.add(new PathFilter(GenericWithArgumentsComposite
                                        .newPredicateInstance(Equals.IN_CLIPS, source, target)));
                            }
                            nonLocalEquivalenceClasses.removeAll(merged.keySet());
                        } else {
                            // only non-existential fact variables left
                            nonLocalEquivalenceClasses.stream().forEach(ec -> {
                                final PathLeaf source = ec.getFactVariables().pollFirst().getPathLeaf(ec2Path);
                                final PathLeaf target = ec.getSlotVariables().peekFirst().getPathLeaf(ec2Path);
                                filters.add(new PathFilter(GenericWithArgumentsComposite
                                        .newPredicateInstance(Equals.IN_CLIPS, source, target)));
                            });
                            nonLocalEquivalenceClasses.clear();
                        }
                    }
                }
            }

            final List<SingleFactVariable> deepExistentialFactVariables = DeepFactVariableCollector.collect(ce);

            final Set<Path> deepExistentialPaths =
                    deepExistentialFactVariables.stream().map(fv -> ec2Path.get(fv.getEqual())).collect(toSet());

            // Collect all used Paths for every PathFilter
            final Map<PathFilterSet, HashSet<Path>> filter2Paths = filters.stream().collect(Collectors
                    .toMap(Function.identity(),
                            filter -> PathCollector.newHashSet().collectAllInSets(filter).getPaths()));

            // Split PathFilters into those only using existential Paths and those also using non
            // existential Paths
            final LinkedList<PathFilterSet> pureExistentialFilters, mixedExistentialFilters, nonExistentialFilters;
            {
                final Map<Boolean, LinkedList<PathFilterSet>> tmp = filters.stream().collect(Collectors
                        .partitioningBy(filter -> deepExistentialPaths.containsAll(filter2Paths.get(filter)),
                                toCollection(LinkedList::new)));
                pureExistentialFilters = tmp.get(Boolean.TRUE);
                final Map<Boolean, LinkedList<PathFilterSet>> tmp2 = tmp.get(Boolean.FALSE).stream().collect(
                        partitioningBy(filter -> Collections.disjoint(deepExistentialPaths, filter2Paths.get(filter)),
                                toCollection(LinkedList::new)));
                mixedExistentialFilters = tmp2.get(Boolean.FALSE);
                nonExistentialFilters = tmp2.get(Boolean.TRUE);
            }

            final Set<PathFilterSet> purePart = new HashSet<>();
            purePart.addAll(pureExistentialFilters);
            purePart.addAll(nonExistentialFilters);
            // since we disallow nested existentials, casting the PathFilterSets to PathFilters will
            // work for every element of the mixed collection
            final PathFilter existentialClosure;
            if (mixedExistentialFilters.isEmpty()) {
                existentialClosure = new PathFilter(
                        new PredicateWithArgumentsComposite<PathLeaf>(DummyPredicate.INSTANCE,
                                toArray(Stream.concat(shallowExistentialPaths.stream(), Stream.of(initialFactPath))
                                        .map(p -> new PathLeaf(p, (SlotAddress) null)), PathLeaf[]::new)));
            } else if (1 == mixedExistentialFilters.size()) {
                final PathFilter mixed = (PathFilter) mixedExistentialFilters.getFirst();
                final HashSet<Path> pathsInMixed = PathCollector.newHashSet().collect(mixed).getPaths();
                if (pathsInMixed.containsAll(shallowExistentialPaths)) {
                    existentialClosure = mixed;
                } else {
                    final SetView<Path> missingPaths = Sets.difference(shallowExistentialPaths, pathsInMixed);
                    existentialClosure = new PathFilter(PredicateWithArgumentsComposite
                            .newPredicateInstance(And.IN_CLIPS, mixed.getFunction(),
                                    new PredicateWithArgumentsComposite<PathLeaf>(DummyPredicate.INSTANCE,
                                            toArray(missingPaths.stream().map(p -> new PathLeaf(p, null)),
                                                    PathLeaf[]::new))));
                }
            } else {
                Stream<FunctionWithArguments<PathLeaf>> argStream =
                        mixedExistentialFilters.stream().map(f -> ((PathFilter) f).getFunction());
                final HashSet<Path> pathsInMixed =
                        PathCollector.newHashSet().collectAllInSets(mixedExistentialFilters).getPaths();
                if (!pathsInMixed.containsAll(shallowExistentialPaths)) {
                    final SetView<Path> missingPaths = Sets.difference(shallowExistentialPaths, pathsInMixed);
                    argStream = Stream.concat(argStream, Stream.of(
                            new PredicateWithArgumentsComposite<PathLeaf>(DummyPredicate.INSTANCE,
                                    toArray(missingPaths.stream().map(p -> new PathLeaf(p, null)), PathLeaf[]::new))));
                }
                existentialClosure = new PathFilter(GenericWithArgumentsComposite.newPredicateInstance(And.IN_CLIPS,
                        (FunctionWithArguments<PathLeaf>[]) toArray(argStream, FunctionWithArguments[]::new)));
            }
            return Sets.newHashSet(
                    new PathExistentialSet(isPositive, initialFactPath, shallowExistentialPaths, purePart,
                            existentialClosure));
        }

        @Override
        public void defaultAction(final ConditionalElement<SymbolLeaf> ce) {
            // Just ignore. InitialFactCEs and TemplateCEs already did their job during
            // FactVariable collection
            // Test CEs are handled by ShallowTestCollector
        }

        @Override
        public void visit(final AndFunctionConditionalElement<SymbolLeaf> ce) {
            this.filters.addAll(ce.getChildren().stream()
                    // Process all children CEs
                    .map(child -> child
                            .accept(new NoORsPFC(this.initialFactTemplate, this.initialFactPath, this.ec2Path,
                                    this.equivalenceClassToPathLeaf, this.negated, new HashSet<>())).getFilters())
                    // merge Lists
                    .flatMap(Set::stream).collect(Collectors.toCollection(ArrayList::new)));
        }

        @Override
        public void visit(final OrFunctionConditionalElement<SymbolLeaf> ce) {
            throw new Error("There should not be any OrFunctionCEs at this level.");
        }

        @Override
        public void visit(final ExistentialConditionalElement<SymbolLeaf> ce) {
            assert ce.getChildren().size() == 1;
            this.filters.addAll(processExistentialCondition(this.initialFactTemplate, this.initialFactPath,
                    ce.getChildren().get(0), this.ec2Path, this.equivalenceClassToPathLeaf, true));
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<SymbolLeaf> ce) {
            assert ce.getChildren().size() == 1;
            this.filters.addAll(processExistentialCondition(this.initialFactTemplate, this.initialFactPath,
                    ce.getChildren().get(0), this.ec2Path, this.equivalenceClassToPathLeaf, false));
        }

        @Override
        public void visit(final NotFunctionConditionalElement<SymbolLeaf> ce) {
            assert ce.getChildren().size() == 1;
            // Call a PathFilterCollector for the child of the NotFunctionCE with toggled negated
            // flag.
            this.filters.addAll(ce.getChildren().get(0)
                    .accept(new NoORsPFC(this.initialFactTemplate, this.initialFactPath, this.ec2Path,
                            this.equivalenceClassToPathLeaf, !this.negated, new HashSet<>())).getFilters());
        }
    }
}
