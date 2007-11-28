package org.jamocha.rete.memory;

import org.jamocha.rete.nodes.BaseNode;

public interface WorkingMemory {

	public void addBeta(BaseNode owner);
	
	public void addAlpha(BaseNode owner);
	
	public WmeIterator getWorkingMemoryElementIterator(BaseNode owner);
	
	public void removeFact(BaseNode owner);
	
	public void addWorkingMemoryListener(WorkingMemoryListener listener);
	
}
