package org.jamocha.rete.visualisation;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import javax.swing.JComponent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class JShapeContainer extends JComponent {

	private static final long serialVersionUID = 1L;
	protected List lines;
	protected List shapes;
	protected int graphwidth;
	protected int graphheight;
	protected int offsetX;
	protected int offsetY;
	

	public JShapeContainer() {
		lines=new ArrayList();
		shapes=new ArrayList();
		offsetX=offsetY=0;
	}

	public void addPrimitive(Primitive p) {
		if (p instanceof ConnectorLine) lines.add(p);
		if (p instanceof Shape) {
			shapes.add(p);
			Shape s=(Shape)p;
			
			if (s.width+s.x>graphwidth)graphwidth=s.width+s.x;
			if (s.height+s.y>graphheight)graphheight=s.height+s.y;
		}
		Graphics g=getGraphics();
		if (g!=null) drawPrimitive(p,(Graphics2D)g);
	}

	public void removePrimitive(Primitive p) {
		if (p instanceof ConnectorLine) lines.remove(p);
		if (p instanceof Shape) shapes.remove(p);
	}
	
	public void removeAllPrimitives() {
		lines.clear();
		shapes.clear();
		repaint();
	}

	protected void drawPrimitive(Primitive p,Graphics2D g) {
		p.draw(g,offsetX,offsetY);
	}
	
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void paint(Graphics g) {
		Graphics2D gr=(Graphics2D)g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		Iterator it = shapes.iterator();
		while (it.hasNext()) {
			Primitive p=(Primitive) it.next();
			drawPrimitive(p,gr);
		}
		it = lines.iterator();
		while (it.hasNext()) {
			Primitive p=(Primitive) it.next();
			drawPrimitive(p,gr);
		}
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public Shape getShapeAtPosition(int x, int y) {
		for(Iterator it=shapes.iterator();it.hasNext();){
			Shape s=(Shape)it.next();
			int offX=(x-s.getX());
			int offY=(y-s.getY());
			if (offX>=0 && offY>=0 && offX<=s.getWidth() && offY<=s.getHeight())
				return s;
		}
		return null;
	}


}