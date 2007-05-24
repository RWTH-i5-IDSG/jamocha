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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Peter Lin
 * 
 * ObjectCondition is equivalent to RuleML 0.83 resourceType. ObjectCondition
 * matches on the fields of an object. The patterns may be simple value
 * comparisons, or joins against other objects.
 */
public class ObjectCondition extends AbstractCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String templateName = null;

	protected String varname = null;

	protected List<Constraint> propConditions = new ArrayList<Constraint>();

	/**
	 * In the case the object pattern is negated, the boolean would be set to
	 * true.
	 */
	protected boolean negated = false;

	/**
	 * a list for the RETE nodes created by RuleCompiler
	 */
	protected List<BaseNode> nodes = new ArrayList<BaseNode>();

	protected Template template = null;

	/**
	 * 
	 */
	public ObjectCondition() {
		super();
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String name) {
		this.templateName = name;
	}

	public Template getTemplate() {
		return this.template;
	}

	public void setTemplate(Template tmpl) {
		this.template = tmpl;
	}

	public String getVariableName() {
		return this.varname;
	}

	public void setVariableName(String name) {
		this.varname = name;
	}

	/**
	 * set whether or not the pattern is negated
	 * 
	 * @param negate
	 */
	public void setNegated(boolean negate) {
		this.negated = negate;
	}

	/**
	 * by default patterns are not negated. Negated Conditional Elements (aka
	 * object patterns) are expensive, so they should be used with care.
	 * 
	 * @return
	 */
	public boolean getNegated() {
		return this.negated;
	}

	public Constraint[] getConstraints() {
		Constraint[] con = new Constraint[propConditions.size()];
		return (Constraint[]) propConditions.toArray(con);
	}

	public void addConstraint(Constraint con) {
		this.propConditions.add(con);
	}

	public void addConstraint(Constraint con, int position) {
		this.propConditions.add(0, con);
	}

	public void removeConstraint(Constraint con) {
		this.propConditions.remove(con);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Condition#compare(woolfel.engine.rule.Condition)
	 */
	public boolean compare(Complexity cond) {
		return false;
	}

	/**
	 * Return the List of the RETE nodes for the ObjectCondition
	 */
	public List getNodes() {
		return this.nodes;
	}

	public BaseNode getNode(int idx){
		if (idx < nodes.size())
		return nodes.get(idx);
		else
			return null;
	}

	
	/**
	 * Add a node to an ObjectCondition. the node should only be AlphaNodes and
	 * not join nodes.
	 */
	public void addNode(BaseNode node) {
		if (!this.nodes.contains(node)) {
			this.nodes.add(node);
		}
	}

	/**
	 * Return the last alphaNode for the object pattern
	 */
	public BaseNode getLastNode() {
		if (this.nodes.size() > 0) {
			return nodes.get(nodes.size()-1);
		} else {
			return null;
		}
	}

	public BaseNode getFirstNode() {
		if (this.nodes.size() > 0) {
			return (BaseNode) this.nodes.get(0);
		} else {
			return null;
		}
	}

	/**
	 * if the ObjectCondition
	 */
	public boolean hasBindings() {
		Iterator itr = propConditions.iterator();
		while (itr.hasNext()) {
			Object ob = itr.next();
			if (ob instanceof BoundConstraint) {
				return true;
			} else if (ob instanceof PredicateConstraint) {
				return ((PredicateConstraint) ob).isPredicateJoin();
			}
		}
		return false;
	}

	/**
	 * Method will return a list of all the BoundConstraints
	 */
	public List getAllBoundConstraints() {
		ArrayList<Constraint> binds = new ArrayList<Constraint>();
		Iterator<Constraint> itr = propConditions.iterator();
		while (itr.hasNext()) {
			Constraint c = itr.next();
			if (c instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint) c;
				if (!bc.firstDeclaration()) {
					binds.add(c);
				}
			} else if (c instanceof PredicateConstraint) {
				if (((PredicateConstraint) c).isPredicateJoin()) {
					binds.add(c);
				}
			}
		}
		return binds;
	}

	public List getBoundConstraints() {
		ArrayList<Constraint> binds = new ArrayList<Constraint>();
		Iterator<Constraint> itr = propConditions.iterator();
		while (itr.hasNext()) {
			Constraint c = itr.next();
			if (c instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint) c;
				if (!bc.firstDeclaration() && !bc.getIsObjectBinding()) {
					binds.add(c);
				}
			}
		}
		return binds;
	}

	/**
	 * clears the RETE nodes
	 */
	public void clear() {
		nodes.clear();
	}
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}
}
