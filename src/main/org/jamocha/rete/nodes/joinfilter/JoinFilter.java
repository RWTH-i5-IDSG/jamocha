package org.jamocha.rete.nodes.joinfilter;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.nodes.FactTuple;

public interface JoinFilter {

	boolean evaluate(Fact right, FactTuple left, Rete engine) throws JoinFilterException, EvaluationException;
	public String toPPString();
	
}
