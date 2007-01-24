package org.jamocha.rete.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * @author Josef Alexander Hahn
 * This Class represents a connection between two Shapes,
 * which will be drawn as an arrow
 */
public class ConnectorLine extends Primitive{

	private Shape from;
	private Shape to;
	private Color color;

	/**
	 * Sets the color of the representing arrow
	 * @param color Arrow color
	 */
	public void setColor(Color color) {
		this.color=color;
	}
	
	/**
	 * Returns the color of the representing arrow
	 * @return Actual arrow color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Draws an arrow from the From-Shape to the To-Shape
	 * with the choosen color. The draw-position is translated
	 * by (-offsetX,-offsetY).
	 * @param canvas The canvas to draw the arrow on
	 * @param offsetX Translation-Vector's negative x-component
	 * @param offsetY Translation-Vector's negative y-component
	 */
	public void draw(Graphics2D canvas,int offsetX, int offsetY) {
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	/**
	 * Draws an arrow from the From-Shape to the To-Shape
	 * with the choosen color. It will be scaled by (factorX,factorY).
	 * @param canvas The canvas to draw the arrow on
	 * @param factorX Scaling-Vector*s x-component
	 * @param factorY Scaling-Vector*s y-component
	 */
	public void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}
	
	/**
	 * Draws an arrow from the From-Shape to the To-Shape
	 * with the choosen color. The draw-position is translated
	 * by (-offsetX,-offsetY) and then scaled by (factorX,factorY).
	 * @param canvas The canvas to draw the arrow on
	 * @param offsetX Translation-Vector's negative x-component
	 * @param offsetY Translation-Vector's negative y-component
	 * @param factorX Scaling-Vector*s x-component
	 * @param factorY Scaling-Vector*s y-component
	 */
	public void draw(Graphics2D canvas,int offsetX, int offsetY, double factorX, double factorY){
		canvas.setColor(color);

		/* angleFromTo is this angle ;)
		 * (between -PI;+PI)
		 *                     TO
		 *    ----------------(x)-------------------
		 *                   /
		 *                 /)
		 *               / @ )
		 *    --------(x)---------------------------
		 *            FROM 
		 */
		double angleFromTo=Math.atan2(
				-to.getY()+from.getY(),
				 to.getX()-from.getX()
				 );
		
		/* angleToFrom is this angle ;)
		 * (between -PI;+PI)
		 *                     TO
		 *    ----------------(x)-------------------
		 *               ( @ /
		 *                (/
		 *               /
		 *    --------(x)---------------------------
		 *            FROM 
		 */
		double angleToFrom;
		if (angleFromTo>0) {
			angleToFrom=angleFromTo-Math.PI;
		} else {
			angleToFrom=angleFromTo+Math.PI;
		}
		Point pfrom=from.calculateIntersection(angleFromTo);
		Point pto=to.calculateIntersection(angleToFrom);
		Point parrow1=new Point((int) Math.round(6*Math.cos(angleToFrom+Math.PI/4.0)),(int)Math.round(6*Math.sin(angleToFrom+Math.PI/4.0)));
		Point parrow2=new Point((int) Math.round(6*Math.cos(angleToFrom-Math.PI/4.0)), (int)Math.round(6*Math.sin(angleToFrom-Math.PI/4.0)));
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

		//calculate a good line width
		int linewidth=(int)Math.round(1*Math.min(factorX,factorY));
		if (linewidth==0) linewidth=1;
		canvas.setStroke(new BasicStroke(linewidth));
		
		//draw the arrow
		canvas.drawLine(pfrom.x,pfrom.y,pto.x,pto.y);
		canvas.drawLine(pto.x+parrow1.x,pto.y-parrow1.y,pto.x,pto.y);
		canvas.drawLine(pto.x+parrow2.x,pto.y-parrow2.y,pto.x,pto.y);
	}

	/**
	 * @param from The From-Shape for the new ConnectorLine
	 * @param to The To-Shape for the new ConnectorLine
	 */
	public ConnectorLine(Shape from, Shape to) {
		this.from=from;
		this.to=to;
	}
}