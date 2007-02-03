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
package org.jamocha.rete.strategies;

import java.io.Serializable;
import java.util.List;

import org.jamocha.rete.Activation;
import org.jamocha.rete.ActivationList;
import org.jamocha.rete.Strategy;


/**
 * @author Peter Lin
 *
 * Breadth strategy is very similar to CLIPS breadth strategy. The design
 * of Strategies in Sumatra is inspired by CLIPS, but the implementation
 * is quite different. In CLIPS, there's isn't really an interface and
 * there isn't the concept of lazy comparison. Since Sumatra uses these
 * concepts, the design and implementation is quite different.
 * Breadth strategy is often referred to as FIFO (First In First Out).
 * What this means in practice is that matches with older facts will be
 * executed before matches with newer facts. By executed, we mean the
 * actions of the rule will be executed.
 * CLIPS beginner guide provides a clear explanation of breadth:
 * 5.3.2 Breadth Strategy
 * Newly activated rules are placed below all rules of the same salience.
 */
public class BreadthStrategy implements Strategy, Serializable {

	/**
	 * 
	 */
	public BreadthStrategy() {
		super();
	}

	public void addActivation(ActivationList thelist, Activation newActivation) {
        thelist.addActivation(newActivation);
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Strategy#nextActivation(woolfel.engine.rete.ActivationList)
	 */
	public Activation nextActivation(ActivationList thelist) {
        return thelist.nextActivation();
	}

    /**
     * The method first compares the salience. If the salience is equal,
     * we then compare the aggregate time.
     * @param left
     * @param right
     * @return
     */
    public int compare(Activation left, Activation right) {
        if (left.getRule().getSalience() == right.getRule().getSalience()) {
            // Since Sumatra does not propogate based on natural order, we
            // don't use the Activation timestamp. Instead, we use the
            // aggregate time.
            if (left.getAggregateTime() == right.getAggregateTime()) {
                return 0;
            } else {
                if (left.getAggregateTime() > right.getAggregateTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        } else {
            if (left.getRule().getSalience() > right.getRule().getSalience()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
