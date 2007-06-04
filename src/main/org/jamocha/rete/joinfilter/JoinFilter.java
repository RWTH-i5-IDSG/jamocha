package org.jamocha.rete.joinfilter;

import org.jamocha.rete.Fact;
import org.jamocha.rete.nodes.FactTuple;

public interface JoinFilter {

	boolean evaluate(Fact right, FactTuple left) throws JoinFilterException;
	public String toPPString();
	
}
