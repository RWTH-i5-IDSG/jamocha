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
import java.util.List;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;


/**
 * @author Peter Lin
 *
 * AndCondition is specifically created to handle and conjunctions. AndConditions
 * are compiled to a BetaNode.
 */
public class AndCondition implements Condition {

    protected List nestedCE = new ArrayList();
    protected BaseJoin reteNode = null;
    
	/**
	 * 
	 */
	public AndCondition() {
		super();
	}

	public boolean compare(Condition cond) {
		return false;
	}

    public void addNestedConditionElement(Object ce) {
        this.nestedCE.add(ce);
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
	}

	public boolean hasBindings() {
		return false;
	}

	public BaseNode getLastNode() {
		return reteNode;
	}

	public List getAllBindings() {
		return null;
	}

    public List getBindings() {
        return null;
    }
    
    public void clear() {
    	reteNode = null;
    }
    
	public String toPPString() {
		return "";
	}
}
