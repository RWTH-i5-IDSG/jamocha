package org.jamocha.dn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.NodeVisitor;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.TerminalNode;

public class NetworkToDot implements NodeVisitor {

	final private Map<AlphaNode, String> alphaNodes = new HashMap<>();
	final private Map<ObjectTypeNode, String> otns = new HashMap<>();
	final private Map<BetaNode, String> betaNodes = new HashMap<>();
	final private Map<TerminalNode, String> terminalNodes = new HashMap<>();
	final private String rootNode = "root";
	final private List<String> edges = new ArrayList<>();

	private void generateEdge(String from, String to) {
		edges.add('"' + from + "\" -> \"" + to + '"');
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
			final String sourceNodeName = getNodeName(sourceNode);
			generateEdge(sourceNodeName, ruleName);
			sourceNode.accept(this);
		}
	}

	@Override
	public void visit(AlphaNode node) {
		final String targetNodeName = getNodeName(node);
		if (alphaNodes.containsKey(node))
			return;
		alphaNodes.put(node, targetNodeName);
		for (Edge edge : node.getIncomingEdges()) {
			final Node sourceNode = edge.getSourceNode();
			final String sourceNodeName = getNodeName(sourceNode);
			generateEdge(sourceNodeName, targetNodeName);
			sourceNode.accept(this);
		}
	}

	@Override
	public void visit(BetaNode node) {
		final String targetNodeName = getNodeName(node);
		if (betaNodes.containsKey(node))
			return;
		betaNodes.put(node, targetNodeName);
		for (Edge edge : node.getIncomingEdges()) {
			final Node sourceNode = edge.getSourceNode();
			final String sourceNodeName = getNodeName(sourceNode);
			generateEdge(sourceNodeName, targetNodeName);
			sourceNode.accept(this);
		}
	}

	@Override
	public void visit(ObjectTypeNode node) {
		final String targetNodeName = getNodeName(node);
		if (otns.containsKey(node))
			return;
		otns.put(node, targetNodeName);
		generateEdge(rootNode, targetNodeName);
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
			sb.append('"').append(alpha).append('"').append(n);
		}
		// sb.append("}").append(n);
		sb.append(n);

		sb.append("// beta nodes").append(n);
		sb.append("node[shape=invtriangle]").append(n);
		// sb.append("{ rank = same").append(n);
		for (final String beta : betaNodes.values()) {
			sb.append('"').append(beta).append('"').append(n);
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