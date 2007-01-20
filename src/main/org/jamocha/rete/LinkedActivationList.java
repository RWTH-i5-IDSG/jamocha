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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Peter Lin
 *
 * LinkedActivationWrapper is a container for LinkedActivation. It provdes
 * the logic for modifying a LinkedList created from LinkedActivation.
 * Null values are not permitted, and are silently ignored. Generally speaking,
 * it doesn't make sense to add a null activation to the agenda.
 */
public class LinkedActivationList extends AbstractActivationList implements
		List, Iterator {

	private int count = 0;

	private LinkedActivation first = null;

	private LinkedActivation last = null;

	private LinkedActivation current = null;

	private LinkedActivation itrCurrent = null;
	
	private int counter = 0;
    
	public LinkedActivationList(Strategy strat) {
		this.theStrategy = strat;
	}

	public Activation nextActivation() {
		if (lazy) {
			return this.theStrategy.nextActivation(this);
		} else {
			if (count > 0 && this.last != null) {
				return (Activation)this.remove(this.count - 1);
			} else {
				return null;
			}
		}
	}

	public void addActivation(Activation act) {
		if (act instanceof LinkedActivation) {
			if (lazy) {
				LinkedActivation newLast = (LinkedActivation) act;
				if (count == 0) {
					this.first = newLast;
					this.last = newLast;
				} else {
					this.last.setNext(newLast);
					this.last = newLast;
				}
				this.count++;
			} else {
				this.theStrategy.addActivation(this, act);
			}
		}
	}

	/**
	 * removeActivation will check to see if the activation is
	 * the first or last before removing it.
	 */
	public Activation removeActivation(Activation act) {
		if (act instanceof LinkedActivation) {
			LinkedActivation lact = (LinkedActivation)act;
			if (first == lact) {
				this.first = lact.getNext();
				this.current = this.first;
			} 
			if (this.last == lact) {
				this.last = lact.getPrevious();
				this.current = this.last;
			}
			lact.remove();
			this.count--;
		}
		return act;
	}

	public List getList() {
		return this;
	}

	public boolean isAscendingOrder() {
		return true;
	}

	public int size() {
		return this.count;
	}

	public boolean isEmpty() {
		return this.count > 0;
	}

	/**
	 * the current implementation iterates over the LinkedActivations
	 * from the start until it finds a match. If it doesn't find a
	 * match, the method returns false.
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
	 * every time the iterator is called, it starts at the beginning
	 */
	public Iterator iterator() {
		itrCurrent = this.first;
		counter = 0;
		return this;
	}

	/**
	 * Not implemented
	 */
	public Object[] toArray() {
		return null;
	}

	/**
	 * not implemented
	 * @param arg0
	 * @return
	 */
	public Object[] toArray(Object[] arg0) {
		return null;
	}

	public boolean add(Object activation) {
		if (activation != null) {
			this.addActivation((Activation) activation);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * the method checks to see if the activation is not null and an
	 * instance of LinkedActivation. If it is, it passes it to
	 * removeActivation method.
	 */
	public boolean remove(Object activation) {
		if (activation != null && activation instanceof LinkedActivation) {
			LinkedActivation lnkact = (LinkedActivation)activation;
			return removeActivation(lnkact) != null;
		} else {
			return false;
		}
	}

	/**
	 * Not implemented
	 * @param arg0
	 * @return
	 */
	public boolean containsAll(Collection arg0) {
		return false;
	}

	/**
	 * Not implemented at the moment, since merging lists of LinkedActivation
	 * is not needed currently.
	 * @param arg0
	 * @return
	 */
	public boolean addAll(Collection arg0) {
		return false;
	}

	/**
	 * Not implemented at the moment, since merging lists of LinkedActivation
	 * is not needed currently.
	 * @param arg0
	 * @return
	 */
	public boolean addAll(int arg0, Collection arg1) {
		return false;
	}

	/**
	 * Not implemented at the moment
	 * @param arg0
	 * @return
	 */
	public boolean removeAll(Collection arg0) {
		return false;
	}

	/**
	 * Not implemented at the moment
	 * @param arg0
	 * @return
	 */
	public boolean retainAll(Collection arg0) {
		return false;
	}

	/**
	 * Iterate over the LinkedList and null the references to previous
	 * and next in the LinkedActivation
	 */
	public void clear() {
		while (this.first != null) {
			LinkedActivation la = this.first;
			this.first = la.getNext();
			la.remove();
		}
		this.last = null;
		this.current = null;
		this.count = 0;
		this.counter = 0;
	}

	public Object get(int index) {
		if (this.count == 1) {
			return this.first;
		} else if (index < this.count) {
			if (index == 0) {
				this.current = this.first;
				this.counter = 0;
				return this.first;
			} else if (index == (count -1)) {
				this.current = this.last;
				this.counter = count -1;
				return this.current;
			} else if (this.current != null && (index == counter+1)) {
				this.current = this.current.getNext();
				this.counter++;
				return this.current;
			} else if (this.current != null && (index == counter-1)) {
				this.current = this.current.getPrevious();
				this.counter--;
				return this.current;
			} else {
				LinkedActivation la = this.last;
				int start = count -1;
				for (int idx = start; idx >= index; idx--) {
					la = la.getPrevious();
					this.counter = idx;
					this.current = la;
				}
				return la;
			}
		} else {
			return null;
		}
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
			pre.setNext( (LinkedActivation)activation );
			nxt.setPrevious( (LinkedActivation)activation );
			act.remove();
		}
		return activation;
	}

	/**
	 * if the index value is greater than the count, it will add the
	 * activation to the end
	 * @param index
	 * @param activation
	 */
	public void add(int index, Object activation) {
		if (activation != null) {
			if (this.count == 0) {
				first = (LinkedActivation)activation;
				last = (LinkedActivation)activation;
				this.count++;
			} else if (index == 0) {
				// this means add it to the beginngin
				first.setPrevious( (LinkedActivation)activation );
				first = (LinkedActivation)activation;
				this.counter = 0;
				this.count++;
			} else if (index == count) {
				last.setNext( (LinkedActivation)activation );
				last = (LinkedActivation)activation;
				this.counter = index;
				this.count++;
			} else if (index == counter) {
				LinkedActivation prv = this.current.getPrevious();
				LinkedActivation nxt = this.current;
				((LinkedActivation)activation).setPrevious(prv);
				((LinkedActivation)activation).setNext(nxt);
				this.counter = index;
				this.count++;
			} else if (index > counter) {
				this.current = this.current.getNext();
				this.counter = counter+1;
				add(index,activation);
			} else if (index < counter) {
				this.current = this.current.getPrevious();
				this.counter = this.counter -1;
				add(index,activation);
			}
			this.current = (LinkedActivation)activation;
		}
	}

	/**
	 * Method will iterate to the index value and remove the LinkedActivation
	 * using LinkedActivation.remove(). It will also decrement the count by
	 * 1.
	 */
	public Object remove(int index) {
		LinkedActivation act = null;
		if (index == (count -1)) {
			act = this.last;
			this.last = act.getPrevious();
			this.current = this.last;
			this.counter = count -2;
			act.remove();
			this.count--;
		} else if (index == 0) {
			act = this.first;
			this.first = act.getNext();
			this.current = this.first;
			act.remove();
			this.counter = 0;
			this.count--;
		} else if (index < count) {
			LinkedActivation curr = this.last;
			int start = this.count -1;
			// we start iterating at one
			for (int idx=start; idx > 0; idx--) {
				if (idx == index) {
					act = curr;
					if (curr == this.last) {
						last = curr.getPrevious();
					}
					curr.remove();
					break;
				} else {
					curr = curr.getPrevious();
				}
			}
			this.count--;
		} else if (index >= count) {
			return null;
		}
		return act;
	}

	/**
	 * Current implemenation will return the index of the activation, if
	 * it is in the LinkedList. If activation isn't in the list, the method
	 * returns -1.
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
	 * This method doesn't apply, since the activations are unique
	 */
	public int lastIndexOf(Object activation) {
		return -1;
	}

	/**
	 * Method does not apply and isn't implemented
	 */
	public ListIterator listIterator() {
		return null;
	}

	/**
	 * Method does not apply and isn't implemented
	 */
	public ListIterator listIterator(int index) {
		return null;
	}

	/**
	 * Method does not apply and isn't implemented
	 */
	public List subList(int fromIndex, int toIndex) {
		return null;
	}

	/**
	 * Implementation checks to see if the current LinkedActivation.getNext()
	 * returns null. If it isn't null, the method returns true.
	 */
	public boolean hasNext() {
		if (this.itrCurrent.getNext() != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the next LinkedActivation
	 */
	public Object next() {
		this.itrCurrent = this.itrCurrent.getNext();
		counter++;
		return this.itrCurrent;
	}

	/**
	 * Method will remove the current LinkedActivation
	 */
	public void remove() {
		this.itrCurrent.remove();
		this.count--;
	}
}
