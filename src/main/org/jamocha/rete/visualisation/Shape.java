package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public abstract class Shape extends Primitive{


	protected Color bgcolor;
	protected Color bordercolor;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected String text;
	void setBgcolor(Color bgcolor) {this.bgcolor=bgcolor;}
	void setBordercolor(Color bordercolor) {this.bordercolor=bordercolor;}
	Color getBgcolor() {return bgcolor;}
	Color getBordercolor() {return bordercolor;}
	int getX() {return x;}
	int getY() {return y;}
	int getWidth() {return width;}
	int getHeight() {return height;}
	void setWidth(int width) {this.width=width;}
	void setHeight(int height) {this.height=height;}
	void setX(int x) {this.x=x;}
	void setY(int y) {this.y=y;}
	void incHeight(int dh) {y-=dh/2; height+=dh;}
	String getText() {return text;}
	void setText(String text) {this.text=text;}
	
	abstract Point calculateIntersection(double angle);
	

	protected Point calculateTextPosition(String text, Graphics g, int width, int height) {
		int stringHeight=(int)g.getFontMetrics().getLineMetrics(text,g).getHeight();
		int stringWidth=g.getFontMetrics().stringWidth(text);
		int xpos=(width-stringWidth)/2;
		int ypos=(height+stringHeight)/2;
		return new Point(xpos,ypos);
	}

	Shape() {
		super();
	}

	Shape(Color bgcolor, Color bordercolor, int x,int y, int width, int height, String text) {
		super();
		this.bgcolor=bgcolor;
		this.bordercolor=bordercolor;
		this.x=x;
		this.y=y;
		setWidth(width);
		setHeight(height);
		this.text=text;
	}



}