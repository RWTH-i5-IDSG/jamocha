package org.jamocha.rete.memory.implementations.defaultimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.memory.WorkingMemoryListener;
import org.jamocha.rete.nodes.BaseNode;

public class WorkingMemoryImpl implements WorkingMemory {

	private static WorkingMemoryImpl instance;

	private Map<BaseNode, List<WorkingMemoryElement>> alphaMem;

	private Map<BaseNode, List<WorkingMemoryElement>> betaMem;

	private List<WorkingMemoryElement> getList(
			Map<BaseNode, List<WorkingMemoryElement>> map, BaseNode node) {
		List<WorkingMemoryElement> l = map.get(node);
		if (l == null) {
			l = new ArrayList<WorkingMemoryElement>();
			map.put(node, l);
		}
		return l;
	}

	private void addTo(Map<BaseNode, List<WorkingMemoryElement>> map,
			BaseNode node, WorkingMemoryElement element) {
		List<WorkingMemoryElement> l = getList(map, node);
		l.add(element);
	}

	private void removeFrom(Map<BaseNode, List<WorkingMemoryElement>> map,
			BaseNode node, WorkingMemoryElement element) {
		List<WorkingMemoryElement> l = getList(map, node);
		l.remove(element);
	}

	public static WorkingMemoryImpl getWorkingMemory() {
		if (instance == null) {
			instance = new WorkingMemoryImpl();
		}
		return instance;
	}

	protected List<WorkingMemoryListener> listeners;

	private WorkingMemoryImpl() {
		listeners = new ArrayList<WorkingMemoryListener>();
		alphaMem = new HashMap<BaseNode, List<WorkingMemoryElement>>();
		betaMem = new HashMap<BaseNode, List<WorkingMemoryElement>>();
	}

	public Iterator<WorkingMemoryElement> getAlphaWorkingMemoryElementIterator(
			BaseNode owner) {
		return getList(alphaMem, owner).iterator();
	}

	public Iterator<WorkingMemoryElement> getBetaWorkingMemoryElementIterator(
			BaseNode owner) {
		return getList(betaMem, owner).iterator();
	}

	public void addWorkingMemoryListener(WorkingMemoryListener listener) {
		listeners.add(listener);
	}

	public void addAlpha(BaseNode owner, WorkingMemoryElement element) {
		addTo(alphaMem, owner, element);
		for (WorkingMemoryListener l : listeners)
			l.addedeToAlpha(element);
	}

	public void addBeta(BaseNode owner, WorkingMemoryElement element) {
		addTo(betaMem, owner, element);
		for (WorkingMemoryListener l : listeners)
			l.addedToBeta(element);
	}

	public void removeAlpha(BaseNode owner, WorkingMemoryElement element) {
		removeFrom(alphaMem, owner, element);
		for (WorkingMemoryListener l : listeners)
			l.removedFromAlpha(element);
	}

	public void removeBeta(BaseNode owner, WorkingMemoryElement element) {
		removeFrom(betaMem, owner, element);
		for (WorkingMemoryListener l : listeners)
			l.removedFromBeta(element);
	}

}
