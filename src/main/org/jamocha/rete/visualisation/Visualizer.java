package org.jamocha.rete.visualisation;

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

import org.jamocha.rete.Rete;
import org.jamocha.rete.nodes.AbstractBeta;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.LIANode;
import org.jamocha.rete.nodes.TerminalNode;

public class Visualizer extends JComponent implements ComponentListener, MouseInputListener, ViewportChangedListener, MouseWheelListener {

	BaseNode rootNode;

	VisualizerSetup setup;

	protected final Color betaColor = new Color(0, 0, 255, 120);

	protected final Color alphaColor = new Color(255, 0, 0, 120);

	protected final Color betaColorDeselected = new Color(0, 0, 255, 20);

	protected final Color alphaColorDeselected = new Color(255, 0, 0, 20);

	protected Map<Point, BaseNode> point2node;

	protected Map<BaseNode, Point> node2point;

	protected Map<BaseNode, Boolean> isSelectedNode = new HashMap<BaseNode, Boolean>();

	protected int logicalWidth = 0;

	protected int logicalHeight = 0;

	protected List<ViewportChangedListener> viewportChangedListener;

	protected boolean viewportChangeByClick = false;

	protected boolean autoScale = false;

	protected Map<BaseNode, Integer> rowHints;

	protected boolean showSelection = false;

	protected Visualizer selectionRelativeTo;

	protected boolean pressed;

	protected ClickListener clickListener;

	protected Map<BaseNode, List<String>> usedForRules;

	protected List<String> selectedRules = new ArrayList<String>();

	protected List<BaseNode> selectedNodes;

	protected Point pressPos;

	protected int halfLineHeight = 20;

	protected Point offsetWhenPressed;

	protected int linestyle = VisualizerSetup.QUARTERELLIPSE;

	protected boolean rightScroll;

