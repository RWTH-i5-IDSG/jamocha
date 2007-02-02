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

import java.util.Map;

import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * TerminalNode3 is for rules that have an effective and expiration date.
 * When rules do not have it set, we don't bother checking. If it does, we
 * make sure a new activation is added only if the rule is within the
 * the two dates.
 */
public class TerminalNode3 extends TerminalNode2 {

	/**
	 * @param id
	 */
	public TerminalNode3(int id, Rule rl) {
		super(id, rl);
		this.theRule = rl;
	}

	/**
	 * The implementation checks to see if the rule is active before it tries to
	 * assert the fact. It checks in the following order.
	 * 1. is the expiration date greater than zero
	 * 2. is the current time > the effective date
	 * 3. is the current time < the expiration date
	 * @param facts
	 * @param engine
	 */
	public void assertFacts(Index inx, Rete engine, WorkingMemory mem) {
		long time = System.currentTimeMillis();
		if (this.theRule.getExpirationDate() > 0
				&& time > this.theRule.getEffectiveDate()
				&& time < this.theRule.getExpirationDate()) {
			LinkedActivation act = new LinkedActivation(this.theRule, inx);
			act.setTerminalNode(this);
			Map tmem = (Map) mem.getTerminalMemory(this);
			tmem.put(act.getIndex(), act);
			// add the activation to the current module's activation list.
			engine.getAgenda().addActivation(act);
		}
	}

	/**
	 * The implementation checks to see if the rule is active before it tries to
	 * retract the fact. It checks in the following order.
	 * 1. is the expiration date greater than zero
	 * 2. is the current time > the effective date
	 * 3. is the current time < the expiration date
	 * @param idx
	 * @param engine
	 */
	public void retractFacts(Index inx, Rete engine, WorkingMemory mem) {
		long time = System.currentTimeMillis();
		if (this.theRule.getExpirationDate() > 0
				&& time > this.theRule.getEffectiveDate()
				&& time < this.theRule.getExpirationDate()) {
			Map tmem = (Map) mem.getTerminalMemory(this);
			LinkedActivation act = new LinkedActivation(this.theRule, inx);
			if (tmem.containsKey(act.getIndex())) {
				act = (LinkedActivation) tmem.remove(act.getIndex());
				engine.getAgenda().removeActivation(act);
			}
			act.clear();
		}
	}
}
