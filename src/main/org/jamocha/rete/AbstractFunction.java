package org.jamocha.rete;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.functions.FunctionDescription;

public abstract class AbstractFunction implements Function, Formattable {
	
	protected List<String> aliases = new ArrayList<String>();

	public AbstractFunction() {
	}
	
	public abstract JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException;

	public abstract FunctionDescription getDescription();

	public abstract String getName();

	public List<String> getAliases() {
		return aliases;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
