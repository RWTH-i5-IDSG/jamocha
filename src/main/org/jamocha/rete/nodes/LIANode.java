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
package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * LIANode stands for Left Input Adapter Node. Left input adapter node is
 * responsible for creating a List to pass to the BetaNode. This is important
 * because the same fact may be re-asserted.
 */
public class LIANode extends AbstractAlpha {

	public LIANode(int id) {
		super(id);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * the Left Input Adapter Node returns zero length string
	 */
	public String toString() {
		return "LIANode";
	}

	/**
	 * the Left input Adapter Node returns zero length string
	 */
	public String toPPString() {
		return "LIANode-" + this.nodeID + ">";
	}

	@Override
	protected void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		// add to own buffer list:
		facts.add((Fact) fact);
		// build tuple and propagate:
		FactTuple tuple = new FactTuple((Fact) fact);
		propogateAssert(tuple, engine);
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		if (facts.remove(fact)) {
			FactTuple tuple = new FactTuple((Fact) fact);
			propogateRetract(tuple, engine);
		}
	}

	public static boolean isRightNode() {
		return false;
	}

}
