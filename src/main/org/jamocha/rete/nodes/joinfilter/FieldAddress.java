package org.jamocha.rete.nodes.joinfilter;

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
	
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException(); //abstract class
	}
	
	public abstract boolean refersWholeFact();
	public abstract int getSlotIndex() throws FieldAddressingException;
	public abstract String toPPString();
	
	public boolean isObjectBinding() {
		return refersWholeFact();
	}

	public String toClipsFormat(int indent) {
		String ind = "";
		while (ind.length() < indent*blanksPerIndent) ind+=" ";
		return ind+getExpressionString();
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException {
		return null;
	}
}
