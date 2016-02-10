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
package org.jamocha.gui.network;

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
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.Network;
import org.jamocha.dn.nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
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

    public NetworkVisualisation(final Network network) {
        super();
        this.network = network;
    }

    public void update() {
        final Group rootGroup = new Group(this);
        this.clear();
        this.cnt = 0;
        final Map<Defrule, List<TerminalNode>> terminalsByRules =
                this.network.getTerminalNodes().stream().collect(Collectors.groupingBy(t -> t.getRule().getParent()));
        for (final Map.Entry<Defrule, List<TerminalNode>> entry : terminalsByRules.entrySet()) {
            rootGroup.add(visualiseRule(entry.getKey(), entry.getValue()));
        }
    }

    private Group visualiseRule(final Defrule rule, final List<TerminalNode> terminalNodes) {
        final Group ruleGroup = new Group(this);
        for (TerminalNode terminalNode : terminalNodes) {
            ruleGroup.add(this.visualizeNode(terminalNode.getEdge().getSourceNode(), this));
        }
        ruleGroup.add(new GraphicalNode.Terminal());
        return ruleGroup;
    }

    private void add(final javafx.scene.Node node) {
        this.getChildren().add(node);
    }

    private static class ConnectionPoint {
        final DoubleProperty xProperty = new SimpleDoubleProperty();
        final DoubleProperty yProperty = new SimpleDoubleProperty();

        public DoubleProperty xProperty() {
            return this.xProperty;
        }

        public DoubleProperty yProperty() {
            return this.yProperty;
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

        private GraphicalNode(final boolean shared, final Node node) {
            super();
            this.shared = shared;
            this.node = node;
            this.output.xProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(2)));
            this.output.yProperty().bind(this.layoutYProperty().add(this.heightProperty()));
        }

        public ConnectionPoint getInput() {
            final ConnectionPoint newInput = new ConnectionPoint();
            this.inputs.add(newInput);
            int cnt = 1;
            for (ConnectionPoint input : this.inputs) {
                input.xProperty().bind(this.layoutXProperty()
                        .add(this.widthProperty().divide(this.inputs.size() + 1).multiply(cnt++)));
                input.yProperty().bind(this.layoutYProperty());
            }
            return newInput;
        }

        public DoubleProperty heightProperty() {
            return this.heightProperty;
        }

        public DoubleProperty widthProperty() {
            return this.widthProperty;
        }

        private static class Terminal extends GraphicalNode {
            final Polyline polyline =
                    new Polyline(0, NODE_HEIGHT, NODE_WIDTH, NODE_HEIGHT, NODE_WIDTH / 2, 0, 0, NODE_HEIGHT);

            Terminal() {
                super(false, null);
                this.polyline.setStrokeWidth(STROKE_WIDTH);
                this.getChildren().add(this.polyline);
            }

            @Override
            public ConnectionPoint getInput() {
                final ConnectionPoint newInput = new ConnectionPoint();
                this.inputs.add(newInput);
                newInput.xProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(2)));
                newInput.yProperty().bind(this.layoutYProperty());
                return newInput;
            }
        }

        private static class Beta extends GraphicalNode {
            final Polyline polyline = new Polyline(0, 0, NODE_WIDTH, 0, NODE_WIDTH / 2, NODE_HEIGHT, 0, 0);

            Beta(final BetaNode betaNode) {
                super(betaNode.getOutgoingEdges().size() > 1, betaNode);
                this.polyline.setStrokeWidth(STROKE_WIDTH);
                this.getChildren().add(this.polyline);
            }
        }

        private static class Alpha extends GraphicalNode {
            final Polyline polyline =
                    new Polyline(0, NODE_HEIGHT / 2, NODE_WIDTH / 2, 0, NODE_WIDTH, NODE_HEIGHT / 2, NODE_WIDTH / 2,
                            NODE_HEIGHT, 0, NODE_HEIGHT / 2);

            Alpha(final AlphaNode alphaNode) {
                super(alphaNode.getOutgoingEdges().size() > 1, alphaNode);
                this.polyline.setStrokeWidth(STROKE_WIDTH);
                this.getChildren().add(this.polyline);
            }
        }

        private static class OTN extends GraphicalNode {
            final Polyline polyline =
                    new Polyline(0, NODE_HEIGHT / 2, NODE_WIDTH / 2, 0, NODE_WIDTH, NODE_HEIGHT / 2, NODE_WIDTH / 2,
                            NODE_HEIGHT, 0, NODE_HEIGHT / 2);

            OTN() {
                super(false, null);
                this.polyline.setStrokeWidth(STROKE_WIDTH);
                this.getChildren().add(this.polyline);
            }
        }
    }

    private static class Link extends Parent {

        final Line line1 = new Line();
        final Line line2 = new Line();

        Link(final ConnectionPoint from, final ConnectionPoint to) {
            super();
            this.line1.setFill(null);
            this.line1.setStroke(Color.BLACK);
            this.line1.setStrokeWidth(STROKE_WIDTH);
            this.line1.startXProperty().bind(from.xProperty());
            this.line1.startYProperty().bind(from.yProperty());
            this.line1.endXProperty().bind(to.xProperty());
            this.line1.endYProperty().bind(to.yProperty());
            // line1.endYProperty().bind(from.yProperty().add(Y_SPACER / 3 * 2));
            // line2.setStroke(Color.BLACK);
            // line2.setStrokeWidth(STROKE_WIDTH);
            // line2.startXProperty().bind(line1.endXProperty());
            // line2.startYProperty().bind(line1.endYProperty());
            // line2.endXProperty().bind(to.xProperty());
            // line2.endYProperty().bind(to.yProperty());
            final ObservableList<javafx.scene.Node> children = this.getChildren();
            children.add(this.line1);
            // children.add(line2);
        }

    }

    private interface GroupInterface {
        DoubleProperty heightProperty();

        DoubleProperty widthProperty();

        DoubleProperty xProperty();

        DoubleProperty yProperty();

        ConnectionPoint getOutput();
    }

    @RequiredArgsConstructor
    private static class SharedGroup implements GroupInterface {
        final Group group;

        final DoubleProperty xProperty = new SimpleDoubleProperty();
        final DoubleProperty yProperty = new SimpleDoubleProperty();
        final DoubleProperty widthProperty = new SimpleDoubleProperty(NODE_WIDTH);

        @Override
        public ConnectionPoint getOutput() {
            return this.group.getOutput();
        }

        @Override
        public DoubleProperty heightProperty() {
            return this.group.heightProperty();
        }

        @Override
        public DoubleProperty widthProperty() {
            return this.widthProperty;
        }

        @Override
        public DoubleProperty xProperty() {
            return this.xProperty;
        }

        @Override
        public DoubleProperty yProperty() {
            return this.yProperty;
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

        Group(final NetworkVisualisation nv) {
            this.nv = nv;
            this.heightProperty.bind(this.internalHeightProperty);
            this.internalXProperty.bind(this.xProperty);
            this.internalYProperty.bind(this.yProperty);
        }

        @Override
        public ConnectionPoint getOutput() {
            return this.node.getOutput();
        }

        private void bindHeight() {
            if (this.groups.size() == 0) {
                this.internalHeightProperty.set(-1 * Y_SPACER);
                return;
            }
            NumberBinding lastHeight = null;
            for (GroupInterface aGroup : this.groups) {
                lastHeight = (null == lastHeight) ? Bindings.add(0, aGroup.heightProperty())
                        : Bindings.max(lastHeight, aGroup.heightProperty());
            }
            if (null != this.node && this.node.isShared()) lastHeight = lastHeight.add(20);
            this.internalHeightProperty.bind(lastHeight);
        }

        private void bindWidth() {
            if (this.groups.size() == 0) {
                this.widthProperty.set(NODE_WIDTH);
                return;
            }
            NumberBinding lastWidth = null;
            for (GroupInterface aGroup : this.groups) {
                lastWidth = (null == lastWidth) ? Bindings.add(0, aGroup.widthProperty())
                        : Bindings.add(lastWidth, aGroup.widthProperty()).add(X_SPACER);
            }
            if (null != this.node && this.node.isShared()) lastWidth = lastWidth.add(20);
            this.widthProperty.bind(lastWidth);
        }

        public void add(final GroupInterface group) {
            if (this.groups.size() > 0) {
                final GroupInterface lastGroup = this.groups.get(this.groups.size() - 1);
                group.xProperty().bind(lastGroup.xProperty().add(lastGroup.widthProperty()).add(X_SPACER));
            } else {
                group.xProperty().bind(this.internalXProperty);
            }
            group.yProperty().bind(this.internalYProperty);
            this.groups.add(group);
        }

        public void add(final GraphicalNode node) {
            if (null != this.node) throw new Error("Node already set");
            this.node = node;
            node.layoutXProperty()
                    .bind(this.internalXProperty.add(this.widthProperty.subtract(node.widthProperty()).divide(2)));
            node.layoutYProperty().bind(this.internalYProperty.add(this.internalHeightProperty).add(Y_SPACER));
            this.heightProperty.bind(this.internalHeightProperty.add(Y_SPACER).add(node.heightProperty()));
            this.nv.add(node);
            for (GroupInterface group : this.groups) {
                final Link line = new Link(group.getOutput(), node.getInput());
                this.nv.add(line);
            }
            this.bindHeight();
            this.bindWidth();
            if (node.isShared()) {
                final String name = NetworkVisualisation.this.shared2Names
                        .computeIfAbsent(node.getNode(), n -> "" + NetworkVisualisation.this.sharedCounter++);
                final Rectangle frame = new Rectangle();
                frame.setStroke(Color.BLACK);
                frame.setStrokeWidth(STROKE_WIDTH);
                frame.setFill(null);
                frame.layoutXProperty().bind(this.xProperty());
                frame.layoutYProperty().bind(this.yProperty());
                frame.widthProperty().bind(this.widthProperty());
                this.heightProperty.bind(this.internalHeightProperty.add(Y_SPACER).add(node.heightProperty()).add(20));
                frame.heightProperty().bind(this.heightProperty());
                this.internalXProperty.bind(this.xProperty.add(10));
                this.internalYProperty.bind(this.yProperty.add(10));
                final Text text = new Text(name);
                text.setFont(new Font(20));
                text.layoutXProperty().bind(this.xProperty.add(10));
                text.layoutYProperty().bind(this.yProperty.add(this.heightProperty).subtract(10));
                this.nv.add(frame);
                this.nv.add(text);
            }
        }

        @Override
        public DoubleProperty heightProperty() {
            return this.heightProperty;
        }

        @Override
        public DoubleProperty widthProperty() {
            return this.widthProperty;
        }

        @Override
        public DoubleProperty xProperty() {
            return this.xProperty;
        }

        @Override
        public DoubleProperty yProperty() {
            return this.yProperty;
        }
    }

    private final Map<Node, Group> node2Group = new HashMap<>();

    private final Map<Node, String> shared2Names = new HashMap<>();

    private int sharedCounter = 1;

    public GroupInterface visualizeNode(final Node node, final NetworkVisualisation nv) {
        if (this.unifyShared) {
            final Group nodeGroup = this.node2Group.get(node);
            if (null != nodeGroup) return new SharedGroup(nodeGroup);
        }
        {
            final Group nodeGroup = node.accept(new NodeVisualiser(nv)).nodeGroup;
            this.node2Group.put(node, nodeGroup);
            return nodeGroup;
        }
    }

    public void clear() {
        this.getChildren().clear();
        this.node2Group.clear();
    }

    private final class NodeVisualiser implements NodeVisitor {

        private final NetworkVisualisation nv;
        private final Group nodeGroup;

        private NodeVisualiser(final NetworkVisualisation nv) {
            this.nodeGroup = new Group(nv);
            this.nv = nv;
        }

        @Override
        public void visit(final AlphaNode alphaNode) {
            for (Edge edge : alphaNode.getIncomingEdges()) {
                this.nodeGroup.add(visualizeNode(edge.getSourceNode(), this.nv));
            }
            this.nodeGroup.add(new GraphicalNode.Alpha(alphaNode));
        }

        @Override
        public void visit(final BetaNode betaNode) {
            for (Edge edge : betaNode.getIncomingEdges()) {
                this.nodeGroup.add(visualizeNode(edge.getSourceNode(), this.nv));
            }
            this.nodeGroup.add(new GraphicalNode.Beta(betaNode));
        }

        @Override
        public void visit(final ObjectTypeNode otn) {
            this.nodeGroup.add(new GraphicalNode.OTN());
        }
    }
}
