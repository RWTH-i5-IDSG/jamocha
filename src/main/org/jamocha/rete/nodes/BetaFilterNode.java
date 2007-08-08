/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.joinfilter.JoinFilter;
import org.jamocha.rete.nodes.joinfilter.JoinFilterException;
import org.jamocha.rete.visualisation.VisualizerSetup;

/**
 * @author Peter Lin
 * 
 * BetaNode is the basic class for join nodes. The implementation uses
 * BetaMemory for the left and AlphaMemory for the right. When the left and
 * right match, the facts are merged and propogated to the succeeding nodes.
 * This is an important distinction for a couple of reasons.
 * 
 * 1. The next join may join one or more objects. 2. Rather than store the facts
 * needed for the next join in a global map of map, it's more efficient to
 * simply merge the two arrays and pass it on. 3. It isn't sufficient to pass
 * just the bound attributes of this node to the next.
 * 
 * Some important notes. If a rule defines a join, which doesn't compare a slot
 * from one fact against the slot of a different fact, the node simply
 * propogates.
 */
public class BetaFilterNode extends AbstractBeta {

	private static final long serialVersionUID = 1L;

	/**
	 * binding for the join
	 */
	protected List<JoinFilter> filters = null;

	/**
	 * The operator for the join by default is equal. The the join doesn't
	 * comparing values, the operator should be set to -1.
	 */
	protected int operator = Constants.EQUAL;

	public BetaFilterNode(int id) {
		super(id);
	}

	/**
	 * Set the bindings for this join
	 * 
	 * @param binds
	 * @throws AssertException
	 */
	public void setFilters(List<JoinFilter> filters, Rete engine)
			throws AssertException {
		this.filters = filters;
	}

	/**
	 * Method will use the right binding to perform the evaluation of the join.
	 * Since we are building joins similar to how CLIPS and other rule engines
	 * handle it, it means 95% of the time the right fact list only has 1 fact.
	 * 
	 * @param leftlist
	 * @param right
	 * @return
	 */
	protected boolean evaluate(FactTuple tuple, Fact right) {
		if (filters != null) {
			// we iterate over the binds and evaluate the facts
			for (JoinFilter filter : filters) {
				try {
					if (!filter.evaluate(right, tuple))
						return false;
				} catch (JoinFilterException e) {
					// TODO make good error output
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public void addFilter(JoinFilter f) {
		if (filters == null) filters = new ArrayList<JoinFilter>();
		filters.add(f);
	}

	@Override
	protected void mountChild(BaseNode newChild, Rete engine)
			throws AssertException {
		Iterator<FactTuple> itr = mergeMemory.iterator();
		while (itr.hasNext()) {
			newChild.assertFact(itr.next(), engine, this);
		}
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine)
			throws RetractException {
		Iterator<FactTuple> itr = mergeMemory.iterator();
		while (itr.hasNext()) {
			oldChild.retractFact(itr.next(), engine, this);
		}
	}

	public String toPPString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toPPString());
		sb.append("\nFilters: ");
		if (filters != null) {
			for (JoinFilter f : filters)
				sb.append(f.toPPString()).append("\n");
		} else
			sb.append("none\n");
		return sb.toString();
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////
	/// GRAPHICS STUFF ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////	
	
	protected void drawNode(int x, int y, int height, int width,  List<BaseNode> selected, Graphics2D canvas){
		int alpha = (selected.contains(this)) ? 255 : 20;
		int[] xpoints = { x , x+width , (int)(x+width*0.8) , x+ (int)(width*0.2)       };
		int[] ypoints = {  y+height, y+height, y , y  };
		canvas.setColor( new Color(0,255,0,alpha) );
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.setColor(  new Color(54,208,55,alpha) );
		canvas.drawPolygon(xpoints, ypoints, 4);
		drawId(x,y,height,width,canvas);
	}
	
	
	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point topLeft = new Point((int)(-shapeWidth/2.8) ,  shapeHeight/2);
	protected static Point topRight = new Point((int)(shapeWidth/2.8) , shapeHeight/2);
	protected static Point bottomLeft = new Point(-shapeWidth/2 ,  -shapeHeight/2);
	protected static Point bottomRight = new Point(shapeWidth/2 ,  -shapeHeight/2);
	
	protected static double angleTopLeft = atan3(topLeft.y, topLeft.x);
	protected static double angleTopRight = atan3(topRight.y, topRight.x);
	protected static double angleBottomLeft = atan3(bottomLeft.y, bottomLeft.x);
	protected static double angleBottomRight = atan3(bottomRight.y, bottomRight.x);
	
	
	
	public Point getLineEndPoint(Point target, Point me, VisualizerSetup setup) {
		return getLineEndPoint2(target,me,setup,
				angleTopRight, angleTopLeft, angleBottomRight, angleBottomLeft,
				topRight,topLeft,bottomRight,bottomLeft
		);
	}

}
