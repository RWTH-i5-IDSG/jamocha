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

import org.jamocha.formatter.Formattable;
import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Peter Lin
 * 
 * Conditions are patterns. It may be a simple fact pattern, test function, or
 * an object pattern.
 */
public interface Condition extends Serializable, Complexity, Compileable,
		Comparable, Cloneable, Formattable {

	/**
	 * Method is used to compare the pattern to another pattern and determine if
	 * they are equal.
	 * 
	 * @param cond
	 * @return
	 */
	boolean compare(Complexity cond);

	/**
	 * clear the condition
	 */
	void clear();

	BaseNode getLastNode();

	public Object clone() throws CloneNotSupportedException;
	
	public List<Constraint> getConstraints();

}
