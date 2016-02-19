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
package org.jamocha.languages.common;

import com.google.common.collect.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache;
import org.jamocha.dn.compiler.DeepFactVariableCollector;
import org.jamocha.dn.compiler.ShallowFactVariableCollector;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWASymbolToECTranslator;
import org.jamocha.function.fwatransformer.FWATranslator;
import org.jamocha.languages.common.ConditionalElement.*;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.Scope;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@Log4j2
public class RuleConditionProcessor {

    private static <L extends ExchangeableLeaf<L>> ConditionalElement<L> combine(
            final List<? extends ConditionalElement<L>> conditionalElements,
            final Function<List<? extends ConditionalElement<L>>, ConditionalElement<L>> combiner) {
        if (conditionalElements.size() > 1) {
            return combiner.apply(conditionalElements);
        }
        return conditionalElements.get(0);
    }

    public static <L extends ExchangeableLeaf<L>> ConditionalElement<L> combineViaAnd(
            final List<? extends ConditionalElement<L>> conditionalElements) {
        return combine(conditionalElements, list -> new AndFunctionConditionalElement<>(Lists.newArrayList(list)));
    }

    public static <L extends ExchangeableLeaf<L>> ConditionalElement<L> combineViaOr(
            final List<? extends ConditionalElement<L>> conditionalElements) {
        return combine(conditionalElements, list -> new OrFunctionConditionalElement<>(Lists.newArrayList(list)));
    }

    public static <L extends ExchangeableLeaf<L>> AndFunctionConditionalElement<L> and(final ConditionalElement<L> ce) {
        return new AndFunctionConditionalElement<>(Lists.newArrayList(ImmutableList.of(ce)));
    }

    public static <L extends ExchangeableLeaf<L>> ConditionalElement<L> flattenInPlace(
            final ConditionalElement<L> toFlatten) {
        ConditionalElement<L> ce = toFlatten;
        // move (not )s down to the lowest possible nodes
        ce = RuleConditionProcessor.moveNots(ce);
        // combine nested ands and ors
        RuleConditionProcessor.combineNested(ce);
        // expand ors
        ce = RuleConditionProcessor.expandOrs(ce);
        return ce;
    }

    public static ConditionalElement<SymbolLeaf> flattenInPlace(final RuleCondition condition) {
        return flattenInPlace(condition.getConditionalElements());
    }

    public static <L extends ExchangeableLeaf<L>> ConditionalElement<L> flattenInPlace(
            final List<ConditionalElement<L>> toFlatten) {
        final ArrayList<ConditionalElement<L>> copy = new ArrayList<>(toFlatten);
        toFlatten.clear();
        toFlatten.add(new AndFunctionConditionalElement<>(copy));
        return flattenInPlace(toFlatten.get(0));
    }

    public static List<ConstructCache.Defrule.ECBasedCERule> flattenOutOfPlace(final ConstructCache.Defrule rule) {
        return rule.newECBasedCERules();
    }

    public static void removeMissingBindingsInOR(final ConditionalElement<ECLeaf> ecCE) {
        for (final ConditionalElement<ECLeaf> child : ecCE.getChildren()) {
            removeMissingBindingsInNonOR(child);
        }
    }

    public static void removeMissingBindingsInNonOR(final ConditionalElement<ECLeaf> child) {
        final List<SingleFactVariable> fvs = DeepFactVariableCollector.collect(child);
        final Set<EquivalenceClass> equivalenceClasses = child.accept(new DeepECCollector()).getEquivalenceClasses();
        for (final EquivalenceClass equivalenceClass : equivalenceClasses) {
            equivalenceClass.getFactVariables().removeIf(negate(fvs::contains));
            equivalenceClass.getSlotVariables().removeIf(sv -> !fvs.contains(sv.getFactVariable()));
        }
    }

    @Value
    public static class CopyWithMetaInformation {
        ConditionalElement<ECLeaf> copy;
        HashBiMap<EquivalenceClass, EquivalenceClass> oldToNewEC;
        HashBiMap<SingleFactVariable, SingleFactVariable> oldToNewFV;
    }

