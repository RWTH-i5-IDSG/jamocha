/*
 * Copyright 2002-2007 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
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
import java.util.List;

import org.jamocha.rete.Fact;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.visualisation.VisualizerSetup;

/**
 * @author Peter Lin
 * 
 * LIANode stands for Left Input Adapter Node. Left input adapter node is
 * responsible for creating a List to pass to the BetaNode. This is important
 * because the same fact may be re-asserted.
 */

//TODO: is LIANode really an alpha node? -jh
public class LIANode extends AbstractAlpha {

	public LIANode(int id, WorkingMemory memory) {
		super(id, memory);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void assertFact(WorkingMemoryElement fact, ReteNet net, BaseNode sender)
			throws AssertException {
		// add to own buffer list:
		workingMemory.addAlpha(this,fact);
		// build tuple and propagate:
		FactTuple tuple = new FactTupleImpl((Fact) fact);
		propogateAssert(tuple, net);
	}

	@Override
	public void retractFact(WorkingMemoryElement fact, ReteNet net, BaseNode sender)
			throws RetractException {
		if ( workingMemory.removeAlpha(this,fact) ){
			FactTuple tuple = new FactTupleImpl((Fact) fact);
			propogateRetract(tuple, net);
		}
	}

	protected void mountChild(BaseNode newChild, ReteNet net)
			throws AssertException {
		for (WorkingMemoryElement fact : workingMemory.getAlpha(this))
			// we have to send down a fact tuple:
			newChild.assertFact(new FactTupleImpl((Fact) fact), net, this);
	}

	protected void unmountChild(BaseNode oldChild, ReteNet net)
			throws RetractException {
		for (WorkingMemoryElement fact : workingMemory.getAlpha(this))
			// we have to send down a fact tuple:
			oldChild.retractFact(new FactTupleImpl((Fact) fact), net, this);
	}

	public boolean isRightNode() {
		return false;
	}

	
	@Override
	public boolean mergableTo(BaseNode other) {
		//return true: we should be equal to an other LIA-node 
		//(we may check if parent node (OTN) has same template)
		return other instanceof LIANode;
	}	
	
	//////////////////////////////////////////////////////////////
	/// GRAPHICS STUFF ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////	
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<BaseNode> selected, Graphics2D canvas){
		int alpha = (selected.contains(this)) ? 255 : 20;
		int[] xpoints = { x , x+width , (int)(x+width*0.8) , x+ (int)(width*0.2)       };
		int[] ypoints = { y , y       , y+height, y+height};
		canvas.setColor( new Color(28,255,252,alpha) );
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.setColor(  new Color(107,197,196,alpha) );
		canvas.drawPolygon(xpoints, ypoints, 4);
		canvas.setColor( new Color(0,0,0,alpha) );
		drawId(x,y,height,width,halfLineHeight,canvas);
	}
	
	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point bottomLeft = new Point((int)(-shapeWidth/2.8) ,  -shapeHeight/2);
	protected static Point bottomRight = new Point((int)(shapeWidth/2.8) , -shapeHeight/2);
	protected static Point topLeft = new Point(-shapeWidth/2 ,  shapeHeight/2);
	protected static Point topRight = new Point(shapeWidth/2 ,  shapeHeight/2);
	
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
