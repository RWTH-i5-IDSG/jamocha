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
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
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

	/**
	 * 
	 */
	public ObjectTypeNode(int id, Template deftemp) {
		super(id);
		this.deftemplate = deftemp;
	}

	public Template getDeftemplate() {
		return this.deftemplate;
	}

	@Override
	public void assertFact(Assertable fact, Rete engine, BaseNode sender)
			throws AssertException {
		if (((Fact) fact).getTemplate().equals(this.getDeftemplate())) {
			this.facts.add((Fact) fact);
			propogateAssert(fact, engine);
		}
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender)
			throws RetractException {
		if (facts.remove((Fact) fact))
			propogateRetract(fact, engine);
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

	protected void evZeroUseCount(Rete engine) {
		try {
			getRootNode().deactivateObjectTypeNode(this, engine);
		} catch (RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected void drawNode(int x, int y, int height, int width, List<BaseNode> selected, Graphics2D canvas){
		int alpha = (selected.contains(this)) ? 255 : 20;
		canvas.setBackground( new Color(90,255,90,alpha) );
		canvas.setColor(  new Color(15,200,15,alpha) );
		canvas.fillRect(x, y, width, height);
		canvas.drawRect(x, y, width, height);
		drawId(x,y,height,width,canvas);
		
		String dtn = deftemplate.getName();
		if (dtn.equals("bier")) {
			ImageIcon icon = IconLoader.getImageIcon("src/main/org/jamocha/rete/visualisation/images/bier.png");
			System.out.println(icon);
			int w = 48;
			int h = 48;
			icon.paintIcon(new JTextPane(), canvas, x, y);
			canvas.drawImage(icon.getImage(), x, y, w, h, null);
		} else if (dtn.equals("wurst")) {
			
		} else if (dtn.equals("salat")) {
			
		} 
		
		
	}

}
