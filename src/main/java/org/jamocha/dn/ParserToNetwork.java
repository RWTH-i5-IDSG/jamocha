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

import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.languages.common.ScopeStack;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ParserToNetwork {
    Template defTemplate(final String name, final String description, final Slot... slots);

    Template getTemplate(final String name);

    Collection<Template> getTemplates();

    void defRules(final List<Defrule> defrules);

    Defrule getRule(final String name);

    Collection<Defrule> getRules();

    Deffacts defFacts(final String name, final String description,
            final List<Assert.TemplateContainer<ParameterLeaf>> containers);

    Deffacts getDeffacts(final String name);

    Collection<Deffacts> getDeffacts();

    ScopeStack getScope();

    Template getInitialFactTemplate();

    EnumMap<SlotType, Object> getDefaultValues();
}
