package org.jamocha.rete.memory;

import java.util.Iterator;

import org.jamocha.rete.nodes.BaseNode;

public interface WorkingMemory {
	
	public void addBeta(BaseNode owner, WorkingMemoryElement element);
	
	public void addAlpha(BaseNode owner, WorkingMemoryElement element);
	
	public Iterable<WorkingMemoryElement> getAlpha(BaseNode owner);
	
	public Iterable<WorkingMemoryElement> getBeta(BaseNode owner);
	
	public boolean removeAlpha(BaseNode owner, WorkingMemoryElement element);
	
	public boolean removeBeta(BaseNode owner, WorkingMemoryElement element);
	
	public void addWorkingMemoryListener(WorkingMemoryListener listener);
	
	public void clear();
	
}
