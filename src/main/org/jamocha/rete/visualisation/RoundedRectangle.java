package org.jamocha.rete.visualisation;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

public class RoundedRectangle extends Shape {

	protected double angleTopLeft;
	protected double angleTopRight;
	protected double angleBottomLeft;
	protected double angleBottomRight;
	
	RoundedRectangle(Color bgcolor, Color bordercolor,int x, int y, int width, int height,String text) {
		super(bgcolor,bordercolor,x,y,width,height,text);
	}
	
	public RoundedRectangle() {
		super();
	}
	
	void setWidth(int width){
		super.setWidth(width);
		calculateCornerAngles();
	}
	
	void setHeight(int height){
		super.setHeight(height);
		calculateCornerAngles();
	}
	
	protected void calculateCornerAngles(){
		angleTopRight=Math.atan2(height,width-height);
		angleTopLeft=Math.PI-angleTopRight;
		angleBottomRight=-angleTopRight;
		angleBottomLeft=-angleTopLeft;
	}

	void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY){
		int x=(int)Math.round( (this.x-offsetX)*factorX  );
		int y=(int)Math.round( (this.y-offsetY)*factorY  );
		int width=(int)Math.round( this.width*factorX  );
		int height=(int)Math.round( this.height*factorY  );
		
		
		
		/*canvas.setColor(bgcolor);
		canvas.fillRect(x,y,width+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawRect(x,y,width,height);*/

		canvas.setColor(bgcolor);
		canvas.fillOval(x,y,height+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x,y,height,height);
		
		canvas.setColor(bgcolor);
		canvas.fillOval(x+width-height,y,height+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x+width-height,y,height,height);
		
		canvas.setColor(bgcolor);
		canvas.fillRect(x+(height/2), y, width-height+1, height+1);
		canvas.setColor(bordercolor);
		canvas.drawLine(x+(height/2), y, x+width-height/2, y);
		canvas.drawLine(x+(height/2), y+height, x+width-height/2, y+height);
		
		
		
		Point textpos=calculateTextPosition(text,canvas,width,height);
		canvas.setColor(Color.black);
		if (bgcolor.getRGB()==Color.black.getRGB()) canvas.setColor(Color.white);
		if (height>canvas.getFontMetrics().getHeight()) {
			canvas.drawString(text,(int)textpos.getX()+x,(int)textpos.getY()+y);
		}
	}
	
	void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}

	Point calculateIntersection(double angle) {
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