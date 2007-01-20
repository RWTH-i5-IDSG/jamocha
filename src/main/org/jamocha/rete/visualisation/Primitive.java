package org.jamocha.rete.visualisation;

import java.awt.Graphics2D;

public abstract class Primitive{

	Primitive(){
	}

	abstract void draw(Graphics2D canvas,int offsetX,int offsetY);
	abstract void draw(Graphics2D canvas,double factorX, double factorY);
	abstract void draw(Graphics2D canvas,int offsetX,int offsetY,double factorX, double factorY);





} 
