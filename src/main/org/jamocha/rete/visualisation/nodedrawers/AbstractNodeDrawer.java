package org.jamocha.rete.visualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.VisualizerSetup;

public class AbstractNodeDrawer implements NodeDrawer {

	protected Node node;
	
	public AbstractNodeDrawer(Node owner) {
		node = owner;
	}
	
	/**
	 * this method draws itself onto a Graphics2D canvas. The alternative is to
	 * extend JComponent here, but imho, that would be overkill here.
	 * Furthermore, that would be conceptionally wrong, since the JComponent
	 * representation is not the major thing in the BaseNode class. It is
	 * protected, since it will be called from a higher-level (public) method
	 * inside BaseNode.
	 */
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<Node> selected, Graphics2D canvas) {
		int alpha = (selected.contains(this.node)) ? 255 : 20;
		canvas.setColor(new Color(255, 40, 40, alpha));
		canvas.fillRect(x, y, width, height);
		canvas.setColor(new Color(200, 15, 15, alpha));
		canvas.drawRect(x, y, width, height);
		canvas.setColor(new Color(0, 0, 0, alpha));
		drawId(x, y, height, width, halfLineHeight, canvas);
	}

	protected void drawId(int x, int y, int height, int width, int halfLineHeight, Graphics2D canvas) {
		if (height < 12)
			return;
		String text = String.valueOf(node.getId());
		int halfLineWidth = canvas.getFontMetrics().stringWidth(text) / 2;
		int paintX = x + width / 2 - halfLineWidth;
		int paintY = y + height / 2 + halfLineHeight;
		canvas.drawString(text, paintX, paintY);
	}

	public final static int shapeWidth = 64;

	public final static int shapeHeight = 24;

	public final static int shapeGapWidth = 25;

	public final static int shapeGapHeight = 25;

	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point bottomLeft = new Point(-shapeWidth / 2, -shapeHeight / 2);

	protected static Point bottomRight = new Point(shapeWidth / 2, -shapeHeight / 2);

	protected static Point topLeft = new Point(-shapeWidth / 2, shapeHeight / 2);

	protected static Point topRight = new Point(shapeWidth / 2, shapeHeight / 2);

	protected static double angleTopLeft = atan3(topLeft.y, topLeft.x);

	protected static double angleTopRight = atan3(topRight.y, topRight.x);

	protected static double angleBottomLeft = atan3(bottomLeft.y, bottomLeft.x);

	protected static double angleBottomRight = atan3(bottomRight.y, bottomRight.x);

	/**
	 * draws a node. here, the row gives a logical y position (1 for first row,
	 * 2 for second and so on) and the column is analog but only the half width
	 * is one unit here.
	 * 
	 * @param row
	 *            the row to paint (from 0 to infinite)
	 * @param fromColumn
	 *            the first column you can paint
	 * @param alpha
	 *            alpha value
	 * @param canvas
	 *            the canvas
	 * @param setup
	 *            the setup
	 * @return the width
	 */
	public int drawNode(int fromColumn, List<Node> selected, Graphics2D canvas, VisualizerSetup setup, Map<Node, Point> positions, Map<Point, Node> p2n, Map<Node, Integer> rowHints,
			int halfLineHeight) {
		int firstColumn = fromColumn;
		Integer row = rowHints.get(this.node);
		if (row == null ) row =0;
		for (Node child : node.getChildNodes()) {
			// only draw the child node, iff i am the "primary parent"
			if (!(child.getParentNodes()[0] == node))
				continue;
			firstColumn += child.getNodeDrawer().drawNode(firstColumn, selected, canvas, setup, positions, p2n, rowHints, halfLineHeight);
		}
		int width = firstColumn - fromColumn;
		if (width == 0)
			width = 2;
		// calculate real positions and draw them
		int column = fromColumn + width / 2 - 1;
		int y = (shapeHeight + shapeGapHeight) * row + shapeGapHeight / 2;
		int x = ((shapeWidth + shapeGapWidth) / 2) * column + shapeGapWidth / 2;
		x += setup.offsetX;
		y += setup.offsetY;
		x *= setup.scaleX;
		y *= setup.scaleY;
		int w = shapeWidth;
		int h = shapeHeight;
		h *= setup.scaleY;
		w *= setup.scaleX;
		Point p1 = new Point(column, row);
		Point p2 = new Point(column + 1, row);
		positions.put(node, p1);
		p2n.put(p1, node);
		p2n.put(p2, node);
		drawNode(x, y, h, w, halfLineHeight, selected, canvas);
		//
		return width;
	}

	protected static Point intersectionPoint(Point l1p1, Point l1p2, Point l2p1, Point l2p2) {
		Point result = new Point();
		double x1 = l1p1.x;
		double x2 = l1p2.x;
		double x3 = l2p1.x;
		double x4 = l2p2.x;
		double y1 = l1p1.y;
		double y2 = l1p2.y;
		double y3 = l2p1.y;
		double y4 = l2p2.y;
		double denom = ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
		double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
		result.x = (int) (x1 + ua * (x2 - x1));
		result.y = (int) (y1 + ua * (y2 - y1));
		return result;
	}

	public static double atan3(double y, double x) {
		double result = Math.atan2(y, x);
		if (result < 0)
			result += 2 * Math.PI;
		return result;
	}

	public Point getHorizontalEndPoint(Point target, Point me, VisualizerSetup setup) {
		Point target2 = new Point(target.x, me.y);
		return getLineEndPoint(target2, me, setup);
	}

	public Point getVerticalEndPoint(Point target, Point me, VisualizerSetup setup) {
		Point target2 = new Point(me.x, target.y);
		return getLineEndPoint(target2, me, setup);
	}

	public Point getLineEndPoint(Point target, Point me, VisualizerSetup setup) {
		return getLineEndPoint2(target, me, setup, angleTopRight, angleTopLeft, angleBottomRight, angleBottomLeft, topRight, topLeft, bottomRight, bottomLeft);
	}

	public Point getLineEndPoint2(Point target, Point me, VisualizerSetup setup, double atr, double atl, double abr, double abl, Point ptr, Point ptl, Point pbr, Point pbl) {
		double angle = atan3(-target.y + me.y, target.x - me.x);
		Point p1;
		Point p2;
		if (angle < atr || angle >= abr) {
			// RIGHT SIDE
			p1 = ptr;
			p2 = pbr;
		} else if (angle < atl) {
			// TOP SIDE
			p1 = ptl;
			p2 = ptr;
		} else if (angle < abl) {
			// LEFT SIDE
			p1 = ptl;
			p2 = pbl;
		} else {
			// BOTTOM SIDE
			p1 = pbr;
			p2 = pbl;
		}

		Point pp1 = new Point(p1);
		Point pp2 = new Point(p2);

		pp1.x *= setup.scaleX;
		pp1.y *= setup.scaleY;
		pp2.x *= setup.scaleX;
		pp2.y *= setup.scaleY;

		pp1.x += me.x;
		pp1.y = me.y - pp1.y;
		pp2.x += me.x;
		pp2.y = me.y - pp2.y;

		return intersectionPoint(pp1, pp2, target, me);
	}

	

}
