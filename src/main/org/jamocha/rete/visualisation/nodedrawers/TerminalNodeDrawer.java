package org.jamocha.rete.visualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import org.jamocha.rete.nodes.Node;

public class TerminalNodeDrawer extends AbstractNodeDrawer {

	public TerminalNodeDrawer(Node owner) {
		super(owner);
	}
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<Node> selected, Graphics2D canvas){
		int alpha = (selected.contains(this.node)) ? 255 : 20;
		canvas.setColor( new Color(0,0,0,alpha) );
		canvas.fillRect(x, y, width, height);
		canvas.setColor(  new Color(40,40,40,alpha) );
		canvas.drawRect(x, y, width, height);
		canvas.setColor( new Color(255,255,255,alpha) );
		drawId(x,y,height,width,halfLineHeight,canvas);
	}
}