	public void computeRowHints() {
		rowHints.clear();
		int actLvl = 0;
		Stack<BaseNode> activeLevel = new Stack<BaseNode>();
		Stack<BaseNode> nextLevel;
		activeLevel.add(rootNode);
		while (!activeLevel.isEmpty()) {
			nextLevel = new Stack<BaseNode>();
			for (BaseNode node : activeLevel) {
				rowHints.put(node, actLvl);
				for (BaseNode child : node.getChildNodes()) {
					nextLevel.push(child);
				}
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
		node2point = new HashMap<BaseNode, Point>();
		rootNode.drawNode(0, selectedNodes, (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics(), setup, node2point, point2node, rowHints, halfLineHeight);

	}

	public void addViewportChangedListener(ViewportChangedListener listener) {
		viewportChangedListener.add(listener);
	}

	protected void callViewportChangedListeners() {
		ViewportChangeEvent e = new ViewportChangeEvent();
		e.x = -setup.offsetX;
		e.y = -setup.offsetY;
		e.width = (int) (this.getWidth() / setup.scaleX);
		e.height = (int) (this.getHeight() / setup.scaleY);
		callViewportChangedListeners(e);
	}

	protected void callViewportChangedListeners(ViewportChangeEvent e) {
		for (ViewportChangedListener listener : viewportChangedListener)
			listener.viewportChanged(e);
	}

	public void setLineStyle(int style) {
		linestyle = style;
		repaint();
	}

	public Visualizer(Rete e) {
		rootNode = e.getNet().getRoot();
		setup = new VisualizerSetup();
		point2node = new HashMap<Point, BaseNode>();
		rowHints = new HashMap<BaseNode, Integer>();
		viewportChangedListener = new ArrayList<ViewportChangedListener>();
		usedForRules = new HashMap<BaseNode, List<String>>();
		reload();
		this.addComponentListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
	}

	protected Point toPhysical(Point p, VisualizerSetup setup) {
		Point result = new Point(p);
		result.x *= (BaseNode.shapeWidth + BaseNode.shapeGapWidth) / 2;
		result.x += (BaseNode.shapeGapWidth + BaseNode.shapeWidth) / 2;
		result.y *= (BaseNode.shapeHeight + BaseNode.shapeGapHeight);
		result.y += (BaseNode.shapeGapHeight + BaseNode.shapeHeight) / 2;
		result.x += setup.offsetX;
		result.y += setup.offsetY;
		result.x *= setup.scaleX;
		result.y *= setup.scaleY;
		return result;
	}

	protected int atan4(double y, double x) {
		double result = Math.atan2(y, x);
		result = (result * 180.0) / Math.PI;
		if (result < 0)
			result += 360;
		if (result > 360)
			result -= 360;
		return (int) result;
	}

	protected List<String> calculateSelectedNodesHelper(BaseNode node) {
		List<String> result = new ArrayList<String>();

		if (node instanceof TerminalNode) {
			TerminalNode n = (TerminalNode) node;
			result.add((String) n.getRule().getName());
		} else {
			for (BaseNode child : node.getChildNodes()) {
				List<String> childResult = calculateSelectedNodesHelper(child);
				result.addAll(childResult);
			}
		}
		usedForRules.put(node, result);
		return result;
	}

	protected void calculateSelectedNodes() {
		calculateSelectedNodesHelper(rootNode);

		selectedNodes = new ArrayList<BaseNode>();

		Stack<BaseNode> nodes = new Stack<BaseNode>();
		nodes.add(rootNode);

		while (!nodes.isEmpty()) {
			BaseNode act = nodes.pop();

			for (BaseNode child : act.getChildNodes())
				nodes.add(child);

			if (isNodeSelected(act))
				selectedNodes.add(act);

		}

	}

	public boolean isNodeSelected(BaseNode node) {
		List<String> rules = usedForRules.get(node);
		for (String noderule : rules)
			for (String selected : selectedRules)
				if (noderule.equals(selected))
					return true;
		return false;
	}

	protected void setSelectedRules(List<String> selected) {
		selectedRules = selected;
		calculateSelectedNodes();
		repaint();
	}

	protected void drawConnectionLines(BaseNode root, Map<BaseNode, Point> positions, Graphics2D canvas, boolean selected, boolean unselected) {
		for (BaseNode child : root.getChildNodes()) {
			Point childPos = positions.get(child);
			Point rootPos = positions.get(root);
			rootPos = toPhysical(rootPos, setup);
			childPos = toPhysical(childPos, setup);

			if (isNodeSelected(child) && selected || !isNodeSelected(child) && unselected) {

				if (isNodeSelected(child)) {
					if (root instanceof AbstractBeta || root instanceof LIANode) {
						canvas.setColor(betaColor);
					} else {
						canvas.setColor(alphaColor);
					}
				} else {
					if (root instanceof AbstractBeta || root instanceof LIANode) {
						canvas.setColor(betaColorDeselected);
					} else {
						canvas.setColor(alphaColorDeselected);
					}
				}

				if (linestyle == VisualizerSetup.QUARTERELLIPSE) {
					if (childPos.x == rootPos.x) {
						rootPos = root.getVerticalEndPoint(childPos, rootPos, setup);
					} else {
						rootPos = root.getHorizontalEndPoint(childPos, rootPos, setup);
					}
					if (childPos.y == rootPos.y) {
						childPos = child.getHorizontalEndPoint(rootPos, childPos, setup);
					} else {
						childPos = child.getVerticalEndPoint(rootPos, childPos, setup);
					}
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
					int originToRootX = rootPos.x - midX;
					int originToRootY = rootPos.y - midY;
					int originToChildX = childPos.x - midX;
					int originToChildY = childPos.y - midY;
					int startAngle = atan4(-originToRootY, originToRootX);
					int arcAngle = atan4(-originToChildY, originToChildX) - startAngle;
					canvas.drawArc(arcX, arcY, w, h, startAngle, arcAngle);
				} else if (linestyle == VisualizerSetup.LINE) {
					rootPos = root.getLineEndPoint(childPos, rootPos, setup);
					childPos = child.getLineEndPoint(rootPos, childPos, setup);
					canvas.drawLine(rootPos.x, rootPos.y, childPos.x, childPos.y);

				}

				double angle = Math.atan2((childPos.y - rootPos.y), (childPos.x - rootPos.x));
				drawArrowHead(childPos.x, childPos.y, angle, canvas);
			}
			drawConnectionLines(child, positions, canvas, selected, unselected);
		}
	}

	protected void drawArrowHead(int x, int y, double angle, Graphics2D canvas) {
		int width = (int) (8 * setup.scaleX);
		int height = (int) (8 * setup.scaleY);
		canvas.fillOval(x - width / 2, y - width / 2, width + 1, height + 1);
	}

	protected void loadGoodFont(Graphics2D g) {
		int allowedHeight = (int) (BaseNode.shapeHeight * setup.scaleY * .9);
		int dpi = getToolkit().getScreenResolution();
		double allowedInches = ((double) allowedHeight) / ((double) dpi);
		double allowedPoints = allowedInches * 72; // see point definition
		Font goodFont = new Font("Helvetica", Font.BOLD, (int) allowedPoints);
		g.setFont(goodFont);
		halfLineHeight = (int) (allowedHeight / 2);
	}

	public void paint(Graphics g) {
		point2node.clear();
		Graphics2D canvas = (Graphics2D) g;
		canvas.setColor(Color.white);
		canvas.fillRect(0, 0, getWidth(), getHeight());

		BasicStroke widthOneStroke = new BasicStroke(1 * setup.scaleX);
		canvas.setStroke(widthOneStroke);

		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawConnectionLines(rootNode, node2point, canvas, false, true);

		loadGoodFont(canvas);
		rootNode.drawNode(0, selectedNodes, canvas, setup, node2point, point2node, rowHints, halfLineHeight);

		canvas.setStroke(widthOneStroke);

		drawConnectionLines(rootNode, node2point, canvas, true, false);

		if (showSelection) {
			VisualizerSetup s = selectionRelativeTo.setup;
			int x = (int) ((-s.offsetX + ((selectionRelativeTo.getWidth() / s.scaleX) / 2)) * setup.scaleX);
			int y = (int) ((-s.offsetY + ((selectionRelativeTo.getHeight() / s.scaleY) / 2)) * setup.scaleY);
			int w = (int) ((selectionRelativeTo.getWidth() / s.scaleX) * setup.scaleX);
			int h = (int) ((selectionRelativeTo.getHeight() / s.scaleY) * setup.scaleY);
			g.setColor(new Color(100, 100, 255, 100));
			g.fillRect(x - w / 2, y - h / 2, w + 1, h + 1);
		}
	}

	public void enableShowSelection(boolean enable) {
		showSelection = enable;
		repaint();
	}

	protected Point getLogicalPosition(int x, int y) {
		Point result = new Point();

		x /= setup.scaleX;
		y /= setup.scaleY;

		x -= setup.offsetX;
		y -= setup.offsetY;

		result.y = y / (BaseNode.shapeGapHeight + BaseNode.shapeHeight);
		result.x = x / ((BaseNode.shapeGapWidth + BaseNode.shapeWidth) / 2);

		return result;
	}

	public String getToolTipText(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		BaseNode node = point2node.get(getLogicalPosition(x, y));
		if (node == null)
			return null;
		return "<html>" + node.toPPString().replace("\n", "<br>") + "</html>";
	}

	public void enableToolTips(boolean enable) {
		if (enable) {
			this.setToolTipText(" ");
		} else {
			this.setToolTipText(null);
		}
	}

	public void enableAutoScale(boolean enable) {
		autoScale = enable;
	}

	public void componentResized2(ComponentEvent arg0) {
		if (autoScale) {
			int w = this.getWidth();
			int h = this.getHeight();
			int graphWidth = logicalWidth * (BaseNode.shapeWidth + BaseNode.shapeGapWidth);
			int graphHeight = logicalHeight * (BaseNode.shapeHeight + BaseNode.shapeGapHeight);
			double scaleW = ((double) w) / ((double) graphWidth);
			double scaleH = ((double) h) / ((double) graphHeight);
			setup.scaleX = setup.scaleY = (float) Math.min(scaleW, scaleH);
			repaint();
		}
	}

	public void componentResized(ComponentEvent arg0) {
		componentResized2(arg0);
		callViewportChangedListeners();
	}

	public void enableViewportByClick(boolean enable, Visualizer other) {
		viewportChangeByClick = enable;
		selectionRelativeTo = other;
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	protected void changeViewport(MouseEvent ev) {
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

		double normalizedWidth = selectionRelativeTo.getWidth() / selectionRelativeTo.setup.scaleX;
		double normalizedHeight = selectionRelativeTo.getHeight() / selectionRelativeTo.setup.scaleY;

		x -= normalizedWidth / 2;
		y -= normalizedHeight / 2;

		setup.offsetX = (int) x;
		setup.offsetY = (int) y;

	}

	protected void _changeVP(int x, int y) {
		ViewportChangeEvent ev = new ViewportChangeEvent();

		x -= (selectionRelativeTo.getWidth() / selectionRelativeTo.setup.scaleX * setup.scaleX) / 2;
		y -= (selectionRelativeTo.getHeight() / selectionRelativeTo.setup.scaleY * setup.scaleY) / 2;

		ev.x = (int) -(x / setup.scaleX);
		ev.y = (int) -(y / setup.scaleY);
		callViewportChangedListeners(ev);
		repaint();

	}

	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			if (viewportChangeByClick) {
				_changeVP(arg0.getX(), arg0.getY());
			} else {
				int x = arg0.getX();
				int y = arg0.getY();
				BaseNode node = point2node.get(getLogicalPosition(x, y));
				if (node != null) {
					String description = node.toPPString();
					clickListener.nodeClicked(description);
				}
			}
		} else if (arg0.getButton() == MouseEvent.BUTTON2) {
			final float margin = 5.0f;
			Point lp = node2point.get(point2node.get(getLogicalPosition(arg0.getX(), arg0.getY())));
			int midX = (BaseNode.shapeGapWidth / 2) + ((lp.x) * (BaseNode.shapeGapWidth + BaseNode.shapeWidth)) / 2 + (BaseNode.shapeWidth / 2);
			int midY = (BaseNode.shapeGapHeight / 2) + (lp.y) * (BaseNode.shapeGapHeight + BaseNode.shapeHeight) + (BaseNode.shapeHeight / 2);
			float scaleX = getWidth() / (BaseNode.shapeWidth * margin);
			float scaleY = getHeight() / (BaseNode.shapeHeight * margin);
			float scale = Math.min(scaleX, scaleY);
			midX -= (getWidth() / scale) / 2;
			midY -= (getHeight() / scale) / 2;
			setup.scaleX = scale;
			setup.scaleY = scale;
			setup.offsetX = -midX;
			setup.offsetY = -midY;
			correctOffsets();
			callViewportChangedListeners();
			repaint();
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		pressPos = arg0.getPoint();
		offsetWhenPressed = new Point(setup.offsetX, setup.offsetY);
		if (!viewportChangeByClick && arg0.getButton() == MouseEvent.BUTTON3)
			rightScroll = true;
	}

	public void mouseReleased(MouseEvent arg0) {
		rightScroll = false;
	}

	public void mouseDragged(MouseEvent arg0) {
		if (viewportChangeByClick) {
			_changeVP(arg0.getX(), arg0.getY());
		} else {
			if (rightScroll) {
				int altOffX = offsetWhenPressed.x;
				int altOffY = offsetWhenPressed.y;
				int offsetOffsetX = (arg0.getPoint().x - pressPos.x);
				int offsetOffsetY = (arg0.getPoint().y - pressPos.y);
				offsetOffsetX /= setup.scaleX;
				offsetOffsetY /= setup.scaleY;
				setup.offsetX = altOffX + offsetOffsetX;
				setup.offsetY = altOffY + offsetOffsetY;
				correctOffsets();
				repaint();
				callViewportChangedListeners();
			}
		}

	}

	public void mouseMoved(MouseEvent arg0) {

	}

	protected void correctOffsets() {
		int maxOffsetX = (int) (-logicalWidth * (BaseNode.shapeGapWidth + BaseNode.shapeWidth) + getWidth() / setup.scaleX);
		int maxOffsetY = (int) (-logicalHeight * (BaseNode.shapeGapHeight + BaseNode.shapeHeight) + getHeight() / setup.scaleY);

		if (setup.offsetX < maxOffsetX)
			setup.offsetX = maxOffsetX;
		if (setup.offsetY < maxOffsetY)
			setup.offsetY = maxOffsetY;

		if (setup.offsetX > 0)
			setup.offsetX = 0;
		if (setup.offsetY > 0)
			setup.offsetY = 0;
	}

	public void viewportChanged(ViewportChangeEvent e) {

		if (!autoScale) {

			setup.offsetX = e.x;
			setup.offsetY = e.y;

			correctOffsets();
		}
		repaint();
	}

	public void setClickListener(ClickListener cl) {
		this.clickListener = cl;
	}

	protected void zoom(double valueX, double valueY) {
		// is always absolute
		int midX = (int) (-setup.offsetX + (getWidth() / setup.scaleX) / 2.0);
		int midY = (int) (-setup.offsetY + (getHeight() / setup.scaleY) / 2.0);

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

		setup.offsetX = (int) (-midX + (getWidth() / setup.scaleX) / 2.0);
		setup.offsetY = (int) (-midY + (getHeight() / setup.scaleY) / 2.0);
		correctOffsets();
	}

	protected void zoom(double valueX, double valueY, boolean relative) {
		if (relative) {
			zoom(valueX * setup.scaleX, valueY * setup.scaleY);
		} else {
			zoom(valueX, valueY);
		}
		callViewportChangedListeners();
		repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if (autoScale)
			return;
		int dir = arg0.getWheelRotation();
		double d = 1;
		if (dir > 0) {
			d =  1.1;
		} else {
			d =  0.9;
		}
		zoom(d, d, true);
	}

}
