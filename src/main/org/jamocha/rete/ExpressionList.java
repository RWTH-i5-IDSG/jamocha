package org.jamocha.rete;

import java.util.ArrayList;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class ExpressionList extends ExpressionCollection {
    
	public Object clone(){
		ArrayList<Parameter> paramList = (ArrayList<Parameter>)parameterList.clone();
		ExpressionCollection result = new ExpressionList();
		result.parameterList = paramList;
		return result;
	}
	
    public JamochaValue getValue(Rete engine) throws EvaluationException {
	JamochaValue[] values = new JamochaValue[parameterList.size()];
	for(int i=0; i<parameterList.size(); ++i) {
	    values[i] = parameterList.get(i).getValue(engine);
	}
	return JamochaValue.newList(values);
    }

}
