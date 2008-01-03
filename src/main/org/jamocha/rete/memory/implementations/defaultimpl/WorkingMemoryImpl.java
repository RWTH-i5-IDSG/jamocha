package org.jamocha.rete.memory.implementations.defaultimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.memory.WorkingMemoryListener;
import org.jamocha.rete.nodes.Node;

public class WorkingMemoryImpl implements WorkingMemory {

	private static WorkingMemoryImpl instance;

	private Map<Node, Set<WorkingMemoryElement>> mem;

	private Set<WorkingMemoryElement> getList(Map<Node, Set<WorkingMemoryElement>> map, Node node) {
		Set<WorkingMemoryElement> l = map.get(node);
		if (l == null) {
			l = new HashSet<WorkingMemoryElement>();
			map.put(node, l);
		}
		return l;
	}

	private boolean addTo(Map<Node, Set<WorkingMemoryElement>> map, Node node, WorkingMemoryElement element) {
		Set<WorkingMemoryElement> l = getList(map, node);
		return l.add(element);
	}

	private boolean removeFrom(Map<Node, Set<WorkingMemoryElement>> map, Node node, WorkingMemoryElement element) {
		Set<WorkingMemoryElement> l = getList(map, node);
		return l.remove(element);
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
		mem = new HashMap<Node, Set<WorkingMemoryElement>>();
	}

	public void addWorkingMemoryListener(WorkingMemoryListener listener) {
		listeners.add(listener);
	}

	public boolean add(Node owner, WorkingMemoryElement element) {
		boolean result = addTo(mem, owner, element);
		for (WorkingMemoryListener l : listeners)
			l.added(element);
		return result;
	}


	public boolean remove(Node owner, WorkingMemoryElement element) {
		boolean result = removeFrom(mem, owner, element);
		for (WorkingMemoryListener l : listeners)
			l.removed(element);
		return result;
	}


	public void clear() {
		mem.clear();
	}

	public Iterable<WorkingMemoryElement> getMemory(Node owner) {
		return getList(mem, owner);
	}

	@Override
	public int size(Node owner) {
		return getList(mem, owner).size();
	}

}
