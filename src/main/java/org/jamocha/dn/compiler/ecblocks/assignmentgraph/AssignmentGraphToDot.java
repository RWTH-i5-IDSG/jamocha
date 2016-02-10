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

package org.jamocha.dn.compiler.ecblocks.assignmentgraph;

import com.google.common.collect.HashBiMap;
import lombok.experimental.UtilityClass;
import org.jamocha.dn.compiler.ecblocks.ECOccurrenceLeaf;
import org.jamocha.dn.compiler.ecblocks.ExistentialInfo;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.*;
import org.jamocha.filter.ECFilter;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwatransformer.FWAECLeafToTypeLeafTranslator;
import org.jamocha.languages.common.SingleFactVariable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
public final class AssignmentGraphToDot {

    private static <T> String toString(final T element, final Function<T, String> toString,
            final HashBiMap<T, String> target) {
        final String old = target.get(element);
        if (null != old) return old;
        final String base = toString.apply(element) + "_";
        int i = 1;
        String str = base + i;
        while (target.containsValue(str)) {
            str = base + ++i;
        }
        target.put(element, str);
        return str;
    }

    private static String toString(final ECFilter f) {
        return FWAECLeafToTypeLeafTranslator.translate(f.getFunction()).toString();
    }

    private static String toString(final ECFilter filter, final HashBiMap<ECFilter, String> target) {
        return toString(filter, AssignmentGraphToDot::toString, target);
    }

    private static String toString(final ECOccurrenceNode occurrenceNode) {
        switch (occurrenceNode.getNodeType()) {
        case IMPLICIT_OCCURRENCE:
            return "o_i";
        case FILTER_OCCURRENCE:
            return "o:" + ((FilterOccurrenceNode) occurrenceNode).getParameterPosition();
        case FUNCTIONAL_OCCURRENCE:
            return "o:" + ((FunctionalExpressionOccurrenceNode) occurrenceNode).getParameterPosition();
        }
        return "o";
    }

    private static String toString(final ECOccurrenceNode occurrenceNode,
            final HashBiMap<ECOccurrenceNode, String> target) {
        return toString(occurrenceNode, AssignmentGraphToDot::toString, target);
    }

    private static String toString(final BindingNode bindingNode) {
        switch (bindingNode.getNodeType()) {
        case SLOT_OR_FACT_BINDING:
            return (bindingNode instanceof FactBindingNode) ? ((FactBindingNode) bindingNode).getGroupingFactVariable()
                    .getTemplate().getName()
                    : ((SlotBindingNode) bindingNode).getSlotInGroupingFactVariable().getSlotName();
        case CONSTANT_EXPRESSION:
            return Objects.toString(((ConstantBindingNode) bindingNode).getConstant().getValue());
        case FUNCTIONAL_EXPRESSION:
            return ((FunctionWithArgumentsComposite<ECOccurrenceLeaf>) ((FunctionalExpressionBindingNode) bindingNode)
                    .getFunctionalExpression()).getFunction().toString();
        }
        return "null";
    }

    private static String toString(final BindingNode bindingNode, final HashBiMap<BindingNode, String> target) {
        return toString(bindingNode, b -> '[' + toString(b) + ']', target);
    }

    private static String toString(final SingleFactVariable fv) {
        return fv.getTemplate().getName();
    }

    private static String toString(final SingleFactVariable factVariable,
            final HashBiMap<SingleFactVariable, String> target) {
        return toString(factVariable, AssignmentGraphToDot::toString, target);
    }

    private static StringBuffer makeEdge(final StringBuffer sb, final String rawSource, final String rawTarget) {
        sb.append('"').append(rawSource).append('"');
        sb.append("->");
        sb.append('"').append(rawTarget).append('"');
        return sb;
    }

    private static final String POSITIVE_EDGE_MARKUP = "[label=\"p\",style=\"dashed\"]";
    private static final String NEGATED_EDGE_MARKUP = "[label=\"n\",style=\"dashed\"]";
    private static final String IMPLICIT_EDGE_MARKUP = "[style=\"dotted\"]";

