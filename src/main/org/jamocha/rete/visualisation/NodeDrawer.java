package org.jamocha.rete.visualisation;

import jade.core.BaseNode;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.nodes.Node;


public interface NodeDrawer {

	final static int shapeWidth = 64;

	final static int shapeHeight = 24;

	final static int shapeGapWidth = 25;

	final static int shapeGapHeight = 25;
	
	int drawNode(int fromColumn, List<Node> selected, Graphics2D canvas, VisualizerSetup setup, Map<Node,Point> positions, Map<Point,Node> p2n, Map<Node,Integer> rowHints, int halfLineHeight);

	Point getHorizontalEndPoint(Point target, Point me, VisualizerSetup setup);
	
	Point getVerticalEndPoint(Point target, Point me, VisualizerSetup setup);
	
	Point getLineEndPoint(Point target, Point me, VisualizerSetup setup);
	
	Point getLineEndPoint2(Point target, Point me, VisualizerSetup setup, double atr, double atl, double abr, double abl, Point ptr, Point ptl, Point pbr, Point pbl);

}
