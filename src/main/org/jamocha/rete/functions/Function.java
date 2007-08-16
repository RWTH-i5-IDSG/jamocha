package org.jamocha.rete.functions;

import java.util.List;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public interface Function {

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException;

	public FunctionDescription getDescription();

	public String getName();

	public void addToFunctionGroup(FunctionGroup group);

	public void removeFromFunctionGroup(FunctionGroup group);

	public List<FunctionGroup> getFunctionGroups();

	public List<String> getAliases();

	public String format(Formatter visitor);

}