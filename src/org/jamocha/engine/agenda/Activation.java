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

package org.jamocha.engine.agenda;

import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.rules.Action;
import org.jamocha.rules.Rule;

/**
 * @author Josef Alexander Hahn an activation is a (rule,facttuple)-pair. it
 *         signals, that the containing agenda must fire the rule's action part
 *         with the values from the facttuple.
 */
public class Activation {

	protected Rule rule;
	protected boolean valid = true;
	protected FactTuple tuple;
	protected long aggregatedTime = 0;
	protected TerminalNode tnode;
	
	@Override
	public int hashCode() {
		return rule.hashCode() + tuple.hashCode() + tnode.getId();
	}

	@Override
	/**
	 * two activations are equal, if the rules are equal and the tuples are
	 * equal
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Activation) {
			Activation other = (Activation) obj;
			return tuple.equals(other.tuple) && rule.equals(other.rule) && tnode==other.tnode;
		} else
			return false;
	}

	public Activation(Rule rule, FactTuple tuple, TerminalNode tnode) {
		this.rule = rule;
		this.tnode = tnode;
		setTuple(tuple);
	}

	/**
	 * gets the rule
	 * 
	 * @return
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * gets the tuple
	 * 
	 * @return
	 */
	public FactTuple getTuple() {
		return tuple;
	}

	protected void setTuple(FactTuple tuple) {
		this.tuple = tuple;
		for (Fact fact : tuple)
			aggregatedTime = Math.max(aggregatedTime, fact
					.getCreationTimeStamp());
	}

	/**
	 * fires that activation
	 * 
	 * @param engine
	 * @throws org.jamocha.rete.exception.ExecuteException
	 */
	public void fire(Engine engine) throws ExecuteException {
		engine.pushScope(rule);
		for (Action action : rule.getActions())
			action.executeAction(tuple,tnode);
		engine.popScope();
	}

	@Override
	public String toString() {
		return "[" + rule.toString() + " " + tuple.toString() + "]";
	}
}
