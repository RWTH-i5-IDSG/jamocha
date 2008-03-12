package org.jamocha.rete.nodes;

import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.ObjectTypeNodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;
import org.jamocha.rete.wme.Template;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * this node type filters by the object type (the deftemplate)
 */
public class ObjectTypeNode extends OneInputNode {

	protected Template template;
	
	private ObjectTypeNode(int id, WorkingMemory memory, ReteNet net) {
		super(id, memory, net);
	}
	
	public ObjectTypeNode(int id, WorkingMemory memory, ReteNet net, Template templ){
		this(id,memory,net);
		this.template = templ;
	}

	@Override
	public void addWME(WorkingMemoryElement newElem) throws NodeException {
		Template t = newElem.getFirstFact().getTemplate();
		if (t.equals(template)) {
			addAndPropagate(newElem);
		}
	}

	@Override
	public void removeWME(WorkingMemoryElement oldElem) throws NodeException {
		Template t = oldElem.getFirstFact().getTemplate();
		if (t.equals(template)) {
			removeAndPropagate(oldElem);
		}
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

	protected NodeDrawer newNodeDrawer() {
		return new ObjectTypeNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|template:").append(template.getName());
	}
	
}
