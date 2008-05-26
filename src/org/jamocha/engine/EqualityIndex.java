/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine;

import java.io.Serializable;

import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Peter Lin<p/>
 * 
 * EqualityIndex is used specifically for deffacts to check if 2 facts are
 * equal. By equal, we mean the values of the facts are equal.
 */
public class EqualityIndex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Fact fact = null;
	private int hashCode;

	/**
	 * 
	 */
	public EqualityIndex(Fact facts) {
		super();
		fact = facts;
		calculateHash();
	}

	/**
	 * This is a very simple implementation that gets the slot hash from the
	 * deffact.
	 */
	private void calculateHash() {
		hashCode = fact.hashCode();
	}

	/**
	 * The implementation is similar to the index class.
	 */
	@Override
	public boolean equals(Object val) {
		if (this == val)
			return true;
		if (val == null || !(val instanceof EqualityIndex))
			return false;
		EqualityIndex eval = (EqualityIndex) val;
		if (eval.fact.getTemplate() != fact.getTemplate())
			return false;
		return eval.fact.equals(fact);
	}

	/**
	 * Method simply returns the cached hashCode.
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

}
