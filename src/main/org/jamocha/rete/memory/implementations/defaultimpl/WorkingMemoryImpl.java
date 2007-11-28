package org.jamocha.rete.memory.implementations.defaultimpl;

import org.jamocha.rete.memory.WmeIterator;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryListener;
import org.jamocha.rete.nodes.BaseNode;

public class WorkingMemoryImpl implements WorkingMemory {

	private WorkingMemoryImpl() {
		
	}
	
	private static WorkingMemoryImpl instance;
	
	public static WorkingMemoryImpl getWorkingMemory() {
		if (instance == null) {
			instance = new WorkingMemoryImpl();
		}
		return instance;
	}

	@Override
	public WmeIterator getWorkingMemoryElementIterator(BaseNode owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFact(BaseNode owner) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void addAlpha(BaseNode owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBeta(BaseNode owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWorkingMemoryListener(WorkingMemoryListener listener) {
		// TODO Auto-generated method stub
		
	}

}
