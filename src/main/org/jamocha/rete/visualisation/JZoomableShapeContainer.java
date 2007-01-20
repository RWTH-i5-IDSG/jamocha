package org.jamocha.rete.visualisation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;

public class JZoomableShapeContainer extends JShapeContainer {

	JMiniRadarShapeContainer radarShapeContainer;
	
	public JZoomableShapeContainer() {
		super();
		zoomLevel=0;
		
		// TODO Auto-generated constructor stub
	}
	
	void zoomIn() {
		zoomLevel++;
		repaint();
		if (radarShapeContainer!=null) {
			radarShapeContainer.componentResized(null);
			radarShapeContainer.repaint();
		}
	}
	
	void zoomOut() {
		zoomLevel--;
		repaint();
		if (radarShapeContainer!=null) {
			radarShapeContainer.componentResized(null);
			radarShapeContainer.repaint();
		}
	}
	
	double getZoomFactor() {
		double factor=1.0;
		for (int i=1; i<=zoomLevel; i++) {
			factor*=2.0;
		}
		for (int i=-1; i>=zoomLevel; i--) {
			factor/=2.0;
		}
		return factor;
	}
	
	protected void drawPrimitive(Primitive p,Graphics2D g) {
		double factor=getZoomFactor();
		p.draw(g,offsetX,offsetY,factor, factor);
	}
	
	int zoomLevel;

	public JMiniRadarShapeContainer getRadarShapeContainer() {
		return radarShapeContainer;
	}

	public void setRadarShapeContainer(JMiniRadarShapeContainer radarShapeContainer) {
		this.radarShapeContainer = radarShapeContainer;
	}
	
	public Shape getShapeAtPosition(int x, int y) {
		int rx=(int)Math.round(x/getZoomFactor()+offsetX);
		int ry=(int)Math.round(y/getZoomFactor()+offsetY);
		for(Iterator it=shapes.iterator();it.hasNext();){
			Shape s=(Shape)it.next();
			int offX=(rx-s.getX());
			int offY=(ry-s.getY());
			if (offX>=0 && offY>=0 && offX<=s.getWidth() && offY<=s.getHeight())
				return s;
		}
		return null;
	}

}
