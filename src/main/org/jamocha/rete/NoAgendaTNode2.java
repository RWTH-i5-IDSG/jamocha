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
 * NoAgendaTNode is different than TerminalNode2 in that it doesn't
 * get added to the agenda. Instead, it fires immediately.
 */
public class NoAgendaTNode2 extends NoAgendaTNode {

	/**
	 * @param id
	 */
	public NoAgendaTNode2(int id, Rule rl) {
		super(id,rl);
        this.theRule = rl;
	}

    /**
     * @param facts
     * @param engine
     */
    public void assertFacts(Index facts, Rete engine, WorkingMemory mem){
		long time = System.currentTimeMillis();
		if (this.theRule.getExpirationDate() > 0
				&& time > this.theRule.getEffectiveDate()
				&& time < this.theRule.getExpirationDate()) {
			LinkedActivation act = new LinkedActivation(this.theRule, facts);
			act.setTerminalNode(this);
			// fire the activation immediately
			engine.fireActivation(act);
		}
    }
    
}
