package org.jamocha.rete.visualisation;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

public class Ellipse extends Shape {

	Ellipse(Color bgcolor, Color bordercolor,int x, int y, int width, int height,String text) {
		super(bgcolor,bordercolor,x,y,width,height,text);
	}

	Ellipse(){
		super();
	}
	
	void draw(Graphics2D canvas,int offsetX,int offsetY){
		draw(canvas,offsetX,offsetY,1.0,1.0);
	}
	
	void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY){
		int x=(int)Math.round( (this.x-offsetX)*factorX  );
		int y=(int)Math.round( (this.y-offsetY)*factorY  );
		int width=(int)Math.round( this.width*factorX  );
		int height=(int)Math.round( this.height*factorY  );
		
		canvas.setColor(bgcolor);
		canvas.fillOval(x,y,width+1,height+1);
		canvas.setColor(bordercolor);
		canvas.drawOval(x,y,width,height);
		Point textpos=calculateTextPosition(text,canvas,width,height);
		canvas.setColor(Color.black);
		canvas.drawString(text,(int)textpos.getX()+x,(int)textpos.getY()+y);
	}
	
	void draw(Graphics2D canvas,double factorX, double factorY){
		draw(canvas,0,0,factorX,factorY);
	}

	Point calculateIntersection(double angle) {
		Point result=new Point();
		result.x=(int)Math.round( (Math.cos(angle)*width*0.5)+x+(width*0.5)    );
		result.y=(int)Math.round( -(Math.sin(angle)*height*0.5)+y+(height*0.5)  );
		return result;
	}

}