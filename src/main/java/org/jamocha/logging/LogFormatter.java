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
package org.jamocha.logging;

import org.jamocha.dn.ConstructCache.Defrule.Translated;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface LogFormatter {

    void messageFactDetails(final SideEffectFunctionToNetwork network, final int id, final MemoryFact value);

    void messageFactList(final SideEffectFunctionToNetwork network);

    void messageTemplateDetails(final SideEffectFunctionToNetwork network, final Template template);

    void messageTemplateList(final SideEffectFunctionToNetwork network);

    void messageRuleList(final SideEffectFunctionToNetwork network);

    void messageFactAssertions(final SideEffectFunctionToNetwork network, final FactIdentifier[] assertedFacts);

    void messageFactRetractions(final SideEffectFunctionToNetwork network, final FactIdentifier[] factsToRetract);

    void messageRuleActivation(final SideEffectFunctionToNetwork network, final Translated translated,
            final Assert plus);

    void messageRuleDeactivation(final SideEffectFunctionToNetwork network, final Translated rule, final Retract minus);

    void messageRuleFiring(final SideEffectFunctionToNetwork network, final Translated translated, final Assert plus);

    void messageArgumentTypeMismatch(final SideEffectFunctionToNetwork network, final String function,
            final int paramIndex, final Type expected);

    void messageUnknownSymbol(final SideEffectFunctionToNetwork network, final Type expectedType, final String name);

    String formatTemplate(final Template template);

    String formatFact(final Fact fact);

    String formatSlotType(final SlotType type);

    String formatSlotValue(final SlotType type, final Object value);

    String formatSlotValue(final SlotType type, final Object value, final boolean quoteString);

    String formatType(final Type type);

    String formatTypeValue(final Type type, final Object value);

}
