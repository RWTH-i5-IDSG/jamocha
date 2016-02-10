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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.*;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.function.fwa.*;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.Modify.SlotAndValue;

import java.util.*;

public class NetworkToDot {

    private final Map<Node, List<FactAddress>> node2AddressArrays = new HashMap<>();
    private final Map<AlphaNode, String> alphaNodes = new HashMap<>();
    private final Map<ObjectTypeNode, String> otns = new HashMap<>();
    private final Map<BetaNode, String> betaNodes = new HashMap<>();
    private final Map<TerminalNode, String> terminalNodes = new HashMap<>();
    private final String rootNode = "root";
    private final List<String> edges = new ArrayList<>();

    private void generateEdge(final String from, final String to, final String label) {
        this.edges.add('"' + from + "\" -> \"" + to + '"' + (null == label ? "" : " [label=\"" + label + "\"]"));
    }

    @RequiredArgsConstructor
    private static class FWAFormatter implements FunctionWithArgumentsVisitor<ParameterLeaf> {

        private final String[] params;

        @Getter
        private final StringBuffer sb = new StringBuffer();

        private static String escape(final String string) {
            return string.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }

        private void visitComposite(final GenericWithArgumentsComposite<?, ?, ParameterLeaf> gwac) {
            this.sb.append("(" + escape(gwac.getFunction().inClips()));
            int pos = 0;
            for (final FunctionWithArguments<ParameterLeaf> functionWithArguments : gwac.getArgs()) {
                final int width = functionWithArguments.getParamTypes().length;
                this.sb.append(" ");
                this.sb.append(functionWithArguments
                        .accept(new FWAFormatter(Arrays.copyOfRange(this.params, pos, pos + width))).getSb());
                pos += width;
            }
            this.sb.append(")");
        }

        @Override
        public void visit(final FunctionWithArgumentsComposite<ParameterLeaf> functionWithArgumentsComposite) {
            visitComposite(functionWithArgumentsComposite);
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<ParameterLeaf> predicateWithArgumentsComposite) {
            visitComposite(predicateWithArgumentsComposite);
        }

        @Override
        public void visit(final ConstantLeaf<ParameterLeaf> constantLeaf) {
            this.sb.append(constantLeaf.toString());
        }

        @Override
        public void visit(final GlobalVariableLeaf<ParameterLeaf> globalVariableLeaf) {
            this.sb.append(globalVariableLeaf.toString());
        }

        @Override
        public void visit(final ParameterLeaf leaf) {
            assert this.params.length == 1;
            this.sb.append(this.params[0]);
        }

        @Override
        public void visit(final Bind<ParameterLeaf> fwa) {
            throw new RuntimeException("There should not be a bind inside a rule condition!");
        }

        @Override
        public void visit(final Assert<ParameterLeaf> fwa) {
            throw new RuntimeException("There should not be an assert inside a rule condition!");
        }

        @Override
        public void visit(final TemplateContainer<ParameterLeaf> fwa) {
            throw new RuntimeException("There should not be a TemplateContainer inside a rule condition!");
        }

        @Override
        public void visit(final Retract<ParameterLeaf> fwa) {
            throw new RuntimeException("There should not be a retract inside a rule condition!");
        }

        @Override
        public void visit(final Modify<ParameterLeaf> fwa) {
            throw new RuntimeException("There should not be a modify inside a rule condition!");
        }

        @Override
        public void visit(final SlotAndValue<ParameterLeaf> fwa) {
            throw new RuntimeException("There should not be a SlotAndValue inside a rule condition!");
        }

    }

    private static class NameNodes implements NodeVisitor {

        private final Map<Node, String> nodeNames = new HashMap<>();

        private String lastName = null;

        private int alphaCounter = 0;
        private int betaCounter = 0;

        String getNodeName(final Node node) {
            final String name = this.nodeNames.get(node);
            if (null != name) return name;
            final String newName = node.accept(this).lastName;
            this.nodeNames.put(node, newName);
            return newName;
        }

        @Override
        public void visit(final AlphaNode node) {
            this.lastName = "Alpha" + this.alphaCounter++;
        }

        @Override
        public void visit(final BetaNode node) {
            this.lastName = "Beta" + this.betaCounter++;
        }

        @Override
        public void visit(final ObjectTypeNode node) {
            this.lastName = node.getTemplate().getName();
        }

    }

    @RequiredArgsConstructor
    private class GraphConstructingNodeVisitor implements NodeVisitor {

        private final List<String> names;
        private final List<Template> templates;
        private final List<FactAddress> addresses;

