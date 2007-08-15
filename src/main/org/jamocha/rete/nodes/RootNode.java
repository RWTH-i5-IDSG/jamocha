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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.visualisation.VisualizerSetup;

public class RootNode extends BaseNode {

	public RootNode(int id) {
		super(id);
		this.maxChildCount = Integer.MAX_VALUE;
		this.maxParentCount = 0;
	}

	private static final long serialVersionUID = 1L;

	// input Nodes are linked to the net
	protected Map<Template, ObjectTypeNode> inputNodes = new HashMap<Template, ObjectTypeNode>();

	// temp input nodes are not linked to the net and exists to temporal store
	// facts
	protected Map<Template, ObjectTypeNode> tempInputNodes = new HashMap<Template, ObjectTypeNode>();

	/**
	 * Add a new ObjectTypeNode. The implementation will check to see if the
	 * node already exists. It will only add the node if it doesn't already
	 * exist in the network.
	 * 
	 * @param Template
	 */

	public void addObjectTypeNode(Template template, Rete engine) {
		if (!inputNodes.containsKey(template)
				&& !tempInputNodes.containsKey(template)) {
			ObjectTypeNode node = new ObjectTypeNode(engine.getNet().nextNodeId(),
					template);
			tempInputNodes.put(template, node);
		}
	}

	/**
	 * The current implementation just removes the ObjectTypeNode and doesn't
	 * prevent the removal. The method should be called with care, since
	 * removing the ObjectTypeNode can have serious negative effects. This would
	 * generally occur when an undeftemplate occurs.
	 */
	public void removeObjectTypeNode(ObjectTypeNode node) {
		this.inputNodes.remove(node.getDeftemplate());
		this.tempInputNodes.remove(node.getDeftemplate());
	}

	/**
	 * Return the HashMap with all the ObjectTypeNodes
	 * 
	 * @return ObjectTypeNode
	 * @throws AssertException
	 */
	public ObjectTypeNode activateObjectTypeNode(Template template, ReteNet net)
			throws AssertException {
		ObjectTypeNode result = this.tempInputNodes.remove(template);
		if (result != null) {
			inputNodes.put(template, result);
			addNode(result, net);
		} else
			result = inputNodes.get(template);
		return result;
	}

	public void deactivateObjectTypeNode(ObjectTypeNode node, ReteNet net)
			throws RetractException {
		Template tmpl = node.getDeftemplate();
		inputNodes.remove(tmpl);
		tempInputNodes.put(tmpl, node);
		removeNode(node, net);
	}

	/**
	 * Return the HashMap with all the ObjectTypeNodes
	 * 
	 * @return
	 */
	public Map getObjectTypeNodes() {
		return this.inputNodes;
	}

	/**
	 * assertObject begins the pattern matching
	 * 
	 * @param fact
	 * @param engine
	 * @param mem
	 * @throws AssertException
	 */
	public synchronized void assertObject(Fact fact, ReteNet net)
			throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		// we need to lookup the defclass and deftemplate to assert
		// the object to the network
		this.assertFact(fact, net, this);
	}

	/**
	 * Retract an object from the Working memory
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact, ReteNet net)
			throws RetractException {
		this.retractFact(fact, net, this);
	}

	/**
	 * Method will get the deftemplate's parent and do a lookup
	 * 
	 * @param fact
	 * @param templates
	 * @throws AssertException
	 */

	public synchronized void clear() {
		this.inputNodes.clear();
		this.tempInputNodes.clear();
	}

	@Override
	public void assertFact(Assertable fact, ReteNet net, BaseNode sender)
			throws AssertException {
		Fact fct = (Fact) fact;
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(fct
				.getTemplate());
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fct.getTemplate());
		}
		if (otn != null) {
			otn.assertFact(fact, net, sender);
		}
	}

	@Override
	protected void mountChild(BaseNode newChild, ReteNet net)
			throws AssertException {
		// nothing to do: facts are allready asserted to all possible otn
	}

	@Override
	public void retractFact(Assertable fact, ReteNet net, BaseNode sender)
			throws RetractException {
		Fact fct = (Fact) fact;
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(fct
				.getTemplate());
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fct.getTemplate());
		}
		if (otn != null) {
			otn.retractFact(fact, net, sender);
		}
	}

	@Override
	protected void unmountChild(BaseNode oldChild, ReteNet net)
			throws RetractException {
		// nothing to do: facts are allready asserted to all possible otn
	}

	
	
	
	
	
	
	//////////////////////////////////////////////////////////////
	/// GRAPHICS STUFF ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////	
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<BaseNode> selected, Graphics2D canvas){
		int alpha = (selected.contains(this)) ? 255 : 20;
		canvas.setBackground( new Color(0,0,0,alpha) );
		canvas.setColor(  new Color(40,40,40,alpha) );
		canvas.fillOval(x,y,width,height);
		canvas.drawOval(x,y,width,height);
		canvas.setColor( new Color(255,255,255,alpha) );
		drawId(x,y,height,width,halfLineHeight,canvas);
	}
	
	
	public Point getLineEndPoint(Point target, Point me, VisualizerSetup setup) {
		double angle = atan3(-target.y+me.y, target.x-me.x);
		
		double unitCircleX = Math.cos(angle);
		double unitCircleY = Math.sin(angle);
		
		Point result = new Point();
		result.x = (int)(unitCircleX * (BaseNode.shapeWidth * setup.scaleX /2) + me.x);
		result.y = (int)(-unitCircleY * (BaseNode.shapeHeight * setup.scaleY /2) + me.y);
		return result;
	}
	
	
	
}
