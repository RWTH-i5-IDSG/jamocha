/*
 * Copyright 2007 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.nodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.Map;

import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.visualisation.VisualizerSetup;

/**
 * @author Sebastian Reinartz
 * 
 * BaseNode is meant to define common logic that all nodes must have and
 * implement common logic.
 */
public abstract class BaseNode implements Serializable {

	protected int nodeID;

	/**
	 * We use an object Array to keep things efficient
	 */
	protected BaseNode[] childNodes = new BaseNode[0];

	protected BaseNode[] parentNodes = new BaseNode[0];

	// override these values in subclasses:
	protected int maxParentCount = 1;

	protected int maxChildCount = 1;

	/**
	 * BaseNode has only one constructor which takes an unique node id. All
	 * subclasses need to call the constructor.
	 */
	public BaseNode(int id) {
		super();
		this.nodeID = id;
	}

	public BaseNode[] getParentNodes() {
		return parentNodes;
	}

	public BaseNode[] getChildNodes() {
		return childNodes;
	}

	public int getChildCount() {
		return childNodes.length;
	}

	public int getParentCount() {
		return parentNodes.length;
	}

	/**
	 * Add the node to the list of successors
	 * 
	 * @param n
	 * @return
	 */
	public boolean addNode(BaseNode n, Rete engine) throws AssertException {
		boolean add = false;
		// check if not inserted yet and free space for subchild:
		// if (!containsNode(this.childNodes, n) && childNodes.length <
		// maxChildCount) {
		if (childNodes.length < maxChildCount) {
			// inform added child node:
			BaseNode weWillAddThisNode = n.evAdded(this, engine);
			if (weWillAddThisNode != null) {
				// add to own list:
				this.childNodes = ConversionUtils.add(this.childNodes,
						weWillAddThisNode);
				mountChild(weWillAddThisNode, engine);
				add = true;
			} else {
				throw new AssertException(
						"Adding Node not Possible, Child does not want to be added");
			}
		}

		try {
			checkForConsistence();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return add;
	}

	protected abstract void mountChild(BaseNode newChild, Rete engine)
			throws AssertException;

	/**
	 * This node has been added to the given parant node
	 * 
	 * @param n
	 * @return
	 */
	protected BaseNode evAdded(BaseNode newParentNode, Rete engine) {
		// we have been added to the new parent, add parent to own list:
		if (!containsNode(this.parentNodes, newParentNode)
				&& parentNodes.length < maxParentCount) {
			// add to own list:
			this.parentNodes = ConversionUtils.add(this.parentNodes,
					newParentNode);
			return this;
		}
		return null;
	}

	/**
	 * remove the node from the succesors
	 * 
	 * @param n
	 * @return
	 * @throws RetractException
	 */
	public boolean removeNode(BaseNode n, Rete engine) throws RetractException {
		boolean rem = false;
		if (containsNode(this.childNodes, n))
			// inform removed child node:
			if (n.evRemoved(this)) {
				this.childNodes = ConversionUtils.remove(this.childNodes, n);
				unmountChild(n, engine);
				// dec own node use count
				if (getChildCount() == 0)
					evZeroUseCount(engine);
				rem = true;
			}
		return rem;
	}

	protected abstract void unmountChild(BaseNode oldChild, Rete engine)
			throws RetractException;

	public void destroy(Rete engine) throws RetractException {
		for (BaseNode node : parentNodes) {
			node.removeNode(this, engine);

		}
		// for (int i = 0; i < parentNodes.length; i++) {
		// parentNodes[i].removeNode(this, engine);
		// }
	}

	/**
	 * This node has been added to the given parant node
	 * 
	 * @param n
	 * @return
	 */
	private boolean evRemoved(BaseNode oldParentNode) {
		// we have been added to the new parent, add parent to own list:
		if (containsNode(this.parentNodes, oldParentNode)) {
			// add to own list:
			this.parentNodes = ConversionUtils.remove(this.parentNodes,
					oldParentNode);
			return true;
		}
		return false;
	}

	/**
	 * This method stub handles what happens if the usecount gets zero possible
	 * actions: remove self from parents:
	 * 
	 * @return
	 */
	protected void evZeroUseCount(Rete engine) {
		try {
			destroy(engine);
		} catch (RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Subclasses need to implement clear and make sure all memories are cleared
	 * properly.
	 */
	public void propagateClear() {
		for (BaseNode nNode : childNodes) {
			clear();
			nNode.propagateClear();
		}
	}

	public abstract void clear();

	/**
	 * toPPString should return a string format, but formatted nicely so it's
	 * easier for humans to read. Chances are this method will be used in
	 * debugging mode, so the more descriptive the string is, the easier it is
	 * to figure out what the node does.
	 * 
	 * @return
	 */
	public String toPPString() {
		return toString();
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Type: ");
		result.append(this.getClass().getSimpleName());
		result.append("\nID: ");
		result.append(getNodeId());
		result.append("\nSubnodes: ");
		result.append(getChildCount());
		result.append("\n");
		return result.toString();
	}

	protected boolean containsNode(BaseNode[] list, BaseNode n) {
		boolean cn = false;
		for (int idx = 0; idx < list.length; idx++) {
			if (list[idx] == n) {
				cn = true;
				break;
			}
		}
		return cn;
	}

	/**
	 * method for propogating the retract
	 * 
	 * @param fact
	 * @param engine
	 */
	protected void propogateRetract(Assertable fact, Rete engine)
			throws RetractException {
		for (BaseNode nNode : childNodes) {
			nNode.retractFact(fact, engine, this);
		}
	}

	/**
	 * Method is used to pass a fact to the successor nodes
	 * 
	 * @param fact
	 * @param engine
	 */
	protected void propogateAssert(Assertable fact, Rete engine)
			throws AssertException {
		for (BaseNode nNode : childNodes) {
			nNode.assertFact(fact, engine, this);
		}
	}

	// use of good old Delphi sender...
	public abstract void assertFact(Assertable fact, Rete engine,
			BaseNode sender) throws AssertException;

	public abstract void retractFact(Assertable fact, Rete engine,
			BaseNode sender) throws RetractException;

	public boolean isRightNode() {
		return true;
	}

	public int getNodeId() {
		return this.nodeID;
	}

	/*
	 * this method is for debugging purposes only. it checks the consistency of
	 * parent- and child-arrays. since that is a non-productive method, it is
	 * not optimized and a bit redundant ;)
	 */
	protected void checkForConsistence() throws Exception {
		for (BaseNode child : childNodes) {
			if (!containsNode(child.parentNodes, this)) {
				throw new Exception("Array inconsistent. my("
						+ this.getNodeId() + ") child-array contains "
						+ child.getNodeId()
						+ " but it doesnt holds me as parent!");
			}
		}
		for (BaseNode parent : parentNodes) {
			if (!containsNode(parent.childNodes, this)) {
				throw new Exception("Array inconsistent. my("
						+ this.getNodeId() + ") parent-array contains "
						+ parent.getNodeId()
						+ " but it doesnt holds me as child!");
			}
		}
	}

	/**
	 * this method draws itself onto a Graphics2D canvas. The alternative
	 * is to extend JComponent here, but imho, that would be overkill here.
	 * Furthermore, that would be conceptionally wrong, since the JComponent
	 * representation is not the major thing in the BaseNode class. It is protected,
	 * since it will be called from a higher-level (public) method inside BaseNode.
	 */
	protected void drawNode(int x, int y, int height, int width, int alpha, Graphics2D canvas){
		canvas.setBackground( new Color(155,0,0,alpha) );
		canvas.setColor(  new Color(85,0,0,alpha) );
		canvas.fillRect(x, y, width, height);
		canvas.drawRect(x, y, width, height);
		drawId(x,y,height,width,canvas);
	}
	
	protected void drawId(int x, int y, int height, int width, Graphics2D canvas){
		int paintX = x + height/2;
		int paintY = y + width/2;
		canvas.drawString( String.valueOf(nodeID) , paintX, paintY);
	}
	
	
	public final static int shapeWidth = 48;
	public final static int shapeHeight = 24;
	public final static int shapeGapWidth = 20;
	public final static int shapeGapHeight = 10;

	
	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point bottomLeft = new Point(-shapeWidth/2 ,  -shapeHeight/2);
	protected static Point bottomRight = new Point(shapeWidth/2 , -shapeHeight/2);
	protected static Point topLeft = new Point(-shapeWidth/2 ,  shapeHeight/2);
	protected static Point topRight = new Point(shapeWidth/2 ,  shapeHeight/2);
	
	protected static double angleTopLeft = atan3(topLeft.y, topLeft.x);
	protected static double angleTopRight = atan3(topRight.y, topRight.x);
	protected static double angleBottomLeft = atan3(bottomLeft.y, bottomLeft.x);
	protected static double angleBottomRight = atan3(bottomRight.y, bottomRight.x);
	
	/**
	 * draws a node. here, the row gives a logical y position
	 * (1 for first row, 2 for second and so on) and the column
	 * is analog but only the half width is one unit here.
	 * @param row the row to paint (from 0 to infinite)
	 * @param fromColumn the first column you can paint
	 * @param alpha alpha value
	 * @param canvas the canvas
	 * @param setup the setup
	 * @return the width
	 */
	public int drawNode(int row, int fromColumn, int alpha, Graphics2D canvas, VisualizerSetup setup, Map<BaseNode,Point> positions) {
		int firstColumn = fromColumn;
		for (BaseNode child : childNodes ){
			// only draw the child node, iff i am the "primary parent"
			if (!(child.parentNodes[0] == this)) continue;
			firstColumn += child.drawNode(row+1, firstColumn, alpha, canvas, setup, positions);
		}
		int width = firstColumn - fromColumn;
		if (width == 0) width = 2;
		// calculate real positions and draw them
			int column = fromColumn + width/2;
			int y = (shapeHeight+shapeGapHeight)*row + shapeGapHeight/2;
			int x = ((shapeWidth+shapeGapWidth)/2)  *column + shapeGapWidth/2;
			x += setup.offsetX;
			y += setup.offsetY;
			x *= setup.scaleX;
			y *= setup.scaleY;
			int w = shapeWidth;
			int h = shapeHeight;
			h *= setup.scaleY;
			w *= setup.scaleX;
			positions.put(this, new Point(column, row));
			drawNode(x, y, h, w, alpha, canvas);
		//
		return width;
	}

	
	protected static Point intersectionPoint(Point l1p1, Point l1p2, Point l2p1, Point l2p2) {
		Point result = new Point();
		double x1 = l1p1.x;
		double x2 = l1p2.x;
		double x3 = l2p1.x;
		double x4 = l2p2.x;
		double y1 = l1p1.y;
		double y2 = l1p2.y;
		double y3 = l2p1.y;
		double y4 = l2p2.y;

		
		double denom = ( (y4-y3)*(x2-x1)-(x4-x3)*(y2-y1)  );
		double ua = ( (x4-x3)*(y1-y3)-(y4-y3)*(x1-x3) ) / denom;
		//double ub = ( (x2-x1)*(y1-y3)-(y2-y1)*(x1-x3) ) / denom;
		
		result.x = (int) (x1+ua*(x2-x1));
		result.y = (int) (y1+ua*(y2-y1));

		return result;
	}
	
	public static double atan3(double y, double x) {
		double result = Math.atan2(y, x);
		if (result<0) result += 2*Math.PI;
		return result;
	}
	
	public static Point getLineEndPoint(Point target, Point me) {
		double angle = atan3(-target.y+me.y, target.x-me.x);
		Point p1;
		Point p2;
		System.out.println(angle);
		
		System.out.println("boombe");
		System.out.println(angleTopRight);
		System.out.println(angleTopLeft);
		System.out.println(angleBottomRight);
		System.out.println(angleBottomLeft);
		if (angle < angleTopRight || angle >= angleBottomRight) {
			// RIGHT SIDE
			p1 = topRight;
			p2 = bottomRight;
		} else if (angle < angleTopLeft) {
			// TOP SIDE
			p1 = topLeft;
			p2 = topRight;
		} else if (angle < angleBottomLeft) {
			// LEFT SIDE
			p1 = topLeft;
			p2 = bottomLeft;
		} else {
			// BOTTOM SIDE
			p1 = bottomRight;
			p2 = bottomLeft;
		}
		Point pp1 = new Point(p1);
		Point pp2 = new Point(p2);
		
		pp1.x += me.x;
		pp1.y = me.y - pp1.y;
		pp2.x += me.x;
		pp2.y = me.y - pp2.y;
		
		System.out.println("help1"+pp1);
		System.out.println("help2"+pp2);
		System.out.println("me"+me);
		System.out.println("target"+target);
		System.out.println("newpoint"+intersectionPoint(pp1, pp2, target, me));
		
		return intersectionPoint(pp1, pp2, target, me);
	}

}
