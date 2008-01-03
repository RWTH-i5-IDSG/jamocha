package org.jamocha.rete.memory;

import org.jamocha.rete.nodes.Node;

public interface WorkingMemory {
	
	public boolean add(Node owner, WorkingMemoryElement element);
	
	public boolean remove(Node owner, WorkingMemoryElement element);
	
	public void addWorkingMemoryListener(WorkingMemoryListener listener);
	
	public void clear();
	
	public Iterable<WorkingMemoryElement> getMemory(Node owner);
	
	public int size(Node owner);
	
}
