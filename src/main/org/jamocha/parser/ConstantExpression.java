package org.jamocha.parser;

import org.jamocha.rete.Rete;

public class ConstantExpression implements Expression {
    
    private JamochaValue value;
    

    public ConstantExpression(JamochaValue value) {
	super();
	this.value = value;
    }

    public String getExpressionString() {
	return value.toString();
    }

    public JamochaValue getValue(Rete engine) throws EvaluationException {
	return value;
    }

}