    public static CopyWithMetaInformation copyDeeplyUsingNewECsAndFactVariables(final ConditionalElement<ECLeaf> child,
            final Collection<EquivalenceClass> equivalenceClasses) {
        final HashBiMap<EquivalenceClass, EquivalenceClass> oldToNewEC = HashBiMap.create(equivalenceClasses.stream()
                .collect(toMap(java.util.function.Function.identity(), EquivalenceClass::new)));
        for (final Map.Entry<EquivalenceClass, EquivalenceClass> entry : oldToNewEC.entrySet()) {
            final EquivalenceClass oldEC = entry.getKey();
            final EquivalenceClass newEC = entry.getValue();
            oldEC.getEqualParentEquivalenceClasses().stream().map(oldToNewEC::get)
                    .forEach(newEC::addEqualParentEquivalenceClass);
        }

        // collect all fact variables contained in this child
        final List<SingleFactVariable> deepFactVariables = DeepFactVariableCollector.collect(child);

        // copy all fact variables contained in this child while making them point at the
        // newly created equivalence classes
        final HashBiMap<SingleFactVariable, SingleFactVariable> oldToNewFV = HashBiMap.create(deepFactVariables.stream()
                .collect(toMap(Function.identity(),
                        (final SingleFactVariable fv) -> new SingleFactVariable(fv, oldToNewEC))));

        // replace the old FVs in the new ECs by the new FVs
        for (final Iterator<EquivalenceClass> ecIter = oldToNewEC.values().iterator(); ecIter.hasNext(); ) {
            final EquivalenceClass newEC = ecIter.next();
            newEC.getFactVariables().removeIf(negate(deepFactVariables::contains));
            newEC.getSlotVariables().removeIf(sv -> !deepFactVariables.contains(sv.getFactVariable()));
            if (newEC.getElementCount() == 0) {
                ecIter.remove();
                continue;
            }
            newEC.getFactVariables().replaceAll(oldToNewFV::get);
            newEC.getSlotVariables()
                    .replaceAll(sv -> oldToNewFV.get(sv.getFactVariable()).getSlots().get(sv.getSlot()));
        }

        final ConditionalElement<ECLeaf> copy = child.accept(new CEECReplacer(oldToNewEC, oldToNewFV)).getResult();

        // remove all equivalence classes not occurring in the filters and only consisting of a single constant xor a
        // single functional expression
        final Set<EquivalenceClass> usedECs = copy.accept(new DeepECCollector()).getEquivalenceClasses();
        oldToNewEC.values().removeIf(
                ec -> !usedECs.contains(ec) && ec.getElementCount() == 1 && !(ec.getConstantExpressions().isEmpty()
                        && ec.getFunctionalExpressions().isEmpty()));

        return new CopyWithMetaInformation(copy, oldToNewEC, oldToNewFV);
    }

    public static <L extends ExchangeableLeaf<L>> ConditionalElement<L> moveNots(final ConditionalElement<L> ce) {
        return ce.accept(new NotFunctionConditionalElementSeep<L>()).getCe();
    }

    public static <L extends ExchangeableLeaf<L>> void combineNested(final ConditionalElement<L> ce) {
        ce.accept(new CombineNested<L>());
    }

    public static <L extends ExchangeableLeaf<L>> ConditionalElement<L> expandOrs(final ConditionalElement<L> ce) {
        return new OrFunctionConditionalElement<L>(Lists.newArrayList(ce.accept(new ExpandOrs<L>()).ces));
    }

    private static class ExpandOrs<L extends ExchangeableLeaf<L>> implements DefaultConditionalElementsVisitor<L> {
        @Getter
        private List<AndFunctionConditionalElement<L>> ces;

        private void expand(final ConditionalElement<L> ce) {
            // recurse on children, partition to find the children that had an (or ) on top level
            // (will have more than one element)
            final Map<Boolean, List<List<AndFunctionConditionalElement<L>>>> partition =
                    ce.getChildren().stream().map(el -> el.accept(new ExpandOrs<L>()).getCes())
                            .collect(partitioningBy(el -> el.size() == 1));
            final List<List<AndFunctionConditionalElement<L>>> orLists = partition.get(Boolean.FALSE);
            final List<List<AndFunctionConditionalElement<L>>> singletonLists = partition.get(Boolean.TRUE);
            if (!singletonLists.isEmpty()) {
                this.ces = Lists.newArrayList(ImmutableList.of(new AndFunctionConditionalElement<L>(
                        singletonLists.stream().flatMap(l -> l.get(0).getChildren().stream()).collect(toList()))));
            } else {
                this.ces = orLists.remove(0);
            }
            // gradually blow up the CEs
            // the elements of CEs will always be AndFunctionConditionalElements acting as a list
            // while the construction of CEs is incomplete, thus we start by adding the shared part
            // for every (or ) occurrence we need to duplicate the list of CEs and combine them with
            // the (or ) elements
            for (final List<AndFunctionConditionalElement<L>> orList : orLists) {
                final List<AndFunctionConditionalElement<L>> newCEs = new ArrayList<>(orList.size() * this.ces.size());
                for (final AndFunctionConditionalElement<L> newPart : orList) {
                    // copy the old part and add the shared part, add combination of them to newCEs
                    for (final AndFunctionConditionalElement<L> oldPart : this.ces) {
                        final ArrayList<ConditionalElement<L>> children =
                                Lists.newArrayList(new ArrayList<>(oldPart.getChildren()));
                        children.addAll(newPart.getChildren());
                        newCEs.add(new AndFunctionConditionalElement<>(children));
                    }
                }
                this.ces = newCEs;
            }
        }

