package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemoryImpl;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.RootNode;

public class Visualizer extends JComponent{
	
	BaseNode rootNode;
	VisualizerSetup setup;
	
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
		result = (result / Math.PI)* 180.0;
		return (  ((int)result) % 360  );
	}
	
	protected void drawConnectionLines(BaseNode root, Map<BaseNode,Point> positions, Graphics2D canvas) {
		for (BaseNode child : root.getChildNodes() ) {
			Point rootPos = positions.get(root);
			Point childPos = positions.get(child);
			rootPos = toPhysical(rootPos,setup);
			childPos = toPhysical(childPos,setup);
			childPos = BaseNode.getLineEndPoint(rootPos, childPos);
			rootPos = BaseNode.getLineEndPoint(childPos, rootPos);
			canvas.setColor(Color.green.darker().darker());
			
			// nice algo
			int midX, midY, w, h;
			w = Math.abs(rootPos.x - childPos.x);
			if (rootPos.y < childPos.y) {
				h = childPos.y - rootPos.y;
				midX = rootPos.x-w;
				midY = childPos.y-h;
			} else {
				h = - childPos.y + rootPos.y;
				midY = rootPos.y-w;
				midX = childPos.x-h;
			}
			h *= 2;
			w *= 2;
			
			int originToRootX = rootPos.x - midX;
			int originToRootY = rootPos.y - midY;
			
			int originToChildX = childPos.x - midX;
			int originToChildY = childPos.y - midY;

			System.out.println(originToRootX);
			System.out.println(originToRootY);
			
			int startAngle = atan4(-originToRootY,originToRootX);
			int arcAngle = atan4(originToRootY,originToRootX);
			
			//startAngle = 0;
			arcAngle= 30;
			
			canvas.drawArc(midX, midY, w, h, startAngle, arcAngle);
			//canvas.drawLine(rootPos.x, rootPos.y, 0,0);
			
			drawConnectionLines(child, positions, canvas);
		}
	}

	public void paint(Graphics g) {
		Map<BaseNode,Point> positions = new HashMap<BaseNode,Point>();
		Graphics2D canvas = (Graphics2D) g;
		rootNode.drawNode(0,0,255,canvas,setup,positions);
		drawConnectionLines(rootNode, positions, canvas);
	}

	
	

}
