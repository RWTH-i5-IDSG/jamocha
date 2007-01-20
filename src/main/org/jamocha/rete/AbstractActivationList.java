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

import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Lin
 *
 * This is a base class the defines the common fields like lazy and
 * strategy for the activation list. Creating new activationList
 * implementations should extend this class.
 */
public abstract class AbstractActivationList implements ActivationList,
		Serializable {

	protected Strategy theStrategy = null;

	protected boolean lazy = false;

	public abstract Activation nextActivation();

	public abstract void addActivation(Activation act);

	public abstract Activation removeActivation(Activation act);

	public abstract List getList();

	public abstract boolean isAscendingOrder();

	public void setStrategy(Strategy strat) {
		this.theStrategy = strat;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isLazy() {
		return this.lazy;
	}

}
