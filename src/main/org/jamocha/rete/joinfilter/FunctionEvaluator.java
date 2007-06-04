package org.jamocha.rete.joinfilter;

import java.util.List;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.nodes.FactTuple;
import org.jamocha.rule.BoundConstraint;

public class FunctionEvaluator implements JoinFilter {

	protected Parameter[] parameters;
	protected Function function;

	private FunctionEvaluator(Function function) {
		this.function = function;
	}
	
	private void sanityCheckParameterTypes() throws JoinFilterException {
		for ( int paramIdx = 0 ; paramIdx < parameters.length ; paramIdx++){
			Parameter p = parameters[paramIdx];
			if (p instanceof JamochaValue) {
				// everything is good, we dont need to convert that
			} else if (p instanceof FieldAddress) {
				// everything is good, we dont need to convert that
			}
			else if (p instanceof BoundConstraint) {
				// FATAL: we cant handle that here. rule-compiler has to convert that to a fieldfieldaddress
				throw new JoinFilterException("our rule compiler made something wrong. it gave me a BoundParam. it must convert BoundParams to LeftFieldAddress for me.");

			} else {
				// FATAL: we have a type of Parameter we didnt know how to convert.
				throw new JoinFilterException("i cannot convert the following parameter in your test call (maybe since not yet implemented): "+p.getClass().getName());
			}
		}
	}
	
	public FunctionEvaluator(Function function, List<Parameter> parameters) throws JoinFilterException {
		this(function);
		Parameter[] params = new Parameter[0];
		this.parameters = parameters.toArray(params);
		sanityCheckParameterTypes();
	}
	
	public FunctionEvaluator(Function function, Parameter[] parameters) throws JoinFilterException {
		this(function);
		this.parameters = parameters;
		sanityCheckParameterTypes();
	}


	public boolean evaluate(Fact right, FactTuple left) throws JoinFilterException {
		Parameter[] callParams = new Parameter[parameters.length];
		System.arraycopy(parameters, 0, callParams, 0, parameters.length);
		
		for (int i = 0 ; i < callParams.length ; i++) {
			//Parameter p = callParams[]
		}
		
		return false;
	}

	public String toPPString() {
		StringBuffer result = new StringBuffer();
		result.append("test: ");
		result.append(function.getName());
		result.append("(");
		for (int i = 0 ; i<parameters.length ; i++) {
			Parameter param = parameters[i];
			if (i>0) result.append(", ");
			result.append(param.getExpressionString());
		}
		result.append(")");
		return result.toString();
	}

}
