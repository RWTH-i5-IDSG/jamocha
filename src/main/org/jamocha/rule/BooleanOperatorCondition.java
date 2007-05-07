package org.jamocha.rule;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;

public abstract class BooleanOperatorCondition extends AbstractCondition {

	protected List nestedCE = new ArrayList();

	protected BaseJoin reteNode = null;

	public BooleanOperatorCondition() {
		super();
	}

	public boolean compare(Condition cond) {
		return false;
	}

	public void addNestedConditionElement(Object ce) {
		this.nestedCE.add(ce);
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

	public List getAllBindings() {
		return null;
	}

	public List getBindings() {
		return null;
	}

	public void clear() {
		reteNode = null;
	}

}