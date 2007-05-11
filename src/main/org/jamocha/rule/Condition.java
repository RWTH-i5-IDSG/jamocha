/*
 * Copyright 2002-2006 Peter Lin
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

import java.io.Serializable;
import java.util.List;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.RuleCompiler;

/**
 * @author Peter Lin
 * 
 * Conditions are patterns. It may be a simple fact pattern, test function, or
 * an object pattern.
 */
public interface Condition extends Serializable, Complexity, SelfCompilerInterface, Comparable{

	/**
	 * Method is used to compare the pattern to another pattern and determine if
	 * they are equal.
	 * 
	 * @param cond
	 * @return
	 */
	boolean compare(Complexity cond);

	/**
	 * Get the nodes associated with the condition. In the case of
	 * TestConditions, it should only be 1 node.
	 * 
	 * @return
	 */
	List getNodes();

	/**
	 * When the rule is compiled, we add the node to the condition, so that we
	 * can print out the matches for a given rule.
	 * 
	 * @param node
	 */
	void addNode(BaseNode node);

	/**
	 * It's convienant to have a method which rule compilers can call to find
	 * out if a condition has bindings.
	 * 
	 * @return
	 */
	boolean hasBindings();

	/**
	 * Get the last node in the Condition
	 * 
	 * @return
	 */
	BaseNode getLastNode();

	/**
	 * convienance method for getting the bindings
	 * 
	 * @return
	 */
	List getAllBindings();

	/**
	 * method to get just the bindings excluding predicate constraints
	 * 
	 * @return
	 */
	List getBindings();

	/**
	 * clear the condition
	 */
	void clear();
	
}
