package org.jamocha.rule;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.AbstractBeta;

public abstract class ConditionWithNested extends AbstractCondition {

	protected List<Condition> nestedCE = new ArrayList<Condition>();
	protected String clipsName() {return "";}
	protected List<BaseNode> nodes = new ArrayList<BaseNode>();
	
	protected AbstractBeta reteNode = null;

	public ConditionWithNested() {
		super();
	}

	public boolean compare(Complexity cond) {
		return false;
	}

	public void addNestedConditionElement(Object ce) {
		this.nestedCE.add((Condition) ce);
	}

	public List<Condition> getNestedConditionalElement() {
		return this.nestedCE;
	}

	public List getNodes() {
		return nodes;
	}

	/**
	 * the method doesn't apply and isn't implemented currently
	 */
	public void addNode(BaseNode node) {
		nodes.add(node);
	}

	public boolean hasBindings() {
		return false;
	}

	public BaseNode getLastNode() {
		if (nodes.size() > 0) {
			return nodes.get(nodes.size()-1);
		} else {
			return nestedCE.get(0).getLastNode();
		}
	}

	public BaseNode getFirstNode() {
		if (nodes.size() > 0) {
			return (BaseNode) nodes.get(0);
		} else {
			return null;
		}
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
	
	public Object clone() throws CloneNotSupportedException {
		ConditionWithNested result;
		try {
			result = this.getClass().newInstance();
		} catch (Exception e) {
			throw new CloneNotSupportedException("problem while instantiating new condition instance");
		}
		
	
		result.negated = this.negated;
		result.reteNode = this.reteNode;
		result.totalComplexity = this.totalComplexity;
		result.nestedCE = new ArrayList<Condition>();
		for (Condition c : nestedCE) result.nestedCE.add((Condition)c.clone());
		return result;
	}
	
}