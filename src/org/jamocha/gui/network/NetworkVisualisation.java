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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.Network;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.NodeVisitor;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class NetworkVisualisation extends Region {

	private static final int X_SPACER = 20;
	private static final int Y_SPACER = 150;
	private static final int NODE_WIDTH = 80;
	private static final int NODE_HEIGHT = 40;
	private static final double STROKE_WIDTH = 3;

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
			this.visualizeNode(terminalNode.getEdge().getSourceNode(), this).addTo(ruleGroup);
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
		final boolean shared;

		private GraphicalNode(boolean shared) {
			this.shared = shared;
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
			final Polyline polyline = new Polyline(0, NODE_HEIGHT, NODE_WIDTH, NODE_HEIGHT, NODE_WIDTH / 2, 0, 0,
					NODE_HEIGHT);

			Terminal() {
				super(false);
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
				super(betaNode.getOutgoingEdges().size() > 1);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}
		}

		private static class Alpha extends GraphicalNode {
			final Polyline polyline = new Polyline(0, NODE_HEIGHT / 2, NODE_WIDTH / 2, 0, NODE_WIDTH, NODE_HEIGHT / 2,
					NODE_WIDTH / 2, NODE_HEIGHT, 0, NODE_HEIGHT / 2);

			Alpha(AlphaNode alphaNode) {
				super(alphaNode.getOutgoingEdges().size() > 1);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}
		}

		private static class OTN extends GraphicalNode {
			final Polyline polyline = new Polyline(0, NODE_HEIGHT / 2, NODE_WIDTH / 2, 0, NODE_WIDTH, NODE_HEIGHT / 2,
					NODE_WIDTH / 2, NODE_HEIGHT, 0, NODE_HEIGHT / 2);

			OTN() {
				super(false);
				polyline.setStrokeWidth(STROKE_WIDTH);
				this.getChildren().add(polyline);
			}
		}
	}

	private static class Link extends Parent {
		
		final CubicCurve curve = new CubicCurve(); 
		final Line line = new Line();

		public Link(ConnectionPoint from, ConnectionPoint to) {
			super();
			curve.setFill(null);
			curve.setStroke(Color.BLACK);
			curve.setStrokeWidth(STROKE_WIDTH);
			curve.startXProperty().bind(from.xProperty());
			curve.startYProperty().bind(from.yProperty());
			curve.controlX1Property().bind(from.xProperty());
			final DoubleBinding distanceFactor = Bindings.min(to.xProperty().subtract(from.xProperty()).divide(500), 0.3);
			curve.controlY1Property().bind(from.yProperty().add(distanceFactor.multiply(Y_SPACER)));
			curve.controlX2Property().bind(to.xProperty());
			curve.controlY2Property().bind(from.yProperty().add(distanceFactor.subtract(1).multiply(Y_SPACER * -1)));
			curve.endXProperty().bind(to.xProperty());
			curve.endYProperty().bind(from.yProperty().add(Y_SPACER));
			line.setStroke(Color.BLACK);
			line.setStrokeWidth(STROKE_WIDTH);
			line.startXProperty().bind(curve.endXProperty());
			line.startYProperty().bind(curve.endYProperty());
			line.endXProperty().bind(to.xProperty());
			line.endYProperty().bind(to.yProperty());
			final ObservableList<javafx.scene.Node> children = this.getChildren();
			children.add(curve);
			children.add(line);
		}

	}

	private static interface GroupInterface {
		public void addTo(Group group);
	}

	@RequiredArgsConstructor
	private static class SharedGroup implements GroupInterface {
		final Group group;

		public ConnectionPoint getOutput() {
			return group.getOutput();
		}
		
		public DoubleProperty heightProperty() {
			return group.heightProperty();
		}

		public void addTo(Group group) {
			group.add(this);
		}
	}

	private static class Group implements GroupInterface {
		private final NetworkVisualisation nv;

		private final List<Group> groups = new ArrayList<>();
		private final List<SharedGroup> sharedGroups = new ArrayList<>();

		private final DoubleProperty xProperty = new SimpleDoubleProperty(0);
		private final DoubleProperty yProperty = new SimpleDoubleProperty(0);

		private final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);
		private final DoubleProperty internalHeightProperty = new SimpleDoubleProperty(0);
		private final DoubleProperty heightProperty = new SimpleDoubleProperty();

		private GraphicalNode node = null;

		public Group(NetworkVisualisation nv) {
			this.nv = nv;
			heightProperty.bind(internalHeightProperty);
		}

		public ConnectionPoint getOutput() {
			return node.getOutput();
		}
		
		private void bindHeight() {
			NumberBinding lastHeight = null;
			for (Group aGroup : groups) {
				lastHeight =
						(null == lastHeight) ? Bindings.add(0, aGroup.heightProperty) : Bindings.max(lastHeight,
								aGroup.heightProperty);
			}
			for (SharedGroup aSharedGroup : sharedGroups) {
				lastHeight =
						(null == lastHeight) ? Bindings.add(0, aSharedGroup.heightProperty()) : Bindings.max(lastHeight,
								aSharedGroup.heightProperty());
			}
			internalHeightProperty.bind(lastHeight);
		}

		public void add(Group group) {
			if (groups.size() > 0) {
				Group lastGroup = groups.get(groups.size() - 1);
				group.xProperty.bind(lastGroup.xProperty.add(lastGroup.widthProperty).add(X_SPACER));
			} else {
				group.xProperty.bind(xProperty);
			}
			group.yProperty.bind(yProperty);
			groups.add(group);
			widthProperty.bind(group.xProperty.add(group.widthProperty).subtract(xProperty));
			bindHeight();
		}

		public void add(SharedGroup sharedGroup) {
			sharedGroups.add(sharedGroup);
			bindHeight();
		}

		public void addTo(Group group) {
			group.add(this);
		}

		public void add(GraphicalNode node) {
			if (null != this.node)
				throw new Error("Node already set");
			this.node = node;
			node.layoutXProperty().bind(xProperty.add(widthProperty.subtract(node.widthProperty()).divide(2)));
			node.layoutYProperty().bind(internalHeightProperty.add(Y_SPACER));
			heightProperty.bind(internalHeightProperty.add(Y_SPACER).add(node.heightProperty()));
			nv.add(node);
			for (Group group : groups) {
				final Link line = new Link(group.getOutput(), node.getInput());
				nv.add(line);
			}
			for (SharedGroup sharedGroup : sharedGroups) {
				final Link line = new Link(sharedGroup.getOutput(), node.getInput());
				nv.add(line);
			}
		}
		
		public DoubleProperty heightProperty() {
			return heightProperty;
		}
	}
	
	private final Map<Node, Group> node2Group = new HashMap<>();
	
	public GroupInterface visualizeNode(Node node, NetworkVisualisation nv) {
		{
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
				visualizeNode(edge.getSourceNode(), nv).addTo(nodeGroup);
			}
			nodeGroup.add(new GraphicalNode.Alpha(alphaNode));
		}

		@Override
		public void visit(BetaNode betaNode) {
			for (Edge edge : betaNode.getIncomingEdges()) {
				visualizeNode(edge.getSourceNode(), nv).addTo(nodeGroup);
			}
			nodeGroup.add(new GraphicalNode.Beta(betaNode));
		}

		@Override
		public void visit(ObjectTypeNode otn) {
			nodeGroup.add(new GraphicalNode.OTN());
		}

	}

}
