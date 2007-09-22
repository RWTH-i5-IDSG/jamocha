package org.jamocha.rete;

import java.util.ArrayList;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class ExpressionSequence extends ExpressionCollection {

	public Object clone(){
		@SuppressWarnings("unchecked")
		ArrayList<Parameter> paramList = (ArrayList<Parameter>)parameterList.clone();
		ExpressionCollection result = new ExpressionSequence();
		result.parameterList = paramList;
		return result;
	}
	
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		Parameter param ;
		for (int i = 0; i < parameterList.size(); ++i) {
			param = parameterList.get(i);
			try{
			result = param.getValue(engine);
			} catch (Exception e) {
				throw new EvaluationException("Error in: "
						+ param.toString(), e);
			}
		}
		return result;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
