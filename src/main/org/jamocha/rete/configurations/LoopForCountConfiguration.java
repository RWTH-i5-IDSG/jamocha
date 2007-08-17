package org.jamocha.rete.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.Rete;

public class LoopForCountConfiguration extends AbstractConfiguration {

	private BoundParam loopVar = null;
	
	private Expression startIndex = JamochaValue.newLong(1);
	
	private Expression endIndex = null;
	
	private ExpressionCollection actions = null;
	
	public boolean isObjectBinding() {
		return false;
	}

	public String getExpressionString() {
		// Returns null because this is deprecated
		return null;
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException {
		return null;
	}

	public ExpressionCollection getActions() {
		return actions;
	}

	public void setActions(ExpressionCollection actions) {
		this.actions = actions;
	}

	public Expression getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Expression endIndex) {
		this.endIndex = endIndex;
	}

	public BoundParam getLoopVar() {
		return loopVar;
	}

	public void setLoopVar(BoundParam loopVar) {
		this.loopVar = loopVar;
	}

	public Expression getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Expression startIndex) {
		this.startIndex = startIndex;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
