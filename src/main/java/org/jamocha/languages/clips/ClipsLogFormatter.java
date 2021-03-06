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
package org.jamocha.languages.clips;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.Translated;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.Template.SlotConstraint;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.MarkerType;
import org.jamocha.logging.Type;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public final class ClipsLogFormatter implements LogFormatter {

    private static final ClipsLogFormatter SINGLETON = new ClipsLogFormatter();

    /**
     * Retrieves a singleton INSTANCE of the {@link org.jamocha.languages.clips.ClipsLogFormatter} implementation.
     *
     * @return a singleton INSTANCE of the {@link org.jamocha.languages.clips.ClipsLogFormatter} implementation
     */
    public static LogFormatter getMessageFormatter() {
        return SINGLETON;
    }

    private ClipsLogFormatter() {
    }

    @Override
    public void messageFactDetails(final SideEffectFunctionToNetwork network, final int id, final MemoryFact value) {
        final Logger interactiveEventsLogger = network.getInteractiveEventsLogger();
        if (interactiveEventsLogger.isInfoEnabled()) {
            interactiveEventsLogger.info("f-{}\t{}", id, formatFact(value.toMutableFact()));
        }
    }

    @Override
    public void messageFactList(final SideEffectFunctionToNetwork network) {
        network.getMemoryFacts().entrySet().stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())).forEachOrdered(
                e -> network.getLogFormatter().messageFactDetails(network, e.getKey().getId(), e.getValue()));
        network.getInteractiveEventsLogger().info("For a total of {} facts.", network.getMemoryFacts().size());
    }

    @Override
    public void messageTemplateDetails(final SideEffectFunctionToNetwork network, final Template template) {
        network.getInteractiveEventsLogger().info(formatTemplate(template));
    }

    @Override
    public void messageTemplateList(final SideEffectFunctionToNetwork network) {
        final Collection<Template> templates = network.getTemplates();
        for (final Template template : templates) {
            network.getInteractiveEventsLogger().info(template.getName());
        }
        network.getInteractiveEventsLogger().info("For a total of {} deftemplates.", templates.size());
    }

    @Override
    public void messageRuleList(final SideEffectFunctionToNetwork network) {
        final Collection<Defrule> rules = network.getRules();
        for (final Defrule rule : rules) {
            network.getInteractiveEventsLogger().info(rule.getName());
        }
        network.getInteractiveEventsLogger().info("For a total of {} defrules.", rules.size());
    }

    @Override
    public void messageFactAssertions(final SideEffectFunctionToNetwork network, final FactIdentifier[] assertedFacts) {
        if (!network.getTypedFilter().isRelevant(MarkerType.FACTS)) {
            return;
        }
        final Logger interactiveEventsLogger = network.getInteractiveEventsLogger();
        for (final FactIdentifier fi : assertedFacts) {
            final MemoryFact memoryFact = network.getMemoryFact(fi);
            if (null == memoryFact) {
                continue;
            }
            interactiveEventsLogger.info(memoryFact.getTemplate().getInstanceMarker(), "==> f-{}\t{}", fi.getId(),
                    formatFact(memoryFact.toMutableFact()));
        }
    }

    @Override
    public void messageFactRetractions(final SideEffectFunctionToNetwork network,
            final FactIdentifier[] factsToRetract) {
        final Logger interactiveEventsLogger = network.getInteractiveEventsLogger();
        for (final FactIdentifier fi : factsToRetract) {
            final MemoryFact memoryFact = network.getMemoryFact(fi);
            if (null == memoryFact) {
                messageUnknownSymbol(network, Type.FACT, formatTypeValue(Type.FACT, fi));
                continue;
            }
            final Marker instanceMarker = memoryFact.getTemplate().getInstanceMarker();
            if (interactiveEventsLogger.isInfoEnabled(instanceMarker)) {
                interactiveEventsLogger
                        .info(instanceMarker, "<== f-{}\t{}", fi.getId(), formatFact(memoryFact.toMutableFact()));
            }
        }
    }

    @Override
    public void messageRuleActivation(final SideEffectFunctionToNetwork network, final Translated translated,
            final Assert plus) {
        if (!network.getTypedFilter().isRelevant(MarkerType.ACTIVATIONS)) {
            return;
        }
        network.getInteractiveEventsLogger()
                .info(translated.getParent().getActivationMarker(), "==> Activation\t{}: {}",
                        translated.getParent().getName(), formatFactIdentifierArray(plus));
    }

    @Override
    public void messageRuleDeactivation(final SideEffectFunctionToNetwork network, final Translated translated,
            final Retract minus) {
        if (!network.getTypedFilter().isRelevant(MarkerType.ACTIVATIONS)) {
            return;
        }
        network.getInteractiveEventsLogger()
                .info(translated.getParent().getActivationMarker(), "==> Deactivation\t{}: {}",
                        translated.getParent().getName(), formatFactIdentifierArray(minus));
    }

    @Override
    public void messageRuleFiring(final SideEffectFunctionToNetwork network, final Translated translated,
            final Assert plus) {
        if (!network.getTypedFilter().isRelevant(MarkerType.RULES)) {
            return;
        }
        network.getInteractiveEventsLogger()
                .info(translated.getParent().getFireMarker(), "FIRE {} : {}", translated.getParent().getName(),
                        formatFactIdentifierArray(plus));
    }

    protected String formatFactIdentifierArray(final AssertOrRetract<?> token) {
        return Arrays.stream(token.getFactIdentifiers()).map(fi -> null == fi ? "*" : formatTypeValue(Type.FACT, fi))
                .collect(joining(","));
    }

    @Override
    public void messageArgumentTypeMismatch(final SideEffectFunctionToNetwork network, final String function,
            final int paramIndex, final Type expectedType) {
        network.getInteractiveEventsLogger()
                .error("[ARGACCES5] Function {} expected argument #{} to be of type {}", function, paramIndex,
                        formatType(expectedType));
    }

    @Override
    public void messageUnknownSymbol(final SideEffectFunctionToNetwork network, final Type expectedType,
            final String name) {
        network.getInteractiveEventsLogger().error("[PRNTUTIL1] Unable to find {} {}", formatType(expectedType), name);
    }

    @Override
    public String formatTemplate(final Template template) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(deftemplate ").append(template.getName());
        final String description = template.getDescription();
        if (null != description && !description.isEmpty()) {
            sb.append(' ').append(formatSlotValue(SlotType.STRING, description));
        }
        for (final Slot slot : template.getSlots()) {
            sb.append("\n\t(slot ").append(slot.getName()).append(" (type ").append(slot.getSlotType().toString())
                    .append(')');
            for (final SlotConstraint slotConstraint : slot.getSlotConstraints()) {
                final List<Object> interestingValues = slotConstraint.getInterestingValues();
                switch (slotConstraint.getConstraintType()) {
                case ALLOWED_CONSTANTS:
                    sb.append(" (allowed-");
                    switch (slot.getSlotType()) {
                    case DOUBLE:
                    case DOUBLES:
                        sb.append("floats");
                        break;
                    case LONG:
                    case LONGS:
                        sb.append("integers");
                        break;
                    case STRING:
                    case STRINGS:
                        sb.append("strings");
                        break;
                    case SYMBOL:
                    case SYMBOLS:
                        sb.append("symbols");
                        break;
                    default:
                        break;
                    }
                    sb.append(' ').append(interestingValues.stream().map(Object::toString).collect(joining(" ")))
                            .append(')');
                    break;
                case CARDINALITY:
                    sb.append(" (cardinality ").append(interestingValues.get(0)).append(' ')
                            .append(interestingValues.get(1)).append(')');
                    break;
                case RANGE:
                    sb.append(" (range ").append(interestingValues.get(0)).append(' ').append(interestingValues.get(1))
                            .append(')');
                    break;
                }
            }
            sb.append(')');
        }
        sb.append(")\n");
        return sb.toString();
    }

    @Override
    public String formatFact(final Fact fact) {
        final Template template = fact.getTemplate();
        final StringBuilder builder = new StringBuilder();
        builder.append('(').append(template.getName());
        template.getSlots().stream().forEach(s -> builder.append(" (").append(s.getName()).append(' ')
                .append(formatSlotValue(s.getSlotType(), template.getValue(fact, template.getSlotAddress(s.getName()))))
                .append(')'));
        return builder.append(')').toString();
    }

    @Override
    public String formatSlotType(final SlotType type) {
        switch (type) {
        case LONG:
            return "INTEGER";
        case DOUBLE:
            return "FLOAT";
        case FACTADDRESS:
            return "FACT-ADDRESS";
        case BOOLEAN:
            return "BOOLEAN";
        case DATETIME:
            return "DATETIME";
        case NIL:
            return "";
        case STRING:
            return "STRING";
        case SYMBOL:
            return "SYMBOL";
        case BOOLEANS:
        case DATETIMES:
        case DOUBLES:
        case FACTADDRESSES:
        case LONGS:
        case NILS:
        case STRINGS:
        case SYMBOLS:
            return formatSlotType(SlotType.arrayToSingle(type)) + "[]";
        }
        return type.name();
    }

    @Override
    public String formatSlotValue(final SlotType type, final Object value) {
        return formatSlotValue(type, value, true);
    }

    @Override
    public String formatSlotValue(final SlotType type, final Object value, final boolean quoteString) {
        if (type.isArrayType()) {
            return "[" + Arrays.stream((Object[]) value)
                    .map(v -> formatSlotValue(SlotType.arrayToSingle(type), v, quoteString)).collect(joining(", "))
                    + "]";
        }
        switch (type) {
        case STRING:
            if (quoteString) return "\"" + Objects.toString(value) + "\"";
            return Objects.toString(value);
        case FACTADDRESS:
            return (null == value) ? "FALSE" : "<Fact-" + ((FactIdentifier) value).getId() + ">";
        case NIL:
            return null;
        default:
            return Objects.toString(value);
        }
    }

    @Override
    public String formatType(final Type type) {
        switch (type) {
        case LONG:
        case DOUBLE:
        case FACTADDRESS:
        case BOOLEAN:
        case DATETIME:
        case NIL:
        case STRING:
        case SYMBOL:
            return formatSlotType(Enum.valueOf(SlotType.class, type.name()));
        case TEMPLATE:
            return "deftemplate";
        case WATCHABLE_SYMBOL:
            return "watchable symbol";
        case FACT:
            return "fact";
        case CONFLICT_RESOLUTION_STRATEGY:
            return "conflict resolution strategy";
        }
        return null;
    }

    @Override
    public String formatTypeValue(final Type type, final Object value) {
        switch (type) {
        case LONG:
        case DOUBLE:
        case FACTADDRESS:
        case BOOLEAN:
        case DATETIME:
        case NIL:
        case STRING:
        case SYMBOL:
            return formatSlotValue(Enum.valueOf(SlotType.class, type.name()), value);
        case TEMPLATE:
            return ((Template) value).getName();
        case WATCHABLE_SYMBOL:
            return ((Symbol) value).getImage();
        case FACT:
            return "f-" + ((FactIdentifier) value).getId();
        case CONFLICT_RESOLUTION_STRATEGY:
            return formatTypeValue(Type.SYMBOL, value);
        }
        return null;
    }
}
