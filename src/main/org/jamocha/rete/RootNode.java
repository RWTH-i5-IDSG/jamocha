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
package org.jamocha.rete;

import java.io.Serializable;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.CollectionsFactory;

/**
 * @author Peter Lin
 * 
 * RootNode does not extend BaseNode like all other RETE nodes. This is done for
 * a couple of reasons.<br/>
 * <ul>
 * <li> RootNode doesn't need to have a memory </li>
 * <li> RootNode only has ObjectTypeNode for successors</li>
 * <li> RootNode doesn't need the toPPString and other string methods</li>
 * <li> RootNode doesn't need watch or profile capabilities</li>
 * </ul>
 * In the future, the design may change. For now, I've decided to keep it as
 * simple as necessary.
 */
public class RootNode implements Serializable {

	// input Nodes are linked to the net
	protected Map<Template, ObjectTypeNode> inputNodes = CollectionsFactory.newHashMap();

	// temp input nodes are not linked to the net and exists to temporal store
	// facts
	protected Map<Template, ObjectTypeNode> tempInputNodes = CollectionsFactory.newHashMap();

	/**
	 * 
	 */
	public RootNode() {
		super();
	}

	/**
	 * Add a new ObjectTypeNode. The implementation will check to see if the
	 * node already exists. It will only add the node if it doesn't already
	 * exist in the network.
	 * 
	 * @param Template
	 */

	public void addObjectTypeNode(Template template, Rete engine) {
		if (!this.inputNodes.containsKey(template) && !this.tempInputNodes.containsKey(template)) {
			ObjectTypeNode node = new ObjectTypeNode(engine.nextNodeId(), template);
			this.tempInputNodes.put(template, node);
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
	 * @return
	 */
	public ObjectTypeNode activateObjectTypeNode(Template template) {
		ObjectTypeNode result = this.tempInputNodes.remove(template);
		if (result != null) {
			this.inputNodes.put(template, result);
		} else
			result = this.inputNodes.get(template);
		result.incrementUseCount();
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
	public synchronized void assertObject(Fact fact, Rete engine, WorkingMemory mem) throws AssertException {
		// we assume Rete has already checked to see if the object
		// has been added to the working memory, so we just assert.
		// we need to lookup the defclass and deftemplate to assert
		// the object to the network
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(fact.getTemplate());
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fact.getTemplate());
		}
		if (otn != null) {
			otn.assertFact(fact, engine, mem);
		}
		if (fact.getTemplate().getParent() != null) {
			assertObjectParent(fact, fact.getTemplate().getParent(), engine, mem);
		}
	}

	/**
	 * Method will get the deftemplate's parent and do a lookup
	 * 
	 * @param fact
	 * @param templates
	 * @throws AssertException
	 */
	public synchronized void assertObjectParent(Fact fact, Template template, Rete engine, WorkingMemory mem) throws AssertException {
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(template);
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fact.getTemplate());
		}
		if (otn != null) {
			otn.assertFact(fact, engine, mem);
		}
		if (template.getParent() != null) {
			assertObjectParent(fact, template.getParent(), engine, mem);
		}
	}

	/**
	 * Retract an object from the Working memory
	 * 
	 * @param objInstance
	 */
	public synchronized void retractObject(Fact fact, Rete engine, WorkingMemory mem) throws RetractException {
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(fact.getTemplate());
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fact.getTemplate());
		}
		if (otn != null) {
			otn.retractFact(fact, engine, mem);
		}
		if (fact.getTemplate().getParent() != null) {
			retractObjectParent(fact, fact.getTemplate().getParent(), engine, mem);
		}
	}

	/**
	 * Method will get the deftemplate's parent and do a lookup
	 * 
	 * @param fact
	 * @param templates
	 * @throws AssertException
	 */
	public synchronized void retractObjectParent(Fact fact, Template template, Rete engine, WorkingMemory mem) throws RetractException {
		ObjectTypeNode otn = (ObjectTypeNode) this.inputNodes.get(template);
		if (otn == null) {
			otn = (ObjectTypeNode) this.tempInputNodes.get(fact.getTemplate());
		}
		if (otn != null) {
			otn.retractFact(fact, engine, mem);
		}
		if (template.getParent() != null) {
			retractObjectParent(fact, template.getParent(), engine, mem);
		}
	}

	public synchronized void clear() {
		this.inputNodes.clear();
		this.tempInputNodes.clear();
	}
}
