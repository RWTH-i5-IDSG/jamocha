/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.nodes;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.ObjectTypeNodeDrawer;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Template;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> this node type filters by
 *         the object type (the deftemplate)
 */
public class ObjectTypeNode extends OneInputNode {

	protected Template template;

	private ObjectTypeNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	public ObjectTypeNode(final int id, final WorkingMemory memory,
			final ReteNet net, final Template templ) {
		this(id, memory, net);
		template = templ;
	}

	@Override
	public void addWME(final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		final Template t = newElem.getFirstFact().getTemplate();
		if (t.equals(template))
			addAndPropagate(newElem);
	}

	@Override
	public void removeWME(final WorkingMemoryElement oldElem)
			throws NodeException {
		final Template t = oldElem.getFirstFact().getTemplate();
		if (t.equals(template))
			removeAndPropagate(oldElem);
	}

	/**
	 * returns the template, which this node will let pass
	 */
	public Template getTemplate() {
		return template;
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new ObjectTypeNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|template:").append(template.getName());
	}

}
