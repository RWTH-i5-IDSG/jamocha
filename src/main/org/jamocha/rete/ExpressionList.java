package org.jamocha.rete;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class ExpressionList extends ExpressionCollection {
    
    public JamochaValue getValue(Rete engine) throws EvaluationException {
	JamochaValue[] values = new JamochaValue[parameterList.size()];
	for(int i=0; i<parameterList.size(); ++i) {
	    values[i] = parameterList.get(i).getValue(engine);
	}
	return JamochaValue.newList(values);
    }

}
