package org.jamocha.rete.memory;

import org.jamocha.rete.nodes.BaseNode;

public interface WorkingMemory {

	public void addBeta(BaseNode owner, WorkingMemoryElement element);
	
	public void addAlpha(BaseNode owner, WorkingMemoryElement element);
	
	public WmeIterator getWorkingMemoryElementIterator(BaseNode owner);
	
	public void removeAlpha(BaseNode owner, WorkingMemoryElement element);
	
	public void removeBeta(BaseNode owner, WorkingMemoryElement element);
	
	public void addWorkingMemoryListener(WorkingMemoryListener listener);
	
}
