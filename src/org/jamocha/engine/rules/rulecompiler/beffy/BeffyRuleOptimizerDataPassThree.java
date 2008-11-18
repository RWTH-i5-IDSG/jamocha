/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

/**
 * 
 */
package org.jamocha.engine.rules.rulecompiler.beffy;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.rules.Condition;

/**
 * @author Christoph Terwelp
 *
 */
public class BeffyRuleOptimizerDataPassThree implements Cloneable {
	
	HashSet<String> boundVariables = new HashSet<String>();
	HashSet<String> unboundVariables = new HashSet<String>();
	Condition condition = null;
	
	public static BeffyRuleOptimizerDataPassThree combine(Set<BeffyRuleOptimizerDataPassThree> set) {
		BeffyRuleOptimizerDataPassThree comb = new BeffyRuleOptimizerDataPassThree();
		for (BeffyRuleOptimizerDataPassThree d : set) {
			comb.combine(d);
		}
		return comb;
	}

	public void combine(BeffyRuleOptimizerDataPassThree d) {
		for (String name : d.unboundVariables) {
			this.addUnbound(name);
		}
		for (String name : d.boundVariables) {
			this.addBound(name);
		}
	}
	
	public void add(String name, boolean unbound) {
		if (unbound)
			addUnbound(name);
		else
			addBound(name);
	}
	
	public void addBound(String name) {
		boundVariables.add(name);
		unboundVariables.remove(name);
	}
	
	public void addUnbound(String name) {
		if (!boundVariables.contains(name)) {
			unboundVariables.add(name);
		}
	}
	
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public Condition getCondition() {
		return this.condition;
	}
	
	public void markUnbound() {
		for (String name : boundVariables) {
			unboundVariables.add(name);
		}
		boundVariables.clear();
	}
	
	@SuppressWarnings("unchecked")
	public BeffyRuleOptimizerDataPassThree clone() {
		BeffyRuleOptimizerDataPassThree result = new BeffyRuleOptimizerDataPassThree();
		result.boundVariables = (HashSet<String>)this.boundVariables.clone();
		result.unboundVariables = (HashSet<String>)this.unboundVariables.clone();
		result.condition = this.condition;
		return result;
	}
	
	public boolean isBound() {
		return unboundVariables.isEmpty();
	}
}
