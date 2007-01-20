package org.jamocha.rete.visualisation;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

public class Circle extends Shape {

	Circle(Color bgcolor, Color bordercolor,int x, int y, int width, int height,String text) {
		super(bgcolor,bordercolor,x,y,width,height,text);
	}

	Circle(){
		super();
	}
	
	void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY){
		int circleDim=Math.min(width,height);
		int circleX=(int)Math.round(x+(width-circleDim)*0.5);
		int circleY=(int)Math.round(y+(height-circleDim)*0.5);
		
		int x=(int)Math.round( (circleX-offsetX)*factorX  );
		int y=(int)Math.round( (circleY-offsetY)*factorY  );
		int width=(int)Math.round( circleDim*factorX  );
		int height=(int)Math.round( circleDim*factorY  );
		
		canvas.setColor(bgcolor);
		canvas.fillOval(x,y,width+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x,y,width,height);
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
		int minDim=Math.min(width,height);
		result.x=(int)Math.round( (Math.cos(angle)*minDim*0.5)+x+(width*0.5)    );
		result.y=(int)Math.round( -(Math.sin(angle)*minDim*0.5)+y+(height*0.5)  );
		return result;
	}

}