/*
 * Copyright 2002-2007 Peter Lin
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

import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rule.Rule;

/**
 * @author pete
 *
 */
public class QueueActivation implements Activation, java.io.Serializable {

    private Rule theRule;

    private long timetag;

    private Index index;

    private long aggreTime = 0;
    
    private int queueIndex = 0;
    
    /**
     * 
     */
    public QueueActivation(Rule rule, Index inx) {
        this.theRule = rule;
        this.index = inx;
        this.timetag = System.nanoTime();
        calculateTime(inx.getFacts());
    }

    protected void calculateTime(Fact[] facts) {
        for (int idx=0; idx < facts.length; idx++) {
            this.aggreTime += facts[idx].timeStamp();
        }
    }

    public void setQueueIndex(int index) {
        this.queueIndex = index;
    }
    
    public void clear() {
        
    }

    public boolean compare(Activation act) {
        return false;
    }

    public void executeActivation(Rete engine) throws ExecuteException {
        // TODO Auto-generated method stub

    }

    public long getAggregateTime() {
        return this.aggreTime;
    }

    public Fact[] getFacts() {
        return this.index.getFacts();
    }

    public Index getIndex() {
        return this.index;
    }

    public Rule getRule() {
        return this.theRule;
    }

    public long getTimeStamp() {
        return this.timetag;
    }

    /* (non-Javadoc)
     * @see org.jamocha.rete.Activation#toPPString()
     */
    public String toPPString() {
        return null;
    }

}
