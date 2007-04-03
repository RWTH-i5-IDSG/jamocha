package org.jamocha.rete;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class ExpressionSequence extends ExpressionCollection {
    
    public JamochaValue getValue(Rete engine) throws EvaluationException {
	JamochaValue result = JamochaValue.NIL;
	for(int i=0; i<parameterList.size(); ++i) {
	    result = parameterList.get(i).getValue(engine);
	}
	return result;
    }

}
