package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public abstract class AbstractFunction implements Function {

	protected List<String> aliases = new ArrayList<String>();

	protected List<FunctionGroup> functionGroups = new ArrayList<FunctionGroup>(
			1);

	public AbstractFunction() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#executeFunction(org.jamocha.rete.Rete,
	 *      org.jamocha.rete.Parameter[])
	 */
	public abstract JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#getDescription()
	 */
	public abstract FunctionDescription getDescription();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#getName()
	 */
	public abstract String getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#addToFunctionGroup(org.jamocha.rete.functions.FunctionGroup)
	 */
	public void addToFunctionGroup(FunctionGroup group) {
		if (!functionGroups.contains(group))
			functionGroups.add(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#removeFromFunctionGroup(org.jamocha.rete.functions.FunctionGroup)
	 */
	public void removeFromFunctionGroup(FunctionGroup group) {
		functionGroups.remove(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#getFunctionGroups()
	 */
	public List<FunctionGroup> getFunctionGroups() {
		return functionGroups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#getAliases()
	 */
	public List<String> getAliases() {
		return aliases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.functions.Function#format(org.jamocha.formatter.Formatter)
	 */
	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
