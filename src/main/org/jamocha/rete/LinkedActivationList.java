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
public class LinkedActivationList extends AbstractActivationList {

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
            if (this.count == 0) {
                return null;
            } else {
                LinkedActivation left = this.last;
                LinkedActivation right = this.last.getPrevious();
                while (right != null) {
                    if (this.theStrategy.compare(left,right) < 1) {
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
                    LinkedActivation cur = this.last;
                    LinkedActivation prev = this.last;
                    boolean added = false;
                    while (cur != null) {
                        if (this.theStrategy.compare(newact, cur) >= 0) {
                            cur.setNext(newact);
                            if (this.last == cur) {
                                this.last = newact;
                            } else {
                                newact.setNext(prev);
                            }
                            added = true;
                            break;
                        }
                        prev = cur;
                        cur = cur.getPrevious();
                    }
                    if (!added) {
                        newact.setNext(this.first);
                        this.first = newact;
                    }
                } else if (count == 0){
                    this.first = newact;
                    this.last = newact;
                }
                this.count++;
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
            } 
            if (this.last == lact) {
                this.last = lact.getPrevious();
            }
            this.count--;
            lact.remove();
        }
        return act;
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
            pre.setNext( (LinkedActivation)activation );
            nxt.setPrevious( (LinkedActivation)activation );
            act.remove();
        }
        return activation;
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

}
