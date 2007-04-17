

package org.jamocha.rete;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class AssertConfiguration implements Parameter {

	
	String templateName = null;
	
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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
