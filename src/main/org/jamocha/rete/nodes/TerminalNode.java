package org.jamocha.rete.nodes;

import org.jamocha.rete.agenda.Activation;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.modules.Module;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.TerminalNodeDrawer;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * the sink in our rete network. each fact-tuple, which arrives here,
 * adds a new entry in the agenda.
 */
public class TerminalNode extends OneInputNode {

	private Rule rule;
	
	public TerminalNode(int id, WorkingMemory memory, Rule rule, ReteNet net) {
		super(id, memory, net);
		this.rule = rule;
	}

	@Override
	public void addWME(WorkingMemoryElement newElem) throws NodeException {

		Activation act = new Activation(rule, newElem.getFactTuple());
		Module module = rule.getModule();
		net.getEngine().getAgendas().getAgenda(module).addActivation(act);
	}

	@Override
	public void removeWME(WorkingMemoryElement oldElem) throws NodeException {
		Activation act = new Activation(rule, oldElem.getFactTuple());
		Module module = rule.getModule();
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
	
	protected NodeDrawer newNodeDrawer() {
		return new TerminalNodeDrawer(this);
	}
	
}
