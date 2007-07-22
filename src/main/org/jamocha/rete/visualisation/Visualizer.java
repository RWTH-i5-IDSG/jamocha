package org.jamocha.rete.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JComponent;

import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemoryImpl;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;

 
public class Visualizer extends JComponent implements ComponentListener{
	
	BaseNode rootNode;
	VisualizerSetup setup;
	protected final BasicStroke widthThreeStroke = new BasicStroke(3);
	protected final Color alphaColor = new Color(0,0,255,120);
	protected final Color betaColor = new Color(255,0,0,120);
	protected Map<Point, BaseNode> point2node;
	protected int logicalWidth = 0;
	protected int logicalHeight = 0;

	protected boolean autoScale = false;
	protected Map<BaseNode,Integer> rowHints;
	
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
			actLvl+=1;
			logicalWidth = Math.max(logicalWidth,activeLevel.size());
			activeLevel = nextLevel;
		}
		logicalHeight = actLvl;
	}
	
	public void reload() {
		computeRowHints();
		
	}
	
	public Visualizer(Rete e) {
		rootNode = ((WorkingMemoryImpl) e.getWorkingMemory()).getRootNode();
		setup = new VisualizerSetup();
		point2node = new HashMap<Point, BaseNode>();
		rowHints = new HashMap<BaseNode, Integer>();
		reload();
		this.addComponentListener(this);
	}
	
	protected Point toPhysical(Point p, VisualizerSetup setup){
		Point result = new Point(p);
		result.x *= (BaseNode.shapeWidth + BaseNode.shapeGapWidth)/2;
		result.x += (BaseNode.shapeGapWidth+ BaseNode.shapeWidth)/2 ;
		result.y *= (BaseNode.shapeHeight+BaseNode.shapeGapHeight);
		result.y += (BaseNode.shapeGapHeight+BaseNode.shapeHeight) /2;
		result.x += setup.offsetX;
		result.y += setup.offsetY;
		result.x *= setup.scaleX;
		result.y *= setup.scaleY;
		return result;
	}
	
	protected int atan4(double y, double x) {
		double result = Math.atan2(y, x);
		result = (result * 180.0 ) / Math.PI;
		if (result<0) result += 360;
		if (result>360) result -=360;
		return (int)result;
	}
	
	protected void drawConnectionLines(BaseNode root, Map<BaseNode,Point> positions, Graphics2D canvas) {
		for (BaseNode child : root.getChildNodes() ) {
			Point rootPos = positions.get(root);
			Point childPos = positions.get(child);
			rootPos = toPhysical(rootPos,setup);
			childPos = toPhysical(childPos,setup);
			childPos = BaseNode.getLineEndPoint(rootPos, childPos, setup);
			rootPos = BaseNode.getLineEndPoint(childPos, rootPos, setup);
			
			if (root instanceof AlphaNode) {
				canvas.setColor( alphaColor );
			} else{
				canvas.setColor( betaColor );
			}
			
			int arcX, arcY,midX, midY, w, h;
			w = Math.abs(rootPos.x - childPos.x);
			if (rootPos.y < childPos.y) {
				h = childPos.y - rootPos.y;
				midX = rootPos.x;
				midY = childPos.y;
				arcX = midX-w;
				arcY = midY-h;
			} else {
				h = - childPos.y + rootPos.y;
				midY = rootPos.y;
				midX = childPos.x;
				arcX = midX-w;
				arcY = midY-h;
			}
			h *= 2;
			w *= 2;
			int originToRootX = rootPos.x - midX;
			int originToRootY = rootPos.y - midY;
			int originToChildX = childPos.x - midX;
			int originToChildY = childPos.y - midY;
			int startAngle = atan4(-originToRootY,originToRootX);
			int arcAngle = atan4(  -originToChildY,originToChildX) - startAngle;
			canvas.drawArc(arcX, arcY, w, h, startAngle, arcAngle);
			double angle = Math.atan2( (childPos.y-rootPos.y), (childPos.x-rootPos.x) );
			drawArrowHead(childPos.x, childPos.y, angle, canvas);
			drawConnectionLines(child, positions, canvas);
		}
	}
	
	protected void drawArrowHead(int x, int y, double angle, Graphics2D canvas) {
		int width = (int) (8 * setup.scaleX);
		int height = (int) (8 * setup.scaleY);
		canvas.fillOval(x - width/2, y - width/2, width+1, height+1);
	}

	public void paint(Graphics g) {
		Map<BaseNode,Point> node2point = new HashMap<BaseNode,Point>();
		point2node.clear();
		Graphics2D canvas = (Graphics2D) g;
		canvas.setStroke(widthThreeStroke);
		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rootNode.drawNode(0,255,canvas,setup,node2point, point2node,rowHints);
		drawConnectionLines(rootNode, node2point, canvas);
	}

	
	protected static Point getLogicalPosition(int x, int y) {
		Point result = new Point();
		
		result.y = y / (BaseNode.shapeGapHeight+BaseNode.shapeHeight);
		result.x = x / ((BaseNode.shapeGapWidth+BaseNode.shapeWidth)/2);
		
		return result;
	}
	
	public String getToolTipText(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		BaseNode node = point2node.get(getLogicalPosition(x, y));
		if (node == null) return null;
		return "<html>"+node.toPPString().replace("\n", "<br>")+"</html>";
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
	
	public void componentResized(ComponentEvent arg0) {
		if (autoScale) {
			int w = this.getWidth();
			int h = this.getHeight();
			int graphWidth = logicalWidth*(BaseNode.shapeWidth+BaseNode.shapeGapWidth);
			int graphHeight = logicalHeight*(BaseNode.shapeHeight+BaseNode.shapeGapHeight);
			double scaleW = ((double)w)/((double)graphWidth);
			double scaleH = ((double)h)/((double)graphHeight);
			setup.scaleX = setup.scaleY = (float) Math.min(scaleW, scaleH);
			repaint();
		}
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}
	

}
