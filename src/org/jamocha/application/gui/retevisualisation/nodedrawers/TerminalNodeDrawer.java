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
import java.util.List;

import org.jamocha.engine.nodes.Node;

public class TerminalNodeDrawer extends AbstractNodeDrawer {

	public TerminalNodeDrawer(final Node owner) {
		super(owner);
	}

	@Override
	protected void drawNode(final int x, final int y, final int height,
			final int width, final int halfLineHeight,
			final List<Node> selected, final Graphics2D canvas) {
		final int alpha = selected.contains(node) ? 255 : 20;
		canvas.setColor(new Color(0, 0, 0, alpha));
		canvas.fillRect(x, y, width, height);
		canvas.setColor(new Color(40, 40, 40, alpha));
		canvas.drawRect(x, y, width, height);
		canvas.setColor(new Color(255, 255, 255, alpha));
		drawId(x, y, height, width, halfLineHeight, canvas);
	}
}