        @Override
        public void visit(final AndFunctionConditionalElement<L> ce) {
            expand(ce);
        }

        @Override
        public void visit(final ExistentialConditionalElement<L> ce) {
            expand(ce);
            this.ces = this.ces.stream().map(and -> and(new ExistentialConditionalElement<>(ce.scope, and)))
                    .collect(toList());
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<L> ce) {
            expand(ce);
            this.ces = this.ces.stream().map(and -> and(new NegatedExistentialConditionalElement<>(ce.scope, and)))
                    .collect(toList());
        }

        @Override
        public void defaultAction(final ConditionalElement<L> ce) {
            this.ces = Lists.newArrayList(ImmutableList.of(and(ce)));
        }

        @Override
        public void visit(final OrFunctionConditionalElement<L> ce) {
            this.ces = ce.getChildren().stream().flatMap(el -> el.accept(new ExpandOrs<L>()).getCes().stream())
                    .collect(toList());
        }
    }

    private static class CombineNested<L extends ExchangeableLeaf<L>> implements DefaultConditionalElementsVisitor<L> {

        private void combineNested(final ConditionalElement<L> ce, final Supplier<Stripping<L>> supplier) {
            final List<ConditionalElement<L>> oldChildrenList = ImmutableList.copyOf(ce.getChildren());
            final List<ConditionalElement<L>> childrenList = ce.getChildren();
            childrenList.clear();
            for (final ConditionalElement<L> conditionalElement : oldChildrenList) {
                childrenList.addAll(conditionalElement.accept(supplier.get()).getCes());
            }
        }

        @Override
        public void visit(final AndFunctionConditionalElement<L> ce) {
            defaultAction(ce);
            combineNested(ce, StripAnds::new);
        }

        @Override
        public void visit(final OrFunctionConditionalElement<L> ce) {
            defaultAction(ce);
            combineNested(ce, StripOrs::new);
        }

        @Override
        public void defaultAction(final ConditionalElement<L> ce) {
            ce.getChildren().forEach(child -> child.accept(this));
        }
    }

    @RequiredArgsConstructor
    private static class NotFunctionConditionalElementSeep<L extends ExchangeableLeaf<L>>
            implements ConditionalElementsVisitor<L> {

        private final boolean negated;
        @Getter
        private ConditionalElement<L> ce = null;

        NotFunctionConditionalElementSeep() {
            this.negated = false;
        }

        private void processChildren(final ConditionalElement<L> ce, final boolean nextNegated) {
            ce.getChildren().replaceAll((final ConditionalElement<L> x) -> x
                    .accept(new NotFunctionConditionalElementSeep<L>(nextNegated)).ce);
        }

        private static <L extends ExchangeableLeaf<L>> ConditionalElement<L> applySkippingIfNegated(
                final ConditionalElement<L> ce, final boolean negated,
                final Function<List<ConditionalElement<L>>, ConditionalElement<L>> ctor) {
            return negated ? ctor.apply(ce.getChildren()) : ce;
        }

        @Override
        public void visit(final NotFunctionConditionalElement<L> ce) {
            assert 1 == ce.getChildren().size();
            this.ce = ce.getChildren().get(0).accept(new NotFunctionConditionalElementSeep<L>(!this.negated)).ce;
        }

        @Override
        public void visit(final OrFunctionConditionalElement<L> ce) {
            this.ce = applySkippingIfNegated(ce, this.negated, RuleConditionProcessor::combineViaAnd);
            processChildren(this.ce, this.negated);
        }

        @Override
        public void visit(final AndFunctionConditionalElement<L> ce) {
            this.ce = applySkippingIfNegated(ce, this.negated, RuleConditionProcessor::combineViaOr);
            processChildren(this.ce, this.negated);
        }

        @Override
        public void visit(final ExistentialConditionalElement<L> ce) {
            this.ce = this.negated ? ce.negate() : ce;
            processChildren(this.ce, false);
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<L> ce) {
            this.ce = this.negated ? ce.negate() : ce;
            processChildren(this.ce, false);
        }

        private void visitLeaf(final ConditionalElement<L> ce) {
            this.ce =
                    this.negated ? new NotFunctionConditionalElement<L>(Lists.newArrayList(ImmutableList.of(ce))) : ce;
        }

        @Override
        public void visit(final InitialFactConditionalElement<L> ce) {
            visitLeaf(ce);
        }

        @Override
        public void visit(final TestConditionalElement<L> ce) {
            visitLeaf(ce);
        }

        @Override
        public void visit(final TemplatePatternConditionalElement<L> ce) {
            visitLeaf(ce);
        }
    }

