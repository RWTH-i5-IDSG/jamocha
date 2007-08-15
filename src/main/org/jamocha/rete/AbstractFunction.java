package org.jamocha.rete;

import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.functions.FunctionDescription;

public abstract class AbstractFunction implements Function, Formattable {

	protected static FunctionDescription DESCRIPTION = null;
	
	protected static String NAME = "";

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
