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

/**
 * @author Peter Lin
 * 
 * LinkedActivationWrapper is a container for LinkedActivation. It provdes the
 * logic for modifying a LinkedList created from LinkedActivation. Null values
 * are not permitted, and are silently ignored. Generally speaking, it doesn't
 * make sense to add a null activation to the agenda.
 */
public class LinkedActivationList extends AbstractActivationList {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private int count = 0;

	private LinkedActivation first = null;

	private LinkedActivation last = null;

	public LinkedActivationList(Strategy strat) {
		this.theStrategy = strat;
	}

	public Activation nextActivation() {
		if (lazy) {
			if (this.count == 0) {
				return null;
			} else {
				LinkedActivation left = this.last;
				LinkedActivation right = this.last.getPrevious();
				while (right != null) {
					if (this.theStrategy.compare(left, right) < 1) {
						left = right;
					}
					right = right.getPrevious();
				}
				if (left == this.first) {
					this.first = left.getNext();
				} else if (left == this.last) {
					this.last = left.getPrevious();
				}
				left.remove();
				this.count--;
				return left;
			}
		} else {
			if (this.count > 1) {
				LinkedActivation r = this.last;
				this.last = r.getPrevious();
				this.count--;
				r.remove();
				return r;
			} else if (this.count == 1) {
				LinkedActivation r = this.last;
				this.last = null;
				this.first = null;
				this.count--;
				return r;
			} else {
				return null;
			}
		}
	}

	public void addActivation(Activation act) {
		if (act instanceof LinkedActivation) {
			LinkedActivation newact = (LinkedActivation) act;
			if (lazy) {
				if (count == 0) {
					this.first = newact;
					this.last = newact;
				} else {
					this.last.setNext(newact);
					this.last = newact;
				}
				this.count++;
			} else {
				if (this.count > 0) {
					this.quickSort(newact);
				} else if (count == 0) {
					this.first = newact;
					this.last = newact;
				}
				this.count++;
			}
		}
	}

	/**
	 * the sort method uses binary search to find the correct insertion point
	 * for the new activation. It's much faster than the brute force method.
	 * 
	 * @param newact
	 */
	public void quickSort(LinkedActivation newact) {
		if (this.theStrategy.compare(newact, this.last) > 0) {
			// the new activation has a higher salience than the last, which
			// means
			// it should become the bottom activation
			this.last.setNext(newact);
			this.last = newact;
		} else if (this.theStrategy.compare(newact, this.first) < 0) {
			// the new activation has a salience lower than the first, which
			// means
			// it should become the top activation
			newact.setNext(this.first);
			this.first = newact;
		} else {
			// this means the new activation goes in the middle some where
			int counter = this.count / 2;
			LinkedActivation cur = goUp(counter, this.last);
			boolean added = false;
			while (!added) {
				if (counter <= 1) {
					// add the activation
					if (this.theStrategy.compare(newact, cur) < 0) {
						// if the new activation is lower sailence than the
						// current,
						// we add it before the current (aka above)
						newact.setPrevious(cur.getPrevious());
						newact.setNext(cur);
					} else {
						// the new activation is higher salience than the
						// current
						// therefore we add it after (aka below)
						newact.setNext(cur.getNext());
						newact.setPrevious(cur);
					}
					added = true;
				} else if (this.theStrategy.compare(newact, cur) >= 0) {
					// the new activation is of greater salience down half again
					counter = counter / 2;
					cur = goDown(counter, cur);
				} else {
					// the new activation is of lower salience, up half again
					counter = counter / 2;
					cur = goUp(counter, cur);
				}
			}
		}
	}

	/**
	 * method will loop for the given count and return the item before it. for
	 * example: 1 2 3 4 5 6 If I pass a count of 2 and item #6. it will return
	 * #4.
	 * 
	 * @param count
	 * @param start
	 * @return
	 */
	protected LinkedActivation goUp(int count, LinkedActivation start) {
		LinkedActivation rt = start;
		for (int idx = 0; idx < count; idx++) {
			rt = rt.getPrevious();
		}
		return rt;
	}

	/**
	 * method will loop for the given count and return the item after it. for
	 * example: 1 2 3 4 5 6 If I pass a count of 2 and item #1. it will return
	 * #3.
	 * 
	 * @param count
	 * @param start
	 * @return
	 */
	protected LinkedActivation goDown(int count, LinkedActivation start) {
		LinkedActivation rt = start;
		for (int idx = 0; idx < count; idx++) {
			rt = rt.getNext();
		}
		return rt;
	}

	/**
	 * removeActivation will check to see if the activation is the first or last
	 * before removing it.
	 */
	public Activation removeActivation(Activation act) {
		if (act instanceof LinkedActivation) {
			if (this.contains(act)) {
				LinkedActivation lact = (LinkedActivation) act;
				if (first == lact) {
					this.first = lact.getNext();
				}
				if (this.last == lact) {
					this.last = lact.getPrevious();
				}
				this.count--;
				lact.remove();
			}
			return act;
		}
		return null;
	}

	public boolean isAscendingOrder() {
		return true;
	}

	public int size() {
		return this.count;
	}

	public boolean isEmpty() {
		return this.count == 0;
	}

	/**
	 * the current implementation iterates over the LinkedActivations from the
	 * start until it finds a match. If it doesn't find a match, the method
	 * returns false.
	 */
	public boolean contains(Object o) {
		boolean contain = false;
		LinkedActivation act = first;
		while (act != null) {
			if (o == act) {
				contain = true;
				break;
			} else {
				act = act.getNext();
			}
		}
		return contain;
	}

	/**
	 * Iterate over the LinkedList and null the references to previous and next
	 * in the LinkedActivation
	 */
	public void clear() {
		while (this.first != null) {
			LinkedActivation la = this.first;
			this.first = la.getNext();
			la.remove();
		}
		this.last = null;
		this.count = 0;
	}

	public Object set(int index, Object activation) {
		if (index < this.count && activation != null) {
			LinkedActivation act = this.first;
			for (int idx = 0; idx <= count; idx++) {
				act = act.getNext();
			}
			// now we are at the index point
			LinkedActivation pre = act.getPrevious();
			LinkedActivation nxt = act.getNext();
			pre.setNext((LinkedActivation) activation);
			nxt.setPrevious((LinkedActivation) activation);
			act.remove();
		}
		return activation;
	}

	/**
	 * Current implemenation will return the index of the activation, if it is
	 * in the LinkedList. If activation isn't in the list, the method returns
	 * -1.
	 */
	public int indexOf(Object activation) {
		int index = -1;
		LinkedActivation la = this.first;
		LinkedActivation match = null;
		while (la != null) {
			index++;
			if (la == activation) {
				match = la;
				break;
			} else {
				la = la.getNext();
			}
		}
		if (match != null) {
			return index;
		} else {
			return -1;
		}
	}

	/**
	 * method will clone the list and make a copy of the activations
	 */
	public ActivationList clone() {
		final LinkedActivationList la = new LinkedActivationList(this.theStrategy);
		la.count = this.count;
		la.first = this.first.clone();
		la.lazy = this.lazy;
		LinkedActivation current = this.first;
		LinkedActivation newcurr = la.first;
		while (current != null) {
			newcurr.setNext(current.getNext().clone());
			current = current.getNext();
			newcurr = newcurr.getNext();
		}
		return la;
	}
}
