package org.jamocha.rete.memory;

import org.jamocha.rete.nodes.BaseNode;

public interface WorkingMemory {

	public void addFact(BaseNode owner);
	
	public WmeIterator getWorkingMemoryElementIterator(BaseNode owner);
	
	public void removeFact(BaseNode owner);
	
}
