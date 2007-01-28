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

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SlotParam extends AbstractParam {

	protected int valueType = Constants.SLOT_TYPE;

	protected Slot slot = null;

	/**
	 * 
	 * @param type
	 * @param slot
	 */
	public SlotParam(Slot slot) {
		super();
		this.slot = slot;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.ReturnValue#getValueType()
	 */
	public int getValueType() {
		return this.valueType;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.ReturnValue#getValue()
	 */
	public Object getValue() {
		return this.slot;
	}

	public Slot getSlotValue() {
		return this.slot;
	}

    /**
     * Slot parameter is only used internally, so normal user functions
     * should not need to deal with slot parameters.
     */
    public Object getValue(Rete engine, int valueType) {
        return this.slot;
    }

    /* (non-Javadoc)
	 * @see woolfel.engine.rete.Parameter#reset()
	 */
	public void reset() {
		this.slot = null;
	}

}
