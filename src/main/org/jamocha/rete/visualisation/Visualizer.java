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
	
	protected void drawConnectionLines(BaseNode root, Map<BaseNode,Point> positions, Graphics2D canvas) {
		for (BaseNode child : root.getChildNodes() ) {
			Point me = positions.get(root);
			Point target = positions.get(child);
			me = toPhysical(me,setup);
			target = toPhysical(target,setup);
			//me = root.getLineEndPoint(target, me);
			//target = root.getLineEndPoint(target, me);
			canvas.setColor(Color.GREEN);
			canvas.drawLine(me.x, me.y, target.x, target.y);
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
