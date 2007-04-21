package org.jamocha.rete.configurations;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rule.Rule;

public class ModifyConfiguration extends AbstractConfiguration {

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

	public void setFact(Fact[] facts) {
		factBinding.setFact(facts);
	}

	public void configure(Rete engine, Rule util) {
		// we need to set the row value if the binding is a slot or fact
		Binding b1 = util.getBinding(factBinding.getVariableName());
		if (b1 != null) {
			factBinding.setRow(b1.getLeftRow());
			if (b1.getLeftIndex() == -1) {
				factBinding.setObjectBinding(true);
			}
		}
	}
}
