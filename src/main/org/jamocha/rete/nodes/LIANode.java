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

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * LIANode stands for Left Input Adapter Node. Left input adapter node is
 * responsible for creating a List to pass to the BetaNode. This is important
 * because the same fact may be re-asserted.
 */
public class LIANode extends AbstractAlpha {

	public LIANode(int id) {
		super(id);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void assertFact(Assertable fact, Rete engine, BaseNode sender)
			throws AssertException {
		// add to own buffer list:
		facts.add((Fact) fact);
		// build tuple and propagate:
		FactTuple tuple = new FactTuple((Fact) fact);
		propogateAssert(tuple, engine);
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender)
			throws RetractException {
		assert (fact instanceof Fact);
		if (facts.remove((Fact) fact)) {
			FactTuple tuple = new FactTuple((Fact) fact);
			propogateRetract(tuple, engine);
		}
	}

	protected void mountChild(BaseNode newChild, Rete engine)
			throws AssertException {
		for (Fact fact : facts)
			// we have to send down a fact tuple:
			newChild.assertFact(new FactTuple((Fact) fact), engine, this);
	}

	protected void unmountChild(BaseNode oldChild, Rete engine)
			throws RetractException {
		for (Fact fact : facts)
			// we have to send down a fact tuple:
			oldChild.retractFact(new FactTuple((Fact) fact), engine, this);
	}

	public boolean isRightNode() {
		return false;
	}

	
	
	
	
	
	//////////////////////////////////////////////////////////////
	/// GRAPHICS STUFF ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////	
	
	protected void drawNode(int x, int y, int height, int width, int alpha, Graphics2D canvas){
		canvas.setBackground( new Color(100,100,255,alpha) );
		canvas.setColor(  new Color(15,15,200,alpha) );
		
		int x2 = x + width/2;
		int y2 = y + height/2;
		
		int[] xpoints = {topLeft.x+x2, topRight.x+x2, bottomRight.x+x2, bottomLeft.x+x2};
		int[] ypoints = {topLeft.y+y2, topRight.y+y2, bottomRight.y+y2, bottomLeft.y+y2};
		canvas.fillPolygon(xpoints, ypoints, 4);
		canvas.drawPolygon(xpoints, ypoints, 4);
		
		drawId(x,y,height,width,canvas);
	}
	
	
	// THIS STUFF IS FOR CALCULATING SOME DRAWING INTERNALS
	protected static Point bottomLeft = new Point( (int)(-shapeWidth/2.4) ,  shapeHeight/2);
	protected static Point bottomRight = new Point(  (int)(shapeWidth/2.4) , shapeHeight/2);
	protected static Point topLeft = new Point(-shapeWidth/2 ,  -shapeHeight/2);
	protected static Point topRight = new Point(shapeWidth/2 ,  -shapeHeight/2);
	protected static double angleTopLeft = atan3(topLeft.y, topLeft.x);
	protected static double angleTopRight = atan3(topRight.y, topRight.x);
	protected static double angleBottomLeft = atan3(bottomLeft.y, bottomLeft.x);
	protected static double angleBottomRight = atan3(bottomRight.y, bottomRight.x);

}
