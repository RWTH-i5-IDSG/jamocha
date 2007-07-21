package org.jamocha.rete.visualisation;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Josef Alexander Hahn
 * 
 * A Swing component which helds a list of primitives and draws them
 */
public class JShapeContainer extends JComponent {

	private static final long serialVersionUID = 1L;

	protected List<ConnectorLine> lines;

	protected List<Shape> shapes;

	protected int graphwidth;

	protected int graphheight;

	protected int offsetX;

	protected int offsetY;

	protected Font font;

	public JShapeContainer() {
			lines = new ArrayList<ConnectorLine>();
			shapes = new ArrayList<Shape>();
		offsetX = offsetY = 0;
	}

	/**
	 * sets the Font, which should be used while painting the short-description
	 * 
	 * @param f
	 *            the font
	 */
	public void setFont(Font f) {
		this.font = f;
	}

	/**
	 * Adds a ConnectorLine into the container
	 * 
	 * @param p
	 *            the primitive which should become added
	 */
	public void addPrimitive(ConnectorLine c) {
		synchronized (shapes) {
			lines.add(c);
			Graphics2D gr = (Graphics2D) getGraphics();
			if (gr == null)
				return;
			drawPrimitive(c, gr);
		}
	}

	/**
	 * Adds a Shape into the container
	 * 
	 * @param p
	 *            the primitive which should become added
	 */
	public void addPrimitive(Shape s) {
		synchronized (shapes) {
			shapes.add(s);	
			if (s.width + s.x > graphwidth)
				graphwidth = s.width + s.x;
			if (s.height + s.y > graphheight)
				graphheight = s.height + s.y;
			Graphics2D gr = (Graphics2D) getGraphics();
			if (gr == null)
				return;
			drawPrimitive(s, gr);
		}
	}

	/**
	 * Removes a Primitive from the container
	 * 
	 * @param p
	 *            primitive to delete
	 */
	public void removePrimitive(Primitive p) {
		synchronized (shapes) {
			if (p instanceof ConnectorLine)
				lines.remove(p);
			if (p instanceof Shape)
				shapes.remove(p);
		}
		repaint();
	}

	/**
	 * Flushes the container
	 */
	public void removeAllPrimitives() {
		synchronized (shapes) {
			lines.clear();
			shapes.clear();
		}
		repaint();
	}

	protected void drawPrimitive(Primitive p, Graphics2D g) {
		p.draw(g, offsetX, offsetY);
	}

	/**
	 * sets the negative x-coordinate of the translation
	 * 
	 * @param offsetX
	 *            negative x-coord
	 */
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * sets the negative y-coordinate of the translation
	 * 
	 * @param offsetX
	 *            negative y-coord
	 */
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void paint(Graphics g) {
		Graphics2D gr = (Graphics2D) g;
		/*gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setFont(font);
		synchronized (shapes) {
			Iterator<Shape> itshapes = shapes.iterator();
			Iterator<ConnectorLine> itarrows = lines.iterator();
			while (itshapes.hasNext()) {
				Shape s = itshapes.next();
				drawPrimitive(s, gr);
			}
			while (itarrows.hasNext()) {
				ConnectorLine l = itarrows.next();
				drawPrimitive(l, gr);
			}
		}*/
		
		
		
	}

	/**
	 * @return negative x-coordinate of the translation
	 */
	public int getOffsetX() {
		return offsetX;
	}

	/**
	 * @return negative y-coordinate of the translation
	 */
	public int getOffsetY() {
		return offsetY;
	}

	/**
	 * Returns the Shape at the given position
	 * 
	 * @param x
	 *            x-coordinate of the absolute position
	 * @param y
	 *            y-coordinate of the absolute position
	 * @return the shape at that position
	 */
	public Shape getShapeAtPosition(int x, int y) {
		// TODO: Not so efficient. Later, maybe, we should use a tricky
		// data structure for finding the shape faster ;)
		synchronized (shapes) {
			for (Iterator<Shape> it = shapes.iterator(); it.hasNext();) {
				Shape s = it.next();
				int offX = (x - s.getX());
				int offY = (y - s.getY());
				if (offX >= 0 && offY >= 0 && offX <= s.getWidth()
						&& offY <= s.getHeight())
					return s;
			}
		}
		return null;
	}
}