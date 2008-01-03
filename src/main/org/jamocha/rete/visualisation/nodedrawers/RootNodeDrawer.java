package org.jamocha.rete.visualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.VisualizerSetup;

public class RootNodeDrawer extends AbstractNodeDrawer {

	public RootNodeDrawer(Node owner) {
		super(owner);
	}
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<Node> selected, Graphics2D canvas){
		int alpha = (selected.contains(this.node)) ? 255 : 20;
		canvas.setBackground( new Color(0,0,0,alpha) );
		canvas.setColor(  new Color(40,40,40,alpha) );
		canvas.fillOval(x,y,width,height);
		canvas.drawOval(x,y,width,height);
		canvas.setColor( new Color(255,255,255,alpha) );
		drawId(x,y,height,width,halfLineHeight,canvas);
	}
	
	
	public Point getLineEndPoint(Point target, Point me, VisualizerSetup setup) {
		double angle = atan3(-target.y+me.y, target.x-me.x);
		
		double unitCircleX = Math.cos(angle);
		double unitCircleY = Math.sin(angle);
		
		Point result = new Point();
		result.x = (int)(unitCircleX * (NodeDrawer.shapeWidth * setup.scaleX /2) + me.x);
		result.y = (int)(-unitCircleY * (NodeDrawer.shapeHeight * setup.scaleY /2) + me.y);
		return result;
	}

}
