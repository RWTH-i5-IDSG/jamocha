package org.jamocha.rete.rulecompiler.hokifisch;

import jade.core.BaseNode;

import org.jamocha.rete.nodes.Node;



/**
 * @author Josef Alexander Hahn
 * a sub-piece of a rete network. it has
 * one root node and one last node.
 */
public class ReteSubnet {
	
	private Node root;
	private Node last;
	
	public Node getRoot() {
		return root;
	}

	public Node getLast() {
		return last;
	}

	public ReteSubnet(Node root, Node last) {
		super();
		this.root = root;
		this.last = last;
	}
	
	public ReteSubnet(ReteSubnet n1, ReteSubnet n2) {
		super();
		this.root = n1.root;
		this.last = n2.last;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public void setLast(Node last) {
		this.last = last;
	}
	
}
