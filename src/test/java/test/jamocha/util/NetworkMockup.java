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
package test.jamocha.util;

import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.jamocha.dn.ConflictResolutionStrategy;
import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ParserToNetwork;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.compiler.RuleCompiler;
import org.jamocha.dn.memory.*;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.TypedFilter;

import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class NetworkMockup implements ParserToNetwork, SideEffectFunctionToNetwork {

    Map<String, Template> templates = new HashMap<>();
    Map<String, Defrule> rules = new HashMap<>();
    final ScopeStack scope = new ScopeStack();
    final Template initialFactTemplate;

    @Getter(onMethod = @__(@Override))
    final EnumMap<SlotType, Object> defaultValues = new EnumMap<>(SlotType.class);

    public NetworkMockup() {
        this.initialFactTemplate = defTemplate("initial-fact", "");
        defFacts("initial-fact", "", Arrays.asList(new TemplateContainer<>(initialFactTemplate)));

        {
            final Template dummyFact = this.defTemplate("dummy-fact", "used as default value for FACT-ADDRESS");
            @SuppressWarnings("unchecked")
            final FactIdentifier dummyFactIdentifier = new org.jamocha.function.fwa.Assert<>(this,
                    new TemplateContainer[]{new TemplateContainer<>(dummyFact)}).evaluate();
            for (final SlotType type : EnumSet.allOf(SlotType.class)) {
                switch (type) {
                case BOOLEAN:
                    defaultValues.put(type, Boolean.FALSE);
                    break;
                case DATETIME:
                    defaultValues.put(type, ZonedDateTime.now());
                    break;
                case DOUBLE:
                    defaultValues.put(type, Double.valueOf(0.0));
                    break;
                case LONG:
                    defaultValues.put(type, Long.valueOf(0));
                    break;
                case NIL:
                    defaultValues.put(type, null);
                    break;
                case STRING:
                    defaultValues.put(type, "");
                    break;
                case SYMBOL:
                    defaultValues.put(type, this.getScope().getOrCreateSymbol("nil"));
                    break;
                case FACTADDRESS:
                    defaultValues.put(type, dummyFactIdentifier);
                    break;
                case BOOLEANS:
                case DATETIMES:
                case DOUBLES:
                case FACTADDRESSES:
                case LONGS:
                case NILS:
                case STRINGS:
                case SYMBOLS:
                    defaultValues.put(type, Array.newInstance(type.getJavaClass(), 0));
                    break;
                }
            }
        }
    }

    @Override
    public Template defTemplate(final String name, final String description, final Slot... slots) {
        final TemplateMockup temp = new TemplateMockup(name, description, Arrays.asList(slots));
        templates.put(name, temp);
        return temp;
    }

    @Override
    public Template getTemplate(final String name) {
        return templates.get(name);
    }

    @Override
    public Collection<Template> getTemplates() {
        return templates.values();
    }

    @Override
    public void defRules(final List<Defrule> defrules) {
        for (final Defrule defrule : defrules) {
            rules.put(defrule.getName(), defrule);
        }
    }

    @Override
    public Defrule getRule(final String name) {
        return rules.get(name);
    }

    @Override
    public Collection<Defrule> getRules() {
        return rules.values();
    }

    @Override
    public Deffacts defFacts(final String name, final String description,
            final List<TemplateContainer<ParameterLeaf>> containers) {
        return null;
    }

    @Override
    public Deffacts getDeffacts(final String name) {
        return null;
    }

    @Override
    public Collection<Deffacts> getDeffacts() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public void clear() {
        this.templates.clear();
        this.rules.clear();
    }

    @Override
    public Map<FactIdentifier, MemoryFact> getMemoryFacts() {
        return null;
    }

    @Override
    public MemoryFact getMemoryFact(final FactIdentifier id) {
        return null;
    }

    @Override
    public FactIdentifier[] assertFacts(final Fact... array) {
        return new FactIdentifier[]{null};
    }

    @Override
    public void retractFacts(final FactIdentifier... array) {
    }

    @Override
    public LogFormatter getLogFormatter() {
        return null;
    }

    @Override
    public Logger getInteractiveEventsLogger() {
        return null;
    }

    @Override
    public TypedFilter getTypedFilter() {
        return null;
    }

    @Override
    public Template getInitialFactTemplate() {
        return initialFactTemplate;
    }

    @Override
    public ScopeStack getScope() {
        return scope;
    }

    @Override
    public void run(final long maxNumRules) {
    }

    @Override
    public void halt() {
    }

    @Override
    public Set<TerminalNode> getTerminalNodes() {
        return null;
    }

    @Override
    public ConflictResolutionStrategy getConflictResolutionStrategy() {
        return null;
    }

    @Override
    public void setConflictResolutionStrategy(final ConflictResolutionStrategy conflictResolutionStrategy) {
    }

    @Override
    public Symbol createTopLevelSymbol(final String image) {
        return null;
    }

    @Override
    public boolean loadFromFile(final String path, final boolean progressInformation) {
        return false;
    }

    @Override
    public boolean saveToFile(final String path) {
        return false;
    }

    @Override
    public boolean loadFactsFromFile(final String path) {
        return false;
    }

    @Override
    public boolean saveFactsToFile(final String path) {
        return false;
    }

    @Override
    public void setRuleCompiler(final RuleCompiler ruleCompiler) {
    }
}
