package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public abstract class Function implements Formattable {

	protected List<String> aliases = new ArrayList<String>();

	protected List<FunctionGroup> functionGroups = new ArrayList<FunctionGroup>(
			1);

	public Function() {
	}

	public abstract JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException;

	public abstract FunctionDescription getDescription();

	public abstract String getName();

	public void addToFunctionGroup(FunctionGroup group) {
		if (!functionGroups.contains(group))
			functionGroups.add(group);
	}

	public void removeFromFunctionGroup(FunctionGroup group) {
		functionGroups.remove(group);
	}

	public List<FunctionGroup> getFunctionGroups() {
		return functionGroups;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