        private String[] translateAddresses2Names(final SlotInFactAddress[] sifa) {
            final String[] fullNames = new String[sifa.length];
            for (int i = 0; i < sifa.length; i++) {
                final SlotInFactAddress slotInFactAddress = sifa[i];
                final FactAddress factAddress = slotInFactAddress.getFactAddress();
                for (int j = 0; j < this.addresses.size(); j++) {
                    final FactAddress address = this.addresses.get(j);
                    if (address != factAddress) continue;
                    fullNames[i] = this.names.get(j) + (slotInFactAddress.getSlotAddress() == null ? ""
                            : "." + this.templates.get(j).getSlotName(slotInFactAddress.getSlotAddress()));
                    break;
                }
            }
            return fullNames;
        }

        @Override
        public void visit(final AlphaNode node) {
            if (NetworkToDot.this.alphaNodes.containsKey(node)) {
                assert NetworkToDot.this.node2AddressArrays.containsKey(node);
                final List<FactAddress> list = NetworkToDot.this.node2AddressArrays.get(node);
                assert list.size() == this.addresses.size();
                final Iterator<FactAddress> i = list.iterator();
                this.addresses.replaceAll(a -> i.next());
                return;
            }
            final String targetNodeName = getNodeName(node);
            assert this.names.size() == 1;
            final Edge[] incomingEdges = node.getIncomingEdges();
            assert incomingEdges.length == 1;
            final Edge edge = incomingEdges[0];
            final Node sourceNode = edge.getSourceNode();
            final String sourceNodeName = getNodeName(sourceNode);
            generateEdge(sourceNodeName, targetNodeName, "(" + this.names.get(0) + ")");
            sourceNode.accept(new GraphConstructingNodeVisitor(this.names, this.templates, this.addresses));
            this.addresses.replaceAll(edge::localizeAddress);

            final StringBuilder targetNodeLabel = new StringBuilder();
            for (final AddressFilter addressFilterElement : node.getFilter().getFilters()) {
                final String[] fullNames = translateAddresses2Names(addressFilterElement.getAddressesInTarget());
                String formatted =
                        addressFilterElement.getFunction().accept(new FWAFormatter(fullNames)).getSb().toString();
                if (formatted.equals("(TRUE)")) formatted = String.join(" x ", fullNames);
                if (targetNodeLabel.length() != 0) targetNodeLabel.append("<br/>");
                targetNodeLabel.append(formatted);
            }
            NetworkToDot.this.alphaNodes
                    .put(node, '"' + targetNodeName + "\" [label=<" + targetNodeLabel.toString() + ">]");
            NetworkToDot.this.node2AddressArrays.put(node, new ArrayList<>(this.addresses));
        }

        @Override
        public void visit(final BetaNode node) {
            if (NetworkToDot.this.betaNodes.containsKey(node)) {
                assert NetworkToDot.this.node2AddressArrays.containsKey(node);
                final List<FactAddress> list = NetworkToDot.this.node2AddressArrays.get(node);
                assert list.size() == this.addresses.size();
                final Iterator<FactAddress> i = list.iterator();
                this.addresses.replaceAll(a -> i.next());
                return;
            }
            final String targetNodeName = getNodeName(node);
            int pos = 0;
            for (final Edge edge : node.getIncomingEdges()) {
                final Node sourceNode = edge.getSourceNode();
                final int width = sourceNode.getMemory().getTemplate().length;
                final String sourceNodeName = getNodeName(sourceNode);
                final List<String> namesPart = this.names.subList(pos, pos + width);
                final List<Template> templatesPart = this.templates.subList(pos, pos + width);
                final List<FactAddress> addressesPart = this.addresses.subList(pos, pos + width);
                sourceNode.accept(new GraphConstructingNodeVisitor(namesPart, templatesPart, addressesPart));
                generateEdge(sourceNodeName, targetNodeName, "(" + String.join(", ", namesPart) + ")");
                addressesPart.replaceAll(edge::localizeAddress);
                pos += width;
            }
            final StringBuilder targetNodeLabel = new StringBuilder();
            for (final AddressFilter addressFilterElement : node.getFilter().getFilters()) {
                final String[] fullNames = translateAddresses2Names(addressFilterElement.getAddressesInTarget());
                String formatted =
                        addressFilterElement.getFunction().accept(new FWAFormatter(fullNames)).getSb().toString();
                if (formatted.equals("(TRUE)")) formatted = String.join(" x ", fullNames);
                if (targetNodeLabel.length() != 0) targetNodeLabel.append("<br/>");
                targetNodeLabel.append(formatted);
            }
            pos = 0;
            for (final Edge edge : node.getIncomingEdges()) {
                final int width = edge.getSourceNode().getMemory().getTemplate().length;
                if (edge.getSourceNode().getOutgoingExistentialEdges().contains(edge)) {
                    final List<String> namesPart = this.names.subList(pos, pos + width);
                    namesPart.replaceAll(n -> "!");
                }
                pos += width;
            }
            NetworkToDot.this.betaNodes
                    .put(node, '"' + targetNodeName + "\" [label=<" + targetNodeLabel.toString() + ">]");
            NetworkToDot.this.node2AddressArrays.put(node, new ArrayList<>(this.addresses));
        }

