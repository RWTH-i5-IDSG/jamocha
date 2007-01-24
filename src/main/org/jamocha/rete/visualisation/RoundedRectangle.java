package org.jamocha.rete.visualisation;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

/**
 * @author Josef Alexander Hahn
 * This is a concrete Shape-Implementation for an
 * rectangle with fully-rounded left and right sides
 * with a topleft-point, a width, a height
 */
public class RoundedRectangle extends Shape {

	protected double angleTopLeft;
	protected double angleTopRight;
	protected double angleBottomLeft;
	protected double angleBottomRight;
	
	public RoundedRectangle(Color bgcolor, Color bordercolor,int x, int y, int width, int height,String text) {
		super(bgcolor,bordercolor,x,y,width,height,text);
	}
	
	public RoundedRectangle() {
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
		angleTopRight=Math.atan2(height,width-height);
		angleTopLeft=Math.PI-angleTopRight;
		angleBottomRight=-angleTopRight;
		angleBottomLeft=-angleTopLeft;
	}

	public void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	public void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY){
		int x=(int)Math.round( (this.x-offsetX)*factorX  );
		int y=(int)Math.round( (this.y-offsetY)*factorY  );
		int width=(int)Math.round( this.width*factorX  );
		int height=(int)Math.round( this.height*factorY  );
		// draw the left oval
		canvas.setColor(bgcolor);
		canvas.fillOval(x,y,height+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x,y,height,height);
		// draw the right oval
		canvas.setColor(bgcolor);
		canvas.fillOval(x+width-height,y,height+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x+width-height,y,height,height);
		// draw the center rectangle
		canvas.setColor(bgcolor);
		canvas.fillRect(x+(height/2), y, width-height+1, height+1);
		canvas.setColor(bordercolor);
		canvas.drawLine(x+(height/2), y, x+width-height/2, y);
		canvas.drawLine(x+(height/2), y+height, x+width-height/2, y+height);
		
		// draw the text	
		canvas.setColor(Color.black);
		if (bgcolor.getRGB()==Color.black.getRGB()) canvas.setColor(Color.white);
		if (height>10) {
			Point textpos=calculateTextPosition(text,canvas,width,height);
			canvas.drawString(text,(int)textpos.getX()+x,(int)textpos.getY()+y);
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
			double ys=Math.tan(alpha)*(width-height)*0.5;
			double newalpha=Math.PI-Math.acos(ys/height);
			double x0=height*Math.cos(newalpha);
			double y0=height*Math.sin(newalpha);
			result.x=(int)Math.round(x+(height*0.5)-x0);
			result.y=(int)Math.round(y+(height*0.5)-y0);		} else if (angle>angleTopRight) {
			// top
			double alpha=Math.PI*0.5-angle;
			result.x=(int)Math.round( Math.tan(alpha)*height*0.5 + x + width*0.5 );
			result.y=y;
		} else if (angle>angleBottomRight) {
			// right
			double alpha=angle;
			double ys=Math.tan(alpha)*(width-height)*0.5;
			double newalpha=Math.PI-Math.acos(ys/height);
			double x0=height*Math.cos(newalpha);
			double y0=height*Math.sin(newalpha);
			result.x=(int)Math.round(x+(width-height*0.5)+x0);
			result.y=(int)Math.round(y+(height*0.5)-y0);
		} else {
			// bottom
			double alpha=Math.PI*0.5+angle;
			result.x=(int)Math.round( Math.tan(alpha)*height*0.5 + x + width*0.5 );
			result.y=y+height;
		}
		return result;
	}

}