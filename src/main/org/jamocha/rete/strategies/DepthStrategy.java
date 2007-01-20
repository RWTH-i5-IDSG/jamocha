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
		if (thelist.isAscendingOrder()) {
			addAscending(thelist, newActivation);
		} else {
			addDescending(thelist, newActivation);
		}
	}

	/**
	 * Current implementation will check which order the list is and call
	 * the appropriate method
	 */
	public Activation nextActivation(ActivationList thelist) {
		if (thelist.isAscendingOrder()) {
			return this.nextAscending(thelist);
		} else {
			return this.nextDescending(thelist);
		}
	}

	/**
	 * @param thelist
	 * @param newActivation
	 */
	protected void addDescending(ActivationList thelist,
			Activation newActivation) {
		// start from the bottom and start comparing the activations
		List l = thelist.getList();
		boolean added = false;
		for (int idx = 0; idx < l.size(); idx++) {
			Activation right = (Activation) l.get(idx);
			// if the new activation is equal or less than, we add the activation
			// at position + 1
			if (this.compare(newActivation, right) >= 0) {
				l.add(idx, newActivation);
				added = true;
				break;
			}
		}
		if (!added) {
			l.add(l.size(), newActivation);
		}
	}

	/**
	 * @param thelist
	 * @param newActivation
	 */
	protected void addAscending(ActivationList thelist, Activation newActivation) {
		List l = thelist.getList();
		int start = l.size() - 1;
		boolean added = false;
		for (int idx = start; idx > -1; idx--) {
			Activation right = (Activation) l.get(idx);
			// if the new activation is greater or less than,
			// we add the activation at position + 1
			if (this.compare(newActivation, right) >= 0) {
				l.add(idx + 1, newActivation);
				added = true;
				break;
			}
		}
		// the activation was not added, so we add it at index 0
		if (!added) {
			l.add(0, newActivation);
		}
	}

	/**
	 * Get the next next activation for lists that are ascending order. The
	 * basic idea is to start at the bottom of the list, and compare the
	 * bottom 2 activations. Which ever is greater is compared to the next
	 * activation, which would be the third from the bottom. Since this
	 * method would only be called in lazy mode, we have to iterate over
	 * the entire list to make sure we get the right activation.
	 * @param thelist
	 * @return
	 */
	protected Activation nextDescending(ActivationList thelist) {
		List l = thelist.getList();
		if (l.size() == 1) {
			return (Activation) l.remove(0);
		} else if (l.size() == 0) {
			return null;
		} else {
			// first we get the last item in the list
			Activation left = (Activation) l.get(0);
			for (int idx = 1; idx < l.size(); idx++) {
				Activation right = (Activation) l.get(idx);
				// if the left is less than the right, we set left to the right
				if (this.compare(left, right) < 0) {
					left = right;
				}
			}
			// we remove the Activation from the List
			l.remove(left);
			return left;
		}
	}

	/**
	 * Get the next activation for lists that are descending order.
	 * the basic goal is to return the activation with the highest
	 * salience and aggregate time.
	 * @param thelist
	 * @return
	 */
	protected Activation nextAscending(ActivationList thelist) {
		List l = thelist.getList();
		// if the size is greater than 1 we iterate over it
		// otherwise we just return the 
		if (l.size() == 1) {
			return (Activation) l.remove(0);
		} else if (l.size() == 0) {
			return null;
		} else {
			// the starting point is the second last item
			int start = l.size() - 2;
			// first we get the last item in the list
			Activation left = (Activation) l.get(l.size() - 1);
			for (int idx = start; idx > -1; idx--) {
				Activation right = (Activation) l.get(idx);
				// if the left is less than the right, we set left to the right
				if (this.compare(left, right) < 0) {
					left = right;
				}
			}
			// we remove the Activation from the List
			l.remove(left);
			return left;
		}
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
