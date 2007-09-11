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
	
	public ReteSubnet(ReteSubnet n1, ReteSubnet n2) {
		super();
		this.root = n1.root;
		this.last = n2.last;
	}

	public void setRoot(BaseNode root) {
		this.root = root;
	}

	public void setLast(BaseNode last) {
		this.last = last;
	}
	
}
