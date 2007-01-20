package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class JMiniRadarShapeContainer extends JShapeContainer implements MouseListener, MouseMotionListener, ComponentListener{
	public JMiniRadarShapeContainer() {
		super();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		offsetX=offsetY=0;
	}
	
	JZoomableShapeContainer masterShapeContainer;
	int offsetX;
	int offsetY;
	
	public void paint(Graphics g) {
		super.paint(g);
		Color clr=new Color(100,100,255,100);
		g.setColor(clr);
		double zoomFactor=masterShapeContainer.getZoomFactor();
		double factorX=((double)getWidth())/((double)graphwidth+10);
		double factorY=((double)getHeight())/((double)graphheight+10);
		double factor=Math.min(factorX, factorY);
		int rectwidth=(int)(masterShapeContainer.getWidth()*factor/zoomFactor)+1;
		int rectheight=(int)(masterShapeContainer.getHeight()*factor/zoomFactor)+1;
		int rectx=(int)(offsetX*factor);
		int recty=(int)(offsetY*factor);
		g.fillRect(rectx, recty, rectwidth, rectheight);
	}
	
	protected void drawPrimitive(Primitive p,Graphics2D g) {
		double factorX=((double)getWidth())/((double)graphwidth+10);
		double factorY=((double)getHeight())/((double)graphheight+10);
		double factor=Math.min(factorX, factorY);
		p.draw(g,factor,factor);
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(150,100);
	}
	
	public void addPrimitive(Primitive p) {
		if (p instanceof ConnectorLine) lines.add(p);
		boolean needsRepaint=false;
		if (p instanceof Shape) {
			shapes.add(p);
			Shape s=(Shape)p;
			
			if (s.width+s.x>graphwidth) {
				graphwidth=s.width+s.x;
				needsRepaint=true;
			}
			if (s.height+s.y>graphheight){
				graphheight=s.height+s.y;
				needsRepaint=true;
			}
		}
		if (needsRepaint){
			repaint();
		} else {
			Graphics g=getGraphics();
			if (g!=null) drawPrimitive(p,(Graphics2D)g);
		}
	}

	public JShapeContainer getMasterShapeContainer() {
		return masterShapeContainer;
	}

	public void setMasterShapeContainer(JZoomableShapeContainer masterShapeContainer) {
		this.masterShapeContainer = masterShapeContainer;
		masterShapeContainer.addComponentListener(this);
	}

	public void mouseClicked(MouseEvent arg0) {
		radarNewPosition(arg0.getX(), arg0.getY());
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	protected void radarNewPosition(int x,int y){
		double zoomFactor=masterShapeContainer.getZoomFactor();
		double factorX=((double)getWidth())/((double)graphwidth+10);
		double factorY=((double)getHeight())/((double)graphheight+10);
		double factor=Math.min(factorX, factorY);
		int x1=(int)(x/factor);
		int y1=(int)(y/factor);
		x1-=masterShapeContainer.getWidth()/2/zoomFactor;
		y1-=masterShapeContainer.getHeight()/2/zoomFactor;
		
		
		int offsetXmax=graphwidth+10-(int)(masterShapeContainer.getWidth()/zoomFactor);
		int offsetYmax=graphheight+10-(int)(masterShapeContainer.getHeight()/zoomFactor);
		
		if (x1>offsetXmax) x1=offsetXmax;
		if (y1>offsetYmax) y1=offsetYmax;
		if (x1<0) x1=0; if (y1<0) y1=0;
		
		
		masterShapeContainer.setOffsetX(x1);
		masterShapeContainer.setOffsetY(y1);
		offsetX=x1;
		offsetY=y1;
		masterShapeContainer.repaint();
		this.repaint();
	}
	
	
	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent arg0) {
		radarNewPosition(arg0.getX(), arg0.getY());
	}
	

	public void mouseMoved(MouseEvent arg0) {
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent arg0) {
		boolean changed=false;
		double zoomFactor=masterShapeContainer.getZoomFactor();
		int offsetXmax=graphwidth+10-(int)(masterShapeContainer.getWidth()/zoomFactor);
		int offsetYmax=graphheight+10-(int)(masterShapeContainer.getHeight()/zoomFactor);
		if (offsetX>offsetXmax) {offsetX=offsetXmax;changed=true;}
		if (offsetY>offsetYmax) {offsetY=offsetYmax;changed=true;}
		if (offsetX<0) {
			offsetX=0; changed=true;
		}
		if (offsetY<0) {
			offsetY=0; changed=true;
		}
		if (changed) {
			masterShapeContainer.setOffsetX(offsetX);
			masterShapeContainer.setOffsetY(offsetY);
		}
		
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
