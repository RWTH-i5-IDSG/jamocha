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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Lin
 *
 * This is a basic implementation of activation list that uses LinkedList.
 * When new activations are added in lazy mode, it adds the activation to
 * the end of the list. This means strategies need to 
 */
public class ArrayActivationList extends AbstractActivationList {

	protected ArrayList list = new ArrayList();

	public ArrayActivationList(Strategy strat) {
		this.theStrategy = strat;
	}

	/**
	 * In lazy mode, the strategy will be used to get the next
	 * activation. In non-lazy mode, the list simply returns
	 * the first item in the LinkedList
	 */
	public Activation nextActivation() {
		if (lazy) {
			return this.theStrategy.nextActivation(this);
		} else if (list.size() > 0) {
			return (Activation) this.list.remove(0);
		} else {
			return null;
		}
	}

	/**
	 * In lazy mode, add will simply add the activation to the end
	 * of the LinkedList. In non-lazy mode, it will use the strategy
	 * to add the activation to the list.
	 */
	public void addActivation(Activation act) {
		if (lazy) {
			this.list.add(act);
		} else {
			this.theStrategy.addActivation(this, act);
		}
	}

	/**
	 * The implementation iterates over the List and removes the first
	 * matching Activation. The list should not have more than
	 * one Activation with the same rule + Fact[]. If it does, there is
	 * a bug.
	 */
	public Activation removeActivation(Activation act) {
		Activation ret = null;
		for (int idx = 0; idx < this.list.size(); idx++) {
			Activation right = (Activation) this.list.get(idx);
			if (act.compare(right)) {
				ret = (Activation) this.list.remove(idx);
				break;
			}
		}
		if (ret == null) {
			ret = act;
		}
		return ret;
	}

	/**
	 * LinkedActivationList will return a standard java.util.ArrayList
	 */
	public List getList() {
		return this.list;
	}

	/**
	 * LinkedActivationList will add new activations to the end of
	 * the LinkedList in lazy mode.
	 */
	public boolean isAscendingOrder() {
		return false;
	}
}
