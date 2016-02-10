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
package org.jamocha.dn;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Marker;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.*;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwatransformer.FWASymbolToRHSVariableLeafTranslator;
import org.jamocha.languages.common.*;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.logging.MarkerType;

import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.toIdentityHashSet;
import static org.jamocha.util.ToArray.toArray;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ConstructCache {
    private final HashMap<String, Template> template = new HashMap<>();
    private final HashMap<String, Function<?>> functions = new HashMap<>();
    private final HashMap<String, Defrule> rules = new HashMap<>();
    private final HashMap<String, Deffacts> deffacts = new HashMap<>();

    @Value
    public static class Deffacts {
        final String name;
        final String description;
        final List<Assert.TemplateContainer<ParameterLeaf>> containers;
    }

    @Value
    public static class Defrule {
        final String name;
        final String description;
        final int salience;
        final RuleCondition condition;
        final FunctionWithArguments<SymbolLeaf>[] actionList;
        final Marker fireMarker;
        final Marker activationMarker;

        public Defrule(final String name, final String description, final int salience, final RuleCondition condition,
                final ArrayList<FunctionWithArguments<SymbolLeaf>> actionList) {
            this(name, description, salience, condition, toArray(actionList, FunctionWithArguments[]::new));
        }

        public Defrule(final String name, final String description, final int salience, final RuleCondition condition,
                final FunctionWithArguments<SymbolLeaf>[] actionList) {
            this.name = name;
            this.description = description;
            this.salience = salience;
            this.condition = condition;
            this.actionList = actionList;
            this.fireMarker = MarkerType.RULES.createChild(name);
            this.activationMarker = MarkerType.ACTIVATIONS.createChild(name);
        }

        @RequiredArgsConstructor
        @Getter
        public class ECBasedCERule {
            final ConditionalElement<ECLeaf> condition;
            final Set<SingleFactVariable> factVariables;
            final Set<EquivalenceClass> equivalenceClasses;
            final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs;

            public List<ECBasedCERule> newECBasedCERules() {
                final ConditionalElement<ECLeaf> ecCE = RuleConditionProcessor.flattenInPlace(this.condition);
                // simply translate SymbolLeafs to ECLeafs by calling getEC
                assert ecCE instanceof ConditionalElement.OrFunctionConditionalElement;
                return ecCE.getChildren().stream()
                        .map(child -> toECBasedCERule(child, this.equivalenceClasses, this.localECsToConditionECs, null,
                                CONCATENATE_BI_MAPS)).collect(toList());
            }

            public Defrule getParent() {
                return Defrule.this;
            }

            public ECSetRule newECSetRule(final Set<ECFilterSet> filters, final Set<SingleFactVariable> factVariables,
                    final Set<EquivalenceClass> equivalenceClasses, final int specificity) {
                return new ECSetRule(filters, factVariables, equivalenceClasses, this.localECsToConditionECs,
                        specificity);
            }
        }

        public List<ECBasedCERule> newECBasedCERules() {
            final RuleCondition condition = Defrule.this.getCondition();
            final ConditionalElement<SymbolLeaf> symbolCE = RuleConditionProcessor.flattenInPlace(condition);
            // all ECs of variable symbols are found already, which means all slot and fact variables
            final Set<EquivalenceClass> allECs =
                    Defrule.this.condition.getVariableSymbols().stream().map(ScopeStack.VariableSymbol::getEqual)
                            .collect(toIdentityHashSet());
            // translate SymbolLeafs to ECLeafs by calling getEC, convert ConstantLeafs to ECLeafs by creating an
            // equivalence class containing it (re-using the ECs for all occurrences of the constant in the same scope
            // and saving them into the HashMap provided).
            final HashMap<Pair<ScopeStack.Scope, ConstantLeaf<SymbolLeaf>>, EquivalenceClass> constantToEC =
                    new HashMap<>();
            final ConditionalElement<ECLeaf> ecCE = symbolCE.accept(
                    new RuleConditionProcessor.CESymbolToECTranslator(Defrule.this.getCondition().getScope(),
                            constantToEC)).getResult();
            // we add all ECs created during this step to the set of equivalence classes
            allECs.addAll(constantToEC.values());
            assert ecCE instanceof ConditionalElement.OrFunctionConditionalElement;
            return ecCE.getChildren().stream()
                    .map(child -> toECBasedCERule(child, allECs, null, condition, SPLIT_AND_TRANSFORM_EXISTENTIALS))
                    .flatMap(rule -> rule.newECBasedCERules().stream()).collect(toList());
        }

        private ECBasedCERule toECBasedCERule(final ConditionalElement<ECLeaf> child,
                final Collection<EquivalenceClass> oldECs,
                final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs, final RuleCondition condition,
                final FunctionalInjection functionalInjection) {
            // using those copies, create a copy of the conditional element creating new fact variables on the way
            // remove all bindings not present in this disjunct of the rule
            // collect all fact variables
            final RuleConditionProcessor.CopyWithMetaInformation copyWithMetaInformation =
                    RuleConditionProcessor.copyDeeplyUsingNewECsAndFactVariables(child, oldECs);
            ConditionalElement<ECLeaf> copy = copyWithMetaInformation.getCopy();
            final Set<SingleFactVariable> factVariables = copyWithMetaInformation.getOldToNewFV().values();
            final HashBiMap<EquivalenceClass, EquivalenceClass> oldToNewEC = copyWithMetaInformation.getOldToNewEC();
            copy = functionalInjection.apply(copy, condition, oldToNewEC, localECsToConditionECs);
            return new ECBasedCERule(copy, factVariables, oldToNewEC.values(), oldToNewEC.inverse());
        }

        @FunctionalInterface
        public interface FunctionalInjection {
            ConditionalElement<ECLeaf> apply(final ConditionalElement<ECLeaf> ce, final RuleCondition condition,
                    final HashBiMap<EquivalenceClass, EquivalenceClass> oldToNewEC,
                    final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs);
        }

        private static final FunctionalInjection CONCATENATE_BI_MAPS =
                (final ConditionalElement<ECLeaf> ce, final RuleCondition condition, final
                HashBiMap<EquivalenceClass, EquivalenceClass> oldToNewEC, final BiMap<EquivalenceClass,
                        EquivalenceClass> localECsToConditionECs) -> {
                    // replaceAll seems to be unsupported by HashBiMap for now, use a work-around
                    final Map<EquivalenceClass, EquivalenceClass> map = Maps.newHashMap(
                            Maps.asMap(oldToNewEC.inverse().keySet(),
                                    k -> localECsToConditionECs.get(oldToNewEC.inverse().get(k))));
                    oldToNewEC.inverse().putAll(map);
                    // oldToNewEC.inverse().replaceAll((newEC, oldEC) -> localECsToConditionECs.get(oldEC));
                    return ce;
                };
        private static final FunctionalInjection SPLIT_AND_TRANSFORM_EXISTENTIALS =
                (final ConditionalElement<ECLeaf> ce, final RuleCondition condition, final
                HashBiMap<EquivalenceClass, EquivalenceClass> oldToNew, final BiMap<EquivalenceClass,
                        EquivalenceClass> localECsToConditionECs) -> {
                    ConditionalElement<ECLeaf> copy = ce;
                    // split up the equivalence classes on the existential thresholds
                    // this should leave alive the equivalence classes containing constants only
                    final Pair<ConditionalElement<ECLeaf>, Set<EquivalenceClass>> pair =
                            RuleConditionProcessor.ExistentialECSplitter.split(condition.getScope(), copy);
                    pair.getRight().forEach(ec -> oldToNew.put(ec, ec));
                    copy = pair.getLeft();

                    // move functions not using any existential EC(-part)s out of the existential part
                    // (producing (or)s in case of negated existential conditions)
                    // copy = copy.accept(new RuleConditionProcessor.CEExistentialTransformer()).getCe();
                    return copy;
                };

        public ECSetRule newECSetRule(final Set<ECFilterSet> condition, final Set<SingleFactVariable> factVariables,
                final Set<EquivalenceClass> equivalenceClasses,
                final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs, final int specificity) {
            assert !factVariables.contains(null);
            return new ECSetRule(condition, factVariables, equivalenceClasses, localECsToConditionECs, specificity);
        }

        @Data
        // @RequiredArgsConstructor
        public class ECSetRule {
            final Set<ECFilterSet> condition;
            final Set<SingleFactVariable> factVariables;
            final Set<EquivalenceClass> equivalenceClasses;
            final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs;
            final int specificity;

            public Defrule getParent() {
                return Defrule.this;
            }

            public PathRule toPathRule(final PathFilterList convertedCondition, final Set<Path> resultPaths,
                    final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
                return new PathRule(convertedCondition, resultPaths, equivalenceClassToPathLeaf, this.specificity);
            }

            public ECListRule toECListRule(final ECFilterList condition,
                    final Set<SingleFactVariable> additionalInitialFactVariables) {
                return new ECListRule(condition, Sets.union(this.factVariables, additionalInitialFactVariables),
                        this.equivalenceClasses, this.localECsToConditionECs, this.specificity);
            }

            protected ECSetRule(final Set<ECFilterSet> condition, final Set<SingleFactVariable> factVariables,
                    final Set<EquivalenceClass> equivalenceClasses,
                    final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs, final int specificity) {
                this.condition = condition;
                assert !factVariables.isEmpty();
                this.factVariables = factVariables;
                this.equivalenceClasses = equivalenceClasses;
                this.localECsToConditionECs = localECsToConditionECs;
                this.specificity = specificity;
            }
        }

        @Data
        @RequiredArgsConstructor
        public class ECListRule {
            final ECFilterList condition;
            final Set<SingleFactVariable> factVariables;
            final Set<EquivalenceClass> equivalenceClasses;
            final BiMap<EquivalenceClass, EquivalenceClass> localECsToConditionECs;
            final int specificity;

            public Defrule getParent() {
                return Defrule.this;
            }

            public PathRule toPathRule(final PathFilterList convertedCondition, final Set<Path> resultPaths,
                    final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
                return new PathRule(convertedCondition, resultPaths, equivalenceClassToPathLeaf, this.specificity);
            }
        }

        public PathRule newTranslated(final PathFilterList condition, final Set<Path> resultPaths,
                final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf,
                final int specificity) {
            return new PathRule(condition, resultPaths, equivalenceClassToPathLeaf, specificity);
        }

        public PathRule newTranslated(final PathFilterList condition,
                final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
            return newTranslated(condition, PathCollector.newHashSet().collectOnlyInFilterLists(condition).getPaths(),
                    equivalenceClassToPathLeaf, (int) StreamSupport.stream(condition.spliterator(), false).count());
        }

        public PathRule newTranslated(final List<PathFilterList> condition,
                final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
            return newTranslated(PathFilterList.toSimpleList(condition), equivalenceClassToPathLeaf);
        }

        @Data
        @RequiredArgsConstructor
        public class PathSetRule {
            final Set<PathFilterSet> condition;
            final Set<Path> resultPaths;
            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf;
            final int specificity;

            public Defrule getParent() {
                return Defrule.this;
            }

            public PathRule trivialToPathRule() {
                return new PathRule(PathFilterList.toSimpleList(
                        this.condition.stream().map(TrivialPathSetToPathListConverter::convert).collect(toList())),
                        this.resultPaths, this.equivalenceClassToPathLeaf, this.specificity);
            }

            public PathRule toPathRule(final PathFilterList convertedCondition,
                    final Set<Path> additionalInitialPaths) {
                return new PathRule(convertedCondition, Sets.union(this.resultPaths, additionalInitialPaths),
                        this.equivalenceClassToPathLeaf, this.specificity);
            }
        }

        @Data
        @RequiredArgsConstructor
        public class PathRule {
            final PathFilterList condition;
            final Set<Path> resultPaths;
            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf;
            final int specificity;

            public Defrule getParent() {
                return Defrule.this;
            }

            public Translated translatePathToAddress() {
                final VariableValueContext context = new VariableValueContext();
                return new Translated(this.condition, new AddressesActionList(context,
                        FWASymbolToRHSVariableLeafTranslator
                                .translate(this.equivalenceClassToPathLeaf, context, Defrule.this.actionList)),
                        this.specificity);
            }
        }

        @Data
        public class Translated {
            final PathFilterList condition;
            final AddressesActionList actionList;
            final int specificity;

            public Defrule getParent() {
                return Defrule.this;
            }
        }
    }

    @Value
    public static class AddressesActionList {
        final VariableValueContext context;
        final FunctionWithArguments<RHSVariableLeaf>[] actions;

        public void evaluate(final AssertOrRetract<?> token) {
            this.context.initialize(token);
            for (final FunctionWithArguments<RHSVariableLeaf> action : this.actions) {
                action.evaluate();
            }
        }
    }

    public void addTemplate(final Template template) {
        this.template.put(template.getName(), template);
    }

    public Template getTemplate(final String name) {
        return this.template.get(name);
    }

    public Collection<Template> getTemplates() {
        return this.template.values();
    }

    public void addRule(final Defrule rule) {
        this.rules.put(rule.getName(), rule);
    }

    public Defrule getRule(final String name) {
        return this.rules.get(name);
    }

    public Collection<Defrule> getRules() {
        return this.rules.values();
    }

    public void addDeffacts(final Deffacts deffacts) {
        this.deffacts.put(deffacts.getName(), deffacts);
    }

    public Deffacts getDeffacts(final String name) {
        return this.deffacts.get(name);
    }

    public Collection<Deffacts> getDeffacts() {
        return this.deffacts.values();
    }

    public void clear() {
        this.deffacts.clear();
        this.functions.clear();
        this.rules.clear();
        this.template.clear();
    }
}
