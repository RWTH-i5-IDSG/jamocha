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
import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler;

/**
 * @author Peter Lin
 * 
 * ExistCondition for existential quantifier.
 */
public class ExistCondition extends ConditionWithNested {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	protected List<Condition> nestedCE = new ArrayList<Condition>();

	protected List<Node> nodes = new ArrayList<Node>();

	protected boolean isFirstCE = false;

	/**
	 * 
	 */
	public ExistCondition() {
		super();
	}

	/**
	 * The rule compiler should call this and set the condition as the first CE
	 * in the rule.
	 * 
	 * @param first
	 */
	public void setIsFirstCE(boolean first) {
		this.isFirstCE = first;
	}

	public boolean compare(Complexity cond) {
		return false;
	}

	
	public void addNestedConditionElement(Condition ce) {
		this.nestedCE.add(ce);
	}

	public List<Condition> getNestedConditionalElement() {
		return this.nestedCE;
	}

	public List<Node> getNodes() {
		return new ArrayList<Node>();
	}

	/**
	 * the method doesn't apply and isn't implemented currently
	 */
	public void addNode(Node node) {
		nodes.add(node);
	}

	public boolean hasBindings() {
		return false;
	}

	public Node getFirstNode() {
		if (nodes.size() > 0)
			return nodes.get(0);
		else
			return null;
	}

	/**
	 * this is specific to exist conditions
	 * 
	 * @return
	 */
	public boolean hasObjectCondition() {
		boolean has = false;
		Iterator<Condition> itr = nestedCE.iterator();
		while (itr.hasNext()) {
			if (itr.next() instanceof ObjectCondition) {
				has = true;
				break;
			}
		}
		return has;
	}

	@Deprecated
	public List<BoundConstraint> getAllBoundConstraints() {
		ArrayList<BoundConstraint> bindings = new ArrayList<BoundConstraint>();
		Iterator<Condition> itr = nestedCE.iterator();
		while (itr.hasNext()) {
			Complexity con = (Complexity) itr.next();
			if (con instanceof ObjectCondition) {
				ObjectCondition oc = (ObjectCondition) con;
				for (Constraint c : oc.getConstraints()) {
					if (c instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) c;
						if (!bc.firstDeclaration()) {
							bindings.add(bc);
						}
					} 

				}
			}
		}
		return bindings;
	}

	public List<?> getBoundConstraints() {
		return null;
	}

	public void clear() {
		nodes.clear();
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}

	public List<Constraint> getConstraints() {
 		return null;
	}


	public Node getLastNode() {
		return ((ObjectCondition)nestedCE.get(0)).getLastNode();
	}
	
	protected String clipsName() {return "exists";}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
