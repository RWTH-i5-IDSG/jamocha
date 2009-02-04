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
import java.awt.Image;
import java.awt.MediaTracker;
import java.util.List;

import javax.swing.ImageIcon;

import org.jamocha.Constants;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.ObjectTypeNode;

public class ObjectTypeNodeDrawer extends AbstractNodeDrawer {

	public ObjectTypeNodeDrawer() {
		super();
	}

	@Override
	protected void drawNode(Node node, final int x, final int y, final int height,
			final int width, final int halfLineHeight,
			final List<Node> selected, final Graphics2D canvas) {
		final boolean isSelected = selected.contains(node);
		final int alpha = isSelected ? 255 : 20;
		canvas.setColor(new Color(255, 215, 15, alpha));
		canvas.fillRect(x, y, width, height);
		canvas.setColor(new Color(208, 181, 44, alpha));
		canvas.drawRect(x, y, width, height);
		canvas.setColor(new Color(0, 0, 0, alpha));
		drawId(node, x, y, height, width, halfLineHeight, canvas);
	}

}
