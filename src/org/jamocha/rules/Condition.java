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

package org.jamocha.rules;

import java.io.Serializable;
import java.util.List;

import org.jamocha.formatter.Formattable;
import org.jamocha.engine.Complexity;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.Compileable;

/**
 * @author Peter Lin
 * 
 * Conditions are patterns. It may be a simple fact pattern, test function, or
 * an object pattern.
 */
public interface Condition extends Compileable, Complexity, Formattable {

	/**
	 * returns all constraints in this condition (including sub-conditions)
	 * @return
	 */
	public List<Constraint> getConstraints();
	
	/**
	 * returns all constraints in this condition without taking
	 * sub-conditions into account
	 * @return
	 */
	public List<Constraint> getFlatConstraints();

	/**
	 * returns a positive value, if this is more complex, a negative
	 * value, if the other is more complex, or 0, if complexity is
	 * equal
	 */
	public int compareComplexity(Complexity other);

	public ConditionWithNested getParentCondition();
	
	/**
	 * Argh, i really really hate it to introduce this method inside
	 * the interface. But there is no better way. DONT CALL IT!!!
	 * it is called by some internal methods only...
	 */
	public void setParentCondition(ConditionWithNested c);
	
	/**
	 * Visitor pattern support for the BeffyRuleOptimizer Class.
	 */
	public <T, S> S acceptVisitor(LHSVisitor <T, S> visitor, T data);
	
	public Condition clone();
	
	public boolean testEquals(Condition o);
	
	public String dump();
	
	public String dump(String prefix);
	
}
