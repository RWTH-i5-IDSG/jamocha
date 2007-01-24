package org.jamocha.rete.visualisation;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

/**
 * @author Josef Alexander Hahn
 * This is a concrete Shape-Implementation for an
 * ellipse with a topleft-point, a width, a height
 */
public class Ellipse extends Shape {

	/**
	 * @param bgcolor The Fill-Color the ellipse should get
	 * @param bordercolor The Border-Color the ellipse should get
	 * @param x the x-coordinate of the topleft-point
	 * @param y the y-coordinate of the topleft-point
	 * @param width the width of the ellipse
	 * @param height the height of the ellipse
	 * @param text the short-description which will be drawn into
	 */
	public Ellipse(Color bgcolor, Color bordercolor,int x, int y, int width, int height,String text) {
		super(bgcolor,bordercolor,x,y,width,height,text);
	}

	public Ellipse(){
		super();
	}
	
	/**
	 * Draws the ellipse.
	 * The draw-position is translated by (-offsetX,-offsetY).
	 * @param canvas The canvas to draw the arrow on
	 * @param offsetX Translation-Vector's negative x-component
	 * @param offsetY Translation-Vector's negative y-component
	 */
	public void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	/**
	 * Draws the ellipse.
	 * The draw-position is translated by (-offsetX,-offsetY)
	 * and scaled by (factorX,factorY).
	 * @param canvas The canvas to draw the arrow on
	 * @param offsetX Translation-Vector's negative x-component
	 * @param offsetY Translation-Vector's negative y-component
	 * @param factorX Scaling-Vector*s x-component
	 * @param factorY Scaling-Vector*s y-component
	 */
	public void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY){
		int x=(int)Math.round( (this.x-offsetX)*factorX  );
		int y=(int)Math.round( (this.y-offsetY)*factorY  );
		int width=(int)Math.round( this.width*factorX  );
		int height=(int)Math.round( this.height*factorY  );
		// set colors and draw
		canvas.setColor(bgcolor);
		canvas.fillOval(x,y,width+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x,y,width,height);
		// draw short-description
		canvas.setColor(Color.black);
		if (bgcolor.getRGB()==Color.black.getRGB()) canvas.setColor(Color.white);
		if (height>10) {
			Point textpos=calculateTextPosition(text,canvas,width,height);
			canvas.drawString(text,(int)textpos.getX()+x,(int)textpos.getY()+y);
		}
	}
	
	/**
	 * Draws the ellipse.
	 * The draw-position is scaled by (factorX,factorY).
	 * @param canvas The canvas to draw the arrow on
	 * @param factorX Scaling-Vector*s x-component
	 * @param factorY Scaling-Vector*s y-component
	 */
	public void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}

	public Point calculateIntersection(double angle) {
		Point result=new Point();
		//TODO: That calculation is NOT correct! That leads to wrong angles
		//      in the visualiser. looks not sooo good, but not that problem
		//      for now ;)
		result.x=(int)Math.round( (Math.cos(angle)*width*0.5)+x+(width*0.5)    );
		result.y=(int)Math.round( -(Math.sin(angle)*height*0.5)+y+(height*0.5)  );
		return result;
	}
}