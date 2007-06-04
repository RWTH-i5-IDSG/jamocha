package org.jamocha.rete.joinfilter;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Josef Alexander Hahn
 * This is the first step for replacing our 101 binding-like classes
 * by a more systematic structure of classes.
 * a FieldAddress is immutable, because that world sound so good ;)
 */

public abstract class FieldAddress implements Parameter {
	
	public abstract boolean refersWholeFact();
	public abstract int getSlotIndex() throws FieldAddressingException;
	public abstract String toPPString();
	
	public boolean isObjectBinding() {
		return refersWholeFact();
	}

	public String getExpressionString() {
		// that returns not a real clips-expression!
		// but since this class is only used inside rulecompiler/engine internally,
		// that would be not that problem, i think
		return toPPString();
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException {
		return null;
	}
}
