package org.jamocha.rete.visualisation;

public class VisualizerSetup {

	public int offsetX;
	public int offsetY;
	public float scaleX;
	public float scaleY;
	
	public int lineStyle;
	
	final static int LINE = 1;
	final static int QUARTERELLIPSE = 2;
	
	public VisualizerSetup() {
		offsetX = offsetY = 0;
		scaleX = scaleY = 1;
	}
	
}