    private interface Stripping<L extends ExchangeableLeaf<L>> extends ConditionalElementsVisitor<L> {
        List<ConditionalElement<L>> getCes();
    }

    private static class StripAnds<L extends ExchangeableLeaf<L>>
            implements DefaultConditionalElementsVisitor<L>, Stripping<L> {
        @Getter(onMethod = @__({@Override}))
        private List<ConditionalElement<L>> ces;

        @Override
        public void defaultAction(final ConditionalElement<L> ce) {
            this.ces = Lists.newArrayList(ImmutableList.of(ce));
        }

        @Override
        public void visit(final AndFunctionConditionalElement<L> ce) {
            this.ces = ce.getChildren();
        }
    }

    private static class StripOrs<L extends ExchangeableLeaf<L>>
            implements DefaultConditionalElementsVisitor<L>, Stripping<L> {
        @Getter(onMethod = @__({@Override}))
        private List<ConditionalElement<L>> ces;

        @Override
        public void defaultAction(final ConditionalElement<L> ce) {
            this.ces = Lists.newArrayList(ImmutableList.of(ce));
        }

        @Override
        public void visit(final OrFunctionConditionalElement<L> ce) {
            this.ces = ce.getChildren();
        }
    }

    @Getter
    public static class ShallowSymbolCollector implements DefaultConditionalElementsVisitor<SymbolLeaf>,
            DefaultFunctionWithArgumentsLeafVisitor<SymbolLeaf> {
        final Set<VariableSymbol> symbols = new HashSet<>();

        @Override
        public void visit(final TestConditionalElement<SymbolLeaf> ce) {
            ce.getPredicateWithArguments().accept(this);
        }

        @Override
        public void defaultAction(final ConditionalElement<SymbolLeaf> ce) {
            ce.children.forEach(c -> c.accept(this));
        }

        @Override
        public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
        }

        @Override
        public void visit(final GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
        }

