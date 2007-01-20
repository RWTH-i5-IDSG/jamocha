package org.jamocha.rete.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.*;

public class ConnectorLine extends Primitive{

	private Shape from;
	private Shape to;
	private Color color;

	public void setColor(Color color) {this.color=color;}
	Color getColor() {return color;}

	public void draw(Graphics2D canvas,int offsetX, int offsetY) {
		draw(canvas,offsetX,offsetY,0,0);
	}
	
	public void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}
	
	public void draw(Graphics2D canvas,int offsetX, int offsetY, double factorX, double factorY){
		canvas.setColor(color);
		Point pfrom;
		Point pto;
		
		double angle=Math.atan2( -to.getY()+from.getY(), to.getX()-from.getX()   );
		double angle2;
		if (angle>0) {
			angle2=angle-Math.PI;
		} else {
			angle2=angle+Math.PI;
		}
		
			
		pfrom=from.calculateIntersection(angle);
		pto=to.calculateIntersection(angle2);
		
		
		Point parrow1=new Point((int)(6*Math.cos(angle2+0.6)),(int)(6*Math.sin(angle2+0.6)));
		Point parrow2=new Point((int) (6*Math.cos(angle2-0.6)), (int)(6*Math.sin(angle2-0.6)));
		
		
		pfrom.x-=offsetX;
		pfrom.y-=offsetY;
		pto.x-=offsetX;
		pto.y-=offsetY;
		
		pfrom.x*=factorX;
		pfrom.y*=factorY;
		pto.x*=factorX;
		pto.y*=factorY;
		
		
		parrow1.x*=factorX;
		parrow1.y*=factorY;
		parrow2.x*=factorX;
		parrow2.y*=factorY;		
		
		int linewidth=(int)Math.round(1*Math.min(factorX,factorY));
		if (linewidth==0) linewidth=1;
		canvas.setStroke(new BasicStroke(linewidth));
		canvas.drawLine(pfrom.x,pfrom.y,pto.x,pto.y);
		
		canvas.drawLine(pto.x+parrow1.x,pto.y-parrow1.y,pto.x,pto.y);
		canvas.drawLine(pto.x+parrow2.x,pto.y-parrow2.y,pto.x,pto.y);
		
	}

	public ConnectorLine(Shape from, Shape to) {
		this.from=from;
		this.to=to;
	}


}