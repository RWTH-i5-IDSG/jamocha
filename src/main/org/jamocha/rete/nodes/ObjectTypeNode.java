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

import java.io.Serializable;

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
	public void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		if (((Fact) fact).getTemplate().equals(this.getDeftemplate())) {
			this.facts.add((Fact) fact);
			propogateAssert(fact, engine);
		}
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		if (facts.remove((Fact)fact))
			propogateRetract(fact, engine);
	}

	/**
	 * this returns name of the deftemplate
	 */
	public String toString() {
		return "ObjectTypeNode(" + this.deftemplate.getName() + ")";
	}

	/**
	 * this returns name of the deftemplate
	 */
	public String toPPString() {
		return "ObjectTypeNode-" + this.nodeID + "> for Template(" + this.deftemplate.getName() + ")";
	}

}
