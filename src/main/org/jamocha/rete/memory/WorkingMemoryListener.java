package org.jamocha.rete.memory;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * listener interface for working memory changes
 */
public interface WorkingMemoryListener {

	void added(WorkingMemoryElement element);
	
	void removed(WorkingMemoryElement element);
	
}
