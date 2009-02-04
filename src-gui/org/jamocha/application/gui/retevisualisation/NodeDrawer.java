/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.application.gui.retevisualisation;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.jamocha.engine.nodes.Node;

public interface NodeDrawer {

	final static int shapeWidth = 64;

	final static int shapeHeight = 24;

	final static int shapeGapWidth = 25;

	final static int shapeGapHeight = 25;

	int drawNode(Node node, int fromColumn, List<Node> selected, Graphics2D canvas,
			VisualizerSetup setup, Map<Node, Point> positions,
			Map<Point, Node> p2n, Map<Node, Integer> rowHints,
			int halfLineHeight);

	Point getHorizontalEndPoint(Point target, Point me, VisualizerSetup setup);

	Point getVerticalEndPoint(Point target, Point me, VisualizerSetup setup);

	Point getLineEndPoint(Point target, Point me, VisualizerSetup setup);

	Point getLineEndPoint2(Point target, Point me, VisualizerSetup setup,
			double atr, double atl, double abr, double abl, Point ptr,
			Point ptl, Point pbr, Point pbl);

}
