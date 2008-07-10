/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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
package org.jamocha.application.gui.retevisualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.jamocha.application.gui.retevisualisation.VisualizerSetup;
import org.jamocha.engine.nodes.Node;

public class RIANodeDrawer extends AbstractNodeDrawer {

	public RIANodeDrawer(final Node owner) {
		super(owner);
	}

	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point bottomLeft = new Point((int) (-shapeWidth / 2.8),
			-shapeHeight / 2);
	protected static Point bottomRight = new Point((int) (shapeWidth / 2.8),
			-shapeHeight / 2);
	protected static Point topLeft = new Point(-shapeWidth / 2, shapeHeight / 2);
	protected static Point topRight = new Point(shapeWidth / 2, shapeHeight / 2);

	protected static double angleTopLeft = atan3(topLeft.y, topLeft.x);
	protected static double angleTopRight = atan3(topRight.y, topRight.x);
	protected static double angleBottomLeft = atan3(bottomLeft.y, bottomLeft.x);
	protected static double angleBottomRight = atan3(bottomRight.y,
			bottomRight.x);

	@Override
	protected void drawNode(final int x, final int y, final int height,
			final int width, final int halfLineHeight,
			final List<Node> selected, final Graphics2D canvas) {
		final int alpha = selected.contains(node) ? 255 : 20;
		final int[] xpoints = { x, x + width, (int) (x + width * 0.8),
				x + (int) (width * 0.2) };
		final int[] ypoints = { y, y, y + height, y + height };
		canvas.setColor(new Color(28, 255, 252, alpha));
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.setColor(new Color(107, 197, 196, alpha));
		canvas.drawPolygon(xpoints, ypoints, 4);
		canvas.setColor(new Color(0, 0, 0, alpha));
		drawId(x, y, height, width, halfLineHeight, canvas);
	}

	@Override
	public Point getLineEndPoint(final Point target, final Point me,
			final VisualizerSetup setup) {
		return getLineEndPoint2(target, me, setup, angleTopRight, angleTopLeft,
				angleBottomRight, angleBottomLeft, topRight, topLeft,
				bottomRight, bottomLeft);
	}
}
