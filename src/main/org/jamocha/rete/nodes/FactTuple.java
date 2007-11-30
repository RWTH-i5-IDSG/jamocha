package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.memory.WorkingMemoryElement;

public interface FactTuple extends WorkingMemoryElement, Iterable<Fact>{

	int length();
	
	Fact[] getFacts();
	
	Fact getFact(int index);
	
	FactTuple appendFact(Fact fact);
	
	boolean isMySubTuple(FactTuple other);
	
	boolean isMyLastFact(Fact f);
	
	
	
}
