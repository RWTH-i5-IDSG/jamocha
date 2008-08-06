/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.nodes.joinfilter;

import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class RightFieldAddress extends FieldAddress {

	protected int slotIndex;
	protected int posIndex;

	@Override
	public Object clone() {
		return this;
	}

	public RightFieldAddress() {
		this(-1, -1);
	}

	public RightFieldAddress(final int slotIndex) {
		this(slotIndex, -1);
	}

	public RightFieldAddress(final int slotIndex, final int posIndex) {
		this.slotIndex = slotIndex;
		this.posIndex = posIndex;
	}

	@Override
	public String toPPString() {
		return getExpressionString();
	}

	public String getExpressionString() {
		if (slotIndex == -1)
			return "right(whole fact)";
		else {
			final StringBuffer result = new StringBuffer();
			result.append("right(slot=").append(slotIndex).append(")");
			return result.toString();
		}
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	@Override
	public JamochaValue getIndexedValue(WorkingMemoryElement wme) throws EvaluationException {
		if (slotIndex == -1) {
			return  JamochaValue.newFact(wme.getFirstFact()) ;
		} else {
			return wme.getFirstFact().getSlotValue(slotIndex);
		}
	}

}