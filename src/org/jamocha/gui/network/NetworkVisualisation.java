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

import org.jamocha.dn.Network;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.RootNode;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class NetworkVisualisation extends Region {

	final Network network;

	public NetworkVisualisation(Network network) {
		super();
		this.network = network;
	}

	public void update() {
		RootNode rootNode = network.getRootNode();
		VBox vBox = new VBox(50);
		vBox.setPadding(new Insets(50));
		vBox.setAlignment(Pos.CENTER);
		RootNodeVisualisation rootNodeVisualisation = new RootNodeVisualisation();
		vBox.getChildren().add(rootNodeVisualisation);
		this.getChildren().add(vBox);
		HBox hBox = new HBox(50);
		vBox.getChildren().add(hBox);
		for (ObjectTypeNode objectTypeNode : rootNode.getOTNs()) {
			OTNVisualisation otnVisualisation = new OTNVisualisation();
			vBox.getChildren().add(otnVisualisation);
		}
	}

	private class RootNodeVisualisation extends Ellipse {

		public RootNodeVisualisation() {
			super(100, 40);
		}

	}

	private class OTNVisualisation extends Polygon {

		public OTNVisualisation() {
			super(0,40,40,0,80,40,40,80);
		}

	}

}
