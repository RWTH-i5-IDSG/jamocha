/*
 * Copyright 2002-2007 Peter Lin
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
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * ObjectTypeNode is the input node for a specific type. The node is created
 * with the appropriate Class. A couple of important notes about the
 * implementation of ObjectTypeNode.
 * 
 * <ul>
 * <li> the assertFact method does not check the deftemplate matches the fact.
 * this is because of inheritance.
 * <li> WorkingMemoryImpl checks to see if the fact's deftemplate has parents.
 * If it does, it will keep checking to see if there is an ObjectTypeNode for
 * the parent.
 * <li> if the template has a parent, it will assert it. this means
 * <li> any patterns for parent templates will attempt to pattern match
 * </ul>
 */
public class ObjectTypeNode extends AbstractAlpha implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The Class that defines object type
	 */
	private Template deftemplate = null;
	
	private Image icon = null;

	/**
	 * 
	 */
	public ObjectTypeNode(int id, Template deftemp) {
		super(id);
		this.deftemplate = deftemp;
		String templname = deftemp.getName();
		if (templname.equals(Constants.INITIAL_FACT)) templname="initialFact";
		ImageIcon ii = IconLoader.getImageIcon(templname,this.getClass());
		if (ii != null) {
			while (ii.getImageLoadStatus() == MediaTracker.LOADING)
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				icon = ii.getImage();
		}
	}

	public Template getDeftemplate() {
		return this.deftemplate;
	}

	@Override
	public void assertFact(Assertable fact, ReteNet net, BaseNode sender)
			throws AssertException {
		if (((Fact) fact).getTemplate().equals(this.getDeftemplate())) {
			this.facts.add((Fact) fact);
			propogateAssert(fact, net);
		}
	}
	
	@Override
	public boolean mergableTo(BaseNode other) {
		//obj also OTN and same template?
		if (other instanceof ObjectTypeNode)
		return this.deftemplate.equals(((ObjectTypeNode)other).deftemplate);
		return false;
	}


	@Override
	public void retractFact(Assertable fact, ReteNet net, BaseNode sender)
			throws RetractException {
		if (facts.remove((Fact) fact) != null)
			propogateRetract(fact, net);
	}

	public RootNode getRootNode() {
		// we only have one parent. this must be the rootnode
		if (getParentCount() > 0)
			return (RootNode) this.parentNodes[0];
		else
			return null;

	}

	/**
	 * this returns name of the deftemplate
	 */
	public String toPPString() {
		return super.toPPString() + "Template: " + this.deftemplate.getName()
				+ "\n";
	}

	protected void evZeroUseCount(ReteNet net) {
		try {
			getRootNode().deactivateObjectTypeNode(this, net);
		} catch (RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<BaseNode> selected, Graphics2D canvas){
		boolean isSelected=selected.contains(this);
		int alpha = (isSelected) ? 255 : 20;
		canvas.setColor( new Color(255,215,15,alpha) );
		canvas.fillRect(x, y, width, height);
		canvas.setColor(  new Color(208,181,44,alpha) );
		canvas.drawRect(x, y, width, height);
		canvas.setColor( new Color(0,0,0,alpha) );
		drawId(x,y,height,width, halfLineHeight,canvas);
		if (icon != null && isSelected) {
			float aspectRatio = icon.getWidth(null) / icon.getHeight(null);
			int w = (int) (height * aspectRatio);
			int h = height;
			int x1 = x + width - w/2;
			int y1 = y;
			canvas.drawImage(icon, x1, y1, w, h, null);
		}
		
		
	}
}
