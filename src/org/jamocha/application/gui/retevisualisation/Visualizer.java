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

package org.jamocha.application.gui.retevisualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.nodes.TwoInputNode;

public class Visualizer extends JComponent implements ComponentListener,
		MouseInputListener, ViewportChangedListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;

	Node rootNode;

	VisualizerSetup setup;

	protected final Color betaColor = new Color(0, 0, 255, 120);

	protected final Color alphaColor = new Color(255, 0, 0, 120);

	protected final Color betaColorDeselected = new Color(0, 0, 255, 20);

	protected final Color alphaColorDeselected = new Color(255, 0, 0, 20);

	protected Map<Point, Node> point2node;

	protected Map<Node, Point> node2point;

	protected Map<Node, Boolean> isSelectedNode = new HashMap<Node, Boolean>();

	protected int logicalWidth = 0;

	protected int logicalHeight = 0;

	protected List<ViewportChangedListener> viewportChangedListener;

	protected boolean viewportChangeByClick = false;

	protected boolean autoScale = false;

	protected Map<Node, Integer> rowHints;

	protected boolean showSelection = false;

	protected Visualizer selectionRelativeTo;

	protected boolean pressed;

	protected ClickListener clickListener;

	protected Map<Node, List<String>> usedForRules;

	protected List<String> selectedRules = new ArrayList<String>();

	protected List<Node> selectedNodes;

	protected Point pressPos;

	protected int halfLineHeight = 20;

	protected Point offsetWhenPressed;

	protected int linestyle = VisualizerSetup.QUARTERELLIPSE;

	protected boolean rightScroll;

	public void computeRowHints() {
		rowHints.clear();
		int actLvl = 0;
		Stack<Node> activeLevel = new Stack<Node>();
		Stack<Node> nextLevel;
		activeLevel.add(rootNode);
		while (!activeLevel.isEmpty()) {
			nextLevel = new Stack<Node>();
			for (final Node node : activeLevel) {
				rowHints.put(node, actLvl);
				for (final Node child : node.getChildNodes())
					nextLevel.push(child);
			}
			actLvl += 1;
			logicalWidth = Math.max(logicalWidth, activeLevel.size());
			activeLevel = nextLevel;
		}
		logicalHeight = actLvl;
	}

	public void reload() {
		computeRowHints();
		calculateSelectedNodes();
		componentResized2(null);
		node2point = new HashMap<Node, Point>();
		rootNode
				.getNodeDrawer()
				.drawNode(
						0,
						selectedNodes,
						(Graphics2D) new BufferedImage(1, 1,
								BufferedImage.TYPE_INT_RGB).getGraphics(),
						setup, node2point, point2node, rowHints, halfLineHeight);

	}

	public void addViewportChangedListener(
			final ViewportChangedListener listener) {
		viewportChangedListener.add(listener);
	}

	protected void callViewportChangedListeners() {
		final ViewportChangeEvent e = new ViewportChangeEvent();
		e.x = -setup.offsetX;
		e.y = -setup.offsetY;
		e.width = (int) (getWidth() / setup.scaleX);
		e.height = (int) (getHeight() / setup.scaleY);
		callViewportChangedListeners(e);
	}

	protected void callViewportChangedListeners(final ViewportChangeEvent e) {
		for (final ViewportChangedListener listener : viewportChangedListener)
			listener.viewportChanged(e);
	}

	public void setLineStyle(final int style) {
		linestyle = style;
		repaint();
	}

	public int getLineStyle() {
		return linestyle;
	}

	public Visualizer(final Engine e) {
		rootNode = e.getNet().getRoot();
		setup = new VisualizerSetup();
		point2node = new HashMap<Point, Node>();
		rowHints = new HashMap<Node, Integer>();
		viewportChangedListener = new ArrayList<ViewportChangedListener>();
		usedForRules = new HashMap<Node, List<String>>();
		reload();
		addComponentListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
	}

	protected Point toPhysical(final Point p, final VisualizerSetup setup) {
		assert (p != null);
		final Point result = new Point(p);
		result.x *= (NodeDrawer.shapeWidth + NodeDrawer.shapeGapWidth) / 2;
		result.x += (NodeDrawer.shapeGapWidth + NodeDrawer.shapeWidth) / 2;
		result.y *= NodeDrawer.shapeHeight + NodeDrawer.shapeGapHeight;
		result.y += (NodeDrawer.shapeGapHeight + NodeDrawer.shapeHeight) / 2;
		result.x += setup.offsetX;
		result.y += setup.offsetY;
		result.x *= setup.scaleX;
		result.y *= setup.scaleY;
		return result;
	}

	protected int atan4(final double y, final double x) {
		double result = Math.atan2(y, x);
		result = result * 180.0 / Math.PI;
		if (result < 0)
			result += 360;
		if (result > 360)
			result -= 360;
		return (int) result;
	}

	protected List<String> calculateSelectedNodesHelper(final Node node) {
		final List<String> result = new ArrayList<String>();

		if (node instanceof TerminalNode) {
			final TerminalNode n = (TerminalNode) node;
			result.add(n.getRule().getName());
		} else
			for (final Node child : node.getChildNodes()) {
				final List<String> childResult = calculateSelectedNodesHelper(child);
				result.addAll(childResult);
			}
		usedForRules.put(node, result);
		return result;
	}

	protected void calculateSelectedNodes() {
		calculateSelectedNodesHelper(rootNode);

		selectedNodes = new ArrayList<Node>();

		final Stack<Node> nodes = new Stack<Node>();
		nodes.add(rootNode);

		while (!nodes.isEmpty()) {
			final Node act = nodes.pop();

			for (final Node child : act.getChildNodes())
				nodes.add(child);

			if (isNodeSelected(act))
				selectedNodes.add(act);

		}

	}

	public boolean isNodeSelected(final Node node) {
		final List<String> rules = usedForRules.get(node);
		for (final String noderule : rules)
			for (final String selected : selectedRules)
				if (noderule.equals(selected))
					return true;
		return false;
	}

	protected void setSelectedRules(final List<String> selected) {
		selectedRules = selected;
		calculateSelectedNodes();
		repaint();
	}

	protected void drawConnectionLines(final Node root,
			final Map<Node, Point> positions, final Graphics2D canvas,
			final boolean selected, final boolean unselected) {
		for (final Node child : root.getChildNodes()) {
			Point childPos = positions.get(child);
			Point rootPos = positions.get(root);
			if (childPos == null || rootPos == null) {
				reload();
				return;
			}
			rootPos = toPhysical(rootPos, setup);
			childPos = toPhysical(childPos, setup);

			if (isNodeSelected(child) && selected || !isNodeSelected(child)
					&& unselected) {

				if (isNodeSelected(child)) {
					if (root instanceof TwoInputNode
							|| root instanceof LeftInputAdaptorNode)
						canvas.setColor(betaColor);
					else
						canvas.setColor(alphaColor);
				} else if (root instanceof TwoInputNode
						|| root instanceof LeftInputAdaptorNode)
					canvas.setColor(betaColorDeselected);
				else
					canvas.setColor(alphaColorDeselected);

				if (linestyle == VisualizerSetup.QUARTERELLIPSE) {
					if (childPos.x == rootPos.x)
						rootPos = root.getNodeDrawer().getVerticalEndPoint(
								childPos, rootPos, setup);
					else
						rootPos = root.getNodeDrawer().getHorizontalEndPoint(
								childPos, rootPos, setup);
					if (childPos.y == rootPos.y)
						childPos = child.getNodeDrawer().getHorizontalEndPoint(
								rootPos, childPos, setup);
					else
						childPos = child.getNodeDrawer().getVerticalEndPoint(
								rootPos, childPos, setup);
					int arcX, arcY, midX, midY, w, h;
					w = Math.abs(rootPos.x - childPos.x);
					if (rootPos.y < childPos.y) {
						h = childPos.y - rootPos.y;
						midX = rootPos.x;
						midY = childPos.y;
						arcX = midX - w;
						arcY = midY - h;
					} else {
						h = -childPos.y + rootPos.y;
						midY = rootPos.y;
						midX = childPos.x;
						arcX = midX - w;
						arcY = midY - h;
					}
					h *= 2;
					w *= 2;
					final int originToRootX = rootPos.x - midX;
					int originToRootY = rootPos.y - midY;
					final int originToChildX = childPos.x - midX;
					int originToChildY = childPos.y - midY;
					final int startAngle = atan4(-originToRootY, originToRootX);
					final int arcAngle = atan4(-originToChildY, originToChildX)
							- startAngle;
					canvas.drawArc(arcX, arcY, w, h, startAngle, arcAngle);
				} else if (linestyle == VisualizerSetup.LINE) {
					rootPos = root.getNodeDrawer().getLineEndPoint(childPos,
							rootPos, setup);
					childPos = child.getNodeDrawer().getLineEndPoint(rootPos,
							childPos, setup);
					canvas.drawLine(rootPos.x, rootPos.y, childPos.x,
							childPos.y);

				}

				final double angle = Math.atan2((childPos.y - rootPos.y),
						(childPos.x - rootPos.x));
				drawArrowHead(childPos.x, childPos.y, angle, canvas);
			}
			drawConnectionLines(child, positions, canvas, selected, unselected);
		}
	}

	protected void drawArrowHead(final int x, final int y, final double angle,
			final Graphics2D canvas) {
		final int width = (int) (8 * setup.scaleX);
		final int height = (int) (8 * setup.scaleY);
		canvas.fillOval(x - width / 2, y - width / 2, width + 1, height + 1);
	}

	protected void loadGoodFont(final Graphics2D g) {
		final int allowedHeight = (int) (NodeDrawer.shapeHeight * setup.scaleY * .9);
		final int dpi = getToolkit().getScreenResolution();
		final double allowedInches = (double) allowedHeight / (double) dpi;
		final double allowedPoints = allowedInches * 72; // see point
		// definition
		final Font goodFont = new Font("Helvetica", Font.BOLD,
				(int) allowedPoints);
		g.setFont(goodFont);
		halfLineHeight = allowedHeight / 2;
	}

	@Override
	public void paint(final Graphics g) {
		point2node.clear();
		final Graphics2D canvas = (Graphics2D) g;
		canvas.setColor(Color.white);
		canvas.fillRect(0, 0, getWidth(), getHeight());

		final BasicStroke widthOneStroke = new BasicStroke(1 * setup.scaleX);
		canvas.setStroke(widthOneStroke);

		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		drawConnectionLines(rootNode, node2point, canvas, false, true);

		loadGoodFont(canvas);
		rootNode.getNodeDrawer().drawNode(0, selectedNodes, canvas, setup,
				node2point, point2node, rowHints, halfLineHeight);

		canvas.setStroke(widthOneStroke);

		drawConnectionLines(rootNode, node2point, canvas, true, false);

		if (showSelection) {
			final VisualizerSetup s = selectionRelativeTo.setup;
			final int x = (int) ((-s.offsetX + selectionRelativeTo.getWidth()
					/ s.scaleX / 2) * setup.scaleX);
			final int y = (int) ((-s.offsetY + selectionRelativeTo.getHeight()
					/ s.scaleY / 2) * setup.scaleY);
			final int w = (int) (selectionRelativeTo.getWidth() / s.scaleX * setup.scaleX);
			final int h = (int) (selectionRelativeTo.getHeight() / s.scaleY * setup.scaleY);
			g.setColor(new Color(100, 100, 255, 100));
			g.fillRect(x - w / 2, y - h / 2, w + 1, h + 1);
		}
	}

	public void enableShowSelection(final boolean enable) {
		showSelection = enable;
		repaint();
	}

	protected Point getLogicalPosition(int x, int y) {
		final Point result = new Point();

		x /= setup.scaleX;
		y /= setup.scaleY;

		x -= setup.offsetX;
		y -= setup.offsetY;

		result.y = y / (NodeDrawer.shapeGapHeight + NodeDrawer.shapeHeight);
		result.x = x / ((NodeDrawer.shapeGapWidth + NodeDrawer.shapeWidth) / 2);

		return result;
	}

	@Override
	public String getToolTipText(final MouseEvent event) {
		final int x = event.getX();
		final int y = event.getY();
		final Node node = point2node.get(getLogicalPosition(x, y));
		if (node == null)
			return null;
		return "<html>" + node.toString().replace("\n", "<br>") + "</html>";
	}

	public void enableToolTips(final boolean enable) {
		if (enable)
			setToolTipText(" ");
		else
			setToolTipText(null);
	}

	public void enableAutoScale(final boolean enable) {
		autoScale = enable;
	}

	public void componentResized2(final ComponentEvent arg0) {
		if (autoScale) {
			final int w = getWidth();
			final int h = getHeight();
			final int graphWidth = logicalWidth
					* (NodeDrawer.shapeWidth + NodeDrawer.shapeGapWidth);
			final int graphHeight = logicalHeight
					* (NodeDrawer.shapeHeight + NodeDrawer.shapeGapHeight);
			final double scaleW = (double) w / (double) graphWidth;
			final double scaleH = (double) h / (double) graphHeight;
			setup.scaleX = setup.scaleY = (float) Math.min(scaleW, scaleH);
			if (setup.scaleX == 0.0)
				setup.scaleX = setup.scaleY = 1.0f;
			repaint();
		}
	}

	public void componentResized(final ComponentEvent arg0) {
		componentResized2(arg0);
		callViewportChangedListeners();
	}

	public void enableViewportByClick(final boolean enable,
			final Visualizer other) {
		viewportChangeByClick = enable;
		selectionRelativeTo = other;
	}

	public void componentHidden(final ComponentEvent e) {
	}

	public void componentMoved(final ComponentEvent e) {
	}

	public void componentShown(final ComponentEvent e) {
	}

	protected void changeViewport(final MouseEvent ev) {
		if (ev.getButton() != MouseEvent.BUTTON1)
			return;
		double x = ev.getX();
		double y = ev.getY();

		x /= setup.scaleX;
		y /= setup.scaleY;

		if (!autoScale) {
			x -= setup.offsetX;
			y -= setup.offsetY;
		}

		final double normalizedWidth = selectionRelativeTo.getWidth()
				/ selectionRelativeTo.setup.scaleX;
		final double normalizedHeight = selectionRelativeTo.getHeight()
				/ selectionRelativeTo.setup.scaleY;

		x -= normalizedWidth / 2;
		y -= normalizedHeight / 2;

		setup.offsetX = (int) x;
		setup.offsetY = (int) y;

	}

	protected void _changeVP(int x, int y) {
		final ViewportChangeEvent ev = new ViewportChangeEvent();

		x -= selectionRelativeTo.getWidth() / selectionRelativeTo.setup.scaleX
				* setup.scaleX / 2;
		y -= selectionRelativeTo.getHeight() / selectionRelativeTo.setup.scaleY
				* setup.scaleY / 2;

		ev.x = (int) -(x / setup.scaleX);
		ev.y = (int) -(y / setup.scaleY);
		callViewportChangedListeners(ev);
		repaint();

	}

	public void mouseClicked(final MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			if (viewportChangeByClick)
				_changeVP(arg0.getX(), arg0.getY());
			else {
				final int x = arg0.getX();
				final int y = arg0.getY();
				final Node node = point2node.get(getLogicalPosition(x, y));
				if (node != null) {
					final String description = node.toString();
					clickListener.nodeClicked(description);
				}
			}
		} else if (arg0.getButton() == MouseEvent.BUTTON2) {
			final float margin = 5.0f;
			final Point lp = node2point.get(point2node.get(getLogicalPosition(
					arg0.getX(), arg0.getY())));
			int midX = NodeDrawer.shapeGapWidth / 2 + lp.x
					* (NodeDrawer.shapeGapWidth + NodeDrawer.shapeWidth) / 2
					+ NodeDrawer.shapeWidth / 2;
			int midY = NodeDrawer.shapeGapHeight / 2 + lp.y
					* (NodeDrawer.shapeGapHeight + NodeDrawer.shapeHeight)
					+ NodeDrawer.shapeHeight / 2;
			final float scaleX = getWidth() / (NodeDrawer.shapeWidth * margin);
			final float scaleY = getHeight()
					/ (NodeDrawer.shapeHeight * margin);
			final float scale = Math.min(scaleX, scaleY);
			midX -= getWidth() / scale / 2;
			midY -= getHeight() / scale / 2;
			setup.scaleX = scale;
			setup.scaleY = scale;
			setup.offsetX = -midX;
			setup.offsetY = -midY;
			correctOffsets();
			callViewportChangedListeners();
			repaint();
		}
	}

	public void mouseEntered(final MouseEvent arg0) {
	}

	public void mouseExited(final MouseEvent arg0) {
	}

	public void mousePressed(final MouseEvent arg0) {
		pressPos = arg0.getPoint();
		offsetWhenPressed = new Point(setup.offsetX, setup.offsetY);
		if (!viewportChangeByClick && arg0.getButton() == MouseEvent.BUTTON3)
			rightScroll = true;
	}

	public void mouseReleased(final MouseEvent arg0) {
		rightScroll = false;
	}

	public void mouseDragged(final MouseEvent arg0) {
		if (viewportChangeByClick)
			_changeVP(arg0.getX(), arg0.getY());
		else if (rightScroll) {
			final int altOffX = offsetWhenPressed.x;
			final int altOffY = offsetWhenPressed.y;
			int offsetOffsetX = arg0.getPoint().x - pressPos.x;
			int offsetOffsetY = arg0.getPoint().y - pressPos.y;
			offsetOffsetX /= setup.scaleX;
			offsetOffsetY /= setup.scaleY;
			setup.offsetX = altOffX + offsetOffsetX;
			setup.offsetY = altOffY + offsetOffsetY;
			correctOffsets();
			repaint();
			callViewportChangedListeners();
		}

	}

	public void mouseMoved(final MouseEvent arg0) {

	}

	protected void correctOffsets() {
		final int maxOffsetX = (int) (-logicalWidth
				* (NodeDrawer.shapeGapWidth + NodeDrawer.shapeWidth) + getWidth()
				/ setup.scaleX);
		final int maxOffsetY = (int) (-logicalHeight
				* (NodeDrawer.shapeGapHeight + NodeDrawer.shapeHeight) + getHeight()
				/ setup.scaleY);

		if (setup.offsetX < maxOffsetX)
			setup.offsetX = maxOffsetX;
		if (setup.offsetY < maxOffsetY)
			setup.offsetY = maxOffsetY;

		if (setup.offsetX > 0)
			setup.offsetX = 0;
		if (setup.offsetY > 0)
			setup.offsetY = 0;
	}

	public void viewportChanged(final ViewportChangeEvent e) {

		if (!autoScale) {

			setup.offsetX = e.x;
			setup.offsetY = e.y;

			correctOffsets();
		}
		repaint();
	}

	public void setClickListener(final ClickListener cl) {
		clickListener = cl;
	}

	protected void zoom(final double valueX, final double valueY) {
		// is always absolute
		int midX = (int) (-setup.offsetX + getWidth() / setup.scaleX / 2.0);
		int midY = (int) (-setup.offsetY + getHeight() / setup.scaleY / 2.0);

		setup.scaleX = (float) valueX;
		setup.scaleY = (float) valueY;

		if (setup.scaleX < 0.1)
			setup.scaleX = 0.1f;
		if (setup.scaleY < 0.1)
			setup.scaleY = 0.1f;
		if (setup.scaleX > 10)
			setup.scaleX = 10;
		if (setup.scaleY > 10)
			setup.scaleY = 10;

		setup.offsetX = (int) (-midX + getWidth() / setup.scaleX / 2.0);
		setup.offsetY = (int) (-midY + getHeight() / setup.scaleY / 2.0);
		correctOffsets();
	}

	protected void zoom(final double valueX, final double valueY,
			final boolean relative) {
		if (relative)
			zoom(valueX * setup.scaleX, valueY * setup.scaleY);
		else
			zoom(valueX, valueY);
		callViewportChangedListeners();
		repaint();
	}

	public void mouseWheelMoved(final MouseWheelEvent arg0) {
		if (autoScale)
			return;
		final int dir = arg0.getWheelRotation();
		double d = 1;
		if (dir > 0)
			d = 1.1;
		else
			d = 0.9;
		zoom(d, d, true);
	}

}
