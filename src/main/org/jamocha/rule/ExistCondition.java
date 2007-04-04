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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.ExistJoinFrst;

/**
 * @author Peter Lin
 *
 * ExistCondition for existential quantifier.
 */
public class ExistCondition implements Condition {

    protected List nestedCE = new ArrayList();
    protected List nodes = new ArrayList();
    protected boolean isFirstCE = false;
    
	/**
	 * 
	 */
	public ExistCondition() {
		super();
	}

	/**
	 * The rule compiler should call this and set the condition as
	 * the first CE in the rule.
	 * @param first
	 */
	public void setIsFirstCE(boolean first) {
		this.isFirstCE = first;
	}
	
	public boolean compare(Condition cond) {
		return false;
	}

    public void addNestedConditionElement(Object ce) {
    	if (ce instanceof List) {
    		List l = (List)ce;
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

	public BaseNode getLastNode() {
		BaseNode base = null;
		if (this.isFirstCE) {
			base = (BaseNode)this.nodes.get(this.nodes.size() - 1);
		} else {
			Condition c = (Condition)nestedCE.get(nestedCE.size() - 1);
			base = c.getLastNode();
		}
		return base;
	}

	/**
	 * this is specific to exist conditions
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
	
	/**
	 * if the first nested CE in the exist is an object condition, we
	 * return the first item in the ArrayList
	 * @return
	 */
	public ObjectCondition getObjectCondition() {
		ObjectCondition oc = null;
		oc = (ObjectCondition)nestedCE.get(0);
		return oc;
	}
	
	public List getAllBindings() {
		ArrayList bindings = new ArrayList();
		Iterator itr = nestedCE.iterator();
		while (itr.hasNext()) {
			Condition con = (Condition)itr.next();
			if (con instanceof ObjectCondition) {
				ObjectCondition oc = (ObjectCondition)con;
				Constraint[] constr = oc.getConstraints();
				for (int idx=0; idx < constr.length; idx++) {
		            Object c = constr[idx];
		            if (c instanceof BoundConstraint) {
		            	BoundConstraint bc = (BoundConstraint)c;
		            	if (!bc.firstDeclaration()) {
		            		bindings.add(c);
		            	}
		            } else if (c instanceof PredicateConstraint) {
		            	if (((PredicateConstraint)c).isPredicateJoin()) {
		            		bindings.add(c);
		            	}
		            }
					
				}
			} else if (con instanceof TestCondition) {
				TestCondition tc = (TestCondition)con;
				
			}
		}
		return bindings;
	}

    public List getBindings() {
        return null;
    }
    
    public void clear() {
    	nodes.clear();
    }
}