    public static String toDot(final AssignmentGraph assignmentGraph) {
        final HashBiMap<ECFilter, String> filterToString = HashBiMap.create();
        final HashBiMap<ECOccurrenceNode, String> occurrenceNodeToString = HashBiMap.create();
        final HashBiMap<BindingNode, String> bindingNodeToString = HashBiMap.create();
        final HashBiMap<SingleFactVariable, String> templateInstanceToString = HashBiMap.create();


        final StringBuffer sb = new StringBuffer();
        final String n = System.lineSeparator();
        sb.append("digraph network {").append(n).append(n);

        sb.append("// filter nodes").append(n).append("{ rank = same").append(n);
        for (final ECFilter filter : assignmentGraph.getFilterToOccurrenceNodes().keySet()) {
            sb.append('\t').append('"').append(toString(filter, filterToString)).append('"');
            sb.append("[label=\"").append(toString(filter)).append("\"]").append(n);
        }
        sb.append("}").append(n).append(n);

        sb.append("// occurrence nodes").append(n).append("{ rank = same").append(n);
        for (final ECOccurrenceNode occurrenceNode : assignmentGraph.getGraph().getECOccurrenceNodes()) {
            sb.append('\t').append('"').append(toString(occurrenceNode, occurrenceNodeToString)).append('"');
            sb.append("[label=\"").append(toString(occurrenceNode)).append("\"]").append(n);
        }
        sb.append("}").append(n).append(n);

        sb.append("// binding nodes").append(n).append("{ rank = same").append(n);
        for (final BindingNode bindingNode : assignmentGraph.getGraph().getBindingNodes()) {
            sb.append('\t').append('"').append(toString(bindingNode, bindingNodeToString)).append('"');
            sb.append("[label=\"").append(toString(bindingNode)).append("\"]").append(n);
        }
        sb.append("}").append(n).append(n);

        sb.append("// template instance nodes").append(n).append("{ rank = same").append(n);
        for (final SingleFactVariable templateInstance : assignmentGraph.getTemplateInstanceToBindingNodes().keySet()) {
            sb.append('\t').append('"').append(toString(templateInstance, templateInstanceToString)).append('"');
            sb.append("[label=\"").append(toString(templateInstance)).append("\"]").append(n);
        }
        sb.append("}").append(n).append(n);

        // edges between filters and filter occurrence nodes
        for (final Map.Entry<ExistentialInfo.FunctionWithExistentialInfo, Set<ECFilter>>
                functionWithExistentialInfoEntry : assignmentGraph
                .getPredicateToFilters().entrySet()) {
            final ExistentialInfo existentialInfo = functionWithExistentialInfoEntry.getKey().getExistentialInfo();
            final Set<Integer> existentialArguments =
                    existentialInfo.isExistential() ? IntStream.of(existentialInfo.getExistentialArguments()).boxed()
                            .collect(toSet()) : Collections.emptySet();
            for (final ECFilter filter : functionWithExistentialInfoEntry.getValue()) {
                for (final Map.Entry<Integer, FilterOccurrenceNode> entry : assignmentGraph.getFilterToOccurrenceNodes()
                        .get(filter).entrySet()) {
                    final Integer argument = entry.getKey();
                    final FilterOccurrenceNode occurrenceNode = entry.getValue();
                    makeEdge(sb, toString(filter, filterToString), toString(occurrenceNode, occurrenceNodeToString));
                    if (existentialArguments.contains(argument)) {
                        sb.append(existentialInfo.isPositive() ? POSITIVE_EDGE_MARKUP : NEGATED_EDGE_MARKUP);
                    }
                    sb.append(n);
                }
            }
        }
        // edges between functional expression bindings and occurrence nodes
        for (final Map.Entry<FunctionalExpressionBindingNode, TreeMap<Integer, FunctionalExpressionOccurrenceNode>>
                entry : assignmentGraph
                .getFunctionalExpressionBindingToOccurrenceNodes().entrySet()) {
            final FunctionalExpressionBindingNode functionalExpression = entry.getKey();
            for (final FunctionalExpressionOccurrenceNode occurrenceNode : entry.getValue().values()) {
                makeEdge(sb, toString(functionalExpression, bindingNodeToString),
                        toString(occurrenceNode, occurrenceNodeToString)).append(n);
            }
        }
        // edges between bindings and template instances
        for (final Map.Entry<SingleFactVariable, Set<SlotOrFactBindingNode>> entry : assignmentGraph
                .getTemplateInstanceToBindingNodes().entrySet()) {
            final SingleFactVariable templateInstance = entry.getKey();
            for (final SlotOrFactBindingNode bindingNode : entry.getValue()) {
                makeEdge(sb, toString(bindingNode, bindingNodeToString),
                        toString(templateInstance, templateInstanceToString)).append(n);
            }
        }
        // edges between occurrences and bindings
        for (final AssignmentGraph.Edge<ECOccurrenceNode, BindingNode> edge : assignmentGraph.getGraph().edgeSet()) {
            makeEdge(sb, toString(edge.getSource(), occurrenceNodeToString),
                    toString(edge.getTarget(), bindingNodeToString));
            if (edge.getSource().getNodeType() == OccurrenceType.IMPLICIT_OCCURRENCE
                    && ((ImplicitOccurrenceNode) edge.getSource()).getCorrespondingBindingNode() != edge.getTarget()) {
                sb.append(IMPLICIT_EDGE_MARKUP);
            }
            sb.append(n);
        }

        sb.append("}").append(n);

        return sb.toString();
    }

    public static void toDot(final AssignmentGraph assignmentGraph, final String fileName) {
        try (final FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(toDot(assignmentGraph));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
