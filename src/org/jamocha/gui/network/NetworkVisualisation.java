/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.gui.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.Network;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.NodeVisitor;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.TerminalNode;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class NetworkVisualisation extends Region {

	private static final int X_SPACER = 20;
	private static final int Y_SPACER = 150;
	private static final int NODE_WIDTH = 160;
	private static final int NODE_HEIGHT = 80;
	private static final double STROKE_WIDTH = 3;

	private boolean unifyShared = false;

	int cnt = 0;

	final Network network;

	public NetworkVisualisation(Network network) {
		super();
		this.network = network;
	}

	public void update() {
		final Group rootGroup = new Group(this);
		this.clear();
		cnt = 0;
		final Map<Defrule, List<TerminalNode>> terminalsByRules =
				network.getTerminalNodes().stream().collect(Collectors.groupingBy(t -> t.getRule().getParent()));
		for (Defrule rule : terminalsByRules.keySet()) {
			rootGroup.add(visualiseRule(rule, terminalsByRules.get(rule)));
		}
	}

	private Group visualiseRule(Defrule rule, List<TerminalNode> terminalNodes) {
		final Group ruleGroup = new Group(this);
		for (TerminalNode terminalNode : terminalNodes) {
			ruleGroup.add(this.visualizeNode(terminalNode.getEdge().getSourceNode(), this));
		}
		ruleGroup.add(new GraphicalNode.Terminal());
		return ruleGroup;
	}

	private void add(javafx.scene.Node node) {
		this.getChildren().add(node);
	}

	private static class ConnectionPoint {
		final DoubleProperty xProperty = new SimpleDoubleProperty();
		final DoubleProperty yProperty = new SimpleDoubleProperty();

		public DoubleProperty xProperty() {
			return xProperty;
		}

		public DoubleProperty yProperty() {
			return yProperty;
		}
	}

	private abstract static class GraphicalNode extends Parent {

		final DoubleProperty heightProperty = new SimpleDoubleProperty(NODE_HEIGHT);
		final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);

		final List<ConnectionPoint> inputs = new ArrayList<>();

		@Getter
		final ConnectionPoint output = new ConnectionPoint();

		@Getter
		final Node node;

		@Getter
		final boolean shared;

		private GraphicalNode(boolean shared, Node node) {
			super();
			this.shared = shared;
			this.node = node;
			output.xProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(2)));
			output.yProperty().bind(this.layoutYProperty().add(this.heightProperty()));
		}

		public ConnectionPoint getInput() {
			final ConnectionPoint newInput = new ConnectionPoint();
			inputs.add(newInput);
			int cnt = 1;
			for (ConnectionPoint input : inputs) {
				input.xProperty().bind(
						this.layoutXProperty().add(this.widthProperty().divide(inputs.size() + 1).multiply(cnt++)));
				input.yProperty().bind(this.layoutYProperty());
			}
			return newInput;
		}

		public DoubleProperty heightProperty() {
			return heightProperty;
		}

		public DoubleProperty widthProperty() {
			return widthProperty;
		}

		private static class Terminal extends GraphicalNode {
			final Polyline polyline =
					new Polyline(0, NODE_HEIGHT, NODE_WIDTH, NODE_HEIGHT, NODE_WIDTH / 2, 0, 0, NODE_HEIGHT);

			Terminal() {
				super(false, null);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}

			public ConnectionPoint getInput() {
				final ConnectionPoint newInput = new ConnectionPoint();
				inputs.add(newInput);
				newInput.xProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(2)));
				newInput.yProperty().bind(this.layoutYProperty());
				return newInput;
			}
		}

		private static class Beta extends GraphicalNode {
			final Polyline polyline = new Polyline(0, 0, NODE_WIDTH, 0, NODE_WIDTH / 2, NODE_HEIGHT, 0, 0);

			Beta(BetaNode betaNode) {
				super(betaNode.getOutgoingEdges().size() > 1, betaNode);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}
		}

		private static class Alpha extends GraphicalNode {
			final Polyline polyline = new Polyline(0, NODE_HEIGHT / 2, NODE_WIDTH / 2, 0, NODE_WIDTH, NODE_HEIGHT / 2,
					NODE_WIDTH / 2, NODE_HEIGHT, 0, NODE_HEIGHT / 2);

			Alpha(AlphaNode alphaNode) {
				super(alphaNode.getOutgoingEdges().size() > 1, alphaNode);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}
		}

		private static class OTN extends GraphicalNode {
			final Polyline polyline = new Polyline(0, NODE_HEIGHT / 2, NODE_WIDTH / 2, 0, NODE_WIDTH, NODE_HEIGHT / 2,
					NODE_WIDTH / 2, NODE_HEIGHT, 0, NODE_HEIGHT / 2);

			OTN() {
				super(false, null);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}
		}
	}

	private static class Link extends Parent {

		final Line line1 = new Line();
		final Line line2 = new Line();

		public Link(ConnectionPoint from, ConnectionPoint to) {
			super();
			line1.setFill(null);
			line1.setStroke(Color.BLACK);
			line1.setStrokeWidth(STROKE_WIDTH);
			line1.startXProperty().bind(from.xProperty());
			line1.startYProperty().bind(from.yProperty());
			line1.endXProperty().bind(to.xProperty());
			line1.endYProperty().bind(to.yProperty());
			// line1.endYProperty().bind(from.yProperty().add(Y_SPACER / 3 * 2));
			// line2.setStroke(Color.BLACK);
			// line2.setStrokeWidth(STROKE_WIDTH);
			// line2.startXProperty().bind(line1.endXProperty());
			// line2.startYProperty().bind(line1.endYProperty());
			// line2.endXProperty().bind(to.xProperty());
			// line2.endYProperty().bind(to.yProperty());
			final ObservableList<javafx.scene.Node> children = this.getChildren();
			children.add(line1);
			// children.add(line2);
		}

	}

	private static interface GroupInterface {
		public DoubleProperty heightProperty();

		public DoubleProperty widthProperty();

		public DoubleProperty xProperty();

		public DoubleProperty yProperty();

		public ConnectionPoint getOutput();
	}

	@RequiredArgsConstructor
	private static class SharedGroup implements GroupInterface {
		final Group group;

		final DoubleProperty xProperty = new SimpleDoubleProperty();
		final DoubleProperty yProperty = new SimpleDoubleProperty();
		final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);

		@Override
		public ConnectionPoint getOutput() {
			return group.getOutput();
		}

		@Override
		public DoubleProperty heightProperty() {
			return group.heightProperty();
		}

		@Override
		public DoubleProperty widthProperty() {
			return widthProperty;
		}

		@Override
		public DoubleProperty xProperty() {
			return xProperty;
		}

		@Override
		public DoubleProperty yProperty() {
			return yProperty;
		}
	}

	private class Group implements GroupInterface {
		private final NetworkVisualisation nv;

		private final List<GroupInterface> groups = new ArrayList<>();

		private final DoubleProperty xProperty = new SimpleDoubleProperty(0);
		private final DoubleProperty yProperty = new SimpleDoubleProperty(0);
		private final DoubleProperty internalXProperty = new SimpleDoubleProperty();
		private final DoubleProperty internalYProperty = new SimpleDoubleProperty();

		private final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);
		private final DoubleProperty internalHeightProperty = new SimpleDoubleProperty(-1 * Y_SPACER);
		private final DoubleProperty heightProperty = new SimpleDoubleProperty(0);

		private GraphicalNode node = null;

		public Group(NetworkVisualisation nv) {
			this.nv = nv;
			heightProperty.bind(internalHeightProperty);
			internalXProperty.bind(xProperty);
			internalYProperty.bind(yProperty);
		}

		@Override
		public ConnectionPoint getOutput() {
			return node.getOutput();
		}

		private void bindHeight() {
			if (groups.size() == 0) {
				internalHeightProperty.set(-1 * Y_SPACER);
				return;
			}
			NumberBinding lastHeight = null;
			for (GroupInterface aGroup : groups) {
				lastHeight = (null == lastHeight) ? Bindings.add(0, aGroup.heightProperty())
						: Bindings.max(lastHeight, aGroup.heightProperty());
			}
			if (null != node && node.isShared())
				lastHeight = lastHeight.add(20);
			internalHeightProperty.bind(lastHeight);
		}

		private void bindWidth() {
			if (groups.size() == 0) {
				widthProperty.set(NODE_WIDTH);
				return;
			}
			NumberBinding lastWidth = null;
			for (GroupInterface aGroup : groups) {
				lastWidth = (null == lastWidth) ? Bindings.add(0, aGroup.widthProperty())
						: Bindings.add(lastWidth, aGroup.widthProperty()).add(X_SPACER);
			}
			if (null != node && node.isShared())
				lastWidth = lastWidth.add(20);
			widthProperty.bind(lastWidth);
		}

		public void add(GroupInterface group) {
			if (groups.size() > 0) {
				GroupInterface lastGroup = groups.get(groups.size() - 1);
				group.xProperty().bind(lastGroup.xProperty().add(lastGroup.widthProperty()).add(X_SPACER));
			} else {
				group.xProperty().bind(internalXProperty);
			}
			group.yProperty().bind(internalYProperty);
			groups.add(group);
		}

		public void add(GraphicalNode node) {
			if (null != this.node)
				throw new Error("Node already set");
			this.node = node;
			node.layoutXProperty().bind(internalXProperty.add(widthProperty.subtract(node.widthProperty()).divide(2)));
			node.layoutYProperty().bind(internalYProperty.add(internalHeightProperty).add(Y_SPACER));
			heightProperty.bind(internalHeightProperty.add(Y_SPACER).add(node.heightProperty()));
			nv.add(node);
			for (GroupInterface group : groups) {
				final Link line = new Link(group.getOutput(), node.getInput());
				nv.add(line);
			}
			this.bindHeight();
			this.bindWidth();
			if (node.isShared()) {
				String name = shared2Names.computeIfAbsent(node.getNode(), n -> "" + sharedCounter++);
				Rectangle frame = new Rectangle();
				frame.setStroke(Color.BLACK);
				frame.setStrokeWidth(STROKE_WIDTH);
				frame.setFill(null);
				frame.layoutXProperty().bind(this.xProperty());
				frame.layoutYProperty().bind(this.yProperty());
				frame.widthProperty().bind(this.widthProperty());
				heightProperty.bind(internalHeightProperty.add(Y_SPACER).add(node.heightProperty()).add(20));
				frame.heightProperty().bind(this.heightProperty());
				internalXProperty.bind(xProperty.add(10));
				internalYProperty.bind(yProperty.add(10));
				Text text = new Text(name);
				text.setFont(new Font(20));
				text.layoutXProperty().bind(xProperty.add(10));
				text.layoutYProperty().bind(yProperty.add(heightProperty).subtract(10));
				nv.add(frame);
				nv.add(text);
			}
		}

		@Override
		public DoubleProperty heightProperty() {
			return heightProperty;
		}

		@Override
		public DoubleProperty widthProperty() {
			return widthProperty;
		}

		@Override
		public DoubleProperty xProperty() {
			return xProperty;
		}

		@Override
		public DoubleProperty yProperty() {
			return yProperty;
		}
	}

	private final Map<Node, Group> node2Group = new HashMap<>();

	private final Map<Node, String> shared2Names = new HashMap<>();

	private int sharedCounter = 1;

	public GroupInterface visualizeNode(Node node, NetworkVisualisation nv) {
		if (unifyShared) {
			final Group nodeGroup = node2Group.get(node);
			if (null != nodeGroup)
				return new SharedGroup(nodeGroup);
		}
		{
			final Group nodeGroup = node.accept(new NodeVisualiser(nv)).nodeGroup;
			node2Group.put(node, nodeGroup);
			return nodeGroup;
		}
	}

	public void clear() {
		this.getChildren().clear();
		node2Group.clear();
	}

	private class NodeVisualiser implements NodeVisitor {

		private final NetworkVisualisation nv;
		private final Group nodeGroup;

		private NodeVisualiser(NetworkVisualisation nv) {
			nodeGroup = new Group(nv);
			this.nv = nv;
		}

		@Override
		public void visit(AlphaNode alphaNode) {
			for (Edge edge : alphaNode.getIncomingEdges()) {
				nodeGroup.add(visualizeNode(edge.getSourceNode(), nv));
			}
			nodeGroup.add(new GraphicalNode.Alpha(alphaNode));
		}

		@Override
		public void visit(BetaNode betaNode) {
			for (Edge edge : betaNode.getIncomingEdges()) {
				nodeGroup.add(visualizeNode(edge.getSourceNode(), nv));
			}
			nodeGroup.add(new GraphicalNode.Beta(betaNode));
		}

		@Override
		public void visit(ObjectTypeNode otn) {
			nodeGroup.add(new GraphicalNode.OTN());
		}

	}

}
