package org.jamocha.rete;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.functions.FunctionDescription;

public abstract class AbstractFunction implements Function, Formattable {

	protected FunctionDescription description = null;

	protected String name = "";

	protected List<String> aliases = new ArrayList<String>();

	protected AbstractFunction() {
	}
	
	public static AbstractFunction getInstance() {
		return null;
	}
	
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public FunctionDescription getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
