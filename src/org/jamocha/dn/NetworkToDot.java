package org.jamocha.dn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.NodeVisitor;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;

public class NetworkToDot {

	final private Map<Node, List<FactAddress>> node2AddressArrays = new HashMap<>();
	final private Map<AlphaNode, String> alphaNodes = new HashMap<>();
	final private Map<ObjectTypeNode, String> otns = new HashMap<>();
	final private Map<BetaNode, String> betaNodes = new HashMap<>();
	final private Map<TerminalNode, String> terminalNodes = new HashMap<>();
	final private String rootNode = "root";
	final private List<String> edges = new ArrayList<>();

	private void generateEdge(String from, String to, String label) {
		edges.add('"' + from + "\" -> \"" + to + '"' + (null == label ? "" : " [label=\"" + label + "\"]"));
	}

	@RequiredArgsConstructor
	private static class FWAFormatter implements FunctionWithArgumentsVisitor<ParameterLeaf> {

		private final String[] params;

		@Getter
		private final StringBuffer sb = new StringBuffer();

		private void visitComposite(final GenericWithArgumentsComposite<?, ?, ParameterLeaf> gwac) {
			sb.append("(" + gwac.getFunction().inClips());
			int pos = 0;
			for (FunctionWithArguments<ParameterLeaf> functionWithArguments : gwac.getArgs()) {
				final int width = functionWithArguments.getParamTypes().length;
				sb.append(" ");
				sb.append(functionWithArguments.accept(new FWAFormatter(Arrays.copyOfRange(params, pos, pos + width)))
						.getSb().toString());
				pos += width;
			}
			sb.append(")");
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
			sb.append(constantLeaf.toString());
		}

		@Override
		public void visit(final GlobalVariableLeaf<ParameterLeaf> globalVariableLeaf) {
			sb.append(globalVariableLeaf.toString());
		}

		@Override
		public void visit(final ParameterLeaf leaf) {
			assert params.length == 1;
			sb.append(params[0]);
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

		final private Map<Node, String> nodeNames = new HashMap<>();

		private String lastName = null;

		private int alphaCounter = 0;
		private int betaCounter = 0;

		String getNodeName(Node node) {
			final String name = nodeNames.get(node);
			if (null != name)
				return name;
			final String newName = node.accept(this).lastName;
			nodeNames.put(node, newName);
			return newName;
		}

		@Override
		public void visit(AlphaNode node) {
			lastName = "Alpha" + alphaCounter++;
		}

		@Override
		public void visit(BetaNode node) {
			lastName = "Beta" + betaCounter++;
		}

		@Override
		public void visit(ObjectTypeNode node) {
			lastName = node.getTemplate().getName();
		}

	}

	@RequiredArgsConstructor
	private class GraphConstructingNodeVisitor implements NodeVisitor {

		final private List<String> names;
		final private List<Template> templates;
		final private List<FactAddress> addresses;

		private String[] translateAddresses2Names(final SlotInFactAddress[] sifa) {
			final String[] fullNames = new String[sifa.length];
			for (int i = 0; i < sifa.length; i++) {
				final SlotInFactAddress slotInFactAddress = sifa[i];
				final FactAddress factAddress = slotInFactAddress.getFactAddress();
				for (int j = 0; j < addresses.size(); j++) {
					final FactAddress address = addresses.get(j);
					if (address != factAddress)
						continue;
					fullNames[i] =
							names.get(j)
									+ (slotInFactAddress.getSlotAddress() == null ? "" : "."
											+ templates.get(j).getSlotName(slotInFactAddress.getSlotAddress()));
					break;
				}
			}
			return fullNames;
		}

		@Override
		public void visit(AlphaNode node) {
			if (alphaNodes.containsKey(node)) {
				assert node2AddressArrays.containsKey(node);
				final List<FactAddress> list = node2AddressArrays.get(node);
				assert list.size() == addresses.size();
				Iterator<FactAddress> i = list.iterator();
				addresses.replaceAll(a -> i.next());
				return;
			}
			final String targetNodeName = getNodeName(node);
			assert names.size() == 1;
			final Edge[] incomingEdges = node.getIncomingEdges();
			assert incomingEdges.length == 1;
			final Edge edge = incomingEdges[0];
			final Node sourceNode = edge.getSourceNode();
			final String sourceNodeName = getNodeName(sourceNode);
			generateEdge(sourceNodeName, targetNodeName, "(" + names.get(0) + ")");
			sourceNode.accept(new GraphConstructingNodeVisitor(names, templates, addresses));
			addresses.replaceAll(edge::localizeAddress);

			final StringBuilder targetNodeLabel = new StringBuilder();
			for (AddressFilter addressFilterElement : node.getFilter().getFilters()) {
				final String[] fullNames = translateAddresses2Names(addressFilterElement.getAddressesInTarget());
				String formatted =
						addressFilterElement.getFunction().accept(new FWAFormatter(fullNames)).getSb().toString();
				if (formatted.equals("(TRUE)"))
					formatted = String.join(" x ", fullNames);
				if (targetNodeLabel.length() != 0)
					targetNodeLabel.append("<br/>");
				targetNodeLabel.append(formatted);
			}
			alphaNodes.put(node, '"' + targetNodeName + "\" [label=<" + targetNodeLabel.toString() + ">]");
			node2AddressArrays.put(node, new ArrayList<>(addresses));
		}

