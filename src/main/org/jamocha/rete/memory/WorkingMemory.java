package org.jamocha.rete.memory;

import java.util.Iterator;

import org.jamocha.rete.nodes.BaseNode;

public interface WorkingMemory {

	public void addBeta(BaseNode owner, WorkingMemoryElement element);
	
	public void addAlpha(BaseNode owner, WorkingMemoryElement element);
	
	public Iterator<WorkingMemoryElement> getAlphaWorkingMemoryElementIterator(BaseNode owner);
	
	public Iterator<WorkingMemoryElement> getBetaWorkingMemoryElementIterator(BaseNode owner);
	
	public void removeAlpha(BaseNode owner, WorkingMemoryElement element);
	
	public void removeBeta(BaseNode owner, WorkingMemoryElement element);
	
	public void addWorkingMemoryListener(WorkingMemoryListener listener);
	
}
