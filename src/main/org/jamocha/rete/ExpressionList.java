package org.jamocha.rete;

import java.util.ArrayList;

import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

public class ExpressionList extends ExpressionCollection implements Formattable {
    
	public Object clone(){
		@SuppressWarnings("unchecked")
		ArrayList<Parameter> paramList = (ArrayList<Parameter>) parameterList.clone();
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

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getExpressionString() {
		return ParserFactory.getFormatter().visit(this);
	}

}
