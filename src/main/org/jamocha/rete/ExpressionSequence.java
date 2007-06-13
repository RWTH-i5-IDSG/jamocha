package org.jamocha.rete;

import java.util.ArrayList;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class ExpressionSequence extends ExpressionCollection {

	public Object clone(){
		ArrayList<Parameter> paramList = (ArrayList<Parameter>)parameterList.clone();
		ExpressionCollection result = new ExpressionSequence();
		result.parameterList = paramList;
		return result;
	}
	
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		for (int i = 0; i < parameterList.size(); ++i) {
			result = parameterList.get(i).getValue(engine);
		}
		return result;
	}

}
