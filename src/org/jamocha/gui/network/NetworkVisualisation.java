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
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
	private static final int Y_SPACER = 40;
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
		this.getChildren().clear();
		cnt = 0;
		for (TerminalNode terminalNode : network.getTerminalNodes()) {
			rootGroup.add(visualiseRule(terminalNode));
		}
	}

	private Group visualiseRule(TerminalNode terminalNode) {
		final Group ruleGroup = new Group(this);
		ruleGroup.add(NodeVisualiser.visualizeNode(terminalNode.getEdge().getSourceNode(), this));
		ruleGroup.add(new GraphicalNode.Terminal());
		return ruleGroup;
	}

	private void add(javafx.scene.Node node) {
		this.getChildren().add(node);
	}

	@RequiredArgsConstructor
	private abstract static class GraphicalNode extends Parent {
		
		final DoubleProperty heightProperty = new SimpleDoubleProperty(NODE_HEIGHT);
		final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);
		
		@Getter
		final boolean shared;
		
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

	private static class Group {
		private final NetworkVisualisation nv;

		private final List<Group> groups = new ArrayList<>();

		private final DoubleProperty xProperty = new SimpleDoubleProperty(0);
		private final DoubleProperty yProperty = new SimpleDoubleProperty(0);

		private final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);
		private final DoubleProperty internalHeightProperty = new SimpleDoubleProperty(0);
		private final DoubleProperty heightProperty = new SimpleDoubleProperty();

		private javafx.scene.Node node = null;

		public Group(NetworkVisualisation nv) {
			this.nv = nv;
			heightProperty.bind(internalHeightProperty);
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
			NumberBinding lastHeight = null;
			for (Group aGroup : groups) {
				lastHeight =
						(null == lastHeight) ? Bindings.add(0, aGroup.heightProperty) : Bindings.max(lastHeight,
								aGroup.heightProperty);
			}
			internalHeightProperty.bind(lastHeight);
		}

		public void add(GraphicalNode node) {
			if (null != this.node)
				throw new Error("Node already set");
			this.node = node;
			node.layoutXProperty().bind(xProperty.add(widthProperty.subtract(node.widthProperty()).divide(2)));
			node.layoutYProperty().bind(internalHeightProperty.add(Y_SPACER));
			heightProperty.bind(internalHeightProperty.add(Y_SPACER).add(node.heightProperty()));
			nv.add(node);
			int grpCnt = 1;
			for (Group group : groups) {
				final Line line = new Line();
				line.setStrokeWidth(STROKE_WIDTH);
				line.startXProperty().bind(group.xProperty.add(group.widthProperty.divide(2)));
				line.startYProperty().bind(group.yProperty.add(group.heightProperty));
				line.endXProperty().bind(node.layoutXProperty().add(node.widthProperty().divide(groups.size() + 1).multiply(grpCnt++)));
				line.endYProperty().bind(node.layoutYProperty());
				nv.add(line);
			}
		}
	}

	private static class NodeVisualiser implements NodeVisitor {

		private final NetworkVisualisation nv;
		private final Group nodeGroup;

		public static Group visualizeNode(Node node, NetworkVisualisation nv) {
			return node.accept(new NodeVisualiser(nv)).nodeGroup;
		}

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
