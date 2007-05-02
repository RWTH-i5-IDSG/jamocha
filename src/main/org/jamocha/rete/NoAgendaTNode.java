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

package org.jamocha.rete;

import java.util.Map;

import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * NoAgendaTNode is different than TerminalNode2 in that it doesn't
 * get added to the agenda. Instead, it fires immediately.
 */
public class NoAgendaTNode extends TerminalNode2 {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;
	
	/**
	 * @param id
	 */
	public NoAgendaTNode(int id, Rule rl) {
		super(id,rl);
        this.theRule = rl;
	}

	/**
	 * method does not apply for no agenda terminal node
	 */
	public void clear(WorkingMemory mem) {
		Map tmem = (Map)mem.getTerminalMemory(this);
        if (tmem != null) {
            tmem.clear();
        }
	}

    /**
     * @param facts
     * @param engine
     */
    public void assertFacts(Index facts, Rete engine, WorkingMemory mem){
        LinkedActivation act = LinkedActivation.acquire(this.theRule,facts);
        act.setTerminalNode(this);
        // fire the activation immediately
        engine.fireActivation(act);
    }
    
    /**
     * method does not apply, since the activation fires immediately,
     * there's nothing to remove from the agenda
     * @param idx
     * @param engine
     */
    public void retractFacts(Index idx, Rete engine, WorkingMemory mem){
    }
    
    /**
     * Return the Rule object associated with this terminal node
     * @return
     */
    public Rule getRule() {
        return this.theRule;
    }
    
    /**
     * method doesn't apply for no agenda terminal node
     * @param LinkedActivation
     */
    public void removeActivation(WorkingMemory mem, LinkedActivation activation) {
    }
    
    /**
     * return the name of the rule
     */
    public String toString(){
        return this.theRule.getName();
    }

    /**
     * return the name of the rule
     */
    public String toPPString(){
        return this.theRule.getName();
    }
}
