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
package org.jamocha.application.gui.retevisualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.VisualizerSetup;
import org.jamocha.engine.nodes.Node;

public class RootNodeDrawer extends AbstractNodeDrawer {

	public RootNodeDrawer(final Node owner) {
		super(owner);
	}

	@Override
	protected void drawNode(final int x, final int y, final int height,
			final int width, final int halfLineHeight,
			final List<Node> selected, final Graphics2D canvas) {
		final int alpha = selected.contains(node) ? 255 : 20;
		canvas.setBackground(new Color(0, 0, 0, alpha));
		canvas.setColor(new Color(40, 40, 40, alpha));
		canvas.fillOval(x, y, width, height);
		canvas.drawOval(x, y, width, height);
		canvas.setColor(new Color(255, 255, 255, alpha));
		drawId(x, y, height, width, halfLineHeight, canvas);
	}

	@Override
	public Point getLineEndPoint(final Point target, final Point me,
			final VisualizerSetup setup) {
		final double angle = atan3(-target.y + me.y, target.x - me.x);

		final double unitCircleX = Math.cos(angle);
		double unitCircleY = Math.sin(angle);

		final Point result = new Point();
		result.x = (int) (unitCircleX * NodeDrawer.shapeWidth * setup.scaleX
				/ 2 + me.x);
		result.y = (int) (-unitCircleY * NodeDrawer.shapeHeight * setup.scaleY
				/ 2 + me.y);
		return result;
	}

}
