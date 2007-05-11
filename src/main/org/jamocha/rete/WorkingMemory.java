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
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * Interface defining working memory
 */
public interface WorkingMemory extends Serializable {

	void assertObject(Fact fact) throws AssertException;

	void retractObject(Fact fact) throws RetractException;

	/**
	 * The key for looking up the memory should be the node. Each node
	 * should pass itself as the key for the lookup.
	 * @param key
	 * @return
	 */
	Object getAlphaMemory(Object key);

	/**
	 * In the case of AlphaMemory, during the compilation process,
	 * we may want to remove an alpha memory if one already exists.
	 * This depends on how rule compilation works.
	 * @param key
	 */
	void removeAlphaMemory(Object key);

	/**
	 * The key for the lookup should be the node. Each BetaNode has
	 * a left and right memory, so it's necessary to have a lookup
	 * method for each memory.
	 * @param key
	 * @return
	 */
	//Object getBetaLeftMemory(Object key);

	/**
	 * The key for the lookup should be the node. Each BetaNode has
	 * a left and right memory, so it's necessary to have a lookup
	 * method for each memory.
	 * @param key
	 * @return
	 */
	//Object getBetaRightMemory(Object key);

	/**
	 * The for the lookup is the terminalNode. Depending on the terminal
	 * node used, it may not have a memory.
	 * @param key
	 * @return
	 */
	Object getTerminalMemory(Object key);

	/**
	 * Return the RuleCompiler for this working memory
	 * @return
	 */
	RuleCompiler getRuleCompiler();

	/**
	 * Printout the working memory. If the method is called with
	 * true, the workingmemory should print out the number of 
	 * matches for each node. It isn't necessary to print the full
	 * detail of each fact in each node. For now, just the number
	 * of matches for each Node is sufficient.
	 * @param detailed
	 */
	//void printWorkingMemory(boolean detailed, boolean inputNodes);

	/**
	 * Printout the working memory with the given filter. if no filer
	 * is passed, it should call printWorkingMemory(true,false);
	 * @param filter
	 */
	//void printWorkingMemory(Map filter);

	/**
	 * Printout the facts on the right side of BetaNodes.
	 */
	void printWorkingMemoryBetaRight();

	/**
	 * Clears everything in the working memory
	 *
	 */
	void clear();
}
