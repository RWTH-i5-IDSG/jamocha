/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.engine.workingmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.engine.nodes.Node;

public class WorkingMemoryImpl implements WorkingMemory {

	private static WorkingMemoryImpl instance;

	private final Map<Node, Set<WorkingMemoryElement>> mem;

	private Set<WorkingMemoryElement> getList(
			final Map<Node, Set<WorkingMemoryElement>> map, final Node node) {
		Set<WorkingMemoryElement> l = map.get(node);
		if (l == null) {
			l = new HashSet<WorkingMemoryElement>();
			map.put(node, l);
		}
		return l;
	}

	private boolean addTo(final Map<Node, Set<WorkingMemoryElement>> map,
			final Node node, final WorkingMemoryElement element) {
		final Set<WorkingMemoryElement> l = getList(map, node);
		return l.add(element);
	}

	private boolean removeFrom(final Map<Node, Set<WorkingMemoryElement>> map,
			final Node node, final WorkingMemoryElement element) {
		final Set<WorkingMemoryElement> l = getList(map, node);
		return l.remove(element);
	}

	public static WorkingMemoryImpl getWorkingMemory() {
		if (instance == null)
			instance = new WorkingMemoryImpl();
		return instance;
	}

	protected List<WorkingMemoryListener> listeners;

	private WorkingMemoryImpl() {
		listeners = new ArrayList<WorkingMemoryListener>();
		mem = new HashMap<Node, Set<WorkingMemoryElement>>();
	}

	public void addWorkingMemoryListener(final WorkingMemoryListener listener) {
		listeners.add(listener);
	}

	public boolean add(final Node owner, final WorkingMemoryElement element) {
		final boolean result = addTo(mem, owner, element);
		for (final WorkingMemoryListener l : listeners)
			l.added(element);
		return result;
	}

	public boolean remove(final Node owner, final WorkingMemoryElement element) {
		final boolean result = removeFrom(mem, owner, element);
		for (final WorkingMemoryListener l : listeners)
			l.removed(element);
		return result;
	}

	public void clear() {
		mem.clear();
	}

	public Iterable<WorkingMemoryElement> getMemory(final Node owner) {
		return getList(mem, owner);
	}

	public int size(final Node owner) {
		return getList(mem, owner).size();
	}

}