		@Override
		public void visit(BetaNode node) {
			if (betaNodes.containsKey(node)) {
				assert node2AddressArrays.containsKey(node);
				final List<FactAddress> list = node2AddressArrays.get(node);
				assert list.size() == addresses.size();
				Iterator<FactAddress> i = list.iterator();
				addresses.replaceAll(a -> i.next());
				return;
			}
			final String targetNodeName = getNodeName(node);
			int pos = 0;
			for (Edge edge : node.getIncomingEdges()) {
				final Node sourceNode = edge.getSourceNode();
				final int width = sourceNode.getMemory().getTemplate().length;
				final String sourceNodeName = getNodeName(sourceNode);
				final List<String> namesPart = names.subList(pos, pos + width);
				final List<Template> templatesPart = templates.subList(pos, pos + width);
				final List<FactAddress> addressesPart = addresses.subList(pos, pos + width);
				sourceNode.accept(new GraphConstructingNodeVisitor(namesPart, templatesPart, addressesPart));
				generateEdge(sourceNodeName, targetNodeName, "(" + String.join(", ", namesPart) + ")");
				addressesPart.replaceAll(edge::localizeAddress);
				pos += width;
			}
			final StringBuilder targetNodeLabel = new StringBuilder();
			for (AddressFilter addressFilterElement : node.getFilter().getFilters()) {
				final String[] fullNames = translateAddresses2Names(addressFilterElement.getAddressesInTarget());
				String formatted =
						addressFilterElement.getFunction().accept(new FWAFormatter(fullNames)).getSb().toString();
				if (formatted.equals("(TRUE)"))
					formatted = String.join(" x ", fullNames);
				if (targetNodeLabel.length() != 0)
					targetNodeLabel.append("<br/>");
				targetNodeLabel.append(formatted);
			}
			pos = 0;
			for (Edge edge : node.getIncomingEdges()) {
				final int width = edge.getSourceNode().getMemory().getTemplate().length;
				if (edge.getSourceNode().getOutgoingExistentialEdges().contains(edge)) {
					final List<String> namesPart = names.subList(pos, pos + width);
					namesPart.replaceAll(n -> "!");
				}
				pos += width;
			}
			betaNodes.put(node, '"' + targetNodeName + "\" [label=<" + targetNodeLabel.toString() + ">]");
			node2AddressArrays.put(node, new ArrayList<>(addresses));
		}

		@Override
		public void visit(ObjectTypeNode node) {
			final String targetNodeName = getNodeName(node);
			if (otns.containsKey(node)) {
				assert node2AddressArrays.containsKey(node);
				final List<FactAddress> list = node2AddressArrays.get(node);
				assert addresses.size() == 1;
				assert list.size() == 1;
				Iterator<FactAddress> i = list.iterator();
				addresses.replaceAll(a -> i.next());
				return;
			}
			assert this.addresses.size() == 1;
			this.addresses.set(0, node.getFactAddress());
			otns.put(node, targetNodeName);
			generateEdge(rootNode, targetNodeName, null);
			node2AddressArrays.put(node, new ArrayList<>(addresses));
		}
	}

	private final NameNodes nameNodes = new NameNodes();

	private String getNodeName(Node node) {
		return nameNodes.getNodeName(node);
	}

	public NetworkToDot(final SideEffectFunctionToNetwork network, final String... rules) {
		super();
		Arrays.sort(rules);
		for (TerminalNode terminalNode : network.getTerminalNodes()) {
			final String ruleName = terminalNode.getRule().getParent().getName();
			if (rules.length != 0 && Arrays.binarySearch(rules, ruleName) < 0) {
				continue;
			}
			terminalNodes.put(terminalNode, ruleName);
			final Node sourceNode = terminalNode.getEdge().getSourceNode();

			final Map<Template, String> template2Name = new HashMap<>();
			final Map<Template, Integer> template2Occurences = new HashMap<>();
			List<Template> templates = Arrays.asList(sourceNode.getMemory().getTemplate());
			List<FactAddress> addresses = new ArrayList<>(Collections.nCopies(templates.size(), null));
			List<String> names = new ArrayList<>(Collections.nCopies(templates.size(), null));
			for (int i = 0; i < templates.size(); i++) {
				Template template = templates.get(i);
				String name = template2Name.get(template);
				if (null == name) {
					int length = 1;
					while (null == name || template2Name.containsValue(name))
						name = template.getName().substring(0, length++);
					template2Name.put(template, name);
				}
				Integer occurencesI = template2Occurences.get(template);
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
		StringBuilder sb = new StringBuilder();
		final String n = System.lineSeparator();
		sb.append("digraph network {").append(n).append(n);

		sb.append("// root node").append(n);
		sb.append(rootNode).append("[shape=ellipse];").append(n).append(n);

		sb.append("node[shape=diamond]").append(n).append(n);

		sb.append("// ot nodes").append(n);
		sb.append("{ rank = same").append(n);
		for (final String otn : otns.values()) {
			sb.append('"').append(otn).append('"').append(n);
		}
		sb.append("}").append(n);
		sb.append(n);

		sb.append("// alpha nodes").append(n);
		// sb.append("{ rank = same").append(n);
		for (final String alpha : alphaNodes.values()) {
			sb.append(alpha).append(n);
		}
		// sb.append("}").append(n);
		sb.append(n);

		sb.append("// beta nodes").append(n);
		sb.append("node[shape=invtriangle]").append(n);
		// sb.append("{ rank = same").append(n);
		for (final String beta : betaNodes.values()) {
			sb.append(beta).append(n);
		}
		// sb.append("}").append(n);
		sb.append(n);

		sb.append("// terminal nodes").append(n);
		sb.append("node[shape=triangle]").append(n);
		sb.append("{ rank = same").append(n);
		for (final String terminal : terminalNodes.values()) {
			sb.append('"').append(terminal).append('"').append(n);
		}
		sb.append("}").append(n);
		sb.append(n);

		for (final String edge : edges) {
			sb.append(edge).append(n);
		}
		sb.append(n);

		sb.append("}").append(n);
		return sb.toString();
	}
}