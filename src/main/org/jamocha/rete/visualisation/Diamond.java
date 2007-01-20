package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

// TODO: DELETE THIS CLASS; IT IS NOT USED BY THE VISUALIZER

public class Diamond extends Shape {

	public Diamond(Color bgcolor, Color bordercolor, int x, int y, int width,
			int height, String text) {
		super(bgcolor, bordercolor, x, y, width, height, text);
	}
	
	public Diamond(){
		super();
	}

	void draw(Graphics2D canvas,double factorX,double factorY){
		draw(canvas,0,0,factorX,factorY);
	}
	
	void draw(Graphics2D canvas,int offsetX, int offsetY, double factorX,double factorY){
		drawHelper(canvas,(int)((x+offsetX)*factorX),(int)((y+offsetY)*factorY),(int)(width*factorX),(int)(height*factorY));
	}
	
	protected void drawHelper(Graphics2D canvas, int x, int y,int width,int height){
		int ypos = y - (height / 4);
		int[] xpoints = { x + (width/2), x + (width/8), x + (width/2),
				x + (width * 9/10) };
		int[] ypoints = { ypos + (height/8), ypos + (height/2),
				ypos + (height * 9/10), ypos + (height/2) };
		canvas.setColor(bgcolor);
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.setColor(bordercolor);
		canvas.drawPolygon(xpoints, ypoints, 4);
		Point textpos = calculateTextPosition(text, canvas,width,height);
		// the text position is -2 y so it is centered
		canvas.drawString(text, (int) textpos.getX() + x,
				(int) textpos.getY() + ypos -2);
	}
	
	void draw(Graphics2D canvas,int offsetX,int offsetY) {
		drawHelper(canvas,x+offsetX,y+offsetY,width,height);
	}


	@Override
	Point calculateIntersection(double angle) {
		Point result=new Point();
		result.x=this.x;
		result.y=this.y;
		return result;
	}

}
