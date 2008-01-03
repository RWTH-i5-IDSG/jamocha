package org.jamocha.rule;

import jade.core.BaseNode;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.nodes.Node;

public abstract class ConditionWithNested extends AbstractCondition {

	protected List<Condition> nestedCE = new ArrayList<Condition>();
	protected String clipsName() {return "";}
	protected List<Node> nodes = new ArrayList<Node>();
	
	protected Node reteNode = null;

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

	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * the method doesn't apply and isn't implemented currently
	 */
	public void addNode(Node node) {
		nodes.add(node);
	}

	public Node getLastNode() {
		if (nodes.size() > 0) {
			return nodes.get(nodes.size()-1);
		} else {
			return nestedCE.get(0).getLastNode();
		}
	}

	public Node getFirstNode() {
		if (nodes.size() > 0) {
			return (Node) nodes.get(0);
		} else {
			return null;
		}
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