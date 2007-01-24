package org.jamocha.rete.visualisation;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

/**
 * @author Josef Alexander Hahn
 * This is a concrete Shape-Implementation for an
 * rectangle with a topleft-point, a width, a height
 */
public class Rectangle extends Shape {

	protected double angleTopLeft;
	protected double angleTopRight;
	protected double angleBottomLeft;
	protected double angleBottomRight;
	
	/**
	 * @param bgcolor The Fill-Color the ellipse should get
	 * @param bordercolor The Border-Color the ellipse should get
	 * @param x the x-coordinate of the centre
	 * @param y the y-coordinate of the centre
	 * @param width the width of the ellipse
	 * @param height the height of the ellipse
	 * @param text the short-description which will be drawn into
	 */
	public Rectangle(Color bgcolor, Color bordercolor,int x, int y, int width, int height,String text) {
		super(bgcolor,bordercolor,x,y,width,height,text);
	}
	
	public Rectangle() {
		super();
	}
	
	public void setWidth(int width){
		super.setWidth(width);
		calculateCornerAngles();
	}
	
	public void setHeight(int height){
		super.setHeight(height);
		calculateCornerAngles();
	}
	
	protected void calculateCornerAngles(){
		angleTopRight=Math.atan2(height,width);
		angleTopLeft=Math.PI-angleTopRight;
		angleBottomRight=-angleTopRight;
		angleBottomLeft=-angleTopLeft;
	}

	/**
	 * Draws the rectangle.
	 * The draw-position is translated by (-offsetX,-offsetY).
	 * @param canvas The canvas to draw the arrow on
	 * @param offsetX Translation-Vector's negative x-component
	 * @param offsetY Translation-Vector's negative y-component
	 */
	public void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	/**
	 * Draws the rectangle.
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
		canvas.setColor(bgcolor);
		canvas.fillRect(x,y,width+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawRect(x,y,width,height);
		canvas.setColor(Color.black);
		if (bgcolor.getRGB()==Color.black.getRGB()) canvas.setColor(Color.white);
		if (height>10) {
			Point textpos=calculateTextPosition(text,canvas,width,height);
			canvas.drawString(text,(int)textpos.getX()+x,(int)textpos.getY()+y);
		}

	}
	
	/**
	 * Draws the rectangle.
	 * The draw-position is scaled by (factorX,factorY).
	 * @param canvas The canvas to draw the arrow on
	 * @param factorX Scaling-Vector*s x-component
	 * @param factorY Scaling-Vector*s y-component
	 */
	public void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}

	/**
	 * Calculates the intersection-coordinate between
	 * the borderline of that shape and a line, starting in
	 * the centre with the given angle
	 * @param angle the angle to the x-axis
	 * @return the intersection-point
	 */
	public Point calculateIntersection(double angle) {
		Point result=new Point();
		if (angle>angleTopLeft || angle<angleBottomLeft) {
			// left
			double alpha;
			if (angle>0) {alpha=Math.PI-angle;} else {alpha=-angle-Math.PI;}
			result.x=x;
			result.y=(int)Math.round( -Math.tan(alpha)*width*0.5 +y+ height*0.5);
		} else if (angle>angleTopRight) {
			// top
			double alpha=Math.PI*0.5-angle;
			result.x=(int)Math.round( Math.tan(alpha)*height*0.5 + x + width*0.5 );
			result.y=y;
		} else if (angle>angleBottomRight) {
			// right
			double alpha=angle;
			result.x=x+width;
			result.y=(int)Math.round( -Math.tan(alpha)*width*0.5 +y+ height*0.5);
		} else {
			// bottom
			double alpha=Math.PI*0.5+angle;
			result.x=(int)Math.round( Math.tan(alpha)*height*0.5 + x + width*0.5 );
			result.y=y+height;
		}
		return result;
	}

}