package org.jamocha.rete.memory;

public interface WorkingMemoryListener {

	void addedToBeta(WorkingMemoryElement element);
	
	void addedeToAlpha(WorkingMemoryElement element);
	
	void removedFromBeta(WorkingMemoryElement element);
	
	void removedFromAlpha(WorkingMemoryElement element);
	
}
