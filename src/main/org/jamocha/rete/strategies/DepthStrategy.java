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
 * Depth strategy is very similar to CLIPS depth strategy. The design
 * of Strategies in Sumatra is inspired by CLIPS, but the implementation
 * is quite different. In CLIPS, there's isn't really an interface and
 * there isn't the concept of lazy comparison. Since Sumatra uses these
 * concepts, the design and implementation is quite different.
 * Depth strategy is often referred to as LIFO (Last In First Out). In
 * rule engines like CLIPS that propogate in assertion order, the facts
 * that were asserted earlier will propogate down the network before
 * newer facts. In Sumatra, HashMaps are used, which means fact
 * propogation is not gauranteed to follow assertion order. It's bad
 * idea to rely on assertion order for proper rule functioning. This
 * is especially true of real-time systems where facts change rapidly
 * in non-deterministic ways. Rules should be written such that assertion
 * order is not critical. In other words, regardless of the sequence the
 * facts are asserted, the correct result is produced.
 */
public class DepthStrategy implements Strategy, Serializable {

	/**
	 * 
	 */
	public DepthStrategy() {
		super();
	}

	/**
	 * Current implementation will check which order the list is and call
	 * the appropriate method
	 */
	public void addActivation(ActivationList thelist, Activation newActivation) {
        thelist.addActivation(newActivation);
	}

	/**
	 * Current implementation will check which order the list is and call
	 * the appropriate method
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
		if (right != null) {
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
				if (left.getRule().getSalience() > right.getRule()
						.getSalience()) {
					return 1;
				} else {
					return -1;
				}
			}
		} else {
			return 1;
		}
	}
}
