package org.jamocha.rete.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public class AssertConfiguration extends AbstractConfiguration {

	private String templateName = null;

	private Parameter[] data = null;

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

	public SlotConfiguration[] getSlotConfigurations() throws EvaluationException {
		SlotConfiguration[] slots = new SlotConfiguration[data.length];
		Signature sig;
		for (int i = 0; i < data.length; i++) {

			if (!(data[i] instanceof Signature))
				throw new EvaluationException("wrong syntax for assert of unordered fact");
			sig = (Signature) data[i];
			slots[i] = new SlotConfiguration(sig.signatureName, i, sig.getParameters());
		}
		return slots;

	}

	public Parameter[] getData() {
		return data;
	}

	public void setData(Parameter[] data) {
		this.data = data;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
