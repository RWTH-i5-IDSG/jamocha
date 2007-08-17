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
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Peter Lin
 * 
 * ExistCondition for existential quantifier.
 */
public class ExistCondition extends ConditionWithNested {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	protected List nestedCE = new ArrayList();

	protected List<BaseNode> nodes = new ArrayList<BaseNode>();

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

	public void addNestedConditionElement(Object ce) {
		if (ce instanceof List) {
			List l = (List) ce;
			Iterator itr = l.iterator();
			while (itr.hasNext()) {
				this.nestedCE.add(itr.next());
			}
		} else {
			this.nestedCE.add(ce);
		}
	}

	public List getNestedConditionalElement() {
		return this.nestedCE;
	}

	public List getNodes() {
		return new ArrayList();
	}

	/**
	 * the method doesn't apply and isn't implemented currently
	 */
	public void addNode(BaseNode node) {
		nodes.add(node);
	}

	public boolean hasBindings() {
		return false;
	}

	public BaseNode getFirstNode() {
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
		Iterator itr = nestedCE.iterator();
		while (itr.hasNext()) {
			if (itr.next() instanceof ObjectCondition) {
				has = true;
				break;
			}
		}
		return has;
	}


	public List<BoundConstraint> getAllBoundConstraints() {
		ArrayList<BoundConstraint> bindings = new ArrayList<BoundConstraint>();
		Iterator itr = nestedCE.iterator();
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

	public List getBoundConstraints() {
		return null;
	}

	public void clear() {
		nodes.clear();
	}

	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}

	public List<Constraint> getConstraints() {
 		return null;
	}


	public BaseNode getLastNode() {
		return ((ObjectCondition)nestedCE.get(0)).getLastNode();
	}
	
	protected String clipsName() {return "exists";}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
