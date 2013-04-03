package org.jamocha.engine.memory;

import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.JamochaValue;

public class SlotAddress {
	int index;

	public SlotAddress(int index) {
		this.index = index;
	}

	public JamochaValue getValue(Fact fact) {
		return fact.getValue(index);
	}
}