        @Override
        public void visit(final ObjectTypeNode node) {
            final String targetNodeName = getNodeName(node);
            if (NetworkToDot.this.otns.containsKey(node)) {
                assert NetworkToDot.this.node2AddressArrays.containsKey(node);
                final List<FactAddress> list = NetworkToDot.this.node2AddressArrays.get(node);
                assert this.addresses.size() == 1;
                assert list.size() == 1;
                final Iterator<FactAddress> i = list.iterator();
                this.addresses.replaceAll(a -> i.next());
                return;
            }
            assert this.addresses.size() == 1;
            this.addresses.set(0, node.getFactAddress());
            NetworkToDot.this.otns.put(node, targetNodeName);
            generateEdge(NetworkToDot.this.rootNode, targetNodeName, null);
            NetworkToDot.this.node2AddressArrays.put(node, new ArrayList<>(this.addresses));
        }
    }

    private final NameNodes nameNodes = new NameNodes();

    private String getNodeName(final Node node) {
        return this.nameNodes.getNodeName(node);
    }

    public NetworkToDot(final SideEffectFunctionToNetwork network, final String... rules) {
        super();
        Arrays.sort(rules);
        for (final TerminalNode terminalNode : network.getTerminalNodes()) {
            final String ruleName = terminalNode.getRule().getParent().getName();
            if (rules.length != 0 && Arrays.binarySearch(rules, ruleName) < 0) {
                continue;
            }
            this.terminalNodes.put(terminalNode, ruleName);
            final Node sourceNode = terminalNode.getEdge().getSourceNode();

            final Map<Template, String> template2Name = new HashMap<>();
            final Map<Template, Integer> template2Occurences = new HashMap<>();
            final List<Template> templates = Arrays.asList(sourceNode.getMemory().getTemplate());
            final List<FactAddress> addresses = new ArrayList<>(Collections.nCopies(templates.size(), null));
            final List<String> names = new ArrayList<>(Collections.nCopies(templates.size(), null));
            for (int i = 0; i < templates.size(); i++) {
                final Template template = templates.get(i);
                String name = template2Name.get(template);
                if (null == name) {
                    int length = 1;
                    while (null == name || template2Name.containsValue(name)) {
                        name = template.getName().substring(0, length++);
                    }
                    template2Name.put(template, name);
                }
                final Integer occurencesI = template2Occurences.get(template);
                int occurences = 0;
                if (null != occurencesI) {
                    occurences = occurencesI;
                }
                occurences++;
                template2Occurences.put(template, occurences);
                name = name + StringUtils.repeat("'", occurences - 1);
                names.set(i, name);
            }

            final String sourceNodeName = getNodeName(sourceNode);
            sourceNode.accept(new GraphConstructingNodeVisitor(names, templates, addresses));
            generateEdge(sourceNodeName, ruleName, "(" + String.join(", ", names) + ")");
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String n = System.lineSeparator();
        sb.append("digraph network {").append(n).append(n);

        sb.append("// root node").append(n);
        sb.append(this.rootNode).append("[shape=ellipse];").append(n).append(n);

        sb.append("node[shape=diamond]").append(n).append(n);

        sb.append("// ot nodes").append(n);
        sb.append("{ rank = same").append(n);
        for (final String otn : this.otns.values()) {
            sb.append('"').append(otn).append('"').append(n);
        }
        sb.append("}").append(n);
        sb.append(n);

        sb.append("// alpha nodes").append(n);
        // sb.append("{ rank = same").append(n);
        for (final String alpha : this.alphaNodes.values()) {
            sb.append(alpha).append(n);
        }
        // sb.append("}").append(n);
        sb.append(n);

        sb.append("// beta nodes").append(n);
        sb.append("node[shape=invtriangle]").append(n);
        // sb.append("{ rank = same").append(n);
        for (final String beta : this.betaNodes.values()) {
            sb.append(beta).append(n);
        }
        // sb.append("}").append(n);
        sb.append(n);

        sb.append("// terminal nodes").append(n);
        sb.append("node[shape=triangle]").append(n);
        sb.append("{ rank = same").append(n);
        for (final String terminal : this.terminalNodes.values()) {
            sb.append('"').append(terminal).append('"').append(n);
        }
        sb.append("}").append(n);
        sb.append(n);

        for (final String edge : this.edges) {
            sb.append(edge).append(n);
        }
        sb.append(n);

        sb.append("}").append(n);
        return sb.toString();
    }
}
