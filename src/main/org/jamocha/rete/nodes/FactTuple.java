package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.memory.WorkingMemoryElement;

public interface FactTuple extends WorkingMemoryElement{

	int length();
	
	Fact[] getFacts();
	
	FactTuple appendFact(Fact fact);
	
	boolean isMySubTuple(FactTuple other);
	
	boolean isMyLastFact(Fact f);
	
	
	
}
