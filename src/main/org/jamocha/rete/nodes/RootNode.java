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

import java.util.Map;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.CollectionsFactory;

public class RootNode extends BaseNode {

	public RootNode(int id) {
		super(id);
		this.maxChildCount =Integer.MAX_VALUE;
		this.maxParentCount =0;
	}

	private static final long serialVersionUID = 1L;

	// input Nodes are linked to the net
	protected Map<Template, ObjectTypeNode> inputNodes = CollectionsFactory.newHashMap();

	// temp input nodes are not linked to the net and exists to temporal store
	// facts
	protected Map<Template, ObjectTypeNode> tempInputNodes = CollectionsFactory.newHashMap();

	/**
	 * Add a new ObjectTypeNode. The implementation will check to see if the
	 * node already exists. It will only add the node if it doesn't already
	 * exist in the network.
	 * 
	 * @param Template
	 */

	public void addObjectTypeNode(Template template, Rete engine) {
		if (!inputNodes.containsKey(template) && !tempInputNodes.containsKey(template)) {
			ObjectTypeNode node = new ObjectTypeNode(engine.nextNodeId(), template);
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
	public ObjectTypeNode activateObjectTypeNode(Template template, Rete engine) throws AssertException {
		ObjectTypeNode result = this.tempInputNodes.remove(template);
		if (result != null) {
			this.inputNodes.put(template, result);
			this.addNode(result, engine);
		} else
			result = this.inputNodes.get(template);
		return result;
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
	public synchronized void assertObject(Fact fact, Rete engine) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		// we need to lookup the defclass and deftemplate to assert
		// the object to the network
		this.assertFact(fact, engine, this);
	}

	/**
	 * Retract an object from the Working memory
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact, Rete engine) throws RetractException {
		this.retractFact(fact, engine, this);
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
	public void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		Fact fct = (Fact) fact;
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(fct.getTemplate());
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fct.getTemplate());
		}
		if (otn != null) {
			otn.assertFact(fact, engine, sender);
		}
	}

	@Override
	protected void mountChild(BaseNode newChild, Rete engine) throws AssertException {
		// nothing to do: facts are allready asserted to all possible otn
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		Fact fct = (Fact) fact;
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(fct.getTemplate());
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fct.getTemplate());
		}
		if (otn != null) {
			otn.retractFact(fact, engine, sender);
		}
	}

	@Override
	protected void unmountChild(BaseNode oldChild, Rete engine) throws RetractException {
		// nothing to do: facts are allready asserted to all possible otn
	}

	@Override
	public String toPPString() {
		StringBuilder sb = new StringBuilder();
		sb.append("InputNodes: ");
		sb.append(inputNodes.toString());
		sb.append("ObjectTypeNodes(not used in Net): ");
		sb.append(tempInputNodes.toString());
		return sb.toString();
	}
	
	public String toString() {
		return "Root Node";
	}
	
}
