package org.jamocha.rete.nodes.joinfilter;

import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.nodes.FactTuple;

public class FunctionEvaluator implements JoinFilter {

	protected Parameter[] parameters;
	protected Function function;
	protected Rete engine;
	
	//TODO: maybe it is better to store a Signature instead of Parameter[]&Function
	
	private FunctionEvaluator(Rete engine, Function function) {
		this.function = function;
		this.engine = engine;
	}
	
	

	
	public FunctionEvaluator(Rete engine, Function function, List<Parameter> parameters) throws JoinFilterException {
		this(engine,function);
		Parameter[] params = new Parameter[0];
		this.parameters = parameters.toArray(params);
	}
	
	public FunctionEvaluator(Rete engine, Function function, Parameter[] parameters) throws JoinFilterException {
		this(engine,function);
		this.parameters = parameters;
	}

	
	private void substitute(Parameter[] params, Fact right, FactTuple left) throws FieldAddressingException, EvaluationException{
		for (int i = 0 ; i < params.length ; i++) {
			Parameter p = params[i];
			if (p instanceof RightFieldAddress) {
				RightFieldAddress addr = (RightFieldAddress) p;
				JamochaValue val;
				if (addr.refersWholeFact()) {
					val = JamochaValue.newFact(right);
				} else {
					val = right.getSlotValue(addr.getSlotIndex());	
				}
				params[i] = val;
			} else if (p instanceof LeftFieldAddress){
				LeftFieldAddress addr = (LeftFieldAddress) p;
				JamochaValue val;
				if (addr.refersWholeFact()) {
					val = JamochaValue.newFact(left.getFacts()[addr.getRowIndex()]);
				} else {
					Fact fact = left.getFact(addr.getRowIndex());
					val = fact.getSlotValue(addr.getSlotIndex());	
				}
				params[i] = val;
			} else if (p instanceof Signature){
				Signature sig = ((Signature)p);
				substitute(sig.getParameters(),right,left);
			}
		}
	}
	
	private Parameter[] semicloneParameters(Parameter[] orig){
		Parameter[] clone = orig.clone();
		
		for (int i=0 ; i<clone.length ; i++){
			if (clone[i] instanceof Signature){
				Signature s = (Signature)clone[i];
				Signature sigClone = (Signature) s.clone();
				sigClone.setParameters( semicloneParameters(sigClone.getParameters()));
				clone[i] = sigClone;
			}
		}
		
		return clone;
	}

	public boolean evaluate(Fact right, FactTuple left, Rete engine) throws JoinFilterException, EvaluationException {
		Parameter[] callParams = semicloneParameters(parameters);
		substitute(callParams, right, left);

		try {
			return function.executeFunction(engine, callParams).getBooleanValue();
		} catch (EvaluationException e) {
			return false;
		}
	}

	public String toPPString() {
		//TODO thats not really good since FieldAdresses only were printed if in first level since clipsformatter doesnt format it
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
