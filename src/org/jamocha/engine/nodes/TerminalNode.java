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
import org.jamocha.application.gui.retevisualisation.nodedrawers.TerminalNodeDrawer;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.agenda.Activation;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.rules.Rule;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> the sink in our rete
 *         network. each fact-tuple, which arrives here, adds a new entry in the
 *         agenda.
 */
public class TerminalNode extends OneInputNode {

	private final Rule rule;
	
	private boolean autoFocus;

	@Deprecated
	public TerminalNode(final int id, final WorkingMemory memory,
			final Rule rule, final ReteNet net) {
		super(id, memory, net);
		this.rule = rule;
	}
	
	public TerminalNode(Engine e, Rule r) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), r, e.getNet());
		autoFocus = false;
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		final Activation act = new Activation(rule, newElem.getFactTuple(), this);
		final Module module = rule.parentModule();
		if (autoFocus) {
			try {
				net.getEngine().getAgendas().getAgenda(module).autoFire(act);
			} catch (ExecuteException e) {
				throw new NodeException("error while auto-fire an activation",e,this);
			}
		} else {
			net.getEngine().getAgendas().getAgenda(module).addActivation(act);
		}
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
		final Activation act = new Activation(rule, oldElem.getFactTuple(),this);
		final Module module = rule.parentModule();
		net.getEngine().getAgendas().getAgenda(module).removeActivation(act);
	}

	public Rule getRule() {
		return rule;
	}

	@Override
	public boolean outputsBeta() {
		// they wont output anything, so it doesnt care what to return here
		return true;
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new TerminalNodeDrawer(this);
	}

	public void autoFocus() {
		autoFocus = true;
	}

}
