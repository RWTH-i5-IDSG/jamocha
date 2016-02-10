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

import org.apache.logging.log4j.Logger;
import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.compiler.RuleCompiler;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.TypedFilter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface SideEffectFunctionToNetwork {
    Map<FactIdentifier, MemoryFact> getMemoryFacts();

    MemoryFact getMemoryFact(final FactIdentifier id);

    Template getTemplate(final String name);

    Collection<Template> getTemplates();

    Defrule getRule(final String name);

    Collection<Defrule> getRules();

    Deffacts getDeffacts(final String name);

    Collection<Deffacts> getDeffacts();

    FactIdentifier[] assertFacts(final Fact... array);

    void retractFacts(final FactIdentifier... array);

    LogFormatter getLogFormatter();

    Logger getInteractiveEventsLogger();

    TypedFilter getTypedFilter();

    void run(final long maxNumRules);

    void halt();

    void clear();

    void reset();

    Set<TerminalNode> getTerminalNodes();

    void setConflictResolutionStrategy(final ConflictResolutionStrategy conflictResolutionStrategy);

    ConflictResolutionStrategy getConflictResolutionStrategy();

    Symbol createTopLevelSymbol(final String image);

    boolean loadFromFile(final String path, final boolean progressInformation);

    boolean saveToFile(final String path);

    boolean loadFactsFromFile(final String path);

    boolean saveFactsToFile(final String path);

    void setRuleCompiler(final RuleCompiler ruleCompiler);
}
