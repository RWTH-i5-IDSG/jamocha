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
package org.jamocha.rete.nodes;

import org.jamocha.rete.Slot;


/**
 * @author Peter Lin
 * 
 * BaseAlpha2 is an abstract class for AlphaNodes that compare literal or bound
 * constraints. It isn't used for LIANode, ObjectTypeNode.
 */
public abstract class SlotAlpha extends AbstractAlpha {

	/**
	 * @param id
	 */
	public SlotAlpha(int id) {
		super(id);
	}

	/**
	 * set the operator type for the node
	 * @param opr
	 */
	public abstract void setOperator(int opr);

	/**
	 * set the slot for the node
	 * @param sl
	 */
	public abstract void setSlot(Slot sl);
}
