package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;


/**
 * @author Josef Alexander Hahn
 * This is a concrete Shape-Implementation for an
 * trapezoid with a topleft-point, a width, a height
 * in that way, that the top line is 50% of the bottom line
 * and centered.
 */
public class Trapezoid extends Shape {
	
	protected double angleTopLeft;
	protected double angleTopRight;
	protected double angleBottomLeft;
	protected double angleBottomRight;
	
	public Trapezoid(Color bgcolor, Color bordercolor, int x, int y, int width,
			int height, String text) {
		super(bgcolor, bordercolor, x, y, width, height, text);
	}
	
	public Trapezoid(){
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
		angleTopRight=Math.atan2(height,width*0.5);
		angleTopLeft=Math.PI-angleTopRight;
		angleBottomRight=-Math.atan2(height,width);
		angleBottomLeft=-angleTopLeft;
	}
	
	public void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	public void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY){
		int ourX=(int)Math.round(  (x-offsetX)*factorX);
		int ourY=(int)Math.round(  (y-offsetY)*factorY);
		int ourWidth=(int)Math.round(width*factorX);
		int ourHeight=(int)Math.round(height*factorY);
		int[] xpoints = { ourX + (int)(ourWidth*.25), ourX + (int)(ourWidth*.75), ourX + ourWidth,	ourX};
		int[] ypoints = { ourY, ourY, ourY+ourHeight,ourY+ourHeight };
		canvas.setColor(bgcolor);
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.setColor(bordercolor);
		canvas.drawPolygon(xpoints, ypoints, 4);
		canvas.setColor(Color.black);
		if (bgcolor.getRGB()==Color.black.getRGB()) canvas.setColor(Color.white);
		if (ourHeight>10) {
			Point textpos=calculateTextPosition(text,canvas,ourWidth,ourHeight);
			canvas.drawString(text,(int)textpos.getX()+ourX,(int)textpos.getY()+ourY);
		}
	}
	
	public void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}
	
	public Point calculateIntersection(double angle) {
		Point result=new Point();
		if (angle>angleTopLeft || angle<angleBottomLeft) {
			// left
			double alpha;
			if (angle>0) {alpha=Math.PI-angle;} else {alpha=-angle-Math.PI;}
			double dy=(Math.tan(alpha)*width+height)/4.0;
			double dx=(dy*width)/(4.0*height);
			result.x=(int)Math.round(x+ dx);
			result.y=(int)Math.round( -dy+y+height);
		} else if (angle>angleTopRight) {
			// top
			double alpha=Math.PI*0.5-angle;
			result.x=(int)Math.round( Math.tan(alpha)*height*0.5 + x + width*0.5 );
			result.y=y;
		} else if (angle>angleBottomRight) {
			// right
			double alpha=angle;
			double dy=(Math.tan(alpha)*width+height)/4.0;
			double dx=(dy*width)/(4.0*height);
			result.x=(int)Math.round(x+width-dx);
			result.y=(int)Math.round( -dy+y+height);
		} else {
			// bottom
			double alpha=Math.PI*0.5+angle;
			result.x=(int)Math.round( Math.tan(alpha)*height*0.5 + x + width*0.5 );
			result.y=y+height;
		}
		return result;
	}

}
