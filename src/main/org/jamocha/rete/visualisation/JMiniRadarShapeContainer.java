package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Josef Alexander Hahn
 * 
 * A special JShapeContainer which can be used as mini-map. Here you can scroll
 * in the network and the new offset is given to the MasterShapeContainer.
 */
public class JMiniRadarShapeContainer extends JShapeContainer implements
		MouseListener, MouseMotionListener, ComponentListener {

	private static final long serialVersionUID = 1L;

	protected JZoomableShapeContainer masterShapeContainer;

	protected int offsetX;

	protected int offsetY;

	protected int normalizedFontHeight; // that is the Font-Height in pixel

	// which should be used normalized by
	// the scaling-factor

	public JMiniRadarShapeContainer() {
		super();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		offsetX = offsetY = 0;
	}

	/**
	 * Sets the font height in pixels (normalized by the scaling factor, which
	 * means, when scaling-factor=1)
	 * 
	 * @param h
	 *            font height in pixels
	 */
	public void setNormalizedFontHeight(int h) {
		normalizedFontHeight = h;
	}

	/**
	 * Paints itself to the given Graphics
	 * 
	 * @param g
	 *            The Graphics-canvas
	 */
	public void paint(Graphics g) {
		super.paint(g);
		Color clr = new Color(100, 100, 255, 100);
		g.setColor(clr);
		double zoomFactor = masterShapeContainer.getZoomFactor();
		double factorX = ((double) getWidth()) / ((double) graphwidth + 10);
		double factorY = ((double) getHeight()) / ((double) graphheight + 10);
		double factor = Math.min(factorX, factorY);
		int rectwidth = (int) (masterShapeContainer.getWidth() * factor / zoomFactor) + 1;
		int rectheight = (int) (masterShapeContainer.getHeight() * factor / zoomFactor) + 1;
		int rectx = (int) (offsetX * factor);
		int recty = (int) (offsetY * factor);
		g.fillRect(rectx, recty, rectwidth, rectheight);
	}

	/**
	 * Returns the actual scaling factor of the mini-map
	 * 
	 * @return scaling factor
	 */
	public double getFactor() {
		double factorX = ((double) getWidth()) / ((double) graphwidth + 10);
		double factorY = ((double) getHeight()) / ((double) graphheight + 10);
		double factor = Math.min(factorX, factorY);
		return factor;
	}

	protected void drawPrimitive(Primitive p, Graphics2D g) {
		double factor = getFactor();
		p.draw(g, factor, factor);
	}

	public Dimension getPreferredSize() {
		return new Dimension(150, 100);
	}

	/**
	 * Adds a Shape into the container
	 * 
	 * @param p
	 *            the primitive which should become added
	 */
	public void addPrimitive(Shape s) {
		super.addPrimitive(s);
		if (s.width + s.x > graphwidth) {
			graphwidth = s.width + s.x;
			repaint();
		}
		if (s.height + s.y > graphheight) {
			graphheight = s.height + s.y;
			repaint();
		}
	}

	/**
	 * @return the MasterShapeContainer. That the JShapeContainer which is
	 *         controlled by this MiniRadar.
	 */
	public JShapeContainer getMasterShapeContainer() {
		return masterShapeContainer;
	}

	/**
	 * sets the MasterShapeContainer. That the JShapeContainer which is
	 * controlled by this MiniRadar.
	 * 
	 * @param masterShapeContainer
	 *            The MasterShapeContainer
	 */
	public void setMasterShapeContainer(
			JZoomableShapeContainer masterShapeContainer) {
		this.masterShapeContainer = masterShapeContainer;
		masterShapeContainer.addComponentListener(this);
	}

	public void mouseClicked(MouseEvent arg0) {
		radarNewPosition(arg0.getX(), arg0.getY());
	}

	public void mouseDragged(MouseEvent arg0) {
		mouseClicked(arg0);
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}

	protected void radarNewPosition(int x, int y) {
		double masterZoomFactor = masterShapeContainer.getZoomFactor();
		double myScalingFactor = getFactor();
		int x1 = (int) (x / myScalingFactor);
		int y1 = (int) (y / myScalingFactor);
		x1 -= masterShapeContainer.getWidth() / 2 / masterZoomFactor;
		y1 -= masterShapeContainer.getHeight() / 2 / masterZoomFactor;
		int offsetXmax = graphwidth + 10
				- (int) (masterShapeContainer.getWidth() / masterZoomFactor);
		int offsetYmax = graphheight + 10
				- (int) (masterShapeContainer.getHeight() / masterZoomFactor);
		if (x1 > offsetXmax)
			x1 = offsetXmax;
		if (y1 > offsetYmax)
			y1 = offsetYmax;
		if (x1 < 0)
			x1 = 0;
		if (y1 < 0)
			y1 = 0;
		masterShapeContainer.setOffsetX(x1);
		masterShapeContainer.setOffsetY(y1);
		offsetX = x1;
		offsetY = y1;
		masterShapeContainer.repaint();
		this.repaint();
	}

	public void componentResized(ComponentEvent arg0) {
		boolean weHadToChangeOffset = false;
		double zoomFactor = masterShapeContainer.getZoomFactor();
		int offsetXmax = graphwidth + 10
				- (int) (masterShapeContainer.getWidth() / zoomFactor);
		int offsetYmax = graphheight + 10
				- (int) (masterShapeContainer.getHeight() / zoomFactor);
		if (offsetX > offsetXmax) {
			offsetX = offsetXmax;
			weHadToChangeOffset = true;
		}
		if (offsetY > offsetYmax) {
			offsetY = offsetYmax;
			weHadToChangeOffset = true;
		}
		if (offsetX < 0) {
			offsetX = 0;
			weHadToChangeOffset = true;
		}
		if (offsetY < 0) {
			offsetY = 0;
			weHadToChangeOffset = true;
		}
		if (weHadToChangeOffset) {
			masterShapeContainer.setOffsetX(offsetX);
			masterShapeContainer.setOffsetY(offsetY);
		}

		// calculate good font size
		int dpi = getToolkit().getScreenResolution();
		int ppRadarContainer = (int) ((normalizedFontHeight * getFactor() / dpi) * 72);
		setFont(new Font("SansSerif", Font.PLAIN, ppRadarContainer));

		repaint();
	}
}
