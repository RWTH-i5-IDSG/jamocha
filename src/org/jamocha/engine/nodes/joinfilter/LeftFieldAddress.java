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

import org.jamocha.engine.util.MutableInteger;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class LeftFieldAddress extends FieldAddress {
	protected int slotIndex;
	protected MutableInteger rowIndex;

	@Override
	public Object clone() {
		return this;
	}

	public LeftFieldAddress(final int rowIndex) {
		this(rowIndex, -1);
	}

	public LeftFieldAddress(final int rowIndex, final int slotIndex) {
		this.slotIndex = slotIndex;
		this.rowIndex = new MutableInteger(rowIndex);
	}
	
	public LeftFieldAddress(MutableInteger row, int slot) {
		rowIndex=row;
		slotIndex=slot;
	}

	public LeftFieldAddress(MutableInteger row) {
		this(row,-1);
	}
	
	@Override
	public String toPPString() {
		return getExpressionString();
	}

	public String getExpressionString() {
		final StringBuffer result = new StringBuffer();
		result.append("left(row=");
		result.append(rowIndex.get());
		if (slotIndex == -1)
			result.append(";whole fact)");
		else
			result.append(";slot=").append(slotIndex).append(")");
		return result.toString();
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	@Override
	public JamochaValue getIndexedValue(WorkingMemoryElement wme) throws EvaluationException {
		if (slotIndex == -1) {
			return JamochaValue.newFact(wme.getFactTuple().getFact(rowIndex.get()));
		} else {
			return wme.getFactTuple().getFact(rowIndex.get()).getSlotValue(slotIndex);
		}
	}

	public MutableInteger getTupleIndex() {
		return rowIndex;
	}

	public int getSlotIndex() {
		return slotIndex;
	}

}
