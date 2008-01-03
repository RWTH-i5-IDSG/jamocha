package org.jamocha.rete.visualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.visualisation.VisualizerSetup;

public class QuantorBetaFilterNodeDrawer extends AbstractNodeDrawer {

	public QuantorBetaFilterNodeDrawer(Node owner) {
		super(owner);
	}
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<Node> selected, Graphics2D canvas) {
		int alpha = (selected.contains(this.node)) ? 255 : 20;
		int[] xpoints = { x, x + width, (int) (x + width * 0.8), x + (int) (width * 0.2) };
		int[] ypoints = { y + height, y + height, y, y };
		canvas.setColor(new Color(0, 255, 200, alpha));
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.setColor(new Color(54, 208, 130, alpha));
		canvas.drawPolygon(xpoints, ypoints, 4);
		canvas.setColor(new Color(0, 0, 0, alpha));
		drawId(x, y, height, width, halfLineHeight, canvas);
		
		
	}

	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point topLeft = new Point((int) (-shapeWidth / 2.8), shapeHeight / 2);

	protected static Point topRight = new Point((int) (shapeWidth / 2.8), shapeHeight / 2);

	protected static Point bottomLeft = new Point(-shapeWidth / 2, -shapeHeight / 2);

	protected static Point bottomRight = new Point(shapeWidth / 2, -shapeHeight / 2);

	protected static double angleTopLeft = atan3(topLeft.y, topLeft.x);

	protected static double angleTopRight = atan3(topRight.y, topRight.x);

	protected static double angleBottomLeft = atan3(bottomLeft.y, bottomLeft.x);

	protected static double angleBottomRight = atan3(bottomRight.y, bottomRight.x);

	public Point getLineEndPoint(Point target, Point me, VisualizerSetup setup) {
		return getLineEndPoint2(target, me, setup, angleTopRight, angleTopLeft, angleBottomRight, angleBottomLeft, topRight, topLeft, bottomRight, bottomLeft);
	}

}
