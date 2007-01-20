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
package org.jamocha.rete;

import java.io.Serializable;

/**
 * @author Peter Lin
 *
 * Interface for alpha memory. Alpha memories are used to remember
 * which facts entered and match for alpha nodes.
 */
public interface AlphaMemory extends Serializable {
	/**
	 * Add a partial match to the memory
	 * @param fact
	 */
	void addPartialMatch(Fact fact);
	/**
	 * clear the alpha memory for the node
	 */
	void clear();
	/**
	 * if the fact matched for the node, the method returns true.
	 * On assert and retract, the node should check if the fact
	 * already matched.
	 * @param fact
	 * @return
	 */
	boolean isPartialMatch(Fact fact);
	/**
	 * Remove a partial match from the memory
	 * @param fact
	 */
	void removePartialMatch(Fact fact);
	/**
	 * size returns the number of matches
	 * @return
	 */
	int size();
	/**
	 * Return an iterator to iterate over the matches.
	 * @return
	 */
	java.util.Iterator iterator();
}
