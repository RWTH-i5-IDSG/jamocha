package org.jamocha.rete.rulecompiler.hokifisch;

import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Josef Alexander Hahn
 * a sub-piece of a rete network. it has
 * one root node and one last node.
 */
public class ReteSubnet {
	
	private BaseNode root;
	private BaseNode last;
	
	public BaseNode getRoot() {
		return root;
	}

	public BaseNode getLast() {
		return last;
	}

	public ReteSubnet(BaseNode root, BaseNode last) {
		super();
		this.root = root;
		this.last = last;
	}

	public void setRoot(BaseNode root) {
		this.root = root;
	}

	public void setLast(BaseNode last) {
		this.last = last;
	}
	
}