        @Override
        public void visit(final SymbolLeaf leaf) {
            this.symbols.add(leaf.getSymbol());
        }
    }

    @Getter
    public static class ExistentialECSplitter extends CETranslator<ECLeaf, ECLeaf> {
        @Value
        static class State {
            final Scope scope;
            final Set<SingleFactVariable> shallowFactVariables;
            final Map<EquivalenceClass, EquivalenceClass> oldToNew;
            final Set<EquivalenceClass> newECs;
        }

        final State state;

        public ExistentialECSplitter(final Scope scope, final Set<SingleFactVariable> shallowFactVariables,
                final Set<EquivalenceClass> newECs) {
            this.state =
                    new State(scope, shallowFactVariables, split(scope, shallowFactVariables, newECs::add), newECs);
        }

        public ExistentialECSplitter(final State state) {
            this.state = state;
        }

        public static Pair<ConditionalElement<ECLeaf>, Set<EquivalenceClass>> split(final Scope scope,
                final ConditionalElement<ECLeaf> child) {
            final Set<EquivalenceClass> newECs = Sets.newIdentityHashSet();
            final ConditionalElement<ECLeaf> result = child.accept(
                    new ExistentialECSplitter(scope, newIdentityHashSet(ShallowFactVariableCollector.collect(child)),
                            newECs)).getResult();
            assert null != result;
            return Pair.of(result, newECs);
        }

        @Override
        public ExistentialECSplitter of() {
            return new ExistentialECSplitter(this.state);
        }

        @Override
        public void visit(final OrFunctionConditionalElement<ECLeaf> ce) {
            throw new IllegalStateException("No ORs are allowed at this point!");
        }

        private static Map<EquivalenceClass, EquivalenceClass> split(final Scope scope,
                final Set<SingleFactVariable> shallowFactVariables, final Consumer<EquivalenceClass> newECConsumer) {
            final Set<EquivalenceClass> ecs =
                    Stream.concat(shallowFactVariables.stream().map(SingleFactVariable::getEqual),
                            shallowFactVariables.stream().flatMap(fv -> fv.getSlotVariables().stream())
                                    .flatMap(sv -> sv.getEqualSet().stream())).collect(toIdentityHashSet());
            final Map<EquivalenceClass, EquivalenceClass> oldToNew = HashBiMap.create();
            for (final EquivalenceClass oldEC : ecs) {
                oldToNew.put(oldEC, splitEC(scope, shallowFactVariables, oldEC, newECConsumer));
            }
            return oldToNew;
        }

        private static EquivalenceClass splitEC(final Scope scope, final Set<SingleFactVariable> shallowFactVariables,
                final EquivalenceClass oldEC, final Consumer<EquivalenceClass> newECConsumer)
                throws IllegalStateException {
            assert oldEC.getFunctionalExpressions().isEmpty() && (oldEC.getConstantExpressions().isEmpty() || (
                    oldEC.getFactVariables().isEmpty() && oldEC.getSlotVariables().isEmpty())) :
                    "This method assumes that the parser leaves the equality tests involving constants and "
                            + "functional expressions explicitly. Thus there should only be slot/fact binding ECs and "
                            + "constant ECs (separately).";
            if (scope == oldEC.getMaximalScope()) {
                // EC belongs to this scope, nothing to do
                return oldEC;
            }
            if (scope.isParentOf(oldEC.getMaximalScope())) {
                // can not be accessed in the current scope!
                throw new IllegalStateException();
            }
            // oldEC scope is parent scope of this scope
            // => modify when it reappears in current scope
            final EquivalenceClass newEC = EquivalenceClass.newECFromType(scope, oldEC.getType());
            // move fact bindings
            for (final Iterator<SingleFactVariable> iterator = oldEC.getFactVariables().iterator();
                    iterator.hasNext(); ) {
                final SingleFactVariable factVariable = iterator.next();
                if (shallowFactVariables.contains(factVariable)) {
                    // add to new EC
                    newEC.add(factVariable);
                    // remove from old EC
                    iterator.remove();
                    // replace pointer in fact binding
                    factVariable.setEqual(newEC);
                }
            }
            // move slot bindings
            for (final Iterator<SingleSlotVariable> iterator = oldEC.getSlotVariables().iterator();
                    iterator.hasNext(); ) {
                final SingleSlotVariable slotVariable = iterator.next();
                if (shallowFactVariables.contains(slotVariable.getFactVariable())) {
                    // add to new EC
                    newEC.add(slotVariable);
                    // remove from old EC
                    iterator.remove();
                    // replace pointer in fact binding
                    slotVariable.getEqualSet().remove(oldEC);
                    slotVariable.getEqualSet().add(newEC);
                }
            }
            // if there are no bindings at the current level, use the old EC
            if (newEC.getSlotVariables().isEmpty() && newEC.getFactVariables().isEmpty()) {
                return oldEC;
            }
            // add equal parent relationship
            newEC.addEqualParentEquivalenceClass(oldEC);
            newECConsumer.accept(newEC);
            return newEC;
        }

        @Override
        public void visit(final TestConditionalElement<ECLeaf> ce) {
            this.result = new TestConditionalElement<>((PredicateWithArguments<ECLeaf>) ce.getPredicateWithArguments()
                    .accept(new FWAECReplacer(key -> this.state.oldToNew.getOrDefault(key, key)))
                    .getFunctionWithArguments());
        }

        @Override
        public void visit(final ExistentialConditionalElement<ECLeaf> ce) {
            assert ce.getChildren().size() == 1;
            this.result = new ExistentialConditionalElement<>(ce.getScope(), ce.getChildren().get(0)
                    .accept(new ExistentialECSplitter(ce.scope,
                            newIdentityHashSet(ShallowFactVariableCollector.collect(ce.getChildren().get(0))),
                            this.state.newECs)).result.getChildren());
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<ECLeaf> ce) {
            assert ce.getChildren().size() == 1;
            this.result = new NegatedExistentialConditionalElement<>(ce.getScope(), new AndFunctionConditionalElement<>(
                    ce.getChildren().get(0).accept(new ExistentialECSplitter(ce.scope,
                            newIdentityHashSet(ShallowFactVariableCollector.collect(ce.getChildren().get(0))),
                            this.state.newECs)).result.getChildren()));
        }
    }

    @AllArgsConstructor
    public static class CESymbolToECTranslator extends CETranslator<SymbolLeaf, ECLeaf> {
        private Scope scope;
        private final HashMap<Pair<Scope, ConstantLeaf<SymbolLeaf>>, RuleCondition.EquivalenceClass>
                constantToEquivalenceClasses;

        @Override
        public CETranslator<SymbolLeaf, ECLeaf> of() {
            return new CESymbolToECTranslator(this.scope, this.constantToEquivalenceClasses);
        }

        @Override
        public void visit(final TestConditionalElement<SymbolLeaf> ce) {
            this.result = new TestConditionalElement<>(FWASymbolToECTranslator
                    .translate(this.scope, this.constantToEquivalenceClasses, ce.getPredicateWithArguments()));
        }

        @Override
        public void visit(final ExistentialConditionalElement<SymbolLeaf> ce) {
            final Scope scopeBackup = this.scope;
            this.scope = ce.getScope();
            super.visit(ce);
            this.scope = scopeBackup;
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<SymbolLeaf> ce) {
            final Scope scopeBackup = this.scope;
            this.scope = ce.getScope();
            super.visit(ce);
            this.scope = scopeBackup;
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @Getter
    public static class CEExistentialTransformer implements DefaultConditionalElementsVisitor<ECLeaf> {
        private ConditionalElement<ECLeaf> ce;

        @Override
        public void defaultAction(final ConditionalElement<ECLeaf> ce) {
            ce.children.replaceAll(c -> c.accept(new CEExistentialTransformer()).ce);
            this.ce = ce;
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<ECLeaf> ce) {
            assert ce.getChildren().size() == 1;
            final Scope scope = ce.getScope();

            final ConditionalElement<ECLeaf> andCE = ce.getChildren().get(0);
            // since there are no OR-CEs within ce, we only have to consider TemplatePattern & InitialFact CEs
            // and Test & [Negated]Existential CEs (not-CE will also be collected, as we assume there is a test CE
            // directly contained)
            final Set<ConditionalElement<ECLeaf>> fvCEs = andCE.accept(new ShallowTPAndIFCECollector()).fvCEs;
            final Set<ConditionalElement<ECLeaf>> testAndExistentialCEs =
                    andCE.accept(new ShallowTestCEAndExistentialCollector()).getTestAndExistentialCEs();

            final Map<Boolean, List<ConditionalElement<ECLeaf>>> shallowTestsMap1 = testAndExistentialCEs.stream()
                    .collect(partitioningBy(t -> t.accept(new DeepFactVariableCollector<>()).getFactVariables().stream()
                            .map(fv -> fv.getEqual()).anyMatch(ec -> ec.getMaximalScope() == scope)));
            final Map<Boolean, List<ConditionalElement<ECLeaf>>> shallowTestsMap = testAndExistentialCEs.stream()
                    .collect(partitioningBy(t -> t.accept(new DeepECCollector()).getEquivalenceClasses().stream()
                            .anyMatch(ec -> ec.getMaximalScope() == scope)));
            // store all test and existential CEs that contain at least one local EC in innerTests
            final List<ConditionalElement<ECLeaf>> innerTests = shallowTestsMap.get(Boolean.TRUE);
            // and the rest in outerTests
            final List<ConditionalElement<ECLeaf>> outerTests = shallowTestsMap.get(Boolean.FALSE);

            final ArrayList<ConditionalElement<ECLeaf>> disjuncts = Lists.newArrayList();
            // add the conditions for the situations where the existential condition is fulfilled just because there
            // is (at least) one template used existentially without any facts
            final List<ExistentialConditionalElement<ECLeaf>> fvsDone = new ArrayList<>(fvCEs.size());
            for (final ConditionalElement<ECLeaf> fvCE : fvCEs) {
                final NegatedExistentialConditionalElement<ECLeaf> newCE =
                        new NegatedExistentialConditionalElement<>(scope, and(fvCE));
                disjuncts.add(fvsDone.isEmpty() ? newCE : new AndFunctionConditionalElement<>(
                        Lists.newArrayList(Iterables.concat(fvsDone, Collections.singleton(newCE)))));
                fvsDone.add(newCE.negate());
            }
            // add the part where there are facts for all existential templates, but the instances don't match the
            // existential predicates
            final NegatedExistentialConditionalElement<ECLeaf> negatedExistSatisfyingInner =
                    new NegatedExistentialConditionalElement<>(scope, new AndFunctionConditionalElement<>(
                            Lists.newArrayList(Iterables.concat(fvCEs, innerTests))));
            disjuncts.add(new AndFunctionConditionalElement<>(
                    Lists.newArrayList(Iterables.concat(fvsDone, Collections.singleton(negatedExistSatisfyingInner)))));

            final Iterable<ConditionalElement<ECLeaf>> existsSatisfyingInner =
                    Collections.singleton(negatedExistSatisfyingInner.negate());

            // add the part where there are fact for all existential templates matching the existential predicates,
            // but the non-existential predicates are not fulfilled
            final List<ConditionalElement<ECLeaf>> outerTestsDone = new ArrayList<>(outerTests.size());
            for (final ConditionalElement<ECLeaf> outerTest : outerTests) {
                final Iterable<ConditionalElement<ECLeaf>> negatedOuterTest = Collections
                        .singleton(new NotFunctionConditionalElement<>(new ArrayList<>(ImmutableList.of(outerTest))));
                disjuncts.add(new AndFunctionConditionalElement<>(Lists.newArrayList(Iterables
                        .concat(existsSatisfyingInner, outerTestsDone.isEmpty() ? negatedOuterTest
                                : Iterables.concat(outerTestsDone, negatedOuterTest)))));
                outerTestsDone.add(outerTest);
            }
            this.ce = new OrFunctionConditionalElement<>(disjuncts);
        }

        @Override
        public void visit(final ExistentialConditionalElement<ECLeaf> ce) {
            assert ce.getChildren().size() == 1;
            final Scope scope = ce.getScope();
            final ConditionalElement<ECLeaf> andCE = ce.getChildren().get(0);
            final Map<Set<SingleFactVariable>, List<ConditionalElement<ECLeaf>>> partition = determinePartition(andCE);
            final ArrayList<ConditionalElement<ECLeaf>> conjuncts = Lists.newArrayList();
            for (final ConditionalElement<ECLeaf> conditionalElement : partition
                    .remove(Collections.<SingleFactVariable>emptySet())) {
                // here we don't need the existential, since the CE doesn't use any FVs
                conjuncts.add(conditionalElement);
            }
            for (final List<ConditionalElement<ECLeaf>> list : partition.values()) {
                conjuncts.add(new ExistentialConditionalElement<>(scope, new AndFunctionConditionalElement<>(list)));
            }
            this.ce = new AndFunctionConditionalElement<>(conjuncts);
        }

        private Map<Set<SingleFactVariable>, List<ConditionalElement<ECLeaf>>> determinePartition(
                final ConditionalElement<ECLeaf> ce) {
            final Map<SingleFactVariable, Set<SingleFactVariable>> occurringWith = new HashMap<>();
            final Map<ConditionalElement<ECLeaf>, SingleFactVariable> childToRepresentative = new HashMap<>();
            for (final ConditionalElement<ECLeaf> child : ce.getChildren()) {
                // for every child, look at the ECs used within tests and the FVs occurring in
                // InitialFact- and TemplatePatternCEs
                final Set<EquivalenceClass> childECs = child.accept(new ShallowECCollector()).equivalenceClasses;
                final Set<SingleFactVariable> fvs = Stream.concat(
                        childECs.stream().map(EquivalenceClass::getDirectlyDependentFactVariables).flatMap(Set::stream),
                        ShallowFactVariableCollector.collect(child).stream()).collect(toIdentityHashSet());
                // should be empty in rare cases only
                // e.g. when only a test like (< 2 3) is contained
                if (!fvs.isEmpty()) {
                    childToRepresentative.put(child, fvs.iterator().next());
                    merge(occurringWith, fvs);
                }
            }
            final Map<Set<SingleFactVariable>, List<ConditionalElement<ECLeaf>>> partition = ce.getChildren().stream()
                    .collect(groupingBy(c -> Optional.ofNullable(childToRepresentative.get(c)).map(occurringWith::get)
                            .orElse(Collections.emptySet())));
            return partition;
        }

        protected void merge(final Map<SingleFactVariable, Set<SingleFactVariable>> occurringWith,
                final Collection<SingleFactVariable> factVariables) {
            final Set<SingleFactVariable> combinedFVs = factVariables.stream()
                    .flatMap(fv -> occurringWith.getOrDefault(fv, Collections.emptySet()).stream())
                    .collect(toCollection(HashSet::new));
            combinedFVs.addAll(factVariables);
            combinedFVs.forEach(fv -> occurringWith.put(fv, combinedFVs));
        }
    }

    private static class ShallowTPAndIFCECollector implements DefaultShallowConditionalElementsLeafVisitor<ECLeaf> {
        final Set<ConditionalElement<ECLeaf>> fvCEs = Sets.newIdentityHashSet();

        @Override
        public void visit(final InitialFactConditionalElement<ECLeaf> ce) {
            this.fvCEs.add(ce);
        }

        @Override
        public void visit(final TemplatePatternConditionalElement<ECLeaf> ce) {
            this.fvCEs.add(ce);
        }

        @Override
        public void visit(final TestConditionalElement<ECLeaf> ce) {
        }
    }

    @Getter
    private static class ShallowTestCEAndExistentialCollector implements ConditionalElementsVisitor<ECLeaf> {
        final Set<ConditionalElement<ECLeaf>> testAndExistentialCEs = Sets.newIdentityHashSet();

        @Override
        public void visit(final AndFunctionConditionalElement<ECLeaf> ce) {
        }

        @Override
        public void visit(final OrFunctionConditionalElement<ECLeaf> ce) {
        }

        @Override
        public void visit(final InitialFactConditionalElement<ECLeaf> ce) {
        }

        @Override
        public void visit(final TemplatePatternConditionalElement<ECLeaf> ce) {
        }

        @Override
        public void visit(final TestConditionalElement<ECLeaf> ce) {
            this.testAndExistentialCEs.add(ce);
        }

        @Override
        public void visit(final NotFunctionConditionalElement<ECLeaf> ce) {
            this.testAndExistentialCEs.add(ce);
        }

        @Override
        public void visit(final ExistentialConditionalElement<ECLeaf> ce) {
            this.testAndExistentialCEs.add(ce);
        }

        @Override
        public void visit(final NegatedExistentialConditionalElement<ECLeaf> ce) {
            this.testAndExistentialCEs.add(ce);
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    private static class ShallowECCollector implements DefaultShallowConditionalElementsLeafVisitor<ECLeaf>,
            DefaultFunctionWithArgumentsLeafVisitor<ECLeaf> {
        @Getter
        final Set<EquivalenceClass> equivalenceClasses = new HashSet<>();

        @Override
        public void visit(final InitialFactConditionalElement<ECLeaf> ce) {
            // nothing to do
        }

        @Override
        public void visit(final TemplatePatternConditionalElement<ECLeaf> ce) {
            // nothing to do
        }

        @Override
        public void visit(final TestConditionalElement<ECLeaf> ce) {
            ce.getPredicateWithArguments().accept(this);
        }

        @Override
        public void visit(final ECLeaf leaf) {
            this.equivalenceClasses.add(leaf.getEc());
        }

        @Override
        public void visit(final ConstantLeaf<ECLeaf> constantLeaf) {
        }

        @Override
        public void visit(final GlobalVariableLeaf<ECLeaf> globalVariableLeaf) {
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    private static class DeepECCollector
            implements DefaultConditionalElementsVisitor<ECLeaf>, DefaultFunctionWithArgumentsLeafVisitor<ECLeaf> {
        @Getter
        final Set<EquivalenceClass> equivalenceClasses = new HashSet<>();

        @Override
        public void defaultAction(final ConditionalElement<ECLeaf> ce) {
            ce.getChildren().forEach(c -> c.accept(this));
        }

        @Override
        public void visit(final InitialFactConditionalElement<ECLeaf> ce) {
            // nothing to do
        }

        @Override
        public void visit(final TemplatePatternConditionalElement<ECLeaf> ce) {
            // nothing to do
        }

        @Override
        public void visit(final TestConditionalElement<ECLeaf> ce) {
            ce.getPredicateWithArguments().accept(this);
        }

        @Override
        public void visit(final ECLeaf leaf) {
            this.equivalenceClasses.add(leaf.getEc());
        }

        @Override
        public void visit(final ConstantLeaf<ECLeaf> constantLeaf) {
        }

        @Override
        public void visit(final GlobalVariableLeaf<ECLeaf> globalVariableLeaf) {
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @RequiredArgsConstructor
    private static class CEECReplacer extends CETranslator<ECLeaf, ECLeaf> {
        final Map<EquivalenceClass, EquivalenceClass> oldToNewEC;
        final Map<SingleFactVariable, SingleFactVariable> oldToNewFV;

        @Override
        public void visit(final TestConditionalElement<ECLeaf> ce) {
            this.result = new TestConditionalElement<>((PredicateWithArguments<ECLeaf>) ce.getPredicateWithArguments()
                    .accept(new FWAECReplacer(this.oldToNewEC::get)).getFunctionWithArguments());
        }

        @Override
        public void visit(final TemplatePatternConditionalElement<ECLeaf> ce) {
            this.result = new TemplatePatternConditionalElement<>(this.oldToNewFV.get(ce.getFactVariable()));
        }

        @Override
        public void visit(final InitialFactConditionalElement<ECLeaf> ce) {
            this.result = new InitialFactConditionalElement<>(this.oldToNewFV.get(ce.getInitialFactVariable()));
        }

        @Override
        public CEECReplacer of() {
            return new CEECReplacer(this.oldToNewEC, this.oldToNewFV);
        }
    }

    /**
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @RequiredArgsConstructor
    public static class FWAECReplacer extends FWATranslator<ECLeaf, ECLeaf> {
        final Function<EquivalenceClass, EquivalenceClass> oldToNew;

        @Override
        public void visit(final ECLeaf leaf) {
            this.functionWithArguments = new ECLeaf(this.oldToNew.apply(leaf.getEc()));
        }

        @Override
        public FWAECReplacer of() {
            return new FWAECReplacer(this.oldToNew);
        }
    }
}
