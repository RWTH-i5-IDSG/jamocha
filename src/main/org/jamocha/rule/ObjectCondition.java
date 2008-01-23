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

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.StopCompileException;
import org.jamocha.rete.Template;
import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler;

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
	 * a list for the RETE nodes created by RuleCompiler
	 */
	protected List<Node> nodes = new ArrayList<Node>();

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

	@Deprecated
	public Template getTemplate() {
		return this.template;
	}

	@Deprecated
	public void setTemplate(Template tmpl) {
		this.template = tmpl;
	}

	public String getVariableName() {
		return this.varname;
	}

	public void setVariableName(String name) {
		this.varname = name;
	}

	public List<Constraint> getConstraints() {
		return propConditions;
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

	public Node getNode(int idx) {
		if (idx < nodes.size())
			return nodes.get(idx);
		else
			return null;
	}

	/**
	 * Add a node to an ObjectCondition. the node should only be AlphaNodes and
	 * not join nodes.
	 */
	public void addNode(Node node) {
		if (!this.nodes.contains(node)) {
			this.nodes.add(node);
		}
	}

	/**
	 * Return the last alphaNode for the object pattern
	 */
	public Node getLastNode() {
		if (this.nodes.size() > 0) {
			return nodes.get(nodes.size() - 1);
		} else {
			return null;
		}
	}

	public Node getFirstNode() {
		if (this.nodes.size() > 0) {
			return (Node) this.nodes.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Method will return a list of all the BoundConstraints
	 */
	@Deprecated
	public List<Constraint> getAllBoundConstraints() {
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
				binds.add(c);
			}
		}
		return binds;
	}

	@Deprecated
	public List<Constraint> getBoundConstraints() {
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

	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex)
			throws EvaluationException, StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public Object clone() throws CloneNotSupportedException {
		ObjectCondition result = new ObjectCondition();

		result.templateName = this.templateName;
		result.varname = this.varname;
		result.negated = this.negated;
		result.template = this.template;
		result.totalComplexity = this.totalComplexity;
		result.varname = this.varname;

		result.propConditions = new ArrayList<Constraint>();
		for (Constraint c : propConditions)
			result.propConditions.add(c);

		result.nodes = new ArrayList<Node>();
		for (Node b : nodes)
			result.nodes.add(b);

		return result;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
