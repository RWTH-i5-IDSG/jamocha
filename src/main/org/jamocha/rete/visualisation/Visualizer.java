package org.jamocha.rete.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemoryImpl;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;

public class Visualizer extends JComponent{
	
	BaseNode rootNode;
	VisualizerSetup setup;
	protected final BasicStroke widthThreeStroke = new BasicStroke(3);
	protected final Color alphaColor = new Color(0,0,255,120);
	protected final Color betaColor = new Color(255,0,0,120);

	
	public Visualizer(Rete e) {
		rootNode = ((WorkingMemoryImpl) e.getWorkingMemory()).getRootNode();
		setup = new VisualizerSetup();
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
			childPos = BaseNode.getLineEndPoint(rootPos, childPos);
			rootPos = BaseNode.getLineEndPoint(childPos, rootPos);
			
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
		canvas.fillOval(x - width/2, y - width/2, width, height);
	}

	public void paint(Graphics g) {
		Map<BaseNode,Point> positions = new HashMap<BaseNode,Point>();
		Graphics2D canvas = (Graphics2D) g;
		canvas.setStroke(widthThreeStroke);
		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rootNode.drawNode(0,0,255,canvas,setup,positions);
		drawConnectionLines(rootNode, positions, canvas);
	}

	
	

}
