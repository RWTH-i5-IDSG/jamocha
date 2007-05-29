package org.jamocha.rule;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.AbstractBeta;

public abstract class BooleanOperatorCondition extends AbstractCondition {

	protected List<Condition> nestedCE = new ArrayList<Condition>();

	protected AbstractBeta reteNode = null;

	public BooleanOperatorCondition() {
		super();
	}

	public boolean compare(Complexity cond) {
		return false;
	}

	public void addNestedConditionElement(Object ce) {
		this.nestedCE.add((Condition) ce);
	}

	public List getNestedConditionalElement() {
		return this.nestedCE;
	}

	public List getNodes() {
		return new ArrayList();
	}

	/**
	 * the method doesn't apply and isn't implemented currently
	 */
	public void addNode(BaseNode node) {
	}

	public boolean hasBindings() {
		return false;
	}

	public BaseNode getLastNode() {
		return reteNode;
	}
	
	public BaseNode getFirstNode() {
		return reteNode;
	}

	public List getAllBoundConstraints() {
		return null;
	}

	public List getBoundConstraints() {
		return null;
	}

	public void clear() {
		reteNode = null;
	}

}