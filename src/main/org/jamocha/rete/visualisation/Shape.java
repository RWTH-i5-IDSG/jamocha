package org.jamocha.rete.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author Josef Alexander Hahn
 * 
 * abstract Shape-Class for use in visualiser
 */
public abstract class Shape extends Primitive {
	
	protected Color bgcolor;

	protected Color bordercolor;

	protected int x;

	protected int y;

	protected int width;

	protected int height;

	protected String text;

	protected String longDescription;

	protected boolean activated;
	
	/**
	 * gets the optional long description text
	 * 
	 * @return long description text
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * sets the optional long description text (will not become printed with the
	 * shape)
	 * 
	 * @param val
	 *            long description text
	 */
	public void setLongDescription(String val) {
		longDescription = val;
	}

	/**
	 * sets the fill-color
	 * 
	 * @param bgcolor
	 *            fill-color
	 */
	public void setBgcolor(Color bgcolor) {
		this.bgcolor = bgcolor;
	}

	/**
	 * sets the border-color
	 * 
	 * @param bordercolor
	 *            bordercolor
	 */
	public void setBordercolor(Color bordercolor) {
		this.bordercolor = bordercolor;
	}

	/**
	 * @return the fill-color
	 */
	public Color getBgcolor() {
		return bgcolor;
	}

	/**
	 * @return the border-color
	 */
	public Color getBordercolor() {
		return bordercolor;
	}

	/**
	 * @return x-coordinate of the topleft-point
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return y-coordinate of the topleft-point
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return width of the shape
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return height of the shape
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * sets the width of the shape
	 * 
	 * @param width
	 *            width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * sets the height of the shape
	 * 
	 * @param height
	 *            height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * sets the shape-position
	 * 
	 * @param x
	 *            the x-coordinate of the topleft-point
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * sets the shape-position
	 * 
	 * @param y
	 *            the y-coordinate of the topleft-point
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * increments the height _with invariant centre_
	 * 
	 * @param dh
	 *            height to add
	 */
	public void incHeight(int dh) {
		y -= dh / 2;
		height += dh;
	}

	/**
	 * increments the width _with invariant centre_
	 * 
	 * @param dh
	 *            width to add
	 */
	public void incWidth(int dw) {
		x -= dw / 2;
		width += dw;
	}

	/**
	 * @return the short description
	 */
	public String getText() {
		return text;
	}

	/**
	 * sets the short description which will be printed with the shape
	 * 
	 * @param text
	 *            the short description
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Calculates the intersection-coordinate between the borderline of that
	 * shape and a line, starting in the centre with the given angle
	 * 
	 * @param angle
	 *            the angle to the x-axis
	 * @return the intersection-point
	 */
	public abstract Point calculateIntersection(double angle);

	protected Point calculateTextPosition(String text, Graphics g, int width,
			int height) {
		int stringHeight = (int) g.getFontMetrics().getLineMetrics(text, g)
				.getHeight();
		int stringWidth = g.getFontMetrics().stringWidth(text);
		int xpos = (int) Math.round((width - stringWidth) * 0.5);
		int ypos = (int) Math.round((height + stringHeight) * 0.5 * 0.845);

		return new Point(xpos, ypos);
	}

	Shape() {
		super();
	}

	/**
	 * @param bgcolor
	 *            The Fill-Color the shape should get
	 * @param bordercolor
	 *            The Border-Color the shape should get
	 * @param x
	 *            the x-coordinate of the topleft-point
	 * @param y
	 *            the y-coordinate of the topleft-point
	 * @param width
	 *            the width of the shape
	 * @param height
	 *            the height of the shape
	 * @param text
	 *            the short-description which will be drawn into
	 */
	Shape(Color bgcolor, Color bordercolor, int x, int y, int width,
			int height, String text) {
		super();
		this.bgcolor = bgcolor;
		this.bordercolor = bordercolor;
		this.x = x;
		this.y = y;
		setWidth(width);
		setHeight(height);
		this.text = text;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

}