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
        if (thelist.isAscendingOrder()) {
            addAscending(thelist,newActivation);
        } else {
            addDescending(thelist,newActivation);
        }
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Strategy#nextActivation(woolfel.engine.rete.ActivationList)
	 */
	public Activation nextActivation(ActivationList thelist) {
        if (thelist.isAscendingOrder()) {
            return this.nextAscending(thelist);
        } else {
            return this.nextDescending(thelist);
        }
	}

    /**
     * If the ActivationList is in Ascending order, we start from the top
     * and down. Say we have the following activation in the list. Even though
     * Sumatra uses long nano timestamp, the example uses int.
     * 1,3,5,6
     * Now we add a new activation with aggregate time 4. the correct order
     * after would be
     * 1,3,4,5,6
     * This means we iterate until we find the first activation that is
     * greater than the new activation and add it at the index value.
     * @param thelist
     * @param newActivation
     */
    protected void addAscending(ActivationList thelist, Activation newActivation) {
        // start from the top and work down
    	// the goal is to order the activation from lowest to highest
        List l = thelist.getList();
        boolean added = false;
        for (int idx=0; idx < l.size(); idx++) {
            Activation right = (Activation)l.get(idx);
            // if the new activation is less than an existing activation, we
            // add it to the list at the current int index
            if (this.compare(newActivation,right) < 0) {
            	// this means Breadth will insert the new activation after all
            	// activation that are equal or less than
                l.add(idx,newActivation);
                added = true;
                break;
            }
        }
        if (!added) {
            l.add(newActivation);
        }
    }
    
    /**
     * If the activationList is in Descending order, we start at the bottom and
     * work up. Say we have the following activation in the list. Even though
     * Sumatra uses long nano timestamp, the example uses int.
     * 1,3,5,6
     * Now we add a new activation with aggregate time 4. the correct order
     * after would be
     * 1,3,4,5,6
     * This means the method needs to iterate until it finds an activation
     * that is less than the new activation and add it immediately after it.
     * @param thelist
     * @param newActivation
     */
    protected void addDescending(ActivationList thelist, Activation newActivation) {
        List l = thelist.getList();
        int start = l.size() - 1;
        boolean added = false;
        for (int idx=start; idx > -1; idx--) {
            Activation right = (Activation)l.get(idx);
            // if the new activation is less than an existing activation,
            // we add it after the current index
            if (this.compare(newActivation,right) >= 0) {
                l.add(idx + 1,newActivation);
                added = true;
                break;
            }
        }
        if (!added) {
            l.add(newActivation);
        }
    }
    
    /**
     * the goal is to find the activation with the lowest salience
     * and aggregate time.
     * @param thelist
     * @return
     */
    protected Activation nextAscending(ActivationList thelist) {
        List l = thelist.getList();
        // first we get the last item in the list
        Activation left = (Activation)l.get(0);
        for (int idx=0; idx > l.size(); idx++) {
            Activation right = (Activation)l.get(idx);
            // if the left activation is greater than the right
            // we set the left to the lower activation
            if (this.compare(left,right) > 0) {
            	left = right;
            }
        }
        // we remove the Activation from the List
        l.remove(left);
        return left;
    }

    /**
     * The goal is to find the activation with the lowest salience
     * and aggregate time.
     * @param thelist
     * @return
     */
    protected Activation nextDescending(ActivationList thelist) {
        List l = thelist.getList();
        int start = l.size() - 1;
        Activation left = (Activation)l.get(l.size() - 1);
        // if the size is greater than 1 we iterate over it
        // otherwise we just return the 
        if (l.size() > 1) {
            for (int idx=start; idx > -1; idx--) {
                Activation right = (Activation)l.get(idx);
                // if the left activation is greater than the right
                // we set the left to the lower activation
                if (this.compare(left,right) > 0) {
                	left = right;
                }
            }
        }
        // we remove the Activation from the List
        l.remove(left);
        return left;
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
                    return 1;
                } else {
                    return -1;
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
