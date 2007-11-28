package org.jamocha.rete.memory.implementations.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.memory.WmeIterator;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.memory.WorkingMemoryListener;
import org.jamocha.rete.nodes.BaseNode;

public class WorkingMemoryImpl implements WorkingMemory {

	private static WorkingMemoryImpl instance;
	
	public static WorkingMemoryImpl getWorkingMemory() {
		if (instance == null) {
			instance = new WorkingMemoryImpl();
		}
		return instance;
	}
	
	

	protected List<WorkingMemoryListener> listeners;
	
	
	private WorkingMemoryImpl() {
		listeners = new ArrayList<WorkingMemoryListener>();
	}
	
	
	

	@Override
	public WmeIterator getWorkingMemoryElementIterator(BaseNode owner) {
		// TODO Auto-generated method stub
		return null;
	}


	



	@Override
	public void addWorkingMemoryListener(WorkingMemoryListener listener) {
		listeners.add(listener);
	}




	@Override
	public void addAlpha(BaseNode owner, WorkingMemoryElement element) {
		

		for (WorkingMemoryListener l : listeners) l.addedeToAlpha(element);
	}




	@Override
	public void addBeta(BaseNode owner, WorkingMemoryElement element) {
		
		for (WorkingMemoryListener l : listeners) l.addedToBeta(element);
	}




	@Override
	public void removeAlpha(BaseNode owner, WorkingMemoryElement element) {
		

		for (WorkingMemoryListener l : listeners) l.removedFromAlpha(element);
	}




	@Override
	public void removeBeta(BaseNode owner, WorkingMemoryElement element) {
	

		for (WorkingMemoryListener l : listeners) l.removedFromBeta(element);
	}

}
