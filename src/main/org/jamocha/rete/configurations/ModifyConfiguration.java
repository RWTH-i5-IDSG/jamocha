

package org.jamocha.rete.configurations;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public class ModifyConfiguration implements Parameter {

	
	BoundParam factBinding = null;
	
	SlotConfiguration[] slots = null;
	
	
	public boolean isObjectBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public SlotConfiguration[] getSlots() {
		return slots;
	}

	public void setSlots(SlotConfiguration[] slots) {
		this.slots = slots;
	}

	public BoundParam getFactBinding() {
		return factBinding;
	}

	public void setFactBinding(BoundParam factBinding) {
		this.factBinding = factBinding;
	}


}
